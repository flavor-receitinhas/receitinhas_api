package com.flavor.recipes.core.cache

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration


@Configuration
class RedisConfig {
    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(RedisCacheName.config(Duration.ofHours(2)))
            .withInitialCacheConfigurations(RedisCacheName.configs())
            .build()
    }
}