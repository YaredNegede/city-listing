package com.wemakesoftware.citilistingservice.service;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
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
    @Description("should upload file")
    void uploadFile() throws Exception {
        final Resource imageResource = new ClassPathResource("Map.png");
        String flname = "city";
        String result = imageService.uploadFile(flname, imageResource, bucketName);
        assertNotNull(result);
        assertEquals("/v1/api/city/image/download?objectName=city",result);
    }

    @Test
    @Description("should not upload file")
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
    void removeObjectFromBucket() throws MinioException {
        String obectName = "objectName";
        imageService.removeObjectFromBucket(bucketName, obectName);
    }

    @Test
    void replace() throws Exception {
        MultipartFile city = new MockMultipartFile("fileName", new byte[0]);
        String obectName = "objectName";
        String result = imageService.replace(city,obectName);
        assertNotNull(result);
        assertEquals("/v1/api/city/image/download?objectName=objectName",result);
    }

    @Test
    void download() throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        String obectName = "objectName";
        final Resource imageResource = new ClassPathResource("Map.png");
        when(minioClient.getObject(any())).thenReturn(imageResource.getInputStream());
        byte[] result = imageService.download(obectName);
        assertNotNull(result);
        assertTrue(result.length>0);
    }
}
