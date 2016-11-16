package com.hgits.hardware;

import java.io.UnsupportedEncodingException;

/**
 * 打印机设备
 * @author zengzb
 *
 */
public interface Printer {

	/**
     * 获取打印机错误信息
     *
     * @return 打印机错误信息
     */
    public String getPrintError();
    
    /**
     * 要打印的车辆信息
     *
     * @param limit_weight 限重
     * @param weight 车重
     * @param laneId 车道
     * @param staffId 员工号
     * @param type 车型
     * @param cash 车费
     * @param entrance 入口站
     * @param exit 出口站
     * @param city 城市
     * @param cityFee 城市通行费
     * @param plate 车牌
     * @throws UnsupportedEncodingException
     */
    public void print(String limit_weight, String weight, String laneId,
            String staffId, String type, String cash,
            String entrance, String exit, String city,String cityFee, String plate)
            		throws UnsupportedEncodingException;
    
}
