package com.ovg.flipper.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisJwtRepository implements JwtRepository {

    private final StringRedisTemplate redisTemplate;

    public RedisJwtRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String userId, String token) {
        redisTemplate.opsForValue().set(userId, token);
    }

    @Override
    public boolean exists(String userId) {
        return redisTemplate.hasKey(userId) == Boolean.TRUE;
    }

    @Override
    public void delete(String userId) {
        redisTemplate.delete(userId);
    }

    @Override
    public String get(String userId) {
        return redisTemplate.opsForValue().get(userId);
    }
}
