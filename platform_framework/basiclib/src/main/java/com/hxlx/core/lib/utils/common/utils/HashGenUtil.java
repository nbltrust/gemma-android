package com.hxlx.core.lib.utils.common.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

/**
 * checksum 生成工具类
 */
public class HashGenUtil {
  public static final String TYPE_MD5 = "MD5";
  public static final String TYPE_SHA1 = "SHA-1";
  public static final String TYPE_SHA256 = "SHA-256";
  public static final String TYPE_CRC32 = "crc32";
  private static final String TAG = "HashGenerate";
  final protected static char[] hexDigits =
      {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


  public static String generateHashFromText(String input, String type) {
    String hash = null;
    try {
      byte[] bytes = input.getBytes("UTF-8");

      if (type.equalsIgnoreCase("crc32")) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        long chksum = crc32.getValue();

        hash = Long.toHexString(chksum);
      } else {
        MessageDigest digest = MessageDigest.getInstance(type);
        digest.update(bytes, 0, bytes.length);
        bytes = digest.digest();

        hash = bytesToHex(bytes);
      }


    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    Log.d(TAG, hash);
    return hash;
  }


  public static String checksumMD5(File aFile) throws FileNotFoundException, IOException {
    MessageDigest digest = null;
    String output;

    try {
      digest = MessageDigest.getInstance(TYPE_MD5);
    } catch (NoSuchAlgorithmException e) {
      Log.e(TAG, "Exception while getting digest", e);
      return null;
    }

    InputStream is;
    try {
      is = new FileInputStream(aFile);
    } catch (FileNotFoundException e) {
      Log.e(TAG, "Exception while getting FileInputStream", e);
      return null;
    }

    byte[] buffer = new byte[10240];
    int read;
    try {
      while ((read = is.read(buffer)) > 0) {
        digest.update(buffer, 0, read);
      }

      byte[] hash = digest.digest();
      BigInteger bigInt = new BigInteger(1, hash);
      output = bigInt.toString(16);
      // Fill to 32 chars
      output = String.format("%32s", output).replace(' ', '0');


      return output;
    } catch (IOException e) {
      throw new RuntimeException("Unable to process file for Hash", e);
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        Log.e(TAG, "Exception on closing Hash input stream", e);
      }
    }
  }



  /**
   * 根据算法type，生成checksum
   * 
   * @param aFile
   * @param type
   * @return
   */
  public static String generateHashFromFile(File aFile, String type) {
    MessageDigest digest = null;
    Checksum crc32 = null;
    String output;

    try {
      if (type.equalsIgnoreCase("crc32")) {
        crc32 = new CRC32();
      } else {
        digest = MessageDigest.getInstance(type);
      }
    } catch (NoSuchAlgorithmException e) {
      Log.e(TAG, "Exception while getting digest", e);
      return null;
    }

    InputStream is;
    try {
      is = new FileInputStream(aFile);
    } catch (FileNotFoundException e) {
      Log.e(TAG, "Exception while getting FileInputStream", e);
      return null;
    }

    byte[] buffer = new byte[10240];
    int read;
    try {
      while ((read = is.read(buffer)) > 0) {
        if (type.equalsIgnoreCase("crc32")) {
          crc32.update(buffer, 0, read);
        } else {
          digest.update(buffer, 0, read);
        }
      }
      byte[] hash = null;
      long chksum = 0;

      if (type.equalsIgnoreCase("crc32")) {
        chksum = crc32.getValue();
        output = Long.toHexString(chksum);
      } else {
        hash = digest.digest();
        BigInteger bigInt = new BigInteger(1, hash);
        output = bigInt.toString(16);
        // Fill to 32 chars
        output = String.format("%32s", output).replace(' ', '0');
      }

      return output;
    } catch (IOException e) {
      throw new RuntimeException("Unable to process file for Hash", e);
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        Log.e(TAG, "Exception on closing Hash input stream", e);
      }
    }
  }


  public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexDigits[v >>> 4];
      hexChars[j * 2 + 1] = hexDigits[v & 0x0F];
    }
    return new String(hexChars);
  }



  /**
   * 生成文件的checksum 值
   *
   * @param file
   * @return
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static long checksumCrc32(File file) throws FileNotFoundException, IOException {
    CRC32 checkSummer = new CRC32();
    CheckedInputStream cis = null;

    try {
      cis = new CheckedInputStream(new FileInputStream(file), checkSummer);
      byte[] buf = new byte[128];
      while (cis.read(buf) >= 0) {
        // Just read for checksum to get calculated.
      }
      return checkSummer.getValue();
    } finally {
      if (cis != null) {
        try {
          cis.close();
        } catch (IOException e) {}
      }
    }
  }

}
