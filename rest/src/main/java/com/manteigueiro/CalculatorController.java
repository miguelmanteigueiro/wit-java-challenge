package com.manteigueiro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);
    private final KafkaTemplate<String, CalculatorRequestModel> kafkaTemplate;
    private final Map<String, CompletableFuture<ResponseEntity<String>>> futureRequests;

    public CalculatorController(
            KafkaTemplate<String, CalculatorRequestModel> kafkaTemplate,
            Map<String, CompletableFuture<ResponseEntity<String>>> futureResponses
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.futureRequests = futureResponses;
        logger.info("CalculatorController initialized");
    }

    @GetMapping(value = "/{operation}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<String>> calculate(
            @PathVariable String operation,
            @RequestParam String a,
            @RequestParam String b) {

        logger.info("Received calculation request - Operation: {}, A: {}, B: {}", operation, a, b);

        try {
            BigDecimal numA = new BigDecimal(a);
            BigDecimal numB = new BigDecimal(b);

            CalculatorRequestModel request = new CalculatorRequestModel(numA, numB, operation);
            logger.debug("Created request with requestId: {}", request.getRequestId());
            return sendRequest(request);
        }
        catch (Exception e) {
            logger.error("Error processing calculation request: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private CompletableFuture<ResponseEntity<String>> sendRequest(CalculatorRequestModel request) {
        CompletableFuture<ResponseEntity<String>> futureResponse = new CompletableFuture<>();
        futureRequests.put(request.getRequestId(), futureResponse);
        logger.debug("Sending request to Kafka with requestId: {}", request.getRequestId());
        kafkaTemplate.send("calculate", request.getRequestId(), request);
        return futureResponse;
    }

    @KafkaListener(topics = "calculate-answer")
    public void getAnswerFromQueue(ConsumerRecord<String, CalculatorResponseModel> answer) {
        CalculatorResponseModel response = answer.value();
        logger.debug("Received response from Kafka for requestId: {}", response.getRequestId());

        CompletableFuture<ResponseEntity<String>> future = futureRequests.get(response.getRequestId());

        if (future == null) {
            logger.warn("No future found for requestId: {}. Possibly already processed or timed out.", response.getRequestId());
            return;
        }

        try {
            ResponseEntity<String> responseEntity = createResponse(response);
            future.complete(responseEntity);
            logger.info("Successfully processed response for requestId: {}", response.getRequestId());
        } catch (Exception e) {
            logger.error("Error processing response for requestId: {}", response.getRequestId(), e);
            throw e;
        } finally {
            futureRequests.remove(response.getRequestId());
        }
    }

    private ResponseEntity<String> createResponse(CalculatorResponseModel response) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-ID", response.getRequestId());

        Map<String, String> jsonResponse = new HashMap<>();

        if (response.getSuccessfullyProcessed()) {
            jsonResponse.put("result", response.getResult());
            logger.debug("Creating successful response for requestId: {}", response.getRequestId());
            return createJsonResponse(jsonResponse, HttpStatus.OK, headers);
        } else {
            jsonResponse.put("error", "There was an error within the service");
            logger.warn("Creating error response for requestId: {}", response.getRequestId());
            return createJsonResponse(jsonResponse, HttpStatus.INTERNAL_SERVER_ERROR, headers);
        }
    }

    private ResponseEntity<String> createJsonResponse(Map<String, String> jsonResponse,
                                                      HttpStatus status,
                                                      HttpHeaders headers) {
        try {
            String json = new ObjectMapper().writeValueAsString(jsonResponse);
            return ResponseEntity.status(status)
                    .headers(headers)
                    .body(json);
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON response: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)
                    .body("Error processing response");
        }
    }
}