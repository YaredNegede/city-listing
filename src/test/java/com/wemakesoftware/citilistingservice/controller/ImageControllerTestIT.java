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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
    private static AmazonS3     client     =  null;
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
    @Description("Should be able to create image")
    void create() throws Exception {
        final Resource imageResource = new ClassPathResource("1.jpeg");

        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        String fileName = UUID.randomUUID() +".jpeg";

        this.mockMvc.perform(
                    MockMvcRequestBuilders.multipart(Paths.root_image+Paths.root_image_create)
                            .file("image", image.getBytes())
                            .param("objectName", fileName)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains(String.join(Paths.root_image,Paths.root_image_download,fileName));

    }

    @Test
    @Description("Should not be able to create image")
    void createError() throws Exception {
        this.mockMvc.perform(post(Paths.root_image+Paths.root_image_create))
                .andExpect(MockMvcResultMatchers.status().is(415))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains("Content-Type 'null' is not supported");
    }

    @Test
    @Description("Should update images")
    void update() throws Exception {

        final Resource imageResource = new ClassPathResource("1.jpeg");

        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        String fileName = UUID.randomUUID() +".jpeg";

        this.mockMvc.perform(
                        MockMvcRequestBuilders.multipart(Paths.root_image+Paths.root_image_create)
                                .file("image", image.getBytes())
                                .param("objectName", fileName)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains(String.join(Paths.root_image,Paths.root_image_download,fileName));


        this.mockMvc.perform(MockMvcRequestBuilders.multipart(Paths.root_image+Paths.root_image_update)
                        .file("image", image.getBytes())
                        .param("objectName", fileName))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains(String.join(Paths.root_image,Paths.root_image_download,fileName));

        this.mockMvc.perform(post(Paths.root_image+Paths.root_image_update))
                .andExpect(MockMvcResultMatchers.status().is(415))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains("Content-Type 'null' is not supported");
    }

    @Test
    @Description("Should not be updating image")
    void updateError() throws Exception {

        final Resource imageResource = new ClassPathResource("1.jpeg");

        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        String fileName = UUID.randomUUID() +".jpeg";

        this.mockMvc.perform(
                        MockMvcRequestBuilders.multipart(Paths.root_image+Paths.root_image_create)
                                .file("image", image.getBytes())
                                .param("objectName", fileName)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains(String.join(Paths.root_image,Paths.root_image_download,fileName));

        this.mockMvc.perform(post(Paths.root_image+Paths.root_image_update))
                .andExpect(MockMvcResultMatchers.status().is(415))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains("Content-Type 'null' is not supported");
    }

    @Test
    @Description("Should remove image")
    void remove() throws Exception {
        final Resource imageResource = new ClassPathResource("1.jpeg");
        String objectName = UUID.randomUUID() +".jpeg";
        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        this.mockMvc.perform(
                        MockMvcRequestBuilders.multipart(Paths.root_image+Paths.root_image_create)
                                .file("image", image.getBytes())
                                .param("objectName", objectName)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(delete(Paths.root_image+Paths.root_image_delete+"?objectName="+objectName))
                .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isAccepted());
    }
 
    @Test
    @Description("Should download image")
    void download() throws Exception {
        final Resource imageResource = new ClassPathResource("1.jpeg");
        String objectName = UUID.randomUUID() +".jpeg";
        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        ResultActions result = this.mockMvc.perform(multipart(Paths.root_image+Paths.root_image_create)
                .file("image", image.getBytes())
                .param("objectName", objectName));

        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        String path = result.andReturn().getResponse().getContentAsString();

        mockMvc.perform(get(path))
                .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type","image/jpeg"))
                .andExpect(header().stringValues("Content-Disposition","attachment; filename="+objectName));
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
