/**
 *
 */
package com.hgits.service.constant;

/**
 * 参数相关的常量
 *
 * @author wh
 * @date 2014-08-14
 *
 */
public interface ParamConstant extends BaseConstant {

    /**
     * Recv后缀
     */
    public static final String PARAM_RECV = "_Recv";

    /**
     * 读取国家或省级高速公路编码表
     */
    public static final String PARAM_NATIONAL_ROAD = "tb_nationalRoad";

    /**
     * 读取区域管理处编码表
     */
    public static final String PARAM_AREA = "tb_area";

    /**
     * 读取路段编码表
     */
    public static final String PARAM_ROAD = "tb_road";

    /**
     * 路段分区编码表
     */
    public static final String PARAM_ROADSUBSECTION = "tb_roadsubsection";

    /**
     * 收费站编码表
     */
    public static final String PARAM_STATION = "tb_station";

    /**
     * 车道编码表
     */
    public static final String PARAM_LANE = "tb_lane";

    /**
     * 车道类型编码表
     */
    public static final String PARAM_LANETYPE = "tb_lanetype";

    /**
     * 车牌颜色编码表
     */
    public static final String PARAM_VEHPLATECOLOUR = "tb_vehplatecolour";

    /**
     * 车型编码表
     */
    public static final String PARAM_VEHTYPE = "tb_vehtype";

    /**
     * 车种编码表
     */
    public static final String PARAM_VEHCLASS = "tb_vehclass";

    /**
     * 特殊事件编码表
     */
    public static final String PARAM_SPEVENT = "tb_spevent";

    /**
     * 操作员表
     */
    public static final String PARAM_OPERATOR = "tb_operator";

    /**
     * 权限表
     */
    public static final String PARAM_ROLE = "tb_role";

    /**
     * 操作员角色分配表
     */
    public static final String PARAM_OPERATORROLE = "tb_operatorrole";

    /**
     * 工班编码表
     */
    public static final String PARAM_SQUAD = "tb_squad";

    /**
     * 路段收费标准表
     */
    public static final String PARAM_ROADCHARGESTD = "tb_roadchargestd";

    /**
     * 费率表
     */
    public static final String PARAM_PRICE = "tb_price";

    /**
     * 桥隧叠加收费标准表
     */
    public static final String PARAM_BRIDGE_EXTRA_CHARGE_STD = "tb_bridgeextrachargestd";

    /**
     * 黑白灰名单
     */
    public static final String PARAM_BWG_LIST = "tb_vehplatebwg";

    /**
     * *********************************新增的表 ***********************************
     */
    /**
     * 通行卡黑名单
     */
    public static final String PARAM_PASSCARD_BLACK_LIST = "tb_passcardblacklist";

    /**
     * 公务卡黑名单
     */
    public static final String PARAM_OFFICIALCARD_BLACK_LIST = "tb_officialcardblacklist";

    /**
     * 公务卡
     */
    public static final String PARAM_OFFICIALCARD = "tb_officialcard";

    /**
     * ETC卡黑名单表
     */
//    public static final String PARAM_XTCARD_BLACK_LIST = "tb_xtcardblacklist";
    
    /**
     * ETC卡黑名单增量基础表
     */
    public static final String PARAM_XTCARD_BLACK_BASE_LIST = "tb_xtcardblackbaselist";

    /**
     * ETC卡黑名单增量
     */
    public static final String PARAM_XTCARD_BLACK_DELTA_LIST = "tb_xtcardblackdeltalist";

    /**
     * 通行卡发行方名单
     */
    public static final String PARAM_PASSCARD_ISSUER = "tb_passcardissuer";

    /**
     * 发行方
     */
    public static final String PARAM_ISSUER = "tb_issuer";

    /**
     * 用户折扣表
     */
    public static final String PARAM_USERRATE = "tb_userrate";

    /**
     * 授权职员表
     */
    public static final String PARAM_AUTHORIZE_OPERATOR = "tb_authorizeoperator";

    /**
     * 桥隧编码表
     */
    public static final String PARAM_BRIDGE = "v_bridge";

    /**
     * 货车超载收费标准表
     */
    public static final String PARAM_OVERLOADPRICE = "tb_overloadprice";

    /**
     * 行程时间表
     */
    public static final String PARAM_TRAVELTIME = "tb_traveltime";

    /**
     * 轴类型表
     */
    public static final String PARAM_AXISTYPE = "tb_axistype";

    /**
     * 车货轴型总限重表
     */
    public static final String PARAM_TRUCKTOALLLIMIT = "tb_trucktoalllimit";

    /**
     * 费率表
     */
    public static final String PARAM_VEHROUTE = "tb_VehRoute";
    
    /**
     * 优惠信息表
     */
    public static final String PARAM_TBL_DISCOUNTINFO = "tbl_DiscountInfo";
    
    /**
     * 密钥信息表
     */
    public static final String PARAM_ENCRYPTIONKEY = "tb_EncryptionKey";

    public static final String PARAM_BLACK_CARDNO = "cardNo";
    public static final String PARAM_BLACK_GENCAU = "genCau";
    public static final String PARAM_BLACK_VALIDFLAG = "validFlag";
    public static final String PARAM_BLACK_VERSION = "version";
    public static final String PARAM_BLACK_STARTTIME = "startTime";
    
    /**
     * 行程时间属性长度
     */
    public static final int TRAVELTIME_LENGTH = 10;

    /**
     * 桥隧叠加收费标准属性长度
     */
    public static final int BRIDGEEXTRACHARGESTD_LENGTH = 22;

    /**
     * 路段叠加收费标准属性长度
     */
    public static final int ROADCHARGESTD_LENGTH = 21;

    /**
     * 收费站属性长度
     */
    public static final int STATION_LENGTH = 19;

    /**
     * 路段属性长度
     */
    public static final int ROAD_LENGTH = 12;

    /**
     * 费率表属性长度
     */
    public static final int PRICE_LENGTH = 20;
    
    /**
     * 发行方属性长度
     */
    public static final int ISSUER_LENGTH = 12;
    
    /**
     * 用户折扣率属性长度
     */
    public static final int USERRATE_LENGTH = 7;
    
    /**
     * 货车超载率属性长度
     */
    public static final int OVER_LOAD_PRICE_LENGTH = 8;
    
    /**
     * 优惠信息属性长度
     */
    public static final int DISCOUNTINFO_LENGTH = 13;
    
    /**
     * 密钥信息属性长度
     */
    public static final int ENCRYPTIONKEY_LENGTH = 8;
    
    /**
     * 公务卡属性长度
     */
    public static final int OFFICIALCARD_LENGTH = 10;
    
    /**
     * 
     */
    public static final String RATESIZE_NOT_MATCH = "RateSize";
    
    /**
     * 黑名单全量表类型
     */
    public static final Integer ALL_CARD_BLACK_TYPE = 1;
    
    /**
     * 黑名单增量表类型
     */
    public static final Integer DELTA_CARD_BLACK_TYPE = 2;
    
}
