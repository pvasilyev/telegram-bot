package com.github.pvasilyev.telegrambot.webhook;

import com.github.pvasilyev.telegrambot.model.HelperBotConfiguration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author pvasilyev
 * @since 08 Apr 2020
 */
@Slf4j
@RequiredArgsConstructor
public class PavelsLittleHelperBot extends TelegramWebhookBot {

    private final AtomicInteger counter = new AtomicInteger();

    @NonNull
    private final HelperBotConfiguration configuration;

    @Override
    public BotApiMethod onWebhookUpdateReceived(final Update update) {
        log.info("Received following update: {}", update);
        if (update.getMessage() != null) {
            final Message message = update.getMessage();
            if (message.getChat() != null) {
                final Chat chat = message.getChat();
                if (chat.getId() == configuration.getPavelGroupChatId()) {
                    return new SendMessage(configuration.getPavelGroupChatId(), "echo test message #" + counter.incrementAndGet());
                }
                if (chat.getId() == configuration.getAgonChatId() && message.getFrom().getId() == configuration.getPavelUserId()) {
                    return new SendMessage(configuration.getAgonChatId(), "Yes sir, as you wish. Signature: " + UUID.randomUUID().toString());
                }
            }
        }
        return null;
    }

    @Override
    public String getBotUsername() {
        return configuration.getBotName();
    }

    @Override
    public String getBotToken() {
        return configuration.getApiToken();
    }

    @Override
    public String getBotPath() {
        return getBotUsername();
    }
}
