package com.hgits.util;

import java.util.Vector;

/**
 * 硬件异常缓存工具类
 *
 * @author Administrator
 *
 */
public class DeviceErrorCacheUtils {

    private static int MAX_SIZE = 100;

	/**
	 * 硬件异常集合
	 */
	public static Vector<Object> errors = new Vector<>(MAX_SIZE, 10);

	/**
	 * 添加元素
	 *
	 * @param device
	 *            设备
	 * @param errorMsg
	 *            错误描述
	 */
	public synchronized static void put(String device, String errorMsg) {
		Vector<Object> oneRow = new Vector<Object>();
		oneRow.add(DateUtils.formatDateToString(DateUtils.getCurrentDate()));
		if (errors.size() >= MAX_SIZE) {
			errors.removeElementAt(0); // 当元素超过最大限制，则将第一个元素移除，保证集合中只有MAX_SIZE个元素
		}
		oneRow.add(device);
		oneRow.add(errorMsg);
		errors.add(oneRow);
	}

	/**
	 * 移除异常信息
	 * 
	 * @param device
	 * @param error
	 * @param cacheError
	 */
	public synchronized static void remove(String device, String error,
			String cacheError) {
		if (StringUtils.isEmptyTrim(error)
				&& !StringUtils.isEmptyTrim(cacheError)) {// 硬件无异常，但缓存中有异常表示异常已恢复
			Object [] temp = null;
			Vector<Object> oneRow = null;
			/**
			 * 硬件异常集合
			 */
			Vector<Object> tempErrors = new Vector<>(MAX_SIZE, 10);
			for (Object object : errors) {
				temp = ((Vector)object).toArray();
				if(!temp[1].equals(device)){
					if(!temp[2].equals(cacheError)){ //删除缓存中的异常
						oneRow = new Vector<Object>();
						oneRow.add(temp[0]);
						oneRow.add(temp[1]);
						oneRow.add(temp[2]);
						tempErrors.add(oneRow);
					}
				}
			}
			DeviceErrorCacheUtils.errors.clear();
			DeviceErrorCacheUtils.errors.addAll(tempErrors);
		}
	}

}
