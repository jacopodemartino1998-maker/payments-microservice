package eu.proxima.payments.model.request;

import eu.proxima.payments.enums.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentRequestDTO {
    @NotBlank(message = "PAN cannot be blank")
    @Size(min = 13, max = 19, message = "PAN must be between 13 and 19 digits")
    private String pan;

    @NotBlank(message = "CVV cannot be blank")
    @Size(min = 3, max = 4, message = "CVV must be 3-4 digits")
    private String cvv;

    @NotBlank(message = "Expiry date cannot be blank")
    @Size(min = 5, max = 5, message = "Expiry date must be in MM/YY format")
    private String expiryDate;

    @Positive(message = "Amount must be positive")
    private double amount;

    @NotNull(message = "Card type cannot be null")
    private CardType cardType; // DEBIT, PREPAID

    private double internalAmount; // if PREPAID

    @NotBlank(message = "Associate IBAN cannot be blank for DEBIT cards")
    private String associateIban; // if DEBIT

    @NotBlank(message = "Beneficiary IBAN cannot be blank")
    @Size(min = 15, max = 34, message = "IBAN must be between 15 and 34 characters")
    private String beneficiaryIban;
}