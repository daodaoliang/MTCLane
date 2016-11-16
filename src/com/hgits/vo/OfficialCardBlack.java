/**
 * 
 */
package com.hgits.vo;

import java.util.Date;

/**
 * 公务卡黑名单
 * 
 * @author wh
 *
 */
public class OfficialCardBlack {

	private String officialCardNo;
	private Integer genCau;
	private Date genTime;
	private Integer version;
	private Date startTime;


	public String getOfficialCardNo() {
		return officialCardNo;
	}

	public void setOfficialCardNo(String officialCardNo) {
		this.officialCardNo = officialCardNo;
	}

	public Integer getGenCau() {
		return genCau;
	}

	public void setGenCau(Integer genCau) {
		this.genCau = genCau;
	}

	public Date getGenTime() {
		return genTime;
	}

	public void setGenTime(Date genTime) {
		this.genTime = genTime;
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
