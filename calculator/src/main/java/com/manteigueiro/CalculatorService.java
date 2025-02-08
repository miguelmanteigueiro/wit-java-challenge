package com.manteigueiro;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculatorService {
    private final KafkaTemplate<String, CalculatorAnswerModel> kafkaTemplate;
    private static final int SCALE = 10;

    public CalculatorService(KafkaTemplate<String, CalculatorAnswerModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "calculate")
    public void calculate(CalculatorModel request) {
        BigDecimal result;

        try {
            result = switch (request.getOperation()) {
                case "sum" ->
                        request.getOperandA().add(request.getOperandB());
                case "subtraction" ->
                        request.getOperandA().subtract(request.getOperandB());
                case "multiplication" ->
                        request.getOperandA().multiply(request.getOperandB());
                case "division" ->
                        request.getOperandA().divide(request.getOperandB(), SCALE, RoundingMode.HALF_UP);
                default ->
                        throw new IllegalArgumentException("Operation not defined: " + request.getOperation());
            };

            CalculatorAnswerModel answer = new CalculatorAnswerModel(request.getOperation(), request.getRequestId(), result.toString(), true);
            kafkaTemplate.send("calculate-answer", request.getRequestId(), answer);

        } catch (Exception e) {
            CalculatorAnswerModel answer = new CalculatorAnswerModel(request.getOperation(), request.getRequestId(), null, false);
            kafkaTemplate.send("calculate-answer", request.getRequestId(), answer);
        }
    }
}