package eu.proxima.payments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
	private long id;
	private String state;
	private String message;

}
