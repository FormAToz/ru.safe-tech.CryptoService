package ru.safetech.service;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.safetech.repository.RedisRepository;
import ru.safetech.repository.SignatureRepository;

import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class SignatureService {

    private static final String SPEC = "prime256v1";
    private static final String ALGO = "SHA256withECDSA";

    private String publicKeyString;
    private byte[] data;

    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private SignatureRepository signatureRepository;

    public void getDataAndPutSignToRedis() {
        data = redisRepository.getData();
        System.out.println("Signature service: got data from Redis");

        try {
            generateAndSave(data);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Формируем для данных массива электронную подпись
    private void generateAndSave(byte[] data) throws Exception {
        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(SPEC);
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA",  "BC");

        g.initialize(ecSpec, new SecureRandom());
        KeyPair keypair = g.generateKeyPair();

        PublicKey publicKey = keypair.getPublic();
        PrivateKey privateKey = keypair.getPrivate();

        Signature ecdsaSign = Signature.getInstance(ALGO);
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(data);
        byte[] signature = ecdsaSign.sign();
        publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String sig = Base64.getEncoder().encodeToString(signature);
        System.out.println("Signature service: signature created: " + sig);

        signatureRepository.addSign(sig);
        System.out.println("Signature service: signature added to Redis");
    }

    public boolean verifySign(String sign) throws Exception {

        Signature ecdsaVerify = Signature.getInstance(ALGO);
        KeyFactory kf = KeyFactory.getInstance("EC");

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(data);
        boolean result = ecdsaVerify.verify(Base64.getDecoder().decode(sign));
        System.out.println("Signature service: verifying signature result: " + result);

        return result;
    }

    public String getSignFromRedis() {
        return signatureRepository.getSign();
    }
}
