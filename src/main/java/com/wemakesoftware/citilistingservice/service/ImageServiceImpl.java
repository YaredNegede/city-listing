package com.wemakesoftware.citilistingservice.service;

import com.wemakesoftware.citilistingservice.controller.Paths;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    @Value("${minio.bucket-name.city}")
    private String cityImageBucketName;

    private final MinioClient minioClient;

    public ImageServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String uploadFile(String bucketName, Resource resource, String fileName) throws Exception {

        if(resource == null){
            throw new Exception("Resource is not set");
        }

        if(StringUtils.isBlank(bucketName)){
            throw new Exception("Bucket is not set");
        }

        if(StringUtils.isBlank(fileName)){
            throw new Exception("File name cannot be null");
        }

        fileName = UUID.randomUUID()+fileName.replace(" ","_");

        try (InputStream fis = resource.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName)
                    .stream(fis, fis.available(), -1).build());

            return Paths.root_image_download+"?objectName="+fileName;

        } catch (Exception e) {
            log.error("Error when call minio upload file {}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String uploadImage(MultipartFile image, String objectName) throws Exception {
        return uploadFile(this.cityImageBucketName, new ByteArrayResource(image.getBytes()), objectName);
    }

    @Override
    public void removeObjectFromBucket(String bucketName, String objectName) throws MinioException {

        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucketName)
                    .object(objectName).build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("Error info bucketName: {}, objectName: {}", bucketName, objectName);
            throw new MinioException(e.getMessage());
        }
    }

    @Override
    public String replace(MultipartFile image, String objectName) throws Exception {
        if(image == null){
            throw new Exception("Bucket is not set");
        }

        if(StringUtils.isBlank(objectName)){
            throw new Exception("File name cannot be null");
        }
        removeObjectFromBucket(cityImageBucketName, objectName);
        return uploadImage(image, objectName);
    }

    @Override
    public void remove(String objectName) throws MinioException {
        removeObjectFromBucket(cityImageBucketName, objectName);
    }

    @Override
    public byte[] download(String objectName) throws MinioException {
        try (InputStream fis = minioClient
                                    .getObject(GetObjectArgs.builder()
                                            .bucket(cityImageBucketName)
                                            .object(objectName)
                                            .build()
                                    )
        ) {
            return IOUtils.toByteArray(fis);

        } catch (Exception e) {
            throw new MinioException(e.getMessage());
        }
    }

    public void setCityImageBucketName(String cityImageBucketName) {
        this.cityImageBucketName = cityImageBucketName;
    }
}
