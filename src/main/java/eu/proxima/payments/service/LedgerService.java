package eu.proxima.payments.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import eu.proxima.payments.model.LedgerEntity;
import eu.proxima.payments.model.exit.LedgerEntityDTO;
import eu.proxima.payments.model.mapper.LedgerMapper;
import eu.proxima.payments.repositories.LedgerRepository;

@Service
public class LedgerService {

    @Autowired
    private LedgerRepository ledgerRepository;

    public Page<LedgerEntityDTO> list(Pageable pageable, String transactionType, String accountIban,
            String externalOperationId) {
        // simple implementation: support externalOperationId filter and pagination
        if (externalOperationId != null && !externalOperationId.isBlank()) {
            var list = ledgerRepository.findAll().stream()
                    .filter(l -> externalOperationId.equals(l.getExternalOperationId()))
                    .map(LedgerMapper::toDto)
                    .toList();
            return new PageImpl<>(list, pageable, list.size());
        }

        // fallback: return paged all (no heavy filtering implemented yet)
        var page = ledgerRepository.findAll(pageable);
        return page.map(LedgerMapper::toDto);
    }

    public Optional<LedgerEntityDTO> findById(long id) {
        return ledgerRepository.findById(id).map(LedgerMapper::toDto);
    }

    public Optional<LedgerEntityDTO> findByExternalId(String externalId) {
        return ledgerRepository.findAll().stream().filter(l -> externalId.equals(l.getExternalOperationId()))
                .findFirst().map(LedgerMapper::toDto);
    }

    public LedgerEntityDTO create(LedgerEntityDTO dto) {
        LedgerEntity entity = LedgerMapper.toEntity(dto);
        LedgerEntity saved = ledgerRepository.save(entity);
        return LedgerMapper.toDto(saved);
    }
}
