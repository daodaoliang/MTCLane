package com.hgits.vo;

import java.util.Date;

/**
 * 代收流水
 *
 * @author Wang Guodong
 */
public class ColList {
//流水序号，同一工班内，由1递增		

    private int recordNo;
//流水号	车道号（3位）+交易时间（14位）+流水序号（4位），如：E01201208010759460001
    private String ListNo;
//工班号	由上班时间决定23:30-07:30为1，07:30-15:30为2,15：30-23:30为3
    private int squadNo;
//工班日期 由上班时间决定	,如2012-08-01 00:00:00.000	
    private Date squadDate;
//工班日期，如20120801A	
    private String squad;
//收费员姓名
    private String operatorName;
//收费员号	
    private String operatorNo;
//代收金额(单位：元)	正数表示代收，负数表示代退
    private int collMoney;
//通行费（单位：元）不含代收金额
    private int passMoney;
//交易时间	
    private Date chargeTime;
//费率版本	
    private int verNo;
//车型
    private int carType;
//货车车型
    private int tructType;
//入口站编码
    private String inStationNo;
//入口站名称
    private String inStationName;
//总轴重	（单位：千克）
    private int axleWeight;
//总限重（单位：千克）	
    private int axleWeightLimit;
//车道号		
    private String laneNo;
//本站站编码
    private String stationNo;
//本站站名称
    private String stationName;
//车道类型，出入口（入口1，出口2）
    private int laneType;
//车辆计数		正常是1，重打是0
    private int vehCnt;
//特殊事件,军警车 1;绿通车 2;重打 4
    private int spEvent;
//默认是0
    private int backUp1;
//默认是0
    private int backUp2;
//默认是空
    private String backUp3;
//默认是空
    private String backUp4;

    public int getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(int no) {
        this.recordNo = no;
    }

    public String getListNo() {
        return ListNo;
    }

    public void setListNo(String ListNo) {
        this.ListNo = ListNo;
    }

    public int getSquadNo() {
        return squadNo;
    }

    public void setSquadNo(int squadNo) {
        this.squadNo = squadNo;
    }

    public Date getSquadDate() {
        return squadDate;
    }

    public void setSquadDate(Date squadDate) {
        this.squadDate = squadDate;
    }

    public String getSquad() {
        return squad;
    }

    public void setSquad(String squad) {
        this.squad = squad;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    public int getCollMoney() {
        return collMoney;
    }

    public void setCollMoney(int collMoney) {
        this.collMoney = collMoney;
    }

    public int getPassMoney() {
        return passMoney;
    }

    public void setPassMoney(int passMoney) {
        this.passMoney = passMoney;
    }

    public Date getChargeTime() {
        return chargeTime;
    }

    public void setChargeTime(Date chargeTime) {
        this.chargeTime = chargeTime;
    }

    public int getVerNo() {
        return verNo;
    }

    public void setVerNo(int verNo) {
        this.verNo = verNo;
    }

    public int getCarType() {
        return carType;
    }

    public void setCarType(int carType) {
        this.carType = carType;
    }

    public int getTructType() {
        return tructType;
    }

    public void setTructType(int tructType) {
        this.tructType = tructType;
    }

    public String getInStationNo() {
        return inStationNo;
    }

    public void setInStationNo(String inStationNo) {
        this.inStationNo = inStationNo;
    }

    public String getInStationName() {
        return inStationName;
    }

    public void setInStationName(String inStationName) {
        this.inStationName = inStationName;
    }

    public int getAxleWeight() {
        return axleWeight;
    }

    public void setAxleWeight(int axleWeight) {
        this.axleWeight = axleWeight;
    }

    public int getAxleWeightLimit() {
        return axleWeightLimit;
    }

    public void setAxleWeightLimit(int axleWeightLimit) {
        this.axleWeightLimit = axleWeightLimit;
    }

    public String getLaneNo() {
        return laneNo;
    }

    public void setLaneNo(String laneNo) {
        this.laneNo = laneNo;
    }

    public String getStationNo() {
        return stationNo;
    }

    public void setStationNo(String stationNo) {
        this.stationNo = stationNo;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getLaneType() {
        return laneType;
    }

    public void setLaneType(int laneType) {
        this.laneType = laneType;
    }

    public int getVehCnt() {
        return vehCnt;
    }

    public void setVehCnt(int vehCnt) {
        this.vehCnt = vehCnt;
    }

    public int getSpEvent() {
        return spEvent;
    }

    public void setSpEvent(int spEvent) {
        this.spEvent = spEvent;
    }

    public int getBackUp1() {
        return backUp1;
    }

    public void setBackUp1(int backUp1) {
        this.backUp1 = backUp1;
    }

    public int getBackUp2() {
        return backUp2;
    }

    public void setBackUp2(int backUp2) {
        this.backUp2 = backUp2;
    }

    public String getBackUp3() {
        return backUp3;
    }

    public void setBackUp3(String backUp3) {
        this.backUp3 = backUp3;
    }

    public String getBackUp4() {
        return backUp4;
    }

    public void setBackUp4(String backUp4) {
        this.backUp4 = backUp4;
    }

    @Override
    public String toString() {
        return "CollectList{" + "no=" + recordNo + ", ListNo=" + ListNo + ", squadNo=" + squadNo + ", squadDate=" + squadDate + ", squad=" + squad + ", operatorName=" + operatorName + ", operatorNo=" + operatorNo + ", collMoney=" + collMoney + ", passMoney=" + passMoney + ", chargeTime=" + chargeTime + ", verNo=" + verNo + ", carType=" + carType + ", tructType=" + tructType + ", enStationNo=" + inStationNo + ", inStationName=" + inStationName + ", axleWeight=" + axleWeight + ", axleWeightLimit=" + axleWeightLimit + ", laneNo=" + laneNo + ", stationNo=" + stationNo + ", stationName=" + stationName + ", laneType=" + laneType + ", vehCnt=" + vehCnt + ", spEvent=" + spEvent + ", backUp1=" + backUp1 + ", backUp2=" + backUp2 + ", backUp3=" + backUp3 + ", backUp4=" + backUp4 + '}';
    }

}
