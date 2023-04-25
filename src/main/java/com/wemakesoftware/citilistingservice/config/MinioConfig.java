package com.wemakesoftware.citilistingservice.config;


import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
public class MinioConfig {

    @Value("${MINIO_URL:http://wemakesoftware-minio:9000}")
    private String url;

    @Value("${USERNAME:username}")
    private String username;

    @Value("${PASSWORD:password}")
    private String password;

    @Bean
    public MinioClient minioclient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(username, password)
                .build();
    }
}
