package com.arturviader.pelisbdapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class PelisbdapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PelisbdapiApplication.class, args);
	}

}
