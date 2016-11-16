package com.hgits.vo;

import java.util.Date;

/**
 * 用户折扣表
 * 
 * @author wh
 *
 */
public class UserRate {
	private Integer vehType;
	private Integer userType;
	private Integer netNo;
	private String provinceBcdName;
	private Double rate;
	private Integer version;
	private Date startTime;
	
	public Integer getVehType() {
		return vehType;
	}
	public void setVehType(Integer vehType) {
		this.vehType = vehType;
	}
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	public Integer getNetNo() {
		return netNo;
	}
	public void setNetNo(Integer netNo) {
		this.netNo = netNo;
	}
	public String getProvinceBcdName() {
		return provinceBcdName;
	}
	public void setProvinceBcdName(String provinceBcdName) {
		this.provinceBcdName = provinceBcdName;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
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

}
