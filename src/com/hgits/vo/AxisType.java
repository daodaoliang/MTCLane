/**
 * 
 */
package com.hgits.vo;

/**
 * 轴类型表
 * 
 * @author wh
 *
 */
public class AxisType {
	private Integer axisTypeId;
	private String axisTypeName;
	private Integer axisLimitWeight;
	private Integer tyresCount;
	private String symbol;
	/**
	 * @return the axisTypeId
	 */
	public Integer getAxisTypeId() {
		return axisTypeId;
	}
	/**
	 * @param axisTypeId the axisTypeId to set
	 */
	public void setAxisTypeId(Integer axisTypeId) {
		this.axisTypeId = axisTypeId;
	}
	/**
	 * @return the axisTypeName
	 */
	public String getAxisTypeName() {
		return axisTypeName;
	}
	/**
	 * @param axisTypeName the axisTypeName to set
	 */
	public void setAxisTypeName(String axisTypeName) {
		this.axisTypeName = axisTypeName;
	}
	/**
	 * @return the axisLimitWeight
	 */
	public Integer getAxisLimitWeight() {
		return axisLimitWeight;
	}
	/**
	 * @param axisLimitWeight the axisLimitWeight to set
	 */
	public void setAxisLimitWeight(Integer axisLimitWeight) {
		this.axisLimitWeight = axisLimitWeight;
	}
	/**
	 * @return the tyresCount
	 */
	public Integer getTyresCount() {
		return tyresCount;
	}
	/**
	 * @param tyresCount the tyresCount to set
	 */
	public void setTyresCount(Integer tyresCount) {
		this.tyresCount = tyresCount;
	}
	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}
	/**
	 * @param symbol the symbol to set
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
