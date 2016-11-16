package com.hgits.hardware;

/**
 * cxp板设备
 * @author zengzb
 *
 */
public interface CXP extends Runnable{

	/**
     * 雨棚灯变红
     */
	public void changeCanopyLightRed();
	
	/**
     * 自动栏杆放下
     */
    public void lowerRailing();
    
    /**
     * 警报器停止报警
     */
    public void stopAlarm();
    
    /**
     * 获取雨棚等状态标识
     *
     * @return 0 雨棚灯灭 1 雨棚灯红色 2 雨棚等绿色
     */
    public int getCanopyFlag();
    
    /**
     * 检测通道线圈上是否有车
     */
    public boolean checkPassageCoil();
    
    /**
     * 警报器报警
     */
    public void warningAlarm();
    
    /**
     * 检测到达线圈上是否有车
     */
    public boolean checkArriveCoil();
    
    /**
     * 雨棚灯变绿
     */
    public void changeCanopyLightGreen();
    
    /**
     * 自动栏杆抬起
     */
    public void liftRailing();
    
    /**
     * 通行灯变绿
     */
    public void changeTrafficLightGreen();
    
    /**
     * 通行灯变红
     */
    public void changeTrafficLightRed();
    
    /**
     * 获取CXP通讯异常信息
     *
     * @return
     */
    public String getCxpErrorMsg();
    /**
     * 设置到达线圈状态
     * @param flag 
     */
    public void setArrriveCoil(boolean flag);
    /**
     * 设置通道线圈状态
     * @param flag 
     */
    public void setPassageCoil(boolean flag);
    
}
