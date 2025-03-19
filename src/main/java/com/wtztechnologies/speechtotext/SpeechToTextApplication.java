package com.wtztechnologies.speechtotext;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpeechToTextApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpeechToTextApplication.class, args);
	}

}
