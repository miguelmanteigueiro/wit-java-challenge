package com.manteigueiro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculatorService {
    private static final Logger logger = LoggerFactory.getLogger(CalculatorService.class);
    private final KafkaTemplate<String, CalculatorResponseModel> kafkaTemplate;
    private static final int SCALE = 10;

    public CalculatorService(KafkaTemplate<String, CalculatorResponseModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        logger.info("CalculatorService initialized");
    }

    @KafkaListener(topics = "calculate")
    public void calculate(CalculatorRequestModel request) {
        logger.info("Received calculation request - Operation: {}, RequestId: {}",
                request.getOperation(), request.getRequestId());
        logger.debug("Calculation details - OperandA: {}, OperandB: {}",
                request.getOperandA(), request.getOperandB());

        BigDecimal result;

        try {
            result = switch (request.getOperation()) {
                case "sum" -> {
                    logger.debug("Performing addition");
                    yield request.getOperandA().add(request.getOperandB());
                }
                case "subtraction" -> {
                    logger.debug("Performing subtraction");
                    yield request.getOperandA().subtract(request.getOperandB());
                }
                case "multiplication" -> {
                    logger.debug("Performing multiplication");
                    yield request.getOperandA().multiply(request.getOperandB());
                }
                case "division" -> {
                    logger.debug("Performing division with scale {} and rounding mode {}",
                            SCALE, RoundingMode.HALF_UP);
                    yield request.getOperandA().divide(request.getOperandB(), SCALE, RoundingMode.HALF_UP);
                }
                default -> {
                    logger.error("Unsupported operation requested: {}", request.getOperation());
                    throw new IllegalArgumentException("Operation not defined: " + request.getOperation());
                }
            };

            logger.debug("Calculation result: {}", result);
            CalculatorResponseModel answer = new CalculatorResponseModel(
                    request.getOperation(),
                    request.getRequestId(),
                    result.toString(),
                    true
            );

            logger.info("Sending successful response for requestId: {}", request.getRequestId());
            kafkaTemplate.send("calculate-answer", request.getRequestId(), answer);

        } catch (ArithmeticException e) {
            logger.error("Arithmetic error during calculation for requestId: {} - {}",
                    request.getRequestId(), e.getMessage());
            sendErrorResponse(request, "Arithmetic error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid operation for requestId: {} - {}",
                    request.getRequestId(), e.getMessage());
            sendErrorResponse(request, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during calculation for requestId: {} - {}",
                    request.getRequestId(), e.getMessage(), e);
            sendErrorResponse(request, "Unexpected error occurred");
        }
    }

    private void sendErrorResponse(CalculatorRequestModel request, String errorMessage) {
        CalculatorResponseModel answer = new CalculatorResponseModel(
                request.getOperation(),
                request.getRequestId(),
                null,
                false
        );
        logger.info("Sending error response for requestId: {}", request.getRequestId());
        logger.debug("Error details: {}", errorMessage);
        kafkaTemplate.send("calculate-answer", request.getRequestId(), answer);
    }
}