package eu.proxima.payments.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    @NotBlank
    private String sourceIban;

    @NotBlank
    private String beneficiaryIban;

    @NotBlank
    private String beneficiaryName;

    @Positive
    private double amount;

    @Size(max = 512)
    private String message;

    private boolean isInstant;
}