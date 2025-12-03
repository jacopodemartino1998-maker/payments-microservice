package eu.proxima.payments.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.proxima.payments.client.CoreBankingClient;
import eu.proxima.payments.enums.PreorderStatus;
import eu.proxima.payments.exception.InvalidAmountException;
import eu.proxima.payments.exception.InvalidIbanException;
import eu.proxima.payments.model.TransactionPreorder;
import eu.proxima.payments.model.exit.TransactionPreorderDTO;
import eu.proxima.payments.model.mapper.TransactionPreorderMapper;
import eu.proxima.payments.repositories.TransactionEntityRepository;
import eu.proxima.payments.utils.AmountUtils;
import eu.proxima.payments.utils.IbanUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionPreorderService {

    @Autowired
    private TransactionEntityRepository transactionRep;

    @Autowired
    private TransactionPreorderMapper mapper;

    @Autowired
    private CoreBankingClient coreBankingClient;

    @Autowired
    private eu.proxima.payments.repositories.LedgerRepository ledgerRepository;

    // --- CREATE ---
    @Transactional
    public TransactionPreorderDTO createPreorder(eu.proxima.payments.model.request.TransferRequestDTO dto,
            String requestId) {
        log.info("Richiesta creazione Preorder per IBAN: {}, importo: {}", dto.getBeneficiaryIban(), dto.getAmount());

        TransactionPreorder entity = mapper.toEntity(dto);
        TransactionPreorder saved = transactionRep.save(entity);

        log.info("Preorder creato con ID: {}", saved.getId());
        return mapper.toDto(saved);
    }

    // --- READ (All) with pagination and filters ---
    public org.springframework.data.domain.Page<TransactionPreorderDTO> getAllPreorders(
            eu.proxima.payments.enums.PreorderStatus status,
            String beneficiaryIban,
            String createdFrom,
            String createdTo,
            org.springframework.data.domain.Pageable pageable) {
        log.debug("Recupero lista Preorders con filtri status={}, beneficiaryIban={}, createdFrom={}, createdTo={}",
                status, beneficiaryIban, createdFrom, createdTo);

        java.time.LocalDateTime from = null, to = null;
        try {
            if (createdFrom != null && !createdFrom.isBlank())
                from = java.time.LocalDateTime.parse(createdFrom);
            if (createdTo != null && !createdTo.isBlank())
                to = java.time.LocalDateTime.parse(createdTo);
        } catch (Exception e) {
            log.warn("Date parsing failed for filters: {} - {}", createdFrom, createdTo);
        }

        java.util.List<TransactionPreorderDTO> all = new java.util.ArrayList<>();
        for (TransactionPreorder e : transactionRep.findAll()) {
            TransactionPreorderDTO p = mapper.toDto(e);
            if (status != null && p.getStatus() != status)
                continue;
            if (beneficiaryIban != null && !beneficiaryIban.isBlank()
                    && !p.getBeneficiary_iban().equalsIgnoreCase(beneficiaryIban))
                continue;
            java.time.LocalDateTime created = p.getExecutedAt();
            if (from != null) {
                if (created == null || created.isBefore(from))
                    continue;
            }
            if (to != null) {
                if (created == null || created.isAfter(to))
                    continue;
            }
            all.add(p);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), all.size());
        java.util.List<TransactionPreorderDTO> pageContent = start <= end ? all.subList(start, end)
                : java.util.List.of();
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, all.size());
    }

    // --- UPDATE ---
    @Transactional
    public TransactionPreorderDTO updatePreorder(long id, TransactionPreorderDTO dto) {
        log.info("Richiesta modifica Preorder ID: {}", id);

        TransactionPreorder existingEntity = transactionRep.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Preorder non trovato con id: " + id));

        // Aggiorniamo solo i campi modificabili
        existingEntity.setBeneficiary_iban(dto.getBeneficiary_iban());
        existingEntity.setAmount(dto.getAmount());
        existingEntity.setMessage(dto.getMessage());

        TransactionPreorder updatedEntity = transactionRep.save(existingEntity);

        log.info("Preorder ID: {} aggiornato con successo", id);
        return mapper.toDto(updatedEntity);
    }

    // --- DELETE ---
    @Transactional
    public void deletePreorder(long id) {
        log.info("Richiesta cancellazione Preorder ID: {}", id);

        if (!transactionRep.existsById(id)) {
            log.warn("Tentativo di cancellazione fallito: Preorder ID {} non esiste", id);
            throw new EntityNotFoundException("Preorder non trovato con id: " + id);
        }

        transactionRep.deleteById(id);
        log.info("Preorder ID: {} cancellato con successo", id);
    }

    // --- EXECUTE ---
    @Transactional
    public boolean executePreorder(long id, String requestId) {
        log.info(">>> INIZIO ESECUZIONE Preorder ID: {} <<<", id);

        TransactionPreorder preorder = transactionRep.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Preorder non trovato con id: " + id));

        // If already executed -> idempotent return
        if (preorder.getStatus() == PreorderStatus.EXECUTED) {
            log.info("Preorder ID {} già eseguito", id);
            return true;
        }

        // VALIDAZIONI PRELIMINARI
        try {
            try {
                if (!IbanUtils.ibanIsValid(preorder.getBeneficiary_iban())) {
                    log.warn("Validation failed: invalid IBAN for preorder id={}", id);
                    preorder.setStatus(PreorderStatus.FAILED);
                    transactionRep.save(preorder);
                    return false;
                }
            } catch (InvalidIbanException iie) {
                log.warn("Invalid IBAN for preorder id={}: {}", id, iie.getMessage());
                preorder.setStatus(PreorderStatus.FAILED);
                transactionRep.save(preorder);
                return false;
            }

            try {
                AmountUtils.amountIsValid(preorder.getAmount());
            } catch (InvalidAmountException iae) {
                log.warn("Invalid amount for preorder id={}: {}", id, iae.getMessage());
                preorder.setStatus(PreorderStatus.FAILED);
                transactionRep.save(preorder);
                return false;
            }

            log.info("Invio richiesta a CoreBanking per IBAN: {} importo: {}", preorder.getBeneficiary_iban(),
                    preorder.getAmount());

            eu.proxima.payments.model.external.OperationResult result = coreBankingClient.postAccountUpdate(
                    preorder.getBeneficiary_iban(), preorder.getAmount());

            if (result != null && result.isSuccess() && result.getOperationId() != null
                    && !result.getOperationId().isBlank()) {
                // Create ledger entry with external operation id for audit trail
                eu.proxima.payments.model.LedgerEntity ledger = new eu.proxima.payments.model.LedgerEntity();
                ledger.setTransactionType(eu.proxima.payments.enums.TransactionType.WIRE_TRANSFER);
                ledger.setAmount(preorder.getAmount());
                ledger.setCurrencyType(eu.proxima.payments.enums.CurrencyType.EUR);
                ledger.setEntryType(eu.proxima.payments.enums.EntryType.CREDIT);
                ledger.setExternalOperationId(result.getOperationId());

                // Store transaction details using TransferTransactionDetailDTO
                eu.proxima.payments.model.detail.TransferTransactionDetailDTO trDetail = new eu.proxima.payments.model.detail.TransferTransactionDetailDTO();
                trDetail.setSourceIban(preorder.getSource_iban());
                trDetail.setBeneficiaryIban(preorder.getBeneficiary_iban());
                trDetail.setBeneficiaryName(preorder.getBeneficiary_name());
                trDetail.setTransactionDate(java.time.LocalDate.now());
                ledger.setTrDetail(trDetail);

                ledgerRepository.save(ledger);

                preorder.setStatus(PreorderStatus.EXECUTED);
                transactionRep.save(preorder);
                log.info("Preorder ID {} eseguito con successo; externalOpId={}", id, result.getOperationId());
                return true;
            } else {
                preorder.setStatus(PreorderStatus.AUTHORIZED);
                preorder.setRetryCount(preorder.getRetryCount() + 1);
                transactionRep.save(preorder);
                log.warn("Esecuzione Preorder ID {} fallita (no operation id). RetryCount={}", id,
                        preorder.getRetryCount());
                return false;
            }

        } catch (Exception e) {
            log.error("Errore durante esecuzione Preorder ID {}: {}", id, e.getMessage());
            preorder.setStatus(PreorderStatus.AUTHORIZED);
            preorder.setRetryCount(preorder.getRetryCount() + 1);
            transactionRep.save(preorder);
            return false;
        }
    }
}