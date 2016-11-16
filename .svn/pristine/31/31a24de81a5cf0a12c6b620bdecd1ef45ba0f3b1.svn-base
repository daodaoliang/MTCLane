package com.hgits.realTimePath.vo;

import java.util.List;
import net.sf.json.JSONObject;

/**
 * 路径明细查询返回结果
 *
 * @author Wang Guodong
 */
public class QueryPathResp {

    private int Status;	//	Integer	请求返回结果
    private String FareVer;	//	字符串	费率版本
    private String TotalFee;	//	字符串	车辆通行费（包括机场代收、代退） 精确到小数点后两位  
    private String CollectionFee;	//	字符串	代收费用,目前是城通费 ，精确到小数点后两位 
    private String TotalDistance;	//	字符串	车辆行驶的总里程数，数值精确到小数点后四位   单位：公里（单位不包含在Value中）
    private EntryStationInfo entryStationInfo;//车辆入口站信息
    private List<SpotInfo> spotList;//识别点序列
    private ExitStationInfo exitStationInfo;//车辆出口站信息

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public String getFareVer() {
        return FareVer;
    }

    public void setFareVer(String FareVer) {
        this.FareVer = FareVer;
    }

    public String getTotalFee() {
        return TotalFee;
    }

    public void setTotalFee(String TotalFee) {
        this.TotalFee = TotalFee;
    }

    public String getCollectionFee() {
        return CollectionFee;
    }

    public void setCollectionFee(String CollectionFee) {
        this.CollectionFee = CollectionFee;
    }

    public String getTotalDistance() {
        return TotalDistance;
    }

    public void setTotalDistance(String TotalDistance) {
        this.TotalDistance = TotalDistance;
    }

    public EntryStationInfo getEntryStationInfo() {
        return entryStationInfo;
    }

    public void setEntryStationInfo(EntryStationInfo entryStationInfo) {
        this.entryStationInfo = entryStationInfo;
    }

    public List<SpotInfo> getSpotList() {
        return spotList;
    }

    public void setSpotList(List<SpotInfo> spotLIst) {
        this.spotList = spotLIst;
    }

    public ExitStationInfo getExitStationInfo() {
        return exitStationInfo;
    }

    public void setExitStationInfo(ExitStationInfo exitStationInfo) {
        this.exitStationInfo = exitStationInfo;
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
}
