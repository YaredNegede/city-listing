package com.wemakesoftware.citilistingservice.service;

import io.minio.errors.MinioException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImageService {

    String uploadFile(String bucketName, Resource resource, String fileName) throws Exception;

    String uploadImage(MultipartFile image, String objectName) throws Exception;

    void removeObjectFromBucket(String bucketName, String objectName) throws MinioException;

    String replace(MultipartFile image, String objectName) throws Exception;

    void remove(String objectName) throws MinioException;

    byte[] download(String objectName) throws MinioException;

    void setCityImageBucketName(String bucketName);
}
