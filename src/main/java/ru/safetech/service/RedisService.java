package ru.safetech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.safetech.repository.RedisRepository;

@Service
public class RedisService {

    @Autowired
    RedisRepository redisRepository;

    public void saveData(byte[] data) {
        redisRepository.addData(data);
    }

    public byte[] getData() {
        return redisRepository.getData();
    }
}
