package com.hgits.hardware;

/**
 * 费显设备
 * @author zengzb
 *
 */
public interface TFI extends Runnable{
	
    /**
     * 全屏显示四行内容,每行最多8个汉字或16个数字
     *
     * @param line1 第一行内容
     * @param line2 第二行内容
     * @param line3 第三行内容
     * @param line4 第四行内容
     */
    public void showFourLines(String line1, String line2, String line3, String line4);
    
    /**
     * 费显按行显示，其他行信息不受影响
     *
     * @param info 需要显示的信息(8个汉字或16个数字)
     * @param lineNum 行号(1,2,3,4)
     */
    public void showLine(String info, int lineNum);

	
	/**
	 * 显示总轴重及 限重
	 * @param totalWeight
	 * @param limitWeight
	 */
	public void showTotalWeightAndLimitWeight(double totalWeight, double limitWeight);
	
    /**
     * 显示支付金额以及找零金额
     *
     * @param total 支付金额
     * @param change 找零金额
     */
	public void showTotalAndChange(String total, String change);
	
	/**
	 * 显示代收通行费
	 * @param collectFee
	 */
	public void showCollectFee(int collectFee);
	
	/**
	 * 发送清屏命令
	 */
	public void clearScreen();
	
	/**
     * 显示"欢迎使用湖南高速公路系统"
     *
     */
    public void showWelcome();
    
    /**
     * 显示车型 (20150410增加用于代替原有显示车型命令)
     *
     * @param vehClass 车型1,2,3,4,5,6,7,8,9
     */
    public void showVehClass(String vehClass);
    
    /**
     * 显示收费额
     *
     * @param fare 收费额
     */
    public void showFare(double fare);
    
    /**
     * 第二行显示通行费，第三行清空，第四行显示代收信息（如“含代收: 999999元”）
     *
     * @param tollFare 总费用
     * @param collectFee 代收费用
     */
    public void showFareAndCollectFee(double tollFare, int collectFee);
    
    /**
     * 返回费显通讯状态
     *
     * @return 异常信息
     */
    public String getErrorMsg();
}
