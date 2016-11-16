/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hgits.vo;

import java.io.Serializable;

/**
 * 对应车道报警编码表tb_AlarmCode
 *
 * //@author Wang Guodong
 */
//@Entity
//@Table(name = "tb_AlarmCode")
//@NamedQueries({
    //@NamedQuery(name = "TbAlarmCode.findAll", query = "SELECT t FROM TbAlarmCode t")})
public class AlarmCode implements Serializable {

    private static final long serialVersionUID = 1L;
    //@Id
    //@Basic(optional = false)
    //@Column(name = "SerialId")
    private Short serialId;//序号
    //@Basic(optional = false)
    //@Column(name = "LaneType")
    private short laneType;//车道类型
    //@Column(name = "LaneTypeName")
    private String laneTypeName;//车道类型名称
    //@Basic(optional = false)
    //@Column(name = "DeviceName")
    private String deviceName;//设备名称
    //@Column(name = "Order")
    private String order;//故障代码
    //@Column(name = "Describe")
    private String describe;//故障描述
    //@Basic(optional = false)
    //@Column(name = "IsShow")
    private Character isShow;//是否显示
    //@Column(name = "Spare")
    private String spare;//备用

    public AlarmCode() {
    }

    public AlarmCode(Short serialId) {
        this.serialId = serialId;
    }

    public AlarmCode(Short serialId, short laneType, String deviceName, Character isShow) {
        this.serialId = serialId;
        this.laneType = laneType;
        this.deviceName = deviceName;
        this.isShow = isShow;
    }

    public Short getSerialId() {
        return serialId;
    }

    public void setSerialId(Short serialId) {
        this.serialId = serialId;
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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public Character getIsShow() {
        return isShow;
    }

    public void setIsShow(Character isShow) {
        this.isShow = isShow;
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
        hash += (serialId != null ? serialId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AlarmCode)) {
            return false;
        }
        AlarmCode other = (AlarmCode) object;
        if ((this.serialId == null && other.serialId != null) || (this.serialId != null && !this.serialId.equals(other.serialId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hgits.vo.TbAlarmCode[ serialId=" + serialId + " ]";
    }

}
