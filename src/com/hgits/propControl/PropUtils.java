package com.hgits.propControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Wang Guodong
 */
public class PropUtils {

    private static final List<String> templateList = new ArrayList();//记录模板参数文件的集合
    private static final List<String> actualList = new ArrayList();//记录实际参数文件的集合
    private static final Map<String, String> map = new HashMap();//记录模板参数文件和实际参数文件对应关系的集合
    private static final String TEMPLATE_DIR = "/com/hgits/propControl/template/";
    private static final String ACTUAL_DIR = "resource/";
    public static final String PROP_MTCCONFIG = "MTCConfig.properties";
    public static final String PROP_MTCLANE = "MTCLane.properties";
    public static final String PROP_MTCLANE_COMM = "MTCLaneComm.properties";
    public static final String PROP_MTCLANE_CONSTANT = "MTCLaneConstant.properties";
    public static final String PROP_MTCLANE_ETC = "MTCLaneETC.properties";
    public static final String PROP_MTCLANE_FUNCTION = "MTCLaneFunction.properties";
    public static final String PROP_MTCLANE_RTP = "MTCLaneRTP.properties";
    public static final String PROP_MTCLANE_SERVER = "MTCLaneServer.properties";
    public static final String PROP_MTCLANE_TEST = "MTCLaneTest.properties";
    public static final String PROP_MTCLANE_KEYBOARD = "MTCLaneKeyboard.properties";
    public static final String PROP_SOCKET = "socket.properties";
    public static final String PROP_MTCLANE_LPR = "MTCLaneLPR.properties";
    public static final String PROP_LANE = "hardware/lane.properties";
    public static final String PROP_KEYBOARD = "template/keyboard.properties";
    public static final String PROP_WANGLUKEYBOARD = "template/WangluKeyboard.properties";
    public static final String PROP_TENGHAOKEYBOARD = "template/TengHaoKeyboard.properties";

    static {
        templateList.add(TEMPLATE_DIR + PROP_KEYBOARD);
        templateList.add(TEMPLATE_DIR + PROP_LANE);
        templateList.add(TEMPLATE_DIR + PROP_MTCCONFIG);
        templateList.add(TEMPLATE_DIR + PROP_MTCLANE);
        templateList.add(TEMPLATE_DIR + PROP_MTCLANE_COMM);
        templateList.add(TEMPLATE_DIR + PROP_MTCLANE_CONSTANT);
        templateList.add(TEMPLATE_DIR + PROP_MTCLANE_ETC);
        templateList.add(TEMPLATE_DIR + PROP_MTCLANE_FUNCTION);
        templateList.add(TEMPLATE_DIR + PROP_MTCLANE_LPR);
        templateList.add(TEMPLATE_DIR + PROP_MTCLANE_RTP);
        templateList.add(TEMPLATE_DIR + PROP_MTCLANE_SERVER);
        templateList.add(TEMPLATE_DIR + PROP_MTCLANE_TEST);
        templateList.add(TEMPLATE_DIR + PROP_SOCKET);
        templateList.add(TEMPLATE_DIR + PROP_MTCLANE_KEYBOARD);
        templateList.add(TEMPLATE_DIR + PROP_WANGLUKEYBOARD);
        templateList.add(TEMPLATE_DIR + PROP_TENGHAOKEYBOARD);
        actualList.add(ACTUAL_DIR + PROP_KEYBOARD);
        actualList.add(ACTUAL_DIR + PROP_LANE);
        actualList.add(ACTUAL_DIR + PROP_MTCCONFIG);
        actualList.add(ACTUAL_DIR + PROP_MTCLANE);
        actualList.add(ACTUAL_DIR + PROP_MTCLANE_COMM);
        actualList.add(ACTUAL_DIR + PROP_MTCLANE_CONSTANT);
        actualList.add(ACTUAL_DIR + PROP_MTCLANE_ETC);
        actualList.add(ACTUAL_DIR + PROP_MTCLANE_FUNCTION);
        actualList.add(ACTUAL_DIR + PROP_MTCLANE_LPR);
        actualList.add(ACTUAL_DIR + PROP_MTCLANE_RTP);
        actualList.add(ACTUAL_DIR + PROP_MTCLANE_SERVER);
        actualList.add(ACTUAL_DIR + PROP_MTCLANE_TEST);
        actualList.add(ACTUAL_DIR + PROP_SOCKET);
        actualList.add(ACTUAL_DIR + PROP_MTCLANE_KEYBOARD);
        actualList.add(ACTUAL_DIR + PROP_WANGLUKEYBOARD);
        actualList.add(ACTUAL_DIR + PROP_TENGHAOKEYBOARD);
        map.put(TEMPLATE_DIR + PROP_KEYBOARD, ACTUAL_DIR + PROP_KEYBOARD);
        map.put(TEMPLATE_DIR + PROP_LANE, ACTUAL_DIR + PROP_LANE);
        map.put(TEMPLATE_DIR + PROP_MTCCONFIG, ACTUAL_DIR + PROP_MTCCONFIG);
        map.put(TEMPLATE_DIR + PROP_MTCLANE, ACTUAL_DIR + PROP_MTCLANE);
        map.put(TEMPLATE_DIR + PROP_MTCLANE_COMM, ACTUAL_DIR + PROP_MTCLANE_COMM);
        map.put(TEMPLATE_DIR + PROP_MTCLANE_CONSTANT, ACTUAL_DIR + PROP_MTCLANE_CONSTANT);
        map.put(TEMPLATE_DIR + PROP_MTCLANE_ETC, ACTUAL_DIR + PROP_MTCLANE_ETC);
        map.put(TEMPLATE_DIR + PROP_MTCLANE_FUNCTION, ACTUAL_DIR + PROP_MTCLANE_FUNCTION);
        map.put(TEMPLATE_DIR + PROP_MTCLANE_LPR, ACTUAL_DIR + PROP_MTCLANE_LPR);
        map.put(TEMPLATE_DIR + PROP_MTCLANE_RTP, ACTUAL_DIR + PROP_MTCLANE_RTP);
        map.put(TEMPLATE_DIR + PROP_MTCLANE_SERVER, ACTUAL_DIR + PROP_MTCLANE_SERVER);
        map.put(TEMPLATE_DIR + PROP_MTCLANE_TEST, ACTUAL_DIR + PROP_MTCLANE_TEST);
        map.put(TEMPLATE_DIR + PROP_SOCKET, ACTUAL_DIR + PROP_SOCKET);
        map.put(TEMPLATE_DIR + PROP_MTCLANE_KEYBOARD, ACTUAL_DIR + PROP_MTCLANE_KEYBOARD);
        map.put(TEMPLATE_DIR + PROP_WANGLUKEYBOARD, ACTUAL_DIR + PROP_WANGLUKEYBOARD);
        map.put(TEMPLATE_DIR + PROP_TENGHAOKEYBOARD, ACTUAL_DIR + PROP_TENGHAOKEYBOARD);
    }

    /**
     * 获取模板参数文件集合
     *
     * @return 模板参数文件集合
     */
    public static List<String> getTemplateList() {
        return templateList;
    }

    /**
     * 获取实际参数文件集合
     *
     * @return 实际参数文件集合
     */
    public static List<String> getActualList() {
        return actualList;
    }

    /**
     * 根据模板文件获取其对应的参数文件
     *
     * @param str 模板文件
     * @return 参数文件
     */
    public static String getActualByTemplate(String str) {
        if (str == null) {
            return null;
        } else {
            return map.get(str);
        }
    }
}
