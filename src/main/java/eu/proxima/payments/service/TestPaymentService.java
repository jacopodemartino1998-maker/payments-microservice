package eu.proxima.payments.service;

import org.springframework.stereotype.Service;

import eu.proxima.payments.client.CoreBankingClient;
import eu.proxima.payments.model.external.AccountInfoExternalDTO;
import eu.proxima.payments.model.mapper.PaymentMapper;
import eu.proxima.payments.model.request.TransferRequestDTO;
import eu.proxima.payments.model.response.PaymentExecutionResponseDTO;
import eu.proxima.payments.model.util.PaymentResponseUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Lombok per iniezione dipendenze
public class TestPaymentService {
 
    private final PaymentMapper paymentMapper;
    private final CoreBankingClient coreBankingClient; // Ipotetico Feign Client
 
    public PaymentExecutionResponseDTO processTransfer(TransferRequestDTO request) {
        // 1. Chiamata DTO Esterni (Core Banking)
        AccountInfoExternalDTO accountInfo = coreBankingClient.getAccountInfo(request.getSourceIban());
 
        // 2. Validazione Logica (Esempio: Saldo insufficiente)
        if (accountInfo.getAvailableBalance() < request.getAmount()) {
            return PaymentResponseUtil.buildRejectedResponse(
                request.getSourceIban(),
                request.getAmount(),
                "INSUFFICIENT_FUNDS"
            );
        }
 
        // 3. Logica di Esecuzione (omessa per brevità...)
        // ... bonifico eseguito ...
 
        // 4. Mapping dei dettagli specifici
        // (Il nome del sender potrebbe venire dall'accountInfo recuperato prima)
        var transferDetails = paymentMapper.mapToTransferDetails(request, "Mario Rossi");
 
        // 5. Costruzione Risposta di Successo
        return PaymentResponseUtil.buildSuccessResponse(
            request.getSourceIban(),
            request.getAmount(),
            transferDetails
        );
    }
}