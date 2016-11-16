/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hgits.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据库中tb_LaneAlarm主键
 *
 * //@author Wang Guodong
 */
//@Embeddable
public class LaneAlarmPK implements Serializable {

    //@Basic(optional = false)
    //@Column(name = "RoadId")
    private short roadId;//路段编码
    //@Basic(optional = false)
    //@Column(name = "StationId")
    private short stationId;//收费站编码
    //@Basic(optional = false)
    //@Column(name = "LaneNo")
    private String laneNo;//车道编号
    //@Basic(optional = false)
    //@Column(name = "OpTime")
    private Date opTime;//报警时间

    public LaneAlarmPK() {
    }

    public LaneAlarmPK(short roadId, short stationId, String laneNo, Date opTime) {
        this.roadId = roadId;
        this.stationId = stationId;
        this.laneNo = laneNo;
        this.opTime = opTime;
    }

    public short getRoadId() {
        return roadId;
    }

    public void setRoadId(short roadId) {
        this.roadId = roadId;
    }

    public short getStationId() {
        return stationId;
    }

    public void setStationId(short stationId) {
        this.stationId = stationId;
    }

    public String getLaneNo() {
        return laneNo;
    }

    public void setLaneNo(String laneNo) {
        this.laneNo = laneNo;
    }

    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) roadId;
        hash += (int) stationId;
        hash += (laneNo != null ? laneNo.hashCode() : 0);
        hash += (opTime != null ? opTime.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LaneAlarmPK)) {
            return false;
        }
        LaneAlarmPK other = (LaneAlarmPK) object;
        if (this.roadId != other.roadId) {
            return false;
        }
        if (this.stationId != other.stationId) {
            return false;
        }
        if ((this.laneNo == null && other.laneNo != null) || (this.laneNo != null && !this.laneNo.equals(other.laneNo))) {
            return false;
        }
        if ((this.opTime == null && other.opTime != null) || (this.opTime != null && !this.opTime.equals(other.opTime))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hgits.vo.LaneAlarmPK[ roadId=" + roadId + ", stationId=" + stationId + ", laneNo=" + laneNo + ", opTime=" + opTime + " ]";
    }

}