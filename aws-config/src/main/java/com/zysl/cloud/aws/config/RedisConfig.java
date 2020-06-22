package com.zysl.cloud.aws.config;

import com.zysl.cloud.utils.StringUtils;
import java.time.Duration;
import lombok.Getter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@Getter
@PropertySource("classpath:redis.properties")
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;


    @Value("${spring.redis.database}")
    private int database;

//    @Value("#{'${redis.nodes}'.split(',')}")
//    private String[] clusterNodes;

    @Value("${spring.redis.socket.timeout}")
    private Duration socketTimeout;

    @Value("${spring.redis.command.timeout}")
    private Duration commandTimeout;
    
    @Value("${spring.redis.lettuce.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.lettuce.pool.min-idle}")
    private int minIdle;

    @Value("${spring.redis.lettuce.pool.max-active}")
    private int maxActive;

    @Value("${spring.redis.lettuce.pool.max-wait}")
    private long maxWait;

    @Value("${spring.redis.cluster.max-redirects}")
    private int maxRedirects;

    /**
     * 单机redis
     * @return
     */
    @Bean
    public JedisClientConfiguration getJedisClientConfiguration() {
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder JedisPoolingClientConfigurationBuilder = (
                JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        GenericObjectPoolConfig GenericObjectPoolConfig = new GenericObjectPoolConfig();
        GenericObjectPoolConfig.setMaxIdle(this.maxIdle);
        GenericObjectPoolConfig.setMaxTotal(this.maxActive);
        GenericObjectPoolConfig.setMinIdle(this.minIdle);
        return JedisPoolingClientConfigurationBuilder.poolConfig(GenericObjectPoolConfig).build();
    }

    @Bean
    public JedisConnectionFactory getJedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(this.database);
        redisStandaloneConfiguration.setHostName(this.host);
        redisStandaloneConfiguration.setPassword(StringUtils.isEmpty(this.password) ? RedisPassword.none() : RedisPassword.of(this.password));
        redisStandaloneConfiguration.setPort(this.port);
        return new JedisConnectionFactory(redisStandaloneConfiguration, getJedisClientConfiguration());
    }

    @Bean
    public RedisTemplate<Object,Object> getRedisTemplate(){
        RedisTemplate<Object,Object> redisTemplate=new RedisTemplate<>();
        RedisSerializer stringRedisSerializer=redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setConnectionFactory(getJedisConnectionFactory());
        return redisTemplate;
    }



}
