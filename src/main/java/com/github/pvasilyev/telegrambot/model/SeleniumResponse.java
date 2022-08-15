package com.github.pvasilyev.telegrambot.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Duration;

/**
 * @author pvasilyev
 * @since 15 Aug 2022
 */
@Value
@Builder
public class SeleniumResponse {

    @NonNull
    private final String title;

    @NonNull
    private final String searchBox;

    @NonNull
    private final Duration responseTime;

}
