package com.github.pvasilyev.telegrambot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pvasilyev
 * @since 13 Apr 2020
 */
@RestController
public class RootController {
    @RequestMapping("/")
    public String root() {
        System.err.println("Invoked root");
        return "Ok";
    }
}
