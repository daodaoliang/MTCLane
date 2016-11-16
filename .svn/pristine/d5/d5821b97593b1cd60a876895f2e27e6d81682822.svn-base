/**
 * 
 */
package com.hgits.util.rate;

import java.util.Vector;

import com.hgits.util.DateUtils;

/**
 * 新版本参数文件异常工具
 * @author Administrator
 *
 */
public class RateParamErrorUtils {
	private static int MAX_SIZE = 30;

	/**
	 * 参数文件读取异常集合
	 */
	private static Vector<Object> errors = new Vector<>(MAX_SIZE, 5);

	/**
	 * 添加元素
	 *
	 * @param paramName
	 *            参数名称
	 * @param errorMsg
	 *            错误描述
	 */
	public synchronized static void put(String paramName, String errorMsg) {
		remove(paramName); //首先移除相同级别的错误信息，只保留最后一次的异常信息。
		Vector<Object> oneRow = new Vector<Object>();
		oneRow.add(DateUtils.formatDateToString(DateUtils.getCurrentDate()));
		if (errors.size() >= MAX_SIZE) {
			errors.removeElementAt(0); // 当元素超过最大限制，则将第一个元素移除，保证集合中只有MAX_SIZE个元素
		}
		oneRow.add(paramName);
		oneRow.add(errorMsg);
		errors.add(oneRow);
	}

	/**
	 * 移除异常信息
	 * @param paramName
	 */
	public synchronized static void remove(String paramName) {
		Object[] temp = null;
		Vector<Object> oneRow = null;
		/**
		 * 费率参数文件读取异常集合
		 */
		Vector<Object> tempErrors = new Vector<>(MAX_SIZE, 10);
		for (Object object : errors) {
			temp = ((Vector<?>) object).toArray();
			if (!temp[1].equals(paramName)) {
				oneRow = new Vector<Object>();
				oneRow.add(temp[0]);
				oneRow.add(temp[1]);
				oneRow.add(temp[2]);
				tempErrors.add(oneRow);
			}
		}
		errors.clear();
		errors.addAll(tempErrors);
	}
	
	public static Vector<Object> getErrors() {
		return errors;
	}

//	/**
//	 * 移除缓存中的所有异常信息
//	 */
//	public static void removeAll() {
//		errors.clear();
//	}

//	public static void main(String[] args) {
//		for (int i = 0; i < 20; i++) {
//			put("Test" + i, "描述" + i);
//		}
//
//		for (Object string : errors) {
//			System.out.println(string);
//		}
//		System.out.println(errors.size());
//
//		remove("Test2");
//		remove("Test4");
//
//		for (Object string : errors) {
//			System.out.println(string);
//		}
//		System.out.println(errors.size());
//	}
}
