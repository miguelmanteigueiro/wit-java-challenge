package com.manteigueiro;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/")
public class CalculatorController {
    private final KafkaTemplate<String, CalculatorModel> kafkaTemplate;

    public CalculatorController(KafkaTemplate<String, CalculatorModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping(value = "/{operation}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> calculate(
            @PathVariable String operation,
            @RequestParam String a,
            @RequestParam String b) {

        try {
            BigDecimal numA = new BigDecimal(a);
            BigDecimal numB = new BigDecimal(b);

            CalculatorModel request = new CalculatorModel(numA, numB, operation);
            return sendRequest(request);
        }
        catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private CompletableFuture<String> sendRequest(CalculatorModel request) {
        CompletableFuture<String> futureResponse = new CompletableFuture<>();
        kafkaTemplate.send("calculate", request.getRequestId(), request);
        return futureResponse;
    }

    @KafkaListener(topics = "calculate-answer")
    public void getAnswerFromQueue(ConsumerRecord<String, CalculatorAnswerModel> answer) {
        CalculatorAnswerModel response = answer.value();
        System.out.println("Received response: " + response);

    }
}