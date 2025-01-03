package com.flavor.recipes.core.cache

import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration

class RedisCacheName {
    companion object {
        const val RECIPE = "RECIPE"
        fun configs(): Map<String, RedisCacheConfiguration> = mapOf(
            RECIPE to config(Duration.ofHours(12))
        )

        fun config(duration: Duration): RedisCacheConfiguration =
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(duration)
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        GenericJackson2JsonRedisSerializer()
                    )
                )
    }

}