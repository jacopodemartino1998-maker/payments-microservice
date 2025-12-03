package eu.proxima.payments.model.exit;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import eu.proxima.payments.enums.PreorderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionPreorderDTO {
	private long id;

	private String source_iban;

	@NotBlank
	private String beneficiary_iban;

	private String beneficiary_name;

	@Positive
	private double amount;

	private String message;

	private PreorderStatus status;

	private LocalDateTime executedAt;

	private LocalDateTime updateAt;

	private int retryCount;

}
