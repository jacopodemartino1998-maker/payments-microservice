package eu.proxima.payments.model.request;

import eu.proxima.payments.enums.CardType;
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
    private CardType cardType; // DEBIT, PREPAID
    private double internalAmount; // if prepaid
    private String associateIban; // if debit
    // private CurrencyType currency;
    private String beneficiaryIban;
}
