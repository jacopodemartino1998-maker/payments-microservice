package eu.proxima.payments.service.processor;

import eu.proxima.payments.enums.TransactionType;
import eu.proxima.payments.exception.PaymentProcessingException;
import eu.proxima.payments.model.TransactionPreorder;
import eu.proxima.payments.model.external.OperationResult;

/**
 * Strategy interface for processing different payment types (WIRE_TRANSFER,
 * CARD, INSTANT_TRANSFER).
 * Each implementation handles type-specific validation, orchestration, and
 * ledger creation.
 */
public interface PaymentProcessor {

    /**
     * Process a preorder according to this payment type's logic.
     * 
     * Responsibilities:
     * 1. Validate payment-specific details
     * 2. Call external payment service (CoreBanking, CardService, etc.)
     * 3. Create ledger entries (DEBIT/CREDIT)
     * 4. Return OperationResult
     * 
     * @param preorder The preorder to process
     * @return OperationResult with success status and externalOperationId
     * @throws PaymentProcessingException if processing fails (non-retryable)
     */
    OperationResult process(TransactionPreorder preorder) throws PaymentProcessingException;

    /**
     * @return The TransactionType this processor handles
     */
    TransactionType getTransactionType();
}
