package eu.proxima.payments.model.response;

import eu.proxima.payments.enums.OrderStatus;
import eu.proxima.payments.model.generiscsinterface.TransactionGenericsDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResposneDto {
	private long transactionId;
	private OrderStatus state; // Approved,rejected
	private String message;
	private TransactionGenericsDetailDTO transactionGenericsDetail; // Dettagli specifici ib fase di response
}
