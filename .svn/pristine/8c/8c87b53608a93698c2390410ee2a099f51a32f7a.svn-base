package com.hgits.util;

/**
 *
 * @author Administrator
 */
public class ShortUtils {

    /**
     * 表示数字的正则表达式
     */
    public static final String REGIX_NUM = "-?[0-9]\\d*";
    /**
     * 表示16进制数字的正则表达式
     */
    public static final String REGIX_HEX = "[a-fA-F0-9]+";

    /**
     * 将字符串转换为相应的数字，若字符串为非数字或为null，转为0
     *
     * @param str 字符串
     * @return 数字
     */
    public static short parseString(String str) {
        if (str != null && str.trim().matches(REGIX_NUM)) {//是否符合正则表达式
            return Short.valueOf(str.trim());
        } else {
            return 0;
        }
    }

    /**
     * 将16进制字符串转换为数字，若字符串不符合16进制规则或为null，返回0
     *
     * @param hex 16进制字符串
     * @return
     */
    public static short parseHexString(String hex) {
        if (hex != null && hex.trim().matches(REGIX_HEX)) {//是否符合正则表达式
            return Short.valueOf(hex.trim(), 16);
        } else {
            return 0;
        }
    }
}
