package org.edward.pandora.common.codec.rsa;

import org.edward.pandora.common.codec.CipherTool;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

public class Tool {
    private static final String SIGN_ALGORITHM = "SHA256withRSA";
    private static final String CODEC_ALGORITHM = "RSA/ECB/PKCS1PADDING";

    public static byte[] sign(
            byte[] data,
            PrivateKey privateKey
    ) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public static void verify(
            byte[] data,
            byte[] signatureBytes,
            PublicKey publicKey
    ) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        boolean result = signature.verify(signatureBytes);
        if(result) {
            return;
        }
        throw new Exception("signature error");
    }

    public static byte[] encrypt(
            byte[] data,
            PublicKey publicKey
    ) throws Exception {
        return CipherTool.codecLargeData(data, Cipher.ENCRYPT_MODE, publicKey, CODEC_ALGORITHM);
    }

    public static String encryptToBase64(
            byte[] data,
            PublicKey publicKey
    ) throws Exception {
        byte[] bytes = encrypt(data, publicKey);
        return new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
    }

    public static byte[] decrypt(
            byte[] data,
            PrivateKey privateKey
    ) throws Exception {
        return CipherTool.codecLargeData(data, Cipher.DECRYPT_MODE, privateKey, CODEC_ALGORITHM);
    }

    public static byte[] decryptBase64(
            String data,
            PrivateKey privateKey
    ) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8));
        return decrypt(bytes, privateKey);
    }
}