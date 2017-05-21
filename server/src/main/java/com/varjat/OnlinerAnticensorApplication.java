package com.varjat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OnlinerAnticensorApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlinerAnticensorApplication.class, args);
	}
}
