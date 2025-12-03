package eu.proxima.payments.model.util;

import eu.proxima.payments.enums.OrderStatus;
import eu.proxima.payments.model.generiscsinterface.TransactionGenericsDetailDTO;
import eu.proxima.payments.model.response.TransactionResposneDto;

public class TransactionResponseUtil {

    // Assumendo che OrderStatus contenga anche lo stato 'APPROVED'
    // Per un bonifico, lo stato iniziale potrebbe essere PENDING o
    // APPROVED/EXECUTED
    public static final OrderStatus STATUS_APPROVED = OrderStatus.AUTHORIZED;
    public static final OrderStatus STATUS_REJECTED = OrderStatus.REJECTED;

    /**
     * Costruisce una risposta di SUCCESS per un bonifico.
     * Si noti che i bonifici spesso usano 'message' invece di 'transactionId'.
     */
    public static TransactionResposneDto buildSuccessResponse(
            long transactionId,
            String successMessage,
            TransactionGenericsDetailDTO details) {

        // Assumo che TransactionResposneDto non abbia il Builder di Lombok,
        // quindi lo istanziamo manualmente.
        TransactionResposneDto response = new TransactionResposneDto();

        response.setTransactionId(transactionId); // ID univoco dal database
        response.setState(STATUS_APPROVED);
        response.setMessage(successMessage);
        response.setTransactionGenericsDetail(details);

        return response;
    }

    /**
     * Costruisce una risposta di ERRORE (Rejected) per un bonifico.
     */
    public static TransactionResposneDto buildRejectedResponse(
            long transactionId,
            String rejectionMessage) {

        TransactionResposneDto response = new TransactionResposneDto();

        // Usiamo un ID fittizio o l'ID della richiesta se il DB non ha generato nulla.
        // Se non è mai arrivata al DB, si potrebbe usare id=0 o un valore negativo.
        response.setTransactionId(transactionId);
        response.setState(STATUS_REJECTED);
        response.setMessage(rejectionMessage);
        response.setTransactionGenericsDetail(null);

        return response;
    }
}