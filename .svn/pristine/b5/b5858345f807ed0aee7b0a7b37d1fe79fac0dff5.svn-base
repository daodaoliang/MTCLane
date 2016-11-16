package com.hgits.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 对字符串 数组等进行转换的工具类
 *
 * @author Wang Guodong
 */
public class StringUtil {

    /**
     * 对一串数字字符串进行加密并将加密后的结果返回
     *
     * @param id 需要加密的数字字符串
     * @return 加密后的结果
     */
    public static String encodeID(String id) {
        if (id == null || id.length() == 0) {
            return null;
        }
        int res = 0;
        for (int i = 0; i < id.length(); i++) {
            String num = id.substring(i, i + 1);
            int temp = Integer.parseInt(num);
            if (i % 2 == 0) {
                res = res + temp * 2 / 10 + temp * 2 % 10;
            } else {
                res += temp;
            }
        }
        if (res % 10 == 0) {
            return id + 0;
        } else {
            return id + (10 - res % 10);
        }

    }

    /**
     * 将StringBuilder中指定的相邻重复的字符串合并(若最后四位是1003,不参与运算) 注:只判断偶数位的子字符串
     *
     * @param sb 目标字符串
     * @param str 指定的重复的字符串
     */
    public static void mergeDouble(StringBuilder sb, String str) {
        int length = sb.length();
        if (sb.toString().endsWith("1003")) {
            length = length - 4;
        }
        for (int i = length; i > 3; i -= 2) {
            if (sb.substring(i - 2, i).equals(str) && sb.substring(i - 4, i - 2).equals(str)) {
                sb.delete(i - 2, i);
                i = i - 2;
            }
        }
    }

    /**
     * 在StringBuilder中指定的str字符串后加入str(若最后四位是1003,不参与运算),后面字符依次向后推移 注:只判断偶数位的子字符串
     *
     * @param sb 进行操作的字符串
     * @param str 指定的需要重复的字符串
     */
    public static void doubleStr(StringBuilder sb, String str) {
        int length = sb.length();
        if (sb.toString().endsWith("1003")) {
            length = length - 4;
        }
        for (int i = length; i > 0; i -= 2) {
            if (sb.substring(i - 2, i).equals(str)) {
                sb.insert(i, str);
            }
        }
    }

    /**
     * 将当前的数字字符串中所有数字按照指定的进制进行异或运算,然后将异或运算的结果追加到字符串尾部并返回
     *
     * @param str 进行操作的数字字符串
     * @param radix 数字字符串的进制
     * @return 追加了异或运算结果之后的字符串
     */
    public static String fillBCC(String str, int radix) {
        StringBuilder sb = new StringBuilder(str);
        if (radix == 16) {
            byte temp = getBCC(HexUtils.decode(str));
            sb.append(toLength(Integer.toHexString(temp), 2));
        }
        return sb.toString();
    }

    /**
     * 获取指定字节数组的异或运算结果
     *
     * @param byt 指定的字节数组
     * @return 异或运算的结果
     */
    public static byte getBCC(byte[] byt) {
        byte temp = 0;
        for (byte b : byt) {
            temp ^= b;
        }
        return temp;
    }

    /**
     * 按照指定长度从后取指定字符串的子字符串,若指定字符串长度不够,前面补零
     *
     * @param str 指定的字符串
     * @param length 指定的长度
     * @return 返回的子字符串
     */
    public static String toLength(String str, int length) {
        if (str == null) {
            str = "";
        }
        String res;
        int temp = str.length();
        if (temp >= length) {
            res = str.substring(temp - length, temp);
        } else {
            res = str;
            for (int i = 0; i < length - temp; i++) {
                res = "0" + res;
            }
        }
        return res;
    }

    /**
     * 按照指定长度从后取指定数字的子字符串,若指定数字长度不够,前面补零
     *
     * @param i 指定数字
     * @param length 指定的长度
     * @return 返回的子字符串
     */
    public static String toLength(int i, int length) {
        return toLength(String.valueOf(i), length);
    }

    /**
     * 按照指定长度从后取指定数字的子字符串,若指定数字长度不够,前面补零
     *
     * @param l 指定数字
     * @param length 指定的长度
     * @return 返回的字符串
     */
    public static String toLength(long l, int length) {
        return toLength(String.valueOf(l), length);
    }

    /**
     * 将十进制数字转换为16进制字符串并保留指定长度(长度不够高位补零)
     *
     * @param i 十进制数字
     * @param length 转换之后的字符串的长度
     * @return 转换之后的字符串
     */
    public static String changeIntToHex(int i, int length) {
        String temp = Integer.toHexString(i);
        return toLength(temp, length);
    }

    /**
     * 将十进制数字转换为16进制字符串并保留指定长度(长度不够高位补零)
     *
     * @param l 十进制数字
     * @param length 转换之后的字符串的长度
     * @return 转换之后的字符串
     */
    public static String changeLongToHex(long l, int length) {
        String temp = Long.toHexString(l);
        return toLength(temp, length);
    }

    /**
     * 十,十六进制数字字符串转二进制字符串
     *
     * @param str 需要转换的字符串
     * @param radix 源字符串的进制
     * @param length 转换后的二进制字符串的长度,从后向前截取，若长度不够，高位补零
     * @return 返回的二进制字符串(4位)
     */
    public static String changeStrToBinary(String str, int radix, int length) {
        int i = Integer.parseInt(str, radix);
        String bin = Integer.toBinaryString(i);
        String res = toLength(bin, length);
        return res;
    }

    /**
     * 二进制字符串转十,十六进制数字字符串
     *
     * @param bin 需要转换的二进制字符串
     * @param radix 转换之后的进制
     * @param length 转换之后字符串的长度，若长度不够，高位补零
     * @return 转换之后的字符串
     */
    public static String changeBinaryToStr(String bin, int radix, int length) {
        int i = Integer.parseInt(bin, 2);
        String str = Integer.toString(i, radix);
        String res = toLength(str, length);
        return res;
    }

    /**
     * 十六进制字符串转换为相应的日期字符串
     *
     * @param hex 十六进制字符串如（32 6F 28 E3）
     * @return 日期字符串如（20120923183535）
     */
    public static String hexToDateStr(String hex) {
        hex = hex.replaceAll(" ", "");
        int length = hex.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(changeStrToBinary(hex.substring(i, i + 1), 16, 4));
        }
        String binStr = sb.toString();
        String yy = changeBinaryToStr(binStr.substring(0, 6), 10, 2);
        String MM = changeBinaryToStr(binStr.substring(6, 10), 10, 2);
        String dd = changeBinaryToStr(binStr.substring(10, 15), 10, 2);
        String hh = "";
        String mm = "";
        String ss = "";
        StringBuilder timeSB = new StringBuilder();
        if (length == 8) {
            timeSB.append("20");
            hh = changeBinaryToStr(binStr.substring(15, 20), 10, 2);
            mm = changeBinaryToStr(binStr.substring(20, 26), 10, 2);
            ss = changeBinaryToStr(binStr.substring(26, 32), 10, 2);
        }

        timeSB.append(yy).append(MM).append(dd).append(hh).append(mm).append(ss);
        return timeSB.toString();
    }

    /**
     * 将日期字符串转换为相应的16进制字符串
     *
     * @param date 日期字符串 如（20120923183535）
     * @return 16进制字符串 如（32 6F 28 E3）
     */
    public static String dateStrToHex(String date) {
        String yy = changeStrToBinary(date.substring(2, 4), 10, 6);
        String MM = changeStrToBinary(date.substring(4, 6), 10, 4);
        String dd = changeStrToBinary(date.substring(6, 8), 10, 5);
        String hh = "";
        String mm = "";
        String ss = "";
        if (date.length() == 14) {
            hh = changeStrToBinary(date.substring(8, 10), 10, 5);
            mm = changeStrToBinary(date.substring(10, 12), 10, 6);
            ss = changeStrToBinary(date.substring(12, 14), 10, 6);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(yy).append(MM).append(dd).append(hh).append(mm).append(ss);
        return changeBinaryToStr(sb.toString(), 16, 8).toUpperCase();
    }

    /**
     * 根据传入的数字给出其对应月份的英文简称
     *
     * @param num 数字
     * @return 数字对应的英文月份，不符合要求返回“000”
     */
    public static String getMonth(String num) {
        String str = "000";
        if (num == null) {
            return str;
        }
        if (!num.matches("^[0-9]*$")) {
            return str;
        }
        int a = IntegerUtils.parseString(num);
        switch (a) {
            case 1:
                str = "JAN";
                break;
            case 2:
                str = "FEB";
                break;
            case 3:
                str = "MAR";
                break;
            case 4:
                str = "APR";
                break;
            case 5:
                str = "MAY";
                break;
            case 6:
                str = "JUN";
                break;
            case 7:
                str = "JUL";
                break;
            case 8:
                str = "AUG";
                break;
            case 9:
                str = "SEP";
                break;
            case 10:
                str = "OCT";
                break;
            case 11:
                str = "NOV";
                break;
            case 12:
                str = "DEC";
                break;
        }
        return str;
    }

    /**
     * 产生交易流水号 3位路段号+3位站编码+3位车道号+14位时间+交易顺序号
     *
     * @param roadid 路段号
     * @param stationid 收费站号
     * @param laneid 车道号
     * @param ts 交易时间
     * @param vehCount 交易顺序号
     * @return 交易流水号
     */
    public static String getListNo(String roadid, String stationid, String laneid, Date ts, int vehCount) {
        if (roadid == null) {
            roadid = "000";
        }
        if (stationid == null) {
            stationid = "000";
        }
        if (laneid == null) {
            laneid = "000";
        }
        while (roadid.length() < 3) {
            roadid = "0" + roadid;
        }
        while (stationid.length() < 3) {
            stationid = "0" + stationid;
        }

        if (ts == null) {
            ts = new Date(0);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dhm = sdf.format(ts);
        String str = vehCount + "";
        while (str.length() < 4) {
            str = "0" + str;
        }
        if (str.length() > 4) {
            str = "0000";
        }
        String listNo = roadid + stationid + laneid + dhm + str;
        return listNo;
    }

    /**
     * 根据车道号，交易时间，交易顺序号生成交易流水号
     *
     * @param laneid 车道号
     * @param ts 交易时间
     * @param vehCount 交易顺序号
     * @return 交易流水号
     */
    public static String getListNo(String laneid, Date ts, int vehCount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dhm = sdf.format(ts);
        StringBuilder sb = new StringBuilder();
        sb.append(laneid).append(dhm);
        String temp = String.valueOf(vehCount);
        while (temp.length() < 4) {
            temp = "0" + temp;
        }
        if (temp.length() > 4) {
            temp = "0000";
        }
        sb.append(temp);
        return sb.toString();
    }

    /**
     * 获取字符串中的数字
     *
     * @param str 给定字符串
     * @return 字符串中的数字
     */
    public static String getNumFromStr(String str) {
        String result = str.replaceAll("\\D", "");
        return result;
    }

    /**
     * 将给定的16进制字符串转换为给定长度的10进制数字字符串
     *
     * @param hexStr 16进制字符串
     * @param len 给定长度
     * @return 转换后的10进制字符串
     */
    public static String hexToNum(String hexStr, int len) {
        int i = IntegerUtils.parseHexString(hexStr);
        return StringUtil.toLength(i, 2);
    }

}
