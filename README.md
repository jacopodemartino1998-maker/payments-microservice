# Payments Orchestrator

Spring Boot microservice per validare, instradare e registrare transazioni di pagamento (bonifici, carte, pagamenti istantanei).

## 📋 Struttura Progetto

```
src/main/java/eu/proxima/payments/
│
├── controller/                      # REST Endpoints
│   ├── TransController.java         # Preorder CRUD + execute (GET, POST, PUT, DELETE)
│   └── LedgerController.java        # Ledger query (not found - future)
│
├── service/                         # Business Logic
│   ├── TransactionPreorderService.java  # Preorder CRUD, execute, validazione
│   ├── LedgerService.java              # Ledger operations (future)
│   ├── TestPaymentService.java         # Test utilities
│   └── processor/                      # Payment processors (future: strategy pattern)
│
├── client/
│   └── CoreBankingClient.java       # Mock client per operazioni bancarie
│                                      # Metodi: getAccountInfo(), postAccountUpdate()
│
├── model/                           # Domain Models
│   ├── TransactionPreorder.java     # Entity: preorder state (PENDING, AUTHORIZED, EXECUTED, FAILED)
│   │                                  # Campi: id, beneficiary_iban, amount, message, status, version, retryCount, maxRetries
│   │                                  # @Version per optimistic locking
│   │
│   ├── LedgerEntity.java            # Entity: immutable audit trail
│   │                                  # Campi: id, transactionType, amount, currencyType, entryType, externalOperationId, trDetail, executedAt
│   │                                  # trDetail: JSON polymorphic details (sourceIban, beneficiaryIban, bankReference, etc.)
│   │
│   ├── request/                     # DTOs per richieste API (INPUT)
│   │   ├── TransferRequestDTO.java  # Wire transfer: sourceIban, beneficiaryIban, beneficiaryName, amount, message, isInstant
│   │   └── CardPaymentRequestDTO.java  # Card payment: pan, cvv, expiryDate, amount, cardType, associateIban, beneficiaryIban
│   │
│   ├── response/                    # DTOs per risposte API (OUTPUT)
│   │   ├── TransactionPreorderDTO.java  # Preorder state response
│   │   ├── PaymentExecutionResponseDTO.java # Execution result
│   │   └── TransactionResposneDto.java     # Generic transaction response
│   │
│   ├── external/                    # DTOs per risposte esterne (BANK RESPONSES)
│   │   ├── OperationResult.java     # Bank response: success, operationId, status
│   │   ├── AccountInfoExternalDTO.java  # Account info: status, availableBalance
│   │   └── CardInfoExternalDTO.java     # Card info (future)
│   │
│   ├── mapper/                      # Entity <-> DTO Mappers
│   │   ├── TransactionPreorderMapper.java  # toDto(entity), toEntity(dto), toEntity(TransferRequestDTO)
│   │   ├── LedgerMapper.java               # Ledger entity <-> DTO
│   │   └── PaymentMapper.java              # Generic payment mapping
│   │
│   ├── detail/                      # Transaction detail types (future)
│   │
│   ├── exit/                        # Alias per response/ DTOs
│   │
│   ├── generiscsinterface/          # Generic interfaces
│   │   └── TransactionGenericsDetailDTO.java  # Base interface per polymorphic trDetail
│   │
│   └── util/
│       └── TransactionDetailConverter.java    # Jackson @Convert per serializzazione JSON trDetail
│
├── repositories/                    # JPA Repositories
│   ├── TransactionEntityRepository.java     # CRUD preorders: findAll(), findById(), findByStatus(), etc.
│   └── LedgerRepository.java                # CRUD ledger (future)
│
├── enums/
│   ├── TransactionType.java         # CARD, WIRE_TRANSFER, DEPOSIT, WITHDRAWAL
│   ├── PreorderStatus.java          # PENDING, AUTHORIZED, EXECUTED, FAILED
│   ├── EntryType.java               # DEBIT, CREDIT
│   ├── CardType.java                # DEBIT, PREPAID
│   ├── CurrencyType.java            # EUR, USD, ... (attualmente solo EUR)
│   └── OrderStatus.java             # (da verificare uso)
│
├── exception/
│   ├── InvalidIbanException.java
│   ├── InvalidAmountException.java
│   ├── GlobalExceptionHandler.java  # @ControllerAdvice per errori globali
│   └── ... (altre eccezioni custom)
│
├── utils/                           # Utility Functions
│   ├── IbanUtils.java               # Validazione IBAN: format, lunghezza per paese
│   ├── AmountUtils.java             # Validazione importi: positive, entro limiti
│   ├── CvvUtils.java                # Validazione CVV carta
│   ├── PanUtils.java                # Validazione PAN carta
│   ├── DateUtils.java               # Date utilities
│   └── FileUtil.java                # File operations (se usato)
│
├── scheduler/                       # Scheduled Tasks (future)
│   └── PreorderRetryScheduler.java  # Retry logic per preorder AUTHORIZED
│
└── PaymentsOrchestratorApplication.java  # Spring Boot entry point
```

---

## 🏗️ Architecture

### Layers

1. **Controller** (`TransController`, `LedgerController`)

   - Endpoint REST: GET, POST, PUT, DELETE
   - Input validation via `@Valid` annotations
   - Response mapping via DTOs

2. **Service** (`TransactionPreorderService`, `LedgerService`)

   - Business logic: CRUD, execute, validation
   - External client calls (CoreBankingClient)
   - Ledger entry creation
   - Error handling e retry logic

3. **Repository** (Spring Data JPA)

   - Database operations
   - Query methods: findByStatus(), findByBeneficiaryIban(), etc.

4. **Client** (`CoreBankingClient`)
   - Mock per core banking API
   - Metodi: getAccountInfo(), postAccountUpdate()
   - Restituisce `OperationResult` con externalOperationId

### Key Features

| Aspetto            | Implementazione                                                                                 |
| ------------------ | ----------------------------------------------------------------------------------------------- |
| **Concurrency**    | `@Version` optimistic locking su `TransactionPreorder`                                          |
| **JSON Storage**   | `TransactionDetailConverter` per serializzazione polymorphic `trDetail`                         |
| **Validation**     | Jakarta annotations (`@NotBlank`, `@Positive`, `@Size`) + custom utils (IbanUtils, AmountUtils) |
| **Timestamps**     | `@CreationTimestamp`, `@UpdateTimestamp` su entities                                            |
| **External IDs**   | `externalOperationId` su `LedgerEntity` per reconciliazione                                     |
| **Mapper Pattern** | Separate mapper classes per decoupling entity ↔ DTO                                             |
| **Retry Logic**    | `retryCount`, `maxRetries` su preorder per scheduled retry (future)                             |

---

## 🔌 API Endpoints

### Preorders - `/transaction/v1/preorders`

| Metodo     | Endpoint                  | Descrizione                                        | Request                                                                   | Response                      |
| ---------- | ------------------------- | -------------------------------------------------- | ------------------------------------------------------------------------- | ----------------------------- |
| **GET**    | `/preorders`              | Lista paginated con filtri                         | query params: status, beneficiaryIban, createdFrom, createdTo, page, size | Page<TransactionPreorderDTO>  |
| **POST**   | `/preorders`              | Crea nuovo preorder                                | TransferRequestDTO o CardPaymentRequestDTO                                | TransactionPreorderDTO        |
| **PUT**    | `/preorders/{id}`         | Aggiorna preorder                                  | TransactionPreorderDTO                                                    | TransactionPreorderDTO        |
| **DELETE** | `/preorders/{id}`         | Cancella preorder                                  | -                                                                         | 204 No Content                |
| **POST**   | `/preorders/{id}/execute` | Esegui preorder (valida, chiama bank, crea ledger) | -                                                                         | 200 OK oppure 400 Bad Request |

**Esempio Request**: Wire Transfer

```json
POST /transaction/v1/preorders
{
  "sourceIban": "IT60X0542811101000000123456",
  "beneficiaryIban": "IT99X0123456789012345678901",
  "beneficiaryName": "Acme Corp",
  "amount": 500.00,
  "message": "Invoice #123",
  "isInstant": false
}
```

**Esempio Response**:

```json
{
  "id": 1,
  "beneficiary_iban": "IT99X0123456789012345678901",
  "amount": 500.0,
  "message": "Invoice #123",
  "status": "PENDING",
  "executedAt": "2024-01-15T10:30:00",
  "updateAt": "2024-01-15T10:30:00",
  "retryCount": 0
}
```

### Ledger - `/ledger/v1` (Future)

TBD - Endpoints per query ledger entries

---

## 📦 Key DTOs

### Input DTOs (Request)

**TransferRequestDTO**

```
- sourceIban: String (validated @NotBlank)
- beneficiaryIban: String (validated @NotBlank)
- beneficiaryName: String (validated @NotBlank)
- amount: double (validated @Positive)
- message: String (max 512 chars)
- isInstant: boolean (flag per SCT Instant)
```

**CardPaymentRequestDTO**

```
- pan: String
- cvv: String
- expiryDate: String
- amount: double
- cardType: CardType (DEBIT, PREPAID)
- internalAmount: double (if PREPAID)
- associateIban: String (if DEBIT)
- beneficiaryIban: String
```

### Output DTOs (Response)

**TransactionPreorderDTO**

```
- id: long
- beneficiary_iban: String
- amount: double
- message: String
- status: PreorderStatus
- executedAt: LocalDateTime
- updateAt: LocalDateTime
- retryCount: int
```

### External DTOs (Bank Responses)

**OperationResult**

```
- success: boolean
- operationId: String (per reconciliation)
- status: String (e.g., "COMPLETED", "PENDING")
```

**AccountInfoExternalDTO**

```
- status: String (e.g., "ACTIVE", "CLOSED")
- availableBalance: double
```

---

## 🎯 Enums

| Enum                | Valori                                   |
| ------------------- | ---------------------------------------- |
| **PreorderStatus**  | PENDING, AUTHORIZED, EXECUTED, FAILED    |
| **TransactionType** | CARD, WIRE_TRANSFER, DEPOSIT, WITHDRAWAL |
| **EntryType**       | DEBIT, CREDIT                            |
| **CardType**        | DEBIT, PREPAID                           |
| **CurrencyType**    | EUR (primary), others TBD                |

---

## 🔍 Entities

### TransactionPreorder (Table: `pre_orders`)

| Campo            | Tipo                  | Note                                      |
| ---------------- | --------------------- | ----------------------------------------- |
| id               | long                  | PK, auto-generated                        |
| beneficiary_iban | String                | Destination account                       |
| amount           | double                | Payment amount                            |
| message          | String                | Description/reference                     |
| status           | PreorderStatus (enum) | PENDING → AUTHORIZED → EXECUTED or FAILED |
| executedAt       | LocalDateTime         | @CreationTimestamp                        |
| updateAt         | LocalDateTime         | @UpdateTimestamp                          |
| version          | Long                  | Optimistic locking                        |
| retryCount       | int                   | Number of retry attempts                  |
| maxRetries       | int                   | Max allowed retries (default 5)           |

**Nota**: `sourceIban` è estratto da una lookup di CustomerService (future) o incluso nel dto di richiesta.

### LedgerEntity (Table: `transactions`)

| Campo               | Tipo                       | Note                                                                      |
| ------------------- | -------------------------- | ------------------------------------------------------------------------- |
| id                  | long                       | PK, auto-generated                                                        |
| transactionType     | TransactionType (enum)     | WIRE_TRANSFER, CARD, etc.                                                 |
| amount              | double                     | Amount debited/credited                                                   |
| currencyType        | CurrencyType (enum)        | EUR, etc.                                                                 |
| entryType           | EntryType (enum)           | DEBIT or CREDIT                                                           |
| executedAt          | LocalDateTime              | @CreationTimestamp                                                        |
| externalOperationId | String                     | Bank operation ID (per reconciliation)                                    |
| trDetail            | Map<String, Object> (JSON) | Polymorphic details: { "sourceIban": "...", "bankReference": "...", ... } |

**Nota**: Ledger è **immutable** - record di auditoria permanente.

---

## 🛠️ Utilities

### IbanUtils.java

- `ibanIsValid(String)`: Validazione IBAN con regex + lunghezza paese
- `CleanIban(String)`: Normalizzazione (uppercase, trim)
- Paesi supportati: IT, FR, DE, ES, NL, BE, GB, PT, IE, CH, PL, AT

### AmountUtils.java

- `amountIsValid(Double)`: Validazione (not null, > 0, <= 1,000,000)

### CvvUtils.java, PanUtils.java, DateUtils.java

- Card validation utilities (future implementation)

---

## 🔐 Validation

| Componente                 | Validazione                                                                                      |
| -------------------------- | ------------------------------------------------------------------------------------------------ |
| **TransferRequestDTO**     | @NotBlank sourceIban, beneficiaryIban, beneficiaryName; @Positive amount; @Size(max=512) message |
| **IbanUtils**              | Regex + country-specific length check                                                            |
| **AmountUtils**            | Positive, max 1M                                                                                 |
| **GlobalExceptionHandler** | Centralized error handling                                                                       |

---

## 🏦 External Client

### CoreBankingClient.java (Mock)

**Metodo**: `getAccountInfo(String sourceIban)`

- Restituisce: `AccountInfoExternalDTO { status: "ACTIVE", availableBalance: MAX_DOUBLE }`

**Metodo**: `postAccountUpdate(String sourceIban, double amount)`

- Restituisce: `OperationResult { success: true, operationId: "op-<UUID>", status: "COMPLETED" }`
- Simula: debit/credit sul core banking
- **Nota**: In production, sostituire con Feign client @FeignClient(...) con base URL reale

---

## 📊 Dipendenze Principali

```xml
<!-- Spring Boot -->
<dependency>spring-boot-starter</dependency>
<dependency>spring-boot-starter-webmvc</dependency>
<dependency>spring-boot-starter-data-jpa</dependency>

<!-- Logging -->
<dependency>spring-boot-starter-log4j2</dependency>

<!-- Cloud -->
<dependency>spring-cloud-starter-openfeign</dependency>

<!-- DB -->
<dependency>mysql-connector-j (runtime)</dependency>
<dependency>h2 (test scope)</dependency>

<!-- Validation -->
<dependency>jakarta.validation-api (3.0.2)</dependency>

<!-- JSON -->
<dependency>jackson-databind (2.17.2)</dependency>

<!-- Utilities -->
<dependency>lombok (1.18.30)</dependency>

<!-- Testing -->
<dependency>junit-jupiter (5.10.0, test scope)</dependency>
<dependency>mockito-junit-jupiter (5.5.0, test scope)</dependency>
```

---

## 🧪 Testing

```bash
# Unit tests only
mvn test

# Full build with tests
mvn clean package

# Build senza tests
mvn -DskipTests package
```

**Test Scope**: H2 in-memory database (config: `src/test/resources/application.properties`)

---

## 🚀 Local Development

1. **Database**: H2 per test, MySQL per production (config: `src/main/resources/application.properties`)
2. **Build**: `mvn clean package`
3. **Run**: `java -jar target/payments-orchestrator-0.0.1-SNAPSHOT.jar` oppure IDE
4. **Test Endpoints**: Postman, curl, oppure IDE REST client

---

## ✅ Stato Attuale (MVP)

| Feature                          | Status                                   |
| -------------------------------- | ---------------------------------------- |
| Preorder CRUD                    | ✅ Implementato                          |
| Execute (valida + bank call)     | ✅ Implementato (mock CoreBankingClient) |
| Ledger creation                  | ✅ Implementato                          |
| Wire transfer logic              | ✅ Implementato                          |
| Paginated query con filtri       | ✅ Implementato                          |
| Validation (IBAN, amount)        | ✅ Implementato                          |
| Optimistic locking (@Version)    | ✅ Implementato                          |
| Retry logic (fields)             | ✅ Struttura presente, scheduler TBD     |
| Card payment support             | ⏳ DTO presente, logic TBD               |
| Instant payment (SCT Inst)       | ⏳ Flag presente, logic TBD              |
| Webhook support                  | ❌ Not implemented                       |
| 3DS/SCA orchestration            | ❌ Not implemented                       |
| Reconciliation job               | ❌ Not implemented                       |
| Client-side idempotency          | ❌ Not implemented                       |
| Observability (metrics, tracing) | ❌ Not implemented                       |

---

## 🎯 Prossimi Passi (Priorità)

### Fase 1: Pulizia Codice (IMMEDIATO)

1. Rimuovere magic strings e input non validati
2. Aggiungere input validation su `TransferRequestDTO` e `CardPaymentRequestDTO`
3. Semplificare logica nel service (rimuovere duplicazioni)
4. Aggiungere logging strutturato

### Fase 2: Card Payment Support (BREVE TERMINE)

1. Implementare `CardPaymentProcessor` (strategy pattern)
2. Creare `CardServiceClient` (Feign mock)
3. Aggiungere validazione PAN/CVV
4. Supportare transazioni multi-ledger (debit card account + credit beneficiary)

### Fase 3: Instant Payments + Fiscal Code (BREVE TERMINE)

1. Implementare `InstantTransferProcessor`
2. Creare `CustomerService` client (lookup fiscal code → account ID)
3. Aggiungere supporto SCT Instant in ledger
4. Validare limiti di importo per instant payments

### Fase 4: Webhook + Async (MEDIO TERMINE)

1. Aggiungere `WebhookController` e `WebhookService`
2. Implementare firma HMAC validation
3. Creare `WebhookEvent` entity + repository
4. Update preorder/ledger basato su webhook events

### Fase 5: Robustness (MEDIO TERMINE)

1. Implementare client-side `requestId` per idempotency
2. Aggiungere reconciliation scheduler (query external systems)
3. Implementare Resilience4j (circuit breaker, retry, timeout)
4. Aggiungere metrics (Micrometer + Prometheus)

---

## 📝 Note Architetturali

- **Preorder vs Ledger**: Preorder è **mutable** state machine; Ledger è **immutable** audit trail
- **externalOperationId**: Stored su LedgerEntity (non Preorder) per tracciamento e reconciliazione
- **trDetail JSON**: Polymorphic details per flessibilità - evita schema explosion
- **Strategy Pattern**: Future: PaymentProcessor interface per card/wire/instant logic separation
- **Mapper Pattern**: DTOs separate per API versioning e decoupling
- **Validation Layer**: Combo di Jakarta annotations + custom utils per riusabilità

---

## 🔗 Riferimenti Utili

- Spring Boot 4.0 Docs: https://spring.io/projects/spring-boot
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Jakarta Validation: https://jakarta.ee/specifications/bean-validation/
- IBAN Standard: ISO 13616
- PSD2/Open Banking: https://www.openbanking.org.uk/
