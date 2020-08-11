package ru.safetech.repository;

import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class SignatureRepository {

    private static final String KEY = "signKey";

    @Resource(name="redisTemplate")
    private SetOperations<String, String> setOps;

    public void addSign(String sign) {
        setOps.add(KEY, sign);
    }

    public String getSign() {
        String sign = setOps.pop(KEY);
        setOps.remove(KEY, sign);
        return sign;
    }
}
