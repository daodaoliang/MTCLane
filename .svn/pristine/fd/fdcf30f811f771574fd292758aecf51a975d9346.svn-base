/**
 *
 */
package com.hgits.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.hgits.service.constant.MtcConstant;
import com.hgits.util.file.FileUtils;
import com.hgits.vo.Constant;

/**
 * 日志处理类
 *
 * @author wh
 *
 */
public class LogUtils {
	
	/**
	 * 历史文件的正则匹配表达式
	 */
	private static Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}){1}");
	
	private static Logger log = Logger.getLogger(LogUtils.class);
	
	/**
	 * 日志与整理 只备份logs/文件夹下的*_yyyy-MM-dd.*格式的日志文件
	 */
	public static void backup() {
		// 需要备份Log
		File file = new File("logs/");

		if (file.isDirectory()) {

			Vector<File> zipFiles = new Vector<File>(); // 满足整理条件的文件集合
			try {

				// 获取日志列表
				File[] files = file.listFiles();

				/**
				 * 只备份*_yyyy-MM-dd.log*这种格式的日志，此文件夹下，默认只会产生.log结尾的日志
				 */
				String reg = "^.*\\d{4}(\\-)\\d{1,2}\\1\\d{1,2}.*$";
				for (File file2 : files) {
					if (file2.getName().matches(reg)) {
						zipFiles.add(file2);
						continue;
					}
				}

				File zipFile = new File(file, DateUtils.formatDateToString(
						DateUtils.calculate(DateUtils.getCurrentDate(),
								Calendar.MONTH, "-", 1), "yyyy-MM_")
						+ "log.zip");

				if (!zipFiles.isEmpty()) { // 备份文件不为空
					ZipUtils.zipFilesAndDelete(zipFiles, zipFile); // 备份并删除
				}

			} catch (Exception e) {
				log.error("备份日志异常，错误原因：" + e.getMessage(), e);
			}

		}

	}

	/**
	 * 清除超过指定日期的压缩日志文件
	 */
	public static void cleanExpiresLog() {
		try {
			// 需要备份Log
			File file = new File("logs/");
			/**
			 * 失效日期
			 */
			int expiresDays = Integer.valueOf(MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT,"expiresLogDays", MtcConstant.LOG_EXPIRES_DATE));
			
			if (file.isDirectory()) {
				// 获取日志列表
				File[] files = file.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						if (pathname.isDirectory()) {
							return false;
						} else {
							return pathname.getName().toLowerCase().endsWith("zip");
						}
					}
				});

				String fileName = null;
				Matcher matcher = null;
				Date fileDate = null;
				Date nowDate = null;
				for (File file2 : files) {
					fileName = file2.getName();
					matcher = pattern.matcher(fileName);
					if (matcher.find()) {
						fileDate = DateUtils.parseDate(matcher.group(0), "yyyy-MM");
						if (fileDate != null) {
							// 判断文件的历史日期是否比配置的失效日期要晚，如果晚的话，则删除掉
							nowDate = DateUtils.addDays(DateUtils.getCurrentDate(),
									-expiresDays);
							if (nowDate.after(fileDate)) {
								// 删除此文件
								FileUtils.deleteFile(file2.getAbsolutePath());
								log.debug("超过配置日期：" + expiresDays + "之前的日志备份文件"
										+ fileName + "删除");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("清除历史日志失败，错误原因:" + e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
//		DOMConfigurator.configure("resource" + File.separator + "log4j.xml");
        //初始化配置文件
        try {
        	List<String> list=new ArrayList();
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
			backup();
			cleanExpiresLog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
