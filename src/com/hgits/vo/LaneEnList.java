package com.hgits.vo;

import java.util.Date;

public class LaneEnList {

    /**
     * 记录编码
     */
    private Integer recordId;
    /*
     * 流水编号
     */
    private String listNo;
    /*
     * 路段编码
     */
    private Integer roadId;
    /*
     * 收费站编码
     */
    private Integer stationId;
    /*
     * 收费站名
     */
    private String stationName;
    /*
     * 车道编号
     */
    private String laneNo;
    /*
     * 车道编码
     */
    private Integer laneId;
    /*
     * 车道类型编码
     */
    private Integer laneType;
    /*
     * 车型编码
     */
    private Integer vehType;
    /*
     * 车种编码
     */
    private Integer vehClass;
    /*
     * 车牌
     */
    private String vehPlate;
    /*
     * 车牌颜色
     */
    private Integer vehPlateColor;
    /*
     * 上班时间
     */
    private Date loginTime;
    /*
     * 工班号
     */
    private Integer squadId;
    /*
     * 工班日期
     */
    private Date squadDate;
    /*
     * 收费员工号
     */
    private String operatorNo;
    /*
     * 收费员姓名
     */
    private String operatorName;
    /*
     * 身份卡卡号
     */
    private String opCardNo;
    /*
     * 操作时间
     */
    private Date opTime;
    /*
     * 通行券类型
     */
    private Integer ticketType;
    /*
     * 通行卡卡号
     */
    private String passCardNo;
    /*
     * 卡箱号
     */
    private String cardBoxNo;
    /*
     * ETC卡类型
     */
    private Integer xTCardType;
    /*
     * ETC卡卡号
     */
    private String xTCardNo;
    /*
     * OBU编码
     */
    private String oBUId;
    /*
     * 车牌图像序号
     */
    private String plateImageNo;
    /*
     * 全景图像序号
     */
    private String vehImageNo;
    /*
     * 特殊事件
     */
    private String spEvent;
    /*
     * 通行卡观察代码
     */
    private String cardSelcetCode;
    /*
     * 收费操作观察代码
     */
    private String opSelcetCode;
    /*
     * 车费观察代码
     */
    private String vehChargeSelcetCode;
    /*
     * 车辆通行观察代码
     */
    private String vehPassSelcetCode;
    /*
     * 车型自动识别
     */
    private Integer vehTypeAuto;
    /*
     * 车牌自动识别
     */
    private String vehPlateAuto;
    /*
     * 识别车牌颜色
     */
    private Integer plateAutoColor;
    /*
     * PSAM卡编号
     */
    private String pSAMCardNo;
    /*
     * 程序版本号
     */
    private String programVer;
    /*
     * 备用1
     */
    private Integer spare1;
    /*
     * 备用2
     */
    private Integer spare2;
    /*
     * 备用3
     */
    private String spare3;
    /*
     * 备用4
     */
    private String spare4;
    /*
     * 传输标志
     */
    private Integer transferTag;
    /*
     * CRC32校验码
     */
    private Integer verifyCode;

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getListNo() {
        return listNo;
    }

    public void setListNo(String listNo) {
        this.listNo = listNo;
    }

    public Integer getRoadId() {
        return roadId;
    }

    public void setRoadId(Integer roadId) {
        this.roadId = roadId;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getLaneNo() {
        return laneNo;
    }

    public void setLaneNo(String laneNo) {
        this.laneNo = laneNo;
    }

    public Integer getLaneId() {
        return laneId;
    }

    public void setLaneId(Integer laneId) {
        this.laneId = laneId;
    }

    public Integer getLaneType() {
        return laneType;
    }

    public void setLaneType(Integer laneType) {
        this.laneType = laneType;
    }

    public Integer getVehType() {
        return vehType;
    }

    public void setVehType(Integer vehType) {
        this.vehType = vehType;
    }

    public Integer getVehClass() {
        return vehClass;
    }

    public void setVehClass(Integer vehClass) {
        this.vehClass = vehClass;
    }

    public String getVehPlate() {
        return vehPlate;
    }

    public void setVehPlate(String vehPlate) {
        this.vehPlate = vehPlate;
    }

    public Integer getVehPlateColor() {
        return vehPlateColor;
    }

    public void setVehPlateColor(Integer vehPlateColor) {
        this.vehPlateColor = vehPlateColor;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Integer getSquadId() {
        return squadId;
    }

    public void setSquadId(Integer squadId) {
        this.squadId = squadId;
    }

    public Date getSquadDate() {
        return squadDate;
    }

    public void setSquadDate(Date squadDate) {
        this.squadDate = squadDate;
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

    public String getOpCardNo() {
        return opCardNo;
    }

    public void setOpCardNo(String opCardNo) {
        this.opCardNo = opCardNo;
    }

    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }

    public Integer getTicketType() {
        return ticketType;
    }

    public void setTicketType(Integer ticketType) {
        this.ticketType = ticketType;
    }

    public String getPassCardNo() {
        return passCardNo;
    }

    public void setPassCardNo(String passCardNo) {
        this.passCardNo = passCardNo;
    }

    public String getCardBoxNo() {
        return cardBoxNo;
    }

    public void setCardBoxNo(String cardBoxNo) {
        this.cardBoxNo = cardBoxNo;
    }

    public Integer getxTCardType() {
        return xTCardType;
    }

    public void setxTCardType(Integer xTCardType) {
        this.xTCardType = xTCardType;
    }

    public String getxTCardNo() {
        return xTCardNo;
    }

    public void setxTCardNo(String xTCardNo) {
        this.xTCardNo = xTCardNo;
    }

    public String getoBUId() {
        return oBUId;
    }

    public void setoBUId(String oBUId) {
        this.oBUId = oBUId;
    }

    public String getPlateImageNo() {
        return plateImageNo;
    }

    public void setPlateImageNo(String plateImageNo) {
        this.plateImageNo = plateImageNo;
    }

    public String getVehImageNo() {
        return vehImageNo;
    }

    public void setVehImageNo(String vehImageNo) {
        this.vehImageNo = vehImageNo;
    }

    public String getSpEvent() {
        return spEvent;
    }

    public void setSpEvent(String spEvent) {
        this.spEvent = spEvent;
    }

    public String getCardSelcetCode() {
        return cardSelcetCode;
    }

    public void setCardSelcetCode(String cardSelcetCode) {
        this.cardSelcetCode = cardSelcetCode;
    }

    public String getOpSelcetCode() {
        return opSelcetCode;
    }

    public void setOpSelcetCode(String opSelcetCode) {
        this.opSelcetCode = opSelcetCode;
    }

    public String getVehChargeSelcetCode() {
        return vehChargeSelcetCode;
    }

    public void setVehChargeSelcetCode(String vehChargeSelcetCode) {
        this.vehChargeSelcetCode = vehChargeSelcetCode;
    }

    public String getVehPassSelcetCode() {
        return vehPassSelcetCode;
    }

    public void setVehPassSelcetCode(String vehPassSelcetCode) {
        this.vehPassSelcetCode = vehPassSelcetCode;
    }

    public Integer getVehTypeAuto() {
        return vehTypeAuto;
    }

    public void setVehTypeAuto(Integer vehTypeAuto) {
        this.vehTypeAuto = vehTypeAuto;
    }

    public String getVehPlateAuto() {
        return vehPlateAuto;
    }

    public void setVehPlateAuto(String vehPlateAuto) {
        this.vehPlateAuto = vehPlateAuto;
    }

    public Integer getPlateAutoColor() {
        return plateAutoColor;
    }

    public void setPlateAutoColor(Integer plateAutoColor) {
        this.plateAutoColor = plateAutoColor;
    }

    public String getpSAMCardNo() {
        return pSAMCardNo;
    }

    public void setpSAMCardNo(String pSAMCardNo) {
        this.pSAMCardNo = pSAMCardNo;
    }

    public String getProgramVer() {
        return programVer;
    }

    public void setProgramVer(String programVer) {
        this.programVer = programVer;
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

    public String getSpare3() {
        return spare3;
    }

    public void setSpare3(String spare3) {
        this.spare3 = spare3;
    }

    public String getSpare4() {
        return spare4;
    }

    public void setSpare4(String spare4) {
        this.spare4 = spare4;
    }

    public Integer getTransferTag() {
        return transferTag;
    }

    public void setTransferTag(Integer transferTag) {
        this.transferTag = transferTag;
    }

    public Integer getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(Integer verifyCode) {
        this.verifyCode = verifyCode;
    }
    
    @Override
    public String toString() {
        return "LaneEnList{" + "recordId=" + recordId + ", listNo=" + listNo + ", roadId=" + roadId + ", stationId=" + stationId + ", stationName=" + stationName + ", laneNo=" + laneNo + ", laneId=" + laneId + ", laneType=" + laneType + ", vehType=" + vehType + ", vehClass=" + vehClass + ", vehPlate=" + vehPlate + ", vehPlateColor=" + vehPlateColor + ", loginTime=" + loginTime + ", squadId=" + squadId + ", squadDate=" + squadDate + ", operatorNo=" + operatorNo + ", operatorName=" + operatorName + ", opCardNo=" + opCardNo + ", opTime=" + opTime + ", ticketType=" + ticketType + ", passCardNo=" + passCardNo + ", cardBoxNo=" + cardBoxNo + ", xTCardType=" + xTCardType + ", xTCardNo=" + xTCardNo + ", oBUId=" + oBUId + ", plateImageNo=" + plateImageNo + ", vehImageNo=" + vehImageNo + ", spEvent=" + spEvent + ", cardSelcetCode=" + cardSelcetCode + ", opSelcetCode=" + opSelcetCode + ", vehChargeSelcetCode=" + vehChargeSelcetCode + ", vehPassSelcetCode=" + vehPassSelcetCode + ", vehTypeAuto=" + vehTypeAuto + ", vehPlateAuto=" + vehPlateAuto + ", plateAutoColor=" + plateAutoColor + ", pSAMCardNo=" + pSAMCardNo + ", programVer=" + programVer + ", spare1=" + spare1 + ", spare2=" + spare2 + ", spare3=" + spare3 + ", spare4=" + spare4 + ", transferTag=" + transferTag + ", verifyCode=" + verifyCode + '}';
    }

}
