package com.manteigueiro;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.manteigueiro.kafka.MDCContextModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class CalculatorRequestModel implements Serializable {
    private BigDecimal operandA;
    private BigDecimal operandB;
    private String operation;
    private String requestId;
    private MDCContextModel mdcContext;

    public CalculatorRequestModel() {}

    public CalculatorRequestModel(
            BigDecimal operandA,
            BigDecimal operandB,
            String operation
            ) {
        this.operandA = operandA;
        this.operandB = operandB;
        this.operation = operation;
        this.requestId = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        this.mdcContext = null;
    }

    public CalculatorRequestModel(
            BigDecimal operandA,
            BigDecimal operandB,
            String operation,
            String requestId
            ) {
        this.operandA = operandA;
        this.operandB = operandB;
        this.operation = operation;
        this.requestId = requestId;
        this.mdcContext = null;
    }

    // Getters and Setter
    public BigDecimal getOperandA() { return operandA; }
    public BigDecimal getOperandB() { return operandB; }
    public String getOperation() { return operation; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    // MDC
    @JsonProperty("mdcContext")
    public void setMdcContext(MDCContextModel mdcContext) {
        this.mdcContext = mdcContext;
    }

    @JsonProperty("mdcContext")
    public MDCContextModel getMdcContext() {
        return mdcContext;
    }

    @Override
    public String toString() {
        return "CalculatorModel{" +
                "operandA=" + operandA +
                ", operandB=" + operandB +
                ", operation='" + operation + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }

}