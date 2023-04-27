package com.wemakesoftware.citilistingservice.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.wemakesoftware.citilistingservice.CityListingServiceApplication;
import com.wemakesoftware.citilistingservice.controller.util.MinioContainer;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = CityListingServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class ImageControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    private static final String USERNAME   = "username";

    private static final String PASSWORD   = "password";

    private static final int    MINIO_PORT =  9000;

    private static AmazonS3 client = null;

    private static AmazonS3 getClient(@NotNull MinioContainer container) {
        return   AmazonS3Client.builder()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(USERNAME, PASSWORD)))
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration("http://" + container.getHostAddress(),"oregon")
                )
                .build();
    }

    private static MinioContainer container;

    @BeforeAll
    public static void before(){
        container = new MinioContainer(new MinioContainer.CredentialsProvider(USERNAME, PASSWORD));
        container.withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                new HostConfig()
                        .withPortBindings(new PortBinding(Ports.Binding.bindPort(MINIO_PORT), new ExposedPort(MINIO_PORT)))
        ));
        container.start();
        client = getClient(container);
        Bucket bucket = client.createBucket("city");
        Assertions.assertNotNull(bucket);
        Assertions.assertEquals("city",bucket.getName());
    }

    @Test
    void update() throws Exception {
        final Resource imageResource = new ClassPathResource("1.jpeg");

        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/api/city/image/update")
                        .file("image", image.getBytes())
                        .part(new MockPart("type", "TAG".getBytes()))
                        .param("objectName", UUID.randomUUID() +".jpeg"))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void create() throws Exception {
        final Resource imageResource = new ClassPathResource("1.jpeg");

        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        this.mockMvc.perform(
                MockMvcRequestBuilders.multipart("/v1/api/city/image/create")
                        .file("image", image.getBytes())
                        .param("objectName", UUID.randomUUID() +".jpeg")
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void remove() throws Exception {
        final Resource imageResource = new ClassPathResource("1.jpeg");

        String objectName = UUID.randomUUID() +".jpeg";

        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        this.mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/v1/api/city/image/create")
                                .file("image", image.getBytes())
                                .param("objectName", objectName)
                ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(delete("/v1/api/city/image/remove?objectName="+objectName))
               .andExpect(status().isAccepted());
    }

    @Test
    void download() throws Exception {
        final Resource imageResource = new ClassPathResource("1.jpeg");

        String objectName = UUID.randomUUID() +".jpeg";

        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/api/city/image/c?objectName="+objectName)
                        .file("image", image.getBytes())
                        .part(new MockPart("type", "TAG".getBytes())))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(get("/v1/api/city/image/download?objectName="+objectName))
               .andExpect(status().isOk());
    }

    @After
    public void shutDown() {
        if (client != null) {
            client.shutdown();
            client = null;
        }
        if(container != null) {
            container.stop();
        }
    }

}
