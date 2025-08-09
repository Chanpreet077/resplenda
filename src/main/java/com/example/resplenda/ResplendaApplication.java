package com.example.resplenda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ResplendaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResplendaApplication.class, args);
	}

	@Bean//understand this (beans and resttemplate)
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}



}
