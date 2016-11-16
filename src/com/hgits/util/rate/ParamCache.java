package com.hgits.util.rate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.hgits.vo.BridgeExtraChargeStd;
import com.hgits.vo.DiscountInfo;
import com.hgits.vo.EncryptionKey;
import com.hgits.vo.Issuer;
import com.hgits.vo.LaneParaVer;
import com.hgits.vo.LaneParam;
import com.hgits.vo.OfficialCard;
import com.hgits.vo.Operator;
import com.hgits.vo.OverLoadPrice;
import com.hgits.vo.Price;
import com.hgits.vo.Road;
import com.hgits.vo.RoadChargeStd;
import com.hgits.vo.Station;
import com.hgits.vo.TravelTime;
import com.hgits.vo.UserRate;
import com.hgits.vo.VehPlateBWG;
import com.hgits.vo.Version;

/**
 * 参数缓存控制类
 *
 * @author wh
 *
 */
/**
 * @author Administrator
 *
 */
public class ParamCache {

    /**
     * 路段编码
     */
    public static Vector<Road> roadMap = new Vector<Road>();


    /**
     * 收费站编码
     */
    public static Vector<Station> stationMap = new Vector<Station>();

    /**
     * 车道编码
     */
    public static Vector<LaneParam> laneMap = new Vector<LaneParam>();

    /**
     * 操作员
     */
    public static Vector<Operator> operatorMap = new Vector<Operator>();

    /**
     * 路段收费标准
     */
    public static Vector<RoadChargeStd> roadChargeStdMap = new Vector<RoadChargeStd>();

    /**
     * 费率表存储
     */
    public static Map<Integer, Vector<Price>> validPriceMap = new HashMap<Integer, Vector<Price>>();

    /**
     * 费率表存储
     */
    public static Map<Integer, Map<Integer, Vector<Price>>> priceMap = new HashMap<Integer, Map<Integer, Vector<Price>>>();

    /**
     * 桥隧叠加收费标准
     */
    public static Vector<BridgeExtraChargeStd> bridgeExtraChargeStdMap = new Vector<BridgeExtraChargeStd>();

    /**
     * 黑白灰名单 1黑名单，2白名单，3灰名单
     */
    public static Vector<VehPlateBWG> bwgListMap = new Vector<VehPlateBWG>();

    /**
     * ETC卡黑名单增量
     */
    public static Map<String, List<byte[]>> XTCardBlackDeltaList = new HashMap<String, List<byte[]>>();

    /**
     * 发行方名单
     */
    public static Vector<Issuer> issuerMap = new Vector<Issuer>();

    /**
     * 用户折扣表
     */
    public static Vector<UserRate> userRateMap = new Vector<UserRate>();

    /**
     * 货车超载收费标准表
     */
    public static OverLoadPrice overLoadPrices = new OverLoadPrice();

    /**
     * 行程时间表
     */
    public static List<TravelTime> travelTimes = new ArrayList<TravelTime>();

    /**
     * 参数文件当前版本
     */
    public static Map<String, Version> currentVersionMap = new HashMap<>();
    
    /**
     * 参数文件未来版本
     */
    public static Map<String, Version> futureVersionMap = new HashMap<>();
    
    /**
     * 费率参数文件版本
     */
    public static Map<String, Version> rateVersionMap = new HashMap<>();
    
    /**
     * 显示参数文件当前版本
     */
    public static Map<String, Version>displayVersionMap = new HashMap<>();
    
    /**
     * 优惠信息表
     */
    public static Vector<DiscountInfo> discountInfos = new Vector<DiscountInfo>();

    /**
     * 密钥信息表
     */
    public static Vector<EncryptionKey> encryptionKeys = new Vector<EncryptionKey>();
    
    /**
     * 参数版本信息
     */
    public static Vector<LaneParaVer> runLaneParaVers = new Vector<LaneParaVer>();
    
    /**
     * 费率版本
     */
    public static String fareVersion;
    
    /**
     * 公务卡表
     */
    public static Vector<OfficialCard> officialCardMap = new Vector<OfficialCard>();

    public static void setRoadMap(Vector<Road> roadMap) {
        ParamCache.roadMap = roadMap;
    }


    public static void setStationMap(Vector<Station> stationMap) {
        ParamCache.stationMap = stationMap;
    }

    public static void setLaneMap(Vector<LaneParam> laneMap) {
        ParamCache.laneMap = laneMap;
    }
    public static void setOperatorMap(Vector<Operator> operatorMap) {
        ParamCache.operatorMap = operatorMap;
    }

    public static void setRoadChargeStdMap(Vector<RoadChargeStd> roadChargeStdMap) {
        ParamCache.roadChargeStdMap = roadChargeStdMap;
    }

    public static void setValidPriceMap(Map<Integer, Vector<Price>> validPriceMap) {
        ParamCache.validPriceMap = validPriceMap;
    }

    public static void setPriceMap(
            Map<Integer, Map<Integer, Vector<Price>>> priceMap) {
        ParamCache.priceMap = priceMap;
    }

    public static void setBridgeExtraChargeStdMap(
            Vector<BridgeExtraChargeStd> bridgeExtraChargeStdMap) {
        ParamCache.bridgeExtraChargeStdMap = bridgeExtraChargeStdMap;
    }

    public static void setBwgListMap(Vector<VehPlateBWG> bwgListMap) {
        ParamCache.bwgListMap = bwgListMap;
    }

    public static void setIssuerMap(Vector<Issuer> issuerMap) {
        ParamCache.issuerMap = issuerMap;
    }

    public static void setXTCardBlackDeltaList(Map<String, List<byte[]>> XTCardBlackDeltaList) {
        ParamCache.XTCardBlackDeltaList = XTCardBlackDeltaList;
    }

    public static void setUserRateMap(Vector<UserRate> userRateMap) {
        ParamCache.userRateMap = userRateMap;
    }

    public static void setOverLoadPrices(OverLoadPrice overLoadPrices) {
        ParamCache.overLoadPrices = overLoadPrices;
    }

    public static void setTravelTimes(List<TravelTime> travelTimes) {
        ParamCache.travelTimes = travelTimes;
    }

    public static void setCurrentVersionMap(
			Map<String, Version> currentVersionMap) {
		ParamCache.currentVersionMap = currentVersionMap;
	}
    
	public static void setFutureVersionMap(Map<String, Version> futureVersionMap) {
		ParamCache.futureVersionMap = futureVersionMap;
	}
	
	public static void setDisplayVersionMap(Map<String, Version> displayVersionMap) {
		ParamCache.displayVersionMap = displayVersionMap;
	}

	public static void setDiscountInfos(Vector<DiscountInfo> discountInfos) {
		ParamCache.discountInfos = discountInfos;
	}
	
	public static void setEncryptionKeys(Vector<EncryptionKey> encryptionKeys) {
		ParamCache.encryptionKeys = encryptionKeys;
	}
	
	public static void setRunLaneParaVers(Vector<LaneParaVer> runLaneParaVers) {
		ParamCache.runLaneParaVers = runLaneParaVers;
	}

	public static Vector<Road> getRoadMap() {
        return roadMap;
    }

    public static Vector<Station> getStationMap() {
        return stationMap;
    }

    public static Vector<LaneParam> getLaneMap() {
        return laneMap;
    }

    public static Vector<Operator> getOperatorMap() {
        return operatorMap;
    }

    public static Vector<RoadChargeStd> getRoadChargeStdMap() {
        return roadChargeStdMap;
    }

    public static Map<Integer, Vector<Price>> getValidPriceMap() {
        return validPriceMap;
    }

    public static Map<Integer, Map<Integer, Vector<Price>>> getPriceMap() {
        return priceMap;
    }

    public static Vector<BridgeExtraChargeStd> getBridgeExtraChargeStdMap() {
        return bridgeExtraChargeStdMap;
    }

    public static Vector<VehPlateBWG> getBwgListMap() {
        return bwgListMap;
    }

    public static Map<String, List<byte[]>> getXTCardBlackDeltaList() {
        return XTCardBlackDeltaList;
    }

    public static Vector<Issuer> getIssuerMap() {
        return issuerMap;
    }

    public static Vector<UserRate> getUserRateMap() {
        return userRateMap;
    }

    public static OverLoadPrice getOverLoadPrices() {
        return overLoadPrices;
    }

    public static List<TravelTime> getTravelTimes() {
        return travelTimes;
    }

	public static Map<String, Version> getCurrentVersionMap() {
		return currentVersionMap;
	}
	
	public static Map<String, Version> getFutureVersionMap() {
		return futureVersionMap;
	}

	public static String getFareVersion() {
		return fareVersion;
	}

	public static void setFareVersion(String fareVersion) {
		ParamCache.fareVersion = fareVersion;
	}

	public static Map<String, Version> getRateVersionMap() {
		return rateVersionMap;
	}

	public static void setRateVersionMap(Map<String, Version> rateVersionMap) {
		ParamCache.rateVersionMap = rateVersionMap;
	}

	public static Vector<DiscountInfo> getDiscountInfos() {
		return discountInfos;
	}

	public static Vector<EncryptionKey> getEncryptionKeys() {
		return encryptionKeys;
	}

	public static Vector<LaneParaVer> getRunLaneParaVers() {
		return runLaneParaVers;
	}

	public static Map<String, Version> getDisplayVersionMap() {
		return displayVersionMap;
	}


	public static Vector<OfficialCard> getOfficialCardMap() {
		return officialCardMap;
	}


	public static void setOfficialCardMap(Vector<OfficialCard> officialCardMap) {
		ParamCache.officialCardMap = officialCardMap;
	}
	
	
	
	
}
