package com.wemakesoftware.citilistingservice;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CityListingServiceApplication  implements ApplicationRunner {


	public static void main(String[] args) {
		SpringApplication.run(CityListingServiceApplication.class, args);
	}

	@Autowired
	private Flyway flyway;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		flyway.migrate();
	}
}
