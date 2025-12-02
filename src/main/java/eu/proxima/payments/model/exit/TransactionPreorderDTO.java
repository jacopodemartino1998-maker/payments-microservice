package eu.proxima.payments.model.exit;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionPreorderDTO {
	private long id;
	private String beneficiary_iban;
	private double amount;
	private String message;
	private Date executedAt;
	private Date updateAt;

}
