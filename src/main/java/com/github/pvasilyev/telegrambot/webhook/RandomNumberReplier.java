package com.github.pvasilyev.telegrambot.webhook;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author pvasilyev
 * @since 04 Oct 2020
 */
@Slf4j
public class RandomNumberReplier {

    @Nullable
    public BotApiMethod<Message> getRandomNumber(@NonNull final Message message,
                                                 @NonNull final Chat chat) {
        if (!message.isCommand()) {
            return null;
        }
        return message.getEntities()
                .stream()
                .filter(Objects::nonNull)
                .filter(messageEntity -> EntityType.BOTCOMMAND.equals(messageEntity.getType()))
                .findFirst()
                .map(messageEntity -> {
                    final String actualText = message.getText().substring(messageEntity.getLength());
                    final Pattern pattern = Pattern.compile("-?\\d+");
                    final Matcher matcher = pattern.matcher(actualText.trim());
                    final List<Integer> list = extractInts(matcher);
                    if (list.isEmpty()) {
                        return getDefaultRandomNumber();
                    } else if (list.size() == 1) {
                        final int boundary = list.get(0);
                        if (boundary > 0) {
                            return getRandomNumber(1, boundary);
                        } else if (boundary < 0) {
                            return getRandomNumber(boundary, -1);
                        } else {
                            return "0";
                        }
                    }
                    list.sort(Comparator.naturalOrder());
                    final int min = list.get(0);
                    final int max = list.get(list.size() - 1);
                    return getRandomNumber(min, max);
                })
                .map(text -> new SendMessage(chat.getId(), text))
                .orElse(null);
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

}
