package eu.proxima.payments.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Version;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import eu.proxima.payments.enums.PreorderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity // tabella per transazioni in promemoria
@Table(name = "pre_orders")
public class TransactionPreorder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "preOrder_id")
	private long id;

	@Column(name = "source_iban", nullable = false)
	private String source_iban;

	@Column(name = "beneficiary_iban", nullable = false)
	private String beneficiary_iban;

	@Column(name = "beneficiary_name", nullable = false)
	private String beneficiary_name;

	@Column(name = "amount", nullable = false)
	private double amount;

	@Column(name = "message")
	private String message;
	@Column(name = "execute_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime executedAt;
	@Column(name = "update_at")
	@UpdateTimestamp
	private LocalDateTime updateAt;
	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private PreorderStatus status = PreorderStatus.PENDING;

	@Version
	private Long version;

	@Column(name = "retry_count", nullable = false)
	private int retryCount = 0;

	@Column(name = "max_retries", nullable = false)
	private int maxRetries = 5;

	// external operation id is stored on the LedgerEntity for reconciliation
}
