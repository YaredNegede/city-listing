package com.wemakesoftware.citilistingservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.wemakesoftware.citilistingservice.CityListingServiceApplication;
import com.wemakesoftware.citilistingservice.controller.util.MinioContainer;
import com.wemakesoftware.citilistingservice.controller.util.MinioSetup;
import com.wemakesoftware.citilistingservice.controller.util.Page;
import com.wemakesoftware.citilistingservice.dto.PhotoDto;
import com.wemakesoftware.citilistingservice.model.City;
import com.wemakesoftware.citilistingservice.repository.CityListingRepository;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.*;
import java.util.UUID;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(classes = CityListingServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class CityListingControllerTestIT extends MinioSetup {

    @Autowired
    private MockMvc mockMvc;

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

            City city = new City(0l, cyt[0]);

            cityListingRepository.save(city);

        }

        log.debug("migration started");
    }

    @Test
    @DisplayName("Should read all cities , and update for photos")
    void getAllCities() throws Exception {
        int currentPage = 0;
        int size = 10;
        for (int i = 0; i < 2; i++) {
            ResultActions result = mockMvc.perform(
                            get(Paths.root_city + "/public?currentPage=" + currentPage + "&size=" + size).with(jwt()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()", Matchers.is(10)))
                    .andExpect(jsonPath("$.numberOfElements", Matchers.is(10)))
                    .andExpect(jsonPath("$.totalPages", Matchers.is(1)))
                    .andExpect(jsonPath("$.size", Matchers.is(10)));

            String content = result.andReturn()
                    .getResponse()
                    .getContentAsString();

            Page pagesCityDtos = objectMapper.readValue(content, Page.class);

            pagesCityDtos.content.forEach(cityDto -> {
                String fileName = UUID.randomUUID() + ".jpeg";
                try {

                    String url = createOne(fileName, "1.jpeg", this.mockMvc)
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

                    PhotoDto photoDto = PhotoDto.builder()
                            .photoUrl(url)
                            .photoName(fileName)
                            .build();

                    mockMvc.perform(
                                    put(Paths.root_city + cityDto.getId() + "/photo").with(jwt())
                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                            .content(objectMapper.writeValueAsBytes(photoDto))
                            )
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn()
                            .getResponse()
                            .getContentAsString()
                            .contains(Paths.root_image + Paths.root_image_download + "?objectName=" + fileName);

                    mockMvc.perform(get(Paths.root_image+url))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andExpect(header().stringValues("Content-Type","image/jpeg"))
                            .andReturn()
                            .getResponse()
                            .getContentAsString()
                            .contains("attachment; filename="+url);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        }

    }

}