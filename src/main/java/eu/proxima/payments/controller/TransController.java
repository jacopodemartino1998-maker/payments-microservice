package eu.proxima.payments.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import eu.proxima.payments.model.exit.TransactionPreorderDTO;
import eu.proxima.payments.service.TransactionPreorderService;

@RestController
@RequestMapping("transaction/v1")
public class TransController {
	@Autowired
	TransactionPreorderService preorderService;

	@GetMapping("/transactions")
	public List<TransactionPreorderDTO> getAllTransaction() {
		return preorderService.getAllPreorders();
	}
}
