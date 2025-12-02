package eu.proxima.payments.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResposneDto {
	private long id;
	private String state;
	private String message;

}
