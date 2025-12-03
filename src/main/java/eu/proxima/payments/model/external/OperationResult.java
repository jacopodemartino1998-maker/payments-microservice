package eu.proxima.payments.model.external;

public class OperationResult {
    private boolean success;
    private String operationId;
    private String status;

    public OperationResult() {
    }

    public OperationResult(boolean success, String operationId, String status) {
        this.success = success;
        this.operationId = operationId;
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
