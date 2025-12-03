package eu.proxima.payments.model.response;

import eu.proxima.payments.enums.OrderStatus;
import eu.proxima.payments.model.generiscsinterface.TransactionGenericsDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentExecutionResponseDTO {
    private OrderStatus status; // REJECTED APPROVED
    private String transactionId;
    private String sourceId; // IBAN o PAN
    private double amount;
    private String rejectionReasonCode; // Se REJECTED
    private TransactionGenericsDetailDTO transactionGenericsDetail; // Dettagli specifici ib fase di response
}