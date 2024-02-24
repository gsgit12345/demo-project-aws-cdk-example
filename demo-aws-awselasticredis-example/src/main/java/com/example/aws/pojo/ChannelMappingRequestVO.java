package com.example.aws.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelMappingRequestVO implements Serializable {
    private String propertyId;
    private String channelId;

}