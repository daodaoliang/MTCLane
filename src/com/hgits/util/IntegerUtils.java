package com.hgits.util;



/**
 * Integer工具类
 *
 * @author Administrator
 *
 */
public class IntegerUtils {

    /**
     * 表示数字的正则表达式
     */
    public static final String REGIX_NUM = "-?[0-9]\\d*";
    public static final String REGIX_HEX = "[a-fA-F0-9]+";

    /**
     * 将字符串转换成为数字
     *
     * @param s 字符串
     * @return 如果没有则返回null，有则返回对应的数字表示
     */
    public static Integer parseInteger(String s) {
        return StringUtils.isEmpty(s) ? null : Integer.valueOf(s.trim());
    }

    /**
     * 将字符串转换为相应的数字，若字符串为非数字或为null，转为0
     *
     * @param str 字符串
     * @return 数字
     */
    public static int parseString(String str) {
        if (str != null && str.trim().matches(REGIX_NUM)) {//是否符合数字正则表达式
            return Integer.valueOf(str.trim());
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
    public static int parseHexString(String hex) {
        if (hex != null && hex.trim().matches(REGIX_HEX)) {//是否符合数字正则表达式
            return Integer.valueOf(hex.trim(), 16);
        } else {
            return 0;
        }
    }

}
