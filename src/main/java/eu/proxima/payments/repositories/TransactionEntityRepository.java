package eu.proxima.payments.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.proxima.payments.enums.OrderStatus;

@Repository
public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, Long> {

    /**
     * Trova tutti i bonifici in un determinato stato.
     * Utile per lo Scheduler che deve processare i bonifici CREATED (se implementato)
     * o per trovare quelli FALLITI.
     */
    List<TransactionEntity> findByStatus(OrderStatus status);

    /**
     * Trova tutti i bonifici richiesti da uno specifico conto.
     * Utile se vuoi mostrare una lista "I miei bonifici" separata dall'estratto conto.
     */
	List<TransactionEntity> findBySourceAccountIdOrderByCreatedAtDesc(Long sourceAccountId);
}