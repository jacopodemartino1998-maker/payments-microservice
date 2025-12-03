package eu.proxima.payments.model.mapper;

import eu.proxima.payments.model.LedgerEntity;
import eu.proxima.payments.model.exit.LedgerEntityDTO;

public class LedgerMapper {

    /**
     * Converte un oggetto LedgerEntity in LedgerEntityDTO.
     */
    public static LedgerEntityDTO toDto(LedgerEntity entity) {
        if (entity == null) {
            return null;
        }

        LedgerEntityDTO dto = new LedgerEntityDTO();

        // Mapping di tutti i campi
        dto.setId(entity.getId());
        dto.setTransactionType(entity.getTransactionType());
        dto.setAmount(entity.getAmount());
        dto.setCurrencyType(entity.getCurrencyType());
        dto.setExecutedAt(entity.getExecutedAt());
        dto.setEntryType(entity.getEntryType());
        dto.setTrDetail(entity.getTrDetail());

        return dto;
    }

    /**
     * Converte un oggetto LedgerEntityDTO in LedgerEntity.
     */
    public static LedgerEntity toEntity(LedgerEntityDTO dto) {
        if (dto == null) {
            return null;
        }

        LedgerEntity entity = new LedgerEntity();

        // Mapping di tutti i campi
        // Nota: i campi gestiti come @CreationTimestamp (executedAt)
        // e @GeneratedValue (id) dovrebbero essere ignorati per le nuove Entity,
        // ma sono inclusi per l'uso in aggiornamenti o per Entity complete.
        entity.setId(dto.getId());
        entity.setTransactionType(dto.getTransactionType());
        entity.setAmount(dto.getAmount());
        entity.setCurrencyType(dto.getCurrencyType());
        entity.setExecutedAt(dto.getExecutedAt());
        entity.setEntryType(dto.getEntryType());
        entity.setTrDetail(dto.getTrDetail());

        return entity;
    }
}