package eu.proxima.payments.model.exit;

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
public class LedgerEntityDTO {
	private long id;
	private TransactionType transactionType;
	private double amount;
	private CurrencyType currencyType;
	private Date executedAt;
	private EntryType entryType;//Debit / Credi
	private TransactionGenericsDetailDTO trDetail;
	
}
