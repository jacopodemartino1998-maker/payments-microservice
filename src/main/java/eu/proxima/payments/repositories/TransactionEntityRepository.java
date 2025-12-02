package eu.proxima.payments.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.proxima.payments.model.TransactionPreorder;

@Repository
public interface TransactionEntityRepository extends JpaRepository<TransactionPreorder, Long> {
	// Eventuali query custom (es. findByBeneficiaryIban)
}