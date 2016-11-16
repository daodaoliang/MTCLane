/**
 * 
 */
package com.hgits.util.rate;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.hgits.util.DateUtils;
import com.hgits.util.ReflectionUtils;
import com.hgits.vo.Version;

/**
 * 检查参数版本号
 * 
 * @author wh
 *
 */
public class ParamVersionUtils {
	
	/**
	 * 检查参数文件版本
	 * 
	 * @param entry
	 *            对象
	 * @param field
	 *            参数版本键
	 */
	public static synchronized void checkParamVersion(Object entry,String field) {
		Map<String, Version> versions = ParamCache.getCurrentVersionMap();
		Version temp = null;
		
		Object versionObj = ReflectionUtils.invokeGetterMethod(entry, "version");
		Object startTimeObj = ReflectionUtils.invokeGetterMethod(entry, "startTime");
		String version = null; //版本
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
			temp = new Version();
			temp.setVersion(version);
			temp.setStartDate(startTime);
			temp.setGenerateDate(DateUtils.getCurrentDate());
			isNewVersion = true;
		} else {
			// 考虑到版本号相同的话，启用时间肯定是相同的，这里只对比版本号
			if (!checkVersionExists(temp, version)) {
				temp.setVersion(version);
				temp.setStartDate(startTime);
				temp.setGenerateDate(DateUtils.getCurrentDate());
				isNewVersion = true;
			}
		}
		if(isNewVersion){
			//只有新版本的费率才进入
			versions.put(field, temp);
		}
	}
	
	
	/**
	 * 清除之前的参数文件版本
	 * 
	 * @param field
	 *            参数文件版本名
	 */
	public static synchronized void clearParamVersion(String field) {
		Map<String, Version> version = ParamCache.getCurrentVersionMap();
		Version temp = version.get(field); //获取版本集合
		
		if (temp == null) { // 集合中如果还没有数据，则直接添加
			return;
		} else {
			version.remove(field);
		}
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
		if (cacheVersions.getVersion().equalsIgnoreCase(version)) {
			isExists = true;
		}
		return isExists;
	}
	
	/**
	 * 检查参数文件版本
	 * 
	 * @param entry
	 *            对象
	 * @param field
	 *            参数版本键
	 */
	public static synchronized void checkFutureParamVersion(Object entry,
			String field) {
		if (entry != null) {
			Map<String, Version> versions = ParamCache.getFutureVersionMap();
			Version temp = null;

			Object versionObj = ReflectionUtils.invokeGetterMethod(entry,
					"version");
			Object startTimeObj = ReflectionUtils.invokeGetterMethod(entry,
					"startTime");
			String version = null; // 版本
			Date startTime = null;
			boolean isNewVersion = false; // 是否为新版本

			if (versionObj != null) {
				if (versionObj instanceof Integer) { // 除了车道版本外，其他的参数版本为数字类型，所以需要特殊处理一下
					version = String.valueOf((Integer) versionObj);
				} else {
					version = (String) versionObj;
				}
			}
			if (startTimeObj != null) {
				startTime = (Date) startTimeObj;
			}

			temp = versions.get(field); // 获取版本集合
			if (temp == null) { // 集合中如果还没有数据，则直接添加
				temp = new Version();
				temp.setVersion(version);
				temp.setStartDate(startTime);
				temp.setGenerateDate(DateUtils.getCurrentDate());
				isNewVersion = true;
			} else {
				// 考虑到版本号相同的话，启用时间肯定是相同的，这里只对比版本号
				if (!checkVersionExists(temp, version)) {
					temp.setVersion(version);
					temp.setStartDate(startTime);
					temp.setGenerateDate(DateUtils.getCurrentDate());
					isNewVersion = true;
				}
			}
			if (isNewVersion) {
				// 只有新版本的费率才进入
				versions.put(field, temp);
			}
		}
	}
	
	
	/**
	 * 清除之前的历史参数文件版本
	 * 
	 * @param field
	 *            参数文件版本名
	 */
	public static synchronized void clearFutureParamVersion(String field) {
		Map<String, Version> version = ParamCache.getFutureVersionMap();
		Version temp = version.get(field); //获取版本集合
		
		if (temp == null) { // 集合中如果还没有数据，则直接添加
			return;
		} else {
			version.remove(field);
		}
	}
	
	/**
	 * 最新版本的缓存更新到显示版本缓存中
	 * @param orgVersion 当前原始版本缓存
	 * @param displayVersion 显示版本缓存
	 */
	public static synchronized void replaceDisplayVersion(
			Map<String, Version> orgVersion, Map<String, Version> displayVersion) {
		if (!orgVersion.isEmpty()) {
			Set<Entry<String, Version>> orgVersionSet = orgVersion.entrySet();
			for (Entry<String, Version> entry : orgVersionSet) {
				if (displayVersion.containsKey(entry.getKey())) { //存在相同的数据，则替换
					displayVersion.remove(entry.getKey());
				}
				displayVersion.put(entry.getKey(), entry.getValue());
			}
		}
	}
}
