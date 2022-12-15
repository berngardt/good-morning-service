package com.example.demo.client;

import com.example.demo.client.model.Hello;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class AvailabilityServiceClient {
    private final WebClient client;

    public AvailabilityServiceClient(WebClient.Builder builder) {
        this.client = builder.baseUrl("http://localhost:8082/api").build();
    }

    public Mono<String> getAvailabilityList(String lang, String time) {
        return this.client.get()
                .uri(uriBuilder -> uriBuilder.path("/availability")
                        .queryParamIfPresent("time", Optional.ofNullable(time))
                        .queryParamIfPresent("lang", Optional.ofNullable(lang)).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }
}
