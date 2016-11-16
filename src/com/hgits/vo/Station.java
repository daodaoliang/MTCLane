/**
 *
 */
package com.hgits.vo;

import java.util.Date;

/**
 * 收费站编码实体类
 *
 * @author wh
 *
 */
public class Station {

    private Integer areaId;
    private Integer roaduniqueId;
    private Integer roadId;
    private Integer stationId;
    private String stationName;
    private String stationServerName;
    private String stationServerIp;
    private String stationDBName;
    private String stationDBUser;
    private String StationDBPassword;
    private Integer version;
    private Date startTime;
    private Integer status;

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getRoadId() {
        return roadId;
    }

    public void setRoadId(Integer roadId) {
        this.roadId = roadId;
    }

    public Integer getRoaduniqueId() {
        return roaduniqueId;
    }

    public void setRoaduniqueId(Integer roaduniqueId) {
        this.roaduniqueId = roaduniqueId;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationServerName() {
        return stationServerName;
    }

    public void setStationServerName(String stationServerName) {
        this.stationServerName = stationServerName;
    }

    public String getStationServerIp() {
        return stationServerIp;
    }

    public void setStationServerIp(String stationServerIp) {
        this.stationServerIp = stationServerIp;
    }

    public String getStationDBName() {
        return stationDBName;
    }

    public void setStationDBName(String stationDBName) {
        this.stationDBName = stationDBName;
    }

    public String getStationDBUser() {
        return stationDBUser;
    }

    public void setStationDBUser(String stationDBUser) {
        this.stationDBUser = stationDBUser;
    }

    public String getStationDBPassword() {
        return StationDBPassword;
    }

    public void setStationDBPassword(String stationDBPassword) {
        StationDBPassword = stationDBPassword;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
