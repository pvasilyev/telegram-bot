package com.github.pvasilyev.telegrambot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = TelegramBotApplication.class)
class TelegramBotApplicationTests {

	@Test
	void contextLoads() {
	}

}
