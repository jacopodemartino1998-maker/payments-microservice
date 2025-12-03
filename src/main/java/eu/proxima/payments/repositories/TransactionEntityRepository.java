package eu.proxima.payments.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.proxima.payments.model.TransactionPreorder;

@Repository
public interface TransactionEntityRepository extends JpaRepository<TransactionPreorder, Long> {
	// Eventuali query custom (es. findByBeneficiaryIban)
	java.util.List<TransactionPreorder> findByStatus(eu.proxima.payments.enums.PreorderStatus status);

	java.util.List<TransactionPreorder> findByBeneficiary_ibanAndStatus(String iban,
			eu.proxima.payments.enums.PreorderStatus status);

	java.util.List<TransactionPreorder> findByStatusAndRetryCountLessThan(
			eu.proxima.payments.enums.PreorderStatus status, int maxRetry);

	// legacy idempotency helper removed: find by external operation id if needed in
	// future
	// java.util.Optional<TransactionPreorder> findByRequestId(String requestId);
}