/**
 *
 */
package com.hgits.vo;

import java.util.Date;

/**
 * 行程时间表
 *
 * @author wh
 *
 */
public class TravelTime {

    private Integer enRoadId;
    private Integer enStationId;
    private Integer exRoadId;
    private Integer exStationId;
    private Integer vehType;
    private Double mileages;
    private Integer minTravelTime;
    private Integer maxTravelTime;
    private Integer version;
    private Date startTime;
	/**
	 * @return the enRoadId
	 */
	public Integer getEnRoadId() {
		return enRoadId;
	}
	/**
	 * @param enRoadId the enRoadId to set
	 */
	public void setEnRoadId(Integer enRoadId) {
		this.enRoadId = enRoadId;
	}
	/**
	 * @return the enStationId
	 */
	public Integer getEnStationId() {
		return enStationId;
	}
	/**
	 * @param enStationId the enStationId to set
	 */
	public void setEnStationId(Integer enStationId) {
		this.enStationId = enStationId;
	}
	/**
	 * @return the exRoadId
	 */
	public Integer getExRoadId() {
		return exRoadId;
	}
	/**
	 * @param exRoadId the exRoadId to set
	 */
	public void setExRoadId(Integer exRoadId) {
		this.exRoadId = exRoadId;
	}
	/**
	 * @return the exStationId
	 */
	public Integer getExStationId() {
		return exStationId;
	}
	/**
	 * @param exStationId the exStationId to set
	 */
	public void setExStationId(Integer exStationId) {
		this.exStationId = exStationId;
	}
	/**
	 * @return the vehType
	 */
	public Integer getVehType() {
		return vehType;
	}
	/**
	 * @param vehType the vehType to set
	 */
	public void setVehType(Integer vehType) {
		this.vehType = vehType;
	}
	/**
	 * @return the mileages
	 */
	public Double getMileages() {
		return mileages;
	}
	/**
	 * @param mileages the mileages to set
	 */
	public void setMileages(Double mileages) {
		this.mileages = mileages;
	}
	/**
	 * @return the minTravelTime
	 */
	public Integer getMinTravelTime() {
		return minTravelTime;
	}
	/**
	 * @param minTravelTime the minTravelTime to set
	 */
	public void setMinTravelTime(Integer minTravelTime) {
		this.minTravelTime = minTravelTime;
	}
	/**
	 * @return the maxTravelTime
	 */
	public Integer getMaxTravelTime() {
		return maxTravelTime;
	}
	/**
	 * @param maxTravelTime the maxTravelTime to set
	 */
	public void setMaxTravelTime(Integer maxTravelTime) {
		this.maxTravelTime = maxTravelTime;
	}
	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

    
}
