package eu.proxima.payments.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import eu.proxima.payments.model.exit.TransactionPreorderDTO;
import eu.proxima.payments.model.request.TransferRequestDTO;
import eu.proxima.payments.service.TransactionPreorderService;

@RestController
@RequestMapping("transaction/v1")
public class TransController {
	@Autowired
	TransactionPreorderService preorderService;

	@GetMapping("/preorders")
	public org.springframework.data.domain.Page<TransactionPreorderDTO> getAllPreorders(
			@org.springframework.web.bind.annotation.RequestParam(value = "status", required = false) eu.proxima.payments.enums.PreorderStatus status,
			@org.springframework.web.bind.annotation.RequestParam(value = "beneficiaryIban", required = false) String beneficiaryIban,
			@org.springframework.web.bind.annotation.RequestParam(value = "createdFrom", required = false) String createdFrom,
			@org.springframework.web.bind.annotation.RequestParam(value = "createdTo", required = false) String createdTo,
			org.springframework.data.domain.Pageable pageable) {
		return preorderService.getAllPreorders(status, beneficiaryIban, createdFrom, createdTo, pageable);
	}

	@PostMapping("/preorders")
	public TransactionPreorderDTO createPreorder(@Valid @RequestBody TransferRequestDTO dto) {
		return preorderService.createPreorder(dto, null);
	}

	@PutMapping("/preorders/{id}")
	public TransactionPreorderDTO updatePreorder(@PathVariable long id,
			@Valid @RequestBody TransactionPreorderDTO dto) {
		return preorderService.updatePreorder(id, dto);
	}

	@DeleteMapping("/preorders/{id}")
	public ResponseEntity<Void> deletePreorder(@PathVariable long id) {
		preorderService.deletePreorder(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/preorders/{id}/execute")
	public ResponseEntity<Void> executePreorder(@PathVariable long id) {
		boolean ok = preorderService.executePreorder(id, null);
		return ok ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
}
