package com.manteigueiro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/")
public class CalculatorController {

    private final KafkaTemplate<String, CalculatorModel> kafkaTemplate;
    private final Map<String, CompletableFuture<ResponseEntity<String>>> futureRequests;

    public CalculatorController(
            KafkaTemplate<String, CalculatorModel> kafkaTemplate,
            Map<String, CompletableFuture<ResponseEntity<String>>> futureResponses
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.futureRequests = futureResponses;
    }

    @GetMapping(value = "/{operation}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<String>> calculate(
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

    private CompletableFuture<ResponseEntity<String>> sendRequest(CalculatorModel request) {
        CompletableFuture<ResponseEntity<String>> futureResponse = new CompletableFuture<>();
        futureRequests.put(request.getRequestId(), futureResponse);
        kafkaTemplate.send("calculate", request.getRequestId(), request);
        return futureResponse;
    }

    @KafkaListener(topics = "calculate-answer")
    public void getAnswerFromQueue(ConsumerRecord<String, CalculatorAnswerModel> answer) {
        CalculatorAnswerModel response = answer.value();
        CompletableFuture<ResponseEntity<String>> future = futureRequests.get(response.getRequestId());

        // Add the X-Request-ID header to the response
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-ID", response.getRequestId());

        // Create the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> jsonKeyVal = new HashMap<>();
        jsonKeyVal.put("result", response.getResult());

        try {
            String json = objectMapper.writeValueAsString(jsonKeyVal);
            ResponseEntity<String> responseEntity = ResponseEntity.ok().headers(headers).body(json);
            if (future != null) {
                future.complete(responseEntity);
                futureRequests.remove(response.getRequestId());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}