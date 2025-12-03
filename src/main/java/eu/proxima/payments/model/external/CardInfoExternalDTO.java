package eu.proxima.payments.model.external;

import eu.proxima.payments.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardInfoExternalDTO {
    private String status; // E.g., VALID, STOLEN
    private CardType cardType; // E.g., DEBIT, PREPAID
    private String linkedIban; // Necessario se carta DEBIT
    private double dailyRemainingLimit; // Per Limiti carta
    private double maxSingleTransactionLimit;
    // private double cardAmount; //se pregata e necessario ricavare dato da
    // l'esterno
}
