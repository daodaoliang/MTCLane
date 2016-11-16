/**
 * 
 */
package com.hgits.util.rate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.hgits.exception.MTCException;
import com.hgits.service.constant.DateConstant;
import com.hgits.service.constant.MtcConstant;
import com.hgits.util.DateUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.file.FileUtils;
import com.hgits.vo.Constant;

/**
 * 多版本费率工具类
 * 
 * @author Administrator
 *
 */
public class MultiRateFileUtils {
	
	private static Logger _log = Logger.getLogger(MultiRateFileUtils.class);
	/**
	 * 车道类型，默认为入口
	 */
	private Integer laneType = Integer.valueOf(MyPropertiesUtils.getProperties(
			Constant.PROP_MTCCONFIG, MtcConstant.PROPERTIES_LANETYPE,
			MtcConstant.MTC_LANE_TYPE_EN));
	
	/**
	 * 存储所有的多版本费率集合
	 */
	private static Map<String, List<String>> multiRateMap = null;
	
	/**
	 * 最后一次替换的费率启用时间
	 */
	private static String replaceMultiRateStartTime = null;

	/**
	 * 检查是否存在新版本的费率相关表
	 *
	 * @return 存在返回true，不存在返回false
	 * @throws MTCException
	 */
	public void checkMultiPriceRecvFile() throws MTCException {
		if (isMultiRateRecvFileExists()) {
			replaceMultiRecvFile();
		}
	}

	/**
	 * 检查费率参数表是否存在新的版本 需要所有的表全部有下发，才算是完整的费率
	 *
	 * @return
	 * @throws MTCException
	 */
	private boolean isMultiRateRecvFileExists() throws MTCException {
		boolean isExists = false;

		if (laneType == 2) { // 只有车道类型等于出口的时候，才加载费率信息
			multiRateMap = getAllMultiRateFile();
			if (!multiRateMap.isEmpty()) {
				isExists = true;
			}
		}
		return isExists;
	}

	/**
	 * 替换多版本费率
	 *
	 * @return
	 * @throws MTCException
	 */
	private synchronized void replaceMultiRecvFile() throws MTCException {

		final int rateRecvFileSize = 10; // 费率相关文件总数

//		Map<String, List<String>> map = getAllMultiRateFile();
		if (multiRateMap.isEmpty()) {//如果没有需要处理的数据，则退出流程
			return;
		} else {
			List<String> startTimeList = new ArrayList<String>();
			for (Entry<String, List<String>> entry : multiRateMap.entrySet()) {
				if (entry.getValue().size() == rateRecvFileSize) { // 只有费率文件个数完整的才会对参数进行处理
					startTimeList.add(entry.getKey());
				}
			}

			if (startTimeList.isEmpty()) { // 如果没有需要遍历的多版本费率
				return;
			} else {
				Collections.sort(startTimeList); // 对集合进行排序
				String rateStartTimeStr = RecvParamUtils.getRateStartTimeAndVersion();
				// 对未来启用的版本进行比较和转换
				String[] str = rateStartTimeStr.split("_");
				String startTimeStr = str[0];
				Date rateStartTime = DateUtils.parseDate(startTimeStr,
						DateConstant.DATE_FORMAT_YYYYMMDDHHMMSS);
				Date nowTime = DateUtils.getCurrentDate(); //系统当前时间
				if (rateStartTime == null) {
					rateStartTime = nowTime;
				}
				String rateVersion = "-1"; // 是否从Recv记录中获取版本号。
				if (str.length == 2) {
					rateVersion = str[1];
				}
				if(!rateVersion.equals("-1")){ //存在新版本费率的话
					replaceMultiRateStartTime = str[0];
				}
				Date multiStartTime = null;
				Date lastReplaceMultiRateStartTime = DateUtils.parseDate(
						replaceMultiRateStartTime,
						DateConstant.DATE_FORMAT_YYYYMMDDHHMMSS);
				boolean isEqualsStartTime = false; //启用时间是否相同
				boolean isFirstReplace =  false; //是否为第一次替换文件
				boolean isVaildStartTime = false;//对多版本的启用时间进行判断
				for (String string : startTimeList) {
					multiStartTime = DateUtils.parseDate(string,
							DateConstant.DATE_FORMAT_YYYYMMDDHHMMSS);
					/**
					 * 多版本费率启用时间在当前时间之后，且启用时间在新版本启用时间之前
					 * 多版本费率的启用时间在新版本费率启用时间之前
					 * 启用时间相等的参数也要进行替换，防止出现版本号不同，启用时间相同的费率参数文件
					 */
				
					if(multiStartTime.equals(rateStartTime)){
						isEqualsStartTime = true;
					}
					isFirstReplace = (lastReplaceMultiRateStartTime == null && multiStartTime.after(nowTime));
					isVaildStartTime = (lastReplaceMultiRateStartTime != null && multiStartTime.after(nowTime) && (multiStartTime.before(lastReplaceMultiRateStartTime) || multiStartTime.before(rateStartTime)));
					if (isFirstReplace || isVaildStartTime || isEqualsStartTime) {
						// 将未来版本的文件名修改，同时把多版本参数修改为未来版本。
						List<String> list = multiRateMap.get(string);
						String newRecvFileName = null;
						String orgRecvFileName = null;
						String recvFileName = null;
						String version = "1";
						if(!list.isEmpty()){
							String tempFileName = list.get(0); //取出第一个文件名
							String[] temp = tempFileName.split("_");
							if(temp.length >= 2){
								version = temp[1]; //取出文件中记录的版本号。
							}
						}
						for (String modifyFileName : list) {
							recvFileName = modifyFileName
									.substring(modifyFileName.indexOf("tb_"));
							newRecvFileName = DateUtils.formatDateToString(
									rateStartTime,
									DateConstant.DATE_FORMAT_YYYYMMDDHHMMSS)
									+ "_" + rateVersion + "_" + recvFileName;
							if(isEqualsStartTime){ //如果是相同启用时间的费率，则修改为old避免重复检查
								newRecvFileName = newRecvFileName+"old";
							}
							orgRecvFileName = recvFileName;
							if (FileUtils.isExist(getParamFoldPath()
									+ orgRecvFileName)) {
								FileUtils.rename(getParamFoldPath()
										+ orgRecvFileName, getParamFoldPath()
										+ newRecvFileName);// 把Recv文件修改为多版本参数

								_log.debug("已将" + recvFileName + "文件转换成"
										+ newRecvFileName);
							} else {
								_log.debug(orgRecvFileName + "文件不存在，不进行重命名操作");
							}

							FileUtils.rename(getParamFoldPath()
									+ modifyFileName, getParamFoldPath()
									+ orgRecvFileName);// 把多版本参数转成Recv
							_log.debug("将" + getParamFoldPath()
									+ modifyFileName + "文件转换成"
									+ getParamFoldPath() + orgRecvFileName);
						}
						replaceMultiRateStartTime = string; //将最后一次替换的文件名更新，用于下一次检查使用
						RecvParamUtils.setRateStartTimeAndVersion(string+"_"+version);//更新后，把多版本的版本和启用时间修改到
						break;
					}
				}
			}

		}
	}

	/**
	 * 获取所有多版本费率的集合
	 * 
	 * @return 获取所有满足格式的多版本费率
	 */
	private Map<String, List<String>> getAllMultiRateFile() {
		File root = new File(getParamFoldPath());
		String[] files = root.list();
		Vector<String> getMathFile = new Vector<String>();
		for (String file : files) {
			/*
			 * 抽取所有符合要求的参数文件
			 * 文件名为20161001000000_1_tb_xxxxxxx.txt
			 */
			if (file.matches("^[0-9]{14}_[0-9]+_.*(_(?i)Recv){1}\\.(txt)$")) {
				getMathFile.add(file);
			}
		}

		Map<String, List<String>> multiRateMap = new HashMap<String, List<String>>();
		String startTime = null;
		List<String> fileList = null;
		for (String file : getMathFile) {
			startTime = file.substring(0, 14);
			if (multiRateMap.containsKey(startTime)) {
				fileList = multiRateMap.get(startTime);
				fileList.add(file);
			} else {
				fileList = new ArrayList<String>();
				fileList.add(file);
			}
			multiRateMap.put(startTime, fileList);
		}
		return multiRateMap;
	}

	/**
	 * 获取参数表的路径
	 *
	 * @return
	 */
	private static String getParamFoldPath() {
		return FileUtils.getRootPath() + File.separator + "param"
				+ File.separator;
	}
//	public static void main(String[] args) {
//		try {
////			String s = "20161007000000_1_tb_VehRoute7.txt";
////			System.out.println(s.substring(s.indexOf("tb_")));
//
//			MultiRateFileUtils multiRateFileUtils = new MultiRateFileUtils();
//			boolean isExists = multiRateFileUtils
//					.isMultiRateRecvFileExists();
//			for (Entry<String, List<String>> entry : multiRateMap.entrySet()) {
//				System.out.println(entry.getKey() + "\t"
//						+ entry.getValue().toString() + "\t"
//						+ entry.getValue().size());
//			}
//			if(isExists){
//				multiRateFileUtils.replaceMultiRecvFile();
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
