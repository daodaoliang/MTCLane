package com.hgits.service;

import com.hgits.control.TempControl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import ui.ExtJFrame;

import com.hgits.control.AlarmControl;
import com.hgits.control.AudioControl;
import com.hgits.control.AutoMachineControl;
import com.hgits.control.FlowControl;
import com.hgits.control.FunctionControl;
import com.hgits.control.ImgLaneServerControl;
import com.hgits.control.LaneServerControl;
import com.hgits.control.ListControl;
import com.hgits.control.TestControl;
import com.hgits.control.LogControl;
import com.hgits.control.ThreadPoolControl;
import com.hgits.exception.MTCException;
import com.hgits.hardware.CICM;
import com.hgits.hardware.CXP;
import com.hgits.hardware.Keyboard;
import com.hgits.hardware.Printer;
import com.hgits.hardware.TFI;
import com.hgits.tool.socket.entity.Confirm;
import com.hgits.util.DateUtils;
import com.hgits.util.DoubleUtils;
import com.hgits.util.IntegerUtils;
import com.hgits.util.LaneListUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.PriceCalculateUtils;
import com.hgits.util.StringUtils;
import com.hgits.util.rate.ParamCacheQuery;
import com.hgits.vo.Card;
import com.hgits.vo.Constant;
import com.hgits.vo.CpuCard;
import com.hgits.vo.Lane;
import com.hgits.vo.LaneEnList;
import com.hgits.vo.LaneExList;
import com.hgits.vo.Operator;
import com.hgits.vo.SimpleLaneExList;
import com.hgits.vo.SpecialIssue;
import com.hgits.vo.Staff;
import com.hgits.vo.Station;
import com.hgits.vo.TravelTime;
import com.hgits.vo.VehPlateBWG;
import com.hgits.vo.VehPlateBWGConfirm;
import com.hgits.vo.Vehicle;

/**
 * control主服务层
 *
 * @author Wang Guodong
 */
public class MainService {

    private Keyboard keyboard;//收费键盘
    private ExtJFrame extJFrame;
    private Lane lane;
    private ImgLaneServerControl imgLaneServerControl;
    private List<String> imgList = new ArrayList();
    private List<String> blackPlateImgList = new ArrayList();
    private int imgFlag = 0;
    private int blackPlateImgFlag = 0;
    private FlowControl fc;
    private LaneServerControl lc;
    private boolean entryPhotoFlag;//请求入口图片标识
    private boolean checkFlag;//出口启用全车牌校验标识

    public void setLc(LaneServerControl lc) {
        this.lc = lc;
    }

    public MainService(Keyboard keyboard, ExtJFrame extJFrame) {
        this.keyboard = keyboard;
        this.extJFrame = extJFrame;
    }

    public MainService() {
    }

    public void setKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public void setImgLaneClientControl(ImgLaneServerControl imgLaneClientControl) {
        this.imgLaneServerControl = imgLaneClientControl;
    }

    public void setExtJFrame(ExtJFrame extJFrame) {
        this.extJFrame = extJFrame;
    }

    public void setLane(Lane lane) {
        this.lane = lane;
    }

    public void setFc(FlowControl fc) {
        this.fc = fc;
    }

    /**
     * 异常时等待监控室确认入口信息
     *
     * @param card 含有入口信息的通行卡
     * @param vehClass 出口收费员确认车型
     * @return 确认入口信息之后的通行卡
     */
    public Card waitForControlRoomConfirmEntry(Card card, String vehClass) {
        String obCode = "0";
        try {
            if (fc != null) {//在等待监控室确认之前显示收费额及入口站信息
                fc.runGetFare();
            }
            LogControl.logInfo("等待监控室管理员确认入口站" + card);
            obCode = card.getObservationCode();
            if (obCode == null) {
                obCode = "0";
            }
            switch (obCode) {
                case "0"://0正常     
                    break;
                case "1"://1 通行卡状态异常
                case "2"://2 超时
                case "3"://3 车型差异
                case "4"://4 车牌差异
                case "5"://5 车型车牌不符
                case "6"://6 U型车两小时之内
                case "7"://7 U型车两小时之外 
                    extJFrame.showEntryInfoPanel();//显示入口信息
                case "8"://8 无卡
                case "9"://9 不可读卡
                case "10"://10更改车型
                    break;
                case "11"://11 更改车种
                    return card;//更改车种不需要监控室确认
                case "12"://12残卡
                case "13"://13小于最小行程时间
                default:
                    break;

            }
            Confirm cf = new Confirm();
            cf.setCard(card.getCardSerial());
            cf.setEnType(card.getVehClass());
            cf.setInputType(vehClass);
            cf.setLane(lane.getRoadId() + "_" + lane.getStationId() + "_" + lane.getLaneId());//当前车道
            cf.setEnLane(card.getLaneId());//入口车道
            cf.setPlate(card.getFullVehPlateNum());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            cf.setTime(sdf.format(new Date()));
            cf.setConfirmEnStation(card.getRoadid() + card.getStationid());
            cf.setWhyAlam(card.getObservationCode());
            cf.setTimeout(String.valueOf(LaneServerControl.getOverTimeInterval())); //modify by wanghao，增加超时时间的发送
            SimpleDateFormat ensdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cf.setEnTime(ensdf.format(card.getDhm() == null ? new Date() : card.getDhm()));
            AlarmControl.generateAlarmInfo(Lane.getInstance(), FlowControl.staff.getStaffId(), (short) FlowControl.staff.getShiftId(), "29", (short) 1);//生成入口站确认报警信息
            extJFrame.flashFareAndEntry();//等待监控室确认期间入口信息以及收费额闪烁表示等待确认
            String str = lc.waitForEntryConfirm(cf.toString());
            if (str != null) {
                AlarmControl.generateAlarmInfo(Lane.getInstance(), FlowControl.staff.getStaffId(), (short) FlowControl.staff.getShiftId(), "29", (short) 0);//入口站确认报警信息消失
                LogControl.logInfo("监控室确认入口信息:" + str);
                String roadid = str.substring(0, 2);
                String stationid = str.substring(2, 4);
                String sta = getVirtualStation(obCode, card.getRoadid() + card.getStationid());
                if (sta != null && !sta.equals(roadid + stationid)) {//判断监控室是否修改了入口站
                    FlowControl.chargeObserCode = "Q";
                }
                card.setRoadid(roadid);
                card.setStationid(stationid);
                checkStation(card);//检测监控室确认入口站信息是否存在
            } else {
                LogControl.logInfo("无法获取监控室确认");
                switch (card.getObservationCode()) {
                    case "1"://卡状态异常
                        card.setRoadid("99");
                        card.setStationid("97");
                        break;
                    case "5"://车型车牌不符
                        if (FunctionControl.isDiffJumpActive()) {//车型差异车牌差异跳站激活
                            card.setRoadid("99");
                            card.setStationid("94");
                        }
                        break;
                    case "6"://U型车两小时之内
                        card.setRoadid("99");
                        card.setStationid("98");
                        break;
                }
            }
            fc.runGetFare();//更新入口收费额及入口信息
        } catch (Exception ex) {
            LogControl.logInfo("获取监控室确认信息时出现异常", ex);
        } finally {
            keyboard.getMessage();//清空按键记录信息
            extJFrame.confirmFareAndEntry();//监控室确认后入口信息以及收费额停止闪烁表示已经确认
            if ("2".equals(obCode) || "3".equals(obCode) || "4".equals(obCode) || "5".equals(obCode) || "9".equals(obCode)) {
                //超时，车型差异，车牌差异，车型车牌差异，不可都卡监控室确认
                extJFrame.updateSpecialStatus(SpecialIssue.CONFIRMED);
            }
            if ("2".equals(card.getObservationCode())) {
                if ("99".equals(card.getRoadid())) {//非正常超时
                    FlowControl.logout.setUnNormalC(FlowControl.logout.getUnNormalC() + 1);
                    FlowControl.logout.setNotAdjustTimeC(FlowControl.logout.getNotAdjustTimeC() + 1);
                    FlowControl.cardObserCode = "U";
                } else {//合理超时
                    FlowControl.logout.setAdjustTimeC(FlowControl.logout.getAdjustTimeC() + 1);
                    FlowControl.cardObserCode = "N";
                }
            }
        }
        return card;
    }

    /**
     * 等待监控室确认车辆黑白灰名单
     *
     * @param veh 需要确认的车辆信息
     * @return 2 不允许通过 3 允许通过
     */
    public int waitForControlRoomConfirmBGW(Vehicle veh) {
        try {
            LogControl.logInfo("等待监控室管理员确认车牌黑白灰名单" + veh);
            VehPlateBWGConfirm vehPlateBWGConfirm = new VehPlateBWGConfirm();
            vehPlateBWGConfirm.setPlate(veh.getFullVehPlateNum());
            vehPlateBWGConfirm.setTime(DateUtils.formatDateToString(DateUtils.getCurrentDate()));
            vehPlateBWGConfirm.setLane(lane.getRoadId() + "_" + lane.getStationId() + "_" + lane.getLaneId()); //当前车道
            vehPlateBWGConfirm.setTollUser(FlowControl.staff.getStaffId());
            vehPlateBWGConfirm.setCarStatues(getZHSpeVehFlag(veh.getSpeVehFlag()));
            String str = lc.waitForBCKListConfirm(vehPlateBWGConfirm
                    .toString());
            if (str != null) {
                LogControl.logInfo("监控室确认车牌黑白灰信息:" + str);
                if (str.equalsIgnoreCase("N")) {
                    return 2; //不允许通过
                } else {
                    return 3; //允许通过
                }
            } else {
//                try {
//                    Thread.sleep(1000);// 无法获取监控室确认信息时停顿一秒，使收费员确认原因
//                } catch (InterruptedException ex) {
//                }
                LogControl.logInfo("无法获取监控室确认");
            }
        } catch (Exception ex) {
            LogControl.logInfo("获取监控室确认信息时出现异常", ex);
        } finally {
            keyboard.getMessage();// 清空按键记录信息
        }
        return 3;
    }

    /**
     * 等待监控室确认回复免费车辆的图片信息
     *
     * @param veh
     * @return
     */
    public List waitForFreeVehImg(Vehicle veh) {
        List<String> list = new ArrayList();
        return list;
    }

    /**
     * 根据通行卡信息从服务器获取入口照片信息，入口照片信息写入车道机硬盘
     *
     * @param card 入口通行券（用于获取入口图片）
     * @return 返回从服务器获取的入口照片文件地址
     */
    public List<String> getEntryVehPhoto(Card card) {
        List<String> tempImgList = new ArrayList();
        try {
            String roadid = FlowControl.trafficEnRoadid;
            String stationid = FlowControl.trafficEnStationid;
            String laneid = card.getLaneId();
            Date ts = card.getDhm();
            int enTransNum = card.getTransNum();
            //向服务器图片控制程序发送请求入口图片指令
            List<byte[]> list = imgLaneServerControl.requestEntryImg(ts, roadid, stationid, laneid, enTransNum);
            File dir = new File("temp");
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }
            //未获取到入口图片
            if (list == null || list.isEmpty()) {
                LogControl.logInfo("未获取到入口图片");
                return tempImgList;
            }
            //将获取到的入口图片写入本地
            for (int i = 0; i < list.size(); i++) {
                File file = new File(dir, "temp" + i + ".jpg");
                if (!file.exists() || !file.isFile()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(list.get(i));
                fos.flush();
                fos.close();
                tempImgList.add(file.getAbsolutePath());
            }
        } catch (Exception ex) {
            LogControl.logInfo("获取入口图片异常", ex);
        }
        return tempImgList;
    }

    /**
     * 停止请求入口图片
     */
    public void stopRequestEntryPhoto() {
        if (imgLaneServerControl != null) {
            imgLaneServerControl.stopRequestEntryImg();
        }
    }

    /**
     * 根据车牌信息从服务器获取车牌黑名单照片信息，车牌黑名单照片信息写入车道机硬盘
     *
     * @author wh 2015-07-13
     * @param plateNum 车牌信息
     * @return 返回从服务器获取的车牌黑名单照片文件地址
     */
    public List<String> getPlateBlackPhoto(String plateNum) {
        List<String> tempImgList = new ArrayList<String>();
        try {
//        	if(true){
//        		//测试代码
//        		tempImgList.add("D:/Workspaces/NetBeansWork/MTCLane-NoSVN/temp/1.jpg");
//        		tempImgList.add("D:/Workspaces/NetBeansWork/MTCLane-NoSVN/temp/2.jpg");
//        		return tempImgList;
//        	}
            //向服务器图片控制程序发送请求入口图片指令
            List<byte[]> list = imgLaneServerControl.requestPlateBlackImg(plateNum);
            File dir = new File("temp");
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }
            //未获取到入口图片
            if (list == null || list.isEmpty()) {
                LogControl.logInfo("未获取到车牌黑名单图片");
                return tempImgList;
            }
            //将获取到的车牌黑名单图片写入本地
            for (int i = 0; i < list.size(); i++) {
                File file = new File(dir, "tempBlackPlate" + i + ".jpg");
                if (!file.exists() || !file.isFile()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(list.get(i));
                fos.flush();
                fos.close();
                tempImgList.add(file.getAbsolutePath());
            }
        } catch (Exception ex) {
            LogControl.logInfo("获取车牌黑名单图片异常", ex);
        }
        return tempImgList;
    }

    /**
     * 根据用户id查询用户密码 王国栋 2014-8-24
     *
     * @param staffId 员工IP
     * @return 员工密码
     */
    public String getPassword(String staffId) {
        String pwd = "";
        Operator op = ParamCacheQuery.queryOperator(staffId);
        if (op != null) {
            pwd = op.getPassWord();
        }
        return pwd;
    }

    /**
     * 根据路段号级收费站号获取收费站名称 王国栋 2014-8-28
     *
     * @param roadid 路段号
     * @param stationid 收费站号
     * @return 收费站中文名
     */
    public String getStationName(String roadid, String stationid) {
        try {
            if (roadid == null || stationid == null) {
                return "";
            }
            int intRoadid = IntegerUtils.parseString(roadid);
            int intStationid = IntegerUtils.parseString(stationid);
            if (intRoadid == 99) {
                switch (intStationid) {
                    case 94:
                        return "最远站";
                    case 96:
                        return "最近站";
                    case 97:
                        return "最远站";
                    case 98:
                        return "最近站两倍";

                }
            }
            Station sta = ParamCacheQuery.queryStation(intRoadid, intStationid);
            if (sta == null) {
                LogControl.logInfo("无法获取收费站中文名:" + roadid + stationid);
                return "";
            }
            return sta.getStationName();
        } catch (Exception ex) {
            LogControl.logInfo("获取收费站" + roadid + stationid + "中文名出现异常", ex);
            return "";
        }
    }

    /**
     * 根据出入口以及车辆信息获取所需通行费 王国栋 2014-8-25
     *
     * @param enRoadid 入口路段
     * @param enStationid 入口收费站
     * @param exRoadid 出口路段
     * @param exStationid 出口收费站
     * @param veh 车辆信息
     * @return 通行费
     */
    public double getFare(String enRoadid, String enStationid, String exRoadid, String exStationid, Vehicle veh) {
        int vehClass = veh.getVehicleClass();
        double fareWeight = veh.getFareWeight();//计费重量
        double limitWeight = veh.getLimitWeight();//限重
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("enRoadId", enRoadid);                                     // 入口路段编码
        paramMap.put("enStationId", enStationid);                               // 入口站编码
        paramMap.put("exRoadId", exRoadid);                                     // 出口路段编码
        paramMap.put("exStationId", exStationid);                               // 出口站编码
        if (vehClass == 8 || vehClass == 9) {
            vehClass = 7;                                                       //绿通车获取应收费用
        }
        paramMap.put("vehType", vehClass);                                      // 车型
        paramMap.put("weight", fareWeight);                                     // 计费重量
        paramMap.put("weightLimit", limitWeight);                               // 限重重量
        String price = "0";
        try {
            price = PriceCalculateUtils.calculate(paramMap);                    //获取通行费用，以分为单位
            double p = Double.parseDouble(price);
            if (p < 0) {
                LogControl.logInfo("按最小收费额计算费用:入口：" + enRoadid + enStationid + ",出口:" + exRoadid + exStationid + ",车型:" + vehClass + ",计费重量:" + fareWeight + ",限重:" + limitWeight + ",通行费:" + p);
                return p;
            }
        } catch (MTCException ex) {
            LogControl.logInfo("计算通行费异常", ex);
        }
        String temp = StringUtils.div(price, "100", 0);
        int fare = IntegerUtils.parseString(temp);
//        if(fare<=0){
//            fare=5;
//        }
        LogControl.logInfo("按最小收费额计算费用:入口：" + enRoadid + enStationid + ",出口:" + exRoadid + exStationid + ",车型:" + vehClass + ",计费重量:" + fareWeight + ",限重:" + limitWeight + ",通行费:" + fare);
        return fare;
    }

    /**
     * 根据出入口信息获得里程
     *
     * @param enRoadid 入口路段
     * @param enStationid 入口收费站
     * @param exRoadid 出口路段
     * @param exStationid 出口收费站
     * @param vehClass 车型（不同车型对应的最小收费路径可能不同）
     * @return 最小收费里程
     */
    public double getDistance(String enRoadid, String enStationid, String exRoadid, String exStationid, int vehClass) {
        int roadid = IntegerUtils.parseString(enRoadid);
        int stationid = IntegerUtils.parseString(enStationid);
        int roadid0 = IntegerUtils.parseString(exRoadid);
        int stationid0 = IntegerUtils.parseString(exStationid);
        TravelTime tt = ParamCacheQuery.queryTravelTime(roadid, stationid, roadid0, stationid0);
        if (tt == null) {
            LogControl.logInfo("无法获取最小行驶里程：" + enRoadid + enStationid + " " + exRoadid + exStationid);
            return 0.0;
        }
        return DoubleUtils.round(tt.getMileages(), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 根据里程，入口时间，出口时间计算车速 王国栋 2014-8-29
     *
     * @param dist 里程
     * @param entryDhm 入口时间
     * @param exitDhm 出口时间
     * @return 车速
     */
    public double getSpeed(double dist, Date entryDhm, Date exitDhm) {
        if (entryDhm == null || exitDhm == null || !entryDhm.before(exitDhm)) {
            return 0;
        }
        long dif = exitDhm.getTime() - entryDhm.getTime();
        double hour = DoubleUtils.div((double) dif, (double) 1000 * 3600, 2);
        return hour == 0 ? 0 : DoubleUtils.div(dist, hour, 2);
    }

    /**
     * 根据入口路段，入口收费站，出口路段，出口收费站信息获取最大行程时间 王国栋 2014-8-29
     * 超时车辆计算原则为里程除以60公里/小时，再乘以3，若计算结果小于两小时，按两小时计算
     *
     * @param roadid 入口路段
     * @param stationid 入口收费站
     * @param roadid0 出口路段
     * @param stationid0 出口收费站
     * @return 最大行程时间（毫秒）
     */
    public long getMaxTripTime(String roadid, String stationid, String roadid0, String stationid0) {
        if (FlowControl.pi != null) {//若有实际路径收费信息，按实际路径计算最大行程时间
            double distance = FlowControl.pi.getTotalDistance();
            double temp = DoubleUtils.div(distance, 20, 2);//获取最大行驶时间（小时）
            if (temp < 2) {
                temp = 2;
            }
            temp = DoubleUtils.mul(temp, 60, 60, 1000);//小时转换为毫秒
            long max = (long) DoubleUtils.round(temp, 0, BigDecimal.ROUND_HALF_UP);
            return max;
        } else {//若无实际路径信息，按最小收费额对应的里程计算最大行程时间
            int intRoadid = IntegerUtils.parseString(roadid);
            int intStationid = IntegerUtils.parseString(stationid);
            int intRoadid0 = IntegerUtils.parseString(roadid0);
            int intStationid0 = IntegerUtils.parseString(stationid0);
            TravelTime tt = ParamCacheQuery.queryTravelTime(intRoadid, intStationid, intRoadid0, intStationid0);
            if (tt == null) {
                LogControl.logInfo("无法获取最大行程时间 " + roadid + stationid + " " + roadid0 + stationid0);
                return 0;
            }
            return tt.getMaxTravelTime() * 60 * 1000;
        }
    }

    /**
     * 根据入口路段，入口收费站，出口路段，出口收费站信息获取最小行程时间 王国栋 2014-8-29
     *
     * @param roadid 入口路段
     * @param stationid 入口收费站
     * @param roadid0 出口路段
     * @param stationid0 出口收费站
     * @return 最小行程时间(毫秒)
     */
    public long getMinTripTime(String roadid, String stationid, String roadid0, String stationid0) {
        int intRoadid = IntegerUtils.parseString(roadid);
        int intStationid = IntegerUtils.parseString(stationid);
        int intRoadid0 = IntegerUtils.parseString(roadid0);
        int intStationid0 = IntegerUtils.parseString(stationid0);
        TravelTime tt = ParamCacheQuery.queryTravelTime(intRoadid, intStationid, intRoadid0, intStationid0);
        if (tt == null) {
            LogControl.logInfo("无法获取最小行程时间 " + roadid + stationid + " " + roadid0 + stationid0);
            return 0;
        }
        return tt.getMinTravelTime() * 60 * 1000;
    }

    /**
     * 根据车牌颜色，车牌号码从黑白灰名单中获取车辆信息 黑名单为0 灰名单为1 白名单为9 普通车2
     *
     * @param veh 车辆信息
     * @return 加载了车辆黑名单标识符的车辆信息
     */
    private Vehicle getSpeVehPlag(Vehicle veh) {
        String plate = veh.getFullVehPlateNum();
        VehPlateBWG vpb = ParamCacheQuery.queryBWG(plate);//根据车牌查询黑白灰名单信息
        if (vpb == null) {
            veh.setSpeVehFlag(2);
        } else {
            if (vpb.getVehPlateColor().equalsIgnoreCase(veh.getKeyedPlateColor())) {//确认车牌颜色
                veh.setSpeVehFlag(vpb.getStatus());
                veh.setDescribe(vpb.getDescribe()); //黑白灰名单描述信息
            } else {
                veh.setSpeVehFlag(2);
            }
        }
        return veh;
    }

    /**
     * 获取所提供的时间的时间差
     *
     * @param startDHM 入口时间
     * @param endDHM 出口时间
     * @return 时间差如1小时10分钟
     */
    public String getDuration(Date startDHM, Date endDHM) {
        if (startDHM == null || endDHM == null || !startDHM.before(endDHM)) {
            return "";
        }
        long dif = endDHM.getTime() - startDHM.getTime();
        long second = dif / 1000;
        long hour = second / 3600;
        long min = second / 60 - hour * 60;
        if (hour == 0 && min == 0 && second != 0) {//时间差在一分钟之内时按照一分钟计算
            min = 1;
        }
        return hour + "小时" + min + "分";
    }

    /**
     * 出口，检测入口信息并与出口信息核对，核对卡状态，超时，车型差异，车牌差异，U型车并同监控室确认;
     *
     * @author 王国栋 2014-8-29
     * @param card1 读到的入口卡信息
     * @param card2 出口卡信息
     * @return 返回监控室确认的入口信息
     */
    public Card check(Card card1, Card card2) {
        try {
            if (lane.isExit()) {                                      //出口需要检验通行卡状态，车辆超时，车型差异，车牌差异，U型车
                String code = card1.getObservationCode();
                code = code == null ? "0" : code;

                if ("8".equals(code)) {//无卡不需要监控室确认
                    extJFrame.updateSpecialInfo(SpecialIssue.NOCARD, null);

                    return card1;
                } else if ("12".equals(code)) {//残卡不需要监控室确认
                    extJFrame.updateSpecialInfo(SpecialIssue.DAMAGECARD, null);
                    return card1;
                }
                if ("9".equals(code)) {//不可读卡等待监控室确认
                    extJFrame.updateSpecialInfo(SpecialIssue.UNREADCARD, SpecialIssue.UNCONFIRMED);
                    extJFrame.updateInfo("", "等待来自监控室管理员的确认");
                    return waitForControlRoomConfirmEntry(card1, card2.getVehClass());
                }
                String class1 = card1.getVehClass();
                class1 = class1 == null ? "0" : class1;
                String class2 = card2.getVehClass();
                class2 = class2 == null ? "0" : class2;
                int c1 = IntegerUtils.parseString(class1);
                int c2 = IntegerUtils.parseString(class2);

                /*因新增需求，注释现有逻辑代码块，启用注释代码块后新逻辑。*/
//                String plateNum1 = card1.getFullVehPlateNum();//入口全车牌
//                plateNum1 = plateNum1 == null ? "" : plateNum1;
//                String plateNum2 = card2.getFullVehPlateNum();//出口全车牌
//                plateNum2 = plateNum2 == null ? "" : plateNum2;
//                if (plateNum1.trim().length() == 0) {//入口全车牌为空，判断车牌后三位是否一致
//                    LogControl.logInfo("入口全车牌为空");
//                    plateNum1 = card1.getVehPlateNum() == null ? "" : card1.getVehPlateNum().trim();//入口车牌后三位
//                    plateNum2 = card2.getVehPlateNum() == null ? "" : card2.getVehPlateNum().trim();//出口车牌后三位
//                }
                /**
                 * 获取配置文件，是否需要进行全车牌校验 0-不启用全车牌校验 1-启用全车牌校验。 默认不启用
                 * 如果配置启用全车牌则只进行全车牌校验，不考虑车牌后三位，如果配置不启用全车牌则按现有规则执行 modify by yili
                 * 2016/05/31
                 *
                 */
                /*------------------------------modify start----------------------------------------------------------------------------*/
                String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "fullVehPlateCheckFlag", "0");
                switch (str) {
                    case "0":
                        LogControl.logInfo("不启用全车牌校验");
                        checkFlag = false;
                        break;
                    case "1":
                        LogControl.logInfo("启用全车牌校验");
                        checkFlag = true;
                        break;
                    default:
                        checkFlag = false;
                        break;
                }

                String plateNum1 = null;//获取M1卡全车牌域的值
                String plateNum2 = null;//获取M1卡车牌后三位数字域的值
                if (checkFlag == true) {
                    plateNum1 = isNull(card1.getFullVehPlateNum());// 入口全车牌
                    plateNum2 = isNull(card2.getFullVehPlateNum());// 出口全车牌
                } else {
                    plateNum1 = isNull(card1.getFullVehPlateNum());// 入口全车牌
                    plateNum2 = isNull(card2.getFullVehPlateNum());// 出口全车牌
                    if (plateNum1.trim().length() == 0) {// 入口全车牌为空，判断车牌后三位是否一致
                        LogControl.logInfo("入口全车牌为空");
                        plateNum1 = isNull(card1.getVehPlateNum()).trim();// 入口车牌后三位
                        plateNum2 = isNull(card2.getVehPlateNum()).trim();// 出口车牌后三位
                    }
                }
                /*------------------------------modify end----------------------------------------------------------------------------*/

                String color1 = card1.getPlateColor();
                color1 = color1 == null ? "" : color1.trim();
                String color2 = card2.getPlateColor();
                color2 = color2 == null ? "" : color2.trim();
//                if (color1.isEmpty()) {//若入口车牌颜色为空，不进行车牌颜色判断
//                    LogControl.logInfo("入口车牌颜色为空");
//                    color1 = color2;
//                }
                boolean flag = checkCardStatus(card1.getStatus(), lane.getLaneType());
                if (!flag) {//通行卡状态检测
                    card1.setObservationCode("1");
                    LogControl.logInfo("通行卡状态异常" + card1.getStatus());
                    extJFrame.updateInfo("通行卡状态异常", "通行卡状态异常\n等待来自监控室管理员的确认");
                    extJFrame.updateSpecialInfo(SpecialIssue.CARDSTATUSWRONG, null);
                    card1.setRoadid("99");
                    card1.setStationid("97");
                    return card1;
                }
                if (TestControl.isVehDiffIgnored()) {
                    LogControl.logInfo("当前忽略对车型车牌差异的判断");
                } else {
                    if ((c1 != 6 || c2 != 7) && c1 != c2 && !Constant.TRANSIT_CSC_STATUS_06.equals(card1.getStatus())) { //车型差异+非预编码卡(入口6型车，出口7型车不算车型差异) 
                        FlowControl.chargeObserCode = "2";
                        FlowControl.cardObserCode = "J";
                        FlowControl.logout.setClassErrC(FlowControl.logout.getClassErrC() + 1);
                        card1.setObservationCode("3");
                        LogControl.logInfo("车型差异,入口车型：" + c1 + "，出口车型：" + c2);
                        AlarmControl.generateAlarmInfo(Lane.getInstance(), FlowControl.staff.getStaffId(), (short) FlowControl.staff.getShiftId(), "28", (short) 1);
                        extJFrame.updateInfo("车型差异", "车型差异\n通行卡内记录的车型错误\n等待来自监控室管理员的确认");
                        extJFrame.updateSpecialInfo(SpecialIssue.VEHCLASSDIFF, SpecialIssue.UNCONFIRMED);
                        if (!color1.equals(color2) && !Constant.TRANSIT_CSC_STATUS_06.equals(card1.getStatus())) {//车牌颜色差异+非预编码卡
                            extJFrame.updateSpecialInfo(SpecialIssue.VEHCLASSDIFF + "+" + SpecialIssue.VEHPLATECOLDIFF, SpecialIssue.UNCONFIRMED);
                        }
                        if (( //                                !color1.equals(color2) || 
                                !plateNum1.equals(plateNum2))
                                && !Constant.TRANSIT_CSC_STATUS_06.equals(card1.getStatus())) {//车牌差异+非预编码卡
                            FlowControl.logout.setPlateErrC(FlowControl.logout.getPlateErrC() + 1);
                            FlowControl.chargeObserCode = "8";
                            LogControl.logInfo("车牌号码差异" + plateNum1 + " " + plateNum2);
                            extJFrame.updateInfo("车牌差异", "车牌差异\n通行卡内记录的车牌错误\n等待来自监控室管理员的确认");
                            extJFrame.updateSpecialInfo(SpecialIssue.VEHCLASSDIFF + "+" + SpecialIssue.VEHPLATENUMDIFF, SpecialIssue.UNCONFIRMED);
                            card1.setObservationCode("5");
                            if (FunctionControl.isDiffJumpActive()) {//车型车牌差异需要跳站
                                card1.setRoadid("99");
                                card1.setStationid("94");
                            }
                            runShowEntryInfo(card1);
                            return waitForControlRoomConfirmEntry(card1, card2.getVehClass());
                        }
                    } else if (!plateNum1.equals(plateNum2) && !Constant.TRANSIT_CSC_STATUS_06.equals(card1.getStatus())) { //非预编码卡                     //车牌差异检测
                        FlowControl.logout.setPlateErrC(FlowControl.logout.getPlateErrC() + 1);
                        FlowControl.chargeObserCode = "8";
                        FlowControl.cardObserCode = "J";
                        card1.setObservationCode("4");
                        LogControl.logInfo("车牌号码差异" + plateNum1 + " " + plateNum2);
                        extJFrame.updateInfo("车牌差异", "车牌差异\n通行卡内记录的车牌错误\n等待来自监控室管理员的确认");
                        extJFrame.updateSpecialInfo(SpecialIssue.VEHPLATENUMDIFF, SpecialIssue.UNCONFIRMED);
                    } else if (!color1.equals(color2) && !Constant.TRANSIT_CSC_STATUS_06.equals(card1.getStatus())) {//非预编码卡
//                        FlowControl.logout.setPlateErrC(FlowControl.logout.getPlateErrC() + 1);
//                        FlowControl.chargeObserCode = "8";
//                        FlowControl.cardObserCode = "J";
//                        LogControl.logInfo("车牌颜色差异" + color1 + " " + color2);
//                        card1.setObservationCode("4");
//                        extJFrame.updateInfo("车牌差异", "车牌差异\n通行卡内记录的车牌错误\n等待来自监控室管理员的确认");
                        extJFrame.updateSpecialInfo(SpecialIssue.VEHPLATECOLDIFF, null);
                    }
                }

                if ((card1.getRoadid().equals(card2.getRoadid()) && (card1.getStationid().equals(card2.getStationid())))) {//U型车
                    FlowControl.logout.setUturnC(FlowControl.logout.getUturnC() + 1);
                    FlowControl.cardObserCode = "C";
                    if (TestControl.isUVehIgnored()) {
                        LogControl.logInfo("当前忽略对U型车的判断");
                    } else {
                        if (System.currentTimeMillis() - card1.getDhm().getTime() > 2 * 60 * 60 * 1000) {
                            extJFrame.updateSpecialInfo(SpecialIssue.UVEHWITHOUT, null);
                            card1.setObservationCode("7");//U型车行驶时间超过两小时
                            if (Constant.TRANSIT_CSC_STATUS_02.equals(card1.getStatus()) || Constant.TRANSIT_CSC_STATUS_06.equals(card1.getStatus())) {
                                //预编码卡U型车默认跳最近站两倍
                                card1.setRoadid("99");
                                card1.setStationid("98");
                            }
                        } else {
                            card1.setObservationCode("6");//U型车行驶时间两小时之内
                            card1.setRoadid("99");
                            card1.setStationid("98");
                            extJFrame.updateSpecialInfo(SpecialIssue.UVEHWITHIN, null);
                        }
                        LogControl.logInfo("U型车，入口时间为：" + card1.getDhm());
                        runShowEntryInfo(card1);
                        return card1;
                    }
                }
                long minTripTime = getMinTripTime(card1.getRoadid(), card1.getStationid(), card2.getRoadid(), card2.getStationid());
                if ((System.currentTimeMillis() - card1.getDhm().getTime()) < minTripTime) {//车辆小于最小行程时间检测
                    FlowControl.logout.setErTravelTimeC(FlowControl.logout.getErTravelTimeC() + 1);//行程时间不符
                    FlowControl.logout.setLowMinC(FlowControl.logout.getLowMinC() + 1);//小于最小行程时间
                    FlowControl.cardObserCode = "F";
//                    card1.setObservationCode("13");
                    LogControl.logInfo("车辆小于最小行程时间，入口时间为：" + card1.getDhm());
                    //小于最小行程时间仅做记录，不做处理
                }
                long maxTripTime = getMaxTripTime(card1.getRoadid(), card1.getStationid(), card2.getRoadid(), card2.getStationid());
                if ((System.currentTimeMillis() - card1.getDhm().getTime()) > maxTripTime) {//车辆超时检测
                    if (Constant.TRANSIT_CSC_STATUS_02.equals(card1.getStatus()) || Constant.TRANSIT_CSC_STATUS_06.equals(card1.getStatus())) {
                        //预编码卡超时车按照正常车处理
                        return card1;
                    }
                    FlowControl.logout.setOverTimeC(FlowControl.logout.getOverTimeC() + 1);//超时
                    FlowControl.logout.setOverMaxC(FlowControl.logout.getOverMaxC() + 1);//超过最大行程时间
                    FlowControl.logout.setErTravelTimeC(FlowControl.logout.getErTravelTimeC() + 1);//行程时间不符
                    FlowControl.cardObserCode = "D";
                    card1.setObservationCode("2");
                    LogControl.logInfo("车辆超时，入口时间为：" + card1.getDhm());
                    extJFrame.updateInfo("车辆超时", "车辆超时\n等待来自监控室管理员的确认");
                    extJFrame.updateSpecialInfo(SpecialIssue.OVERTIMEVEH, SpecialIssue.UNCONFIRMED);
                    runShowEntryInfo(card1);
                    return waitForControlRoomConfirmEntry(card1, card2.getVehClass());
                }
                if ("0".equals(card1.getObservationCode())) {                   //卡内容完全正常
                    return card1;
                }
                runShowEntryInfo(card1);
                card1 = waitForControlRoomConfirmEntry(card1, card2.getVehClass());                  //监控室确认

            }
        } catch (Exception ex) {
            LogControl.logInfo("核对出入口信息时出现异常", ex);
        }
        return card1;
    }

    /**
     *
     * @param entryCard
     * @param exitCard
     * @return 0正常<br>
     * -1 未知入口站<br>
     * 1 通行卡状态异常<br>
     * 2 超时<br>
     * 3 车型差异<br>
     * 4 车牌差异<br>
     * 5 车型车牌不符<br>
     * 6 U型车两小时之内<br>
     * 7 U型车两小时之外<br>
     * 8 无卡<br>
     * 9 不可读卡<br>
     * 10更改车型<br>
     * 11 更改车种<br>
     * 12残卡<br>
     * 13小于最小行程时间<br>
     *
     */
    public String checkSpecial(Card entryCard, Card exitCard) {
        try {
            if (lane.isEntry()) {
                return "0";
            }
            boolean flag = checkSta(entryCard);
            if (!flag) {//入口站不存在
                return "-1";
            }
            String code = entryCard.getObservationCode();
            code = code == null ? "0" : code;
            if ("8".equals(code) || "12".equals(code)) {//无卡，残卡
                return code;
            }
            if ("9".equals(code)) {//不可读卡
                return code;
            }
            if (!(entryCard instanceof CpuCard)) {//ETC卡不进行卡状态检测
                flag = checkCardStatus(entryCard.getStatus(), Lane.getInstance().getLaneType());
                if (!flag) {//通行卡状态检测
                    return "1";
                }
            }
            boolean vehClassDiffFlag = checkVehClassDiff(entryCard, exitCard);
            boolean vehPlateDiffFlag = checkVehPlateDiff(entryCard, exitCard);
            if (vehClassDiffFlag && vehPlateDiffFlag) {//车型差异+车牌差异
                return "5";
            } else if (vehClassDiffFlag && !vehPlateDiffFlag) {//车型差异
                return "3";
            } else if (!vehClassDiffFlag && vehPlateDiffFlag) {//车牌差异
                return "4";
            }
            boolean uFlag = checkUVehIn2(entryCard, exitCard);//U型车两小时之内
            if (uFlag) {
                return "6";
            }
            uFlag = checkUVehOut2(entryCard, exitCard);//U型车两小时之外
            if (uFlag) {
                return "7";
            }
            int i = checkOvertimeVeh(entryCard, exitCard);
            if (i == 1) {//超时
                return "2";
            } else if (i == -1) {//小于最小行驶时间
                return "13";
            }
        } catch (Exception ex) {
            LogControl.logInfo("核对出入口信息时出现异常", ex);
        }
        return "0";
    }

    /**
     * 检测超时车
     *
     * @param entryCard 入口卡
     * @param exitCard 出口卡
     * @return 0 正常<br>
     * 1 超过最大行驶时间 <br>
     * -1 小于最小行驶时间
     */
    public int checkOvertimeVeh(Card entryCard, Card exitCard) {
        long maxTripTime = getMaxTripTime(entryCard.getRoadid(), entryCard.getStationid(), exitCard.getRoadid(), exitCard.getStationid());
        long minTripTime = getMinTripTime(entryCard.getRoadid(), entryCard.getStationid(), exitCard.getRoadid(), exitCard.getStationid());
        if ((System.currentTimeMillis() - entryCard.getDhm().getTime()) > maxTripTime) {//车辆超时检测
            return 1;
        }
        if ((System.currentTimeMillis() - entryCard.getDhm().getTime()) < minTripTime) {//车辆小于最小行程时间检测
            return -1;
        }
        return 0;
    }

    /**
     * 检测U型车(行驶时间两小时之内)
     *
     * @param entryCard 入口卡
     * @param exitCard 出口卡
     * @return true 两小时之内U型车<br>
     * false
     */
    public boolean checkUVehIn2(Card entryCard, Card exitCard) {
        if (TestControl.isUVehIgnored()) {
            return false;
        }
        StringBuilder sb1 = new StringBuilder();
        sb1.append(entryCard.getRoadid()).append(entryCard.getStationid());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(exitCard.getRoadid()).append(exitCard.getStationid());
        long s1 = entryCard.getDhm().getTime();
        boolean flag = System.currentTimeMillis() - s1 <= 2 * 3600 * 1000;
        return sb1.equals(sb2) && flag;
    }

    /**
     * 检测U型车(行驶时间两小时之外)
     *
     * @param entryCard 入口卡
     * @param exitCard 出口卡
     * @return true两小时之外U型车<BR>
     * false
     */
    public boolean checkUVehOut2(Card entryCard, Card exitCard) {
        if (TestControl.isUVehIgnored()) {
            return false;
        }
        StringBuilder sb1 = new StringBuilder();
        sb1.append(entryCard.getRoadid()).append(entryCard.getStationid());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(exitCard.getRoadid()).append(exitCard.getStationid());
        long s1 = entryCard.getDhm().getTime();
        boolean flag = System.currentTimeMillis() - s1 > 2 * 3600 * 1000;
        return sb1.equals(sb2) && flag;
    }

    /**
     * 检测车型差异
     *
     * @param entryCard 入口卡
     * @param exitCard 出口卡
     * @return true 车型差异<br>
     * false 无车型差异
     */
    public boolean checkVehClassDiff(Card entryCard, Card exitCard) {
        if (TestControl.isVehDiffIgnored()) {
            return false;
        }
        String class1 = entryCard.getVehClass();
        class1 = class1 == null ? "0" : class1;
        String class2 = exitCard.getVehClass();
        class2 = class2 == null ? "0" : class2;
        int c1 = IntegerUtils.parseString(class1);
        int c2 = IntegerUtils.parseString(class2);
        return c1 != c2;
    }

    /**
     * 检测车牌差异
     *
     * @param entryCard 入口卡
     * @param exitCard 出口卡
     * @return true 车牌差异<br>
     * false 无车牌差异
     */
    public boolean checkVehPlateDiff(Card entryCard, Card exitCard) {
        if (TestControl.isVehDiffIgnored()) {
            return false;
        }
        String plateNum1 = entryCard.getFullVehPlateNum();//入口全车牌
        plateNum1 = plateNum1 == null ? "" : plateNum1;
        String plateNum2 = exitCard.getFullVehPlateNum();//出口全车牌
        plateNum2 = plateNum2 == null ? "" : plateNum2;
        if (plateNum1.trim().length() == 0) {//入口全车牌为空，判断车牌后三位是否一致
            LogControl.logInfo("入口全车牌为空");
            plateNum1 = entryCard.getVehPlateNum() == null ? "" : entryCard.getVehPlateNum().trim();//入口车牌后三位
            plateNum2 = exitCard.getVehPlateNum() == null ? "" : exitCard.getVehPlateNum().trim();//出口车牌后三位
        }
        String color1 = entryCard.getPlateColor();
        color1 = color1 == null ? "" : color1.trim();
        String color2 = exitCard.getPlateColor();
        color2 = color2 == null ? "" : color2.trim();
        if (color1.isEmpty()) {//若入口车牌颜色为空，不进行车牌颜色判断
            LogControl.logInfo("入口车牌颜色为空");
            color1 = color2;
        }
        return !color1.equals(color2) || !plateNum1.equals(plateNum2);
    }

    /**
     * 根据车道检测卡片状态
     *
     * @param cardStatus 卡片状态
     * @param laneType 车道类型
     * @return 正常true 异常false
     */
    public boolean checkCardStatus(String cardStatus, int laneType) {
        if (cardStatus == null) {
            return false;
        }
        boolean flag = false;
        switch (laneType) {
            case 1://入口
                flag = Constant.TRANSIT_CSC_STATUS_04.equals(cardStatus);     //入口读到卡状态为已发卡，状态异常
                if (!flag) {
                    LogControl.logInfo("卡状态异常");
                    FlowControl.logout.setStatuErrC(FlowControl.logout.getStatuErrC() + 1);//状态不对+1
                    FlowControl.logout.setErTTC(FlowControl.logout.getErTTC() + 1);//使用状态错误+1
                    FlowControl.cardObserCode = "R";
                } else {
                    LogControl.logInfo("卡状态正常");
                }
                break;
            case 2://出口
                boolean flag1 = Constant.TRANSIT_CSC_STATUS_01.equals(cardStatus);//已发卡
                boolean flag2 = Constant.TRANSIT_CSC_STATUS_02.equals(cardStatus);//已发预编码卡
                boolean flag3 = Constant.TRANSIT_CSC_STATUS_03.equals(cardStatus);//已发维修卡
                boolean flag6 = Constant.TRANSIT_CSC_STATUS_06.equals(cardStatus);//预编码卡
                if (flag2 || flag6) {//预编码卡
                    LogControl.logInfo("预编码卡");
                    FlowControl.cardObserCode = "Q";
                    FlowControl.logout.setPrecodedTTC(FlowControl.logout.getPrecodedTTC() + 1);
                }
                if (flag3) {//维修模式发出通行卡
                    LogControl.logInfo("维修模式发出通行卡");
                    FlowControl.cardObserCode = "K";
                    FlowControl.logout.setTryTTC(FlowControl.logout.getTryTTC() + 1);
                }
                if (!(flag1 || flag2 || flag3 || flag6)) {
                    LogControl.logInfo("卡状态" + cardStatus + "异常");
                    FlowControl.logout.setStatuErrC(FlowControl.logout.getStatuErrC() + 1);//状态不对+1
                    FlowControl.logout.setErTTC(FlowControl.logout.getErTTC() + 1);//使用状态错误+1
                    FlowControl.cardObserCode = "R";
                }
                flag = flag1 || flag2 || flag6;
                break;
            default:
                break;
        }
        if (TestControl.isCardStatusIgnored()) {
            LogControl.logInfo("当前忽略对卡状态的判断");
            return true;
        }
        return flag;
    }

    /**
     * 检测入口站是否存在，不存在自动改为9996
     *
     * @param card 通行券
     */
    public void checkStation(Card card) {
        if (!lane.isExit()) {//仅出口才调用此方法
            return;
        }
        String roadid = card.getRoadid();
        if ("99".equals(roadid)) {//若入口为虚拟站，不需要再检测
            return;
        }
        String stationid = card.getStationid();
        int intRoadid = IntegerUtils.parseString(roadid);
        int intStationid = IntegerUtils.parseString(stationid);
        Station sta = ParamCacheQuery.queryStation(intRoadid, intStationid);
        if (sta == null) {
            LogControl.logInfo("未知入口站：" + roadid + stationid);
            extJFrame.updateSpecialInfo(SpecialIssue.UNKNOWNENTRY, null);
            FlowControl.cardObserCode = "B";
            FlowControl.logout.setErStationC(FlowControl.logout.getErStationC() + 1);
            card.setRoadid("99");
            card.setStationid("96");
        }
    }

    /**
     * 检测入口站是否存在
     *
     * @param card 入口卡
     * @return true 存在<br>
     * false 不存在
     */
    public boolean checkSta(Card card) {
        String roadid = card.getRoadid();
        if ("99".equals(roadid)) {//若入口为虚拟站，不需要再检测
            return true;
        }
        String stationid = card.getStationid();
        int intRoadid = IntegerUtils.parseString(roadid);
        int intStationid = IntegerUtils.parseString(stationid);
        Station sta = ParamCacheQuery.queryStation(intRoadid, intStationid);
        if (sta == null) {
            return false;
        }
        return true;
    }

    /**
     * 更新入口车辆图片面板，对应imgList中的元素
     */
    public void updateEntryPhoto() {
        try {
            if (imgList != null && !imgList.isEmpty()) {
                extJFrame.updateEntryInfoPhoto(imgList.get(imgFlag));
                if (++imgFlag == imgList.size()) {
                    imgFlag = 0;//入口图片标识符，使用该标识符控制多张入口图片的显示
                }
            }
        } catch (Exception ex) {
            LogControl.logInfo("显示入口图片出现异常", ex);
        }
    }

    /**
     * 更新车牌黑名单车辆图片面板，对应blackPlateImgList中的元素
     */
    public void updateBlackPlatePhoto() {
        try {
            if (blackPlateImgList != null && !blackPlateImgList.isEmpty()) {
                extJFrame.updateBlackPlatePhoto(blackPlateImgList.get(blackPlateImgFlag));
                if (++blackPlateImgFlag == blackPlateImgList.size()) {
                    blackPlateImgFlag = 0;//车牌黑名单图片标识符，使用该标识符控制多张车牌黑名单图片的显示
                }
            }
        } catch (Exception ex) {
            LogControl.logInfo("显示车牌黑名单图片出现异常", ex);
        }
    }

    /**
     * 显示入口信息以及从入口获取的图片
     *
     * @param card 从入口通行卡（或ETC卡）读取的信息
     */
    public void runShowEntryInfo(final Card card) {
        try {
            if (!lane.isExit()) {//只有出口才调用显示入口图片信息
                return;
            }
            String code = card.getObservationCode();
            if ("8".equals(code) || "9".equals(code) || "12".equals(code)) {//无卡，残卡，不可读卡不可能查到入口信息
                LogControl.logInfo("入口为无卡，残卡，不可读卡无法调用入口站信息");
                return;
            }
            LogControl.logInfo("显示入口信息" + card);
            Date ts = card.getDhm();
            String str = "";
            if (ts != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                str = sdf.format(ts);
            }
            String entryDHM = "日期：" + (str == null ? "" : str);
            String road = FlowControl.trafficEnRoadid == null ? "" : FlowControl.trafficEnRoadid;
            String station = FlowControl.trafficEnStationid == null ? "" : FlowControl.trafficEnStationid;
            String entryStation = "收费站：" + road + station;
            String laneid = card.getLaneId();
            String entryLane = "车道：" + (laneid == null ? "" : laneid);
            String staff = card.getStaffId();
            String entryStaff = "职员号：" + (staff == null ? "" : staff);
            String vehClass = card.getVehClass();
            String entryVehClass = "车型：" + (vehClass == null ? "" : vehClass);
            String cardSerial = card.getCardSerial();
            String entryCardSerial = "序列号：" + (cardSerial == null ? "" : cardSerial);
            String plate = card.getFullVehPlateNum();
            String entryVehPlate = "车牌：" + (plate == null ? "" : plate) + "      " + getZHPlateColor(card.getPlateColor());
            extJFrame.showEntryInfoPanel(entryDHM, entryStation, entryLane, entryStaff, entryVehClass, entryVehPlate, entryCardSerial);
            if (this.entryPhotoFlag) {//正在请求入口图片，不必再次请求
                LogControl.logInfo("正在请求入口图片,不必再次请求");
                return;
            }
        } catch (Exception ex) {
            LogControl.logInfo("显示入口信息异常", ex);
        }
        // 此处创建新的线程，避免调用入口照片所用时间导致收费员等待
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    entryPhotoFlag = true;
                    imgList = getEntryVehPhoto(card);
                    updateEntryPhoto();
                    entryPhotoFlag = false;//请求入口图片已结束
                } catch (Exception ex) {
                    LogControl.logInfo("获取入口图片并显示时出现异常", ex);
                }
            }
        });
    }

    /**
     * 核对车辆黑白灰名单
     *
     * @author 王国栋
     * @param veh 车辆信息
     * @return 0 普通车辆 1 白名单车辆免费通行 2 违禁车辆不允许通过 3 违禁车辆但允许通过
     */
    public int checkSpeVeh(Vehicle veh) {
        int flag = 0;
        veh = getSpeVehPlag(veh);                                               //获取车辆黑白灰名单信息
        if (veh.getSpeVehFlag() == 0) {                                         //黑名单车辆
            FlowControl.chargeObserCode = "6";
            if (Lane.getInstance().isEntry()
                    && AutoMachineControl.getSingleInstance().isActivated()
                    && FunctionControl.isAutoBlkVehAlertActive()) {//入口自助发卡,支持黑名单车辆语音提示
                AudioControl.getSingleInstance().playAudioBlackVeh();//黑名单车辆语音提示
            }
            AlarmControl.generateAlarmInfo(Lane.getInstance(), FlowControl.staff.getStaffId(), (short) FlowControl.staff.getShiftId(), "32", (short) 1);
            LogControl.logInfo("黑名单车辆：" + veh.getFullVehPlateNum());
            fc.sendInfo("黑名单车辆：" + veh.getFullVehPlateNum());
            extJFrame.updateInfo("等待监控室确认", "黑名单车辆\n等待监控室管理员确认");
            runShowBlackPlateInfo(veh); //从监控室调取车牌黑名单图片 add by wanghao.
            int i = 0; //图片索引
            flag = waitForControlRoomConfirmBGW(veh);
            if (flag == 3) {
                extJFrame.updateInfo("请按【模拟】键授权", "当前车辆被禁止在高速公路通行\n请按【模拟】键授权 允许该车辆通行\n或按【取消】键");
                String str;
                OUTER:
                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                    }
                    str = keyboard.waitForOrder();
                    switch (str) {
                        case "取消":
                            flag = 2;
                            break OUTER;
                        case "模拟":
                            flag = 3;
                            break OUTER;
                        case "上":                                                  //收费员通过上下方向键选择图片
                            if (!blackPlateImgList.isEmpty()) {//图片不为空
                                if (++i == blackPlateImgList.size()) {
                                    i = 0;
                                }
                                extJFrame.updateBlackPlatePhoto(blackPlateImgList.get(i));
                            }
                            break;
                        case "下":                                                  //收费员通过上下方向键选择图片
                            if (!blackPlateImgList.isEmpty()) {
                                if (i == 0) {
                                    i = blackPlateImgList.size() - 1;
                                } else {
                                    i--;
                                }
                                extJFrame.updateBlackPlatePhoto(blackPlateImgList.get(i));
                            }
                            break;
                        default:
                            keyboard.sendAlert();
                            break;
                    }
                }
            } else if (flag == 2) {
                extJFrame.updateInfo("禁止通行", "当前车辆被禁止在高速公路通行\n按【取消】键回退");
                String str;
                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                    }
                    str = keyboard.waitForOrder();
                    if (str.equals("取消")) {
                        break;
                    } else {
                        keyboard.sendAlert();
                    }
                }
            }

        } else if (veh.getSpeVehFlag() == 1) {                                  //灰名单车辆
            FlowControl.chargeObserCode = "6";
            if (Lane.getInstance().isEntry()
                    && AutoMachineControl.getSingleInstance().isActivated()
                    && FunctionControl.isAutoGrayVehAlertActive()) {//入口自助发卡,支持灰名单语音提示
                AudioControl.getSingleInstance().playAudioGrayVeh();//灰名单车辆语音提示
            }
            LogControl.logInfo("灰名单车辆" + veh.getFullVehPlateNum());
            fc.sendInfo("灰名单车辆：" + veh.getFullVehPlateNum());
            extJFrame.showViolateVehPanel("车辆在灰名单上", veh.getFullVehPlateNum(), veh.getDescribe());
            extJFrame.updateInfo("请按【模拟】键授权", "当前车辆被禁止在高速公路通行\n请按【模拟】键授权 允许该车辆通行\n或按【取消】键");
            String str;
            outer:
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                str = keyboard.waitForOrder();
                switch (str) {
                    case "模拟":
                        flag = 3;
                        break outer;
                    case "取消":
                        flag = 2;
                        break outer;
                    default:
                        keyboard.sendAlert();
                }
            }
        } else if (veh.getSpeVehFlag() == 9 && Lane.getInstance().isExit()) {     //白名单车辆，只在出口检测
            LogControl.logInfo("白名单车辆" + veh.getFullVehPlateNum());
            fc.sendInfo("白名单车辆：" + veh.getFullVehPlateNum());
            extJFrame.updateInfo("车牌在内部车辆名单中", "当前车辆车牌号码在内部车辆名单中\n按【确认】键授权免费通行\n或按【取消】键按普通车处理");
            List<String> list = waitForFreeVehImg(veh);                      //从监控室获取免费车图片
            int i = 0;
            if (list == null || list.isEmpty()) {
                extJFrame.showFreeVehPanel("找不到对应车牌号码\n请与监控员联系");
            } else {
                extJFrame.showFreeVehPanel("可以免费通行\n其他信息：" + (veh.getDescribe() == null ? "" : veh.getDescribe()));
                extJFrame.updateFreeVehPhoto(list.get(i));
            }
            String str;
            outer:
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                str = keyboard.waitForOrder();
                switch (str) {
                    case "确认":
                        flag = 1;
                        break outer;
                    case "取消":
                        flag = 0;
                        break outer;
                    case "上":                                                  //收费员通过上下方向键选择图片
                        if (++i == list.size()) {
                            i = 0;
                        }
                        extJFrame.updateFreeVehPhoto(list.get(i));
                        break;
                    case "下":                                                  //收费员通过上下方向键选择图片
                        if (i == 0) {
                            i = list.size() - 1;
                        } else {
                            i--;
                        }
                        extJFrame.updateFreeVehPhoto(list.get(i));
                        break;
                    default:
                        keyboard.sendAlert();
                }
            }
        } else if (veh.getSpeVehFlag() == 2) {                                  //正常车辆
            flag = 0;
        }
        return flag;
    }

    /**
     * 显示车牌黑名单信息以及从监控室获取的图片
     *
     * @author wh 2015-07-13
     * @param veh 车辆信息
     */
    public void runShowBlackPlateInfo(final Vehicle veh) {
        // 此处创建新的线程，避免调用车牌黑名单照片所用时间导致收费员等待
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
//                    if (!lane.isExit()) {//只有出口才调用显示入口图片信息
//                        return;
//                    }
                    LogControl.logInfo("调取车牌黑名单图片" + veh);
//                    String road = FlowControl.trafficEnRoadid == null ? "" : FlowControl.trafficEnRoadid;
//                    String station = FlowControl.trafficEnStationid == null ? "" : FlowControl.trafficEnStationid;
//                    String entryStation = "收费站：" + road + station;
//                    String vehClass = getZHVehicleClass(veh.getVehicleClass());
//                    String entryVehClass = "车型：" + (vehClass == null ? "" : vehClass);
                    String blackPlateVehPlate = "车牌：" + (veh.getFullVehPlateNum() == null ? "" : veh.getFullVehPlateNum()) + "      " + getZHPlateColor(veh.getKeyedPlateColor());
                    String blackPlateViolateReason = "违禁车辆原因：" + (veh.getDescribe() == null ? "" : veh.getDescribe());

                    extJFrame.showBlackPlateInfoPanel("车辆在黑名单上", blackPlateVehPlate, blackPlateViolateReason);
                    blackPlateImgList = getPlateBlackPhoto(veh.getFullVehPlateNum());
                    updateBlackPlatePhoto();
                } catch (Exception ex) {
                    LogControl.logInfo("获取车牌黑名单信息及图片并显示时出现异常", ex);
                }
            }
        });
    }

    /**
     * 生成出口交易流水实体类
     *
     * @param trafficEnRoadid 物理入口路段
     * @param trafficEnStationid 物理入口站
     * @param vehObserCode 车辆通行观察码
     * @param chargeObserCode 收费操作观察码
     * @param mopObserCode 付款方式观察码
     * @param cardObserCode 通行卡观察码
     * @param axleGroupStr 轴组集合
     * @param axleGroupPreStr 修改前轴组集合
     * @param preWeight 修改前轴重
     * @param axleWeightDetail 单轴轴重详细信息
     * @param staff 员工号
     * @param lane 车道
     * @param entryCard 入口通行卡
     * @param exitCard 出口通行卡
     * @param veh 车辆
     * @param ticketNum 票据号
     * @param entryCpuCard 入口ETC卡
     * @param exitCpuCard 出口ETC卡
     * @param orderPay 应付金额
     * @param pay 实付金额
     * @param exitMop 出口付款方式
     * @param vehCount 交易号(无上限，满足实际情况下交易号超过9999的情况)
     * @param recordId 交易记录编码，最大为9999，9999之后从1开始（对应交易流水中的交易记录编码）
     * @param trackNo 字轨号
     * @param ticketType 通行券类型
     * @return 出口流水实体类
     */
    private LaneExList generateExtList(String trafficEnRoadid, String trafficEnStationid, String vehObserCode, String chargeObserCode, String mopObserCode, String cardObserCode,
            //            String axleGroupStr, 
            String axleGroupPreStr, double preWeight,
            //            String axleWeightDetail,
            Staff staff, Lane lane, Card entryCard, Card exitCard, Vehicle veh, String ticketNum, CpuCard entryCpuCard, CpuCard exitCpuCard, double orderPay, double pay, String exitMop, int vehCount, int recordId, String trackNo, int ticketType, String serviceCardSerial) {
        LaneExList lel = new LaneExList();
        try {
            if (staff == null) {
                staff = new Staff();
            }
            if (veh == null) {
                veh = new Vehicle();
            }
            exitMop = exitMop == null ? "0" : exitMop;
            if (!"01".equals(exitMop)) {//非现金付款时，无发票号码
                ticketNum = "0";
            }
            entryCard = entryCard == null ? new Card() : entryCard;
            exitCard = exitCard == null ? new Card() : exitCard;
            ticketNum = ticketNum == null ? "0" : ticketNum;
            entryCpuCard = entryCpuCard == null ? new CpuCard() : entryCpuCard;
            exitCpuCard = exitCpuCard == null ? new CpuCard() : exitCpuCard;

            Date ts = exitCard.getDhm();
            ts = ts == null ? new Date(System.currentTimeMillis()) : ts;
            lel.setOpTime(ts);//出口流水时间（写卡时间）
            lel.setCardBoxNo(exitCard.getCartId() == null ? "" : exitCard.getCartId());
            lel.setChargeType(Constant.CHARGE_TYPE);
            lel.setComputerMoney((int) DoubleUtils.mul(orderPay, 100));//应收金额
            if (entryCard != null) {
                String laneid = entryCard.getLaneId();
                if (laneid == null) {
                    laneid = "000";
                }
                lel.setEnLaneNo(laneid);
                lel.setEnLaneId(IntegerUtils.parseString(laneid.substring(1)));
                int laneType = 0;
                String str = laneid.substring(0, 1);
                switch (str.toUpperCase()) {
                    case "E":
                        laneType = 1;
                        break;
                    case "X":
                        laneType = 2;
                        break;
                    default:
                        laneType = 0;
                        break;
                }
                lel.setEnLaneType(laneType);
                lel.setEnOpTime(entryCard.getDhm() == null ? new Date(0) : entryCard.getDhm());
                lel.setEnOperatorNo(entryCard.getStaffId() == null ? "" : entryCard.getStaffId());
                trafficEnRoadid = trafficEnRoadid == null ? "099" : trafficEnRoadid;
                trafficEnStationid = trafficEnStationid == null ? "095" : trafficEnStationid;
                lel.setEnRoadId(IntegerUtils.parseString(trafficEnRoadid));
                lel.setEnStationId(IntegerUtils.parseString(trafficEnStationid));
                String stationName = getStationName(trafficEnRoadid, trafficEnStationid);
                lel.setEnStationName(stationName == null ? "" : stationName);
                String roadid = entryCard.getRoadid();
                if (roadid == null) {
                    roadid = "099";
                }
                String stationid = entryCard.getStationid();
                if (stationid == null) {
                    stationid = "096";
                }
                String fareEnStation = StringUtils.toLength(roadid, 3) + StringUtils.toLength(stationid, 3);
                lel.setSpare3(fareEnStation);//计费入口路段号+收费站号
                lel.setEnVehPlate(entryCard.getFullVehPlateNum() == null ? "" : entryCard.getFullVehPlateNum());
                lel.setEnVehPlateColor(transPlateColorCode(entryCard.getPlateColor()));
                String vehClass = entryCard.getVehClass();
                lel.setEnVehType(IntegerUtils.parseString(vehClass == null ? "0" : vehClass));//入口车型
                lel.setEnVehClass(0);//入口车种,默认为0
                lel.setEnSquadId(0);//入口工班号，无法获取，默认为0
                lel.setEnListNo(StringUtils.getListNo(trafficEnRoadid, trafficEnStationid, laneid, lel.getEnOpTime(), entryCard.getTransNum()));
                String cardSerial = entryCard.getCardSerial();
                if (cardSerial == null || cardSerial.length() != 10) {//ETC卡做通行卡使用时passcardno为空
                    lel.setPassCardNo("");
                } else {
                    lel.setPassCardNo(cardSerial);
                }
            }
            lel.setGetMoney((int) DoubleUtils.mul(pay, 100));//实收金额
            lel.setInvoiceId(ticketNum == null ? 0 : IntegerUtils.parseString(ticketNum));
            if (lane != null) {
                String laneid = lane.getLaneId();
                if (laneid == null) {
                    laneid = "000";
                }
                lel.setLaneNo(laneid);
                lel.setLaneId(IntegerUtils.parseString(laneid.substring(1)));
                lel.setLaneType(lane.getLaneType());
                String roadid = lane.getRoadId();
                if (roadid == null) {
                    roadid = "000";
                }
                lel.setRoadId(IntegerUtils.parseString(roadid));
                String stationid = lane.getStationId();
                if (stationid == null) {
                    stationid = "000";
                }
                lel.setListNo(StringUtils.getListNo(roadid, stationid, laneid, lel.getOpTime(), recordId));
                lel.setStationId(IntegerUtils.parseString(stationid));
                lel.setStationName(lane.getStationName() == null ? "" : lane.getStationName());
            }

            if (staff != null) {
                lel.setLoginTime(staff.getJobStartTime() == null ? new Date(0) : staff.getJobStartTime());//出口员工上班时间
                lel.setSquadDate(staff.getSquadDate() == null ? new Date(0) : staff.getSquadDate());//排班日期
                lel.setSquadId(staff.getSquadId());//班次
                lel.setOperatorName(staff.getStaffName() == null ? "" : staff.getStaffName());//出口员工姓名
                lel.setOperatorNo(staff.getStaffId() == null ? "" : staff.getStaffId());//出口员工工号
                lel.setOpCardNo(staff.getStaffCardSerial() == null ? "" : staff.getStaffCardSerial());//员工身份卡序列号
            }
            if (veh != null) {
                lel.setAxisGroupNum(veh.getAxleGroupCount());
                lel.setAxisNum(veh.getAxleCount());
                double weight = veh.getWeight();
                double fareWeight = veh.getFareWeight();
                double limitWeight = veh.getLimitWeight();
                lel.setTotalWeight(weight);//检测重量
                lel.setTotalWeightForCharge(fareWeight);//收费重量
                lel.setTotalWeightLimit(limitWeight);//限重
                if (fareWeight > limitWeight) {
                    double overWeight = DoubleUtils.sub(fareWeight, limitWeight);
                    lel.setOverWeight(overWeight);//超重
                    double overRate = DoubleUtils.div(overWeight, limitWeight, 2);
                    lel.setOverRatio(overRate);//超重比例
                } else {//未超载
                    lel.setOverWeight(0.0d);//超重
                    lel.setOverRatio(0.0d);//超重比例
                }

                lel.setPlateAutoColor(transPlateColorCode(veh.getLprPlateColor()));//自动识别车牌颜色
                lel.setVehType(veh.getVehicleClass());//出口车型
                lel.setVehClass(veh.getVehicleType());//出口车种
                lel.setVehPlate(veh.getFullVehPlateNum() == null ? "" : veh.getFullVehPlateNum());
                lel.setVehTypeAuto(0);//车型自动识别
                lel.setVehPlateAuto(veh.getLprFullVehPlateNum() == null ? "" : veh.getLprFullVehPlateNum());
                lel.setVehPlateColor(transPlateColorCode(veh.getKeyedPlateColor()));
                int weightFlag = 0;
                int i = veh.getVehicleClass();
                if (i == 7 || i == 8 || i == 9) {
                    weightFlag = 1;
                } else if (i < 7) {
                    weightFlag = 0;
                }
                lel.setWeightFlag(weightFlag);
            }
//            if (entryCpuCard == null || entryCpuCard.getCardSerial() == null) {
//                if (entryCard == null || entryCard.getCardSerial() == null) {
//                    lel.setTicketType(0);//通行券类型为无卡
//                } else {
//                    lel.setTicketType(1);//通行券类型为通行卡
//                }
//            } else {
//                lel.setTicketType(2);//通行券类型为ETC卡
//            }
            lel.setOfficeMoney(0);//公务车金额
            lel.setOfficialCardNo("");//公务卡卡号
            switch (exitMop) {
                case "01"://现金付款
                    lel.setPayCardType(1);//支付卡类型
                    lel.setCashMoney((int) DoubleUtils.mul(pay, 100));//现金金额
                    lel.setFreeMoney(0);//免费车金额
                    lel.setOfficeMoney(0);//公务车金额
                    lel.setUnpayMoney(0);//未付金额
                    lel.setGetMoney((int) DoubleUtils.mul(pay, 100));//实收金额
                    lel.setxTCardMoney(0);//ETC卡金额
                    lel.setuPCardMoney(0);//银联卡金额
                    lel.setpDiscountToll(0);//ETC卡付款优惠前金额
                    lel.setxTCardDiscount(0.0);//ETC卡折扣率
                    lel.setxTCardBalanceBf(0L);//ETC卡扣款前余额
                    lel.setxTCardBalanceAf(0L);//ETC卡扣款后余额

                    break;
                case "17"://军警车
                    lel.setPayCardType(17);//支付卡类型
                    lel.setFreeMoney((int) DoubleUtils.mul(orderPay - pay, 100));//免费车金额
                    lel.setCashMoney(0);//现金金额
                    lel.setOfficeMoney(0);//公务车金额
                    lel.setUnpayMoney(0);//未付金额
                    lel.setGetMoney((int) DoubleUtils.mul(pay, 100));//实收金额
                    lel.setxTCardMoney(0);//ETC卡金额
                    lel.setuPCardMoney(0);//银联卡金额
                    lel.setpDiscountToll(0);//ETC卡付款优惠前金额
                    lel.setxTCardDiscount(0.0);//ETC卡折扣率
                    lel.setxTCardBalanceBf(0L);//ETC卡扣款前余额
                    lel.setxTCardBalanceAf(0L);//ETC卡扣款后余额
                    break;
                case "40"://内部车
                    lel.setPayCardType(40);//支付卡类型
                    lel.setFreeMoney((int) DoubleUtils.mul(orderPay - pay, 100));//免费车金额
                    lel.setCashMoney(0);//现金金额
                    lel.setOfficeMoney(0);//公务车金额
                    lel.setUnpayMoney(0);//未付金额
                    lel.setGetMoney((int) DoubleUtils.mul(pay, 100));//实收金额
                    lel.setxTCardMoney(0);//ETC卡金额
                    lel.setuPCardMoney(0);//银联卡金额
                    lel.setpDiscountToll(0);//ETC卡付款优惠前金额
                    lel.setxTCardDiscount(0.0);//ETC卡折扣率
                    lel.setxTCardBalanceBf(0L);//ETC卡扣款前余额
                    lel.setxTCardBalanceAf(0L);//ETC卡扣款后余额
                    break;
                case "23"://紧急车
//                    lel.setTicketType(0);//通行券类型
                    lel.setPayCardType(23);//支付卡类型
                    lel.setFreeMoney((int) DoubleUtils.mul(orderPay - pay, 100));//免费车金额
                    lel.setCashMoney(0);//现金金额
                    lel.setOfficeMoney(0);//公务车金额
                    lel.setUnpayMoney(0);//未付金额
                    lel.setGetMoney((int) DoubleUtils.mul(pay, 100));//实收金额
                    lel.setxTCardMoney(0);//ETC卡金额
                    lel.setuPCardMoney(0);//银联卡金额
                    lel.setpDiscountToll(0);//ETC卡付款优惠前金额
                    lel.setxTCardDiscount(0.0);//ETC卡折扣率
                    lel.setxTCardBalanceBf(0L);//ETC卡扣款前余额
                    lel.setxTCardBalanceAf(0L);//ETC卡扣款后余额
                    break;
                case "12"://银联卡付款
                    lel.setPayCardType(12);//支付卡类型
                    lel.setuPCardMoney((int) DoubleUtils.mul(pay, 100));//银联卡金额
                    lel.setFreeMoney(0);//免费车金额
                    lel.setCashMoney(0);//现金金额
                    lel.setOfficeMoney(0);//公务车金额
                    lel.setUnpayMoney(0);//未付金额
                    lel.setGetMoney((int) DoubleUtils.mul(pay, 100));//实收金额
                    lel.setxTCardMoney(0);//ETC卡金额
                    lel.setpDiscountToll(0);//ETC卡付款优惠前金额
                    lel.setxTCardDiscount(0.0);//ETC卡折扣率
                    lel.setxTCardBalanceBf(0L);//ETC卡扣款前余额
                    lel.setxTCardBalanceAf(0L);//ETC卡扣款后余额
                    break;
                case "51"://ETC卡付款
                    lel.setPayCardType(51);//支付卡类型
                    lel.setuPCardMoney(0);//银联卡金额
                    lel.setFreeMoney(0);//免费车金额
                    lel.setCashMoney(0);//现金金额
                    lel.setOfficeMoney(0);//公务车金额
                    lel.setUnpayMoney(0);//未付金额
                    lel.setGetMoney((int) DoubleUtils.mul(pay, 100));//实收金额
                    lel.setxTCardMoney((int) DoubleUtils.mul(pay, 100));//ETC卡金额
                    lel.setpDiscountToll((int) DoubleUtils.mul(orderPay, 100));//ETC卡付款优惠前金额
                    Double dis = exitCpuCard.getDiscount();
                    if (dis == null) {
                        dis = 0.0;
                    }
                    lel.setxTCardDiscount(dis);//ETC卡折扣率
                    lel.setxTCardBalanceBf((long) DoubleUtils.mul(entryCpuCard.getBalance(), 100));//ETC卡扣款前余额
                    lel.setxTCardBalanceAf((long) DoubleUtils.mul(exitCpuCard.getBalance(), 100));//ETC卡扣款后余额
//                    lel.setxTCardBalanceBf(exitCpuCard.getIbalance1());//ETC卡扣款前余额
//                    lel.setxTCardBalanceAf(exitCpuCard.getIbalance2());//ETC卡扣款后余额
                    lel.seteTCTac(exitCpuCard.getTacCode() == null ? "0" : exitCpuCard.getTacCode());//TAC码
                    break;
                case "60"://纸券
                    lel.setPayCardType(IntegerUtils.parseString(exitMop));
                    lel.setuPCardMoney(0);//银联卡金额
                    lel.setFreeMoney((int) DoubleUtils.mul(orderPay - pay, 100));//免费车金额
                    lel.setCashMoney(0);//现金金额
                    lel.setOfficeMoney(0);//公务车金额
                    lel.setUnpayMoney(0);//未付金额
                    lel.setGetMoney(0);//实收金额
                    lel.setxTCardMoney(0);//ETC卡金额
                    lel.setpDiscountToll(0);//ETC卡付款优惠前金额
                    lel.setxTCardDiscount(0.0);//ETC卡折扣率
                    lel.setxTCardBalanceBf(0L);//ETC卡扣款前余额
                    lel.setxTCardBalanceAf(0L);//ETC卡扣款后余额
//                    lel.setTicketType(4);
                    break;
                case "39"://公务车
                    lel.setComputerMoney(0);//公务车应收金额为0
                    lel.setPayCardType(IntegerUtils.parseString(exitMop));
                    lel.setuPCardMoney(0);//银联卡金额
                    lel.setFreeMoney(0);//免费车金额
                    lel.setCashMoney(0);//现金金额
                    lel.setOfficeMoney((int) DoubleUtils.mul(orderPay, 100));//公务车金额
                    lel.setOfficialCardNo(serviceCardSerial);//公务卡卡号
                    lel.setUnpayMoney(0);//未付金额
                    lel.setGetMoney(0);//实收金额
                    lel.setxTCardMoney(0);//ETC卡金额
                    lel.setpDiscountToll(0);//ETC卡付款优惠前金额
                    lel.setxTCardDiscount(0.0);//ETC卡折扣率
                    lel.setxTCardBalanceBf(0L);//ETC卡扣款前余额
                    lel.setxTCardBalanceAf(0L);//ETC卡扣款后余额
                    break;
                default:
                    lel.setPayCardType(IntegerUtils.parseString(exitMop));//支付卡类型
                    break;
            }
            if (exitCpuCard != null) {
                lel.seteTCTermCode(exitCpuCard.getTermCode() == null ? "0" : exitCpuCard.getTermCode());
                lel.seteTCTermTradNo(exitCpuCard.getTermTradNo() == null ? "0" : exitCpuCard.getTermTradNo());
                lel.seteTCTradeNo(exitCpuCard.getTradeNo() == null ? "0" : exitCpuCard.getTradeNo());
                lel.setpSAMCardNo(exitCpuCard.getPsamNum() == null ? "0" : exitCpuCard.getPsamNum());
                lel.setxTCardNo(exitCpuCard.getCardSerial() == null ? "" : exitCpuCard.getCardSerial());//ETC卡内部编号
                String cardType = exitCpuCard.getCardType();
                if (cardType == null) {
                    cardType = "00";
                }
                lel.setxTCardType(IntegerUtils.parseString(cardType));//ETC卡类型  
                String str = exitCpuCard.getUserType();
                str = str == null ? "0" : str;
                lel.setSpare1(IntegerUtils.parseString(str));//ETC卡用户类型
            }
            lel.setoBUId(0);
            lel.setCardSelcetCode(cardObserCode == null ? "" : cardObserCode);//通行卡观察码
            lel.setOpSelcetCode(chargeObserCode == null ? "" : chargeObserCode);//收费操作观察码
            lel.setVehChargeSelcetCode(mopObserCode == null ? "" : mopObserCode);//付款方式观察码
            lel.setVehPassSelcetCode(vehObserCode == null ? "" : vehObserCode);//车辆通行观察码
            String axleGroupStr = veh.getAxleGroupStr();
            lel.setAxisType(axleGroupStr == null ? "" : axleGroupStr);//轴组类型集合
            lel.setPreAxisType(axleGroupPreStr == null ? "" : axleGroupPreStr);//修改前轴组类型集合
            String axleWeightDetail = veh.getAxleWeightDetail();
            lel.setAxisWeightDetail(axleWeightDetail == null ? "" : axleWeightDetail);//单轴轴重集合
            lel.setuPCardBalanceBf(0L);//银联卡扣款前余额
            lel.setuPCardBalanceAf(0L);//银联卡扣款后余额
            lel.setuPCardDiscount(0.0);//银联卡折扣率
            lel.setuPCardNo("");//银联卡卡号
            lel.setuPCardType(0);//银联卡类型
            lel.setPreTotalWeight(preWeight);//修改前总轴重
            lel.setPreVehType(0);//降档前车型
            lel.setPreVehMoney(0);//降档前金额
            String priceVer = ParamCacheQuery.queryFareVer();//获取费率版本号
            if (priceVer != null && priceVer.matches("^[0-9]*$")) {//验证费率版本号是否是数字
                lel.setPriceVer(IntegerUtils.parseString(priceVer));//费率版本号
            } else {
                lel.setPriceVer(0);//费率版本号
            }
            lel.setProgramVer(Constant.LANE_SOFT_VERSION);
            lel.setSpEvent("0");//特殊事件
            lel.setUnpayMoney(0);//未付金额
            lel.setSpare4(trackNo == null ? "0" : trackNo);

            lel.setFreeRoadIdSer("");//免费路段编码序列
            lel.setSectionCharge("");
            lel.setSectionPath("");
            lel.setVehImageNo(lel.getListNo() + "-1");//全景图
            lel.setPlateImageNo(lel.getListNo() + "-2");//车牌图
            lel.setRecordId(recordId);
            lel.setSpare2(vehCount);
            lel.setTicketType(ticketType);
            lel.setTransferTag(0);
        } catch (Exception ex) {
            LogControl.logInfo("产生出口交易流水异常:" + lel, ex);
        }
        return lel;
    }

    /**
     * 产生入口交易流水实体类
     *
     * @param cpuCard ETC卡
     * @param lane 车道
     * @param staff 员工
     * @param entryCard 通行卡
     * @param veh 车辆
     * @param vehCount 交易号
     * @param entryMop 入口付款方式
     * @param vehObserCode 车辆通行观察码
     * @param chargeObserCode 收费操作观察码
     * @param mopObserCode 付款方式观察码
     * @param cardObserCode 通行卡观察码
     * @param recordId 交易记录编码，最大为9999，9999之后从1开始（对应交易流水中的交易记录编码）
     * @param trackNo 字轨号
     * @return 入口流水实体类
     */
    private LaneEnList generateEntList(CpuCard cpuCard, Lane lane, Staff staff, Card entryCard, Vehicle veh, int vehCount, String entryMop,
            String vehObserCode, String chargeObserCode, String mopObserCode, String cardObserCode, int recordId, String trackNo, int ticketType) {
        LaneEnList lel = new LaneEnList();
        try {
            entryCard = entryCard == null ? new Card() : entryCard;
            cpuCard = cpuCard == null ? new CpuCard() : cpuCard;
            veh = veh == null ? new Vehicle() : veh;
            lane = lane == null ? Lane.getInstance() : lane;
            staff = staff == null ? new Staff() : staff;
            lel.setCardBoxNo(entryCard.getCartId() == null ? "" : entryCard.getCartId());//卡箱号
            if (entryCard != null) {
                Date ts = entryCard.getDhm();
                if (ts == null) {
                    ts = new Date(System.currentTimeMillis());
                }
                lel.setOpTime(ts);//入口交易时间为写卡时间
                String cardNo = entryCard.getCardSerial();
                if (cardNo == null || cardNo.length() == 20) {
                    cardNo = "";
                }
                lel.setPassCardNo(cardNo);
                Integer vehClass = entryCard.getVehClass() == null ? 0 : IntegerUtils.parseString(entryCard.getVehClass());
                lel.setVehType(vehClass);
            }
            if (lane != null) {
                lel.setLaneType(lane.getLaneType());
                String roadid = lane.getRoadId();
                if (roadid == null) {
                    roadid = "000";
                }
                String stationid = lane.getStationId();
                if (stationid == null) {
                    stationid = "000";
                }
                lel.setRoadId(IntegerUtils.parseString(roadid));
                lel.setStationId(IntegerUtils.parseString(stationid));
                String stationName = lane.getStationName();
                if (stationName == null) {
                    stationName = "";
                }
                lel.setStationName(stationName);
                String laneid = lane.getLaneId();
                laneid = laneid == null ? "000" : laneid;
                lel.setLaneNo(laneid);
                lel.setLaneId(IntegerUtils.parseString(laneid.substring(1)));
                Date temp = lel.getOpTime();
                lel.setListNo(StringUtils.getListNo(roadid, stationid, laneid, temp, recordId));
            }
            if (staff != null) {
                String staffId = staff.getStaffId();
                if (staffId == null) {
                    staffId = "";
                }
                lel.setOperatorNo(staffId);
                String cardNo = staff.getStaffCardSerial();
                if (cardNo == null) {
                    cardNo = "";
                }
                lel.setOpCardNo(cardNo);
                String staffName = staff.getStaffName();
                if (staffName == null) {
                    staffName = "";
                }
                lel.setOperatorName(staffName);
                Date ts = staff.getJobStartTime();
                if (ts == null) {
                    ts = new Date(0);
                }
                lel.setLoginTime(ts);
                Date ts1 = staff.getSquadDate();
                if (ts1 == null) {
                    ts1 = new Date(0);
                }
                lel.setSquadDate(ts1);
                lel.setSquadId(staff.getSquadId());
            }
            lel.setProgramVer(Constant.LANE_SOFT_VERSION);
            lel.setRecordId(recordId);
            lel.setSpare2(vehCount);
//            Integer ticketType;
//            if (entryMop == null) {
//                entryMop = "0";
//            }
//            switch (entryMop) {
//                case "23":
//                    ticketType = 3;
//                    break;
//                case "48":
//                    ticketType = 1;
//                    break;
//                case "51":
//                    ticketType = 2;
//                    break;
//                case "60"://纸券
//                    ticketType = 4;
//                    break;
//                default:
//                    ticketType = 0;
//                    break;
//            }
            lel.setTicketType(ticketType);

            if (veh != null) {
                lel.setPlateAutoColor(transPlateColorCode(veh.getLprPlateColor()));
                String fvph = veh.getFullVehPlateNum();
                if (fvph == null) {
                    fvph = "";
                }
                lel.setVehPlate(fvph);
                String lprFullVehPlateNum = veh.getLprFullVehPlateNum();
                if (lprFullVehPlateNum == null) {
                    lprFullVehPlateNum = "";
                }
                lel.setVehPlateAuto(lprFullVehPlateNum);
                lel.setVehPlateColor(transPlateColorCode(veh.getKeyedPlateColor()));
                lel.setVehTypeAuto(0);
                lel.setVehClass(veh.getVehicleType());
            }
            if (cpuCard != null) {
                String psam = cpuCard.getPsamNum();
                if (psam == null) {
                    psam = "";
                }
                lel.setpSAMCardNo(psam);
                String cpuCardSerial = cpuCard.getCardSerial();
                if (cpuCardSerial == null) {
                    cpuCardSerial = "";
                }
                lel.setxTCardNo(cpuCardSerial);
                String cpuCardType = cpuCard.getCardType();
                Integer cct = cpuCardType == null ? 0 : IntegerUtils.parseString(cpuCardType);
                lel.setxTCardType(cct);
                String userType = cpuCard.getUserType();
                lel.setSpare1(IntegerUtils.parseString(userType == null ? "0" : userType));
            }
            lel.setVehImageNo(lel.getListNo() + "-1");//全景图
            lel.setPlateImageNo(lel.getListNo() + "-2");//车牌图
            lel.setCardSelcetCode(cardObserCode == null ? "" : cardObserCode);//通行卡观察码
            lel.setOpSelcetCode(chargeObserCode == null ? "" : chargeObserCode);//收费操作观察码
            lel.setVehChargeSelcetCode(mopObserCode == null ? "" : mopObserCode);//付款方式观察码
            lel.setVehPassSelcetCode(vehObserCode == null ? "" : vehObserCode);//车辆通行观察码
            lel.setoBUId("0");
            lel.setSpare3("0");
            lel.setSpare4(trackNo == null ? "0" : trackNo);//字轨号
            lel.setTransferTag(0);
            lel.setSpEvent("0");
        } catch (Exception ex) {
            LogControl.logInfo("产生入口交易流水异常:" + lel, ex);
        }
        return lel;
    }

    /**
     * 生成出口交易流水
     *
     * @param trafficEnRoadid 物理入口路段
     * @param trafficEnStationid 物理入口站
     * @param vehObserCode 车辆通行观察码
     * @param chargeObserCode 收费操作观察码
     * @param mopObserCode 付款方式观察码
     * @param cardObserCode 通行卡观察码
     * @param axleGroupStr 轴组集合
     * @param axleGroupPreStr 修改前轴组集合
     * @param preWeight 修改前轴重
     * @param axleWeightDetail 单轴轴重详细信息
     * @param staff 员工号
     * @param lane 车道
     * @param entryCard 入口通行卡
     * @param exitCard 出口通行卡
     * @param veh 车辆
     * @param ticketNum 票据号
     * @param entryCpuCard 入口ETC卡
     * @param exitCpuCard 出口ETC卡
     * @param orderPay 应付金额
     * @param pay 实付金额
     * @param exitMop 出口付款方式
     * @param vehCount 交易号(无上限，满足实际情况下交易号超过9999的情况)
     * @param recordId 交易记录编码，最大为9999，9999之后从1开始（对应交易流水中的交易记录编码）
     * @param trackNo 字轨号
     */
    public void generateExtTransInfo(String trafficEnRoadid, String trafficEnStationid, String vehObserCode, String chargeObserCode, String mopObserCode, String cardObserCode,
            //            String axleGroupStr, 
            String axleGroupPreStr, double preWeight,
            //            String axleWeightDetail,
            Staff staff, Lane lane, Card entryCard, Card exitCard, Vehicle veh, String ticketNum, CpuCard entryCpuCard, CpuCard exitCpuCard, double orderPay, double pay, String exitMop, int vehCount, int recordId, String trackNo, int ticketType, String serviceCardSerial) {
        LaneExList lel = generateExtList(trafficEnRoadid, trafficEnStationid, vehObserCode, chargeObserCode, mopObserCode, cardObserCode,
                //                axleGroupStr, 
                axleGroupPreStr, preWeight,
                //                axleWeightDetail, 
                staff, lane, entryCard, exitCard, veh, ticketNum, entryCpuCard, exitCpuCard, orderPay, pay, exitMop, vehCount, recordId, trackNo, ticketType, serviceCardSerial);
        LogControl.logInfo("产生出口交易流水");
//        try {
        ListControl.generateLaneExList(lel);
//            LaneListUtils.generationExList(lel);
//        } catch (Exception ex) {
//            LogControl.logInfo("产生出口交易流水异常:" + lel, ex);
//        }

    }

    /**
     * 产生入口交易流水
     *
     * @param cpuCard ETC卡
     * @param lane 车道
     * @param staff 员工
     * @param entryCard 通行卡
     * @param veh 车辆
     * @param vehCount 交易号
     * @param entryMop 入口付款方式
     * @param vehObserCode 车辆通行观察码
     * @param chargeObserCode 收费操作观察码
     * @param mopObserCode 付款方式观察码
     * @param cardObserCode 通行卡观察码
     * @param recordId 交易记录编码，最大为9999，9999之后从1开始（对应交易流水中的交易记录编码）
     * @param trackNo 字轨号
     * @param ticketType 通行券类型
     */
    public void generateEntTransInfo(CpuCard cpuCard, Lane lane, Staff staff, Card entryCard, Vehicle veh, int vehCount, String entryMop,
            String vehObserCode, String chargeObserCode, String mopObserCode, String cardObserCode, int recordId, String trackNo, int ticketType) {
        LaneEnList lel = generateEntList(cpuCard, lane, staff, entryCard, veh, vehCount, entryMop, vehObserCode, chargeObserCode, mopObserCode, cardObserCode, recordId, trackNo, ticketType);
        LogControl.logInfo("记录入口交易流水");
//        try {
//            LaneListUtils.generationEnList(lel);
        ListControl.generateLaneEnList(lel);
//        } catch (Exception ex) {
//            LogControl.logInfo("记录入口交易流水异常:" + lel, ex);
//        }
    }

    /**
     * 生成临时出口交易流水
     *
     * @param trafficEnRoadid 物理入口路段
     * @param trafficEnStationid 物理入口站
     * @param vehObserCode 车辆通行观察码
     * @param chargeObserCode 收费操作观察码
     * @param mopObserCode 付款方式观察码
     * @param cardObserCode 通行卡观察码
     * @param axleGroupStr 轴组集合
     * @param axleGroupPreStr 修改前轴组集合
     * @param preWeight 修改前轴重
     * @param axleWeightDetail 单轴轴重详细信息
     * @param staff 员工号
     * @param lane 车道
     * @param entryCard 入口通行卡
     * @param exitCard 出口通行卡
     * @param veh 车辆
     * @param ticketNum 票据号
     * @param entryCpuCard 入口ETC卡
     * @param exitCpuCard 出口ETC卡
     * @param orderPay 应付金额
     * @param pay 实付金额
     * @param exitMop 出口付款方式
     * @param vehCount 交易号(无上限，满足实际情况下交易号超过9999的情况)
     * @param recordId 交易记录编码，最大为9999，9999之后从1开始（对应交易流水中的交易记录编码）
     * @param trackNo 字轨号
     * @param ticketType 通行券类型
     */
    public void generateTempExtTransInfo(String trafficEnRoadid, String trafficEnStationid, String vehObserCode, String chargeObserCode, String mopObserCode, String cardObserCode,
            //            String axleGroupStr, 
            String axleGroupPreStr, double preWeight,
            //            String axleWeightDetail,
            Staff staff, Lane lane, Card entryCard, Card exitCard, Vehicle veh, String ticketNum, CpuCard entryCpuCard, CpuCard exitCpuCard, double orderPay, double pay, String exitMop, int vehCount, int recordId, String trackNo, int ticketType, String serviceCardSerial) {
        LaneExList lel = generateExtList(trafficEnRoadid, trafficEnStationid, vehObserCode, chargeObserCode, mopObserCode, cardObserCode,
                //                axleGroupStr, 
                axleGroupPreStr, preWeight,
                //                axleWeightDetail, 
                staff, lane, entryCard, exitCard, veh, ticketNum, entryCpuCard, exitCpuCard, orderPay, pay, exitMop, vehCount, recordId, trackNo, ticketType, serviceCardSerial);
        LogControl.logInfo("记录出口临时交易流水");
        try {
            TempControl svc = TempControl.getSingleInstance();
            svc.generateTempExList(lel);
        } catch (Exception ex) {
            LogControl.logInfo("记录临时出口交易流水异常:" + lel, ex);
        }
    }

    /**
     * 产生临时入口交易流水
     *
     * @param cpuCard ETC卡
     * @param lane 车道
     * @param staff 员工
     * @param entryCard 通行卡
     * @param veh 车辆
     * @param vehCount 交易号
     * @param entryMop 入口付款方式
     * @param vehObserCode 车辆通行观察码
     * @param chargeObserCode 收费操作观察码
     * @param mopObserCode 付款方式观察码
     * @param cardObserCode 通行卡观察码
     * @param recordId 交易记录编码，最大为9999，9999之后从1开始（对应交易流水中的交易记录编码）
     * @param trackNo 字轨号
     * @param ticketType 通行券类型
     */
    public void generateTempEntTransInfo(CpuCard cpuCard, Lane lane, Staff staff, Card entryCard, Vehicle veh, int vehCount, String entryMop,
            String vehObserCode, String chargeObserCode, String mopObserCode, String cardObserCode, int recordId, String trackNo, int ticketType) {
        LaneEnList lel = generateEntList(cpuCard, lane, staff, entryCard, veh, vehCount, entryMop, vehObserCode, chargeObserCode, mopObserCode, cardObserCode, recordId, trackNo, ticketType);
        LogControl.logInfo("记录入口临时交易流水");
        try {
            TempControl svc = TempControl.getSingleInstance();
            svc.generateTempEnList(lel);
        } catch (Exception ex) {
            LogControl.logInfo("记录临时入口交易流水异常:" + lel, ex);
        }
    }

    /**
     * 产生简单交易信息（出口ETC车辆直接放行时使用）
     *
     * @param entryCard ETC卡
     * @param lane 当前车道
     * @param staff 收费员
     * @param veh 车辆信息
     */
    public void generateSimpleTransInfo(CpuCard entryCard, Lane lane, Staff staff, Vehicle veh) {
        SimpleLaneExList lel = new SimpleLaneExList();
        try {
            LogControl.logInfo("产生简单出口交易流水（出口ETC车辆直接放行时使用）");
            if (staff == null) {
                staff = new Staff();
            }
            if (entryCard == null) {
                entryCard = new CpuCard();
            }
            if (veh == null) {
                veh = new Vehicle();
            }
            Date date = new Date();
            lel.setOpTime(date);//出口流水时间（写卡时间）
            lel.setCardBoxNo("");
            lel.setChargeType(0);
            lel.setComputerMoney(0);//应收金额
            String laneid = entryCard.getLaneId();
            if (laneid == null) {
                laneid = "000";
            }
            lel.setEnLaneNo(laneid);
            lel.setEnLaneId(IntegerUtils.parseString(laneid.substring(1)));
            int laneType = 0;
            String str = laneid.substring(0, 1);
            switch (str.toUpperCase()) {
                case "E":
                    laneType = 1;
                    break;
                case "X":
                    laneType = 2;
                    break;
                default:
                    laneType = 0;
                    break;
            }
            lel.setEnLaneType(laneType);
            lel.setEnOpTime(entryCard.getDhm() == null ? new Date(0) : entryCard.getDhm());
            lel.setEnOperatorNo(entryCard.getStaffId() == null ? "" : entryCard.getStaffId());
            String roadid = entryCard.getRoadid();
            if (roadid == null) {
                roadid = "099";
            }
            String stationid = entryCard.getStationid();
            if (stationid == null) {
                stationid = "096";
            }
            lel.setEnRoadId(IntegerUtils.parseString(roadid));
            lel.setEnStationId(IntegerUtils.parseString(stationid));
            String stationName = getStationName(roadid, stationid);
            lel.setEnStationName(stationName == null ? "" : stationName);
            lel.setSpare3("");//计费入口路段号+收费站号
            lel.setEnVehPlate(entryCard.getFullVehPlateNum() == null ? "" : entryCard.getFullVehPlateNum());
            lel.setEnVehPlateColor(transPlateColorCode(entryCard.getPlateColor()));
            String vehClass = entryCard.getVehClass();
            lel.setEnVehType(IntegerUtils.parseString(vehClass == null ? "0" : vehClass));//入口车型
            lel.setEnVehClass(0);//入口车种,默认为0
            lel.setEnSquadId(0);//入口工班号，无法获取，默认为0
            lel.setEnListNo(StringUtils.getListNo(roadid, stationid, entryCard.getLaneId(), entryCard.getDhm(), 0));
            lel.setListNo(StringUtils.getListNo(lane.getRoadId(), lane.getStationId(), lane.getLaneId(), date, 0));
            String cardSerial = entryCard.getCardSerial();
            if (cardSerial == null || cardSerial.length() != 10) {//ETC卡做通行卡使用时passcardno为空
                lel.setPassCardNo("");
            } else {
                lel.setPassCardNo(cardSerial);
            }
            lel.setGetMoney(0);//实收金额
            lel.setInvoiceId(0);
            laneid = lane.getLaneId();
            if (laneid == null) {
                laneid = "000";
            }
            lel.setLaneNo(laneid);
            lel.setLaneId(IntegerUtils.parseString(laneid.substring(1)));
            lel.setLaneType(lane.getLaneType());
            roadid = lane.getRoadId();
            if (roadid == null) {
                roadid = "000";
            }
            lel.setRoadId(IntegerUtils.parseString(roadid));
            stationid = lane.getStationId();
            if (stationid == null) {
                stationid = "000";
            }
            lel.setStationId(IntegerUtils.parseString(stationid));
            lel.setStationName(lane.getStationName() == null ? "" : lane.getStationName());
            lel.setLoginTime(staff.getJobStartTime() == null ? new Date(0) : staff.getJobStartTime());//出口员工上班时间
            lel.setSquadDate(staff.getSquadDate() == null ? new Date(0) : staff.getSquadDate());//排班日期
            lel.setSquadId(staff.getSquadId());//班次
            lel.setOperatorName(staff.getStaffName() == null ? "" : staff.getStaffName());//出口员工姓名
            lel.setOperatorNo(staff.getStaffId() == null ? "" : staff.getStaffId());//出口员工工号
            lel.setOpCardNo(staff.getStaffCardSerial() == null ? "" : staff.getStaffCardSerial());//员工身份卡序列号
            lel.setAxisGroupNum(veh.getAxleGroupCount());
            lel.setAxisNum(veh.getAxleCount());
            double weight = veh.getWeight();
            double fareWeight = veh.getFareWeight();
            double limitWeight = veh.getLimitWeight();
            lel.setTotalWeight(weight);//检测重量
            lel.setTotalWeightForCharge(fareWeight);//收费重量
            lel.setTotalWeightLimit(limitWeight);//限重
            if (fareWeight > limitWeight) {
                double overWeight = DoubleUtils.sub(fareWeight, limitWeight);
                lel.setOverWeight(overWeight);//超重
                double overRate = DoubleUtils.div(overWeight, limitWeight, 2);
                lel.setOverRatio(overRate);//超重比例
            }
            lel.setPlateAutoColor(transPlateColorCode(veh.getLprPlateColor()));//自动识别车牌颜色
            lel.setVehType(veh.getVehicleClass());//出口车型
            lel.setVehClass(veh.getVehicleType());//出口车种
            lel.setVehPlate(veh.getFullVehPlateNum() == null ? "" : veh.getFullVehPlateNum());
            lel.setVehPlateAuto(veh.getLprFullVehPlateNum() == null ? "" : veh.getLprFullVehPlateNum());
            lel.setVehPlateColor(transPlateColorCode(veh.getKeyedPlateColor()));
            int weightFlag = 0;
            int i = veh.getVehicleClass();
            if (i == 7 || i == 8 || i == 9) {
                weightFlag = 1;
            } else if (i < 7) {
                weightFlag = 0;
            }
            lel.setWeightFlag(weightFlag);
            lel.setTicketType(0);//通行券类型
            lel.setPayCardType(0);//支付卡类型
            lel.setCashMoney(0);//现金金额
            lel.setFreeMoney(0);//免费车金额
            lel.setOfficeMoney(0);//公务车金额
            lel.setUnpayMoney(0);//未付金额
            lel.setGetMoney(0);//实收金额
            lel.setxTCardMoney(0);//ETC卡金额
            lel.setuPCardMoney(0);//银联卡金额
            lel.setpDiscountToll(0);//ETC卡付款优惠前金额
            lel.setxTCardDiscount(0.0);//ETC卡折扣率
            lel.setxTCardBalanceBf(0L);//ETC卡扣款前余额
            lel.setxTCardBalanceAf(0L);//ETC卡扣款后余额
            lel.seteTCTac(entryCard.getTacCode());//TAC码
            lel.seteTCTermCode(entryCard.getTermCode() == null ? "0" : entryCard.getTermCode());
            lel.seteTCTermTradNo(entryCard.getTermTradNo() == null ? "0" : entryCard.getTermTradNo());
            lel.seteTCTradeNo(entryCard.getTradeNo() == null ? "0" : entryCard.getTradeNo());
            lel.setpSAMCardNo(entryCard.getPsamNum() == null ? "0" : entryCard.getPsamNum());
            lel.setxTCardNo(entryCard.getCardSerial() == null ? "" : entryCard.getCardSerial());//ETC卡内部编号
            String cardType = entryCard.getCardType();
            if (cardType == null) {
                cardType = "00";
            }
            lel.setxTCardType(IntegerUtils.parseString(cardType));//ETC卡类型  
            str = entryCard.getUserType();
            str = str == null ? "0" : str;
            lel.setSpare1(IntegerUtils.parseString(str));//ETC卡用户类型
            lel.setCardSelcetCode("");//通行卡观察码
            lel.setOpSelcetCode("");//收费操作观察码
            lel.setVehChargeSelcetCode("");//付款方式观察码
            lel.setVehPassSelcetCode("");//车辆通行观察码
            lel.setAxisType("");//轴组类型集合
            lel.setPreAxisType("");//修改前轴组类型集合
            lel.setAxisWeightDetail("");//单轴轴重集合
            lel.setuPCardBalanceBf(0L);//银联卡扣款前余额
            lel.setuPCardBalanceAf(0L);//银联卡扣款后余额
            lel.setuPCardDiscount(0.0);//银联卡折扣率
            lel.setuPCardNo("");//银联卡卡号
            lel.setuPCardType(0);//银联卡类型
            lel.setPreTotalWeight(0.0);//修改前总轴重
            lel.setPreVehMoney(0);//降档前金额
            lel.setPriceVer(0);//费率版本号
            lel.setProgramVer(Constant.LANE_SOFT_VERSION);
            lel.setSpEvent("");//特殊事件
            lel.setUnpayMoney(0);//未付金额
            lel.setSpare2(0);
            lel.setSpare4("");
            lel.setOfficeMoney(0);//公务车金额
            lel.setOfficialCardNo("");//公务卡卡号
            lel.setFreeRoadIdSer("");//免费路段编码序列
            lel.setSectionCharge("");
            lel.setSectionPath("");
            lel.setVehImageNo("");//全景图
            lel.setPlateImageNo("");//车牌图
            lel.setVerifyCode(0);//校验码
            lel.setRecordId(0);
            lel.setSpare2(0);
            ListControl.generateLaneSimpleLaneExList(lel);
//            LaneListUtils.generationSimpleExList(lel);
        } catch (Throwable t) {
            LogControl.logInfo("产生简单交易信息时异常:" + lel, t);
        }
    }

    /**
     * 将字符串代表的颜色代码转换为数字 蓝色 U 1; 黄色 Y 2; 白色 W 3;绿色 G 4;红色 R 5; 黑色 B 6;无车牌颜色 空 0
     *
     * @param plateColor 车牌颜色
     * @return 车牌颜色对应数字
     */
    public Integer transPlateColorCode(String plateColor) {
        Integer pc;
        if (plateColor == null) {
            pc = 0;
        } else {
            switch (plateColor) {
                case "U":
                    pc = 1;
                    break;
                case "Y":
                    pc = 2;
                    break;
                case "W":
                    pc = 3;
                    break;
                case "G":
                    pc = 4;
                    break;
                case "R":
                    pc = 5;
                    break;
                case "B":
                    pc = 6;
                    break;
                case "A":
                    pc = 0;
                    break;
                case "":
                    pc = 0;
                    break;
                default:
                    pc = 0;
                    break;
            }
        }
        return pc;
    }

    /**
     * 获取车牌颜色对应的中文
     *
     * @param plateColor 蓝色 U;黄色 Y;白色 W;绿色 G;红色 R;黑色 B;无车牌颜色 空
     * @return 车牌颜色对应中文
     */
    public String getZHPlateColor(String plateColor) {
        String pc;
        if (plateColor == null) {
            pc = "";
        } else {
            switch (plateColor.trim()) {
                case "U":
                    pc = "蓝";
                    break;
                case "Y":
                    pc = "黄";
                    break;
                case "W":
                    pc = "白";
                    break;
                case "G":
                    pc = "绿";
                    break;
                case "R":
                    pc = "红";
                    break;
                case "B":
                    pc = "黑";
                    break;
                case "A":
                    pc = "";
                    break;
                case "":
                    pc = "";
                    break;
                default:
                    pc = "";
                    break;
            }
        }
        return pc;
    }

    /**
     * 获取车型对应的中文
     *
     * @param vehicleClass
     * @return 车型对应中文
     */
    public String getZHVehicleClass(Integer vehicleClass) {
        String pc;
        if (vehicleClass == null) {
            pc = "";
        } else {
            switch (vehicleClass) {
                case 1:
                    pc = "一型车";
                    break;
                case 2:
                    pc = "二型车";
                    break;
                case 3:
                    pc = "三型车";
                    break;
                case 4:
                    pc = "四型车";
                    break;
                case 5:
                    pc = "五型车";
                    break;
                case 7:
                    pc = "七型车";
                    break;
                case 8:
                    pc = "八型车";
                    break;
                case 9:
                    pc = "九型车";
                    break;
                default:
                    pc = "";
                    break;
            }
        }
        return pc;
    }

    /**
     * 特殊车辆标记对应的中文
     *
     * @param speVehFlag 特殊车辆标记0 黑名单 1灰名单 9白名单 2 正常车辆
     * @return 特殊车辆标记对应中文
     */
    public String getZHSpeVehFlag(Integer speVehFlag) {
        String pc;
        if (speVehFlag == null) {
            pc = "正常车辆";
        } else {
            switch (speVehFlag) {
                case 0:
                    pc = "黑名单";
                    break;
                case 1:
                    pc = "灰名单";
                    break;
                case 2:
                    pc = "正常车辆";
                    break;
                case 9:
                    pc = "白名单";
                    break;
                default:
                    pc = "正常车辆";
                    break;
            }
        }
        return pc;
    }

    /**
     * 将入口图片转移到img文件夹下并按照给定流水号重新命名
     *
     * @param listNum 流水号
     */
    public void createEntryImg(final String listNum) {
        if (imgList == null) {
            return;
        }
        for (int i = 0; i < imgList.size(); i++) {
            String tempFilePath = imgList.get(i);
            try {
                File tempFile = new File(tempFilePath);
                if (!tempFile.exists() || !tempFile.isFile()) {
                    LogControl.logInfo("生成入口图片时发现入口图片临时文件" + tempFilePath + "不存在");
                }
                File destFile = new File("img/" + listNum + "-" + (i + 3) + ".jpg");
                FileUtils.copyFile(tempFile, destFile);//将图片文件从临时文件复制到图片文件夹
                tempFile.delete();//删除临时文件
            } catch (IOException ex) {
                LogControl.logInfo("将临时文件夹下的" + tempFilePath + "复制到img文件夹时出现异常", ex);
            }
        }
        imgList = new ArrayList();//重置临时图片缓存
    }

    /**
     * 根据通行卡观察码获取其对应的虚拟收费站,若不符合跳站规则，返回其物理收费站
     *
     * @param code 通行卡观察码
     * @param station 卡内记录入口站，如4911
     * @return 根据通行卡观察吗判断的入口站，如9997
     */
    private String getVirtualStation(String code, String station) {
        if (code == null) {
            return null;
        }
        switch (code) {
            case "1"://卡状态异常
                return "9997";
            case "5"://车型车牌不符
                return station;
            case "6"://U型车两小时之内
                return "9998";
            default:
                return station;
        }
    }

    /**
     * is not null judge
     *
     * @param str
     * @return
     */
    private String isNull(String str) {
        return ((str == null) ? "" : str);
    }

}
