package com.hgits.util;

import java.math.BigDecimal;

/**
 *
 * @author Wang Guodong
 */
public class DoubleUtils {

    /**
     * 对double数据进行取精度.
     *
     * @param value double数据.
     * @param scale 精度位数(保留的小数位数).
     * @param roundingMode 精度取值方式.如：BigDecimal.ROUND_HALF_UP
     * @return 精度计算后的数据.
     */
    public static double round(double value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(scale, roundingMode);
        double d = bd.doubleValue();
        bd = null;
        return d;
    }

    /**
     * double 相加
     *
     * @param d (多个double参数依次相加)
     * @return
     */
    public static double sum(double... d) {
        BigDecimal bd1 = new BigDecimal(Double.toString(0));
        for (double dou : d) {
            BigDecimal bd = new BigDecimal(Double.toString(dou));
            bd1 = bd1.add(bd);
        }
        return bd1.doubleValue();
    }

    /**
     * double 相减
     *
     * @param d (多个参数依次相减)
     * @return
     */
    public static double sub(double... d) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d[0]));
        for (int i = 1; i < d.length; i++) {
            BigDecimal bd = new BigDecimal(Double.toString(d[i]));
            bd1 = bd1.subtract(bd);
        }
        return bd1.doubleValue();
    }

    /**
     * double 乘法
     *
     * @param d (多个参数依次相乘)
     * @return
     */
    public static double mul(double... d) {
        BigDecimal bd1 = new BigDecimal(Double.toString(1));
        for (double dou : d) {
            BigDecimal bd = new BigDecimal(Double.toString(dou));
            bd1 = bd1.multiply(bd);
        }
        return bd1.doubleValue();
    }

    /**
     * double 除法
     *
     * @param d1
     * @param d2
     * @param scale 四舍五入 小数点位数
     * @return
     */
    public static double div(double d1, double d2, int scale) {
        // 当然在此之前，你要判断分母是否为0，
        // 为0你可以根据实际需求做相应的处理

        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public final static String REGEX_DOUBLE = "(\\d*\\.)?\\d*";

    /**
     * 将浮点型数据字符串转换为其对应的浮点型数据，若为null或非浮点数据字符串，返回0
     *
     * @param str
     * @return
     */
    public static double parseString(String str) {
        if (str != null && str.trim().matches(REGEX_DOUBLE)) {
            return Double.valueOf(str.trim());
        } else {
            return 0;
        }
    }

}
