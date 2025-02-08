package com.manteigueiro;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class CalculatorModel implements Serializable {
    private BigDecimal operandA;
    private BigDecimal operandB;
    private String operation;
    private String requestId;

    public CalculatorModel() {}

    public CalculatorModel(
            BigDecimal operandA,
            BigDecimal operandB,
            String operation
            ) {
        this.operandA = operandA;
        this.operandB = operandB;
        this.operation = operation;
        this.requestId = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
    }

    // Getters only
    public BigDecimal getOperandA() { return operandA; }
    public BigDecimal getOperandB() { return operandB; }
    public String getOperation() { return operation; }
    public String getRequestId() { return requestId; }

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