package eu.proxima.payments.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import eu.proxima.payments.enums.CurrencyType;
import eu.proxima.payments.enums.EntryType;
import eu.proxima.payments.enums.TransactionType;
import jakarta.persistence.Convert;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import eu.proxima.payments.model.generiscsinterface.TransactionGenericsDetailDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id")
	private long id;
	@Column(name = "transaction_type", nullable = false)
	private TransactionType transactionType;
	@Column(name = "amount", nullable = false)
	private double amount;
	@Column(name = "currency_type", nullable = false)
	private CurrencyType currencyType;
	@Column(name = "execute_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime executedAt;
	@Column(name = "entry_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private EntryType entryType;// Debit / Credi
	@Column(name = "transaction_detail", nullable = true)
	@Convert(converter = eu.proxima.payments.model.util.TransactionDetailConverter.class)
	private TransactionGenericsDetailDTO trDetail;

	@Column(name = "external_operation_id", nullable = true)
	private String externalOperationId;

}
