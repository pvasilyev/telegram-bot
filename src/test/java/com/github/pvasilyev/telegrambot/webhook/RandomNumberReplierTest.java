package com.github.pvasilyev.telegrambot.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author pvasilyev
 * @since 04 Oct 2020
 */
class RandomNumberReplierTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RandomNumberReplier randomNumberReplier;

    @BeforeEach
    void before() {
        randomNumberReplier = new RandomNumberReplier();
    }

    @Test
    void givenNullableMessage_whenGetRandomNumber_expectExceptionThrown() {
        assertThrows(NullPointerException.class, () -> randomNumberReplier.getRandomNumber(null, new Chat()));
    }

    @Test
    void givenNullableChat_whenGetRandomNumber_expectExceptionThrown() {
        assertThrows(NullPointerException.class, () -> randomNumberReplier.getRandomNumber(new Message(), null));
    }

    @Test
    void givenNullableMessageText_whenGetRandomNumber_expectNullReturned() {
        final Message message = new Message();

        assertThat(message.getText(), nullValue());
        assertThat(randomNumberReplier.getRandomNumber(message, new Chat()), nullValue());
    }

    @Test
    void givenNoBotCommands_whenGetRandomNumber_expectNullReturned() throws Exception {
        final Message message = objectMapper.readValue("{\"text\":\"test\"}", Message.class);

        assertThat(message.getText(), notNullValue());
        assertThat(message.getEntities(), nullValue());
        assertThat(randomNumberReplier.getRandomNumber(message, new Chat()), nullValue());
    }

    @Test
    void givenEmptyBotCommands_whenGetRandomNumber_expectNullReturned() throws Exception {
        final Message message = objectMapper.readValue("{\"text\":\"test\",\"entities\":[]}", Message.class);

        assertThat(message.getText(), notNullValue());
        assertThat(message.getEntities(), empty());
        assertThat(randomNumberReplier.getRandomNumber(message, new Chat()), nullValue());
    }

    @Test
    void givenBotCommandWithoutNumber_whenGetRandomNumber_expectSendMessageReturned() throws Exception {
        final Message message = objectMapper.readValue("{\"text\":\"/random\",\"entities\":[{\"type\":\"bot_command\",\"offset\":0,\"length\":7}]}", Message.class);
        final Chat chat = objectMapper.readValue("{\"id\":1234}", Chat.class);

        assertThat(message.getText(), notNullValue());
        assertThat(message.getEntities(), not(empty()));

        final BotApiMethod<Message> randomNumber = randomNumberReplier.getRandomNumber(message, chat);

        assertThat(randomNumber, notNullValue());
        assertThat(randomNumber, instanceOf(SendMessage.class));
        final SendMessage sendMessage = (SendMessage) randomNumber;
        assertThat(sendMessage.getChatId(), is("1234"));
        assertThat(sendMessage.getText(), hasLength(1));
        assertThat(sendMessage.getText(), anyOf(is("1"), is("2"), is("3"), is("4"), is("5"), is("6")));
    }

    @Test
    void givenBotCommandWith2PositiveNumbers_whenGetRandomNumber_expectSendMessageReturned() throws Exception {
        final Message message = objectMapper.readValue("{\"text\":\"/random 100 104\",\"entities\":[{\"type\":\"bot_command\",\"offset\":0,\"length\":7}]}", Message.class);
        final Chat chat = objectMapper.readValue("{\"id\":4321}", Chat.class);

        assertThat(message.getText(), notNullValue());
        assertThat(message.getEntities(), not(empty()));

        final BotApiMethod<Message> randomNumber = randomNumberReplier.getRandomNumber(message, chat);

        assertThat(randomNumber, notNullValue());
        assertThat(randomNumber, instanceOf(SendMessage.class));
        final SendMessage sendMessage = (SendMessage) randomNumber;
        assertThat(sendMessage.getChatId(), is("4321"));
        assertThat(sendMessage.getText(), hasLength(3));
        assertThat(sendMessage.getText(), anyOf(is("100"), is("101"), is("102"), is("103"), is("104")));
    }

    @Test
    void givenBotCommandWith1PositiveNumber1_whenGetRandomNumber_expectSendMessageReturned() throws Exception {
        final Message message = objectMapper.readValue("{\"text\":\"/random 3\",\"entities\":[{\"type\":\"bot_command\",\"offset\":0,\"length\":7}]}", Message.class);
        final Chat chat = objectMapper.readValue("{\"id\":9988}", Chat.class);

        assertThat(message.getText(), notNullValue());
        assertThat(message.getEntities(), not(empty()));

        final BotApiMethod<Message> randomNumber = randomNumberReplier.getRandomNumber(message, chat);

        assertThat(randomNumber, notNullValue());
        assertThat(randomNumber, instanceOf(SendMessage.class));
        final SendMessage sendMessage = (SendMessage) randomNumber;
        assertThat(sendMessage.getChatId(), is("9988"));
        assertThat(sendMessage.getText(), hasLength(1));
        assertThat(sendMessage.getText(), anyOf(is("1"), is("2"), is("3")));
    }

    @Test
    void givenBotCommandWith2NegativeNumbers_whenGetRandomNumber_expectSendMessageReturned() throws Exception {
        final Message message = objectMapper.readValue("{\"text\":\"/random -10 -14\",\"entities\":[{\"type\":\"bot_command\",\"offset\":0,\"length\":7}]}", Message.class);
        final Chat chat = objectMapper.readValue("{\"id\":5544}", Chat.class);

        assertThat(message.getText(), notNullValue());
        assertThat(message.getEntities(), not(empty()));

        final BotApiMethod<Message> randomNumber = randomNumberReplier.getRandomNumber(message, chat);

        assertThat(randomNumber, notNullValue());
        assertThat(randomNumber, instanceOf(SendMessage.class));
        final SendMessage sendMessage = (SendMessage) randomNumber;
        assertThat(sendMessage.getChatId(), is("5544"));
        assertThat(sendMessage.getText(), hasLength(3));
        assertThat(sendMessage.getText(), anyOf(is("-10"), is("-11"), is("-12"), is("-13"), is("-14")));
    }

    @Test
    void givenBotCommandWith1NegativeNumber_whenGetRandomNumber_expectSendMessageReturned() throws Exception {
        final Message message = objectMapper.readValue("{\"text\":\"/random -4\",\"entities\":[{\"type\":\"bot_command\",\"offset\":0,\"length\":7}]}", Message.class);
        final Chat chat = objectMapper.readValue("{\"id\":7766}", Chat.class);

        assertThat(message.getText(), notNullValue());
        assertThat(message.getEntities(), not(empty()));

        final BotApiMethod<Message> randomNumber = randomNumberReplier.getRandomNumber(message, chat);

        assertThat(randomNumber, notNullValue());
        assertThat(randomNumber, instanceOf(SendMessage.class));
        final SendMessage sendMessage = (SendMessage) randomNumber;
        assertThat(sendMessage.getChatId(), is("7766"));
        assertThat(sendMessage.getText(), hasLength(2));
        assertThat(sendMessage.getText(), anyOf(is("-1"), is("-2"), is("-3"), is("-4")));
    }

    @Test
    void givenBotCommandWith1ZeroNumber_whenGetRandomNumber_expectSendMessageReturned() throws Exception {
        final Message message = objectMapper.readValue("{\"text\":\"/random 0\",\"entities\":[{\"type\":\"bot_command\",\"offset\":0,\"length\":7}]}", Message.class);
        final Chat chat = objectMapper.readValue("{\"id\":4433}", Chat.class);

        assertThat(message.getText(), notNullValue());
        assertThat(message.getEntities(), not(empty()));

        final BotApiMethod<Message> randomNumber = randomNumberReplier.getRandomNumber(message, chat);

        assertThat(randomNumber, notNullValue());
        assertThat(randomNumber, instanceOf(SendMessage.class));
        final SendMessage sendMessage = (SendMessage) randomNumber;
        assertThat(sendMessage.getChatId(), is("4433"));
        assertThat(sendMessage.getText(), hasLength(1));
        assertThat(sendMessage.getText(), is("0"));
    }
}
