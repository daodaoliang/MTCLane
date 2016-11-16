package com.hgits.util.rate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.hgits.control.ETCCardBlackControl;
import com.hgits.exception.MTCException;
import com.hgits.service.constant.BaseConstant;
import com.hgits.service.constant.DateConstant;
import com.hgits.service.constant.MtcConstant;
import com.hgits.service.constant.ParamConstant;
import com.hgits.util.DateUtils;
import com.hgits.util.IntegerUtils;
import com.hgits.util.LaneListUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.ReflectionUtils;
import com.hgits.util.StringUtils;
import com.hgits.util.file.FileUtils;
import com.hgits.util.file.ReadFromFileUtils;
import com.hgits.vo.BridgeExtraChargeStd;
import com.hgits.vo.Constant;
import com.hgits.vo.EncryptionKey;
import com.hgits.vo.Issuer;
import com.hgits.vo.OfficialCard;
import com.hgits.vo.Operator;
import com.hgits.vo.OverLoadPrice;
import com.hgits.vo.Price;
import com.hgits.vo.RoadChargeStd;
import com.hgits.vo.Station;
import com.hgits.vo.TravelTime;
import com.hgits.vo.UserRate;
import com.hgits.vo.VehPlateBWG;
import com.hgits.vo.XTCardBlackBaseListByte;
import com.hgits.vo.XTCardBlackDeltaList;
import com.hgits.vo.XTCardBlackDeltaListByte;
import com.hgits.vo.XTCardBlackList;
import com.hgits.vo.XTCardBlackListByte;

/**
 * 参数表读取工具类
 *
 * @author wh
 *
 */
public class ParamCacheFileRead {

	private static final String RateError = "RateVersion";

	/**
	 * 新费率是否有效
	 */
	public boolean NEW_RATE_VALID = true;

	/**
	 * 是否加载完参数
	 */
	private boolean isLoadParam = false;

	private static ParamCacheFileRead paramCacheFileRead;

	private static XTCardBlackBaseListByte xtCardBlackBaseListByte = new XTCardBlackBaseListByte();

	private static XTCardBlackDeltaListByte xtCardBlackDeltaListByte = new XTCardBlackDeltaListByte();

	private static RecvParamUtils rpu = new RecvParamUtils();

	private ParamCacheFileRead() {

	}

	public synchronized static ParamCacheFileRead getInstance() {
		if (paramCacheFileRead == null) {
			paramCacheFileRead = new ParamCacheFileRead();
		}
		return paramCacheFileRead;
	}

	private Logger logger = Logger.getLogger(ParamCacheFileRead.class);
	
	private ETCCardBlackControl etcCardBlackControl = ETCCardBlackControl.getInstance();

	/*
	 * 获取配置表中的路段、站级信息和车道类型
	 */
	private Integer defaultRoadId = Integer.valueOf(MyPropertiesUtils
			.getProperties(Constant.PROP_MTCCONFIG, "roadId", "999"));
	private Integer defautlStationId = Integer.valueOf(MyPropertiesUtils
			.getProperties(Constant.PROP_MTCCONFIG, "stationId", "999"));
	private Integer laneType = Integer.valueOf(MyPropertiesUtils.getProperties(
			Constant.PROP_MTCCONFIG, MtcConstant.PROPERTIES_LANETYPE,
			MtcConstant.MTC_LANE_TYPE_EN));

	/**
	 * 车型
	 */
	public static List<Integer> vehRoutes = new ArrayList<Integer>();

	static {
		vehRoutes.add(1);
		vehRoutes.add(2);
		vehRoutes.add(3);
		vehRoutes.add(4);
		vehRoutes.add(5);
		vehRoutes.add(7);
	}

	/**
	 * 读取收费站编码
	 *
	 * @throws MTCException
	 */
	public boolean readStation() {
		boolean isRead = false;
		try {
			Vector<Station> tempMap = new Vector<Station>();
			List<String> list = readConfigFile(ParamConstant.PARAM_STATION);
			Station s = null;
			String[] station = null;
			if (!list.isEmpty()) {
				ParamVersionUtils
						.clearParamVersion(ParamConstant.PARAM_STATION);
				for (String string : list) {
					// 读取一行记录
					station = string.split(ParamConstant.TAB_STRING, -1);

					// 正常的编码记录有13个字段，把错误的记录排除掉
					if (station.length == ParamConstant.STATION_LENGTH) {
						s = convertToStation(station);

						ParamVersionUtils.checkParamVersion(s,
								ParamConstant.PARAM_STATION);

						tempMap.add(s);
					}
				}
			}
			if (tempMap.isEmpty()) {
				RateParamErrorUtils.put(ParamConstant.PARAM_STATION,
						"收费站编码参数表缺失，请检查！");
			} else {
				// 将读取到的内容放到内存中
				ParamCache.setStationMap(tempMap);
				RateParamErrorUtils.remove(ParamConstant.PARAM_STATION);//清除掉之前的错误信息
				isRead = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			RateParamErrorUtils.put(ParamConstant.PARAM_STATION,
					"读取收费站编码参数表异常，错误原因：" + e.getMessage());
		}
		return isRead;
	}

	/**
	 * 字符串转收费站对象
	 *
	 * @param road
	 * @return
	 */
	protected Station convertToStation(String[] station) {
		Station s = new Station();
		s.setAreaId(IntegerUtils.parseInteger(station[0]));
		s.setRoaduniqueId(IntegerUtils.parseInteger(station[1]));
		s.setRoadId(IntegerUtils.parseInteger(station[2]));
		s.setStationId(IntegerUtils.parseInteger(station[3]));
		s.setStationName(station[4]);
		s.setStationServerName(station[5]);
		s.setStationServerIp(station[6]);
		s.setStationDBName(station[7]);
		s.setStationDBUser(station[8]);
		s.setStationDBPassword(station[9]);
		s.setVersion(IntegerUtils.parseInteger(station[10]));
		s.setStartTime(parseDate(station[11]));
		s.setStatus(IntegerUtils.parseInteger(station[12]));
		return s;
	}

	/**
	 * 读操作员表
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean readOperator() {
		boolean isRead = false;
		try {
			Vector<Operator> tempMap = new Vector<Operator>();
			List<String> list = readConfigFile(ParamConstant.PARAM_OPERATOR);
			Operator o = null;
			String[] operator = null;
			if (!list.isEmpty()) {
				ParamVersionUtils
						.clearParamVersion(ParamConstant.PARAM_OPERATOR);
				for (String string : list) {
					// 读取一行记录
					operator = string.split(ParamConstant.TAB_STRING, -1);

					// 正常的编码记录有9个字段，把错误的记录排除掉
					if (operator.length == 9) {
						o = new Operator();
						o.setRoaduniqueId(IntegerUtils
								.parseInteger(operator[0]));
						o.setStationId(IntegerUtils.parseInteger(operator[1]));
						o.setOperatorNo(operator[2]);
						o.setOperatorName(operator[3]);
						o.setAuthorizeStationSerial(operator[4]);
						o.setPassWord(operator[5]);
						o.setVersion(IntegerUtils.parseInteger(operator[6]));
						o.setStartTime(parseDate(operator[7]));
						o.setStatus(IntegerUtils.parseInteger(operator[8]));

						ParamVersionUtils.checkParamVersion(o,
								ParamConstant.PARAM_OPERATOR);

						tempMap.add(o);
					}

				}
			}

			if (tempMap.isEmpty()) {
				RateParamErrorUtils.put(ParamConstant.PARAM_OPERATOR,
						"操作员表缺失，请检查！");
			} else {
				// 将读取到的内容放到内存中
				ParamCache.setOperatorMap(tempMap);
				RateParamErrorUtils.remove(ParamConstant.PARAM_OPERATOR);//清除掉之前的错误信息
				isRead = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			RateParamErrorUtils.put(ParamConstant.PARAM_OPERATOR,
					"读取操作员参数表异常，错误原因：" + e.getMessage());
		}

		return isRead;
	}

	/**
	 * 读路段收费标准表
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean readRoadChargeStd() {
		boolean isRead = false;
		try {
			Vector<RoadChargeStd> tempMap = new Vector<RoadChargeStd>();
			List<String> list = readConfigFile(ParamConstant.PARAM_ROADCHARGESTD);
			RoadChargeStd rcs = null;
			String[] roadChargeStd = null;
			if (!list.isEmpty()) {
				ParamVersionUtils
						.clearParamVersion(ParamConstant.PARAM_ROADCHARGESTD);
				for (String string : list) {
					// 读取一行记录
					roadChargeStd = string.split(ParamConstant.TAB_STRING, -1);

					// 正常的编码记录有21个字段，把错误的记录排除掉
					if (roadChargeStd.length == ParamConstant.ROADCHARGESTD_LENGTH) {
						rcs = convertToRoadChargeStd(roadChargeStd);

						ParamVersionUtils.checkParamVersion(rcs,
								ParamConstant.PARAM_ROADCHARGESTD);
						RateParamCacheHelpr.putParamVersion(rcs,
								ParamConstant.PARAM_ROADCHARGESTD);

						ParamCache.setFareVersion(String.valueOf(rcs
								.getVersion()));// 费率版本，供出入口流水生成时使用

						tempMap.add(rcs);
					}

				}
			}

			if (tempMap.isEmpty()) {
				RateParamErrorUtils.put(ParamConstant.PARAM_ROADCHARGESTD,
						"路段叠加收费标准信息缺失，请检查！");
			} else {
				// 费率参数表版本检查
				RateParamCacheHelpr.checkRoadChargeSTDVersion();

				// 将读取到的内容放到内存中
				ParamCache.setRoadChargeStdMap(tempMap);
				
				RateParamErrorUtils.remove(ParamConstant.PARAM_ROADCHARGESTD);//清除掉之前的错误信息

				isRead = true;
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			RateParamErrorUtils.put(ParamConstant.PARAM_ROADCHARGESTD,
					"读路段收费标准参数表异常，错误原因：" + e.getMessage());
		}
		return isRead;
	}

	/**
	 * 字符串转路段叠加收费对象
	 *
	 * @param roadChargeStd
	 * @return
	 */
	protected RoadChargeStd convertToRoadChargeStd(String[] roadChargeStd) {
		RoadChargeStd rcs = new RoadChargeStd();
		rcs.setRoadUniqueId(IntegerUtils.parseInteger(roadChargeStd[0]));
		rcs.setRoadName(roadChargeStd[1]);
		rcs.setVehTypeStd1(parseDouble(roadChargeStd[2]));
		rcs.setVehTypeStd2(parseDouble(roadChargeStd[3]));
		rcs.setVehTypeStd3(parseDouble(roadChargeStd[4]));
		rcs.setVehTypeStd4(parseDouble(roadChargeStd[5]));
		rcs.setVehTypeStd5(parseDouble(roadChargeStd[6]));
		rcs.setVehWeightStd1(parseDouble(roadChargeStd[7]));
		rcs.setVehWeightStd2(parseDouble(roadChargeStd[8]));
		rcs.setVehWeightStd3(parseDouble(roadChargeStd[9]));
		rcs.setWeightRange1(parseDouble(roadChargeStd[10]));
		rcs.setWeightRange2(parseDouble(roadChargeStd[11]));
		rcs.setWeightRange3(parseDouble(roadChargeStd[12]));
		rcs.setModulus(parseDouble(roadChargeStd[13]));
		rcs.setVersion(IntegerUtils.parseInteger(roadChargeStd[14]));
		rcs.setStartTime(parseDate(roadChargeStd[15]));
		rcs.setStatus(IntegerUtils.parseInteger(roadChargeStd[16]));
		rcs.setSpear1(IntegerUtils.parseInteger(roadChargeStd[17]));
		rcs.setSpear2(IntegerUtils.parseInteger(roadChargeStd[18]));
		rcs.setSpear3(roadChargeStd[19]);
		rcs.setSpear4(roadChargeStd[20]);
		return rcs;
	}

	/**
	 * 读费率表
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean readPrice() {
		boolean isRead = false;
		try {
			Map<Integer, Vector<Price>> tempMap = new HashMap<Integer, Vector<Price>>();

			Vector<Price> tempVector = null;
			for (Integer vehType : vehRoutes) { // 遍历1-7型车费率
				List<String> list = readConfigFile(ParamConstant.PARAM_VEHROUTE
						+ vehType);
				Price p = null;
				tempVector = new Vector<Price>();

				String[] price = null;
				if (!list.isEmpty()) {
					ParamVersionUtils
							.clearParamVersion(ParamConstant.PARAM_VEHROUTE
									+ vehType);
					for (String string : list) {
						// 读取一行记录
						price = string.split(ParamConstant.TAB_STRING, -1);
						// 正常的编码记录有20个字段，把错误的记录排除掉
						if (price.length == ParamConstant.PRICE_LENGTH) {

							p = convertToPrice(price);

							ParamVersionUtils.checkParamVersion(p,
									ParamConstant.PARAM_VEHROUTE + vehType); // 添加版本信息
							RateParamCacheHelpr.putParamVersion(p,
									ParamConstant.PARAM_VEHROUTE + vehType); // 添加版本信息

							tempVector.add(p);
						}

					}
				}

				if (tempVector.isEmpty()) {
					RateParamErrorUtils.put(ParamConstant.PARAM_VEHROUTE
							+ vehType, ParamConstant.PARAM_VEHROUTE + vehType
							+ " 表费率信息不存在!");
				} else {
					tempMap.put(vehType, tempVector);
					RateParamErrorUtils.remove(ParamConstant.PARAM_VEHROUTE+ vehType);//清除掉之前的错误信息
				}
			}

			if (tempMap.size() == vehRoutes.size()) { // 读取到全部的费率
				// 费率参数表版本检查
				RateParamCacheHelpr.checkRateVersion();

				// 将读取到的内容放到内存中
				ParamCache.setValidPriceMap(tempMap);
				
				isRead = true;
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			RateParamErrorUtils.put(ParamConstant.PARAM_VEHROUTE,
					"读费率参数表异常，错误原因：" + e.getMessage());
		}
		return isRead;
	}

	/**
	 * 字符串转费率对象
	 *
	 * @param s
	 * @return
	 * @throws MTCException
	 */
	protected Price convertToPrice(String[] s) throws MTCException {
		Integer tempRoadId = IntegerUtils.parseInteger(s[2]);
		Integer tempStationId = IntegerUtils.parseInteger(s[3]);
		Integer version = IntegerUtils.parseInteger(s[11]);
		Integer vehRoute = IntegerUtils.parseInteger(s[4]);

		if (!defaultRoadId.equals(tempRoadId)) {
			RateParamErrorUtils.put(ParamConstant.PARAM_VEHROUTE + vehRoute,
					ParamConstant.PARAM_VEHROUTE + vehRoute
							+ " 表，费率信息与当前车道配置路段信息不符!");
			throw new MTCException(ParamConstant.PARAM_VEHROUTE + vehRoute
					+ " 表，费率信息与当前车道配置路段信息不符!");
		}

		if (!defautlStationId.equals(tempStationId)) {
			RateParamErrorUtils.put(ParamConstant.PARAM_VEHROUTE + vehRoute,
					ParamConstant.PARAM_VEHROUTE + vehRoute
							+ " 表，费率信息与当前车道配置站信息不符!");
			throw new MTCException(ParamConstant.PARAM_VEHROUTE + vehRoute
					+ " 表，费率信息与当前车道配置站信息不符!");
		}
		Price p = new Price();
		p.setEnRoadId(IntegerUtils.parseInteger(s[0]));
		p.setEnStationId(IntegerUtils.parseInteger(s[1]));
		p.setExRoadId(tempRoadId);
		p.setExStationId(tempStationId);
		p.setVehType(IntegerUtils.parseInteger(s[4]));
		p.setPassType(IntegerUtils.parseInteger(s[5]));
		p.setPassRoaduniqueId(IntegerUtils.parseInteger(s[6]));
		p.setRoadUniqueIdForBridge(IntegerUtils.parseInteger(s[7]));
		p.setPassBridgeId(IntegerUtils.parseInteger(s[8]));
		p.setMiles(parseDouble(s[9]));
		p.setDistance(parseDouble(s[10]));
		p.setVersion(version);
		p.setRoadStdVer(IntegerUtils.parseInteger(s[12]));
		p.setBridgeExtVer(IntegerUtils.parseInteger(s[13]));
		p.setTravelTime(IntegerUtils.parseInteger(s[14]));
		p.setStartTime(parseDate(s[15]));
		p.setSpare1(IntegerUtils.parseInteger(s[16]));
		p.setSpare2(IntegerUtils.parseInteger(s[17]));
		p.setSpare3(s[18]);
		p.setSpare4(s[19]);
		return p;
	}

	/**
	 * 读桥隧叠加收费标准表
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean readBridgeExtraChargeStd() {
		boolean isRead = false;
		try {
			Vector<BridgeExtraChargeStd> tempMap = new Vector<BridgeExtraChargeStd>();
			List<String> list = readConfigFile(ParamConstant.PARAM_BRIDGE_EXTRA_CHARGE_STD);
			BridgeExtraChargeStd becs = null;
			String[] bridgeExtraChargeStd = null;
			if (!list.isEmpty()) {
				ParamVersionUtils
						.clearParamVersion(ParamConstant.PARAM_BRIDGE_EXTRA_CHARGE_STD);
				for (String string : list) {
					// 读取一行记录
					bridgeExtraChargeStd = string.split(
							ParamConstant.TAB_STRING, -1);

					// 正常的编码记录有22个字段，把错误的记录排除掉
					if (bridgeExtraChargeStd.length == ParamConstant.BRIDGEEXTRACHARGESTD_LENGTH) {
						becs = convertToBridgeExtraChargeStd(bridgeExtraChargeStd);

						ParamVersionUtils.checkParamVersion(becs,
								ParamConstant.PARAM_BRIDGE_EXTRA_CHARGE_STD);
						RateParamCacheHelpr.putParamVersion(becs,
								ParamConstant.PARAM_BRIDGE_EXTRA_CHARGE_STD);

						tempMap.add(becs);
					}

				}
			}

			if (tempMap.isEmpty()) {
				RateParamErrorUtils.put(
						ParamConstant.PARAM_BRIDGE_EXTRA_CHARGE_STD,
						"桥隧叠加收费标准信息缺失，请检查！");
			} else {
				// 费率参数表版本检查
				RateParamCacheHelpr.checkBridgeExtraChargeSTDVersion();

				// 将读取到的内容放到内存中
				ParamCache.setBridgeExtraChargeStdMap(tempMap);
				
				RateParamErrorUtils.remove(ParamConstant.PARAM_BRIDGE_EXTRA_CHARGE_STD);//清除掉之前的错误信息
				isRead = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			RateParamErrorUtils.put(
					ParamConstant.PARAM_BRIDGE_EXTRA_CHARGE_STD,
					"读取桥隧叠加收费标准参数表异常，错误原因：" + e.getMessage());
		}
		return isRead;
	}

	/**
	 * 字符串转桥隧叠加收费对象
	 *
	 * @param bridgeExtraChargeStd
	 * @return
	 */
	protected BridgeExtraChargeStd convertToBridgeExtraChargeStd(
			String[] bridgeExtraChargeStd) {
		BridgeExtraChargeStd becs = new BridgeExtraChargeStd();
		becs.setRoadUniqueId(IntegerUtils.parseInteger(bridgeExtraChargeStd[0]));
		becs.setBridgeId(IntegerUtils.parseInteger(bridgeExtraChargeStd[1]));
		becs.setBridgeName(bridgeExtraChargeStd[2]);
		becs.setVehTypeExt1(IntegerUtils.parseInteger(bridgeExtraChargeStd[3]));
		becs.setVehTypeExt2(IntegerUtils.parseInteger(bridgeExtraChargeStd[4]));
		becs.setVehTypeExt3(IntegerUtils.parseInteger(bridgeExtraChargeStd[5]));
		becs.setVehTypeExt4(IntegerUtils.parseInteger(bridgeExtraChargeStd[6]));
		becs.setVehTypeExt5(IntegerUtils.parseInteger(bridgeExtraChargeStd[7]));
		becs.setVehWeightExt1(IntegerUtils
				.parseInteger(bridgeExtraChargeStd[8]));
		becs.setVehWeightExt2(IntegerUtils
				.parseInteger(bridgeExtraChargeStd[9]));
		becs.setVehWeightExt3(IntegerUtils
				.parseInteger(bridgeExtraChargeStd[10]));
		becs.setWeightRange1(IntegerUtils
				.parseInteger(bridgeExtraChargeStd[11]));
		becs.setWeightRange2(IntegerUtils
				.parseInteger(bridgeExtraChargeStd[12]));
		becs.setWeightRange3(IntegerUtils
				.parseInteger(bridgeExtraChargeStd[13]));
		becs.setModulus(parseDouble(bridgeExtraChargeStd[14]));
		becs.setVersion(IntegerUtils.parseInteger(bridgeExtraChargeStd[15]));
		becs.setStartTime(parseDate(bridgeExtraChargeStd[16]));
		becs.setStatus(IntegerUtils.parseInteger(bridgeExtraChargeStd[17]));
		becs.setSpear1(IntegerUtils.parseInteger(bridgeExtraChargeStd[18]));
		becs.setSpear2(IntegerUtils.parseInteger(bridgeExtraChargeStd[19]));
		becs.setSpear3(bridgeExtraChargeStd[20]);
		becs.setSpear4(bridgeExtraChargeStd[21]);
		return becs;
	}

	/**
	 * 读黑白灰表
	 * @return 
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean readBwgList() {
		boolean isRead = true;
		try {
			Vector<VehPlateBWG> tempMap = new Vector<VehPlateBWG>();
			List<String> list = readConfigFile(ParamConstant.PARAM_BWG_LIST);
			VehPlateBWG vpbwg = null;
			String[] vehPlateBWG = null;
			if (!list.isEmpty()) {
				ParamVersionUtils
						.clearParamVersion(ParamConstant.PARAM_BWG_LIST);
				for (String string : list) {
					// 读取一行记录
					vehPlateBWG = string.split(ParamConstant.TAB_STRING, -1);

					// 正常的编码记录有6个字段，把错误的记录排除掉
					if (vehPlateBWG.length == 6) {
						vpbwg = new VehPlateBWG();
						vpbwg.setVehPlateNo(vehPlateBWG[0]);
						vpbwg.setVehPlateColor(vehPlateBWG[1]);
						vpbwg.setStatus(IntegerUtils
								.parseInteger(vehPlateBWG[2]));
						vpbwg.setVersion(IntegerUtils
								.parseInteger(vehPlateBWG[3]));
						vpbwg.setStartTime(parseDate(vehPlateBWG[4]));
						vpbwg.setDescribe(vehPlateBWG[5]);

						tempMap.add(vpbwg);
					}
				}
			}
			if (vpbwg == null){
				vpbwg = new VehPlateBWG();
			}
			ParamVersionUtils.checkParamVersion(vpbwg,
					ParamConstant.PARAM_BWG_LIST);

			// 将读取到的内容放到内存中
			ParamCache.setBwgListMap(tempMap);
			
			RateParamErrorUtils.remove(ParamConstant.PARAM_BWG_LIST);//清除掉之前的错误信息

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			RateParamErrorUtils.put(ParamConstant.PARAM_BWG_LIST,
					"读取黑白灰名单参数表异常，错误原因：" + e.getMessage());
			isRead = false;
		}
		return isRead;
	}

	/**
	 * 读湘通卡黑名单增量基础
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean readXtCardBlackBaseList() {
		boolean isRead = false;
		try {
			XTCardBlackList xtCardBlackList = null;
			if(etcCardBlackControl.isOptimisedActive()){
				xtCardBlackList = etcCardBlackControl.queryETCCardBlackAll(null, null);
			}
			else{
				readBlackDeltaFile(ParamConstant.PARAM_XTCARD_BLACK_BASE_LIST, 1);
				xtCardBlackList = getXTCardBlackBaseList();
			}
			if (xtCardBlackList == null) {
				RateParamErrorUtils.put(
						ParamConstant.PARAM_XTCARD_BLACK_BASE_LIST,
						"ETC卡黑名单基础表缺失，请检查！");
			} else {
				ParamVersionUtils.checkParamVersion(xtCardBlackList,
						ParamConstant.PARAM_XTCARD_BLACK_BASE_LIST);
				
				RateParamErrorUtils.remove(ParamConstant.PARAM_XTCARD_BLACK_BASE_LIST);//清除掉之前的错误信息
				
				isRead = true;
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			RateParamErrorUtils.put(ParamConstant.PARAM_XTCARD_BLACK_BASE_LIST,
					"读取ETC卡黑名单基础表异常，错误原因：" + e.getMessage());
		}
		return isRead;
	}

	/**
	 * 生成湘通卡黑名单增量基础数据
	 * 
	 * @return
	 */
	private XTCardBlackList getXTCardBlackBaseList() {

		int length = XTCardBlackBaseListByte.lineLength;
		byte[] record = new byte[length]; // 湘通卡黑名单记录长度

		byte[] by = XTCardBlackBaseListByte.getBuffer1();
		XTCardBlackList xtCardBlackList = null;

		if (by != null) {

			System.arraycopy(by, 0, record, 0, length); // 复制湘通卡黑名单的内容
			if (record.hashCode() == 0) { // 如果记录全部读取完毕，则hashcode为空了
				return xtCardBlackList;
			}

			xtCardBlackList = new XTCardBlackList();
			xtCardBlackList.setXtCardNo(new String(Arrays.copyOfRange(record,
					0, 20)));// 卡号
			xtCardBlackList.setXtCardType(Integer.valueOf(new String(Arrays
					.copyOfRange(record, 20, 22))));// 卡类型
			xtCardBlackList.setStatus(Integer.valueOf(new String(Arrays
					.copyOfRange(record, 22, 23))));// 卡状态
			xtCardBlackList.setGenCau(Integer.valueOf(new String(Arrays
					.copyOfRange(record, 23, 24))));// 生成原因
			xtCardBlackList.setGenTime(DateUtils.parseDate(
					new String(Arrays.copyOfRange(record, 24, 38)),
					DateConstant.DATE_FORMAT_YYYYMMDDHHMMSS));// 生成日期
			xtCardBlackList.setVersion(Integer.valueOf(new String(Arrays
					.copyOfRange(record, 38, 41))));// 版本号
			xtCardBlackList.setStartTime(DateUtils.parseDate(
					new String(Arrays.copyOfRange(record, 41, 55)),
					DateConstant.DATE_FORMAT_YYYYMMDDHHMMSS));// 启用时间
		}

		return xtCardBlackList;
	}

	/**
	 * 读湘通卡黑名单增量<br/>
	 * 由于全国联网的问题，增加了500万的黑名单读取和检查，所以黑名单和黑名单增量信息表里面要进行特殊处理。
	 * @return 
	 *
	 * @throws MTCException
	 */
	public boolean readXtCardBlackIncrementList() {
		boolean isRead = true;
		try {
			XTCardBlackDeltaList xtCardBlackDeltaList = null;
			if(etcCardBlackControl.isOptimisedActive()){
				xtCardBlackDeltaList = etcCardBlackControl.queryETCCardBlackIncrement(null,null);
			}
			else{
				readBlackDeltaFile(ParamConstant.PARAM_XTCARD_BLACK_DELTA_LIST, 2);

				xtCardBlackDeltaList = getXTCardBlackDeltaList();
			}
			
			if (xtCardBlackDeltaList == null) {
				xtCardBlackDeltaList = new XTCardBlackDeltaList();
			}
			ParamVersionUtils.checkParamVersion(xtCardBlackDeltaList,
					ParamConstant.PARAM_XTCARD_BLACK_DELTA_LIST);
		} catch (Exception e) {
			logger.error("读取湘通卡黑名单增量参数表异常，错误原因：" + e.getMessage(), e);
			RateParamErrorUtils.put(
					ParamConstant.PARAM_XTCARD_BLACK_DELTA_LIST,
					"读取湘通卡黑名单增量参数表异常，错误原因：" + e.getMessage());
			isRead = false;
		}
		return isRead;
	}

	/**
	 * 生成湘通卡增量黑名单数据
	 * 
	 * @return
	 */
	private XTCardBlackDeltaList getXTCardBlackDeltaList() {

		int length = XTCardBlackDeltaListByte.lineLenght;
		byte[] record = new byte[length]; // 湘通卡黑名单增量记录长度

		byte[] by = XTCardBlackDeltaListByte.getBuffer1();
		XTCardBlackDeltaList xtCardBlackDeltaList = null;

		if (by != null) {

			System.arraycopy(by, 0, record, 0, length); // 复制湘通卡黑名单的内容

			if (record.hashCode() == 0) { // 如果记录全部读取完毕，则hashcode为空了
				return xtCardBlackDeltaList;
			}

			xtCardBlackDeltaList = new XTCardBlackDeltaList();
			xtCardBlackDeltaList.setXtCardNo(new String(Arrays.copyOfRange(
					record, 0, 20)));// 卡号
			xtCardBlackDeltaList.setXtCardType(Integer.valueOf(new String(
					Arrays.copyOfRange(record, 20, 22))));// 卡类型
			xtCardBlackDeltaList.setStatus(Integer.valueOf(new String(Arrays
					.copyOfRange(record, 22, 23))));// 卡状态
			xtCardBlackDeltaList.setGenCau(Integer.valueOf(new String(Arrays
					.copyOfRange(record, 23, 24))));// 生成原因
			xtCardBlackDeltaList.setGenTime(DateUtils.parseDate(new String(
					Arrays.copyOfRange(record, 24, 38)),
					DateConstant.DATE_FORMAT_YYYYMMDDHHMMSS));// 生成日期
			xtCardBlackDeltaList.setValidFlag(Integer.valueOf(new String(Arrays
					.copyOfRange(record, 38, 39))));// 有效标识
			xtCardBlackDeltaList.setVersion(Integer.valueOf(new String(Arrays
					.copyOfRange(record, 39, 42))));// 版本号
			xtCardBlackDeltaList.setStartTime(DateUtils.parseDate(new String(
					Arrays.copyOfRange(record, 42, 56)),
					DateConstant.DATE_FORMAT_YYYYMMDDHHMMSS));// 启用时间
		}

		return xtCardBlackDeltaList;
	}

	/**
	 * 读发行方名单
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean readIssuer() {
		boolean isRead = false;
		try {
			Vector<Issuer> tempMap = new Vector<Issuer>();

			List<String> list = readConfigFile(ParamConstant.PARAM_ISSUER);
			Issuer is = null;
			String[] issuer = null;

			if (!list.isEmpty()) {
				ParamVersionUtils.clearParamVersion(ParamConstant.PARAM_ISSUER);
				for (String string : list) {
					// 读取一行记录
					issuer = string.split(ParamConstant.TAB_STRING, -1);

					// 正常的编码记录有12个字段，把错误的记录排除掉
					if (issuer.length == ParamConstant.ISSUER_LENGTH) {
						is = convertToIssuer(issuer);

						ParamVersionUtils.checkParamVersion(is,
								ParamConstant.PARAM_ISSUER);

						tempMap.add(is);
					}
				}
			}

			if (tempMap.isEmpty()) {
				RateParamErrorUtils.put(ParamConstant.PARAM_ISSUER,
						"发行方参数表缺失，请检查！");
			} else {
				// 将读取到的内容放到内存中
				ParamCache.setIssuerMap(tempMap);
				
				RateParamErrorUtils.remove(ParamConstant.PARAM_ISSUER);//清除掉之前的错误信息
				
				isRead = true;
			}
		} catch (Exception e) {
			logger.error("读取发行方名单参数表异常，错误原因：" + e.getMessage(), e);
			RateParamErrorUtils.put(ParamConstant.PARAM_ISSUER,
					"读取发行方名单参数表异常，错误原因：" + e.getMessage());
		}
		return isRead;
	}

	/**
	 * 将文本信息转换为发行方名单对象
	 * 
	 * @param issuer
	 * @return
	 */
	protected Issuer convertToIssuer(String[] issuer) {
		Issuer is = new Issuer();
		is.setNetNo(IntegerUtils.parseInteger(issuer[0]));
		is.setBriefName(issuer[1]);
		is.setFullName(issuer[2]);
		is.setProvinceBcdName(issuer[3]);
		is.setProvinceCode(IntegerUtils.parseInteger(issuer[4]));
		is.setTransNetId(IntegerUtils.parseInteger(issuer[5]));
		is.setStartTime(parseDate(issuer[6]));
		is.setEndTime(parseDate(issuer[7]));
		is.setBackup1(IntegerUtils.parseInteger(issuer[8]));
		is.setBackup2(issuer[9]);
		is.setVersion(IntegerUtils.parseInteger(issuer[10]));
		is.setMemo(issuer[11]);
		return is;
	}

	/**
	 * 读用户折扣表
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean readUserRate() {
		boolean isRead = false;
		try {
			Vector<UserRate> tempMap = new Vector<UserRate>();

			List<String> list = readConfigFile(ParamConstant.PARAM_USERRATE);
			UserRate u = null;
			String[] userRate = null;
			if (!list.isEmpty()) {
				ParamVersionUtils
						.clearParamVersion(ParamConstant.PARAM_USERRATE);
				for (String string : list) {
					// 读取一行记录
					userRate = string.split(ParamConstant.TAB_STRING, -1);

					// 正常的编码记录有6个字段，把错误的记录排除掉
					if (userRate.length == ParamConstant.USERRATE_LENGTH) {
						u = convertToUserRate(userRate);

						ParamVersionUtils.checkParamVersion(u,
								ParamConstant.PARAM_USERRATE);

						tempMap.add(u);
					}
				}
			}

			if (tempMap.isEmpty()) {
				RateParamErrorUtils.put(ParamConstant.PARAM_USERRATE,
						"用户折扣表缺失，请检查！");
//				throw new MTCException("用户折扣表缺失，请检查！");
			} else {
				// 将读取到的内容放到内存中
				ParamCache.setUserRateMap(tempMap);
				
				RateParamErrorUtils.remove(ParamConstant.PARAM_USERRATE);//清除掉之前的错误信息

				isRead = true;
			}

		} catch (Exception e) {
			logger.error("读取用户折扣参数表异常，错误原因：" + e.getMessage(), e);
			RateParamErrorUtils.put(ParamConstant.PARAM_USERRATE,
					"读取用户折扣参数表异常，错误原因：" + e.getMessage());
		}
		return isRead;
	}

	/**
	 * 将文本记录转换成为用户折扣信息
	 * 
	 * @param userRate
	 * @return
	 */
	protected UserRate convertToUserRate(String[] userRate) {
		UserRate u = new UserRate();
		u.setVehType(IntegerUtils.parseInteger(userRate[0]));
		u.setUserType(IntegerUtils.parseInteger(userRate[1]));
		u.setNetNo(IntegerUtils.parseInteger(userRate[2]));
		u.setProvinceBcdName(userRate[3]);
		u.setRate(parseDouble(userRate[4]));
		u.setVersion(IntegerUtils.parseInteger(userRate[5]));
		u.setStartTime(parseDate(userRate[6]));
		return u;
	}

	// /**
	// * 读优惠信息表
	// *
	// * @throws MTCException
	// */
	// public void readDiscountInfo() throws MTCException {
	// try {
	// Vector<DiscountInfo> tempMap = new Vector<DiscountInfo>();
	//
	// List<String> list = readConfigFile(ParamConstant.PARAM_TBL_DISCOUNTINFO);
	// DiscountInfo di = null;
	// String[] distinctInfo = null;
	// if(!list.isEmpty()){
	// ParamVersionUtils.clearParamVersion(ParamConstant.PARAM_TBL_DISCOUNTINFO);
	// for (String string : list) {
	// // 读取一行记录
	// distinctInfo = string.split(ParamConstant.TAB_STRING, -1);
	//
	// // 正常的编码记录有13个字段，把错误的记录排除掉
	// if (distinctInfo.length == ParamConstant.DISCOUNTINFO_LENGTH) {
	// di = convertToDiscountInfo(distinctInfo);
	//
	// ParamVersionUtils.checkParamVersion(di,
	// ParamConstant.PARAM_TBL_DISCOUNTINFO);
	//
	// tempMap.add(di);
	// }
	// }
	// }
	//
	// if(tempMap.isEmpty()){
	// RateParamErrorUtils.put(ParamConstant.PARAM_TBL_DISCOUNTINFO,
	// "优惠信息表缺失，请检查！");
	// throw new MTCException("优惠信息表缺失，请检查！");
	// }
	//
	// // 将读取到的内容放到内存中
	// ParamCache.setDiscountInfos(tempMap);
	//
	// } catch (Exception e) {
	// logger.error(e.getMessage(), e);
	// RateParamErrorUtils.put(ParamConstant.PARAM_TBL_DISCOUNTINFO,
	// "读取优惠信息参数表异常，错误原因：" + e.getMessage());
	// throw new MTCException("读取优惠信息参数表异常，错误原因：" + e.getMessage(), e);
	// }
	// }
	//
	// /**
	// * 将文本记录转换成为优惠信息
	// * @param distinctInfo
	// * @return
	// */
	// protected DiscountInfo convertToDiscountInfo(String[] distinctInfo) {
	// DiscountInfo discountInfo = new DiscountInfo();
	// discountInfo.setId(IntegerUtils.parseInteger(distinctInfo[0]));
	// discountInfo.setDisNo(IntegerUtils.parseInteger(distinctInfo[1]));
	// discountInfo.setDisName(distinctInfo[2]);
	// discountInfo.setDisType(IntegerUtils.parseInteger(distinctInfo[3]));
	// discountInfo.setDisRoad(IntegerUtils.parseInteger(distinctInfo[4]));
	// discountInfo.setDis(IntegerUtils.parseInteger(distinctInfo[5]));
	// discountInfo.setVehType(IntegerUtils.parseInteger(distinctInfo[6]));
	// discountInfo.setStartDate(parseDate(distinctInfo[7]));
	// discountInfo.setEndDate(parseDate(distinctInfo[8]));
	// discountInfo.setVersion(IntegerUtils.parseInteger(distinctInfo[9]));
	// discountInfo.setStartTime(parseDate(distinctInfo[10]));
	// discountInfo.setBackup1(distinctInfo[11]);
	// discountInfo.setBackup2(distinctInfo[12]);
	// return discountInfo;
	// }

	/**
	 * 读密钥信息表
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean readEncryptionKey() {
		boolean isRead = false;
		try {
			Vector<EncryptionKey> tempMap = new Vector<EncryptionKey>();

			List<String> list = readConfigFile(ParamConstant.PARAM_ENCRYPTIONKEY);
			EncryptionKey eKey = null;
			String[] encryptionKey = null;
			if (!list.isEmpty()) {
				ParamVersionUtils
						.clearParamVersion(ParamConstant.PARAM_ENCRYPTIONKEY);
				for (String string : list) {
					// 读取一行记录
					encryptionKey = string.split(ParamConstant.TAB_STRING, -1);

					// 正常的编码记录有8个字段，把错误的记录排除掉
					if (encryptionKey.length == ParamConstant.ENCRYPTIONKEY_LENGTH) {
						eKey = convertToEncryptionKey(encryptionKey);

						ParamVersionUtils.checkParamVersion(eKey,
								ParamConstant.PARAM_ENCRYPTIONKEY);

						tempMap.add(eKey);
					}
				}
			}

			if (tempMap.isEmpty()) {
				RateParamErrorUtils.put(ParamConstant.PARAM_ENCRYPTIONKEY,
						"密钥信息表缺失，请检查！");
//				throw new MTCException("密钥信息表缺失，请检查！");
			} else {
				// 将读取到的内容放到内存中
				ParamCache.setEncryptionKeys(tempMap);
				
				RateParamErrorUtils.remove(ParamConstant.PARAM_ENCRYPTIONKEY); //清除掉之前的错误信息
				
				isRead = true;
			}
		} catch (Exception e) {
			logger.error("读取密钥信息参数表异常，错误原因：" + e.getMessage(), e);
			RateParamErrorUtils.put(ParamConstant.PARAM_ENCRYPTIONKEY,
					"读取密钥信息参数表异常，错误原因：" + e.getMessage());
		}
		return isRead;
	}

	/**
	 * 将文本记录转换成为密钥信息
	 * 
	 * @param encryptionKey
	 * @return
	 */
	protected EncryptionKey convertToEncryptionKey(String[] encryptionKey) {
		EncryptionKey eKey = new EncryptionKey();
		eKey.setId(IntegerUtils.parseInteger(encryptionKey[0]));
		eKey.setEncryptionKey(encryptionKey[1]);
		eKey.setVersion(IntegerUtils.parseInteger(encryptionKey[2]));
		eKey.setStartTime(parseDate(encryptionKey[3]));
		eKey.setExpireDate(parseDate(encryptionKey[4]));
		eKey.setEncryptionRank(IntegerUtils.parseInteger(encryptionKey[5]));
		eKey.setSpare1(IntegerUtils.parseInteger(encryptionKey[6]));
		eKey.setSpare2(encryptionKey[7]);
		return eKey;
	}

	/**
	 * 读货车超载收费标准表
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean readOverloadPrice() {
		boolean isRead = false;
		try {
			List<String> list = readConfigFile(ParamConstant.PARAM_OVERLOADPRICE);
			OverLoadPrice olp = null;
			String[] overLoadPrice = null;
			if (!list.isEmpty()) {
				ParamVersionUtils
						.clearParamVersion(ParamConstant.PARAM_OVERLOADPRICE);
				for (String string : list) {
					// 读取一行记录
					overLoadPrice = string.split(ParamConstant.TAB_STRING, -1);

					// 正常的编码记录有8个字段，把错误的记录排除掉
					if (overLoadPrice.length == ParamConstant.OVER_LOAD_PRICE_LENGTH) {
						olp = convertOverLoadPrice(overLoadPrice);
						ParamVersionUtils.checkParamVersion(olp,
								ParamConstant.PARAM_OVERLOADPRICE);
						RateParamCacheHelpr.putParamVersion(olp,
								ParamConstant.PARAM_OVERLOADPRICE);
					}
				}
			}

			if (olp == null) {
				RateParamErrorUtils.put(ParamConstant.PARAM_OVERLOADPRICE,
						"货车超载收费标准表缺失，请检查！");
			} else {
				// 费率参数表版本检查
				RateParamCacheHelpr.checkOverLoadVersion();

				// 将读取到的内容放到内存中
				ParamCache.setOverLoadPrices(olp);
				
				RateParamErrorUtils.remove(ParamConstant.PARAM_OVERLOADPRICE);//清除掉之前的错误信息
				
				isRead = true;
			}

		} catch (Exception e) {
			logger.error("读取货车超载收费标准参数表异常，错误原因：" + e.getMessage(), e);
			RateParamErrorUtils.put(ParamConstant.PARAM_OVERLOADPRICE,
					"读取货车超载收费标准参数表异常，错误原因：" + e.getMessage());
		}
		return isRead;
	}

	/**
	 * 将字符串转换成为货车超载收费标准信息
	 * 
	 * @param overLoadPrice
	 * @return
	 */
	protected OverLoadPrice convertOverLoadPrice(String[] overLoadPrice) {
		OverLoadPrice olp = new OverLoadPrice();
		olp.setWeightOverRatio1(parseDouble(overLoadPrice[0]));
		olp.setWeightOverRatio2(parseDouble(overLoadPrice[1]));
		olp.setWeightOverRatio3(parseDouble(overLoadPrice[2]));
		olp.setTimes1(parseDouble(overLoadPrice[3]));
		olp.setTimes2(parseDouble(overLoadPrice[4]));
		olp.setTimes3(parseDouble(overLoadPrice[5]));
		olp.setVersion(IntegerUtils.parseInteger(overLoadPrice[6]));
		olp.setStartTime(parseDate(overLoadPrice[7]));
		return olp;
	}

	/**
	 * 读行程时间表
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean readTravelTime() {
		boolean isRead = false;
		try {
			Vector<TravelTime> tempList = new Vector<TravelTime>();
			List<String> list = readConfigFile(ParamConstant.PARAM_TRAVELTIME);
			TravelTime tt = null;
			String[] travelTime = null;
			if (!list.isEmpty()) {
				ParamVersionUtils
						.clearParamVersion(ParamConstant.PARAM_TRAVELTIME);
				for (String string : list) {
					// 读取一行记录
					travelTime = string.split(ParamConstant.TAB_STRING, -1);

					// 正常的编码记录有10个字段，把错误的记录排除掉
					if (travelTime.length == ParamConstant.TRAVELTIME_LENGTH) {
						tt = convertToTravelTime(travelTime);
						ParamVersionUtils.checkParamVersion(tt,
								ParamConstant.PARAM_TRAVELTIME);
						RateParamCacheHelpr.putParamVersion(tt,
								ParamConstant.PARAM_TRAVELTIME);
						tempList.add(tt);
					}
				}
			}
			if (tempList.isEmpty()) {
				RateParamErrorUtils.put(ParamConstant.PARAM_TRAVELTIME,
						"行程时间参数表缺失，请检查！");
			} else {
				// 费率参数表版本检查
				RateParamCacheHelpr.checkTravelTimeVersion();

				// 将读取到的内容放到内存中
				ParamCache.setTravelTimes(tempList);
				
				RateParamErrorUtils.remove(ParamConstant.PARAM_TRAVELTIME);//清除掉之前的错误信息

				isRead = true;
			}

		} catch (Exception e) {
			logger.error("读取行程时间参数表异常，错误原因：" + e.getMessage(), e);
			RateParamErrorUtils.put(ParamConstant.PARAM_TRAVELTIME,
					"读取行程时间参数表异常，错误原因：" + e.getMessage());
		}
		return isRead;
	}

	/**
	 * 字符串转行驶里程对象
	 *
	 * @param travelTime
	 * @return
	 */
	protected TravelTime convertToTravelTime(String[] travelTime) {
		TravelTime tt = new TravelTime();
		tt.setEnRoadId(IntegerUtils.parseInteger(travelTime[0]));
		tt.setEnStationId(IntegerUtils.parseInteger(travelTime[1]));
		tt.setExRoadId(IntegerUtils.parseInteger(travelTime[2]));
		tt.setExStationId(IntegerUtils.parseInteger(travelTime[3]));
		tt.setVehType(IntegerUtils.parseInteger(travelTime[4]));
		tt.setMileages(parseDouble(travelTime[5]));
		tt.setMinTravelTime(IntegerUtils.parseInteger(travelTime[6]));
		tt.setMaxTravelTime(IntegerUtils.parseInteger(travelTime[7]));
		tt.setVersion(IntegerUtils.parseInteger(travelTime[8]));
		tt.setStartTime(parseDate(travelTime[9]));
		return tt;
	}
	
	/**
	 * 读取公务卡信息
	 *
	 * @throws MTCException
	 */
	public boolean readOfficialCard() {
		boolean isRead = true;
		try {
			Vector<OfficialCard> tempMap = new Vector<OfficialCard>();
			List<String> list = readConfigFile(ParamConstant.PARAM_OFFICIALCARD);
			OfficialCard officialCard = null;
			String[] officialCardStr = null;
			if (!list.isEmpty()) {
				ParamVersionUtils
						.clearParamVersion(ParamConstant.PARAM_OFFICIALCARD);
				for (String string : list) {
					// 读取一行记录
					officialCardStr = string.split(ParamConstant.TAB_STRING, -1);

					// 正常的编码记录有10个字段，把错误的记录排除掉
					if (officialCardStr.length == ParamConstant.OFFICIALCARD_LENGTH) {
						officialCard = convertToOfficialCard(officialCardStr);
						tempMap.add(officialCard);
					}
				}
			}
			if (tempMap.isEmpty()){
				officialCard = new OfficialCard();
//				RateParamErrorUtils.put(ParamConstant.PARAM_OFFICIALCARD,"公务卡参数表缺失，请检查！");
			}else{
				// 将读取到的内容放到内存中
				ParamCache.setOfficialCardMap(tempMap);
				RateParamErrorUtils.remove(ParamConstant.PARAM_OFFICIALCARD);//清除掉之前的错误信息
			}
			ParamVersionUtils.checkParamVersion(officialCard,
					ParamConstant.PARAM_OFFICIALCARD);
			

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			RateParamErrorUtils.put(ParamConstant.PARAM_OFFICIALCARD,
					"读取公务卡参数表异常，错误原因：" + e.getMessage());
			isRead = false;
		}
		return isRead;
	}
	
	/**
	 * 字符串转公务卡对象
	 *
	 * @param officialCard
	 * @return
	 */
	protected OfficialCard convertToOfficialCard(String[] officialCard) {
		OfficialCard s = new OfficialCard();
		s.setOfficilCardNo(officialCard[0]);
		s.setOfficialCardType(IntegerUtils.parseInteger(officialCard[1]));
		s.setStatus(IntegerUtils.parseInteger(officialCard[2]));
		s.setVehPlate(officialCard[3]);
		s.setFreeType(IntegerUtils.parseInteger(officialCard[4]));
		s.setFreeRoadList(officialCard[5]);
		s.setVersion(IntegerUtils.parseInteger(officialCard[6]));
		s.setStartTime(parseDate(officialCard[7]));
		s.setEndTime(parseDate(officialCard[8]));
		s.setSpare(officialCard[9]);
		return s;
	}

	/**
	 * 将字符串转换成为Double
	 *
	 * @param s
	 *            字符串
	 * @return 如果没有则返回null，有则返回对应的数字表示
	 */
	private static Double parseDouble(String s) {
		return StringUtils.isEmpty(s) ? 0.0 : Double.valueOf(s.trim());
	}

	/**
	 * 将字符串转换成为Date类型
	 *
	 * @param s
	 *            字符串
	 * @return 如果没有则返回null，有则返回对应的日期表示
	 */
	private static Date parseDate(String s) {
		return StringUtils.isEmpty(s) ? null : DateUtils.parseDate(s.trim());
	}

	/**
	 * 以行为单位读取表，常用于读面向行的格式化表
	 *
	 * @param fileName
	 *            表名
	 * @throws MTCException
	 */
	public static List<String> readConfigFile(String fileName)
			throws MTCException {
		List<String> tempList = new ArrayList<String>();
		if (StringUtils.isNotEmpty(fileName)) {
			// 找到工程根目录config下的路径
			File file = new File(getParamFilePath(fileName));
			if (file.exists()) {
				tempList.addAll(ReadFromFileUtils
						.readFileByLinesToListSkipTitleList(file,
								FileUtils.encode()));
			}
		}
		return tempList;
	}

	/**
	 * 以行为单位读取表，常用于读面向行的格式化表
	 *
	 * @param fileName
	 *            表名
	 * @throws MTCException
	 */
	public static void readBlackDeltaFile(String fileName, int type)
			throws MTCException {
		if (StringUtils.isNotEmpty(fileName)) {
			File file = new File(getParamFilePath(fileName));
			if (file.exists()) {
				ParamCacheFileRead.readBlackDeltaFile(file, type);
			}
		}
	}

	/**
	 * 读取黑名单增量信息表
	 *
	 * @param file
	 * @return
	 * @throws MTCException
	 */
	private static void readBlackDeltaFile(File file, int type)
			throws MTCException {
		FileInputStream isr = null;
		try {
			isr = new FileInputStream(file);
			// 读取输入流
			if (type == 1) {
				// 全量表截取逻辑
				boolean isEmpty = structure(isr,
						XTCardBlackBaseListByte.bufferSize, "buffer1",
						xtCardBlackBaseListByte);

				if (!isEmpty) {
					isEmpty = structure(isr,
							XTCardBlackBaseListByte.bufferSize, "buffer2",
							xtCardBlackBaseListByte);
				}
				if (!isEmpty) {
					isEmpty = structure(isr,
							XTCardBlackBaseListByte.bufferSize, "buffer3",
							xtCardBlackBaseListByte);
				}
				if (!isEmpty) {
					isEmpty = structure(isr,
							XTCardBlackBaseListByte.bufferSize, "buffer4",
							xtCardBlackBaseListByte);
				}
				if (!isEmpty) {
					isEmpty = structure(isr,
							XTCardBlackBaseListByte.bufferSize, "buffer5",
							xtCardBlackBaseListByte);
				}
				if (!isEmpty) {
					isEmpty = structure(isr,
							XTCardBlackBaseListByte.bufferSize, "buffer6",
							xtCardBlackBaseListByte);
				}
				if (!isEmpty) {
					isEmpty = structure(isr,
							XTCardBlackBaseListByte.bufferSize, "buffer7",
							xtCardBlackBaseListByte);
				}
			} else if (type == 2) {
				// 增量表截取逻辑
				boolean isEmpty = structure(isr,
						XTCardBlackDeltaListByte.bufferSize, "buffer1",
						xtCardBlackDeltaListByte);

				if (!isEmpty) {
					isEmpty = structure(isr,
							XTCardBlackDeltaListByte.bufferSize, "buffer2",
							xtCardBlackDeltaListByte);
				}
				if (!isEmpty) {
					isEmpty = structure(isr,
							XTCardBlackDeltaListByte.bufferSize, "buffer3",
							xtCardBlackDeltaListByte);
				}
				if (!isEmpty) {
					isEmpty = structure(isr,
							XTCardBlackDeltaListByte.bufferSize, "buffer4",
							xtCardBlackDeltaListByte);
				}
				if (!isEmpty) {
					isEmpty = structure(isr,
							XTCardBlackDeltaListByte.bufferSize, "buffer5",
							xtCardBlackDeltaListByte);
				}
				if (!isEmpty) {
					isEmpty = structure(isr,
							XTCardBlackDeltaListByte.bufferSize, "buffer6",
							xtCardBlackDeltaListByte);
				}
				if (!isEmpty) {
					isEmpty = structure(isr,
							XTCardBlackDeltaListByte.bufferSize, "buffer7",
							xtCardBlackDeltaListByte);
				}
			}

			isr.close();

		} catch (IOException e) {
			throw new MTCException(e);
		}  finally {
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e1) {
				}
			}
			System.gc();
		}
	}

	/**
	 * 初始化数据
	 * 
	 * @param content
	 *            表内容字节数
	 * @param isr
	 * @return
	 * @throws IOException
	 */
	private static boolean structure(FileInputStream isr, int bufferSize,
			String fieldName, Object obj) throws IOException {
		boolean isEmpty = false;
		byte[] content = new byte[bufferSize];
		String mode = FileUtils.getDataEncodeMode();
		if (mode.equalsIgnoreCase("1")) { // 广东版本的参数需要跳过文件头
			if (fieldName.equalsIgnoreCase("buffer1")) { // 黑名单特殊处理，跳过文件头
				if (obj instanceof XTCardBlackBaseListByte) {
					isr.skip(13); // 跳过表头
				} else if (obj instanceof XTCardBlackDeltaListByte) {
					isr.skip(18); // 跳过文件头
				}
			}
		}

		int len = isr.read(content);

		if (len == -1) { // 字节数读取完毕
			isEmpty = true;
		} else {
			ReflectionUtils.setFieldValue(obj, fieldName, content);
		}
		return isEmpty;
	}

	/**
	 * 获取参数表的路径
	 *
	 * @param fileName
	 *            表名
	 * @return
	 */
	public static String getParamFilePath(String fileName) {
		return getParamFoldPath() + fileName + BaseConstant.POINT_STRING
				+ BaseConstant.FILE_POSTFIX_TXT;
	}

	/**
	 * 获取参数表的路径
	 *
	 * @return
	 */
	private static String getParamFoldPath() {
		return FileUtils.getRootPath() + "/param/";
	}

//	/**
//	 * 全量更新
//	 * 
//	 * @return
//	 *
//	 * @throws MTCException
//	 */
//	public boolean fullData() {
//		Vector<String> param = new Vector<String>();
//		param.add(ParamConstant.PARAM_OPERATOR);
//		param.add(ParamConstant.PARAM_BWG_LIST);
//		boolean readNotCheckEnabledTimeData = initNotCheckEnabledTimeData(param);
//		
//		param.add(ParamConstant.PARAM_STATION);
//		param.add(ParamConstant.PARAM_ISSUER);
//		param.add(ParamConstant.PARAM_USERRATE);
//		param.add(ParamConstant.PARAM_ENCRYPTIONKEY);
//		boolean readCheckEnabledTimeData = initCheckEnabledTimeData(param);
//		
//		return readNotCheckEnabledTimeData && readCheckEnabledTimeData;
//	}

	/**
	 * 不需要判断启用时间的参数表
	 * 
	 * @return
	 * @throws MTCException
	 */
	public boolean initNotCheckEnabledTimeData(Vector<String> newParam) {
		// 直接覆盖之前的缓存信息
		// readLane();
		boolean isRead = true;
		if(newParam.contains(ParamConstant.PARAM_OPERATOR)){
			boolean readOperator = readOperator();
			isRead = isRead && readOperator;
		}
		if(newParam.contains(ParamConstant.PARAM_BWG_LIST)){
			boolean readBwgList = readBwgList();
			isRead = isRead && readBwgList;
		}
		return isRead;
	}

	/**
	 * 需要判断启用时间的参数表
	 * @param otherRecvFileExistsEnabled 
	 * 
	 * @return
	 * @throws MTCException
	 */
	public boolean initCheckEnabledTimeData(Vector<String> param) {
		boolean isRead = true;
		if(param.contains(ParamConstant.PARAM_ISSUER)){
			boolean readIssuser = readIssuer();
			isRead = isRead && readIssuser;
		}
		if(param.contains(ParamConstant.PARAM_USERRATE)){
			boolean readUserRate = readUserRate();
			isRead = isRead && readUserRate;
		}
		if(param.contains(ParamConstant.PARAM_STATION)){
			boolean readStation = readStation();
			isRead = isRead && readStation;
		}
		if(param.contains(ParamConstant.PARAM_ENCRYPTIONKEY)){
			boolean readEncryptionKey = readEncryptionKey();
			isRead = isRead && readEncryptionKey;
		}
		if(param.contains(ParamConstant.PARAM_OFFICIALCARD)){
			boolean readfficialCard = readOfficialCard();
			isRead = isRead && readfficialCard;
		}
		// readDiscountInfo();
		return isRead;
	}

	/**
	 * 增量更新的数据
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean initIncrementData(Vector<String> newParam) {
		boolean isRead = true;
		if(etcCardBlackControl.isOptimisedActive()){
			if(newParam.contains(ParamConstant.PARAM_XTCARD_BLACK_BASE_LIST)){
				String tableName = ParamConstant.PARAM_XTCARD_BLACK_BASE_LIST;
				etcCardBlackControl.checkFileFormatVersion(ParamConstant.ALL_CARD_BLACK_TYPE, tableName+".txt");
				if(!FileUtils.isExist(ETCCardBlackControl.etcCardBlackAllDB)){
					//如果不存在sqlite文件的话，则进行装载黑名单数据
	        		/*
	        		 * 将文本文件转成sqlite数据文件
	        		 */
	        		boolean isReplace = etcCardBlackControl.convertToSqlite(tableName+".txt",ParamConstant.ALL_CARD_BLACK_TYPE,tableName);
	        		if(isReplace){
	        			etcCardBlackControl.replace(ETCCardBlackControl.etcCardBlackAllTempDB, ETCCardBlackControl.etcCardBlackAllDB);//替换成正式文件
	        		}
				}
				boolean readXtCardBlackBaseList = readXtCardBlackBaseList(); // 读取基础数据
				isRead = isRead && readXtCardBlackBaseList;
			}
			if(newParam.contains(ParamConstant.PARAM_XTCARD_BLACK_DELTA_LIST)){
				String tableName = ParamConstant.PARAM_XTCARD_BLACK_DELTA_LIST;
				etcCardBlackControl.checkFileFormatVersion(ParamConstant.DELTA_CARD_BLACK_TYPE, tableName+".txt");
				if(!FileUtils.isExist(ETCCardBlackControl.etcCardBlackAllDB)){
					//如果不存在sqlite文件的话，则进行装载黑名单数据
	        		/*
	        		 * 将文本文件转成sqlite数据文件
	        		 */
	        		boolean isReplace = etcCardBlackControl.convertToSqlite(tableName+".txt",ParamConstant.DELTA_CARD_BLACK_TYPE,tableName);
	        		if(isReplace){
	        			etcCardBlackControl.replace(ETCCardBlackControl.etcCardBlackDeltaTempDB, ETCCardBlackControl.etcCardBlackDeltaDB);//替换成正式文件
	        		}
				}
				boolean readXtCardBlackIncrementList = readXtCardBlackIncrementList(); // 读取增量数据
				isRead = isRead && readXtCardBlackIncrementList;
			}
		}else{
			if(newParam.contains(ParamConstant.PARAM_XTCARD_BLACK_BASE_LIST)){
				logger.debug("开始清理ETC卡黑名单基础缓存");
				XTCardBlackListByte.clean();
				XTCardBlackBaseListByte.clean();
				logger.debug("清理ETC卡黑名单缓存结束");
				logger.debug("开始读取ETC卡黑名单基础");
				boolean readXtCardBlackBaseList = readXtCardBlackBaseList(); // 读取增量基础数据
				isRead = isRead && readXtCardBlackBaseList;
				logger.debug("读取ETC卡黑名单结束");
			}
			if(newParam.contains(ParamConstant.PARAM_XTCARD_BLACK_DELTA_LIST)){
				logger.debug("开始清理ETC卡黑名单增量缓存");
				XTCardBlackDeltaListByte.clean(); 
				logger.debug("清理ETC卡黑名单缓存结束");
				logger.debug("开始读取ETC卡黑名单增量");
				boolean readXtCardBlackIncrementList = readXtCardBlackIncrementList(); // 读取增量数据
				isRead = isRead && readXtCardBlackIncrementList;
				logger.debug("读取ETC卡黑名单结束");
			}
		}
		
		return isRead;
	}

	/**
	 * 注销才更新的
	 * 
	 * @return
	 *
	 * @throws MTCException
	 */
	public boolean cancelData() {
		if (laneType == 2) { // 只有车道类型等于出口的时候，才加载收费信息
			boolean readPrice = readPrice();
			boolean readRoadChargeStd = readRoadChargeStd();
			boolean readBridgeExtraChargeStd = readBridgeExtraChargeStd();
			boolean readTravelTime = readTravelTime();
			boolean readOverloadPrice = readOverloadPrice();
			if (RateParamCacheHelpr.version == null) { // 如果费率版本不存在，则提示用户
				RateParamErrorUtils.put(RateError,
						"费率版本存在问题，请检查费率相关版本文件是否正常");
				// throw new MTCException("费率版本存在问题，请检查费率相关版本文件是否正常");
				return false;
			}else{
				RateParamErrorUtils.remove(RateError);
			}
			RateParamCacheHelpr.clearAllParamVersion(); // 清除缓存中的版本信息
			return readPrice && readRoadChargeStd && readBridgeExtraChargeStd
					&& readTravelTime && readOverloadPrice;
		}
		NEW_RATE_VALID = false; // 重置新费率版本状态
		return true;
	}

	/**
	 * 初始化数据表结构
	 *
	 * @throws MTCException
	 * @throws IOException
	 */
	public boolean initData() throws Exception {
		
		Vector<String> param = new Vector<String>();
		param.add(ParamConstant.PARAM_OPERATOR);
		param.add(ParamConstant.PARAM_BWG_LIST);
		param.add(ParamConstant.PARAM_STATION);
		param.add(ParamConstant.PARAM_ISSUER);
		param.add(ParamConstant.PARAM_USERRATE);
		param.add(ParamConstant.PARAM_ENCRYPTIONKEY);
		param.add(ParamConstant.PARAM_XTCARD_BLACK_BASE_LIST);
		param.add(ParamConstant.PARAM_XTCARD_BLACK_DELTA_LIST);
		param.add(ParamConstant.PARAM_OFFICIALCARD);
		final int size = param.size() - 3; //基本参数个数，去除车牌黑名单，etc卡黑名单增量表、公务卡数据可以为空的情况
		Vector<String> returnParam = rpu.refreshOther(); //新参数检查。
		boolean otherParamSize = false;
		if(returnParam.size() >= size || returnParam.size() >= 0){
			otherParamSize = true;
		}
		boolean readNotCheckEnabledTimeData = initNotCheckEnabledTimeData(param);
		boolean readCheckEnabledTimeData = initCheckEnabledTimeData(param);
		boolean readIncrement = initIncrementData(param);

		
		boolean readPriceData = false;
		boolean isRefreshPrice = rpu.refreshPrice();// 新费率检查。
		if (isRefreshPrice) { // 如果加载过了费率，则不需要重新加载
			readPriceData = Boolean.TRUE;
		} else {
			readPriceData = cancelData();
		}
		
		/**
		 * 如果加载费率成功后,为启用时间和版本号设置值
		 * (不获取启用时间，直接用当前时间替代，因为费率能够加载到，肯定是已经启用的。)
		 */
		if (readPriceData) {
			if (RecvParamUtils.getRateStartTimeAndVersion() == "") { // 没有新版本费率的时候，需要初始化一个默认值
				RecvParamUtils.setRateStartTimeAndVersion(DateUtils
						.formatDateToString(DateUtils.getCurrentDate(),
								DateConstant.DATE_FORMAT_YYYYMMDDHHMMSS)
						+ "_" + "-1");
			}
		}
		
		LaneListUtils.genLaneParaVer();
		System.gc();
		return readPriceData && readNotCheckEnabledTimeData
				&& readCheckEnabledTimeData && readIncrement && otherParamSize;
	}

	public static void main(String[] args) {
		try {
			DOMConfigurator
					.configure("resource" + File.separator + "log4j.xml");

			List<String> list = new ArrayList<String>();
			list.add(Constant.PROP_MTCCONFIG);
			list.add(Constant.PROP_MTCLANE);
			list.add(Constant.PROP_MTCLANE_COMM);
			list.add(Constant.PROP_MTCLANE_CONSTANT);
			list.add(Constant.PROP_MTCLANE_ETC);
			list.add(Constant.PROP_MTCLANE_FUNCTION);
			list.add(Constant.PROP_MTCLANE_LPR);
			list.add(Constant.PROP_MTCLANE_RTP);
			list.add(Constant.PROP_MTCLANE_SERVER);
			list.add(Constant.PROP_MTCLANE_TEST);
			list.add(Constant.PROP_SOCKET);
			MyPropertiesUtils.loadProperties(list);
			ParamCacheFileRead pcfru = new ParamCacheFileRead();
			pcfru.initData();
			// XTCardBlackList xtCardBlack =
			// ParamCacheQuery.queryXTCardBlackList("43011426234301182763");
			// //
			// System.out.println("共花费"+(System.currentTimeMillis()-startTime)
			// +"毫秒来查找");
			// if (xtCardBlack == null) {
			// System.out.println("不是黑名单");
			// } else {
			// System.out.println("是黑名单");
			// }
			VehPlateBWG a = ParamCacheQuery.queryBWG("湘A12345");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isLoadParam() {
		return isLoadParam;
	}

	public void setLoadParam(boolean isLoadParam) {
		this.isLoadParam = isLoadParam;
	}

}