package com.github.pvasilyev.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * @author pvasilyev
 * @since 12 Apr 2020
 */
@Value
public class HelperBotConfiguration {

    private final String apiToken;
    private final String botName;
    private final String pathToken;
    private final long pavelGroupChatId;
    private final long agonChatId;
    private final long dubbandChatId;
    private final long pavelUserId;

    @JsonCreator
    public HelperBotConfiguration(
            @JsonProperty("tgbothelper-api-token") final String apiToken,
            @JsonProperty("tgbothelper-name") final String botName,
            @JsonProperty("tgbothelper-path-token") final String pathToken,
            @JsonProperty("tgbothelper-pavel-group-chat-id") final long pavelGroupChatId,
            @JsonProperty("tgbothelper-agon-chat-id") final long agonChatId,
            @JsonProperty("tgbothelper-dubband-chat-id") final long dubbandChatId,
            @JsonProperty("tgbothelper-pavel-user-id") final long pavelUserId) {
        this.apiToken = apiToken;
        this.botName = botName;
        this.pathToken = pathToken;
        this.pavelGroupChatId = pavelGroupChatId;
        this.agonChatId = agonChatId;
        this.dubbandChatId = dubbandChatId;
        this.pavelUserId = pavelUserId;
    }
}
