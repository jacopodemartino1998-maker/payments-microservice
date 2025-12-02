package eu.proxima.payments;

public class TransactionDto {
	private long id;
	private String state;
	private String message;

	public TransactionDto() {
		super();
	}

	public TransactionDto(long id, String state, String message) {
		super();
		this.id = id;
		this.state = state;
		this.message = message;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "TransactionDto [id=" + id + ", state=" + state + ", message=" + message + "]";
	}

}
