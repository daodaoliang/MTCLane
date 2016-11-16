package com.hgits.hardware;

import java.util.List;
import java.util.Map;

import com.hgits.vo.Axle;
import com.hgits.vo.AxleGroup;

/**
 * 计重设备
 * @author zengzb
 *
 */
public interface WeighSystem {
	/**
	 * 判断是否为倒车
	 * @return
	 */
	public boolean isReverseFlag();
	
	/**
     * 设置倒车标志
     *
     * @param reverseFlag
     */
    public void setReverseFlag(boolean reverseFlag);
    
    /**
     * 获取缓存中的车辆数
     *
     * @return
     */
    public int getCarNos();
    
    /**
     * 获取单轴信息，单轴轴型，单轴轴重
     *
     * @return
     */
    public List<List<Axle>> getSingleAxleInfo();
    
    /**
     * 经过计重设备的车辆信息
     *
     * @return List<Map<Integer, AxleGroup>>
     */
    public List<Map<Integer, AxleGroup>> getVehList();
    
    /**
     * 车辆信息减一
     */
    public void carNosDecline();
    
    /**
     * 删除第一辆车单轴信息
     */
    public void deleteFirstVehAxleInfo();
    
    /**
     * 删除第一辆车轴组信息
     */
    public void deleteFirstVehCarInfo();
    
    /**
     * 获取车辆通过称重仪速度集合
     *
     * @return
     */
    public List<String> getSpeedList();
    
    /**
     * 删除第一辆车速度信息
     */
    public void deleteFirstVehSpeetInfo();
    
    /**
     * 获取计重仪的错误信息
     *
     * @return 计重仪的错误信息
     */
    public String getErrorMsg();

}
