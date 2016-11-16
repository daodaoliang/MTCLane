package com.hgits.realTimePath.vo;

import net.sf.json.JSONObject;

/**
 * 路径明细查询实体类
 *
 * @author Wang Guodong
 */
public class QueryPath {

    private String EntryStationID;//	字符串	车辆入口收费站的编号,编号由路段网络号+站号组成（如羊楼司：0501，德山：4579）
    private String EntryChannelNo;	//字符串	车辆入口收费站车道号
    private String EntryTime;	//字符串	车型入高速时间（例：20140506100000）
    private String ExVehicleColor;	//字符串	出口车牌颜色  如：Y/U/B/W…
    private short ExVehicleClass;	//Short	出口车辆类型
    private String ExPlateNo;	//字符串	出口车牌号码
    private String ExitStationID;	//字符串	车辆出口收费站的编号.编号由路段网络号+站号组成（如羊楼司：0501，德山：4579）
    private String ExitChannelNo;	//字符串	出站车道编号
    private short ExitChannelClass;	//Short	出站车道类型,1表示MTC车道，2表示ETC车道
    private String ExitTime;	//字符串	出站时间//（例：20140506100000）
    private String CardNo;	//字符串	收费卡卡号
    private String RealWeight;	//字符串	车辆计费重量,可能为小数不做四舍五入.注：10kg为单位，如1234.587 代表12345.89Kg，单位不包含在Value中
    private String StandWeight;	//字符串	车辆限重

    public String getEntryStationID() {
        return EntryStationID;
    }

    public void setEntryStationID(String EntryStationID) {
        this.EntryStationID = EntryStationID;
    }

    public String getEntryChannelNo() {
        return EntryChannelNo;
    }

    public void setEntryChannelNo(String EntryChannelNo) {
        this.EntryChannelNo = EntryChannelNo;
    }

    public String getEntryTime() {
        return EntryTime;
    }

    public void setEntryTime(String EntryTime) {
        this.EntryTime = EntryTime;
    }

    public String getExVehicleColor() {
        return ExVehicleColor;
    }

    public void setExVehicleColor(String ExVehicleColor) {
        this.ExVehicleColor = ExVehicleColor;
    }

    public short getExVehicleClass() {
        return ExVehicleClass;
    }

    public void setExVehicleClass(short ExVehicleClass) {
        this.ExVehicleClass = ExVehicleClass;
    }

    public String getExPlateNo() {
        return ExPlateNo;
    }

    public void setExPlateNo(String ExPlateNo) {
        this.ExPlateNo = ExPlateNo;
    }

    public String getExitStationID() {
        return ExitStationID;
    }

    public void setExitStationID(String ExitStationID) {
        this.ExitStationID = ExitStationID;
    }

    public String getExitChannelNo() {
        return ExitChannelNo;
    }

    public void setExitChannelNo(String ExitChannelNo) {
        this.ExitChannelNo = ExitChannelNo;
    }

    public short getExitChannelClass() {
        return ExitChannelClass;
    }

    public void setExitChannelClass(short ExitChannelClass) {
        this.ExitChannelClass = ExitChannelClass;
    }

    public String getExitTime() {
        return ExitTime;
    }

    public void setExitTime(String ExitTime) {
        this.ExitTime = ExitTime;
    }

    public String getCardNo() {
        return CardNo;
    }

    public void setCardNo(String CardNo) {
        this.CardNo = CardNo;
    }

    public String getRealWeight() {
        return RealWeight;
    }

    public void setRealWeight(String RealWeight) {
        this.RealWeight = RealWeight;
    }

    public String getStandWeight() {
        return StandWeight;
    }

    public void setStandWeight(String StandWeight) {
        this.StandWeight = StandWeight;
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
        return "QueryPath{" + "EntryStationID=" + EntryStationID + ", EntryChannelNo=" + EntryChannelNo + ", EntryTime=" + EntryTime + ", ExVehicleColor=" + ExVehicleColor + ", ExVehicleClass=" + ExVehicleClass + ", ExPlateNo=" + ExPlateNo + ", ExitStationID=" + ExitStationID + ", ExitChannelNo=" + ExitChannelNo + ", ExitChannelClass=" + ExitChannelClass + ", ExitTime=" + ExitTime + ", CardNo=" + CardNo + ", RealWeight=" + RealWeight + ", StandWeight=" + StandWeight + '}';
    }
    
    
}
