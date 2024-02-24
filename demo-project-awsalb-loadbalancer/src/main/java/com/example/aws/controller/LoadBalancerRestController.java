package com.example.aws.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

@RestController
public class LoadBalancerRestController {

    @GetMapping(value="/getIp")
    public String getIpAddress() throws IOException {
        URL url=new URL("http://checkip.amazonaws.com");
        BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));
        String ip=br.readLine();
        return "ip is "+ip;

    }
}
