package com.example.aws.service;

import com.example.aws.pojo.ChannelMappingRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheService {

    @Cacheable(value="senderSettings",
            key="#channelMappingRequestVO.getPropertyId + '-' + #channelMappingRequestVO.getChannelId() ",
            cacheManager="redisCacheManager")
    public String senderSettings(ChannelMappingRequestVO channelMappingRequestVO) {

        String test= "{\"hello\":\"working fine\"}";
        return test;
    }
}
