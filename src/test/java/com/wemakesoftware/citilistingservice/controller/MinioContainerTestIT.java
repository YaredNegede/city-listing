package com.wemakesoftware.citilistingservice.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.wemakesoftware.citilistingservice.controller.util.MinioContainer;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class MinioContainerTestIT {

    private static final String ACCESS_KEY = "accessKey";
    private static final String SECRET_KEY = "secretKey";

    private static final String BUCKET = "bucket";

    private AmazonS3 client = null;

    @After
    public void shutDown() {
        if (client != null) {
            client.shutdown();
            client = null;
        }
    }

    @Test
    public void testCreateBucket() {
        try (MinioContainer container = new MinioContainer(
                new MinioContainer.CredentialsProvider(ACCESS_KEY, SECRET_KEY))) {
            container.start();
            client = getClient(container);
            Bucket bucket = client.createBucket(BUCKET);
            Assertions.assertNotNull(bucket);
            Assertions.assertEquals(BUCKET,bucket.getName());

            List<Bucket> buckets = client.listBuckets();
            Assertions.assertNotNull(buckets);
            assertEquals(1, buckets.size());
            Assertions.assertTrue(buckets.stream()
                    .map(Bucket::getName)
                    .collect(Collectors.toList())
                    .contains(BUCKET));
        }
    }

    private AmazonS3 getClient(MinioContainer container) {

        return   AmazonS3Client.builder()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration("http://" + container.getHostAddress(),"oregon")
                )
                .build();

    }

}