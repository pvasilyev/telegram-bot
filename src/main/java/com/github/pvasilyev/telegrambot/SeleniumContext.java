package com.github.pvasilyev.telegrambot;

import com.github.pvasilyev.telegrambot.selenium.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author pvasilyev
 * @since 15 Aug 2022
 */
@Slf4j
@Configuration
public class SeleniumContext {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebDriverManager getWebDriver() {
        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        final WebDriver driver;
        try {
            driver = new RemoteWebDriver(new URL("http://localhost:4444"), firefoxOptions);
        } catch (final MalformedURLException ex) {
            throw new RuntimeException("Unable to construct URL, analyze stacktrace", ex);
        }
        return new WebDriverManager(driver);
    }

}
