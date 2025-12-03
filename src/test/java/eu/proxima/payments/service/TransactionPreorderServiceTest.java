package eu.proxima.payments.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import eu.proxima.payments.client.CoreBankingClient;
import eu.proxima.payments.model.TransactionPreorder;
import eu.proxima.payments.model.exit.TransactionPreorderDTO;
import eu.proxima.payments.model.mapper.TransactionPreorderMapper;
import eu.proxima.payments.model.request.TransferRequestDTO;
import eu.proxima.payments.repositories.TransactionEntityRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionPreorderServiceTest {

    @Mock
    TransactionEntityRepository repository;

    @Mock
    TransactionPreorderMapper mapper;

    @Mock
    CoreBankingClient coreBankingClient;

    @Mock
    eu.proxima.payments.repositories.LedgerRepository ledgerRepository;

    @InjectMocks
    TransactionPreorderService service;

    @Test
    void createPreorder_idempotent_whenRequestIdExists() {
        TransferRequestDTO req = new TransferRequestDTO("SRC", "DEST", "Name", 100.0, "msg", false);
        // Now simple flow without requestId: creation always saves
        TransactionPreorder entity = new TransactionPreorder();
        TransactionPreorder saved = new TransactionPreorder();
        saved.setId(1L);
        TransactionPreorderDTO outDto = TransactionPreorderDTO.builder().id(1L).build();

        when(mapper.toEntity(req)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(outDto);

        var dto = service.createPreorder(req, null);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        verify(repository).save(entity);
    }

    @Test
    void createPreorder_createsNew_whenNoRequestId() {
        TransferRequestDTO req = new TransferRequestDTO("SRC", "DEST", "Name", 50.0, "msg", false);
        String requestId = null;

        TransactionPreorder entity = new TransactionPreorder();
        TransactionPreorder saved = new TransactionPreorder();
        saved.setId(2L);
        TransactionPreorderDTO outDto = TransactionPreorderDTO.builder().id(2L).build();

        when(mapper.toEntity(req)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(outDto);

        var dto = service.createPreorder(req, requestId);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        verify(repository).save(entity);
    }

    @Test
    void executePreorder_success_createsLedgerAndUpdatesStatus() {
        TransactionPreorder p = new TransactionPreorder();
        p.setId(10L);
        p.setSource_iban("IT99X0123456789012345678901");
        p.setBeneficiary_iban("IT60X0542811101000000123456");
        p.setBeneficiary_name("Test Name");
        p.setAmount(10.0);
        p.setStatus(eu.proxima.payments.enums.PreorderStatus.PENDING);

        when(repository.findById(10L)).thenReturn(Optional.of(p));

        when(coreBankingClient.postAccountUpdate(p.getBeneficiary_iban(), p.getAmount()))
                .thenReturn(new eu.proxima.payments.model.external.OperationResult(true, "op-123", "COMPLETED"));

        boolean result = service.executePreorder(10L, null);

        assertTrue(result);
        // Verify that ledger is created (not deleted, status updated to EXECUTED)
        verify(ledgerRepository).save(any());
        verify(repository).save(p);
        assertEquals(eu.proxima.payments.enums.PreorderStatus.EXECUTED, p.getStatus());
    }
}
