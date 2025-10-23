package org.edward.pandora.common.codec;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;

public class CipherTool {
    private static final int MAX_ENCRYPT_BLOCK = 245;
    private static final int MAX_DECRYPT_BLOCK = 256;

    public static byte[] codecLargeData(
            byte[] data,
            int mode,
            Key key,
            String algorithm
    ) throws Exception {
        int block = 0;
        if(mode == Cipher.ENCRYPT_MODE) {
            block = MAX_ENCRYPT_BLOCK;
        } else if(mode == Cipher.DECRYPT_MODE) {
            block = MAX_DECRYPT_BLOCK;
        } else {
            throw new Exception("unsupported codec mode");
        }
        Cipher cipher = Cipher.getInstance(algorithm);
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