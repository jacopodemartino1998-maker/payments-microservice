package eu.proxima.payments.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.proxima.payments.model.TransactionPreorder;
import eu.proxima.payments.model.exit.TransactionPreorderDTO;
import eu.proxima.payments.model.mapper.TransactionPreorderMapper;
import eu.proxima.payments.repositories.TransactionEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j // Abilita il logger
public class TransactionPreorderService {

    @Autowired
    private TransactionEntityRepository transactionRep;

    @Autowired
    private TransactionPreorderMapper mapper;

    // --- CREATE ---
    @Transactional
    public TransactionPreorderDTO createPreorder(TransactionPreorderDTO dto) {
        log.info("Richiesta creazione Preorder per IBAN: {}, importo: {}", dto.getBeneficiary_iban(), dto.getAmount());

        TransactionPreorder entity = mapper.toEntity(dto);
        // executedAt gestito da @CreationTimestamp

        TransactionPreorder savedEntity = transactionRep.save(entity);

        log.info("Preorder creato con successo. ID: {}", savedEntity.getId());
        return mapper.toDto(savedEntity);
    }

    // --- READ (Single) ---
    public TransactionPreorderDTO getPreorderById(long id) {
        log.debug("Recupero Preorder ID: {}", id);

        TransactionPreorder entity = transactionRep.findById(id)
                .orElseThrow(() -> {
                    log.error("Preorder con ID {} non trovato", id);
                    return new EntityNotFoundException("Preorder non trovato con id: " + id);
                });

        return mapper.toDto(entity);
    }

    // --- READ (All) ---
    public List<TransactionPreorderDTO> getAllPreorders() {
        log.debug("Recupero lista completa Preorders");

        return transactionRep.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
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
    public boolean executePreorder(long id) {
        log.info(">>> INIZIO ESECUZIONE Preorder ID: {} <<<", id);

        // 1. Recupero il preorder
        TransactionPreorder preorder = transactionRep.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Preorder non trovato con id: " + id));

        try {
            // 2. LOGICA DI BUSINESS (Qui chiameresti i servizi esterni)
            // Esempio:
            // boolean esito = coreBankingClient.doTransfer(preorder.getBeneficiary_iban(),
            // preorder.getAmount());
            // validationService.validate(preorder);

            log.info("Validazione e chiamata ai servizi esterni per IBAN: {}...", preorder.getBeneficiary_iban());

            // Simuliamo un'esecuzione andata a buon fine
            boolean executionSuccess = true;

            if (executionSuccess) {
                // 3. Se successo, spostiamo nello storico (Ledger) e cancelliamo il preorder
                // transactionLedgerService.saveFromPreorder(preorder); <-- Ipotetico servizio

                log.info("Esecuzione completata. Eliminazione Preorder ID: {}", id);
                transactionRep.delete(preorder);
                return true;
            } else {
                log.warn("Esecuzione fallita per logica di business per Preorder ID: {}", id);
                return false;
            }

        } catch (Exception e) {
            log.error("Errore critico durante l'esecuzione del Preorder ID: {}", id, e);
            throw e; // Rilanciamo per il rollback della transazione
        }
    }
}