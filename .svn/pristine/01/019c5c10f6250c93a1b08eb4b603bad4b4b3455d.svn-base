package com.hgits.realTimePath.vo;

import com.hgits.realTimePath.ByteUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 确认收费实体类,用于与路径识别服务器通信
 *
 * @deprecated
 * @author Wang Guodong
 */
public class ConfirmCost {

    private String orderSN;//交易流水号返回RequestPathResp中下发的流水号
    private String vehColor;//出口车牌颜色  如：黄/蓝…
    private short vehClass;//出口车辆类型
    private String vehPlateNum;//出口车牌号码
    private String exitStationid;//车辆出口收费站的编号编号由路段网络号+站号组成（如羊楼司：0501，德山：4579）
    private String exitLane;//出站车道编号
    private Date exitTime;//出站时间（例：20140506100000）
    private String staffid;//收费员的员工编号
    private String accountFee;//应该收取费用     精确到小数点后两位（单位元，单位不包含在Value值中）
    private String realFee;//实际收取的费用  精确到小数点后两位（单位元，单位不包含在Value值中）
    private int exitMop;//付款方式
    private int downLevel;//是否降级 1-是   0-不是
    private String cardSerial;//收费卡号

    /**
     * 获取该实体类对应需要发送给路径识别服务器的数组
     * @deprecated 
     * @return
     */
    public byte[] getByteArray() {
        byte[] buffer1 = ByteUtil.getByteArray(this.orderSN);
        byte[] buffer2 = ByteUtil.getByteArray(this.vehColor);
        byte[] buffer3 = ByteUtil.getByteArray(this.vehClass);
        byte[] buffer4 = ByteUtil.getByteArray(this.vehPlateNum);
        byte[] buffer5 = ByteUtil.getByteArray(this.exitStationid);
        byte[] buffer6 = ByteUtil.getByteArray(this.exitLane);
        byte[] buffer7 = ByteUtil.getByteArray(this.exitTime);
        byte[] buffer8 = ByteUtil.getByteArray(this.staffid);
        byte[] buffer9 = ByteUtil.getByteArray(this.accountFee);
        byte[] buffer10 = ByteUtil.getByteArray(this.realFee);
        byte[] buffer11 = ByteUtil.getByteArray(this.exitMop);
        byte[] buffer12 = ByteUtil.getByteArray(this.downLevel);
        byte[] buffer13 = ByteUtil.getByteArray(this.cardSerial);
        List<byte[]> list = new ArrayList();
        list.add(buffer1);
        list.add(buffer2);
        list.add(buffer3);
        list.add(buffer4);
        list.add(buffer5);
        list.add(buffer6);
        list.add(buffer7);
        list.add(buffer8);
        list.add(buffer9);
        list.add(buffer10);
        list.add(buffer11);
        list.add(buffer12);
        list.add(buffer13);
        return ByteUtil.mergeBytes(list);
    }

    public String getOrderSN() {
        return orderSN;
    }

    public void setOrderSN(String orderSN) {
        this.orderSN = orderSN;
    }

    public String getVehColor() {
        return vehColor;
    }

    public void setVehColor(String vehColor) {
        this.vehColor = vehColor;
    }

    public short getVehClass() {
        return vehClass;
    }

    public void setVehClass(short vehClass) {
        this.vehClass = vehClass;
    }

    public String getVehPlateNum() {
        return vehPlateNum;
    }

    public void setVehPlateNum(String vehPlateNum) {
        this.vehPlateNum = vehPlateNum;
    }

    public String getExitStationid() {
        return exitStationid;
    }

    public void setExitStationid(String exitStationid) {
        this.exitStationid = exitStationid;
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

    public String getStaffid() {
        return staffid;
    }

    public void setStaffid(String staffid) {
        this.staffid = staffid;
    }

    public String getAccountFee() {
        return accountFee;
    }

    public void setAccountFee(String accountFee) {
        this.accountFee = accountFee;
    }

    public String getRealFee() {
        return realFee;
    }

    public void setRealFee(String realFee) {
        this.realFee = realFee;
    }

    public int getExitMop() {
        return exitMop;
    }

    public void setExitMop(int exitMop) {
        this.exitMop = exitMop;
    }

    public int getDownLevel() {
        return downLevel;
    }

    public void setDownLevel(int downLevel) {
        this.downLevel = downLevel;
    }

    public String getCardSerial() {
        return cardSerial;
    }

    public void setCardSerial(String cardSerial) {
        this.cardSerial = cardSerial;
    }

    @Override
    public String toString() {
        return "ConfirmCost{" + "orderSN=" + orderSN + ", vehColor=" + vehColor + ", vehClass=" + vehClass + ", vehPlateNum=" + vehPlateNum + ", exitStationid=" + exitStationid + ", exitLane=" + exitLane + ", exitTime=" + exitTime + ", staffid=" + staffid + ", accountFee=" + accountFee + ", realFee=" + realFee + ", exitMop=" + exitMop + ", downLevel=" + downLevel + ", cardSerial=" + cardSerial + '}';
    }
}
