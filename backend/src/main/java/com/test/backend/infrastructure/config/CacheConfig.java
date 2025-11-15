package com.test.backend.infrastructure.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${cache.caffeine.maximum-size:1000}")
    private int maximumSize;

    @Value("${cache.caffeine.expire-after-write:10m}")
    private Duration expireAfterWrite;

    @Value("${cache.caffeine.record-stats:true}")
    private boolean recordStats;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("productDetails", "similarIds");
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWrite);
        
        if (recordStats) {
            builder.recordStats();
        }
        
        return builder;
    }
}
