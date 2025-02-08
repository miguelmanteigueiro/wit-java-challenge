package com.manteigueiro;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculatorService {
    private final KafkaTemplate<String, CalculatorModel> kafkaTemplate;
    private static final int SCALE = 10;

    public CalculatorService(KafkaTemplate<String, CalculatorModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "calculate")
    public void calculate(CalculatorModel request) {
        BigDecimal result;

        System.out.println("\n\n\n\n------------------------- Received request: " + request);
        System.out.println("------------------------- Received request: " + request.getOperandA());
        System.out.println("------------------------- Received request: " + request.getOperandB());
        System.out.println("------------------------- Received request: " + request.getOperation());

        try {
            result = switch (request.getOperation()) {
                case "sum" ->
                        request.getOperandA().add(request.getOperandB());
                case "subtract" ->
                        request.getOperandA().subtract(request.getOperandB());
                case "multiply" ->
                        request.getOperandA().multiply(request.getOperandB());
                case "divide" ->
                        request.getOperandA().divide(request.getOperandB(), SCALE, RoundingMode.HALF_UP);
                default ->
                        throw new IllegalArgumentException("Operation not defined: " + request.getOperation());
            };
            System.out.println("------------------------- result: " + result + "\n\n\n\n");
            //kafkaTemplate.send("calculate", request.getRequestId(), new CalculatorModel(BigDecimal.ONE, BigDecimal.ONE, "AB"));
        } catch (Exception e) {
            System.out.println("Err: " + e.getMessage());
            //kafkaTemplate.send("calculate", request.getRequestId(), new CalculatorModel(BigDecimal.ONE, BigDecimal.ONE, "XX"));
        }
    }
}