package com.manteigueiro;

import java.io.Serializable;

public class CalculatorAnswerModel implements Serializable {
    private String operation;
    private String requestId;
    private String result;
    private Boolean successfullyProcessed;

    public CalculatorAnswerModel () {}

    public CalculatorAnswerModel (
            String operation,
            String requestId,
            String result,
            Boolean successfullyProcessed
            ) {
        this.operation = operation;
        this.requestId = requestId;
        this.result = result;
        this.successfullyProcessed = successfullyProcessed;
    };


    // Getters and Setters
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public Boolean getSuccessfullyProcessed() { return successfullyProcessed; }
    public void setSuccessfullyProcessed(Boolean successfullyProcessed) { this.successfullyProcessed = successfullyProcessed; }

    @Override
    public String toString() {
        return "CalculatorAnswerModel{" +
                "operation='" + operation + '\'' +
                ", requestId='" + requestId + '\'' +
                ", result='" + result + '\'' +
                ", successfullyProcessed=" + successfullyProcessed +
                '}';
    }
}