/**
 * 
 */
package com.hgits.vo;

import java.util.Date;

/**
 * 参数文件使用的实体
 * @author wh
 *
 */
public class LaneParam {
	
	private Integer roaduniqueId;
	private Integer roadId;
	private Integer stationId;
	private String laneNo;
	private Integer laneId;
	private Integer laneType;
	private String pcName;
	private String laneIp;
	private Integer version;
	private Date startTime;
	private Integer status;
	public Integer getRoaduniqueId() {
		return roaduniqueId;
	}
	public void setRoaduniqueId(Integer roaduniqueId) {
		this.roaduniqueId = roaduniqueId;
	}
	public Integer getRoadId() {
		return roadId;
	}
	public void setRoadId(Integer roadId) {
		this.roadId = roadId;
	}
	public Integer getStationId() {
		return stationId;
	}
	public void setStationId(Integer stationId) {
		this.stationId = stationId;
	}
	public String getLaneNo() {
		return laneNo;
	}
	public void setLaneNo(String laneNo) {
		this.laneNo = laneNo;
	}
	public Integer getLaneId() {
		return laneId;
	}
	public void setLaneId(Integer laneId) {
		this.laneId = laneId;
	}
	public Integer getLaneType() {
		return laneType;
	}
	public void setLaneType(Integer laneType) {
		this.laneType = laneType;
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

}
