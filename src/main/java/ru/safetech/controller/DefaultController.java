package ru.safetech.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.safetech.response.ResponseObj;
import ru.safetech.service.DataGeneratorService;
import ru.safetech.service.SignatureService;

@RestController
@RequestMapping("/")
public class DefaultController {

    private final DataGeneratorService dataGeneratorService;
    private final SignatureService signatureService;

    public DefaultController(DataGeneratorService dataGeneratorService, SignatureService signatureService) {
        this.dataGeneratorService = dataGeneratorService;
        this.signatureService = signatureService;
    }

    @GetMapping("/")
    public ResponseObj index() {

        dataGeneratorService.putDataToRedis();               // Генерируем данные и кладем в Redis
        signatureService.getDataAndPutSignToRedis();         // Забираем даные, генерируем подпись и кладем в Redis

        return dataGeneratorService.getSignFromRedisAndResponse();  // Забираем подпись, проверяем и возвращает ответ
    }
}
