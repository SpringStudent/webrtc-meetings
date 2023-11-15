package io.springstudent.meeting.common.config;

import cn.hutool.core.util.StrUtil;
import io.springstudent.meeting.common.shiro.ShiroVariable;
import io.springstudent.meeting.common.util.EmptyUtils;
import org.crazycake.shiro.RedisManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value(value = "${spring.redis.host}")
    private String host;
    @Value(value = "${spring.redis.port}")
    private int port;
    @Value(value = "${spring.redis.password}")
    private String password;
    @Value(value = "${spring.redis.database0}")
    private int database0;

    //配置工厂
    public RedisConnectionFactory connectionFactory(int dbIndex) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        if (StrUtil.isNotEmpty(password)) {
            redisStandaloneConfiguration.setPassword(password);
        }
        redisStandaloneConfiguration.setDatabase(dbIndex);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(connectionFactory(database0));
        return redisTemplate;
    }

    @Bean("shiroRedisManager")
    public RedisManager shiroRedisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setDatabase(database0);
        if(EmptyUtils.isNotEmpty(password)){
            redisManager.setPassword(password);
        }
        redisManager.setExpire(ShiroVariable.SESSION_TIMEOUT);
        return redisManager;
    }
}
