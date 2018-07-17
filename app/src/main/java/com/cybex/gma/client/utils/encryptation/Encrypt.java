package com.cybex.gma.client.utils.encryptation;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encrypt
 */
public class Encrypt {

    // Llave Simetrica es un String de tamaño múltiplo de 8
    // en este caso si es de tamaño 32 nos permite AES-256 (32 * 8)
    private String SYMMETRI_KEY = "7x-_2bIjMSp2IYCxGw4&o5K1xG#4X#SQ";
    private String SHA_KEY = "7x-_2bIjMSp2IYCxGw4&o5K1xG#4X#SQ";
    private static byte[] iv = "0000000000000000".getBytes();

    public Encrypt() {
    }

    void setAESKey(String key) {
        SYMMETRI_KEY = key;
    }

    void setSHAKey(String key) {
        SHA_KEY = key;
    }

    /**
     * Ecrypt AES.
     *
     * @param message the message
     * @return the string
     */
    public String encryptAES(String message) {
        try {
            byte[] input = message.getBytes("utf-8");

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] thedigest = md.digest(SYMMETRI_KEY.getBytes("utf-8"));
            SecretKeySpec skc = new SecretKeySpec(thedigest, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skc, new IvParameterSpec(iv));

            byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
            int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
            ctLength += cipher.doFinal(cipherText, ctLength);
            return bytesToHex(cipherText);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypt AES.
     *
     * @param encrypted the message
     * @return the string
     */
    public String decryptAES(String encrypted) throws EncryptException {

        try {
            byte[] keyb = SYMMETRI_KEY.getBytes("utf-8");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] thedigest = md.digest(keyb);
            SecretKeySpec skey = new SecretKeySpec(thedigest, "AES");
            Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            dcipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));

            byte[] clearbyte = dcipher.doFinal(hexStringToByteArray(encrypted));
            return new String(clearbyte);

        } catch (Exception e) {
            throw new EncryptException(e.toString());
        }
    }

    /**
     * Ecrypt MD5.
     *
     * @param s the string to encript
     * @return the string
     */
    public String encryptMD5(String s) throws EncryptException {
        try {

            MessageDigest md;
            byte[] buffer, digest;
            String hash = "";

            buffer = s.getBytes();
            md = MessageDigest.getInstance("MD5");
            md.update(buffer);
            digest = md.digest();

            for (byte aux : digest) {
                int b = aux & 0xff;
                if (Integer.toHexString(b).length() == 1) { hash += "0"; }
                hash += Integer.toHexString(b);
            }

            return new String(Base64.encode(hash.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP));

        } catch (NoSuchAlgorithmException e) {
            throw new EncryptException(e.toString());
        }
    }


    /**
     * Ecrypt SHA256.
     *
     * @param s the string to encript
     * @return the string
     */
    public String encryptSHA256(String s) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(s.getBytes());
        return bytesToHex(md.digest());
    }

    public String encryptSHA256WithKey(String s) {

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(SHA_KEY.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] s53 = sha256_HMAC.doFinal(s.getBytes());
            String hash = Base64.encodeToString(s53, Base64.DEFAULT);
            Log.e("beadict", hash);
            return hash;
        } catch (Exception e) {
            System.out.println("Error");
            return null;
        }
    }

    public String encryptSHA256SimpleWithKey(String key, String s) {

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] s53 = sha256_HMAC.doFinal(s.getBytes());
            String hash = Base64.encodeToString(s53, Base64.DEFAULT);
            Log.e("beadict", hash);
            return hash;
        } catch (Exception e) {
            System.out.println("Error");
            return null;
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) { result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1)); }
        return result.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}
