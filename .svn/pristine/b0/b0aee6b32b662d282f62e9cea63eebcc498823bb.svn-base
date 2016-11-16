package com.hgits.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import ui.ExtJFrame;

import com.hgits.control.FlowControl;
import com.hgits.control.FunctionControl;
import com.hgits.control.LogControl;
import com.hgits.hardware.Keyboard;
import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;
import org.apache.commons.lang.StringUtils;

/**
 * 全车牌键盘服务类
 *
 * @author 王国栋
 */
public class FullKeyboardService {

    private final Map<String, String> colorMap = new HashMap<>();
    private final Map<String, String> plateMap = new HashMap<>();
    private final Map<String, String> chMap = new HashMap<>();//记录中文转换键值的集合
    private FlowControl fc;
    private final int chEnInterval;//判断连续两次按【中/英】键的时间间隔
    private final String CHKeyBoard_1 = "湘,粤,鄂,鲁,豫,赣,冀,皖,晋,沪,京,吉,蒙,宁,渝,藏,琼,闽,苏,青,川,辽,甘,新,云,浙,陕,津,,贵,,";
    private final String CHKeyBoard_2 = "警,消,边,广,北,南,沈,兰,成,济,军,海,空,港,澳,使,领,境,挂,水,电,林,,挂,试,临,学,通,,,,";

    public void setFc(FlowControl fc) {
        this.fc = fc;
    }

    public FullKeyboardService() {
        colorMap.put("1", "U");
        colorMap.put("2", "Y");
        colorMap.put("3", "W");
        colorMap.put("4", "G");
        colorMap.put("5", "R");
        colorMap.put("6", "B");
        colorMap.put("0", "");
        colorMap.put("00", null);
        for (int i = 0; i < 10; i++) {
            String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE, "plate" + i, "0,0,0,0,0,0,0,0,0,0");
            String[] buffer = str.split(",");
            for (int j = 0; j < 10; j++) {
                plateMap.put(i + "" + j, buffer[j]);
            }
        }
        //获取判断连续两次按【中/英】键的时间间隔
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "ChEnInterval", "500");
        int temp = IntegerUtils.parseString(str);
        if (temp <= 0) {
            temp = 500;
        }
        chEnInterval = temp;
        //获取中文按键转换对应键值对
        String str1 = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE, "CHKeyBoard_1", CHKeyBoard_1);
        String str2 = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE, "CHKeyBoard_2", CHKeyBoard_2);
        if (str1 != null && str2 != null) {
            str1 = str1.replaceAll("，", ",");
            str2 = str2.replaceAll("，", ",");
            String[] buffer1 = StringUtils.splitPreserveAllTokens(str1, ",");
            String[] buffer2 = StringUtils.splitPreserveAllTokens(str2, ",");
            int len1 = buffer1.length;
            int len2 = buffer2.length;
            if (len1 == len2) {
                for (int i = 0; i < len1; i++) {
                    chMap.put(buffer1[i], buffer2[i]);
                }
            }
        }

    }

    /**
     * 根据提供的数字获得其对应的车牌颜色
     *
     * @param num 数字
     * @return 车牌颜色
     */
    public String getPlateColor(String num) {
        return colorMap.get(num);
    }

    /**
     * 根据提供的数字获得其对应的车牌号码
     *
     * @param num 数字
     * @return 车牌号码
     */
    public String getPlateNum(String num) {
        return plateMap.get(num);
    }

    /**
     * 车牌颜色确认后一直到全车牌确认的流程，结束后返回收费员确认全车牌
     *
     * @author 王国栋 2014-8-25
     * @param extJFrame 收费界面
     * @param keyboard 收费键盘
     * @param lprPlate//车牌识别获取车牌
     * @param plate//收费员确认车牌
     * @param order 收费员键入指令
     * @param autoFlag 自助发卡人工确认车牌标识
     * @return 全车牌
     */
    public String getFullPlate(ExtJFrame extJFrame, Keyboard keyboard, String lprPlate, String plate, String order, boolean autoFlag) {
        int chFlag = 1;//全键盘中文1（1），中文2（2），英文（0）切换标识
        StringBuilder sb = new StringBuilder();//用于记录收费员选择全车牌时键入的按键
        StringBuffer plateNum; //用于记录收费员从全车牌界面选择的车牌号码
        StringBuffer cursorBuffer;//对应游标
        if (plate != null) {
            plateNum = new StringBuffer(plate);
            cursorBuffer = new StringBuffer(plate);
        } else {
            plateNum = new StringBuffer();
            cursorBuffer = new StringBuffer();
        }
        String str;
        long s1 = 0;
        long start = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
            }
            try {
                extJFrame.updateCursor(cursorBuffer.toString());                //更新游标
            } catch (UnsupportedEncodingException ex) {
                LogControl.logInfo("更新游标", ex);
            }
            extJFrame.updateKeyedPlate(plateNum.toString());                    //更新键入车牌
            if (order == null) {
//                str = keyboard.waitForOrder();
                str = keyboard.getMessage();
            } else {
                str = order;
                order = null;
            }
            long now = System.currentTimeMillis();
            String inter = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "autoPlateInterva11", "15");
            int interval = IntegerUtils.parseString(inter);
            interval = interval < 1 ? 1 : interval;//最少1秒的时间间隔
            if (now < start) {//当前时间小于开始时间，重新开始计时
                start = now;
            } else if (now - start > interval * 1000 && autoFlag) {//自助发卡人工确认车牌时，若超过规定时间未进行人工输入，自动确认
                start = now;
                str = "确认";
            }
            if (str == null) {
                continue;
            } else {
                start = now;
            }
            if (".".equals(str)) {                                              //全键盘输入过程中，仍然支持用户通过输入"."来复制车牌识别结果或者无车牌识别结果时直接输入"..."
                s1 = 0;
                if (sb.length() == 1) {
                    keyboard.sendAlert();
                } else {
                    if (lprPlate == null || lprPlate.length() == 0) {           //无车牌识别时输入...为车牌
                        if (plateNum.length() >= 8) {
                            keyboard.sendAlert();
                            continue;
                        }
                        plateNum.append(str);
                        cursorBuffer.append(str);
                    } else {                                                    //有车牌识别，此时复制视频识别车牌
                        int len = cursorBuffer.length();
                        int lprLen = lprPlate.length();
                        int plateLen = plateNum.length();
                        if (len > lprLen) {
                            keyboard.sendAlert();
                            continue;
                        } else if (len == lprLen) {
                            cursorBuffer.replace(len - 1, len, lprPlate.substring(len - 1, len));//复制识别车牌替换收费员确认车牌
                            plateNum.replace(len - 1, len, lprPlate.substring(len - 1, len));
                        } else if (len < lprLen) {
                            if (len == plateLen) {
                                cursorBuffer.append(lprPlate.substring(len, len + 1));
                                plateNum.append(lprPlate.substring(len, len + 1));
                            } else if (len > plateLen) {
                            } else if (len < plateLen) {
                                if (len > 0) {
                                    cursorBuffer.replace(len, len + 1, lprPlate.substring(len, len + 1));//复制识别车牌替换收费员确认车牌
                                    plateNum.replace(len, len + 1, lprPlate.substring(len, len + 1));
                                } else if (len == 0) {
                                    cursorBuffer.append(lprPlate.substring(0, 1));
                                    plateNum.replace(0, 1, lprPlate.substring(0, 1));
                                }

                            }
                        }
                    }
                }
            } else if (str.matches("[0-9]")) {                                  //用户通过数字组合在全键盘图片中选择其对应汉字
                s1 = 0;
                String temp = "";
                if (FunctionControl.isFullKeyboardFunActive()) {
                    temp = str;
                } else {
                    sb.append(str);
                    if (sb.length() == 1) {                                         //用户键入数字组合的第一个数字
                        extJFrame.updateFullPlateOption(str);
                        continue;
                    } else if (sb.length() == 2) {                                  //用户键入数字组合的第二个数字
                        temp = getPlateNum(sb.toString());
                        if (switchPlateColor(temp) != null && sb.toString().startsWith("9")) {//全车牌输入时用户可以通过数字组合重新选择车牌颜色
                            temp = switchPlateColor(temp);
                            extJFrame.updateKeyedPlateCol(temp);
                            fc.setVehKeyedPlateColor(temp);
                            sb.delete(0, 2);
                            extJFrame.reInitFullPlateOption();
                            continue;
                        }
                    }
                }
                if (plateNum.length() >= 8) {//增加车牌长度限制，不超过8位
                    keyboard.sendAlert();
                    sb.delete(0, sb.length());
                    extJFrame.reInitFullPlateOption();
                    continue;
                }
                if (plateNum.length() == 0 || cursorBuffer.length() == plateNum.length()) {
                    plateNum.append(temp);
                } else if (cursorBuffer.length() < plateNum.length()) {
                    plateNum.replace(cursorBuffer.length(), cursorBuffer.length() + 1, temp);
                }
                cursorBuffer.append(temp);
                sb.delete(0, sb.length());
                extJFrame.reInitFullPlateOption();

            } else if ("确认".equals(str)) {//收费员输入完毕确认全车牌
                s1 = 0;
//                if (sb.length() == 1 || plateNum.length() < 3) {//车牌号码最少三位（无车牌是收费员可输入“...”）
                if (plateNum.length() < 3) {//车牌号码最少三位（无车牌是收费员可输入“...”）
                    keyboard.sendAlert();
                } else {
                    extJFrame.reInitFullPlateOption();
                    extJFrame.cleanCursor();
                    break;
                }
            } else if ("取消".equals(str)) {                                    //收费员通过取消键删除游标所对应的车牌号码或者全键盘车牌的选择
                s1 = 0;
                int sblen = sb.length();
                int plen = plateNum.length();
                if (sblen == 1) {
                    extJFrame.reInitFullPlateOption();
                    sb.delete(0, 1);
                } else if (sblen == 0 && plen == 0) {                           //退回车牌颜色选择界面
                    return null;
                } else if (sblen == 0 && plen > 0) {                            //不在全车牌号码选择过程中，并且所选择的全车牌号码不为空
                    int len = cursorBuffer.length();
                    if (len == plen) {
                        plateNum.deleteCharAt(plen - 1);
                        cursorBuffer.deleteCharAt(len - 1);
                    } else if (len == 0) {
                        plateNum.deleteCharAt(0);
                    } else if (len < plen) {
                        cursorBuffer.deleteCharAt(len - 1);
                        plateNum.deleteCharAt(len);
                    }
                }
            } else if (str.equals("上")) {                                      //通过上方向键移动光标向前
                s1 = 0;
                if (sb.length() == 0 && plateNum.length() > 1) {
                    int len = cursorBuffer.length();
                    if (len >= 1) {
                        cursorBuffer.delete(len - 1, len);
                    } else if (len == 0) {
                        keyboard.sendAlert();
                    }
                } else {
                    keyboard.sendAlert();
                }
            } else if (str.equals("下")) {                                      //通过下方向键移动光标向后
                s1 = 0;
                if (sb.length() == 0 && plateNum.length() >= 1) {
                    int len = cursorBuffer.length();
                    if (len < plateNum.length()) {
                        cursorBuffer.append(plateNum.charAt(len));
                    } else {
                        cursorBuffer.delete(0, cursorBuffer.length());//游标移动到最后时继续向后可移动到首位
                        keyboard.sendAlert();
                    }
                } else {
                    keyboard.sendAlert();
                }
            } else if ("军警".equals(str)) {
                s1 = 0;
                fc.runMilitaryVeh();
            } else if (str.matches(Keyboard.CHREG) && FunctionControl.isFullKeyboardFunActive()) {//输入按键为中文
                s1 = 0;
                //直接输入中文时不会根据中文输入修改车牌颜色
                String temp = "";
                if (chFlag == 1) {//获取中文1对应的按键
                    temp = str;
                } else if (chFlag == 0) {//输入为英文
                    temp = keyboard.getEnKeyByChKey(str);
                } else if (chFlag == 2) {
                    temp = chMap.get(str);//获取中文2对应的按键
                    if (temp == null) {
                        temp = str;
                    }
                }
                if (plateNum.length() >= 8) {//增加车牌长度限制，不超过8位
                    keyboard.sendAlert();
                    extJFrame.reInitFullPlateOption();
                    continue;
                }
                if (plateNum.length() == 0 || cursorBuffer.length() == plateNum.length()) {
                    plateNum.append(temp);
                } else if (cursorBuffer.length() < plateNum.length()) {
                    plateNum.replace(cursorBuffer.length(), cursorBuffer.length() + 1, temp);
                }
                cursorBuffer.append(temp);
                sb.delete(0, sb.length());
                extJFrame.reInitFullPlateOption();
            } else if (str.matches(Keyboard.CH_EN) && FunctionControl.isFullKeyboardFunActive()) {//中英文切换
                long s2 = System.currentTimeMillis();
                if (s2 - s1 < chEnInterval) {//快速按两次中英文切换
                    extJFrame.showFullPlateOption2();//显示第二份中文输入界面
                    chFlag = 2;
                } else {
                    s1 = s2;
                    if (chFlag == 1) {//修改中英文输入标识
                        chFlag = 0;
                        extJFrame.showFullPlateOption3();//显示英文输入界面
                    } else if (chFlag == 0) {//修改中英文输入标识
                        chFlag = 1;
                        extJFrame.showFullPlateOption1();//显示英文输入界面
                    } else if (chFlag == 2) {//修改中英文输入标识
                        chFlag = 1;
                        extJFrame.showFullPlateOption1();//显示英文输入界面
                    }
                }
            } else {
                s1 = 0;
                keyboard.sendAlert();
            }
        }
        return plateNum.toString();
    }

    /**
     * 将车牌颜色从汉字转换为其相对应的英文字母如蓝->B
     *
     * @param plateColor 需要转换的车牌颜色汉字 如：黄
     * @return 转换后的车牌颜色
     */
    public String switchPlateColor(String plateColor) {
        if (plateColor == null) {
            return null;
        }
        switch (plateColor) {
            case "蓝":
                return "U";
            case "黄":
                return "Y";
            case "白":
                return "W";
            case "黑":
                return "B";
            case "绿":
                return "G";
            case "红":
                return "R";
            default:
                return null;

        }
    }
}
