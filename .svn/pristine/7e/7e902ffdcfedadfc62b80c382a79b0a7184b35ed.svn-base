/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hgits.vo;

import java.util.Date;

import com.hgits.tool.socket.entity.BaseConfirm;

/**
 * 对应车道报警信息表tb_LaneAlarm
 * //@author Wang Guodong
 */
public class LaneAlarm extends BaseConfirm {
    private static final long serialVersionUID = 1L;
   
    protected LaneAlarmPK laneAlarmPK;//主键
    
    private short roaduniqueId;//路段唯一编码
    
    private short laneType;//车道类型
   
    private String laneTypeName;//车道类型名称
    
    private Short laneId;//车道编号
    
    private String deviceName;//设备名称
   
    private short serialId;//报警序列号
    
    private String operatorNo;//员工号
   
    private Short squadId;//工班号
    
    private short deviceStatus;//设备状态1发生 0消失
    
    private String order;//设备故障代码
    
    private String describe;//故障描述
   
    private String spare;//备用

    public LaneAlarm() {
    }

    public LaneAlarm(LaneAlarmPK laneAlarmPK) {
        this.laneAlarmPK = laneAlarmPK;
    }

    public LaneAlarm(LaneAlarmPK laneAlarmPK, short roaduniqueId, short laneType, String deviceName, short serialId, String operatorNo, short deviceStatus) {
        this.laneAlarmPK = laneAlarmPK;
        this.roaduniqueId = roaduniqueId;
        this.laneType = laneType;
        this.deviceName = deviceName;
        this.serialId = serialId;
        this.operatorNo = operatorNo;
        this.deviceStatus = deviceStatus;
    }

    public LaneAlarm(short roadId, short stationId, String laneNo, Date opTime) {
        this.laneAlarmPK = new LaneAlarmPK(roadId, stationId, laneNo, opTime);
    }

    public LaneAlarmPK getLaneAlarmPK() {
        return laneAlarmPK;
    }

    public void setLaneAlarmPK(LaneAlarmPK laneAlarmPK) {
        this.laneAlarmPK = laneAlarmPK;
    }

    public short getRoaduniqueId() {
        return roaduniqueId;
    }

    public void setRoaduniqueId(short roaduniqueId) {
        this.roaduniqueId = roaduniqueId;
    }

    public short getLaneType() {
        return laneType;
    }

    public void setLaneType(short laneType) {
        this.laneType = laneType;
    }

    public String getLaneTypeName() {
        return laneTypeName;
    }

    public void setLaneTypeName(String laneTypeName) {
        this.laneTypeName = laneTypeName;
    }

    public Short getLaneId() {
        return laneId;
    }

    public void setLaneId(Short laneId) {
        this.laneId = laneId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public short getSerialId() {
        return serialId;
    }

    public void setSerialId(short serialId) {
        this.serialId = serialId;
    }

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    public Short getSquadId() {
        return squadId;
    }

    public void setSquadId(Short squadId) {
        this.squadId = squadId;
    }

    public short getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(short deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getSpare() {
        return spare;
    }

    public void setSpare(String spare) {
        this.spare = spare;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (laneAlarmPK != null ? laneAlarmPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LaneAlarm)) {
            return false;
        }
        LaneAlarm other = (LaneAlarm) object;
        if ((this.laneAlarmPK == null && other.laneAlarmPK != null) || (this.laneAlarmPK != null && !this.laneAlarmPK.equals(other.laneAlarmPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hgits.vo.LaneAlarm[ laneAlarmPK=" + laneAlarmPK + " ]";
    }
    
}
