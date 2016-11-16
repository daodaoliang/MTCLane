package com.hgits.hardware;

/**
 * 视频卡设备
 * @author zengzb
 *
 */
public interface VideoInstruction extends Runnable {

    
    /**
     * 字符叠加显示车道号
     *
     * @param lane 车道号
     */
    public void showLane(String lane);
    
    /**
     * 清除车型和费额
     */
    public void hideVeh();
    
    /**
     * 清除入口收费站
     */
    public void hideEntry();
    
    /**
     * 清除车牌号
     */
    public void hidePlate();
    
    /**
     * 隐藏车类（车种）
     */
    public void hideVehType();
    
    /**
     * 显示车类（车种）
     *
     * @param vehType 车类（车种）
     */
    public void showVehType(String vehType);
    
    /**
     * 显示收费员ID号
     *
     * @param staffId 收费员ID号
     */
    public void showStaff(String staffId);
    
    /**
     * 只显示车型
     *
     * @param vehClass 车型
     */
    public void showVehClass(String vehClass);
    
    /**
     * 显示车牌号(3位0～9数字)
     *
     * @param plate 车牌号 3位0～9数字,若不是3位数字，需要对其进行提取，取其后三位数字，不足补0
     */
    public void showPlate(String plate);
    
    /**
     * 显示入口站名
     *
     * @param entry 入口收费站为4位数字，取值范围0000～9999，其他的数字无效
     */
    public void showEntry(String entry);
    
    /**
     * 显示车型和费额
     *
     * @param vehClass 车型 2位数字，表示车型
     * @param fare 费额 4位数字，表示费额
     */
    public void showVeh(String vehClass, String fare);
    
    /**
     * 清除收费员ID号
     */
    public void hideStaff();
    
    /**
     * 退出视频字符叠加控制
     */
    public void exit();
    
    /**
     * 获取异常消息
     * @return
     */
    public String getErrorMsg();
}
