package eu.proxima.payments.model;

import java.sql.Date;

import org.hibernate.annotations.CreationTimestamp;

import eu.proxima.payments.enums.CurrencyType;
import eu.proxima.payments.enums.EntryType;
import eu.proxima.payments.enums.TransactionType;
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
@Table(name = "pre_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPreorder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "preOrder_id")
	private long id;
	@Column(name="beneficiary_iban" , nullable = false)
	private String beneficiary_iban;
	@Column(name = "amount",nullable = false)
	private double amount;
	@Column(name = "message")
	private String message;
	@Column(name = "execute_at",nullable = false, updatable = false)
	@CreationTimestamp
	private Date executedAt;
	@Column(name = "update_at")
	private Date updateAt;
}
