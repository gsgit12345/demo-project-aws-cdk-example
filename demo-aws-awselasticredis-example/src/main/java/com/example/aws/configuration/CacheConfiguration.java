package com.example.aws.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.List;

import static io.lettuce.core.ReadFrom.REPLICA_PREFERRED;

@Configuration
public class CacheConfiguration {

    @Value("${spring.cache.redis.hostname}")
    private List redisHosts;

    @Value("${spring.cache.redis.host}")
    private String host;

    @Value("${spring.cache.redis.port}")
    private int redisPort;

    public static final long TIME_OUT = 10L;
    public static final Integer NUM_THREADS = 8;


    @Bean
    @Profile("default")
    public LettuceConnectionFactory redisConnectionFactoryDev() {

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(REPLICA_PREFERRED)
                .commandTimeout(Duration.ofSeconds(3L))
                .build();

        RedisStaticMasterReplicaConfiguration serverConfig =
                new RedisStaticMasterReplicaConfiguration
                        (host, redisPort);

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }


    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer(Object.class)))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer(Object.class)))
                .disableCachingNullValues();
    }




    @Bean
    @Qualifier("redisCacheManager")
    @Profile("default")
    public CacheManager redisCacheManagerDev(){
        return RedisCacheManager.RedisCacheManagerBuilder.
                fromConnectionFactory(redisConnectionFactoryDev()).build();
    }
}
