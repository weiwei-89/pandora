package org.edward.pandora.common.codec.aes;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

public class Tool {
    private static final String CODEC_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    public static AesInfo encrypt(
            byte[] data,
            SecretKey key
    ) throws Exception {
        byte[] ivBytes = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);
        Cipher cipher = Cipher.getInstance(CODEC_ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] aesBytes = cipher.doFinal(data);
        return new AesInfo(aesBytes, ivBytes);
    }

    public static byte[] decrypt(
            AesInfo info,
            SecretKey key
    ) throws Exception {
        Cipher cipher = Cipher.getInstance(CODEC_ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, info.getIv());
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        return cipher.doFinal(info.getData());
    }
}