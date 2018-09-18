package com.cybex.gma.client.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.Stack;

/**
 * Created by wanglin on 2018/9/18.
 */
public class ConvertUtils {


    public static String baseString(int num, int base) {
        if (base > 16) {
            throw new RuntimeException("进制数超出范围，base<=16");
        }
        StringBuffer str = new StringBuffer("");
        String digths = "0123456789ABCDEF";
        Stack<Character> s = new Stack<Character>();
        while (num != 0) {
            s.push(digths.charAt(num % base));
            num /= base;
        }
        while (!s.isEmpty()) {
            str.append(s.pop());
        }
        return str.toString();
    }


    public static String baseNum(String num, int srcBase, int destBase) {

        if (srcBase == destBase) {
            return num;
        }
        String digths = "0123456789ABCDEF";
        char[] chars = num.toCharArray();
        int len = chars.length;
        if (destBase != 10) {//目标进制不是十进制 先转化为十进制
            num = baseNum(num, srcBase, 10);
        } else {
            int n = 0;
            for (int i = len - 1; i >= 0; i--) {
                n += digths.indexOf(chars[i]) * Math.pow(srcBase, len - i - 1);
            }
            return n + "";
        }
        return baseString(Integer.valueOf(num), destBase);
    }


    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }


    /**
     * 十六进制转换字符串
     *
     * @param hexStr Byte字符串(Byte之间无分隔符 如:[616C6B])
     * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * bytes转换成十六进制字符串
     *
     * @param b byte数组
     * @return String 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }


    /**
     * 合并byte[]数组
     *
     * @param byte_1
     * @param byte_2
     * @return
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }


    /**
     * 截取byte数组   不改变原数组
     *
     * @param b 原数组
     * @param off 偏差值（索引）
     * @param length 长度
     * @return 截取后的数组
     */
    public static byte[] subByte(byte[] b, int off, int length) {
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }


    /**
     * 字节数组转换成16进制字符串
     *
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    public static String hexEncode(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        return new String(Hex.encodeHex(bytes)); //Hex.encodeHex(bytes, false)
    }

    /**
     * 16进制字符串转换成字节数组
     *
     * @param hexStr 16进制字符串
     * @return 字节数组
     */
    public static byte[] hexDecode(String hexStr) {
        if (hexStr == null || "".equals(hexStr)) {
            return null;
        }
        try {
            char[] cs = hexStr.toCharArray();
            return Hex.decodeHex(cs);
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 字节数组转为16进制字符串
     *
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    public static String byteArray2HexString(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        //先把byte[] 转换维char[]，再把char[]转换为字符串
        char[] chars = new char[bytes.length * 2]; // 每个byte对应两个字符
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (int i = 0, j = 0; i < bytes.length; i++) {
            chars[j++] = hexDigits[bytes[i] >> 4 & 0x0f]; // 先存byte的高4位
            chars[j++] = hexDigits[bytes[i] & 0x0f]; // 再存byte的低4位
        }

        return new String(chars);
    }

    /**
     * 16进制字符串转字节数组
     *
     * @param hexString 16进制字符串
     * @return 字节数组
     */
    public static byte[] hexString2ByteArray(String hexString) {
        if (hexString == null || "".equals(hexString)) {
            return null;
        }
        //先把字符串转换为char[]，再转换为byte[]
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] bytes = new byte[length];
        String hexDigits = "0123456789abcdef";
        for (int i = 0; i < length; i++) {
            int pos = i * 2; // 两个字符对应一个byte
            int h = hexDigits.indexOf(hexChars[pos]) << 4; // 注1
            int l = hexDigits.indexOf(hexChars[pos + 1]); // 注2
            if (h == -1 || l == -1) { // 非16进制字符
                return null;
            }
            bytes[i] = (byte) (h | l);
        }
        return bytes;
    }


    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }


}
