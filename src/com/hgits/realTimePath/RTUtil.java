package com.hgits.realTimePath;

import com.hgits.realTimePath.vo.RTPConstant;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 针对null值进行处理的工具类
 *
 * @author Wang Guodong
 */
public class RTUtil {

    /**
     * 获取给定参数对应的字符串，若给定参数为null，返回空字符串
     *
     * @param str 待处理的字符串
     * @return 处理后的字符串
     */
    public static String getString(String str) {
        if (str == null) {
            try {
                return new String("".getBytes(RTPConstant.CHARSET), "utf-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(RTUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                return new String(str.getBytes(RTPConstant.CHARSET), "utf-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(RTUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }

    /**
     * 获取给定参数对应的字符串，若给定参数为null，返回空字符串
     *
     * @param date 待处理的日期
     * @param format 日期格式
     * @return 处理后的字符串
     */
    public static String getString(Date date, String format) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        }
    }

    /**
     * 获取给定参数对应的字符串
     *
     * @param param 给定参数
     * @return 处理后的字符串
     */
    public static String getString(short param) {
        return String.valueOf(param);
    }

    /**
     * 获取给定参数对应的字符串
     *
     * @param param 给定参数
     * @return 处理后的字符串
     */
    public static String getString(int param) {
        return String.valueOf(param);
    }

    /**
     * 获取给定参数对应的字符串
     *
     * @param param 给定参数
     * @return 处理后的字符串
     */
    public static String getString(double param) {
        return String.valueOf(param);
    }
}
