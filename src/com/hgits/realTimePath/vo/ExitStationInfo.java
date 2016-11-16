package com.hgits.realTimePath.vo;

import net.sf.json.JSONObject;

/**
 * 出口站信息
 *
 * @author Wang Guodong
 */
public class ExitStationInfo {

    private String StationId;//车辆出口收费站的编号，编号由路段网络号+站号组成（如羊楼司：0501，德山：4579）
    private String Distance;//车辆从入口站或最后一个识别点到出口的行驶里程数，数值精确到小数点后四位   单位：公里（单位不包含在Value中）
    private String Fee;	//车辆从入口站或最后一个识别点到出口的通行费，精确到小数点后两位
    private String Path;//字符串	车辆从入口站或最后一个识别点到出口的行驶路径信息

    public String getStationId() {
        return StationId;
    }

    public void setStationId(String StationId) {
        this.StationId = StationId;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String Distance) {
        this.Distance = Distance;
    }

    public String getFee() {
        return Fee;
    }

    public void setFee(String Fee) {
        this.Fee = Fee;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String Path) {
        this.Path = Path;
    }

    /**
     * 获取JSON格式的字符串
     *
     * @return JSON格式的字符串
     */
    public String toJSONString() {
        JSONObject jo = JSONObject.fromObject(this);
        return jo.toString();
    }

    @Override
    public String toString() {
        return "ExitStationInfo{" + "StationId=" + StationId + ", Distance=" + Distance + ", Fee=" + Fee + ", Path=" + Path + '}';
    }
    
}
