package eu.proxima.payments.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import eu.proxima.payments.model.response.TransactionResposneDto;
import eu.proxima.payments.repositories.TransactionEntityRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentService {

	@Autowired
	private TransactionEntityRepository transactionRep;

	List<TransactionDto> tr = new ArrayList<>(List.of(new TransactionDto(1, "APPROVED", "transazione x approvata"),
			new TransactionDto(2, "EXECUTER", "transazione x eseguita"),
			new TransactionDto(3, "CANCELLED", "transazione x cancellata")));

	public void processPayment() {

	}

	public ResponseEntity<List<TransactionResposneDto>> getAllTransaction() {
		log.info("Esecuzione del pagamento...");
		log.debug("Debug del servizio PaymentService");
		log.warn("Attenzione: simulazione warning dal PaymentService");
		log.error("Errore simulato nel PaymentService");
		return ResponseEntity.ok().body(tr);
	}

	public ResponseEntity<TransactionDto> getTransactionById(int id) {
		return ResponseEntity.ok().body(tr.get(id));
	}

	public ResponseEntity<TransactionDto> createTransaction(
			@RequestBody TransactionCreateRequestDto transactionCreateDto) {

		// Simuliamo la logica di salvataggio
		TransactionDto savedTransaction = new TransactionDto();
		savedTransaction.setId(1L);
		savedTransaction.setAmount(transactionCreateDto.getAmount());
		savedTransaction.setDescription(transactionCreateDto.getDescription());

		// Restituisco il risultato con HTTP 201 Created
		return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
	}

	public ResponseEntity<TransactionDto> modifieTransaction(
			@RequestBody TransactionCreateRequestDto transactionCreateDto, long id) {
		transactionRep.findById(id);

	}

	public static String maskPanSafe(String pan) {
		if (pan == null) {
			return "****";
		}

		// Rimuove eventuali spazi o trattini
		String cleanPan = pan.replaceAll("\\D", "");

		if (cleanPan.length() < 4) {
			return "****";
		}

		int visible = 4;
		int maskLen = cleanPan.length() - visible;

		return "*".repeat(maskLen) + cleanPan.substring(maskLen);
	}

}
