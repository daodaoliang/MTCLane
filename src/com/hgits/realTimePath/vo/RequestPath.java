package com.hgits.realTimePath.vo;

import com.hgits.realTimePath.ByteUtil;
import com.hgits.realTimePath.RTUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 路径识别服务器所需请求实际路径信息
 *
 * @author Wang Guodong
 */
public class RequestPath {

    private String enStation;//收费站编号编号,由路段网络号+站号组成（如羊楼司：0501)
    private String enLane;//车辆入收费站的车道编号
    private short exitChannelClass = 1;//出站车道类型 1表示MTC车道，2表示ETC车道
    private Date entryTime;//车型入高速时间
    private String exitVehColor;//出口车牌颜色  如：黄
    private short vehClass;//出口车辆类型
    private String plateNum;//出口车牌号码
    private String exitStation;//车辆出口收费站的编号,由路段网络号+站号组成（如羊楼司：0501)
    private String exitLane;//出站车道编号
    private Date exitTime;//出站时间
    private int cardType;//卡类型
    private String cardNum;//收费卡卡号
    private String fareWeight;//车辆计费重量
    private String limitWeight;//车辆限重

//    /**
//     * 获取该实体类对应需要发送给路径识别服务器的数组
//     *
//     * @deprecated
//     * @return
//     */
//    public byte[] getByteArray() {
//        byte[] buffer1 = ByteUtil.getByteArray(this.enStation);
//        byte[] buffer2 = ByteUtil.getByteArray(this.enLane);
//        byte[] buffer3 = ByteUtil.getByteArray(this.entryTime);
//        byte[] buffer4 = ByteUtil.getByteArray(this.exitVehColor);
//        byte[] buffer5 = ByteUtil.getByteArray(this.vehClass);
//        byte[] buffer6 = ByteUtil.getByteArray(this.plateNum);
//        byte[] buffer7 = ByteUtil.getByteArray(this.exitStation);
//        byte[] buffer8 = ByteUtil.getByteArray(this.exitLane);
//        byte[] buffer9 = ByteUtil.getByteArray(this.exitTime);
//        byte[] buffer10 = ByteUtil.getByteArray(this.cardType);
//        byte[] buffer11 = ByteUtil.getByteArray(this.cardNum);
//        byte[] buffer12 = ByteUtil.getByteArray(this.fareWeight);
//        byte[] buffer13 = ByteUtil.getByteArray(this.limitWeight);
//        List<byte[]> list = new ArrayList();
//        list.add(buffer1);
//        list.add(buffer2);
//        list.add(buffer3);
//        list.add(buffer4);
//        list.add(buffer5);
//        list.add(buffer6);
//        list.add(buffer7);
//        list.add(buffer8);
//        list.add(buffer9);
//        list.add(buffer10);
//        list.add(buffer11);
//        list.add(buffer12);
//        list.add(buffer13);
//        return ByteUtil.mergeBytes(list);
//    }

    public String getEnStation() {
        return enStation;
    }

    public void setEnStation(String enStation) {
        this.enStation = enStation;
    }

    public String getEnLane() {
        return enLane;
    }

    public void setEnLane(String enLane) {
        this.enLane = enLane;
    }

    public Date getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    public String getExitVehColor() {
        return exitVehColor;
    }

    public void setExitVehColor(String exitVehColor) {
        this.exitVehColor = exitVehColor;
    }

    public short getVehClass() {
        return vehClass;
    }

    public void setVehClass(short vehClass) {
        this.vehClass = vehClass;
    }

    public String getPlateNum() {
        return plateNum;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum;
    }

    public String getExitStation() {
        return exitStation;
    }

    public void setExitStation(String exitStation) {
        this.exitStation = exitStation;
    }

    public String getExitLane() {
        return exitLane;
    }

    public void setExitLane(String exitLane) {
        this.exitLane = exitLane;
    }

    public Date getExitTime() {
        return exitTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getFareWeight() {
        return fareWeight;
    }

    public void setFareWeight(String fareWeight) {
        this.fareWeight = fareWeight;
    }

    public String getLimitWeight() {
        return limitWeight;
    }

    public void setLimitWeight(String limitWeight) {
        this.limitWeight = limitWeight;
    }

    public short getExitChannelClass() {
        return exitChannelClass;
    }

    public void setExitChannelClass(short exitChannelClass) {
        this.exitChannelClass = exitChannelClass;
    }


    @Override
    public String toString() {
        return "RequestPath{" + "enStation=" + enStation + ", enLane=" + enLane + ", entryTime=" + entryTime + ", exitVehColor=" + exitVehColor + ", vehClass=" + vehClass + ", plateNum=" + plateNum + ", exitStation=" + exitStation + ", exitLane=" + exitLane + ", exitTime=" + exitTime + ", cardType=" + cardType + ", cardNum=" + cardNum + ", fareWeight=" + fareWeight + ", limitWeight=" + limitWeight + '}';
    }

    public String toJSONString() {
        return "{\"RequestPath\":{"
                + "\"EntryStationID\":\"" + RTUtil.getString(enStation) + "\","
                + "\"EntryChannelNo\":\"" + RTUtil.getString(enLane) + "\","
                + "\"EntryTime\":\"" + RTUtil.getString(entryTime, "yyyyMMddHHmmss") + "\","
                + "\"VehicleColor\":\"" + RTUtil.getString(exitVehColor) + "\","
                + "\"VehicleClass\":\"" + RTUtil.getString(vehClass) + "\","
                + "\"PlateNo\":\"" + RTUtil.getString(plateNum) + "\","
                + "\"ExitStationID\":\"" + RTUtil.getString(exitStation) + "\","
                + "\"ExitChannelNo\":\"" + RTUtil.getString(exitLane) + "\","
                + "\"ExitChannelClass\":\"" + RTUtil.getString(exitChannelClass) + "\","
                + "\"ExitTime\":\"" + RTUtil.getString(exitTime, "yyyyMMddHHmmss") + "\","
                + "\"CardNo\":\"" + RTUtil.getString(cardNum) + "\","
                + "\"RealWeight\":\"" + RTUtil.getString(fareWeight) + "\","
                + "\"StandWeight\":\"" + RTUtil.getString(limitWeight) + "\""
                + "}}";
    }

}
