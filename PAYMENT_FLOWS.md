# Payment Flows: Card & Instant Transfers

## 📋 Overview

This document describes the orchestration flows for **Card Payments** and **Instant Transfers (SCT Inst)**, using the `PaymentProcessor` strategy pattern for clean separation of concerns.

---

## 1. Wire Transfer Flow (Base - Already Implemented)

**Input DTO**: `TransferRequestDTO`

```
- sourceIban: String
- beneficiaryIban: String
- beneficiaryName: String
- amount: double
- message: String
- isInstant: boolean (false for wire, true for SCT Inst)
```

**Flow**:

1. Create preorder (status: PENDING)
2. Execute preorder → validate IBAN + amount → call `CoreBankingClient.postAccountUpdate()`
3. Create ledger entry (CREDIT) with `externalOperationId`
4. Update preorder → EXECUTED

**Enum**: `OperationType` (not used for wire, but needed for card/instant)

---

## 2. Card Payment Flow

### Input Requirements

**DTO**: `CardPaymentRequestDTO`

```
- pan: String (validated length 13-19)
- cvv: String (validated length 3-4)
- expiryDate: String (format: MM/YY)
- amount: double (@Positive)
- cardType: CardType (DEBIT, PREPAID)
- associateIban: String (for DEBIT card)
- internalAmount: double (for PREPAID)
- beneficiaryIban: String
```

### Client Requirements

#### 1. **CardServiceClient** (Mock - Feign ready)

```java
@FeignClient(name = "card-service", url = "${card-service.url}")
public interface CardServiceClient {

    @PostMapping("/authorize")
    OperationResult authorizeCard(CardAuthRequest request);
    // Returns: success=true/false, operationId, status

    @PostMapping("/capture")
    OperationResult captureCard(@RequestParam String authorizationId, @RequestParam double amount);
    // For deferred capture (2-step auth)
}
```

**CardAuthRequest** (new DTO):

```
- cardToken: String (masked/tokenized PAN reference)
- amount: double
- beneficiaryIban: String
- expiryDate: String
- cvv: String
```

**Response**: `OperationResult { success, operationId, status }`

#### 2. **CardInfoExternalDTO** (Already exists)

```
- status: String (ACTIVE, EXPIRED, BLOCKED)
- issuer: String
- cardNetwork: String (VISA, MASTERCARD, etc.)
- lastFourDigits: String
```

### Ledger Entries (Card Payment)

**CardTransactionDetailDTO** (already exists, extend if needed):

```
- cardPan: String (last 4 digits only)
- cardType: CardType
- authorizationId: String
- beneficiaryIban: String
- amount: double
```

**Ledger Entries Created**:

- **DEBIT**: from card account (or associateIban if DEBIT type)
- **CREDIT**: to beneficiaryIban
- Both linked to same preorder for audit

### Validation Rules

| Field    | Rule                         | Example            |
| -------- | ---------------------------- | ------------------ |
| PAN      | 13-19 digits, Luhn check     | `4532015112830366` |
| CVV      | 3-4 digits                   | `123`              |
| Expiry   | MM/YY, not expired           | `12/25`            |
| Amount   | > 0, <= 10,000 (daily limit) | `500.00`           |
| CardType | DEBIT or PREPAID             | DEBIT              |

---

## 3. Instant Transfer Flow (SCT Instant + Fiscal Code Lookup)

### Input Requirements

**DTO**: `TransferRequestDTO` (extended)

```
- sourceIban: String (optional - populated from customer lookup)
- beneficiaryIban: String (insurance/destination account)
- beneficiaryName: String
- amount: double
- message: String
- isInstant: true (flag)
- fiscalCode: String (customer ID - NEW)
```

### Client Requirements

#### 1. **CustomerService** (Mock - Enterprise Lookup)

```java
public interface CustomerService {

    CustomerAccount findByFiscalCode(String fiscalCode);
    // Returns: id, iban, name, fiscalCode, balance, status

    CustomerAccount findById(String customerId);

    List<CustomerAccount> findByIban(String iban);
}
```

**CustomerAccount DTO**:

```
- id: String
- iban: String
- fiscalCode: String (16 char, alphanumeric)
- name: String
- balance: double
- status: String (ACTIVE, SUSPENDED, CLOSED)
- lastTransactionDate: LocalDateTime
```

#### 2. **CoreBankingClient** (Extended)

```java
public interface CoreBankingClient {

    // Existing
    AccountInfoExternalDTO getAccountInfo(String sourceIban);
    OperationResult postAccountUpdate(String sourceIban, double amount);

    // NEW for Instant Payments
    CustomerAccount getAccountById(String accountId);
    // Returns full account info + balance

    OperationResult executeInstantTransfer(InstantTransferRequest request);
    // Special method for SCT Instant with priority flag
}
```

**InstantTransferRequest** (new DTO):

```
- sourceAccountId: String
- beneficiaryIban: String
- amount: double
- scheme: String (SCT_INST)
- urgencyLevel: String (HIGH, NORMAL)
- message: String
```

### Flow: Instant Transfer with Fiscal Code

```
1. Client POST /transaction/v1/preorders
   {
     "beneficiaryIban": "IT99X...",
     "fiscalCode": "RSSMRA80A01H501Z",
     "amount": 500.00,
     "isInstant": true
   }

2. Service: TransactionPreorderService.createPreorder()
   - Store preorder (status: PENDING, sourceIban: null initially)

3. Service: TransactionPreorderService.executePreorder(id)
   - Call InstantTransferProcessor.process(preorder)

4. Processor: InstantTransferProcessor
   a) Lookup customer by fiscal code
      CustomerService.findByFiscalCode("RSSMRA80A01H501Z")
      → CustomerAccount { id: "ACC-123", iban: "IT60X0542811101000000123456", balance: 2000, ... }

   b) Validate balance
      if balance < 500 → return AUTHORIZED (will retry)
      if balance >= 500 → proceed

   c) Validate beneficiary
      CoreBankingClient.getAccountById(beneficiaryIban)
      → AccountInfoExternalDTO { status: ACTIVE, ... }

   d) Execute debit
      CoreBankingClient.executeInstantTransfer({
        sourceAccountId: "ACC-123",
        beneficiaryIban: "IT99X...",
        amount: 500.00,
        scheme: "SCT_INST",
        urgencyLevel: "HIGH"
      })
      → OperationResult { operationId: "SCT-OP-001", status: "COMPLETED" }

   e) Create ledger entries
      - DEBIT: { sourceIban, amount, externalOperationId: "SCT-OP-001", trDetail: {...} }
      - CREDIT: { beneficiaryIban, amount, externalOperationId: "SCT-OP-002", trDetail: {...} }

   f) Update preorder
      status: EXECUTED, sourceIban: "IT60X0542811101000000123456" (from lookup)

5. Response to client
   {
     "id": 1,
     "status": "EXECUTED",
     "source_iban": "IT60X0542811101000000123456",
     "beneficiary_iban": "IT99X...",
     "amount": 500.00,
     "externalOperationId": "SCT-OP-001"
   }
```

### Ledger Entries (Instant Transfer)

**TransferTransactionDetailDTO** (extended for instant):

```
- sourceIban: String
- beneficiaryIban: String
- beneficiaryName: String
- fiscalCode: String (for instant)
- scheme: String (SCT_INST)
- urgencyLevel: String
```

---

## 4. PaymentProcessor Strategy Pattern

### Architecture

```
TransactionPreorderService.executePreorder(id)
    ↓
    Determine TransactionType (WIRE_TRANSFER, CARD, INSTANT_TRANSFER)
    ↓
    Delegate to PaymentProcessor (strategy)
    ├── WireTransferProcessor (existing)
    ├── CardPaymentProcessor (new)
    └── InstantTransferProcessor (new)
    ↓
    Processor returns OperationResult
    ↓
    Service updates preorder status + creates ledger
```

### Interface

```java
public interface PaymentProcessor {
    OperationResult process(TransactionPreorder preorder) throws PaymentProcessingException;
    TransactionType getTransactionType();
}
```

### Processor Registry

```java
@Component
public class PaymentProcessorRegistry {
    private final Map<TransactionType, PaymentProcessor> processors;

    public PaymentProcessor getProcessor(TransactionType type) {
        return processors.get(type);
    }
}
```

### Usage in Service

```java
@Transactional
public boolean executePreorder(long id, String requestId) {
    TransactionPreorder preorder = transactionRep.findById(id)...

    try {
        PaymentProcessor processor = processorRegistry.getProcessor(
            preorder.getTransactionType()
        );

        OperationResult result = processor.process(preorder);

        if (result.isSuccess()) {
            preorder.setStatus(PreorderStatus.EXECUTED);
            // Ledger already created by processor
        } else {
            preorder.setStatus(PreorderStatus.AUTHORIZED);
            preorder.setRetryCount(preorder.getRetryCount() + 1);
        }

        transactionRep.save(preorder);
        return result.isSuccess();

    } catch (PaymentProcessingException e) {
        preorder.setStatus(PreorderStatus.FAILED);
        transactionRep.save(preorder);
        return false;
    }
}
```

---

## 5. Implementation Roadmap

### Phase 1: Foundation (Processors + Clients)

- [ ] Create `PaymentProcessor` interface
- [ ] Create `PaymentProcessorRegistry`
- [ ] Implement `WireTransferProcessor` (refactor existing logic)
- [ ] Create mock `CardServiceClient`
- [ ] Create mock `CustomerService`
- [ ] Create `CardPaymentProcessor`
- [ ] Create `InstantTransferProcessor`
- [ ] Extend `CoreBankingClient` with `getAccountById()`, `executeInstantTransfer()`

### Phase 2: DTOs & Validation

- [ ] `CardAuthRequest` DTO
- [ ] `InstantTransferRequest` DTO
- [ ] `CustomerAccount` DTO
- [ ] Update `TransferTransactionDetailDTO` with instant fields
- [ ] Update `CardTransactionDetailDTO` with card-specific fields
- [ ] Add PAN/CVV validation utilities

### Phase 3: Controller Updates

- [ ] `TransController.createPreorder()` to accept `CardPaymentRequestDTO`
- [ ] `TransController.executePreorder()` to determine processor type
- [ ] Error handling for each processor type

### Phase 4: Testing & Refinement

- [ ] Unit tests for each processor
- [ ] Integration tests for card + instant flows
- [ ] Mock client responses

---

## 6. Key Decisions

| Decision                                  | Rationale                                                                     |
| ----------------------------------------- | ----------------------------------------------------------------------------- |
| **PaymentProcessor Strategy**             | Clean separation; easy to add new types; testable                             |
| **externalOperationId on LedgerEntity**   | Immutable audit trail; reconciliation friendly                                |
| **Two ledger entries per transfer**       | Clear debit/credit visibility; compliance requirement                         |
| **fiscalCode in preorder**                | Enables customer lookup; can be null for wire transfers                       |
| **Mock clients initially**                | Fast feedback; real clients via Feign later                                   |
| **No Util methods for response building** | Keep response building in processors; utils are for validation/transformation |

---

## 7. Notes

- **Utilities** (`IbanUtils`, `AmountUtils`, etc.) are for **validation only**, not response building
- Response DTOs are mapped in processors and service layer
- Ledger entries are created **inside processors** (responsibility delegation)
- Error handling distinguishes **retryable** (insufficient balance) vs **non-retryable** (invalid IBAN)
- Instant payments prioritized by `urgencyLevel` (HIGH = SCT Inst, NORMAL = standard wire)

---

## Next Steps

1. Create `PaymentProcessor` interface + registry
2. Implement mock `CustomerService` + `CardServiceClient`
3. Create `CardPaymentProcessor` + `InstantTransferProcessor`
4. Refactor `executePreorder()` to use strategy pattern
5. Add DTOs + extend existing ones
6. Test each flow end-to-end
