package com.hgits.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 车道实体类
 *
 * @author Wang Guodong
 */
public class Lane implements Serializable {

    private static final long serialVersionUID = 1L;
    //IP地址
    protected String IPAddress;
    //站名
    protected String stationName;
    //站编号(两位)
    protected String stationId;
    //站所属路段名
    protected String roadName;
    //站所属路段编号
    protected String roadId;
    //车道编号
    protected String laneId;
    //车道状态(入口还是出口)
    protected int laneType;

    private Integer roadSubSectionId;
    private String deviceStatus;
    private String pcName;
    private String laneIp;
    private Integer version;
    private Date startTime;
    private Integer status;

    private Lane() {
    }
    private static Lane lane;

    /**
     * 获取车道惟一实例
     *
     * @return Lane实例
     */
    public static synchronized Lane getInstance() {
        if (lane == null) {
            lane = new Lane();
        }
        return lane;
    }

    /**
     * 入口车道类型
     */
    public static final int EntryLane = 1;
    /**
     * 出口车道类型
     */
    public static final int ExitLane = 2;

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getRoadId() {
        return roadId;
    }

    public void setRoadId(String roadId) {
        this.roadId = roadId;
    }

    public String getLaneId() {
        return laneId;
    }

    public void setLaneId(String laneId) {
        this.laneId = laneId;
    }

    public int getLaneType() {
        return laneType;
    }

    public void setLaneType(int laneType) {
        this.laneType = laneType;
    }

    public Integer getRoadSubSectionId() {
        return roadSubSectionId;
    }

    public void setRoadSubSectionId(Integer roadSubSectionId) {
        this.roadSubSectionId = roadSubSectionId;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getPcName() {
        return pcName;
    }

    public void setPcName(String pcName) {
        this.pcName = pcName;
    }

    public String getLaneIp() {
        return laneIp;
    }

    public void setLaneIp(String laneIp) {
        this.laneIp = laneIp;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.IPAddress);
        hash = 17 * hash + Objects.hashCode(this.stationName);
        hash = 17 * hash + Objects.hashCode(this.stationId);
        hash = 17 * hash + Objects.hashCode(this.roadName);
        hash = 17 * hash + Objects.hashCode(this.roadId);
        hash = 17 * hash + Objects.hashCode(this.laneId);
        hash = 17 * hash + Objects.hashCode(this.laneType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Lane other = (Lane) obj;
        if (!Objects.equals(this.IPAddress, other.IPAddress)) {
            return false;
        }
        if (!Objects.equals(this.stationName, other.stationName)) {
            return false;
        }
        if (!Objects.equals(this.stationId, other.stationId)) {
            return false;
        }
        if (!Objects.equals(this.roadName, other.roadName)) {
            return false;
        }
        if (!Objects.equals(this.roadId, other.roadId)) {
            return false;
        }
        if (!Objects.equals(this.laneId, other.laneId)) {
            return false;
        }
        if (this.laneType != other.laneType) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Lane{" + "IPAddress=" + IPAddress + ", stationName=" + stationName + ", stationId=" + stationId + ", roadName=" + roadName + ", roadId=" + roadId + ", laneId=" + laneId + ", laneType=" + laneType + '}';
    }

    /**
     * 获取车道写入通行卡时的字符串形式
     *
     * @return 需写入卡中的字符串
     */
    public String getString() {
        return roadId + stationId + Integer.toHexString(laneId.charAt(0)) + laneId.substring(1, 3);
    }

    /**
     * 车道信息自检
     */
    public void selfCheck() {
        if (laneId.toUpperCase().startsWith("E")) {
            if (laneType != EntryLane) {
                throw new RuntimeException("车道信息异常，车道" + laneId + "与车道类型" + laneType + "不一致");
            }
        } else if (laneId.toUpperCase().startsWith("X")) {
            if (laneType != ExitLane) {
                throw new RuntimeException("车道信息异常，车道" + laneId + "与车道类型" + laneType + "不一致");
            }
        } else {
            throw new RuntimeException("车道信息异常，未知车道" + laneId);
        }
    }

    /**
     * 当前车道是否为入口
     *
     * @return true/false
     */
    public boolean isEntry() {
        return laneType == EntryLane;
    }

    /**
     * 当前车道是否为出口
     *
     * @return true/false
     */
    public boolean isExit() {
        return laneType == ExitLane;
    }
}
