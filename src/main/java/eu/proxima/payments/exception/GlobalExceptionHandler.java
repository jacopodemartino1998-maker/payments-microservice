package eu.proxima.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFound(AccountNotFoundException ex) {
        log.warn("Fail Request : Accaunt not found " + ex.getMessage());
        log.warn("Richiesta Fallita : Accaunt non trovato " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<String> handleInsufficientFunds(InsufficientFundsException ex) {
        log.warn("Fail Request : insufficent founds " + ex.getMessage());
        log.warn("Richiesta Fallita : fondi insufficenti  " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransferNotRevocableException.class)
    public ResponseEntity<String> handleInsufficientFunds(TransferNotRevocableException ex) {
        log.warn("Fail Request : Transfer not revocable " + ex.getMessage());
        log.warn("Richiesta Fallita : fondi insufficenti  " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.warn("Fail Request : " + ex.getMessage());
        log.warn("Richiesta Fallita : " + ex.getMessage());
        String messaggioSicuro = "Si è verificato un errore interno. Contattare il supporto.";
        return new ResponseEntity<>(messaggioSicuro, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidIbanException.class)
    public ResponseEntity<String> handleInvalidIban(InvalidIbanException ex) {
        log.warn("Fail Request : validation failed " + ex.getMessage());
        log.warn("Richiesta Fallita : validazione fallita " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<String> handlerInvalidAmount(InvalidAmountException ex) {
        log.warn("Fail Request : validation failed " + ex.getMessage());
        log.warn("Richiesta Fallita : validazione fallita " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCvvException.class)
    public ResponseEntity<String> handlerInvalidCvv(InvalidCvvException ex) {
        log.warn("Fail Request : validation failed " + ex.getMessage());
        log.warn("Richiesta Fallita : validazione fallita " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPanException.class)
    public ResponseEntity<String> handlerInvalidPan(InvalidPanException ex) {
        log.warn("Fail Request : validation failed " + ex.getMessage());
        log.warn("Richiesta Fallita : validazione fallita " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidValidDateException.class)
    public ResponseEntity<String> handlerInvalidDate(InvalidValidDateException ex) {
        log.warn("Fail Request : validation failed " + ex.getMessage());
        log.warn("Richiesta Fallita : validazione fallita " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<String> paymentProcessingFail(PaymentProcessingException ex) {
        log.warn("Fail Request : payment processing failed " + ex.getMessage());
        log.warn("Richiesta Fallita : processamento fallito " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}
