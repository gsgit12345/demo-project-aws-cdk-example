package com.example.aws.simplestorageserviceS3.bucket;


//import com.amazonaws.auth.AWSCredentialsProvider;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Builder;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.internal.sync.FileContentStreamProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.macie2.model.S3Bucket;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@Service("S3BucketService")
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
    public String createPresignedGetUrl(String bucketName, String keyName) {
        try (S3Presigner presigner = S3Presigner.create()) {

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            log.info("Presigned URL: [{}]", presignedRequest.url().toString());
            log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

            return presignedRequest.url().toExternalForm();
        }
    }
    public void useHttpUrlConnectionToPut(String presignedUrlString, File fileToPut, Map<String, String> metadata) {
        log.info("Begin [{}] upload", fileToPut.toString());
        try {
            URL presignedUrl = new URL(presignedUrlString);
            HttpURLConnection connection = (HttpURLConnection) presignedUrl.openConnection();
            connection.setDoOutput(true);
            metadata.forEach((k, v) -> connection.setRequestProperty("x-amz-meta-" + k, v));
            connection.setRequestMethod("PUT");
            OutputStream out = connection.getOutputStream();

            try (RandomAccessFile file = new RandomAccessFile(fileToPut, "r");
                 FileChannel inChannel = file.getChannel()) {
                ByteBuffer buffer = ByteBuffer.allocate(8192); //Buffer size is 8k

                while (inChannel.read(buffer) > 0) {
                    buffer.flip();
                    for (int i = 0; i < buffer.limit(); i++) {
                        out.write(buffer.get());
                    }
                    buffer.clear();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

            out.close();
            connection.getResponseCode();
            log.info("HTTP response code is " + connection.getResponseCode());

        } catch (S3Exception | IOException e) {
            log.error(e.getMessage(), e);
        }
    }


    public void useHttpClientToPut(String presignedUrlString, File fileToPut, Map<String, String> metadata) {
        log.info("Begin [{}] upload", fileToPut.toString());

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        metadata.forEach((k, v) -> requestBuilder.header("x-amz-meta-" + k, v));

        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            final HttpResponse<Void> response = httpClient.send(requestBuilder
                            .uri(new URL(presignedUrlString).toURI())
                            .PUT(HttpRequest.BodyPublishers.ofFile(Path.of(fileToPut.toURI())))
                            .build(),
                    HttpResponse.BodyHandlers.discarding());

            log.info("HTTP response code is " + response.statusCode());

        } catch (URISyntaxException | InterruptedException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void useSdkHttpClientToPut(String presignedUrlString, File fileToPut, Map<String, String> metadata) {
        log.info("Begin [{}] upload", fileToPut.toString());

        try {
            URL presignedUrl = new URL(presignedUrlString);

            SdkHttpRequest.Builder requestBuilder = SdkHttpRequest.builder()
                    .method(SdkHttpMethod.PUT)
                    .uri(presignedUrl.toURI());
            // Add headers
            metadata.forEach((k, v) -> requestBuilder.putHeader("x-amz-meta-" + k, v));
            // Finish building the request.
            SdkHttpRequest request = requestBuilder.build();

            HttpExecuteRequest executeRequest = HttpExecuteRequest.builder()
                    .request(request)
                    .contentStreamProvider(new FileContentStreamProvider(fileToPut.toPath()))
                    .build();

            try (SdkHttpClient sdkHttpClient = ApacheHttpClient.create()) {
                HttpExecuteResponse response = sdkHttpClient.prepareRequest(executeRequest).call();
                log.info("Response code: {}", response.httpResponse().statusCode());
            }
        } catch (URISyntaxException | IOException e) {
            log.error(e.getMessage(), e);
        }
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