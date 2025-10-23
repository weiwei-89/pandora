package org.edward.pandora.common.codec.rsa;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyGenerator {
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    public static void main(String[] args) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
        generator.initialize(KEY_SIZE);
        KeyPair keyPair = generator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        System.out.println("public key: " + new String(Base64.getEncoder().encode(publicKey.getEncoded()), StandardCharsets.UTF_8));
        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println("private key: " + new String(Base64.getEncoder().encode(privateKey.getEncoded()), StandardCharsets.UTF_8));
    }

    public static PublicKey toPublicKey(String base64) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
        return factory.generatePublic(spec);
    }

    public static PrivateKey toPrivateKey(String base64) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
        return factory.generatePrivate(spec);
    }
}