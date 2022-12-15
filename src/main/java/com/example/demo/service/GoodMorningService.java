package com.example.demo.service;

import com.example.demo.client.AvailabilityServiceClient;
import com.example.demo.client.HelloServiceClient;
import com.example.demo.client.model.Hello;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

@Service
public class GoodMorningService {

    private final AvailabilityServiceClient availabilityServiceClient;
    private final HelloServiceClient helloServiceClient;

    public GoodMorningService(AvailabilityServiceClient availabilityServiceClient, HelloServiceClient helloServiceClient) {
        this.availabilityServiceClient = availabilityServiceClient;
        this.helloServiceClient = helloServiceClient;
    }

    public Mono<String> getGoodMorningMessage(String time, String lang) {
        return Mono.zip(
                availabilityServiceClient.getAvailabilityList(lang, time),
                helloServiceClient.getGreetingList(lang).collectList()
        ).map(result -> {
            String availabilityMessage = result.getT1();
            List<Hello> helloResult = result.getT2();
            Random random = new Random();
            Hello hello = helloResult.get(random.nextInt(helloResult.size()));
            return hello.getGreeting() + " " + availabilityMessage;
        });
    }
}
