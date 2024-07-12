package com.tv.ads;

import android.util.Base64;

import com.blankj.utilcode.util.StringUtils;

import org.json.JSONObject;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static String rightPadding(String key, String replace, int Length) {
        String strReturn = "";
        String strtemp = "";
        int curLength = key.trim().length();
        if (key != null && curLength > Length) {
            strReturn = key.trim().substring(0, Length);
        } else if (key != null && curLength == Length) {
            strReturn = key.trim();
        } else {
            for (int i = 0; i < (Length - curLength); i++) {
                strtemp = strtemp + replace;
            }
            strReturn = key.trim() + strtemp;
        }
        return strReturn;
    }

    public static String ECB(String data, String key) {
        try {
            key = rightPadding(key, "0", 16);
            byte[] data2 = toBytes(data);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            return new String(cipher.doFinal(data2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String CBC(String data, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return new String(cipher.doFinal(toBytes(data)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isJson(String content) {
        try {
            new JSONObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static byte[] toBytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }


    /**
     * 编码
     */
    private static final String ENCODING = "UTF-8";
    /**
     * 算法定义
     */
    private static final String AES_ALGORITHM = "AES";
    /**
     * 指定填充方式
     */
    private static final String CIPHER_CBC_PADDING = "AES/CBC/PKCS5Padding";
    /**
     * 偏移量(CBC中使用，增强加密算法强度)
     */
    private static final String IV_SEED = "12645678ab345678";

    /**
     * AES_CBC加密
     *
     * @param content 待加密内容
     * @param aesKey  密码
     * @return
     */
    public static String encryptCBC(String content, String aesKey) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        //判断秘钥是否为16位
        if (!StringUtils.isEmpty(aesKey) && aesKey.length() == 16) {
            try {
                //对密码进行编码
                byte[] bytes = aesKey.getBytes(ENCODING);
                //设置加密算法，生成秘钥
                SecretKeySpec skeySpec = new SecretKeySpec(bytes, AES_ALGORITHM);
                // "算法/模式/补码方式"
                Cipher cipher = Cipher.getInstance(CIPHER_CBC_PADDING);
                //偏移
                IvParameterSpec iv = new IvParameterSpec(IV_SEED.getBytes(ENCODING));
                //选择加密
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
                //根据待加密内容生成字节数组
                byte[] encrypted = cipher.doFinal(content.getBytes(ENCODING));
                //返回base64字符串
                return Base64.encodeToString(encrypted, Base64.NO_WRAP);
            } catch (Exception e) {
                return "";
            }

        } else {
            return "";
        }
    }

    /**
     * AES_CBC解密
     *
     * @param content 待解密内容
     * @param aesKey  密码
     * @return
     */
    public static String decryptCBC(String content, String aesKey) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        //判断秘钥是否为16位
        if (!StringUtils.isEmpty(aesKey) && aesKey.length() == 16) {
            try {
                //对密码进行编码
                byte[] bytes = aesKey.getBytes(ENCODING);
                //设置解密算法，生成秘钥
                SecretKeySpec skeySpec = new SecretKeySpec(bytes, AES_ALGORITHM);
                //偏移
                IvParameterSpec iv = new IvParameterSpec(IV_SEED.getBytes(ENCODING));
                // "算法/模式/补码方式"
                Cipher cipher = Cipher.getInstance(CIPHER_CBC_PADDING);
                //选择解密
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

                //先进行Base64解码
                byte[] decodeBase64 = Base64.decode(content, Base64.NO_WRAP);

                //根据待解密内容进行解密
                byte[] decrypted = cipher.doFinal(decodeBase64);
                //将字节数组转成字符串
                return new String(decrypted, ENCODING);
            } catch (Exception e) {
                return "";
            }

        } else {
            return "";
        }
    }

}