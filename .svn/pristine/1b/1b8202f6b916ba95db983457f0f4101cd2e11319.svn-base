package com.hgits.util;

import com.hgits.service.HiService;
import com.hgits.util.rate.ParamCacheQuery;
import com.hgits.vo.Card;
import com.hgits.vo.Lane;
import com.hgits.vo.Operator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 检验工具类
 *
 * @author WangGuodong
 */
public class CheckUtils {

    /**
     * 记录上下级关系的集合,key 父类 value所有子类集合
     */
    static Map<String, List<String>> map = new ConcurrentHashMap<String, List<String>>();

    static {
        HiService hiSvc = new HiService();
        map = hiSvc.loadHiFile();
    }

    /**
     * 核对该卡片是否与车道相对应
     *
     * @param card 卡片
     * @param lane 车道
     * @return true 一致 false 不一致
     */
    public static boolean checkCardForStation(Card card, Lane lane) {
        boolean flag = false;
        if (card == null || lane == null) {
            flag = false;
        } else if (card.getRoadid().equals(lane.getRoadId()) && card.getStationid().equals(lane.getStationId())) {
            flag = true;
        } else if (card.getStationid().equals(lane.getStationId())) {
            flag = true;
        }
        return flag;
    }

    /**
     * 核对该卡片授权站在当前收费站是否有操作权限
     *
     * @param card 卡片
     * @param lane 车道
     * @return true 一致 false 不一致
     */
    public static boolean checkAuthStation(Card card, Lane lane) {
        if (card == null || lane == null) {
            return false;
        }
        String authStation = card.getRoadid() + card.getStationid();
        String currStation = lane.getRoadId() + lane.getStationId();
        if (authStation.equalsIgnoreCase(currStation)) {
            return true;
        }
        if (map != null) {
            List<String> authList = map.get(authStation);//获取授权站的子站
            if(authList==null){
                return false;
            }
            if (authList.contains(currStation)) {//当前站为授权站的子站
                return true;
            }
        }
        return false;
    }

    /**
     * 检测该收费员在本站是否有操作权限
     *
     * @param staffId 员工号
     * @param roadid 路段号
     * @param stationid 收费站号
     * @return 0正常 1 参数表无此员工号 2 参数表有此员工，但该员工本站未授权
     */
    public static int checkStaff(String staffId, String roadid, String stationid) {
        if (staffId == null) {
            return 1;
        }
        Operator op = ParamCacheQuery.queryOperator(staffId);
        if (op == null) {
            return 1;
        } else {
            String str = op.getAuthorizeStationSerial();
            if (str == null) {
                return 2;
            } else if (str.contains(roadid + stationid)) {
                return 0;
            } else {
                return 2;
            }
        }
    }

    /**
     * 检测卡箱标签卡和身份卡是否对应
     *
     * @param cart 卡箱标签卡
     * @param idCard 身份卡
     * @return true/false
     */
    public static boolean checkCartForStaff(Card cart, Card idCard) {
        boolean flag = false;
        if (cart == null || idCard == null) {
            flag = false;
        } else if (cart.getStaffId().equals(StringUtils.encodeID(idCard.getId()))) {
            flag = true;
        }
        return flag;
    }
}
