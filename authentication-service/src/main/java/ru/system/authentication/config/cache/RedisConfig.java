package ru.system.authentication.config.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
@Validated
@ConfigurationProperties(prefix = "cache.redis")
public class RedisConfig {
    @Setter
    private List<@Valid redisDataTTL> cacheNames = new ArrayList<>(); // ttsCacheData

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        log.error("{}", cacheNames);
        log.error("{}", cacheNames.size());
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
                .withInitialCacheConfigurations(
                        cacheNames.stream().collect(Collectors.toMap(
                                v -> v.cacheName,
                                v -> {
                                    RedisCacheConfiguration redisConf = RedisCacheConfiguration
                                            .defaultCacheConfig()
                                            .disableCachingNullValues();
                                    if (v.ttl != 0 && v.ttl != -1) {
                                        redisConf.entryTtl(v.getDuration());
                                    }
                                    return redisConf;
                                }
                                )
                        )
                ).build();
    }


    @Data
    private static class redisDataTTL {
        @NotNull
        @NotBlank(message = "Name must not be blank")
        private String cacheName;
        @NotNull
        private Long ttl = 15L;
        private String typeDuration;

        public Duration getDuration() {
            if (typeDuration == null) {
                return Duration.ofMinutes(ttl);
            }
            if (typeDuration.equalsIgnoreCase("ms") || typeDuration.matches("(?i)\\bmilliseconds?\\b(?-i)")) {
                return Duration.ofMillis(ttl);
            }
            if (typeDuration.equalsIgnoreCase("s") || typeDuration.matches("(?i)\\bseconds?\\b(?-i)")) {
                return Duration.ofSeconds(ttl);
            }
            if (typeDuration.equalsIgnoreCase("h") || typeDuration.matches("(?i)\\bhours?\\b(?-i)")) {
                return Duration.ofHours(ttl);
            }
            if (typeDuration.equalsIgnoreCase("d") || typeDuration.matches("(?i)\\bdays?\\b(?-i)")) {
                return Duration.ofDays(ttl);
            }
            return Duration.ofMinutes(ttl);
        }
    }
}
