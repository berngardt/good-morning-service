package com.example.demo.controller;

import com.example.demo.service.GoodMorningService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/good-morning")
public class UIController {

    private final GoodMorningService goodMorningService;

    public UIController(GoodMorningService goodMorningService) {
        this.goodMorningService = goodMorningService;
    }

    @GetMapping
    public Mono<String> getHellos(
            @RequestParam(value = "lang", required = false) String lang,
            @RequestParam(value = "time", required = false) String time
    ) {
        return goodMorningService.getGoodMorningMessage(time, lang);
    }
}
