package eu.proxima.payments.model.detail;

import eu.proxima.payments.model.generiscsinterface.TransactionGenericsDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTransactionDetailDTO implements TransactionGenericsDetailDTO {
 private String maskedPan;
 private String merchantId;
 private String externalAuthId; // Riferimento esterno dell'autorizzazione
 
 
 


}