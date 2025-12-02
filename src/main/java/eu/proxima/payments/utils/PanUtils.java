package eu.proxima.payments.utils;

import eu.proxima.payments.exception.InvalidPanException;

public class PanUtils {
	private static final String PAN_CLEAN_REGEX = "[\\s-]";
    private static final String PAN_REGEX = "^[0-9]{13,19}$";

    public static boolean panIsValid(String pan) throws InvalidPanException {

        if (pan == null) {
            throw new InvalidPanException("PAN non può essere null");
        }

        // Rimuove spazi e trattini
        pan = pan.replaceAll(PAN_CLEAN_REGEX, "");

        if (!pan.matches(PAN_REGEX)) {
            throw new InvalidPanException("Formato PAN non valido");
        }
        return true;
    }
}
