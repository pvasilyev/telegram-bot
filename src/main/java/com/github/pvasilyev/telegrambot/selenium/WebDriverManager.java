package com.github.pvasilyev.telegrambot.selenium;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.DisposableBean;

/**
 * @author pvasilyev
 * @since 15 Aug 2022
 */
@Getter
@RequiredArgsConstructor
public class WebDriverManager implements DisposableBean {

    @NonNull
    private final WebDriver webDriver;

    @Override
    public void destroy() {
        this.webDriver.quit();
    }
}
