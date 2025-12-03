package eu.proxima.payments.model.detail;

import java.time.LocalDate;
import eu.proxima.payments.model.generiscsinterface.TransactionGenericsDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTransactionDetailDTO implements TransactionGenericsDetailDTO { // da allegare ad estratto conto
    private String maskedPan;
    private String beneficiary;
    private LocalDate paymentDate;
}