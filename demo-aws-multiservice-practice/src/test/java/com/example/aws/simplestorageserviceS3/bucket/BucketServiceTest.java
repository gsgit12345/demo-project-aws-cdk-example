package com.example.aws.simplestorageserviceS3.bucket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.auth.credentials.*;

import java.io.IOException;

//@SpringBootTest
@SpringBootTest

//@Disabled
public class BucketServiceTest {

    @Autowired
    BucketService bucketService;

    @Autowired
    DemoConfig demoConfig;
//https://www.youtube.com/watch?v=bQPQhaLLhTg
    @Test
    void uploadFileStatic() throws IOException {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials
                .create(demoConfig.getAccessid(), demoConfig.getSecretkey());
        StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(awsBasicCredentials);
        bucketService.uploadFile(staticCredentialsProvider);
        //step to run
        //1-create IAM User
        //Create policy and give required access to this IAM User and Attach policy
    }

    @Test
    void uploadFileNoPermissions() throws IOException {

        ProfileCredentialsProvider nopermissionsProfile = ProfileCredentialsProvider
                .builder()
                .profileName("nopermissions_dev") //this is the  profile name
                .build();
        bucketService.uploadFile(nopermissionsProfile);
    }

    @Test
    void uploadFileOnlyBucketProfile() throws IOException {
        ProfileCredentialsProvider bucketDemo = ProfileCredentialsProvider
                .builder()
                .profileName("onlybucket")
                .build();
        bucketService.uploadFile(bucketDemo);

    }

    @Test
    void uploadFileEnvironmentVariable() throws IOException {
        EnvironmentVariableCredentialsProvider envVar = EnvironmentVariableCredentialsProvider.create();
        bucketService.uploadFile(envVar);
    }
    @Test
    public void uploadFileJavaSystem() throws IOException {
        SystemPropertyCredentialsProvider envVar = SystemPropertyCredentialsProvider.create();
        bucketService.uploadFile(envVar);
    }

    @Test
    void uploadFileDefaultProfile() throws IOException {
        DefaultCredentialsProvider defaultProfile = DefaultCredentialsProvider
                .builder()
                .build();
        bucketService.uploadFile(defaultProfile);
    }

    @Test
    void createBucket() throws IOException {
        DefaultCredentialsProvider defaultProfile = DefaultCredentialsProvider
                .builder()
                .build();
        bucketService.createBucket(defaultProfile);
    }
    @Test
    public void listBucket() throws IOException {
        DefaultCredentialsProvider defaultProfile = DefaultCredentialsProvider
                .builder()
                .build();
        bucketService.listBucket();
    }
    @Test
    public void readBucketContent()
    {
        DefaultCredentialsProvider defaultProfile = DefaultCredentialsProvider
                .builder()
                .build();
        bucketService.readBucketContent();

    }
    @Test
    public void updateBucketContent()
    {
        DefaultCredentialsProvider defaultProfile = DefaultCredentialsProvider
                .builder()
                .build();
        bucketService.updateBucketContent();

    }
}