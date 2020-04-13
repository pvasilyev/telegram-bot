package com.github.pvasilyev.telegrambot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * @author pvasilyev
 * @since 08 Apr 2020
 */
@RestController
@RequestMapping("/echo")
public class EchoController {
    @RequestMapping("/v1")
    public String echo() {
        System.err.println("Invoked echo");
        return "Server Time is: " + Instant.now();
    }
}
