package org.edward.pandora.common.codec.aes;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class KeyGenerator {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;

    public static void main(String[] args) throws Exception {
        javax.crypto.KeyGenerator generator = javax.crypto.KeyGenerator.getInstance(ALGORITHM);
        generator.init(KEY_SIZE);
        SecretKey key = generator.generateKey();
        System.out.println("key: " + new String(Base64.getEncoder().encode(key.getEncoded()), StandardCharsets.UTF_8));
    }

    public static SecretKey generate() throws Exception {
        javax.crypto.KeyGenerator generator = javax.crypto.KeyGenerator.getInstance(ALGORITHM);
        generator.init(KEY_SIZE);
        return generator.generateKey();
    }

    public static SecretKey toSecretKey(byte[] bytes) throws Exception {
        return new SecretKeySpec(bytes, ALGORITHM);
    }

    public static SecretKey toSecretKey(String base64) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        return toSecretKey(bytes);
    }
}