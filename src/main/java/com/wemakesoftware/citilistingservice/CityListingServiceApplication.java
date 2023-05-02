package com.wemakesoftware.citilistingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


@SpringBootApplication
public class CityListingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CityListingServiceApplication.class, args);
	}

}
