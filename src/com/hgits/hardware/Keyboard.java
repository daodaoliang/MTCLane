package com.hgits.hardware;

import java.util.Map;

import com.hgits.service.KeyBoardService;
import com.hgits.tool.driver.MyKeyAdapter;

/**
 * 收费键盘设备
 *
 * @author zengzb
 *
 */
public interface Keyboard extends Runnable {

    public static final String CONFIRM = "确认";
    public static final String EMER = "紧急";
    public static final String ONDUTY = "上班";
    public static final String OFFDUTY = "下班";
    public static final String GREEN = "绿灯";
    public static final String RED = "红灯";
    public static final String SIMU = "模拟";
    public static final String CART = "卡机";
    public static final String WEIGHT = "计重";
    public static final String IOU = "欠款";
    public static final String CANCEL = "取消";
    public static final String IMG = "图片";
    public static final String MILITARY = "军警";
    public static final String UP = "上";
    public static final String DOWN = "下";
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String FOUR = "4";
    public static final String FIVE = "5";
    public static final String SIX = "6";
    public static final String SEVEN = "7";
    public static final String EIGHT = "8";
    public static final String NINE = "9";
    public static final String ZEROZERO = "00";
    public static final String DOT = ".";
    public static final String SYSTEM = "系统";
    public static final String UNIONPAY = "银联";
    public static final String CH_EN="中/英";
    public static final String CHREG = "[京渝沪津湘皖闽甘云贵琼冀黑豫鄂苏赣吉辽青陕鲁晋川粤浙桂蒙宁藏新警消边广北南山兰成济军海空港澳使领境测挂水电林试临学通]";

    public void setMka(MyKeyAdapter mka);

    /**
     * 获取收费键盘指令对应的按键值(包括电脑键盘按键)
     *
     * @return 返回收费键盘指令对应的按键，如果没有按键，返回null
     */
    public String getMessage();

    /**
     * 向收费键盘发送报警指令
     *
     * @return 成功true，失败false
     */
    public boolean sendAlert();

    /**
     * 等待接到确认指令
     */
    public void waitForConfirm();

    /**
     * 一直等待，直至收费键盘输入指令
     *
     * @return 返回收费键盘输入指令对应的收费键盘按键
     */
    public String waitForOrder();

    /**
     * 获取按键信息，该方法启用后，getMessage方法无效
     *
     * @return 按键信息，无按键返回null
     */
    public String getSuperMessage();

    /**
     * 取消对于getMessage方法的限制
     */
    public void cancelSuperMessage();

    /**
     * 获取异常消息
     *
     * @return
     */
    public String getErrorMsg();

    /**
     * 获取键盘编码转换集合
     *
     * @return
     */
    public Map<String, String> getMap();

    /**
     * 设备键盘服务
     *
     * @param keyBoardService
     */
    public void setKeyBoardService(KeyBoardService keyBoardService);

    /**
     * 根据中文按键获取对应的英文按键
     *
     * @param chKey 中文按键
     * @return 对应的英文按键
     */
    public String getEnKeyByChKey(String chKey);
}
