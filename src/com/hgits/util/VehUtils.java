package com.hgits.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 车辆工具类
 *
 * @author Wang Guodong
 */
public class VehUtils {

    private static final Map<String, String> vehClassMap = new HashMap();//车型对应集合
    private static final Map<String, String> plateColMap = new HashMap();//车牌颜色对应集合

    static {
        vehClassMap.put("1", "一型车");
        vehClassMap.put("2", "二型车");
        vehClassMap.put("3", "三型车");
        vehClassMap.put("4", "四型车");
        vehClassMap.put("5", "五型车");
        vehClassMap.put("7", "货车");
        vehClassMap.put("8", "绿通车");
        vehClassMap.put("9", "绿通车");
        vehClassMap.put("一型车", "一型车");
        vehClassMap.put("二型车", "二型车");
        vehClassMap.put("三型车", "三型车");
        vehClassMap.put("四型车", "四型车");
        vehClassMap.put("五型车", "五型车");
        vehClassMap.put("货车", "货车");
        vehClassMap.put("绿通车", "绿通车");
        plateColMap.put("U", "蓝");
        plateColMap.put("Y", "黄");
        plateColMap.put("R", "红");
        plateColMap.put("B", "黑");
        plateColMap.put("W", "白");
        plateColMap.put("G", "绿");
        plateColMap.put("蓝", "蓝");
        plateColMap.put("黄", "黄");
        plateColMap.put("红", "红");
        plateColMap.put("黑", "黑");
        plateColMap.put("白", "白");
        plateColMap.put("绿", "绿");

    }

    /**
     * 获取汉字车型，如1转换为一型车
     *
     * @param vehClass 数字车型，如1
     * @return 汉字车型，如一型车
     */
    public static String getCHVehClass(String vehClass) {
        String chVehClass = vehClassMap.get(vehClass);
        if (chVehClass == null) {
            chVehClass = "未知车型";
        }
        return chVehClass;
    }

    /**
     * 获取中文车牌颜色
     *
     * @param plateColor 车牌颜色英文缩写如U,Y,B
     * @return 中文车牌颜色，如蓝，黄，黑
     */
    public static String getCHPlateColor(String plateColor) {
        String chPlateColor = plateColMap.get(plateColor);
        if (chPlateColor == null) {
            chPlateColor = "无";
        }
        return chPlateColor;
    }

}
