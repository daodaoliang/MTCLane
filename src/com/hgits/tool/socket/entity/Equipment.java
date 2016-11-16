/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hgits.tool.socket.entity;

/**
 *  入口、出口扩展类
 * @author huangjz
 */
public class Equipment  extends BaseConfirm{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 报警设备*/
    private String equ;
    /** 车道类型*/
    private String laneType;

    public String getEqu() {
        return equ;
    }

    public void setEqu(String equ) {
        this.equ = equ;
    }

    public String getLaneType() {
        return laneType;
    }

    public void setLaneType(String laneType) {
        this.laneType = laneType;
    }
    
}
