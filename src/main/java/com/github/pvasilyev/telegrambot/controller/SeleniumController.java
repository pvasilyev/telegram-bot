package com.github.pvasilyev.telegrambot.controller;

import com.github.pvasilyev.telegrambot.SeleniumContext;
import com.github.pvasilyev.telegrambot.model.SeleniumResponse;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * @author pvasilyev
 * @since 15 Aug 2022
 */
@Slf4j
@RestController
@RequestMapping("/selenium")
public class SeleniumController {

    @Autowired
    private SeleniumContext seleniumContext;

    @GetMapping("/v1")
    public SeleniumResponse reply() {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        final WebDriver webDriver = seleniumContext.getWebDriver().getWebDriver();

        webDriver.get("https://duckduckgo.com/");

        final String title = webDriver.getTitle();

        webDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));

        WebElement searchBox = webDriver.findElement(By.name("q"));
        final WebElement searchButton = webDriver.findElement(By.id("search_button_homepage"));

        searchBox.sendKeys("Selenium");
        searchButton.click();

        searchBox = webDriver.findElement(By.name("q"));
        final String searchBoxValue = searchBox.getAttribute("value");
        
        return SeleniumResponse.builder()
                .title(title)
                .searchBox(searchBoxValue)
                .responseTime(stopwatch.elapsed())
                .build();
    }

}
