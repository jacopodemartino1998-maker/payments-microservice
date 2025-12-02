package eu.proxima.payments.dtos;

import java.sql.Date;

import eu.proxima.payments.enums.CurrencyType;
import eu.proxima.payments.enums.EntryType;
import eu.proxima.payments.enums.TransactionType;
import eu.proxima.payments.model.generiscsinterface.TransactionGenericsDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentRequestDTO {
    private String pan;
    private String cvv;
    private String expiryDate; 
    private double amount;
    private String currency;
    private String merchantId;
}
