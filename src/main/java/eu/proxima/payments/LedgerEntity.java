package eu.proxima.payments;

import java.sql.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;

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
@Table(name ="transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "transaction_id")
	private long id;
	@Column(name="transaction_type" , nullable = false)
	private TransactionType transactionType;
	@Column(name = "amount",nullable = false)
	private double amount;
	@Column(name = "currency_type",nullable = false)
	private CurrencyType currencyType;
	@Column(name = "execute_at",nullable = false, updatable = false)
	@CreationTimestamp
	private Date executedAt;
	@Column(name = "entry_type", nullable = false)
	private EntryType entryType;//Debit / Credi
	@Column(name = "transaction_detail", nullable =  true )
	private TransactionGenericsDetailDTO trDetail;
	
}
