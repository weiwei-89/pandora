package org.edward.pandora.common.codec;

import com.alibaba.fastjson2.JSON;
import org.edward.pandora.common.codec.rsa.KeyGenerator;
import org.edward.pandora.common.model.Response;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HttpTool {
    public static HttpRequest toRequest(
            Object data,
            String _publicKey,
            String privateKey
    ) throws Exception {
        String json1 = JSON.toJSONString(data);
        HybridResult result = HybridCodec.encrypt(
                json1.getBytes(StandardCharsets.UTF_8),
                KeyGenerator.toPublicKey(_publicKey),
                KeyGenerator.toPrivateKey(privateKey)
        );
        String json2 = JSON.toJSONString(result);
        String base64 = new String(
                Base64.getEncoder().encode(json2.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8
        );
        return new HttpRequest().setData(base64);
    }

    public static <T> T readFromRequest(
            HttpRequest request,
            String _publicKey,
            String privateKey,
            Class<T> clazz
    ) throws Exception {
        String base64 = request.getData();
        String json2 = new String(
                Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8
        );
        HybridResult result = JSON.parseObject(json2, HybridResult.class);
        String json1 = new String(
                HybridCodec.decrypt(
                        result,
                        KeyGenerator.toPublicKey(_publicKey),
                        KeyGenerator.toPrivateKey(privateKey)),
                StandardCharsets.UTF_8);
        return JSON.parseObject(json1, clazz);
    }

    public static Response toResponse(
            Object data,
            String _publicKey,
            String privateKey
    ) throws Exception {
        String json1 = JSON.toJSONString(data);
        HybridResult result = HybridCodec.encrypt(
                json1.getBytes(StandardCharsets.UTF_8),
                KeyGenerator.toPublicKey(_publicKey),
                KeyGenerator.toPrivateKey(privateKey)
        );
        String json2 = JSON.toJSONString(result);
        String base64 = new String(
                Base64.getEncoder().encode(json2.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8
        );
        return Response.ok().setData(base64);
    }

    public static <T> T readFromResponse(
            Response response,
            String _publicKey,
            String privateKey,
            Class<T> clazz
    ) throws Exception {
        String base64 = (String) response.getData();
        String json2 = new String(
                Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8
        );
        HybridResult result = JSON.parseObject(json2, HybridResult.class);
        String json1 = new String(
                HybridCodec.decrypt(
                        result,
                        KeyGenerator.toPublicKey(_publicKey),
                        KeyGenerator.toPrivateKey(privateKey)),
                StandardCharsets.UTF_8);
        return JSON.parseObject(json1, clazz);
    }
}