/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hgits.vo;

import java.util.Date;

/**
 * 班次信息
 * @author wh
 */
public class Shift {
    private Integer id;
    private Integer stationId;
    private Integer laneType;
    private Integer lane;
    private Integer groupId;
    private Date startDate;
    private Date endDate;
    private String cardId;
    private String registListNo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public Integer getLaneType() {
        return laneType;
    }

    public void setLaneType(Integer laneType) {
        this.laneType = laneType;
    }

    public Integer getLane() {
        return lane;
    }

    public void setLane(Integer lane) {
        this.lane = lane;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getRegistListNo() {
        return registListNo;
    }

    public void setRegistListNo(String registListNo) {
        this.registListNo = registListNo;
    }
    
}
