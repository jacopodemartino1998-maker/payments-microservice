package eu.proxima.payments.model.util;

import java.util.UUID;

import eu.proxima.payments.enums.OrderStatus;
import eu.proxima.payments.model.generiscsinterface.TransactionGenericsDetailDTO;
import eu.proxima.payments.model.response.PaymentExecutionResponseDTO;

public class PaymentResponseUtil {

    public static final OrderStatus STATUS_EXECUTED = OrderStatus.AUTHORIZED;
    public static final OrderStatus STATUS_REJECTED = OrderStatus.REJECTED;

    /**
     * Costruisce una risposta di SUCCESSO.
     */
    public static PaymentExecutionResponseDTO buildSuccessResponse(
            String sourceId,
            double amount,
            TransactionGenericsDetailDTO details) {
        return PaymentExecutionResponseDTO.builder()
                .status(STATUS_EXECUTED)
                .transactionId(UUID.randomUUID().toString()) // Genera un ID univoco per la transazione
                .sourceId(sourceId)
                .amount(amount)
                .transactionGenericsDetail(details)
                .rejectionReasonCode(null) // Nessun errore
                .build();
    }

    /**
     * Costruisce una risposta di ERRORE (Rejected).
     */
    public static PaymentExecutionResponseDTO buildRejectedResponse(
            String sourceId,
            double amount,
            String reasonCode) {
        return PaymentExecutionResponseDTO.builder()
                .status(STATUS_REJECTED)
                .transactionId(UUID.randomUUID().toString()) // Tracciamo anche i fallimenti
                .sourceId(sourceId)
                .amount(amount)
                .transactionGenericsDetail(null) // Spesso nei rifiuti non serve il dettaglio completo
                .rejectionReasonCode(reasonCode)
                .build();
    }
}
