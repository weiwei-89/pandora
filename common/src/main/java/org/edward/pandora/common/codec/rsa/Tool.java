package org.edward.pandora.common.codec.rsa;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

public class Tool {
    private static final String SIGN_ALGORITHM = "SHA256withRSA";
    private static final int MAX_ENCRYPT_BLOCK = 245;
    private static final int MAX_DECRYPT_BLOCK = 256;

    public static byte[] encrypt(byte[] data,
                                 PrivateKey privateKey,
                                 PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        byte[] signatureBytes = signature.sign();
        Info info = new Info();
        info.setData(data);
        info.setSignature(signatureBytes);
        String json = new ObjectMapper().writeValueAsString(info);
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        return codecLargeData(jsonBytes, Cipher.ENCRYPT_MODE, publicKey);
    }

    public static String encryptToBase64(byte[] data,
                                         PrivateKey privateKey,
                                         PublicKey publicKey) throws Exception {
        byte[] bytes = encrypt(data, privateKey, publicKey);
        return new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
    }

    public static byte[] decrypt(byte[] data,
                                 PrivateKey privateKey,
                                 PublicKey publicKey) throws Exception {
        byte[] jsonBytes = codecLargeData(data, Cipher.DECRYPT_MODE, privateKey);
        String json = new String(jsonBytes, StandardCharsets.UTF_8);
        Info info = new ObjectMapper().readValue(json, Info.class);
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(publicKey);
        boolean result = signature.verify(info.getSignature());
        if(result) {
            return info.getData();
        }
        throw new Exception("signature erroe");
    }

    public static byte[] decryptToBase64(String data,
                                         PrivateKey privateKey,
                                         PublicKey publicKey) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8));
        return decrypt(bytes, privateKey, publicKey);
    }

    public static byte[] codecLargeData(byte[] data,
                                        int mode,
                                        Key key) throws Exception {
        int block = 0;
        if(mode == Cipher.ENCRYPT_MODE) {
            block = MAX_ENCRYPT_BLOCK;
        } else if(mode == Cipher.DECRYPT_MODE) {
            block = MAX_DECRYPT_BLOCK;
        } else {
            throw new Exception("unsupported codec mode");
        }
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
        cipher.init(mode, key);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = null;
        int currentIndex = 0;
        int unHandledCount = data.length;
        while(unHandledCount > 0) {
            if(unHandledCount > block) {
                buffer = cipher.doFinal(data, currentIndex, block);
                unHandledCount -= block;
                currentIndex += block;
            } else {
                buffer = cipher.doFinal(data, currentIndex, unHandledCount);
                unHandledCount -= unHandledCount;
                currentIndex += unHandledCount;
            }
            out.write(buffer);
        }
        return out.toByteArray();
    }
}