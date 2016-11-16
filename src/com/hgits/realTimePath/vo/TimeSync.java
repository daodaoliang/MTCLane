package com.hgits.realTimePath.vo;

import com.hgits.realTimePath.RTUtil;

/**
 * 路径识别时间同步类
 * @author Wang Guodong
 */
public class TimeSync {
    private String StationID;//收费站编号 编号由路段网络号+站号组成（如羊楼司：0501，德山：4579）

    private String ChannelNo;//车辆入收费站的车道编号

    public String getStationID() {
        return StationID;
    }

    public void setStationID(String StationID) {
        this.StationID = StationID;
    }

    public String getChannelNo() {
        return ChannelNo;
    }

    public void setChannelNo(String ChannelNo) {
        this.ChannelNo = ChannelNo;
    }

    @Override
    public String toString() {
        return "TimeSync{" + "StationID=" + StationID + ", ChannelNo=" + ChannelNo + '}';
    }
    
    public String toJSONString(){
        return "{\"TimeSync\":{" 
                + "\"StationID\":\"" + RTUtil.getString(StationID) + "\", "
                + "\"ChannelNo\":\"" + RTUtil.getString(ChannelNo) + "\""
                +"}}";
    }
    
}
