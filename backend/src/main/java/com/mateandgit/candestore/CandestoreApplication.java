package com.mateandgit.candestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class CandestoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CandestoreApplication.class, args);
	}
}
