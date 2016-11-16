package com.hgits.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 处理null值的工具类
 *
 * @author WangGuodong
 */
public class NullUtils {

    static String defaultDHMPattern = "yyyyMMddHHmmss";

    /**
     * 若str为null，返回空字符串，否则返回str自身
     *
     * @param str 参数
     * @return 返回值
     */
    public static String parseNull(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * 若date为null，返回空字符串，否则将date按照给定pattern进行转换并返回
     *
     * @param date 日期
     * @param pattern 日期格式
     * @return 返回值
     */
    public static String parseNull(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        if (date == null) {
            return "";
        } else {
            return sdf.format(date);
        }
    }

    /**
     * 若date为null，返回空字符串，否则将date按照默认的defaultDHMPattern进行转换并返回
     *
     * @param date 日期
     * @return 返回值
     */
    public static String parseNull(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(defaultDHMPattern);
        if (date == null) {
            return "";
        } else {
            return sdf.format(date);
        }
    }

}
