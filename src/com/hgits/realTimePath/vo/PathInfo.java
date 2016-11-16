package com.hgits.realTimePath.vo;

import java.util.Iterator;
import net.sf.json.JSONObject;

/**
 *
 * @author Wang Guodong
 */
public class PathInfo {

    int status;//请求返回结果
    String orderSN;//交易流水号
    String fareVer;//费率版本  
    double totalFee;//车辆总费用（路径识别服务器计算）精确到小数点后两位
    double collectionFee;//代收费用,目前是城通费   精确到小数点后两位
    double totalDistance;//车辆行驶总里程，单位公里
    String path;//车辆行驶路径信息
    String spotString;//识别点序列
    String busStatus;//业务异常状态（0，正常，业务异常码，可为多个以逗号分割）
    String info;//业务信息

    public String getOrderSN() {
        return orderSN;
    }

    public void setOrderSN(String orderSN) {
        this.orderSN = orderSN;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getFareVer() {
        return fareVer;
    }

    public void setFareVer(String fareVer) {
        this.fareVer = fareVer;
    }

    public double getCollectionFee() {
        return collectionFee;
    }

    public void setCollectionFee(double collectionFee) {
        this.collectionFee = collectionFee;
    }

    public String getSpotString() {
        return spotString;
    }

    public void setSpotString(String spotString) {
        this.spotString = spotString;
    }

    public String getBusStatus() {
        return busStatus;
    }

    public void setBusStatus(String busStatus) {
        this.busStatus = busStatus;
    }

    /**
     * 将json字符串解析为PathInfo实体类
     *
     * @param jsonStr
     * @return
     */
    public static PathInfo parseJSON(String jsonStr) {
        PathInfo pi = null;
        JSONObject jo = JSONObject.fromObject(jsonStr);
        Iterator it = jo.keys();
        if (it.hasNext()) {
            String name = (String) it.next();
            JSONObject jo1 = jo.getJSONObject(name);
            if(jo1==null){
                return null;
            }
            pi = new PathInfo();
            pi.setStatus(jo1.getInt("Status"));
            if(pi.getStatus()!=0){
                return null;
            }
            pi.setBusStatus(jo1.getString("BusStatus"));
            pi.setCollectionFee(jo1.getDouble("CollectionFee"));
            pi.setFareVer(jo1.getString("FareVer"));
            pi.setInfo(jo1.getString("Info"));
            pi.setOrderSN(jo1.getString("OrderSN"));
            pi.setSpotString(jo1.getString("SpotString"));
            pi.setTotalDistance(jo1.getDouble("TotalDistance"));
            pi.setTotalFee(jo1.getDouble("TotalFee"));
            pi.setPath(jo1.getString("PathInfo"));
        }
        return pi;
    }

    @Override
    public String toString() {
        return "PathInfo{" + "status=" + status + ", orderSN=" + orderSN + ", fareVer=" + fareVer + ", totalFee=" + totalFee + ", collectionFee=" + collectionFee + ", totalDistance=" + totalDistance + ", path=" + path + ", spotString=" + spotString + ", busStatus=" + busStatus + ", info=" + info + '}';
    }
    
    
}
