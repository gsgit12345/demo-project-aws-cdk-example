package com.example.aws.simplestorageserviceS3.lambda;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.aws.simplestorageserviceS3.bucket.DemoConfig;
import com.example.aws.simplestorageserviceS3.bucket.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.amazonaws.services.lambda.runtime.events.S3Event;

import java.io.File;


@Slf4j
@RequiredArgsConstructor

public class LambdaRequestHandler implements RequestHandler<S3Event,String> {
   private static final  String DESTINATION_BUCKET="ghanshyam-demo";
    private final DemoConfig demoConfig;

    private AmazonS3 amazonS3;


    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(demoConfig.getAccessid(), demoConfig.getSecretkey());

        amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.AP_SOUTH_1)
                .build();

        log.info("in the handle request method");
        String result="";
       String bucket=s3Event.getRecords().get(0).getS3().getBucket().getName();
        String key=s3Event.getRecords().get(0).getS3().getObject().getKey();

        log.info(String.format("new event:bucket %s,object %s ",bucket,key));
        S3Object s3Object=amazonS3.getObject(new GetObjectRequest(bucket,key));
        S3ObjectInputStream stream= s3Object.getObjectContent();
        ObjectMapper mapper=new ObjectMapper();
        User user=null;
try
{
    user=mapper.readValue(stream,User.class);
user.setFirstName("reeat");
user.setAge("30");
user.setLastName("bahguna");
if(!amazonS3.doesBucketExistV2(DESTINATION_BUCKET))
    amazonS3.createBucket(DESTINATION_BUCKET);
File file=new File("/tmp/modified"+key);
mapper.writeValue(file,user);
    amazonS3.putObject(DESTINATION_BUCKET,"modified"+key,file);
    result="Success";;
}catch(Exception ex)
{
    log.info("Exception occured in the processing"+ex.getMessage());
    ex.printStackTrace();
    result="Fail";;

}
        log.info("in the handle request method return statement::"+result);

        return result;
    }
}
