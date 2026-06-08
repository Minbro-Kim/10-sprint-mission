package com.sprint.mission.discodeit.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();

    caffeineCacheManager.registerCustomCache(
        "channelListCache",
        Caffeine.newBuilder()
            .maximumSize(100)
            .recordStats() // 캐시 통계
            .expireAfterAccess(600, TimeUnit.SECONDS)
            .build()
    );

    caffeineCacheManager.registerCustomCache(
        "userListCache",
        Caffeine.newBuilder()
            .maximumSize(100)
            .recordStats() // 캐시 통계
            .expireAfterAccess(600, TimeUnit.SECONDS)
            .build()
    );

    caffeineCacheManager.registerCustomCache(
        "notificationListCache",
        Caffeine.newBuilder()
            .maximumSize(100)
            .recordStats() // 캐시 통계
            .expireAfterAccess(600, TimeUnit.SECONDS)
            .build()
    );

    return caffeineCacheManager;
  }

}
