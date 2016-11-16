/**
 * 
 */
package com.hgits.tool.socket.entity;

import java.io.Serializable;

/**
 * 监控室确认的基类
 * 
 * @author Administrator
 *
 */
public class BaseConfirm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8118794577664885661L;
	/**
	 * 系统超时时间
	 */
	private String timeout = "";
	
	public String getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

}
