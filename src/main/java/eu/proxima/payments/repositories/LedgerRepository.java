package eu.proxima.payments.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.proxima.payments.enums.OrderStatus;
import eu.proxima.payments.model.LedgerEntity;

@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntity, Long> {
    /**
     * Trova tutti i bonifici in un determinato stato.
     * Utile per lo Scheduler che deve processare i bonifici CREATED (se
     * implementato)
     * o per trovare quelli FALLITI.
     */
    List<LedgerEntity> findByStatus(OrderStatus status);

    /**
     * Trova tutti i bonifici richiesti da uno specifico conto.
     * Utile se vuoi mostrare una lista "I miei bonifici" separata dall'estratto
     * conto.
     */
    List<LedgerEntity> findBySourceAccountIdOrderByCreatedAtDesc(Long sourceAccountId);
}
