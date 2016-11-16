/**
 * 
 */
package com.hgits.vo;

import java.util.Date;

/**
 * 操作员表
 * 
 * @author wh
 *
 */
public class Operator {
	private Integer roaduniqueId;
	private Integer stationId;
	private String operatorNo;
	private String operatorName;
	private String authorizeStationSerial;
	private String passWord;
	private Integer version;
	private Date startTime;
	private Integer status;
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
	public String getOperatorNo() {
		return operatorNo;
	}
	public void setOperatorNo(String operatorNo) {
		this.operatorNo = operatorNo;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getAuthorizeStationSerial() {
		return authorizeStationSerial;
	}
	public void setAuthorizeStationSerial(String authorizeStationSerial) {
		this.authorizeStationSerial = authorizeStationSerial;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
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
