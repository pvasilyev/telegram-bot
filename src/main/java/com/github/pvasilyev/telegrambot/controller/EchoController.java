package com.github.pvasilyev.telegrambot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * @author pvasilyev
 * @since 08 Apr 2020
 */
@Slf4j
@RestController
@RequestMapping("/echo")
public class EchoController {
    @RequestMapping("/v1")
    public String echo() {
        log.info("Invoked echo");
        return "Server Time is: " + Instant.now();
    }
}
