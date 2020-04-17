package com.github.pvasilyev.telegrambot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pvasilyev
 * @since 13 Apr 2020
 */
@Slf4j
@RestController
public class RootController {
    @RequestMapping("/")
    public String root() {
        log.info("Invoked root");
        return "Ok";
    }
}
