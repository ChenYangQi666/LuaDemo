package com.govee.pad.base.lua;


import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.spec.KeySpec;

/**
 * @author：YangQi.Chen
 * @date：2023/9/17 04:54
 * @description：AES加解密
 */
public class AESCrypt {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String SALT = "your_salt_here";
    private static final int ITERATION_COUNT = 1000;
    private static final int KEY_LENGTH = 256;
    private static final int IV_LENGTH = 16;

    public static String encrypt(String textToEncrypt, String password) throws Exception {
        byte[] salt = SALT.getBytes();
        byte[] iv = new byte[IV_LENGTH];
        SecretKeySpec keySpec = new SecretKeySpec(generateKey(password, salt), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] encryptedBytes = cipher.doFinal(textToEncrypt.getBytes());
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public static String decrypt(String encryptedText, String password) {
        try {
            byte[] salt = SALT.getBytes();
            byte[] iv = new byte[IV_LENGTH];
            SecretKeySpec keySpec = new SecretKeySpec(generateKey(password, salt), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static byte[] generateKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKey secretKey = factory.generateSecret(keySpec);
        return secretKey.getEncoded();
    }
}
