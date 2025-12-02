package eu.proxima.payments.exception;

public class TransferNotRevocableException extends RuntimeException {
    public TransferNotRevocableException(String message) {
        super(message);
    }
}
