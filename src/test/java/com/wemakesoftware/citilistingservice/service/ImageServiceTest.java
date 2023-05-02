package com.wemakesoftware.citilistingservice.service;

import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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
    @DisplayName("should upload file")
    void uploadFile() throws Exception {
        final Resource imageResource = new ClassPathResource("Map.png");
        String flname = "city";
        String result = imageService.uploadFile(flname, imageResource, bucketName);
        assertNotNull(result);
    }

    @Test
    @DisplayName("should not upload file")
    void uploadFileError() throws Exception {
        final Resource imageResource = new ClassPathResource("Map.png");

        assertThrows(Exception.class,
                ()->{
                   imageService.uploadFile(null, imageResource, bucketName);
                });

        assertThrows(Exception.class,
                ()->{
                   imageService.uploadFile("name", imageResource, null);
                });
        assertThrows(Exception.class,
                ()->{
                   imageService.uploadFile(null, imageResource, null);
                });
        assertThrows(Exception.class,
                ()->{
                   imageService.uploadFile(null, null, null);
                });
    }

    @Test
    void replace() throws Exception {
        MultipartFile city = new MockMultipartFile("fileName", new byte[0]);
        String obectName = "objectName";
        String result = imageService.replace(city,obectName);
        assertNotNull(result);
    }

    @Test
    void download() throws  Exception {
        String obectName = "objectName";
        final Resource imageResource = new ClassPathResource("Map.png");
        when(minioClient.getObject(any())).thenReturn(imageResource.getInputStream());
        byte[] result = imageService.download(obectName);
        assertNotNull(result);
        assertTrue(result.length>0);
    }
}
