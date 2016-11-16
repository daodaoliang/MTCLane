package com.hgits.realTimePath;

import com.hgits.realTimePath.vo.RTPConstant;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    /**
     * 对指定信息进行MD5加密
     *
     * @param s 需要加密的信息
     * @return 加密后的结果
     */
    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes(RTPConstant.CHARSET);
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对指定信息进行MD5加密，并根据参数返回16位或32位加密结果
     *
     * @param plainText 需要加密的信息
     * @param judgeMD 加密结果位数（true 32位，false 16位）
     * @return 加密后的信息
     */
    public static final String MD5(String plainText, boolean judgeMD) {
        try {
            StringBuilder buf = new StringBuilder("");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            if (judgeMD == true) {
                return buf.toString();
            } else {
                return buf.toString().substring(8, 24);
            }
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }

    }
}
