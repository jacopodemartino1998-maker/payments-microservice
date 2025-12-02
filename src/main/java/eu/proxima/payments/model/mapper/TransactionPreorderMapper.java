package eu.proxima.payments.model.mapper;

import org.springframework.stereotype.Component;

import eu.proxima.payments.model.TransactionPreorder;
import eu.proxima.payments.model.exit.TransactionPreorderDTO;

@Component
public class TransactionPreorderMapper {

    // Da Entity a DTO
    public TransactionPreorderDTO toDto(TransactionPreorder entity) {
        if (entity == null) return null;
        
        return TransactionPreorderDTO.builder()
                .id(entity.getId())
                .beneficiary_iban(entity.getBeneficiary_iban())
                .amount(entity.getAmount())
                .message(entity.getMessage())
                .executedAt(entity.getExecutedAt())
                .updateAt(entity.getUpdateAt())
                .build();
    }

    // Da DTO a Entity
    public TransactionPreorder toEntity(TransactionPreorderDTO dto) {
        if (dto == null) return null;

        TransactionPreorder entity = new TransactionPreorder();
        // ID non viene settato qui solitamente per le create, ma gestito dal DB
        entity.setBeneficiary_iban(dto.getBeneficiary_iban());
        entity.setAmount(dto.getAmount());
        entity.setMessage(dto.getMessage());
        // executedAt e updateAt sono gestiti dal DB o dal Service
        return entity;
    }
}