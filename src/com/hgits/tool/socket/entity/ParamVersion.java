package com.hgits.tool.socket.entity;

/**
 * 参数版本
 *
 * @author hjz
 */
public class ParamVersion extends BaseConfirm {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String laneVersion = "详细";
    private String tb_TravelTime="";
    private String tb_bridgeextrachargestd="";
    private String tb_Issuer="";
    private String tb_Operator="";
    private String tb_RoadChargeStd="";
    private String tb_Station="";
    private String tb_UserRate="";
    private String tb_VehPlateBWG="";
    private String tb_VehRoute1="";
    private String tb_VehRoute2="";
    private String tb_VehRoute3="";
    private String tb_VehRoute4="";
    private String tb_VehRoute5="";
    private String tb_VehRoute7="";
    private String tb_XTCardBlackDeltaList="";
    private String tb_XTCardBlackBaseList="";
    private String tb_OverloadPrice="";

    @Override
    public String toString() {
        return "{\"laneVersion\":\"" + laneVersion + "\","
                + "\"tb_TravelTime\":\"" + tb_TravelTime + "\","
                + "\"tb_bridgeextrachargestd\":\"" + tb_bridgeextrachargestd + "\","
                + "\"tb_Issuer\":\"" + tb_Issuer + "\","
                + "\"tb_Operator\":\"" + tb_Operator + "\","
                + "\"tb_RoadChargeStd\":\"" + tb_RoadChargeStd + "\","
                + "\"tb_Station\":\"" + tb_Station + "\","
                + "\"tb_UserRate\":\"" + tb_UserRate + "\","
                + "\"tb_VehPlateBWG\":\"" + tb_VehPlateBWG + "\","
                + "\"tb_VehRoute1\":\"" + tb_VehRoute1 + "\","
                + "\"tb_VehRoute2\":\"" + tb_VehRoute2 + "\","
                + "\"tb_VehRoute3\":\"" + tb_VehRoute3 + "\","
                + "\"tb_VehRoute4\":\"" + tb_VehRoute4 + "\","
                + "\"tb_VehRoute5\":\"" + tb_VehRoute5 + "\","
                + "\"tb_VehRoute7\":\"" + tb_VehRoute7 + "\","
                + "\"tb_XTCardBlackDeltaList\":\"" + tb_XTCardBlackDeltaList + "\","
                + "\"tb_XTCardBlackBaseList\":\"" + tb_XTCardBlackBaseList + "\","
                + "\"tb_OverloadPrice\":\"" + tb_OverloadPrice + "\""
                + "}";
    }


    public String getLaneVersion() {
        return laneVersion;
    }

    public void setLaneVersion(String laneVersion) {
        this.laneVersion = laneVersion;
    }

    public String getTb_TravelTime() {
        return tb_TravelTime;
    }

    public void setTb_TravelTime(String tb_TravelTime) {
        this.tb_TravelTime = tb_TravelTime;
    }

    public String getTb_bridgeextrachargestd() {
        return tb_bridgeextrachargestd;
    }

    public void setTb_bridgeextrachargestd(String tb_bridgeextrachargestd) {
        this.tb_bridgeextrachargestd = tb_bridgeextrachargestd;
    }

    public String getTb_Issuer() {
        return tb_Issuer;
    }

    public void setTb_Issuer(String tb_Issuer) {
        this.tb_Issuer = tb_Issuer;
    }

    public String getTb_Operator() {
        return tb_Operator;
    }

    public void setTb_Operator(String tb_Operator) {
        this.tb_Operator = tb_Operator;
    }

    public String getTb_RoadChargeStd() {
        return tb_RoadChargeStd;
    }

    public void setTb_RoadChargeStd(String tb_RoadChargeStd) {
        this.tb_RoadChargeStd = tb_RoadChargeStd;
    }

    public String getTb_Station() {
        return tb_Station;
    }

    public void setTb_Station(String tb_Station) {
        this.tb_Station = tb_Station;
    }

    public String getTb_UserRate() {
        return tb_UserRate;
    }

    public void setTb_UserRate(String tb_UserRate) {
        this.tb_UserRate = tb_UserRate;
    }

    public String getTb_VehPlateBWG() {
        return tb_VehPlateBWG;
    }

    public void setTb_VehPlateBWG(String tb_VehPlateBWG) {
        this.tb_VehPlateBWG = tb_VehPlateBWG;
    }

    public String getTb_VehRoute1() {
        return tb_VehRoute1;
    }

    public void setTb_VehRoute1(String tb_VehRoute1) {
        this.tb_VehRoute1 = tb_VehRoute1;
    }

    public String getTb_VehRoute2() {
        return tb_VehRoute2;
    }

    public void setTb_VehRoute2(String tb_VehRoute2) {
        this.tb_VehRoute2 = tb_VehRoute2;
    }

    public String getTb_VehRoute3() {
        return tb_VehRoute3;
    }

    public void setTb_VehRoute3(String tb_VehRoute3) {
        this.tb_VehRoute3 = tb_VehRoute3;
    }

    public String getTb_VehRoute4() {
        return tb_VehRoute4;
    }

    public void setTb_VehRoute4(String tb_VehRoute4) {
        this.tb_VehRoute4 = tb_VehRoute4;
    }

    public String getTb_VehRoute5() {
        return tb_VehRoute5;
    }

    public void setTb_VehRoute5(String tb_VehRoute5) {
        this.tb_VehRoute5 = tb_VehRoute5;
    }

    public String getTb_VehRoute7() {
        return tb_VehRoute7;
    }

    public void setTb_VehRoute7(String tb_VehRoute7) {
        this.tb_VehRoute7 = tb_VehRoute7;
    }

    public String getTb_XTCardBlackDeltaList_send() {
        return tb_XTCardBlackDeltaList;
    }

    public void setTb_XTCardBlackDeltaList_send(String tb_XTCardBlackDeltaList_send) {
        this.tb_XTCardBlackDeltaList = tb_XTCardBlackDeltaList_send;
    }

    public String getTb_XTCardBlackBaseList_send() {
        return tb_XTCardBlackBaseList;
    }

    public void setTb_XTCardBlackBaseList_send(String tb_XTCardBlackBaseList_send) {
        this.tb_XTCardBlackBaseList = tb_XTCardBlackBaseList_send;
    }

    public String getTb_OverloadPrice() {
        return tb_OverloadPrice;
    }

    public void setTb_OverloadPrice(String tb_OverloadPrice) {
        this.tb_OverloadPrice = tb_OverloadPrice;
    }

}
