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
public class TransferRequestDTO {
    private String sourceIban;
    private String beneficiaryIban; 
    private double amount;
    private String message;
    private boolean isInstant; 
}