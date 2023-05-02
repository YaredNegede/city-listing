package com.wemakesoftware.citilistingservice.config;


import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@Slf4j
public class MinioConfig {

    @Value("${MINIO_URL:localhost}")
    private String url;

    @Value("${USERNAME:username}")
    private String username;

    @Value("${PASSWORD:password}")
    private String password;

    @Bean
    public MinioClient minioclient() {

        log.info(url);
        log.info(getUsername());
        log.info(getPassword());
        return MinioClient.builder()
                .endpoint(url)
                .credentials(username, password)
                .build();
    }
}
