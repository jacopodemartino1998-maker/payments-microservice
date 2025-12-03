package eu.proxima.payments.model.mapper;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import eu.proxima.payments.model.detail.CardTransactionDetailDTO;
import eu.proxima.payments.model.detail.TransferTransactionDetailDTO;
import eu.proxima.payments.model.request.CardPaymentRequestDTO;
import eu.proxima.payments.model.request.TransferRequestDTO;

@Component
public class PaymentMapper {

    /**
     * Crea i dettagli specifici per un pagamento CARTA.
     * Include la logica di mascheramento del PAN.
     */
    public CardTransactionDetailDTO mapToCardDetails(CardPaymentRequestDTO request) {
        CardTransactionDetailDTO detail = new CardTransactionDetailDTO();
        detail.setMaskedPan(maskPan(request.getPan()));
        detail.setBeneficiary(request.getBeneficiaryIban());// Assumiamo che il beneficiario sia il merchant
        detail.setPaymentDate(LocalDate.now());
        return detail;
    }

    /**
     * Crea i dettagli specifici per un BONIFICO.
     */
    public TransferTransactionDetailDTO mapToTransferDetails(TransferRequestDTO request, String beneficiaryName) {
        TransferTransactionDetailDTO detail = new TransferTransactionDetailDTO();
        detail.setSourceIban(request.getSourceIban());
        detail.setBeneficiaryIban(request.getBeneficiaryIban());
        detail.setBeneficiaryName(beneficiaryName); // Solitamente recuperato dal Core Banking o dal Context utente
        detail.setTransactionDate(LocalDate.now());
        return detail;
    }

    // --- Helper Methods ---

    private String maskPan(String pan) {
        if (pan == null || pan.length() < 4)
            return "****";
        return "**** **** **** " + pan.substring(pan.length() - 4);
    }
}