package com.github.pvasilyev.telegrambot.webhook;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pvasilyev.telegrambot.model.HelperBotConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author pvasilyev
 * @since 12 Apr 2020
 */
@Configuration
public class TelegramBotConfig {

    private static final String CALLBACK_PREFIX = "callback/";

    public TelegramBotsApi createTelegramBots() throws Exception {
        ApiContextInitializer.init();

        final String localIp = "localhost";
        System.err.println(localIp);
        final HelperBotConfiguration helperBotConfiguration = retrieveBotConfiguration();
        final String secretToken = helperBotConfiguration.getPathToken();
        final TelegramBotsApi telegramBotsApi = new TelegramBotsApi(
                "https://webhook.pavelstgbot.xyz/" + CALLBACK_PREFIX + secretToken + "/",
                "http://" + localIp + ":8787/" + CALLBACK_PREFIX + secretToken + "/");
        try {
            final PavelsLittleHelperBot bot = pavelsBot(helperBotConfiguration);
            telegramBotsApi.registerBot(bot);

            return telegramBotsApi;
        } catch (TelegramApiException e) {
            System.err.println("AAAAAA!!!111");
            e.printStackTrace();
            throw new RuntimeException("AAAAAA", e);
        }
    }

    private HelperBotConfiguration retrieveBotConfiguration() {
        final String secretName = "tgbothelper-configuration";
        final String region = "us-east-1";
        final AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .build();
        final String secret;
        final GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        final GetSecretValueResult getSecretValueResult;

        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (DecryptionFailureException | InternalServiceErrorException | InvalidParameterException | InvalidRequestException | ResourceNotFoundException e) {
            throw e;
        }

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

    public PavelsLittleHelperBot pavelsBot(final HelperBotConfiguration helperBotConfiguration) {
        return new PavelsLittleHelperBot(helperBotConfiguration);
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

}
