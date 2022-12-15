package com.example.demo.client;

import com.example.demo.client.model.Hello;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Component
public class HelloServiceClient {

    private final WebClient client;

    public HelloServiceClient(WebClient.Builder builder) {
        this.client = builder.baseUrl("http://localhost:8080/api").build();
    }

    public Flux<Hello> getGreetingList(String lang) {
        return this.client.get()
                .uri(uriBuilder -> uriBuilder.path("/hello").queryParamIfPresent("lang", Optional.ofNullable(lang)).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Hello.class);
    }
}
