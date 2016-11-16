package com.hgits.realTimePath.vo;

import com.hgits.realTimePath.RTUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.json.JSONObject;

/**
 * 路径识别服务器所需车辆入站信息
 *
 * @author Wang Guodong
 */
public class EntryStation {

    private String stationid;//收费站编号编号,由路段网络号+站号组成（如羊楼司：0501)
    private String laneid;//车辆入收费站的车道编号
    private short channelClass = 1;//车道类型1表示MTC车道，2表示ETC车道
    private Date entryTime;//车辆入高速时间
    private String staffid;//发卡员的员工编号
    private String cardNo;//发卡的收费卡号
    private short vehClass;//车辆的类型
    private String plateNum;//车牌号码
    private int cardType;//卡类型
    private String vehColor;//车牌颜色 如：黄
    private String imgPath;//入口车辆图片（大图）URL如：http://192.168.1.1/pic/stest.jpg

    public String getStationid() {
        return stationid;
    }

    public void setStationid(String stationid) {
        this.stationid = stationid;
    }

    public String getLaneid() {
        return laneid;
    }

    public void setLaneid(String laneid) {
        this.laneid = laneid;
    }

    public Date getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    public String getStaffid() {
        return staffid;
    }

    public void setStaffid(String staffid) {
        this.staffid = staffid;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
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

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getVehColor() {
        return vehColor;
    }

    public void setVehColor(String vehColor) {
        this.vehColor = vehColor;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public short getChannelClass() {
        return channelClass;
    }

    public void setChannelClass(short channelClass) {
        this.channelClass = channelClass;
    }

    /**
     * 获取实体类对应的JSON字符串(按照路径识别要求)
     *
     * @return JSON字符串
     */
    public String toJSONString() {
        String str = "{\"EntryStation\":{"
                + "\"StationID\":\"" + RTUtil.getString(stationid) + "\","
                + "\"ChannelNo\":\"" + RTUtil.getString(laneid) + "\","
                + "\"ChannelClass\":\"" + RTUtil.getString(channelClass) + "\","
                + "\"EntryTime\":\"" + RTUtil.getString(entryTime, "yyyyMMddHHmmss") + "\","
                + "\"StaffID\":\"" + RTUtil.getString(staffid) + "\","
                + "\"CardNo\":\"" + RTUtil.getString(cardNo) + "\","
                + "\"VehicleClass\":\"" + RTUtil.getString(vehClass) + "\","
                + "\"PlateNum\":\"" + RTUtil.getString(plateNum) + "\","
                + "\"VehicleColor\":\"" + RTUtil.getString(vehColor) + "\","
                + "\"ImagePath\":\"" + RTUtil.getString(imgPath) + "\""
                + "}}";
        JSONObject jo = JSONObject.fromObject(str);
        return jo.toString();
    }

    /**
     * 将JSONObject解析为EntryStation
     *
     * @param jo
     * @return
     * @throws ParseException
     */
    public static EntryStation parseJSON(JSONObject jo) throws ParseException {
        if (jo == null) {
            return null;
        } else if (jo.get(EntryStation.class.getSimpleName()) == null) {
            return null;
        } else {
            JSONObject jo1 = (JSONObject) jo.get(EntryStation.class.getSimpleName());
            EntryStation es = new EntryStation();
            es.setStationid(jo1.getString("StationID"));
            es.setLaneid(jo1.getString("ChannelNo"));
            es.setChannelClass((short) jo1.getInt("ChannelClass"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            es.setEntryTime(sdf.parse(jo1.getString("EntryTime")));
            es.setStaffid(jo1.getString("StaffID"));
            es.setCardNo(jo1.getString("CardNo"));
            es.setVehClass((short) jo1.getInt("VehicleClass"));
            es.setPlateNum(jo1.getString("PlateNum"));
            es.setVehColor(jo1.getString("VehicleColor"));
            es.setImgPath(jo1.getString("ImagePath"));
            return es;
        }

    }

    @Override
    public String toString() {
        return "EntryStation{" + "stationid=" + stationid + ", laneid=" + laneid + ", channelClass=" + channelClass + ", entryTime=" + entryTime + ", staffid=" + staffid + ", cardNo=" + cardNo + ", vehClass=" + vehClass + ", plateNum=" + plateNum + ", cardType=" + cardType + ", vehColor=" + vehColor + ", imgPath=" + imgPath + '}';
    }
    
    
}
