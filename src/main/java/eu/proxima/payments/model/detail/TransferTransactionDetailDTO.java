package eu.proxima.payments.model.detail;

import java.time.LocalDate;

import eu.proxima.payments.model.generiscsinterface.TransactionGenericsDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferTransactionDetailDTO implements TransactionGenericsDetailDTO {
    private String sourceIban;
    private String beneficiaryIban;
    private String beneficiaryName;
    private LocalDate transactionDate;
}