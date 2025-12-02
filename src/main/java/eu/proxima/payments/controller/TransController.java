package eu.proxima.payments.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.proxima.payments.model.response.TransactionResposneDto;
import eu.proxima.payments.service.PaymentService;

@RestController
@RequestMapping("transaction/v1")
public class TransController {
	@Autowired
	PaymentService ps ;

	@GetMapping("/transactions")
	public ResponseEntity<List<TransactionResposneDto>> getAllTransaction() {
		return ps.getAllTransaction();
	}
}
