
package com.hgits.realTimePath.vo;

import com.hgits.realTimePath.ByteUtil;
import com.hgits.realTimePath.RTUtil;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONObject;

/**
 * 路径识别服务器需要的心跳检测
 * @author Administrator
 */
public class HeartBeat {
    private String stationid;//收费站编号编号,由路段网络号+站号组成（如羊楼司：0501)
    private String laneid;//车辆入收费站的车道编号
    
//    public byte[] getByteArray(){
//        byte[] buffer1 = ByteUtil.getByteArray(this.stationid);
//        byte[] buffer2 = ByteUtil.getByteArray(this.laneid);
//        List<byte[]> list = new ArrayList();
//        list.add(buffer1);
//        list.add(buffer2);
//        return ByteUtil.mergeBytes(list);
//    }

    /**
     * 获取JSON字符串
     * @return 
     */
    public String toJSONString(){
        String str = "{"
                + "\"HeartBeat\":{"
                +"\"StationID\":\"" + RTUtil.getString(stationid) + "\","
                + "\"ChannelNo\":\"" + RTUtil.getString(laneid) + "\""
                + "}"
                + "}";
        JSONObject jo = JSONObject.fromObject(str);
        return jo.toString();
    }
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

    @Override
    public String toString() {
        return "HeartBeat{" + "stationid=" + stationid + ", laneid=" + laneid + '}';
    }
    
}