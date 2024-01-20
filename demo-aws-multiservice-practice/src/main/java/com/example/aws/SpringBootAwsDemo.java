package com.example.aws;

import com.example.aws.simplestorageserviceS3.bucket.DemoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {DemoConfig.class
        })

public class SpringBootAwsDemo {
    public static void main(String str[])
    {
        SpringApplication.run(SpringBootAwsDemo.class, str);    }
}
