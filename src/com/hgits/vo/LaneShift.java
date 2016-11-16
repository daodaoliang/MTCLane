/**
 *
 */
package com.hgits.vo;

import java.util.Date;
import java.util.logging.Logger;

/**
 * 车道工班日志
 *
 * @author wh
 *
 */
public class LaneShift {

    private static int laneShiftNo;//班次流水号1-255
    private int roadId;//路段编码
    private int stationId;//收费站编码
    private String laneNo;//车道编号
    private int laneId;//车道编码
    private String operatorId;//收费员工号
    private String operatorName;//收费员姓名
    private String opCardNo;//身份卡卡号
//    private String opCardId;
    private Date squadDate;//工班日期
    private int squadId;//工班号
    private Date loginTime;//上班时间
    private Date logoutTime;//下班时间
    private int enExFlag;//出入口标识 1 入口上班  2 出口上班
    private int invStartId;//发票起始编号
    private int invEndId;//发票结束编号
    private int invPrintCnt;//发票打印数
    private int badInVoiceCnt;//废票数
    private int passCardUseCnt;//通行卡过车数
//    private int upCardUseCnt;
    private int xtCardUseCnt;//ETC卡过车数
    private int freeVehCnt;//免费过车数
    private int noCardVehCnt;//无卡车数（无卡，残卡，不可读卡）
    private int vehCnt;//总过车数
    private int listCnt;//流水记录数
    private int upCardMoney;//银联卡金额（分）
    private int xtCardMoney;//ETC卡金额（分）
    private int cashMoney;//现金金额（分）
    private int totalMoney;//总金额（分）
    private int endShiftFlag;//下班完整性标志 1
//    private int recordType;//
    private int listName;//流水文件名，同班次流水号
    private int verifyCode;//校验码
    private int spare1;//备用1
    private int spare2;//备用2
    private String spare3;//备用3
    private String spare4;//备用4
//    private int cashUseCnt;
//    private int spUseCnt;

    /**
     * 发票打印数加1
     */
    public void addInvPrintCnt() {
        invPrintCnt++;
    }

    /**
     * 废票数加1
     */
    public void addBadInVoiceCnt() {
        badInVoiceCnt++;
    }

    /**
     * 通行卡过车数加1
     */
    public void addPassCardUseCnt() {
        passCardUseCnt++;
    }

    /**
     * 通行卡过车数减1
     */
    public void decreasePassCardUseCnt() {
        passCardUseCnt--;
    }

    /**
     * ETC卡过车数加1
     */
    public void addXtCardUseCnt() {
        xtCardUseCnt++;
    }

    /**
     * 免费车加1
     */
    public void addFreeVehCnt() {
        freeVehCnt++;
    }

    /**
     * 无卡车加1
     */
    public void addnoCardVehCnt() {
        noCardVehCnt++;
    }

    /**
     * 总过车数加1
     */
    public void addvehCnt() {
        vehCnt++;
    }

    /**
     * 流水记录数加1
     */
    public void addListCnt() {
        listCnt++;
    }

    /**
     * 银联卡金额增加（单位：分）
     *
     * @param i 增加的金额（单位：分）
     */
    public void addUpCardMoney(int i) {
        upCardMoney += i;
    }

    /**
     * ETC卡金额增加（单位：分）
     *
     * @param i 增加的金额（单位：分）
     */
    public void addXtCardMoney(int i) {
        xtCardMoney += i;
    }

    /**
     * 现金金额增加（单位：分）
     *
     * @param i 增加的金额（单位：分）
     */
    public void addCashMoney(int i) {
        cashMoney += i;
    }

    /**
     * 总金额增加（单位：分）
     *
     * @param i 增加的金额（单位：分）
     */
    public void addTotalMoney(int i) {
        totalMoney += i;
    }

    /**
     * 创建班次流水号
     */
    public static void createLaneShiftNo() {
        laneShiftNo++;
        if (laneShiftNo == 256) {
            laneShiftNo = 1;
        }
    }

    public String getLaneNo() {
        return laneNo;
    }

    public void setLaneNo(String laneNo) {
        this.laneNo = laneNo;
    }

    public int getLaneShiftNo() {
        return laneShiftNo;
    }

    public int getRoadId() {
        return roadId;
    }

    public void setRoadId(int roadId) {
        this.roadId = roadId;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public int getLaneId() {
        return laneId;
    }

    public void setLaneId(int laneId) {
        this.laneId = laneId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
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

    public Date getSquadDate() {
        return squadDate;
    }

    public void setSquadDate(Date squadDate) {
        this.squadDate = squadDate;
    }

    public int getSquadId() {
        return squadId;
    }

    public void setSquadId(int squadId) {
        this.squadId = squadId;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    public int getEnExFlag() {
        return enExFlag;
    }

    public void setEnExFlag(int enExFlag) {
        this.enExFlag = enExFlag;
    }

    public int getInvStartId() {
        return invStartId;
    }

    public void setInvStartId(int invStartId) {
        this.invStartId = invStartId;
    }

    public int getInvEndId() {
        return invEndId;
    }

    public void setInvEndId(int invEndId) {
        this.invEndId = invEndId;
    }

    public int getInvPrintCnt() {
        return invPrintCnt;
    }

    public void setInvPrintCnt(int invPrintCnt) {
        this.invPrintCnt = invPrintCnt;
    }

    public int getBadInVoiceCnt() {
        return badInVoiceCnt;
    }

    public void setBadInVoiceCnt(int badInVoiceCnt) {
        this.badInVoiceCnt = badInVoiceCnt;
    }

    public int getPassCardUseCnt() {
        return passCardUseCnt;
    }

    public void setPassCardUseCnt(int passCardUseCnt) {
        this.passCardUseCnt = passCardUseCnt;
    }

    public int getXtCardUseCnt() {
        return xtCardUseCnt;
    }

    public void setXtCardUseCnt(int xtCardUseCnt) {
        this.xtCardUseCnt = xtCardUseCnt;
    }

    public int getFreeVehCnt() {
        return freeVehCnt;
    }

    public void setFreeVehCnt(int freeVehCnt) {
        this.freeVehCnt = freeVehCnt;
    }

    public int getNoCardVehCnt() {
        return noCardVehCnt;
    }

    public void setNoCardVehCnt(int noCardVehCnt) {
        this.noCardVehCnt = noCardVehCnt;
    }

    public int getVehCnt() {
        return vehCnt;
    }

    public void setVehCnt(int vehCnt) {
        this.vehCnt = vehCnt;
    }

    public int getListCnt() {
        return listCnt;
    }

    public void setListCnt(int listCnt) {
        this.listCnt = listCnt;
    }

    public int getUpCardMoney() {
        return upCardMoney;
    }

    public void setUpCardMoney(int upCardMoney) {
        this.upCardMoney = upCardMoney;
    }

    public int getXtCardMoney() {
        return xtCardMoney;
    }

    public void setXtCardMoney(int xtCardMoney) {
        this.xtCardMoney = xtCardMoney;
    }

    public int getCashMoney() {
        return cashMoney;
    }

    public void setCashMoney(int cashMoney) {
        this.cashMoney = cashMoney;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }

    public int getEndShiftFlag() {
        return endShiftFlag;
    }

    public void setEndShiftFlag(int endShiftFlag) {
        this.endShiftFlag = endShiftFlag;
    }

    public int getListName() {
        return listName;
    }

    public void setListName(int listName) {
        this.listName = listName;
    }

    public int getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(int verifyCode) {
        this.verifyCode = verifyCode;
    }

    public int getSpare1() {
        return spare1;
    }

    public void setSpare1(int spare1) {
        this.spare1 = spare1;
    }

    public int getSpare2() {
        return spare2;
    }

    public void setSpare2(int spare2) {
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

    public void setLaneShiftNo(int laneShiftNo) {
        LaneShift.laneShiftNo = laneShiftNo;
    }

    @Override
    public String toString() {
        return "LaneShift{" + "laneShiftNo=" + laneShiftNo + ", roadId=" + roadId + ", stationId=" + stationId + ", laneNo=" + laneNo + ", laneId=" + laneId + ", operatorId=" + operatorId + ", operatorName=" + operatorName + ", opCardNo=" + opCardNo + ", squadDate=" + squadDate + ", squadId=" + squadId + ", loginTime=" + loginTime + ", logoutTime=" + logoutTime + ", enExFlag=" + enExFlag + ", invStartId=" + invStartId + ", invEndId=" + invEndId + ", invPrintCnt=" + invPrintCnt + ", badInVoiceCnt=" + badInVoiceCnt + ", passCardUseCnt=" + passCardUseCnt + ", xtCardUseCnt=" + xtCardUseCnt + ", freeVehCnt=" + freeVehCnt + ", noCardVehCnt=" + noCardVehCnt + ", vehCnt=" + vehCnt + ", listCnt=" + listCnt + ", upCardMoney=" + upCardMoney + ", xtCardMoney=" + xtCardMoney + ", cashMoney=" + cashMoney + ", totalMoney=" + totalMoney + ", endShiftFlag=" + endShiftFlag + ", listName=" + listName + ", verifyCode=" + verifyCode + ", spare1=" + spare1 + ", spare2=" + spare2 + ", spare3=" + spare3 + ", spare4=" + spare4 + '}';
    }
}
