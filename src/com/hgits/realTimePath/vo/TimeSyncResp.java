package com.hgits.realTimePath.vo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;

/**
 * 时间同步应答
 * @author Wang Guodong
 */
public class TimeSyncResp {

    private Date SystemTime;//GPS时间（格式为：YYYYMMDD24HHMISSsss,例：20140506235859001）
    private int Status;//请求返回结果

    public Date getSystemTime() {
        return SystemTime;
    }

    public void setSystemTime(Date SystemTime) {
        this.SystemTime = SystemTime;
    }


    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }
    /**
     * 解析JSON字符串获取时间同步应答实体类
     * @param jsonStr
     * @return 
     */
    public static TimeSyncResp parseJSONStr(String jsonStr){
        TimeSyncResp tsr = null;
        JSONObject jo = JSONObject.fromObject(jsonStr);
        Iterator it = jo.keys();
        if(it.hasNext()){
            String name = (String)it.next();
            JSONObject jo1 = jo.getJSONObject(name);
            tsr = new TimeSyncResp();
            tsr.setStatus(jo1.getInt("Status"));
            String dhm = jo1.getString("SystemTime");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            try {
                tsr.setSystemTime(sdf.parse(dhm));
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }
        return tsr;
    }

    @Override
    public String toString() {
        return "TimeSyncResp{" + "SystemTime=" + SystemTime + ", Status=" + Status + '}';
    }

}
