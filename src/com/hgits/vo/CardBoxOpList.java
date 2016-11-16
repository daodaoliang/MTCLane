/**
 * 
 */
package com.hgits.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 卡箱操作流动单
 * 对应表tb_CardBoxOpList
 * @author Administrator
 *
 */
public class CardBoxOpList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4262375815301035479L;
	
	/**
	 * 卡箱标签号
	 */
	private String cardBoxLabelNo;
	
	/**
	 * 卡箱号
	 */
	private String cardBoxNo;
	
	/**
	 * 卡箱循环号
	 */
	private String cardBoxCycleTimes;
	
	/**
	 * 卡箱卡数
	 */
	private Integer cardNum;
	
	/**
	 * 发送者工号
	 */
	private String senderNo;
	
	/**
	 * 接收者工号
	 */
	private String receiverNo;
	
	/**
	 * 接收路段编号
	 */
	private Integer recRoadId;
	
	/**
	 * 接收站编号
	 */
	private Integer recStationId;
	
	/**
	 * 接收车道号：E01 入口01道   X10  出口10道  向其他站或分中心发送时为空‘’
	 * 向车道发送时或本站接收是为‘000’
	 */
	private String recLaneNo;
	
	/**
	 * 卡箱操作时间
	 */
	private Date opTime;
	
	/**
	 * 操作类型：相对于系统登录者而言，0发送 1接收
	 */
	private Integer opType;
	
	/**
	 * 卡箱状态：1 在本站车道(可发送至站和车道交接)  2 在本站库存(可发送至车道或其他站) 3 站间或站与分中心流动 4 站内流动(从站发送到车道，车道尚未接收，或相反)
	 */
	private Integer status;
	
	/**
	 * 备用1(0正常接收、1强制接收)
	 */
	private Integer spare1;

	/**
	 * 丢失
	 */
	private Integer spare2;
	
	/**
	 * 消失
	 */
	private Integer spare3;
	
	/**
	 * 额外
	 */
	private String spare4;

	public String getCardBoxLabelNo() {
		return cardBoxLabelNo;
	}

	public void setCardBoxLabelNo(String cardBoxLabelNo) {
		this.cardBoxLabelNo = cardBoxLabelNo;
	}

	public String getCardBoxNo() {
		return cardBoxNo;
	}

	public void setCardBoxNo(String cardBoxNo) {
		this.cardBoxNo = cardBoxNo;
	}

	public String getCardBoxCycleTimes() {
		return cardBoxCycleTimes;
	}

	public void setCardBoxCycleTimes(String cardBoxCycleTimes) {
		this.cardBoxCycleTimes = cardBoxCycleTimes;
	}

	public Integer getCardNum() {
		return cardNum;
	}

	public void setCardNum(Integer cardNum) {
		this.cardNum = cardNum;
	}

	public String getSenderNo() {
		return senderNo;
	}

	public void setSenderNo(String senderNo) {
		this.senderNo = senderNo;
	}

	public String getReceiverNo() {
		return receiverNo;
	}

	public void setReceiverNo(String receiverNo) {
		this.receiverNo = receiverNo;
	}

	public Integer getRecRoadId() {
		return recRoadId;
	}

	public void setRecRoadId(Integer recRoadId) {
		this.recRoadId = recRoadId;
	}

	public Integer getRecStationId() {
		return recStationId;
	}

	public void setRecStationId(Integer recStationId) {
		this.recStationId = recStationId;
	}

	public String getRecLaneNo() {
		return recLaneNo;
	}

	public void setRecLaneNo(String recLaneNo) {
		this.recLaneNo = recLaneNo;
	}

	public Date getOpTime() {
		return opTime;
	}

	public void setOpTime(Date opTime) {
		this.opTime = opTime;
	}

	public Integer getOpType() {
		return opType;
	}

	public void setOpType(Integer opType) {
		this.opType = opType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSpare1() {
		return spare1;
	}

	public void setSpare1(Integer spare1) {
		this.spare1 = spare1;
	}

	public Integer getSpare2() {
		return spare2;
	}

	public void setSpare2(Integer spare2) {
		this.spare2 = spare2;
	}

	public Integer getSpare3() {
		return spare3;
	}

	public void setSpare3(Integer spare3) {
		this.spare3 = spare3;
	}

	public String getSpare4() {
		return spare4;
	}

	public void setSpare4(String spare4) {
		this.spare4 = spare4;
	}

    @Override
    public String toString() {
        return "CardBoxOpList{" + "cardBoxLabelNo=" + cardBoxLabelNo + ", cardBoxNo=" + cardBoxNo + ", cardBoxCycleTimes=" + cardBoxCycleTimes + ", cardNum=" + cardNum + ", senderNo=" + senderNo + ", receiverNo=" + receiverNo + ", recRoadId=" + recRoadId + ", recStationId=" + recStationId + ", recLaneNo=" + recLaneNo + ", opTime=" + opTime + ", opType=" + opType + ", status=" + status + ", spare1=" + spare1 + ", spare2=" + spare2 + ", spare3=" + spare3 + ", spare4=" + spare4 + '}';
    }
	
	

}
