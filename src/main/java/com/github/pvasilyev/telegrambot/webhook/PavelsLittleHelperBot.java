package com.github.pvasilyev.telegrambot.webhook;

import com.github.pvasilyev.telegrambot.model.HelperBotConfiguration;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                final String messageText = message.getText();
                if (!StringUtils.isBlank(messageText)
                        && messageText.startsWith("/random")
                        && message.getEntities() != null) {
                    return message.getEntities().stream()
                            .filter(m -> EntityType.BOTCOMMAND.equals(m.getType()))
                            .findFirst()
                            .map(messageEntity -> {
                                final String actualText = StringUtils.isBlank(messageEntity.getText())
                                        ? messageText.substring(messageEntity.getLength())
                                        : messageEntity.getText();
                                final Pattern pattern = Pattern.compile("\\d+");
                                final Matcher matcher = pattern.matcher(actualText.trim());
                                final List<Integer> list = extractInts(matcher);
                                if (list.size() <= 1) {
                                    return getDefaultRandomNumber();
                                }
                                list.sort(Comparator.naturalOrder());
                                final int min = list.get(0);
                                final int max = list.get(list.size() - 1);
                                return getRandomNumber(min, max);
                            })
                            .map(text -> new SendMessage(chat.getId(), text))
                            .orElse(null);
                }
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

    @Nonnull
    private List<Integer> extractInts(@Nonnull final Matcher matcher) {
        final List<Integer> list = new ArrayList<>();
        while (matcher.find()) {
            final int number;
            final String token = matcher.group();
            try {
                number = Integer.parseInt(token);
                list.add(number);
            } catch (final NumberFormatException ex) {
                // ignore
                log.warn("Unable to parse following token into number: {}, skipping.", token);
            }
        }
        return list;
    }

    @Nonnull
    private String getDefaultRandomNumber() {
        return getRandomNumber(1, 6);
    }

    @Nonnull
    private String getRandomNumber(final int minInclusive, final int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1) + "";
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
