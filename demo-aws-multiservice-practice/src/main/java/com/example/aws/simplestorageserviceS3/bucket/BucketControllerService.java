package com.example.aws.simplestorageserviceS3.bucket;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import javax.annotation.PostConstruct;
import java.util.List;
import software.amazon.awssdk.auth.credentials.*;

@Service("BucketControllerService")
@RequiredArgsConstructor
@Slf4j

public class BucketControllerService {
    private AmazonS3 amazonS3;
    private final DemoConfig demoConfig;

    @PostConstruct
    public void init() {
        log.info("initializing the amazons3 object");
        //https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
        // amazonS3 = AmazonS3ClientBuilder.standard().build();

        //AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials
        //      .create(demoConfig.getAccessid(), demoConfig.getSecretkey());
        //StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(awsBasicCredentials);

        // amazonS3 = AmazonS3Client.builder()
        //       .withRegion("ap-south-1")
        //     .withCredentials(new AWSStaticCredentialsProvider(awsBasicCredentials))
        //   .build();

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(demoConfig.getAccessid(), demoConfig.getSecretkey());

        amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.AP_SOUTH_1)
                .build();

       // amazonS3 = AmazonS3ClientBuilder.defaultClient()

    }

    public List<Bucket> listBucket() {
        log.info("listing  the bucket object");
        List<Bucket> bucketList = amazonS3.listBuckets();
        // bucketList.forEach(x -> System.out.println(x.getName() + "creation time:" + x.getCreationDate()));
        return bucketList;

    }

    public Bucket createBucket(String bucketName) {
        log.info("creating  the bucket object");

        Bucket s3Bucket = amazonS3.createBucket(bucketName);
        return s3Bucket;

    }

    public S3Object getObject(String bucketName, String key) {
        log.info("getting  the bucket object");

        S3Object s3Bucket = amazonS3.getObject(bucketName, key);
        return s3Bucket;

    }
}
