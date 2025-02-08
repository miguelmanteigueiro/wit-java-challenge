package com.manteigueiro;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/")
public class CalculatorController {
    private final KafkaTemplate<String, CalculatorModel> kafkaTemplate;

    public CalculatorController(KafkaTemplate<String, CalculatorModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/{operation}")
    public ResponseEntity<String> calculate(
            @PathVariable String operation,
            @RequestParam String a,
            @RequestParam String b) {

        System.out.println(a + " " + b + " " + operation);

        CalculatorModel request = new CalculatorModel(BigDecimal.valueOf(Long.parseLong(a)), BigDecimal.valueOf(Long.parseLong(b)), operation);
        System.out.println(request.toString());

        kafkaTemplate.send("calculate", request.getRequestId(), request);

        return ResponseEntity.accepted()
                .body("ID: " + request.getRequestId());
    }
}