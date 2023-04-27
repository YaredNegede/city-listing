package com.wemakesoftware.citilistingservice.controller;

import com.wemakesoftware.citilistingservice.CityListingServiceApplication;
import com.wemakesoftware.citilistingservice.model.City;
import com.wemakesoftware.citilistingservice.repository.CityListingRepository;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.migration.Context;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;

import java.io.*;
import java.sql.Statement;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(classes = CityListingServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class CityListingControllerTestIT {

    @Autowired
    private CityListingRepository cityListingRepository;

    @BeforeEach
    public void setup() throws Exception {

        log.debug("migration started");

        Reader fileReader = new FileReader("src/test/resources/data/world-cities.csv");

        BufferedReader inStream = new BufferedReader(fileReader);

        String inString;

        while ((inString = inStream.readLine()) != null) {

            String[] cyt = inString.split(",");

            City city = new City(0l,cyt[0]);

            cityListingRepository.save(city);

        }

        log.debug("migration started");
    }

    @Test
    void getAllCities() {

    }

    @Test
    void deleteCity() {

    }

    @Test
    void updateCityDetail() {

    }

}