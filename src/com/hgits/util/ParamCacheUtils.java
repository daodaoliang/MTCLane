package com.hgits.util;

import java.util.HashMap;
import java.util.Map;

import com.hgits.vo.Area;
import com.hgits.vo.NationalRoad;

/**
 * 参数缓存控制类
 *
 * @author wh
 *
 */
public class ParamCacheUtils {

    /**
     * 国家或省级高速公路编号 如 G0401 长沙绕城高速 状态:0-启用,1-未启用
     */
    public static Map<String, NationalRoad> nationalRoadMap = new HashMap<String, NationalRoad>();
    /**
     * 区域管理处编码 根据免费位置可以知道，免费对应的是哪个路段，路段编码连续时，可用路段编码作为免费位置
     */
    public static Map<String, Area> areaMap = new HashMap<String, Area>();
    /**
     * 路段编码
     */
    public static Map<String, Object> roadMap = new HashMap<String, Object>();
    /**
     * 收费站编码
     */
    public static Map<String, Object> stationMap = new HashMap<String, Object>();
    /**
     * 车道编码
     */
    public static Map<String, Object> laneMap = new HashMap<String, Object>();
    /**
     * 车道类型编码<br/> 1、封闭式MTC入口<br/> 2、封闭式MTC出口<br/> 3、封闭式ETC入口<br/>
     * 4、封闭式ETC出口<br/> 5、开放式MTC<br/> 6、开放式ETC<br/> 7、MTC标识车道<br/> 8、ETC标识车道<br/>
     * 9、自助刷卡入口<br/> 10、自助刷标识车道<br/> 11、便携机入口<br/> 12、便携机出口<br/> 13、ETC手持机
     */
    public static Map<String, Object> laneTypeMap = new HashMap<String, Object>();
    /**
     * 车牌颜色编码 1、白色<br/> 2、黄色<br/> 3、黑色<br/> 4、白色 <br/> 5、蓝白渐变色
     */
    public static Map<String, Object> vehPlateColourMap = new HashMap<String, Object>();
    /**
     * 车型编码 1、一型车<br/> 2、二型车<br/> 3、三型车<br/> 4、四型车<br/> 5、五型车<br/> 6、六型车<br/>
     * 7、货车<br/> 8、省内绿通车<br/> 9、省外绿通车
     */
    public static Map<String, Object> vehTypeMap = new HashMap<String, Object>();
    /**
     * 车种编码 公务车 军警车 车队 紧急车 ETC卡车 等
     */
    public static Map<String, Object> vehClassMap = new HashMap<String, Object>();
    /**
     * 特殊事件编码
     */
    public static Map<String, Object> spEventMap = new HashMap<String, Object>();
    /**
     * 操作员
     */
    public static Map<String, Object> operatorMap = new HashMap<String, Object>();
    /**
     * 角色表 1、发卡员<br/> 2、收费员<br/> 3、车道班长<br/> 4、车道稽查<br/> 5、车到维护<br/>
     * 6、站级监控员<br/> 7、站级清点员<br/>
     */
    public static Map<String, Object> roleMap = new HashMap<String, Object>();
    /**
     * 操作员角色分配
     */
    public static Map<String, Object> operatorRoleMap = new HashMap<String, Object>();
    /**
     * 工班编码
     */
    public static Map<String, Object> squadMap = new HashMap<String, Object>();
    /**
     * 费率表存储
     */
    public static Map<String, Object> priceMap = new HashMap<String, Object>();
    /**
     * 桥隧费额
     */
    public static Map<String, Object> bridgeFareMap = new HashMap<String, Object>();
    /**
     * 黑白灰名单 1黑名单，2白名单，3灰名单
     */
    public static Map<String, Integer> bwgListMap = new HashMap<String, Integer>();

    public static void setNationalRoadMap(Map<String, NationalRoad> nationalRoadMap) {
        ParamCacheUtils.nationalRoadMap = nationalRoadMap;
    }

    public static void setAreaMap(Map<String, Area> areaMap) {
        ParamCacheUtils.areaMap = areaMap;
    }

    public static void setRoadMap(Map<String, Object> roadMap) {
        ParamCacheUtils.roadMap = roadMap;
    }

    public static void setStationMap(Map<String, Object> stationMap) {
        ParamCacheUtils.stationMap = stationMap;
    }

    public static void setLaneMap(Map<String, Object> laneMap) {
        ParamCacheUtils.laneMap = laneMap;
    }

    public static void setLaneTypeMap(Map<String, Object> laneTypeMap) {
        ParamCacheUtils.laneTypeMap = laneTypeMap;
    }

    public static void setVehPlateColourMap(Map<String, Object> vehPlateColourMap) {
        ParamCacheUtils.vehPlateColourMap = vehPlateColourMap;
    }

    public static void setVehTypeMap(Map<String, Object> vehTypeMap) {
        ParamCacheUtils.vehTypeMap = vehTypeMap;
    }

    public static void setVehClassMap(Map<String, Object> vehClassMap) {
        ParamCacheUtils.vehClassMap = vehClassMap;
    }

    public static void setSpEventMap(Map<String, Object> spEventMap) {
        ParamCacheUtils.spEventMap = spEventMap;
    }

    public static void setOperatorMap(Map<String, Object> operatorMap) {
        ParamCacheUtils.operatorMap = operatorMap;
    }

    public static void setRoleMap(Map<String, Object> roleMap) {
        ParamCacheUtils.roleMap = roleMap;
    }

    public static void setOperatorRoleMap(Map<String, Object> operatorRoleMap) {
        ParamCacheUtils.operatorRoleMap = operatorRoleMap;
    }

    public static void setSquadMap(Map<String, Object> squadMap) {
        ParamCacheUtils.squadMap = squadMap;
    }

    public static void setPriceMap(Map<String, Object> priceMap) {
        ParamCacheUtils.priceMap = priceMap;
    }

    public static void setBridgeFareMap(Map<String, Object> bridgeFareMap) {
        ParamCacheUtils.bridgeFareMap = bridgeFareMap;
    }

    public static void setBwgListMap(Map<String, Integer> bwgListMap) {
        ParamCacheUtils.bwgListMap = bwgListMap;
    }
}
