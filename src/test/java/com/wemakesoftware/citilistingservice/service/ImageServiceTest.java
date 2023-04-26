package com.wemakesoftware.citilistingservice.service;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageServiceTest {

    private ImageService imageService;
    private MinioClient minioClient;
    private String bucketName = "city";

    @BeforeEach
    public void setup(){
        minioClient = mock(MinioClient.class);
        imageService = new ImageServiceImpl(minioClient);
        imageService.setCityImageBucketName(bucketName);
    }

    @Test
    void uploadFile() {
        final Resource imageResource = new ClassPathResource("Map.png");
        String flname = "city";
        String result = imageService.uploadFile(flname, imageResource, bucketName);
        assertNotNull(result);
    }

    @Test
    void removeObjectFromBucket() throws MinioException {
        String obectName = "objectName";
        imageService.removeObjectFromBucket(bucketName, obectName);
    }

    @Test
    void replace() throws MinioException, IOException {
        MultipartFile city = new MockMultipartFile("fileName", new byte[0]);
        String obectName = "objectName";
        String result = imageService.replace(city,obectName);
        assertNotNull(result);
    }

    @Test
    void download() throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        String obectName = "objectName";
        final Resource imageResource = new ClassPathResource("Map.png");
        when(minioClient.getObject(any())).thenReturn(imageResource.getInputStream());
        byte[] result = imageService.download(obectName);
        assertTrue(result.length>0);
    }
}