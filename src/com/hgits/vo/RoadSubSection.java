/**
 * 
 */
package com.hgits.vo;

import java.util.Date;

/**
 * 路段分区编码实体
 * 
 * @author wh
 *
 */
public class RoadSubSection {
	private String nationalRoadNo;
	private Integer areaId;
	private Integer roadId;
	private String roadName;
	private Integer roadSubSectionId;
	private String roadSubSectionName;
	private String roadSubSectionServerName;
	private String roadSubSectionServerIp;
	private String roadSubSectionDBName;
	private String roadSubSectionDBUser;
	private String roadSubSectionDBPassword;
	private Integer freePositionId;
	private Integer version;
	private Date startTime;
	private Integer status;

	public String getNationalRoadNo() {
		return nationalRoadNo;
	}

	public void setNationalRoadNo(String nationalRoadNo) {
		this.nationalRoadNo = nationalRoadNo;
	}

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

	public String getRoadName() {
		return roadName;
	}

	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}

	public Integer getRoadSubSectionId() {
		return roadSubSectionId;
	}

	public void setRoadSubSectionId(Integer roadSubSectionId) {
		this.roadSubSectionId = roadSubSectionId;
	}

	public String getRoadSubSectionName() {
		return roadSubSectionName;
	}

	public void setRoadSubSectionName(String roadSubSectionName) {
		this.roadSubSectionName = roadSubSectionName;
	}

	public String getRoadSubSectionServerName() {
		return roadSubSectionServerName;
	}

	public void setRoadSubSectionServerName(String roadSubSectionServerName) {
		this.roadSubSectionServerName = roadSubSectionServerName;
	}

	public String getRoadSubSectionServerIp() {
		return roadSubSectionServerIp;
	}

	public void setRoadSubSectionServerIp(String roadSubSectionServerIp) {
		this.roadSubSectionServerIp = roadSubSectionServerIp;
	}

	public String getRoadSubSectionDBName() {
		return roadSubSectionDBName;
	}

	public void setRoadSubSectionDBName(String roadSubSectionDBName) {
		this.roadSubSectionDBName = roadSubSectionDBName;
	}

	public String getRoadSubSectionDBUser() {
		return roadSubSectionDBUser;
	}

	public void setRoadSubSectionDBUser(String roadSubSectionDBUser) {
		this.roadSubSectionDBUser = roadSubSectionDBUser;
	}

	public String getRoadSubSectionDBPassword() {
		return roadSubSectionDBPassword;
	}

	public void setRoadSubSectionDBPassword(String roadSubSectionDBPassword) {
		this.roadSubSectionDBPassword = roadSubSectionDBPassword;
	}

	public Integer getFreePositionId() {
		return freePositionId;
	}

	public void setFreePositionId(Integer freePositionId) {
		this.freePositionId = freePositionId;
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
