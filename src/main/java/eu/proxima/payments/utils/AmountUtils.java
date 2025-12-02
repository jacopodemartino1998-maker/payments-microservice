package eu.proxima.payments.utils;

import eu.proxima.payments.exception.InvalidAmountException;

public class AmountUtils {
	 public static boolean amountIsValid(Double amount) throws InvalidAmountException {

	        if (amount == null) {
	            throw new InvalidAmountException("L'importo non può essere null");
	        }

	        if (amount <= 0) {
	            throw new InvalidAmountException("L'importo deve essere maggiore di zero");
	        }

	        if (amount > 1000000) {
	            throw new InvalidAmountException("L'importo supera il limite consentito");
	        }

	        return true;
	    }
}
