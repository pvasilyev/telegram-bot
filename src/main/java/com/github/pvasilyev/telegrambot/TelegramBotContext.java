package com.github.pvasilyev.telegrambot;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pvasilyev.telegrambot.model.HelperBotConfiguration;
import com.github.pvasilyev.telegrambot.webhook.PavelsLittleHelperBot;
import com.github.pvasilyev.telegrambot.webhook.RandomNumberReplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nonnull;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author pvasilyev
 * @since 12 Apr 2020
 */
@Slf4j
@Configuration
public class TelegramBotContext {

    private static final String CALLBACK_PREFIX = "callback/";

    @Bean
    public TelegramBotsApi createTelegramBots() throws Exception {
        ApiContextInitializer.init();

        final String localIp;
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            localIp = socket.getLocalAddress().getHostAddress();
        }
        log.info("For telegram webhook going to use following local ip: {}", localIp);
        final HelperBotConfiguration helperBotConfiguration = retrieveBotConfiguration();
        final String secretToken = helperBotConfiguration.getPathToken();
        final TelegramBotsApi telegramBotsApi = new TelegramBotsApi(
                "https://webhook.pavelstgbot.xyz/" + CALLBACK_PREFIX + secretToken + "/",
                "http://" + localIp + ":8787/" + CALLBACK_PREFIX + secretToken + "/");
        try {
            final PavelsLittleHelperBot bot = pavelsBot(helperBotConfiguration);
            telegramBotsApi.registerBot(bot);
            bot.echoDeployed();

            return telegramBotsApi;
        } catch (final TelegramApiException e) {
            final String message = "Unable to bootstrap config due to telegram error";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Nonnull
    private HelperBotConfiguration retrieveBotConfiguration() {
        final String secretName = "tgbothelper-configuration";
        final String region = "us-east-1";
        final AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .build();
        final String secret;
        final GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        final GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);

        // Decrypts secret using the associated KMS CMK.
        // Depending on whether the secret is a string or binary, one of these fields will be populated.
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
            try {
                return getObjectMapper().readValue(secret, HelperBotConfiguration.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error while parsing secret", e);
            }
        }
        throw new RuntimeException("Unable to retrieve secret from AWS");
    }

    @Bean
    public PavelsLittleHelperBot pavelsBot(final HelperBotConfiguration helperBotConfiguration) {
        return new PavelsLittleHelperBot(
                helperBotConfiguration,
                new RandomNumberReplier());
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

}
