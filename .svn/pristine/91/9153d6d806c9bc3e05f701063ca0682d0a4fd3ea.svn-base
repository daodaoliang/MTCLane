package com.hgits.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 *
 * @author Wangguodong
 */
public class SquadUtils {

	private static final Logger logger = Logger.getLogger(SquadUtils.class);

	/**
	 * 根据给定时间生成工班号 2014-1-1 23:30 至2014-1-2 7:30之间上班，工班为1;2014-1-2 7:30
	 * 至2014-1-2 15:30之间上班，工班为2;2014-1-2 15:30 至2014-1-2 23:30之间上班，工班为3
	 *
	 * @param ts
	 *            给定时间
	 * @return 工班号
	 */
	public static int getSquadId(Date ts) {
		int squadId = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		String time = sdf.format(ts);
		int t = IntegerUtils.parseString(time);
		if ((t >= 233000 && t < 24000000) || t < 73000) {
			squadId = 1;
		} else if (t >= 73000 && t < 153000) {
			squadId = 2;
		} else if (t >= 153000 && t < 233000) {
			squadId = 3;
		}
		return squadId;
	}

	/**
	 * 获取工班日期 如2014-1-1 23:30 至2014-1-2 23:30之间上班，20140102
	 *
	 * @param ts
	 *            上班时间
	 * @return 工班日期
	 */
	public static Date getSquadDate(Date ts) {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		String time = sdf.format(ts);
		int t = IntegerUtils.parseString(time);
		Date date = new Date(0);
		if (t >= 233000) {
			ts = new Date(ts.getTime() + 24 * 60 * 60 * 1000);

		}
		sdf.applyPattern("yyyyMMdd");
		String temp = sdf.format(ts);
		try {
			date = sdf.parse(temp);
		} catch (ParseException ex) {
			logger.error("异常", ex);
		}
		return date;
	}

	/**
	 * 产生班次，软件启动后依次增长，第二天从1开始
	 *
	 * @param jobstart
	 * @return 班次
	 */
	public static int getShiftId(Date jobstart) {
		int a1;// 临时文件中记录的上班日期 如20150303
		int a2;// 当前上班日期 如20150303
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		if (jobstart != null) {
			String temp = sdf.format(jobstart);
			a2 = IntegerUtils.parseString(temp);
		} else {
			return 0;
		}
		int shiftId = 0;
		String path = "temp/temp_shiftId.txt";
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				byte[] buffer = new byte[1024];
				int i = fis.read(buffer);
				String str = new String(buffer, 0, i);
				String[] tempBuffer = str.split("_");
				a1 = IntegerUtils.parseString(tempBuffer[0]);
				if (a2 != a1) {
					// 第二天班次从1开始
					shiftId = 1;
				} else {
					shiftId = IntegerUtils.parseString(tempBuffer[1]) + 1;
				}
			} catch (Exception ex) {
				logger.error("异常", ex);
				shiftId = 1;// 出现异常，班次改为1
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException ex) {
						logger.error("异常", ex);
					}
				}
			}
		} else {
			shiftId = 1;
		}
		FileOutputStream fos = null;
		FileChannel fc = null;
		try {
			fos = new FileOutputStream(path);
			StringBuilder sb = new StringBuilder();
			sb.append(a2).append("_").append(shiftId);
			fc = fos.getChannel();
			fc.write(ByteBuffer.wrap(sb.toString().getBytes()));
			fc.force(true);
		} catch (Exception ex) {
			logger.error("异常", ex);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (fc != null) {
					fc.close();
				}
			} catch (IOException ex) {
				logger.error("异常", ex);
			}
		}
		return shiftId;
	}

	/**
	 * 根据上班时间获取工班如20150101A
	 *
	 * @param date
	 *            上班时间如2015-01-01 01:00:00
	 * @return 工班如20150101A
	 */
	public static String getSquad(Date date) {
		if (date == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String str = sdf.format(date);
		sb.append(str);
		int i = getSquadId((Date) date);
		if (i == 1) {
			sb.append("A");
		} else if (i == 2) {
			sb.append("B");
		} else {
			sb.append("C");
		}
		return sb.toString();
	}
}
