package eu.proxima.payments.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    private String sourceIban;
    private String beneficiaryIban;
    private String beneficiryName;
    private double amount;
    private String message;
    private boolean isInstant;
}