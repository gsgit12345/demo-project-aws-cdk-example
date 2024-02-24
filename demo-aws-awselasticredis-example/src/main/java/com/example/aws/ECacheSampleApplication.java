package com.example.aws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableCaching
public class ECacheSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECacheSampleApplication.class, args);
    }

}
