package ru.safetech.repository;

import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class RedisRepository {
    private final String KEY = "data";

    @Resource(name="redisTemplate")
    private SetOperations<String, byte[]> setOps;

    public void addData(byte[] data) {
        setOps.add(KEY, data);
    }

    public byte[] getData() {
        byte[] data = setOps.pop(KEY);
        setOps.remove(KEY, data);
        return data;
    }
}
