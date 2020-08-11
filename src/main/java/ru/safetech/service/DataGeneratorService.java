package ru.safetech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.safetech.repository.RedisRepository;
import ru.safetech.response.ResponseObj;

@Service
public class DataGeneratorService {
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private SignatureService signatureService;

    // Формируем данные(при получении HTTP GET запроса)
    private byte[] generateData() {
        byte[] bytes = new byte[200000];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (Math.random() * 100);
        }

        System.out.println("Generator Service: data generated");
        return bytes;
    }

    // Кладем данные в REDIS
    public void putDataToRedis() {
        redisRepository.addData(generateData());
        System.out.println("Generator Service: data putted to Redis");
    }

    // Забираем подпись из Redis
    public ResponseObj getSignFromRedisAndResponse() {
        String sign = signatureService.getSignFromRedis();
        System.out.println("Generator Service: got sign from Redis: " + sign);

        try {
            return signatureService.verifySign(sign)
                    ? new ResponseObj(true, sign)
                    : new ResponseObj(false, "Sign`s aren`n equals!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseObj(false, "An error has occurred...");
    }
}
