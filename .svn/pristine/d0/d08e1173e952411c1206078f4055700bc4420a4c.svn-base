package com.hgits.realTimePath.vo;

import com.hgits.realTimePath.ByteUtil;
import com.hgits.realTimePath.RTUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

/**
 * 路径识别所需收费记录
 *
 * @author Wang Guodong
 */
public class CostRecord {

    private String orderSN;//交易流水号,返回RequestPathResp中下发的流水号
    private String totalDis;//车辆行驶的总里程数     数值精确到小数点后四位   单位：公里（单位不包含在Value中）
    private int downLevel;//是否降级 1-是   0-不是
    private String entryStationid;//费率车辆入口收费站的编号,由路段网络号+站号组成（如羊楼司：0501)
    private String trafficStationid;//物理车辆入口收费站的编号,由路段网络号+站号组成（如羊楼司：0501)
    private String entryLaneid;//车辆入口收费站车道号
    private Date entryTime;//车型入高速时间
    private String entryStaffid;//入口收费员编号
    private String vehColor;//出口车牌颜色 如：黄
    private short vehClass;//出口车辆类型
    private String plateNum;//出口车牌号码
    private String detectWeight;//检测重量
    private String fareWeight;//车辆计费重量 注：10kg为单位,如1234代表12340Kg,单位不包含在Value
    private String limitWeight;//车辆限重 注：10kg为单位,如1234代表12340Kg,单位不包含在Value
    private String exitStationid;//车辆出口收费站的编号
    private String exitLaneid;//出站车道编号
    private short exitChannelClass = 1;//出站车道类型 1表示MTC车道，2表示ETC车道
    private Date exitTime;//出站时间
    private String cardNum;//收费卡卡号
    private short chargeType;//收费类型：0-收费 1-免费
    private int cardType;//卡类型
    private String groupTime;//工班日期(格式为：YYYYMMD，例:20130512)MTC为空
    private Date jobStart;//出口车道收费员上班时间
    private int tranNum;//交易号
    private String fareVer;//费率版本请求路径接口返回的字段，降级时车道收费系统自己的费率版本
    private String saleRate;//折扣率
    private String imgPath;//出口车辆图片（大图）URL如：http://192.168.1.1/pic/stest.jpg
    private String exitStaffid;//收费员的员工编号
    private String orderPay;//应付金额,精确到小数点后两位
    private String collectionPay;//应付代收费用,目前是城通费   精确到小数点后两位
    private String realFee;//实际收取的费用，精确到小数点后两位
    private String collectionFee;//实际代收费用,目前是城通费   精确到小数点后两位
    private int exitMop;//出口付款方式

    public String getCollectionPay() {
        return collectionPay;
    }

    public void setCollectionPay(String collectionPay) {
        this.collectionPay = collectionPay;
    }

    public String getTotalDis() {
        return totalDis;
    }

    public void setTotalDis(String totalDis) {
        this.totalDis = totalDis;
    }

    public int getDownLevel() {
        return downLevel;
    }

    public void setDownLevel(int downLevel) {
        this.downLevel = downLevel;
    }

    public String getGroupTime() {
        return groupTime;
    }

    public void setGroupTime(String groupTime) {
        this.groupTime = groupTime;
    }

    public String getCollectionFee() {
        return collectionFee;
    }

    public void setCollectionFee(String collectionFee) {
        this.collectionFee = collectionFee;
    }

    public String getOrderSN() {
        return orderSN;
    }

    public void setOrderSN(String orderSN) {
        this.orderSN = orderSN;
    }

    public String getEntryStationid() {
        return entryStationid;
    }

    public void setEntryStationid(String entryStationid) {
        this.entryStationid = entryStationid;
    }

    public String getTrafficStationid() {
        return trafficStationid;
    }

    public void setTrafficStationid(String trafficStationid) {
        this.trafficStationid = trafficStationid;
    }

    public String getEntryLaneid() {
        return entryLaneid;
    }

    public void setEntryLaneid(String entryLaneid) {
        this.entryLaneid = entryLaneid;
    }

    public Date getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    public String getEntryStaffid() {
        return entryStaffid;
    }

    public void setEntryStaffid(String entryStaffid) {
        this.entryStaffid = entryStaffid;
    }

    public String getVehColor() {
        return vehColor;
    }

    public void setVehColor(String vehColor) {
        this.vehColor = vehColor;
    }

    public short getVehClass() {
        return vehClass;
    }

    public void setVehClass(short vehClass) {
        this.vehClass = vehClass;
    }

    public String getPlateNum() {
        return plateNum;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum;
    }

    public String getDetectWeight() {
        return detectWeight;
    }

    public void setDetectWeight(String detectWeight) {
        this.detectWeight = detectWeight;
    }

    public String getFareWeight() {
        return fareWeight;
    }

    public void setFareWeight(String fareWeight) {
        this.fareWeight = fareWeight;
    }

    public String getLimitWeight() {
        return limitWeight;
    }

    public void setLimitWeight(String limitWeight) {
        this.limitWeight = limitWeight;
    }

    public String getExitStationid() {
        return exitStationid;
    }

    public void setExitStationid(String exitStationid) {
        this.exitStationid = exitStationid;
    }

    public String getExitLaneid() {
        return exitLaneid;
    }

    public void setExitLaneid(String exitLaneid) {
        this.exitLaneid = exitLaneid;
    }

    public Date getExitTime() {
        return exitTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public short getChargeType() {
        return chargeType;
    }

    public void setChargeType(short chargeType) {
        this.chargeType = chargeType;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public Date getJobStart() {
        return jobStart;
    }

    public void setJobStart(Date jobStart) {
        this.jobStart = jobStart;
    }

    public int getTranNum() {
        return tranNum;
    }

    public void setTranNum(int tranNum) {
        this.tranNum = tranNum;
    }

    public String getFareVer() {
        return fareVer;
    }

    public void setFareVer(String fareVer) {
        this.fareVer = fareVer;
    }

    public String getSaleRate() {
        return saleRate;
    }

    public void setSaleRate(String saleRate) {
        this.saleRate = saleRate;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getExitStaffid() {
        return exitStaffid;
    }

    public void setExitStaffid(String exitStaffid) {
        this.exitStaffid = exitStaffid;
    }

    public String getOrderPay() {
        return orderPay;
    }

    public void setOrderPay(String orderPay) {
        this.orderPay = orderPay;
    }

    public String getRealFee() {
        return realFee;
    }

    public void setRealFee(String realFee) {
        this.realFee = realFee;
    }

    public int getExitMop() {
        return exitMop;
    }

    public void setExitMop(int exitMop) {
        this.exitMop = exitMop;
    }

    public short getExitChannelClass() {
        return exitChannelClass;
    }

    public void setExitChannelClass(short exitChannelClass) {
        this.exitChannelClass = exitChannelClass;
    }

    @Override
    public String toString() {
        return "CostRecord{" + "orderSN=" + orderSN + ", totalDis=" + totalDis + ", downLevel=" + downLevel + ", entryStationid=" + entryStationid + ", trafficStationid=" + trafficStationid + ", entryLaneid=" + entryLaneid + ", entryTime=" + entryTime + ", entryStaffid=" + entryStaffid + ", vehColor=" + vehColor + ", vehClass=" + vehClass + ", plateNum=" + plateNum + ", detectWeight=" + detectWeight + ", fareWeight=" + fareWeight + ", limitWeight=" + limitWeight + ", exitStationid=" + exitStationid + ", exitLaneid=" + exitLaneid + ", exitTime=" + exitTime + ", cardNum=" + cardNum + ", chargeType=" + chargeType + ", cardType=" + cardType + ", groupTime=" + groupTime + ", jobStart=" + jobStart + ", tranNum=" + tranNum + ", fareVer=" + fareVer + ", saleRate=" + saleRate + ", imgPath=" + imgPath + ", exitStaffid=" + exitStaffid + ", orderPay=" + orderPay + ", realFee=" + realFee + ", collectionFee=" + collectionFee + ", exitMop=" + exitMop + '}';
    }

    public String toJSONString() {
        return "{\"CostRecord\":{"
                + "\"OrderSN\":\"" + RTUtil.getString(orderSN) + "\","
                + "\"TotalDistance\":\"" + RTUtil.getString(totalDis) + "\","
                + "\"DownLevel\":\"" + RTUtil.getString(downLevel) + "\","
                + "\"EntryStationID\":\"" + RTUtil.getString(entryStationid) + "\","
                + "\"TrafficStationID\":\"" + RTUtil.getString(trafficStationid) + "\","
                + "\"EntryChannelNo\":\"" + RTUtil.getString(entryLaneid) + "\","
                + "\"EntryTime\":\"" + RTUtil.getString(entryTime, "yyyyMMddHHmmss") + "\","
                + "\"EntryStaff\":\"" + RTUtil.getString(entryStaffid) + "\","
                + "\"VehicleColor\":\"" + RTUtil.getString(vehColor) + "\","
                + "\"VehicleClass\":\"" + RTUtil.getString(vehClass) + "\","
                + "\"PlateNo\":\"" + RTUtil.getString(plateNum) + "\","
                + "\"DWeight\":\"" + RTUtil.getString(detectWeight) + "\","
                + "\"RealWeight\":\"" + RTUtil.getString(fareWeight) + "\","
                + "\"LimitWeight\":\"" + RTUtil.getString(limitWeight) + "\","
                + "\"ExitStationID\":\"" + RTUtil.getString(exitStationid) + "\","
                + "\"ExitChannelNo\":\"" + RTUtil.getString(exitLaneid) + "\","
                + "\"ExitChannelClass\":\"" + RTUtil.getString(exitChannelClass) + "\","
                + "\"ExitTime\":\"" + RTUtil.getString(exitTime, "yyyyMMddHHmmss") + "\","
                + "\"CardNo\":\"" + RTUtil.getString(cardNum) + "\","
                + "\"ChargeType\":\"" + RTUtil.getString(chargeType) + "\","
                + "\"CardType\":\"" + RTUtil.getString(cardType) + "\","
                + "\"GroupTime\":\"" + RTUtil.getString(groupTime) + "\","
                + "\"JobStart\":\"" + RTUtil.getString(jobStart, "yyyyMMddHHmmss") + "\","
                + "\"TranNum\":\"" + RTUtil.getString(tranNum) + "\","
                + "\"FareVer\":\"" + RTUtil.getString(fareVer) + "\","
                + "\"SaleRate\":\"" + RTUtil.getString(saleRate) + "\","
                + "\"ImagePath\":\"" + RTUtil.getString(imgPath) + "\","
                + "\"StaffID\":\"" + RTUtil.getString(exitStaffid) + "\","
                + "\"OrderPay\":\"" + RTUtil.getString(orderPay) + "\","
                + "\"CollectionPay\":\"" + RTUtil.getString(collectionPay) + "\","
                + "\"RealFee\":\"" + RTUtil.getString(realFee) + "\","
                + "\"CollectionFee\":\"" + RTUtil.getString(collectionFee) + "\","
                + "\"exitMOP\":\"" + RTUtil.getString(exitMop) + "\""
                + "}}";
    }

    /**
     * 将对应的JSONObject转换为CostRecord实体类
     *
     * @param jo
     * @return
     */
    public static CostRecord parseJSON(JSONObject jo)  {
        if(jo==null){
            return null;
        }else if(jo.get(CostRecord.class.getSimpleName())==null){
            return null;
        }else{
            JSONObject jo1 = jo.getJSONObject(CostRecord.class.getSimpleName());
            CostRecord cr = new CostRecord();
            cr.setOrderSN(jo1.getString("OrderSN"));
            cr.setTotalDis(jo1.getString("TotalDistance"));
            cr.setDownLevel(jo1.getInt("DownLevel"));
            cr.setEntryStationid(jo1.getString("EntryStationID"));
            cr.setTrafficStationid(jo1.getString("TrafficStationID"));
            cr.setEntryLaneid(jo1.getString("EntryChannelNo"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                cr.setEntryTime(sdf.parse(jo1.getString("EntryTime")));
            } catch (ParseException ex) {
                Logger.getLogger(CostRecord.class.getName()).warn("解析临时文件入口时间异常",ex);
            }
            cr.setEntryStaffid(jo1.getString("EntryStaff"));
            cr.setVehColor(jo1.getString("VehicleColor"));
            cr.setVehClass((short)jo1.getInt("VehicleClass"));
            cr.setPlateNum(jo1.getString("PlateNo"));
            cr.setDetectWeight(jo1.getString("DWeight"));
            cr.setFareWeight(jo1.getString("RealWeight"));
            cr.setLimitWeight(jo1.getString("LimitWeight"));
            cr.setExitStationid(jo1.getString("ExitStationID"));
            cr.setExitLaneid(jo1.getString("ExitChannelNo"));
            cr.setExitChannelClass((short)jo1.getInt("ExitChannelClass"));
            try {
                cr.setExitTime(sdf.parse(jo1.getString("ExitTime")));
            } catch (ParseException ex) {
                 Logger.getLogger(CostRecord.class.getName()).warn("解析临时文件出口时间异常",ex);
            }
            cr.setCardNum(jo1.getString("CardNo"));
            cr.setChargeType((short)jo1.getInt("ChargeType"));
            cr.setCardType(jo1.getInt("CardType"));
            cr.setGroupTime(jo1.getString("GroupTime"));
            try {
                cr.setJobStart(sdf.parse(jo1.getString("JobStart")));
            } catch (ParseException ex) {
                 Logger.getLogger(CostRecord.class.getName()).warn("解析临时文件上班时间异常",ex);
            }
            cr.setTranNum(jo1.getInt("TranNum"));
            cr.setFareVer(jo1.getString("FareVer"));
            cr.setSaleRate(jo1.getString("SaleRate"));
            cr.setImgPath(jo1.getString("ImagePath"));
            cr.setExitStaffid(jo1.getString("StaffID"));
            cr.setOrderPay(jo1.getString("OrderPay"));
            cr.setCollectionPay(jo1.getString("CollectionPay"));
            cr.setRealFee(jo1.getString("RealFee"));
            cr.setCollectionFee(jo1.getString("CollectionFee"));
            cr.setExitMop(jo1.getInt("exitMOP"));
            return cr;
        }
    }

    /**
     * 获取该实体类对应需要发送给路径识别服务器的数组
     *
     * @deprecated
     * @return
     */
    public byte[] getByteArray() {
        byte[] buffer1 = ByteUtil.getByteArray(this.getOrderSN());
        byte[] buffer3 = ByteUtil.getByteArray(this.getEntryStationid());
        byte[] buffer4 = ByteUtil.getByteArray(this.getTrafficStationid());
        byte[] buffer5 = ByteUtil.getByteArray(this.getEntryLaneid());
        byte[] buffer6 = ByteUtil.getByteArray(this.getEntryTime());
        byte[] buffer7 = ByteUtil.getByteArray(this.getEntryStaffid());
        byte[] buffer8 = ByteUtil.getByteArray(this.getVehColor());
        byte[] buffer9 = ByteUtil.getByteArray(this.getVehClass());
        byte[] buffer10 = ByteUtil.getByteArray(this.getPlateNum());
        byte[] buffer11 = ByteUtil.getByteArray(this.getDetectWeight());
        byte[] buffer12 = ByteUtil.getByteArray(this.getFareWeight());
        byte[] buffer13 = ByteUtil.getByteArray(this.getLimitWeight());
        byte[] buffer14 = ByteUtil.getByteArray(this.getExitStationid());
        byte[] buffer15 = ByteUtil.getByteArray(this.getExitLaneid());
        byte[] buffer16 = ByteUtil.getByteArray(this.getExitTime());
        byte[] buffer17 = ByteUtil.getByteArray(this.getCardNum());
        byte[] buffer18 = ByteUtil.getByteArray(this.getChargeType());
        byte[] buffer19 = ByteUtil.getByteArray(this.getCardType());
        byte[] buffer22 = ByteUtil.getByteArray(this.getJobStart());
        byte[] buffer23 = ByteUtil.getByteArray(this.getTranNum());
        byte[] buffer24 = ByteUtil.getByteArray(this.getFareVer());
        byte[] buffer25 = ByteUtil.getByteArray(this.getSaleRate());
        byte[] buffer26 = ByteUtil.getByteArray(this.getImgPath());
        byte[] buffer27 = ByteUtil.getByteArray(this.getExitStaffid());
        byte[] buffer28 = ByteUtil.getByteArray(this.getOrderPay());
        byte[] buffer29 = ByteUtil.getByteArray(this.getRealFee());
        byte[] buffer32 = ByteUtil.getByteArray(this.getExitMop());
        List<byte[]> list = new ArrayList();
        list.add(buffer1);
        list.add(buffer3);
        list.add(buffer4);
        list.add(buffer5);
        list.add(buffer6);
        list.add(buffer7);
        list.add(buffer8);
        list.add(buffer9);
        list.add(buffer10);
        list.add(buffer11);
        list.add(buffer12);
        list.add(buffer13);
        list.add(buffer14);
        list.add(buffer15);
        list.add(buffer16);
        list.add(buffer17);
        list.add(buffer18);
        list.add(buffer19);
        list.add(buffer22);
        list.add(buffer23);
        list.add(buffer24);
        list.add(buffer25);
        list.add(buffer26);
        list.add(buffer27);
        list.add(buffer28);
        list.add(buffer29);
        list.add(buffer32);
        return ByteUtil.mergeBytes(list);
    }
}
