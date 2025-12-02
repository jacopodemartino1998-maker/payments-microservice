package eu.proxima.payments.utils;

import java.time.YearMonth;

import eu.proxima.payments.exception.InvalidValidDateException;

public class DateUtils {
	
	private static final String VALID_REGEX = "^(0[1-9]|1[0-2])/[0-9]{2}$";
	 
	 
	public static boolean dataIsValid(String valid) throws InvalidValidDateException {
		 
		
		if(valid == null) {
			 throw new InvalidValidDateException("Data di scadenza non può essere null");
		 }
		 if(!valid.matches(VALID_REGEX)) {
			 throw new InvalidValidDateException("credenziali non valide");
		 }
		 String[] parts = valid.split("/");
	     int month = Integer.parseInt(parts[0]);
	     int year = 2000 + Integer.parseInt(parts[1]); // converte YY → 20YY

	     YearMonth validDate = YearMonth.of(year, month);
	     YearMonth now = YearMonth.now();
	     if(validDate.isBefore(now)) {
	    	 throw new InvalidValidDateException("carta scaduta");
	     }
	     return true;
	        
	        
	 }
}
