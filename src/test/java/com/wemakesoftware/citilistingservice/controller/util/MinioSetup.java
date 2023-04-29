package com.wemakesoftware.citilistingservice.controller.util;

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
import com.wemakesoftware.citilistingservice.controller.Paths;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

public abstract class MinioSetup {

    protected static final String USERNAME   = "username";
    protected static final String PASSWORD   = "password";
    protected static  int    MINIO_PORT      =  9000;
    protected static AmazonS3 client         =  null;

    protected static MinioContainer container;

    protected static AmazonS3 getClient(@NotNull MinioContainer container) {
        return   AmazonS3Client.builder()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(USERNAME, PASSWORD)))
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration("http://" + container.getHostAddress(),"oregon")
                )
                .build();
    }

    @BeforeAll
    public static void before(){

        if(null == container) {
            container = new MinioContainer(new MinioContainer.CredentialsProvider(USERNAME, PASSWORD))
                    .withReuse(true)

                    .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                            new HostConfig()
                                    .withPortBindings(new PortBinding(Ports.Binding.bindPort(MINIO_PORT), new ExposedPort(MINIO_PORT)))
                    ));
            container.start();
            client = getClient(container);
            Bucket bucket = client.createBucket("city");
            Assertions.assertNotNull(bucket);
            Assertions.assertEquals("city", bucket.getName());
        }
    }

    protected ResultActions createOne(String fileName, String file, MockMvc mockMvc) throws Exception {

        final Resource imageResource = new ClassPathResource("1.jpeg");

        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        return mockMvc.perform(
                multipart(Paths.root_image + Paths.root_image_create)
                        .file("image", image.getBytes())
                        .param("objectName", fileName)
        );
    }

}
