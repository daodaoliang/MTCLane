package com.hgits.control;

import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.StringUtils;
import com.hgits.util.rate.ParamCacheQuery;
import com.hgits.vo.Card;
import com.hgits.vo.Constant;
import com.hgits.vo.OfficialCard;
import com.hgits.vo.ServiceCard;
import java.util.Date;
import org.apache.log4j.Logger;
import ui.ExtJFrame;

/**
 * 公务卡控制
 *
 * @author Wang Guodong
 */
public class ServiceCardControl {

    private static ServiceCardControl instance;//唯一实例化对象
    private ExtJFrame extJFrame;
    private final String DefaultServiceStation = "7012,7013,7014,7015";//默认的允许使用公务卡的收费站
    private M1Control m1Control;
    private Logger logger = Logger.getLogger(ServiceCardControl.class.getName());

    private ServiceCardControl() {

    }

    public void setExtJFrame(ExtJFrame extJFrame) {
        this.extJFrame = extJFrame;
    }

    public void setM1Control(M1Control m1Control) {
        this.m1Control = m1Control;
    }

    /**
     * 获取唯一实例化对象
     *
     * @return 唯一实例化对象
     */
    public static synchronized ServiceCardControl getSingleInstance() {
        if (instance == null) {
            instance = new ServiceCardControl();
        }
        return instance;
    }

    /**
     * 判断公务卡是否符合使用要求
     *
     * @param serviceid 公务卡编号（6位）
     * @param enRoadid 收费入口路段
     * @param enStationid 收费入口站
     * @param exRoadid 出口路段
     * @param exStationid 出口站
     * @param plateNum 当前车牌号码
     * @param servicePlateNum 公务卡内记录车牌号码后三位
     * @return true/false
     */
    private boolean checkServiceCard(String serviceid, String trafficEnRoadid, String trafficEnStationid, String enRoadid, String enStationid, String exRoadid, String exStationid, String plateNum, String servicePlateNum) {
        boolean flag = false;
        int i = checkServiceParam(serviceid);
        if (i == 1) {
            extJFrame.updateInfo("季票卡异常", "公务编号不存在\r\n请刷其他季票卡或现金收费");
            LogControl.logInfo("公务卡编号" + serviceid + "不存在");
            return flag;
        } else if (i == 2) {
            extJFrame.updateInfo("季票卡异常", "季票卡已过期\r\n请刷其他季票卡或现金收费");
            LogControl.logInfo("公务卡已过期");
            return flag;
        } else if (i != 0) {
            extJFrame.updateInfo("未知季票卡异常", "未知季票卡异常");
            LogControl.logInfo("未知公务卡异常" + i);
        }
        flag = checkServiceEnEx(trafficEnRoadid, trafficEnStationid, enRoadid, enStationid, exRoadid, exStationid);
        if (!flag) {
            extJFrame.updateInfo("季票卡异常", "出入口站异常\r\n请核实出入口站是否有误");
            LogControl.logInfo("公务卡物理入口" + trafficEnRoadid + trafficEnStationid + "收费入口" + enRoadid + enStationid + "及出口站" + exRoadid + exStationid + "不在使用公务卡收费站范围之内");
            return flag;
        }

        flag = checkServicePlateNum(plateNum, servicePlateNum);
        if (!flag) {
            extJFrame.updateInfo("季票卡异常", "车牌信息与季票卡内记录的信息不一致\r\n请核实车牌信息是否有误");
            LogControl.logInfo("车牌信息"+plateNum+"与公务卡内记录的车牌"+servicePlateNum+"不一致");
            return flag;
        }
        return flag;
    }

    /**
     * 判断公务卡编号是否在参数中以及是否过期
     *
     * @param serviceid 公务卡编号（6位）
     * @return 0-正常 1-公务卡编号不存在 2 公务卡编号过期
     */
    private int checkServiceParam(String serviceid) {
        //TODO 完成公务卡编号是否在参数表中以及公务卡是否过期的验证
        OfficialCard oc = ParamCacheQuery.queryOfficialCard(serviceid);
        LogControl.logInfo("公务卡" + serviceid + "在参数表中的记录：" + oc);
        if (oc == null) {//公务卡编号不存在
            return 1;
        }
        Date endTime = oc.getEndTime();
        Date date = new Date();
        if (date.after(endTime)) {//公务卡编号过期
            return 2;
        }

        return 0;
    }

    /**
     * 判断公务卡的出入口是否符合要求
     *
     * @param enRoadid 收费入口路段
     * @param enStationid 收费入口收费站
     * @param exRoadid 出口路段
     * @param exStationid 出口站
     * @return true/false
     */
    private boolean checkServiceEnEx(String trafficEnRoadid, String trafficEnStationid, String enRoadid, String enStationid, String exRoadid, String exStationid) {
        String str1 = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "serviceCardEntry", DefaultServiceStation);
        String str2 = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "serviceCardExit", DefaultServiceStation);
        String en = enRoadid + enStationid;
        String trafficEn = trafficEnRoadid + trafficEnStationid;
        String ex = exRoadid + exStationid;
        //兼容配置中可能存在的空格，中文逗号的问题
        str1 = str1.replaceAll("，", ",");
        str1 = str1.replaceAll(" ", "");
        str2 = str2.replaceAll("，", ",");
        str2 = str2.replaceAll(" ", "");
        String[] buffer1 = StringUtils.splitPreserveAllTokens(str1, ",");
        String[] buffer2 = StringUtils.splitPreserveAllTokens(str2, ",");
        boolean flag1 = false;
        boolean flag2 = false;
        for (String str : buffer1) {
            if (str.equals(en) || str.equals(trafficEn)) {//物理入口，收费入口任意一个满足要求即可
                flag1 = true;
                break;
            }
        }
        for (String str : buffer2) {
            if (str.equals(ex)) {//出口符合要求
                flag2 = true;
                break;
            }
        }
        return flag1 && flag2;
    }

    /**
     * 检测公务卡内记录车牌是否符合要求
     *
     * @param plateNum 当前全车牌
     * @param servicePlateNum 公务卡内记录车牌
     * @return true/false
     */
    private boolean checkServicePlateNum(String plateNum, String servicePlateNum) {
        if (plateNum == null || servicePlateNum == null) {
            return false;
        }
        String str = plateNum.replaceAll("\\D", "");//除去车牌号码中的非数字
        while (str.length() < 3) {
            str = "." + str;//不足三位，补足三位
        }
        str = str.substring(str.length() - 3);//获取车牌后三位数字
        boolean flag = str.equals(servicePlateNum);//检验公务卡内车牌后三位
        return flag;
    }

    /**
     * 运行公务卡付费流程
     *
     * @param trafficEnRoadid 物理入口路段
     * @param trafficEnStationid 物理入口站
     * @param fareEnRoadid 收费入口路段
     * @param fareEnStationid 收费入口站
     * @param exRoadid 出口路段
     * @param exStationid 出口站
     * @param plateNum 车牌号码
     * @return 公务卡付款成功，返回公务卡信息，否则返回null
     */
    public ServiceCard runServiceCard(String trafficEnRoadid, String trafficEnStationid, String fareEnRoadid, String fareEnStationid, String exRoadid, String exStationid, String plateNum) {
        if (!FunctionControl.isServiceCardFunActive()) {//未启用季票卡功能
            return null;
        }
        Card card;
        String serial = null;
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.error(ex, ex);
            }
            card = m1Control.getReadedCard();//读卡
            if (card == null) {//无卡
                return null;
            } else if (!card.isServiceCard()) {//非公务卡
                return null;
            } else {
            }
            String id = card.getId();//获取公务卡编号
            String serviceId = StringUtils.encodeID(id);//获取加密后的公务卡编号
            String servicePlateNum = card.getVehPlateNum();
            String serviceCardSerial = card.getCardSerial();
            if (serviceCardSerial != null && serviceCardSerial.equals(serial)) {//已处理过的公务卡，不再做第二次处理
                continue;
            }
            LogControl.logInfo("发现公务卡：" + card);
            serial = serviceCardSerial;
            //判断公务卡能否使用
            boolean flag = checkServiceCard(serviceId, trafficEnRoadid, trafficEnStationid, fareEnRoadid, fareEnStationid, exRoadid, exStationid, plateNum, servicePlateNum);
            if (flag) {//公务卡能够使用
                extJFrame.updatePayInfo("    编号:" + serviceId);
                extJFrame.updateAttachInfo("");
                extJFrame.updateFare(String.valueOf(0));
                extJFrame.updateMop("39");
                ServiceCard svcCard = new ServiceCard();
                svcCard.setCardSerial(serviceCardSerial);
                svcCard.setId(serviceId);
                return svcCard;
            }
        }

    }
}
