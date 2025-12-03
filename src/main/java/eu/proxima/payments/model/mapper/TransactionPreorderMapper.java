package eu.proxima.payments.model.mapper;

import org.springframework.stereotype.Component;

import eu.proxima.payments.enums.PreorderStatus;
import eu.proxima.payments.model.TransactionPreorder;
import eu.proxima.payments.model.exit.TransactionPreorderDTO;
import eu.proxima.payments.model.request.TransferRequestDTO;

@Component
public class TransactionPreorderMapper {

    // Da Entity a DTO
    public TransactionPreorderDTO toDto(TransactionPreorder entity) {
        if (entity == null)
            return null;

        return TransactionPreorderDTO.builder()
                .id(entity.getId())
                .source_iban(entity.getSource_iban())
                .beneficiary_iban(entity.getBeneficiary_iban())
                .beneficiary_name(entity.getBeneficiary_name())
                .amount(entity.getAmount())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .executedAt(entity.getExecutedAt())
                .updateAt(entity.getUpdateAt())
                .retryCount(entity.getRetryCount())
                .build();
    }

    // Da DTO a Entity
    public TransactionPreorder toEntity(TransactionPreorderDTO dto) {
        if (dto == null)
            return null;

        TransactionPreorder entity = new TransactionPreorder();
        entity.setSource_iban(dto.getSource_iban());
        entity.setBeneficiary_iban(dto.getBeneficiary_iban());
        entity.setBeneficiary_name(dto.getBeneficiary_name());
        entity.setAmount(dto.getAmount());
        entity.setMessage(dto.getMessage());
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        } else {
            entity.setStatus(PreorderStatus.PENDING);
        }
        return entity;
    }

    // Da TransferRequestDTO a Entity (usato per creare preorders)
    public TransactionPreorder toEntity(TransferRequestDTO req) {
        if (req == null)
            return null;

        TransactionPreorder entity = new TransactionPreorder();
        entity.setSource_iban(req.getSourceIban());
        entity.setBeneficiary_iban(req.getBeneficiaryIban());
        entity.setBeneficiary_name(req.getBeneficiaryName());
        entity.setAmount(req.getAmount());
        entity.setMessage(req.getMessage());
        entity.setStatus(PreorderStatus.PENDING);
        return entity;
    }
}