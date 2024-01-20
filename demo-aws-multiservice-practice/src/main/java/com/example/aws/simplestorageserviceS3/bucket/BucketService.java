package com.example.aws.simplestorageserviceS3.bucket;


//import com.amazonaws.auth.AWSCredentialsProvider;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Builder;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.macie2.model.S3Bucket;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j

public class BucketService {
    private final DemoConfig demoConfig;

    public S3Client gimmeClient(AwsCredentialsProvider provider) {

        Region region = Region.AP_SOUTH_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(provider)
                .build();

        return s3;
    }

    public void createBucket(AwsCredentialsProvider provider) throws IOException {
        S3Client s3 = gimmeClient(provider);
        s3.createBucket(CreateBucketRequest
                .builder()
                .bucket("demobucketfun")
                .build());
    }

    public void uploadFile(AwsCredentialsProvider provider) throws IOException {
        S3Client s3 = gimmeClient(provider);
        String bucketName = "ghanshyam-demo";
        String remotefilename = "user"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss"))
                + ".json";

        InputStream resourceAsStream = BucketService.class.getResourceAsStream("/user.json");

        s3.putObject(PutObjectRequest
                        .builder()
                        .bucket(bucketName)
                        .key(remotefilename)
                        .build(),
                RequestBody.fromInputStream(resourceAsStream, resourceAsStream.available())
        );

        s3.close();
    }

    public void listBucket() {
        final AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().build();
        List<Bucket> bucketList = amazonS3.listBuckets();
        bucketList.forEach(x -> System.out.println(x.getName() + "creation time:" + x.getCreationDate()));

    }

    public void readBucketContent() {
        String bucketName = "ghanshyam-demo";
        String item = "user2024-01-20 04_27_37.json";
        final AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().build();
        S3Object s3Object = amazonS3.getObject(bucketName, item);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        ObjectMapper mapper = new ObjectMapper();
        try {
            User user = mapper.readValue(inputStream, User.class);
            System.out.println("user is::" + user.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
        public void updateBucketContent()
        {
            String bucketName = "ghanshyam-demo";
            String newBucket = "delhi-ghan2";

            String item = "user2024-01-20 04_27_37.json";
            final AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().build();
            S3Object s3Object = amazonS3.getObject(bucketName, item);
            S3ObjectInputStream stream = s3Object.getObjectContent();
            ObjectMapper mapper = new ObjectMapper();
            try {
                User user = mapper.readValue(stream, User.class);
                System.out.println("user is::" + user.toString());
                if(!amazonS3.doesBucketExistV2(newBucket))
                amazonS3.createBucket(newBucket);
                user.setAge("40");
                user.setFirstName("harish");
                user.setFirstName("tiwari");
                File file=new File("secondfile.json");
                mapper.writeValue(file,user);
               // amazonS3.putObject(newBucket,"secondfile.json",file);

                amazonS3.deleteObject(newBucket,"secondfile.json");
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }
    }