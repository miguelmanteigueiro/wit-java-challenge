package com.manteigueiro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;

@Configuration
public class RestConfiguration {

    @Bean
    public Map<String, CompletableFuture<ResponseEntity<String>>> completableFutureMap() {
        return new HashMap<>();
    }
}
