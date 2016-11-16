package com.hgits.hardware;

/**
 * 车牌识别设备
 *
 * @author zengzb
 *
 */
public interface LprService {

    /**
     * 车牌识别加载
     */
    public void start();

    /**
     * 初始化车牌信息
     */
    public void initPlateInfo();

    /**
     * 开始进行车牌识别
     */
    public void startCaptureImg();

    /**
     * 停止进行车牌识别
     */
    public void stopCaptureImg();

    /**
     * 获取车牌识别车牌颜色
     *
     * @return 识别车牌颜色，无识别返回null
     */
    public String getPlateColor();

    /**
     * 获取车牌识别车牌
     *
     * @return 识别车牌号码，无识别返回null
     */
    public String getPlate();

    /**
     * 根据给定的路径创建图片
     *
     * @param vehImgFilePath 给定的图片路径
     * @return 创建图片成功true 失败false
     */
    public boolean createPicture(String vehImgFilePath);

    /**
     * 获取车牌识别设备错误信息
     *
     * @return 车牌识别设备错误信息
     */
    public String getErrorMsg();

    /**
     * 记录上一辆车的车牌号码
     *
     * @param lastPlateNum 上一辆车的车牌号码
     */
    public void recordLastPlateNum(String lastPlateNum);

    /**
     * 开始进行车牌识别,用于线圈触发及车型触发模式
     */
    public void startCaptureImgNew();

    /**
     * 设置CXP板
     *
     * @param cxp
     */
    public void setCxp(CXP cxp);
}
