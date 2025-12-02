package eu.proxima.payments.utils;

import eu.proxima.payments.exception.InvalidCvvException;

public class CvvUtils {
	private static final String CVV_REGEX = "^[0-9]{3,4}$";

    public static boolean cvvIsValid(String cvv) throws InvalidCvvException {

        if (cvv == null) {
            throw new InvalidCvvException("CVV non può essere null");
        }

        if (!cvv.matches(CVV_REGEX)) {
            throw new InvalidCvvException("Formato CVV non valido");
        }

        return true;
    }
}
