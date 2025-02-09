package com.manteigueiro;

import java.io.Serializable;

public class CalculatorResponseModel implements Serializable {
    private String operation;
    private String requestId;
    private String result;
    private Boolean successfullyProcessed;

    public CalculatorResponseModel() {}

    public CalculatorResponseModel(
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


    // Getters only
    public String getOperation() { return operation; }
    public String getRequestId() { return requestId; }
    public String getResult() { return result; }
    public Boolean getSuccessfullyProcessed() { return successfullyProcessed; }

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