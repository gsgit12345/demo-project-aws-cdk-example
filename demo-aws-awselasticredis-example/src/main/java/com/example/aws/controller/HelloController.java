package com.example.aws.controller;

import com.example.aws.pojo.ChannelMappingRequestVO;
import com.example.aws.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    CacheService cacheService;


    @PostMapping(produces = { "application/json" })
    public String getData(@RequestBody ChannelMappingRequestVO channelMappingRequestVO){
        return cacheService.senderSettings(channelMappingRequestVO);

    }
}
