package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@Slf4j
public class RedisConnectionConfig {

    @Value("${sky.redis.host}")
    private String host;

    @Value("${sky.redis.port}")
    private Integer port;

    @Value("${sky.redis.password}")
    private String password;

    @Value("${sky.redis.database}")
    private Integer database;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("创建Redis连接工厂，连接到 {}:{}", host, port);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(password);
        config.setDatabase(database);
        return new LettuceConnectionFactory(config);
    }
}