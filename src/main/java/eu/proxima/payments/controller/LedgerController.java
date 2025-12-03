package eu.proxima.payments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.proxima.payments.model.exit.LedgerEntityDTO;
import eu.proxima.payments.service.LedgerService;

@RestController
@RequestMapping("/ledger/v1")
public class LedgerController {

    @Autowired
    private LedgerService ledgerService;

    @GetMapping
    public Page<LedgerEntityDTO> list(
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "accountIban", required = false) String accountIban,
            @RequestParam(value = "externalOperationId", required = false) String externalOperationId,
            Pageable pageable) {
        return ledgerService.list(pageable, transactionType, accountIban, externalOperationId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LedgerEntityDTO> getById(@PathVariable long id) {
        return ledgerService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/external/{operationId}")
    public ResponseEntity<LedgerEntityDTO> getByExternal(@PathVariable String operationId) {
        return ledgerService.findByExternalId(operationId).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LedgerEntityDTO> create(@RequestBody LedgerEntityDTO dto) {
        LedgerEntityDTO created = ledgerService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
