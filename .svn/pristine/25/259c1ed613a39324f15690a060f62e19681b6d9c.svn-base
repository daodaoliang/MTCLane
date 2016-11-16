package com.hgits.service;

import com.hgits.hardware.TFI;
import com.hgits.util.VehUtils;
import com.hgits.util.hardware.HardwareHelper;

/**
 * 费显控制类
 *
 * @author Wang Guodong
 */
public class TFIService {

    private final String TRANS_DONE = "    交易成功    ";
    private final String TRANS_FAIL = "    交易失败    ";
    private final String NON_PLATE_NUM = "无车牌";
    private final String NON_PLATE_COL = "无";
    private final String STATION = "入口站：";
    private final String CLASS = "车型：";
    private final String PLATE = "车牌：";
    private final String COLOR = "颜色：";

    private static HardwareHelper helper = new HardwareHelper();
    
    /**
     * 显示收费站中文名称及车型
     *
     * @param stationName 收费站中文名称
     * @param vehClass 车型，如1，2，3，4，5，7，8，9
     */
    public void showStationAndVehClass(String stationName, String vehClass) {
        StringBuilder sb1 = new StringBuilder();//费显第一行显示入口站信息
        sb1.append(STATION).append(stationName);
        StringBuilder sb2 = new StringBuilder();//费显第二行显示车型
        sb2.append(CLASS);
        String str = VehUtils.getCHVehClass(vehClass);
        sb2.append(str);
       
        getTFI().showFourLines(sb1.toString(), sb2.toString(), "", "");//第一行，第二行同时更新
    }

    /**
     * 显示收费站中文名称及车型
     *
     * @param stationName 收费站中文名称
     * @param vehClass 车型，如1，2，3，4，5，7，8，9
     */
    public void showStationAndVehClass(String stationName, int vehClass) {
        showStationAndVehClass(stationName, String.valueOf(vehClass));
    }

    /**
     * 显示车牌颜色
     *
     * @param plateColor 车牌颜色，如蓝，黄，也支持英文缩写U,Y
     */
    public void showVehPlateColor(String plateColor) {
        plateColor = VehUtils.getCHPlateColor(plateColor);
        StringBuilder sb = new StringBuilder();
        if (plateColor == null || plateColor.trim().isEmpty()) {
            plateColor = NON_PLATE_COL;
        }
        sb.append(COLOR).append(plateColor);
        getTFI().showLine(sb.toString(), 4);//车牌颜色在第四行显示
    }

    /**
     * 显示车牌号码
     *
     * @param plateNum 车牌号码
     */
    public void showVehPlateNum(String plateNum) {
        StringBuilder sb = new StringBuilder();
        if (plateNum == null || plateNum.equals("...") || plateNum.trim().isEmpty()) {
            plateNum = NON_PLATE_NUM;//无车牌
        }
        sb.append(PLATE).append(plateNum);
        getTFI().showLine(sb.toString(), 3);//车牌号码在第三行显示
    }
    

    /**
     * 显示交易成功
     */
    public void showTransDone() {
        StringBuilder sb = new StringBuilder();
        sb.append(TRANS_DONE);
        getTFI().showFourLines("", sb.toString(), "", "");//交易成功在第二行显示
    }

    /**
     * 显示交易失败
     */
    public void showTransFail() {
        StringBuilder sb = new StringBuilder();
        sb.append(TRANS_FAIL);
        getTFI().showFourLines("", sb.toString(), "", "");//交易失败在第二行显示
    }
    
    /**
     * 用于动态根据配置的TFI实现类实例化相应对象
     * @return
     */
    private TFI getTFI(){
        TFI tfi = null;
		try {
			tfi = helper.initHardware(TFI.class,helper,"getInstance");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(null == tfi){
				throw new NullPointerException("TFI 接口对象实例化异常!"+tfi);
			}
		}
		return tfi;
    }
}
