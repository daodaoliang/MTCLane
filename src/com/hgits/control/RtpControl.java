package com.hgits.control;

import static com.hgits.control.FlowControl.trafficEnRoadid;
import static com.hgits.control.FlowControl.trafficEnStationid;
import com.hgits.realTimePath.RTUtil;
import com.hgits.realTimePath.RealTimePath;
import com.hgits.realTimePath.vo.EntryStationInfo;
import com.hgits.realTimePath.vo.ExitStationInfo;
import com.hgits.realTimePath.vo.PathInfo;
import com.hgits.realTimePath.vo.QueryPath;
import com.hgits.realTimePath.vo.QueryPathResp;
import com.hgits.realTimePath.vo.RequestPath;
import com.hgits.realTimePath.vo.SpotInfo;
import com.hgits.util.DoubleUtils;
import com.hgits.util.NullUtils;
import com.hgits.vo.Card;
import com.hgits.vo.Lane;
import com.hgits.vo.Vehicle;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路径识别控制类
 *
 * @author Wang Guodong
 */
public class RtpControl {

    private Lane lane;//车道
    private PathInfo pi;//实际路径信息
    private final RealTimePath rtp = RealTimePath.getSingleInstance();//与路径识别服务器通信类
    private Map<String, String> imgMap = new HashMap();//图片集合
    private static RtpControl rtpControl;//唯一实例化对象

    /**
     * 获取唯一实例化对象
     *
     * @return 唯一实例化对象
     */
    public static synchronized RtpControl getSingleInstance() {
        if (rtpControl == null) {
            rtpControl = new RtpControl();
        }
        return rtpControl;
    }

    private RtpControl() {
    }

    /**
     * 请求实时路径信息（仅发送请求信息，不等待结果）
     *
     * @param entryCard 入口所发通行卡
     * @param exitLane 出口车道
     * @param veh 车辆信息
     * @param exitTime 出口交易时间
     */
    public void requestRealTimePath(final Card entryCard, final Lane exitLane, final Vehicle veh, final Date exitTime) {
        try {
            if (lane.isExit() && pi == null) {
                RequestPath pr = new RequestPath();
                pr.setCardNum(RTUtil.getString(entryCard.getCardSerial()));
                pr.setEnLane(RTUtil.getString(entryCard.getLaneId()));
                pr.setEnStation(RTUtil.getString(trafficEnRoadid) + RTUtil.getString(trafficEnStationid));
                pr.setEntryTime(entryCard.getDhm());
                pr.setExitLane(RTUtil.getString(exitLane.getLaneId()));
                pr.setExitStation(RTUtil.getString(exitLane.getRoadId() + exitLane.getStationId()));
                pr.setExitTime(exitTime);
                pr.setExitChannelClass((short) 1);
                pr.setExitVehColor(RTUtil.getString(veh.getKeyedPlateColor()));
                pr.setFareWeight(RTUtil.getString(DoubleUtils.mul(veh.getFareWeight(), 100)));//单位10千克
                pr.setLimitWeight(RTUtil.getString(DoubleUtils.mul(veh.getLimitWeight(), 100)));//单位10千克
                pr.setPlateNum(RTUtil.getString(veh.getFullVehPlateNum()));
                pr.setVehClass((short) veh.getVehicleClass());
                rtp.requestForPath(pr);
            }
        } catch (Exception ex) {
            LogControl.logInfo("请求实际路径异常", ex);
        }
    }

    /**
     * 请求路径明细
     *
     * @param enRoadid 入口路段号
     * @param enStationid 入口收费站号
     * @param enLane 入口车道
     * @param enDHM 入口交易时间
     * @param exVehCol 出口车牌颜色
     * @param exVehClass 出口车型
     * @param exPlateNum 出口车牌号码
     * @param exRoadid 出口路段号
     * @param exStationid 出口收费站号
     * @param exLane 出口车道
     * @param exDHM 出口交易时间
     * @param exCardNo 出口卡号
     * @param realWeight 车辆计费重量
     * @param limitWeight 限重
     * @return -1 异常<br>请求路径明细对应的序列号
     */
    public int requestPathDetail(String enRoadid, String enStationid, String enLane, Date enDHM, String exVehCol,
            int exVehClass, String exPlateNum, String exRoadid, String exStationid, String exLane, Date exDHM,
            String exCardNo, String realWeight, String limitWeight) {
        try {
            QueryPath qp = new QueryPath();
            qp.setCardNo(NullUtils.parseNull(exCardNo));//通行卡卡号
            qp.setEntryChannelNo(NullUtils.parseNull(enLane));//入口车道
            qp.setEntryStationID(NullUtils.parseNull(enRoadid) + NullUtils.parseNull(enStationid));//入口路段+入口收费站
            qp.setExitTime(NullUtils.parseNull(exDHM));//出口交易时间
            qp.setEntryTime(NullUtils.parseNull(enDHM));//入口交易时间
            qp.setExitChannelNo(NullUtils.parseNull(exLane));//出口车道
            qp.setExitChannelClass((short) 1);//出口车道类型默认为1MTC车道
            qp.setExitStationID(NullUtils.parseNull(exRoadid) + NullUtils.parseNull(exStationid));//出口路段+出口收费站
            qp.setExVehicleColor(exVehCol);
            qp.setExPlateNo(NullUtils.parseNull(exPlateNum));//出口车牌号码
            qp.setRealWeight(NullUtils.parseNull(realWeight));//车辆计费重量
            qp.setStandWeight(NullUtils.parseNull(limitWeight));//限重
            qp.setExVehicleClass((short) exVehClass);//出口车型
            return rtp.queryPath(qp);//请求路径明细
        } catch (Exception ex) {
            LogControl.logInfo("请求路径明细异常", ex);
            return -1;
        }
    }

    /**
     * 根据命令序号获取请求路径明细
     *
     * @param serial 命令序号
     * @return 入口+识别点+出口集合
     */
    public Map<String, String> getPathDetail(int serial) {
        Map<String, String> map = new HashMap();
        QueryPathResp qpr = rtp.getQueryPath(serial);//获取路径明细
        if (qpr == null) {
            return map;
        }
        EntryStationInfo enInfo = qpr.getEntryStationInfo();//获取入口信息
        List<SpotInfo> spotList = qpr.getSpotList();//获取识别点集合
        ExitStationInfo exInfo = qpr.getExitStationInfo();//获取出口信息
        int i = 0;
        map.put((i++) + "-" + enInfo.getStationId(), enInfo.getImagePath());
        for (SpotInfo info : spotList) {
            map.put((i++) + "-" + info.getSpotId(), info.getImagePath());
        }
        map.put((i++) + "-" + exInfo.getStationId(), "");
        return map;
    }

    public void showRtpInfo() {

    }
}
