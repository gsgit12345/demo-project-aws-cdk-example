package com.example.aws.simplestorageserviceS3.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.example.aws.simplestorageserviceS3.bucket.BucketControllerService;
import com.example.aws.simplestorageserviceS3.bucket.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BucketServiceController {
    @Autowired
    public BucketControllerService bucketService;


    @GetMapping("/createBucket")
    public Bucket createBucket(@RequestParam String bucketName)
    {
      return  bucketService.createBucket(bucketName);
    }
    @GetMapping("/listBucket")
    public List<Bucket> listBucket()
    {
        return  bucketService.listBucket();

    }
    @GetMapping("/getBucketObject")

    public S3Object getObject(@RequestParam String bucketName, @RequestParam String key)
    {
        return  bucketService.getObject(bucketName,key);

    }
}
