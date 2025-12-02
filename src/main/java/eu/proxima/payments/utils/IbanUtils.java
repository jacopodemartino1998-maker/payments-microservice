package eu.proxima.payments.utils;

import java.util.Map;

import eu.proxima.payments.exception.InvalidIbanException;

public class IbanUtils {
	private static final String IBAN_BASIC_REGEX = "^[A-Z]{2}\\d{2}[A-Z0-9]{10,30}$";
	private static final Map<String, Integer> IBAN_LENGTHS = Map.ofEntries(
	        Map.entry("IT", 27),
	        Map.entry("FR", 27),
	        Map.entry("DE", 22),
	        Map.entry("ES", 24),
	        Map.entry("NL", 18),
	        Map.entry("BE", 16),
	        Map.entry("GB", 22),
	        Map.entry("PT", 25),
	        Map.entry("IE", 22),
	        Map.entry("CH", 21),
	        Map.entry("PL", 28),
	        Map.entry("AT", 20));

	public static boolean ibanIsValid(String iban) throws InvalidIbanException {
		
		
		if(iban == null) {
			return false;
		}
		
		iban = iban.replaceAll("\\s+", "").toUpperCase();
		
		if(!iban.matches(IBAN_BASIC_REGEX)) {
			return false;
		}
		
		 String country = iban.substring(0, 2);
		 Integer expectedLength = IBAN_LENGTHS.get(country);
		 
		 if (expectedLength == null) {
			    throw new InvalidIbanException("Paese IBAN non supportato ");
			}

			if (iban.length() != expectedLength) {
			    throw new InvalidIbanException("Lunghezza IBAN non valida per il paese " + country);
			}

			if (!iban.matches(IBAN_BASIC_REGEX)) {
			    throw new InvalidIbanException("Formato IBAN non valido ");
			}

		
		 return iban.length() == expectedLength;	
	}
	public static String CleanIban(String iban) {
		return iban.toUpperCase().replaceAll("\\s+", "");
		
	}
}

