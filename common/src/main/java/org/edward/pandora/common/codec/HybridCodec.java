package org.edward.pandora.common.codec;

import org.edward.pandora.common.codec.aes.AesInfo;
import org.edward.pandora.common.codec.aes.KeyGenerator;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class HybridCodec {
    public static HybridResult encrypt(
            byte[] data,
            PublicKey _publicKey,
            PrivateKey privateKey
    ) throws Exception {
        SecretKey aesKey = KeyGenerator.generate();
        AesInfo aesInfo = org.edward.pandora.common.codec.aes.Tool.encrypt(data, aesKey);
        byte[] aesKeyBytesByRsa = org.edward.pandora.common.codec.rsa.Tool.encrypt(aesKey.getEncoded(), _publicKey);
        StringBuilder sb = new StringBuilder();
        sb.append(new String(Base64.getEncoder().encode(aesInfo.getData()), StandardCharsets.UTF_8))
                .append("&")
                .append(new String(Base64.getEncoder().encode(aesInfo.getIv()), StandardCharsets.UTF_8))
                .append("&")
                .append(new String(Base64.getEncoder().encode(aesKeyBytesByRsa), StandardCharsets.UTF_8));
        byte[] signatureBytes = org.edward.pandora.common.codec.rsa.Tool.sign(sb.toString().getBytes(StandardCharsets.UTF_8), privateKey);
        return new HybridResult(aesInfo.getData(), aesKeyBytesByRsa, aesInfo.getIv(), signatureBytes);
    }

    public static byte[] decrypt(
            HybridResult result,
            PublicKey _publicKey,
            PrivateKey privateKey
    ) throws Exception {
        if(System.currentTimeMillis()-result.getTimestamp() >= 10*1000) {
            throw new Exception("signature expired");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(new String(Base64.getEncoder().encode(result.getData()), StandardCharsets.UTF_8))
                .append("&")
                .append(new String(Base64.getEncoder().encode(result.getIv()), StandardCharsets.UTF_8))
                .append("&")
                .append(new String(Base64.getEncoder().encode(result.getAesKey()), StandardCharsets.UTF_8));
        org.edward.pandora.common.codec.rsa.Tool.verify(sb.toString().getBytes(StandardCharsets.UTF_8), result.getSignature(), _publicKey);
        byte[] aesKeyBytes = org.edward.pandora.common.codec.rsa.Tool.decrypt(result.getAesKey(), privateKey);
        AesInfo aesInfo = new AesInfo(result.getData(), result.getIv());
        return org.edward.pandora.common.codec.aes.Tool.decrypt(aesInfo, KeyGenerator.toSecretKey(aesKeyBytes));
    }
}