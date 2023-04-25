package com.wemakesoftware.citilistingservice.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
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

import org.apache.commons.io.IOUtils;

@Service
@Slf4j
public class ImageService {

    @Value("${minio.bucket-name.city}")
    private String cityImageBucketName;

    @Autowired
    private MinioClient minioClient;

    public String uploadFile(String bucketName, Resource resource, String fileName) {

        try (InputStream fis = resource.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName)
                    .stream(fis, fis.available(), -1).build());

            return String.join(File.separator, fileName);

        } catch (Exception e) {
            log.error("Error when call minio upload file {}", e);
            throw new RuntimeException(e);
        }
    }

    public String uploadImage(MultipartFile image, String objectName) throws IOException {
        return uploadFile(this.cityImageBucketName, new ByteArrayResource(image.getBytes()), objectName);
    }

    public void removeObjectFromBucket(String bucketName, String objectName) throws MinioException {

        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucketName)
                    .object(objectName).build();
            minioClient.removeObject(removeObjectArgs);
        } catch (InvalidKeyException | ErrorResponseException | IllegalArgumentException | InsufficientDataException
                 | InternalException | InvalidBucketNameException | InvalidResponseException | NoSuchAlgorithmException
                 | ServerException | XmlParserException | IOException e) {
            log.error("Error info bucketName: {}, objectName: {}", bucketName, objectName);
            throw new MinioException(e.getMessage());
        }
    }

    public String replace(MultipartFile image, String objectName) throws IOException, MinioException {
        removeObjectFromBucket(cityImageBucketName, objectName);
        return uploadImage(image, objectName);
    }

    public void remove( String objectName) throws MinioException {
        removeObjectFromBucket(cityImageBucketName, objectName);
    }

    public byte[] download(String objectName) throws MinioException {
        try (InputStream fis = minioClient
                .getObject(GetObjectArgs.builder().bucket(cityImageBucketName).object(objectName).build())) {
            return IOUtils.toByteArray(fis);
        } catch (InvalidKeyException | ErrorResponseException | IllegalArgumentException | InsufficientDataException
                 | InternalException | InvalidBucketNameException | InvalidResponseException | NoSuchAlgorithmException
                 | ServerException | XmlParserException | IOException e) {
            throw new MinioException(e.getMessage());
        }
    }
}
