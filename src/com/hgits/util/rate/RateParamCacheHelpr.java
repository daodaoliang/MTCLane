/**
 * 
 */
package com.hgits.util.rate;

import java.util.Date;
import java.util.Map;

import com.hgits.exception.MTCException;
import com.hgits.service.constant.ParamConstant;
import com.hgits.util.DateUtils;
import com.hgits.util.ReflectionUtils;
import com.hgits.vo.Version;

/**
 * 费率参数表缓存工具类
 * @author Administrator
 *
 */
public class RateParamCacheHelpr {

	public static String version; //当前费率表版本
	
	/**
	 * 费率参数表版本检查
	 * @return
	 * @throws MTCException
	 */
	public static boolean checkRateVersion() throws MTCException{
		 //检查费率版本
        for (Integer vehType : ParamCacheFileRead.vehRoutes) { //遍历1-7型车费率
        	Version tempVector = getRateParamVersion(ParamConstant.PARAM_VEHROUTE + vehType);
        	if(version == null){
        		version = tempVector.getVersion();
        	}
        	if(!version.equalsIgnoreCase(tempVector.getVersion())){
        		RateParamErrorUtils.put(ParamConstant.PARAM_VEHROUTE + vehType, ParamConstant.PARAM_VEHROUTE + vehType + " 表版本，与费率版本不一致!");
        		throw new MTCException(ParamConstant.PARAM_VEHROUTE + vehType + " 表版本，与费率版本不一致!");
        	}
		}
        
        return true;
	}
	
	/**
	 * 超载率参数表版本检查
	 * @return
	 * @throws MTCException
	 */
	public static boolean checkOverLoadVersion() throws MTCException{
		 //检查费率版本
		Version tempVector = getRateParamVersion(ParamConstant.PARAM_OVERLOADPRICE);
    	if(version == null){
    		version = tempVector.getVersion();
    	}
    	if(!version.equalsIgnoreCase(tempVector.getVersion())){
    		RateParamErrorUtils.put(ParamConstant.PARAM_OVERLOADPRICE, "货车超载收费标准表版本，与费率版本不一致!");
    		throw new MTCException("货车超载收费标准表版本，与费率版本不一致!");
    	}
        
        return true;
	}
	
	/**
	 * 路段叠加收费参数表版本检查
	 * @return
	 * @throws MTCException
	 */
	public static boolean checkRoadChargeSTDVersion() throws MTCException{
		 //检查费率版本
		Version tempVector = getRateParamVersion(ParamConstant.PARAM_ROADCHARGESTD);
    	if(version == null){
    		version = tempVector.getVersion();
    	}
    	if(!version.equalsIgnoreCase(tempVector.getVersion())){
    		RateParamErrorUtils.put(ParamConstant.PARAM_ROADCHARGESTD, "路段叠加收费标准表版本，与费率版本不一致!");
    		throw new MTCException("路段叠加收费标准表版本，与费率版本不一致!");
    	}
        
        return true;
	}
	
	/**
	 * 行程时间参数表版本检查
	 * @return
	 * @throws MTCException
	 */
	public static boolean checkTravelTimeVersion() throws MTCException{
		 //检查费率版本
		Version tempVector = getRateParamVersion(ParamConstant.PARAM_TRAVELTIME);
    	if(version == null){
    		version = tempVector.getVersion();
    	}
    	if(!version.equalsIgnoreCase(tempVector.getVersion())){
    		RateParamErrorUtils.put(ParamConstant.PARAM_TRAVELTIME, "行程时间参数表版本，与费率版本不一致!");
    		throw new MTCException("行程时间参数表版本，与费率版本不一致!");
    	}
        
        return true;
	}
	
	/**
	 * 桥隧叠加收费参数表版本检查
	 * @return
	 * @throws MTCException
	 */
	public static boolean checkBridgeExtraChargeSTDVersion() throws MTCException{
		 //检查费率版本
		Version tempVector = getRateParamVersion(ParamConstant.PARAM_BRIDGE_EXTRA_CHARGE_STD);
    	if(version == null){
    		version = tempVector.getVersion();
    	}
    	if(!version.equalsIgnoreCase(tempVector.getVersion())){
    		RateParamErrorUtils.put(ParamConstant.PARAM_BRIDGE_EXTRA_CHARGE_STD, "桥隧叠加收费标准表版本，与费率版本不一致!");
    		throw new MTCException("桥隧叠加收费标准表版本，与费率版本不一致!");
    	}
        
        return true;
	}
	
	/**
	 * 获取参数表版本
	 * 
	 * @param field
	 *            参数表版本名
	 * @return 
	 */
	public static synchronized Version getRateParamVersion(String field) {
		Map<String, Version> versions = ParamCache.getRateVersionMap();
		Version temp = versions.get(field); //获取版本集合
		return temp;
	}
	
	/**
	 * 缓存参数表版本信息
	 * 
	 * @param entry
	 *            对象
	 * @throws MTCException 
	 */
	public static synchronized void putParamVersion(Object entry,String field) throws MTCException {
		Map<String, Version> versions = ParamCache.getRateVersionMap();
		Version temp = null;
		
		Object versionObj = ReflectionUtils.invokeGetterMethod(entry, "version");
		Object startTimeObj = ReflectionUtils.invokeGetterMethod(entry, "startTime");
		String version = null; //版本
		Version v = new Version();
		Date startTime = null;
		boolean isNewVersion = false; //是否为新版本
		
		if (versionObj != null) {
			if(versionObj instanceof Integer){ //除了车道版本外，其他的参数版本为数字类型，所以需要特殊处理一下
				version = String.valueOf((Integer) versionObj);
			}else{
				version = (String) versionObj;
			}
		}
		if(startTimeObj != null){
			startTime = (Date) startTimeObj;
		}
		
		temp = versions.get(field); //获取版本集合
		if (temp == null) { // 集合中如果还没有数据，则直接添加
			v.setVersion(version);
			v.setStartDate(startTime);
			v.setGenerateDate(DateUtils.getCurrentDate());
			isNewVersion = true;
		} else {
			// 考虑到版本号相同的话，启用时间肯定是相同的，这里只对比版本号
			if (!checkVersionExists(temp, version)) {
				//出现不同版本号，提示用户
				throw new MTCException(field+"表版本，与费率版本不一致!");
			}
		}
		if(isNewVersion){
			//只有新版本的费率才进入
			versions.put(field, v);
		}
	}
	
	
	/**
	 * 清除指定的参数表版本
	 * 
	 * @param field
	 *            参数表版本名
	 */
	public static synchronized void clearParamVersion(String field) {
		Map<String, Version> versions = ParamCache.getRateVersionMap();
		Version temp = versions.get(field); //获取版本集合
		
		if (temp == null) { // 集合中如果还没有数据，则直接添加
			return;
		}
		versions.remove(field);
	}
	
	/**
	 * 清除所有的参数表版本
	 */
	public static synchronized void clearAllParamVersion() {
		version = null; //将版本信息重置
		Map<String, Version> versions = ParamCache.getRateVersionMap();
		if (versions == null) { // 集合中如果还没有数据，则直接添加
			return;
		}
		versions.clear();
	}

	/**
	 * 检查版本号是否存在
	 * 
	 * @param cacheVersions 参数版本集合
	 * @param version 当前的参数版本
	 * @return
	 */
	private static boolean checkVersionExists(Version cacheVersions,
			String version) {
		boolean isExists = false;
		// 考虑到版本号相同的话，启用时间肯定是相同的，这里只对比版本号
		if (cacheVersions.getVersion().equalsIgnoreCase(version)) {
			isExists = true;
		}
		return isExists;
	}
}
