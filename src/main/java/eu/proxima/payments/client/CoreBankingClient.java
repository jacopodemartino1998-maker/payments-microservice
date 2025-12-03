package eu.proxima.payments.client;

import org.springframework.stereotype.Service;

import eu.proxima.payments.model.external.AccountInfoExternalDTO;
import eu.proxima.payments.model.external.OperationResult;

/**
 * Temporary mock implementation of CoreBankingClient.
 * Replace with a @FeignClient implementation when real endpoints are known.
 */
@Service
public class CoreBankingClient {

	public AccountInfoExternalDTO getAccountInfo(String sourceIban) {
		// Simple mock: return a default account info with large balance
		AccountInfoExternalDTO dto = new AccountInfoExternalDTO();
		// dto.setIban(sourceIban);
		dto.setStatus("ACTIVE");
		dto.setAvailableBalance(Double.MAX_VALUE);
		return dto;
	}

	public OperationResult postAccountUpdate(String sourceIban, double amount) {
		// Mock success. In a real client, call core banking API to debit/credit and
		// return an external operation id for reconciliation.
		String op = "op-" + java.util.UUID.randomUUID().toString();
		return new OperationResult(true, op, "COMPLETED");
	}

}
