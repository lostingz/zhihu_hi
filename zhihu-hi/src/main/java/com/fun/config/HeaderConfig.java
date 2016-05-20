/**
 * Chsi
 * Created on 2016年5月11日
 */
package com.fun.config;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
public class HeaderConfig {
    public static String getSignature(long timeStamp) throws Exception {
        String signature = "password8d5227e0aaaa4797a763ac64e0c3b8com.zhihu.android";
        signature += String.valueOf(timeStamp);
        String secret = "ecbefbf6b17e47ecb9035107866380";
        byte[] rawHmac = HmacSHA1Encrypt(signature, secret);
        return Hex.encodeHexString(rawHmac);
    }

    private static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes("UTF-8");
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance("HmacSHA1");
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);

        byte[] text = encryptText.getBytes("UTF-8");
        // 完成 Mac 操作
        return mac.doFinal(text);
    }
}
