package de.sandkastenliga.resultserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
public class ResultserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResultserverApplication.class, args);
	}
}
