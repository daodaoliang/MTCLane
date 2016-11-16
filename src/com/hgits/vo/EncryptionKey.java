/**
 * 
 */
package com.hgits.vo;

import java.util.Date;

/**
 * 密钥信息表
 * 
 * @author Administrator
 *
 */
public class EncryptionKey extends BaseVo {
	private Integer id;
	private String encryptionKey;
	private Date expireDate;
	private Integer encryptionRank;//0只用于校验和加密 1 仅用于校验
	private Integer spare1;
	private String spare2;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getEncryptionKey() {
		return encryptionKey;
	}
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	public Date getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
	public Integer getEncryptionRank() {
		return encryptionRank;
	}
	public void setEncryptionRank(Integer encryptionRank) {
		this.encryptionRank = encryptionRank;
	}
	public Integer getSpare1() {
		return spare1;
	}
	public void setSpare1(Integer spare1) {
		this.spare1 = spare1;
	}
	public String getSpare2() {
		return spare2;
	}
	public void setSpare2(String spare2) {
		this.spare2 = spare2;
	}
}
