package eu.proxima.payments.model.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoExternalDTO {
    private String status;           // E.g., ACTIVE, CLOSED
    private double availableBalance; 

}