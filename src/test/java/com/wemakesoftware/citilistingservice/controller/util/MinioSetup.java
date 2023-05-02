package com.wemakesoftware.citilistingservice.controller.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.wemakesoftware.citilistingservice.controller.Paths;
import com.wemakesoftware.citilistingservice.dto.auth.AuthenticationRequest;
import com.wemakesoftware.citilistingservice.dto.auth.AuthenticationResponse;
import com.wemakesoftware.citilistingservice.model.security.Role;
import com.wemakesoftware.citilistingservice.model.security.User;
import com.wemakesoftware.citilistingservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;


import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
public abstract class MinioSetup {

    protected static final String USERNAME   = "username";
    protected static final String PASSWORD   = "password";
    protected static  int    MINIO_PORT      =  9000;
    private final String username = "admin@wemakesoftware.com";
    private static String password = "password";
    private  String passwordEncode;

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

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected PasswordEncoder passwordEncoder;

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

    @BeforeEach
    public void setUp() {
        List<User> userList = (List<User>) userRepository.findAll();
        if(userList.isEmpty()) {
            passwordEncode = passwordEncoder.encode(password);
            User user = User.builder()
                    .role(Role.ADMIN)
                    .email(username)
                    .firstname("yared")
                    .password(passwordEncoder.encode(password))
                    .build();
           userRepository.save(user);
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
                        .with(jwt())
        );
    }

    protected RequestPostProcessor jwt() {
        return request -> {
            try {
                AuthenticationRequest auth = AuthenticationRequest.builder()
                        .email(username)
                        .password(password)
                        .build();

                ResultActions res = mockMvc.perform(
                        post("/v1/api/auth/authenticate")
                                .content(objectMapper.writeValueAsString(auth))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                );
                String cnt = res.andReturn().getResponse().getContentAsString();

                if(StringUtils.isBlank(cnt)){
                    throw new Exception("Failed Authorization");
                }
                AuthenticationResponse authenticationResponse =  objectMapper.readValue(cnt, AuthenticationResponse.class);

                request.addHeader("Authorization","Bearer "+authenticationResponse.getAccessToken());
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return request;
        };
    }

}
