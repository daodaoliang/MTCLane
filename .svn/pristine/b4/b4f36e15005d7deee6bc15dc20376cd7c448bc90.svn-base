/**
 * 
 */
package com.hgits.vo;

import org.apache.log4j.Logger;

import com.hgits.util.DateUtils;

/**
 * ETC卡黑名单增量
 * 
 * @author wh
 *
 */
public class XTCardBlackDeltaListByte {
	
	private static Logger logger = Logger.getLogger(XTCardBlackDeltaListByte.class);
	private static byte[] buffer1;
	private static byte[] buffer2;
	private static byte[] buffer3;
	private static byte[] buffer4;
	private static byte[] buffer5;
	private static byte[] buffer6;
	private static byte[] buffer7;
	private static byte[] buffer8;
	private static byte[] buffer9;
	private static byte[] buffer10;
	
	public static byte[] getBuffer1() {
		return buffer1;
	}

	public static void setBuffer1(byte[] buffer1) {
		XTCardBlackDeltaListByte.buffer1 = buffer1;
	}

	public static byte[] getBuffer2() {
		return buffer2;
	}

	public static void setBuffer2(byte[] buffer2) {
		XTCardBlackDeltaListByte.buffer2 = buffer2;
	}

	public static byte[] getBuffer3() {
		return buffer3;
	}

	public static void setBuffer3(byte[] buffer3) {
		XTCardBlackDeltaListByte.buffer3 = buffer3;
	}

	public static byte[] getBuffer4() {
		return buffer4;
	}

	public static void setBuffer4(byte[] buffer4) {
		XTCardBlackDeltaListByte.buffer4 = buffer4;
	}

	public static byte[] getBuffer5() {
		return buffer5;
	}

	public static void setBuffer5(byte[] buffer5) {
		XTCardBlackDeltaListByte.buffer5 = buffer5;
	}

	public static byte[] getBuffer6() {
		return buffer6;
	}

	public static void setBuffer6(byte[] buffer6) {
		XTCardBlackDeltaListByte.buffer6 = buffer6;
	}

	public static byte[] getBuffer7() {
		return buffer7;
	}

	public static void setBuffer7(byte[] buffer7) {
		XTCardBlackDeltaListByte.buffer7 = buffer7;
	}

	public static byte[] getBuffer8() {
		return buffer8;
	}

	public static void setBuffer8(byte[] buffer8) {
		XTCardBlackDeltaListByte.buffer8 = buffer8;
	}

	public static byte[] getBuffer9() {
		return buffer9;
	}

	public static void setBuffer9(byte[] buffer9) {
		XTCardBlackDeltaListByte.buffer9 = buffer9;
	}

	public static byte[] getBuffer10() {
		return buffer10;
	}

	public static void setBuffer10(byte[] buffer10) {
		XTCardBlackDeltaListByte.buffer10 = buffer10;
	}

	public static final int lineLenght = 58;
	
	public static final int bufferSize = lineLenght * 800 * 1000; // 增量表黑名单一行记录的字节数
	
	public static void clean(){
		logger.debug("XTCardBlackDeltaListByte\t清空前内存--------------------"+DateUtils.formatDateToString(DateUtils.getCurrentDate(),"yyyyMMddHHmmss"));
        logger.debug("最大内存=" + Runtime.getRuntime().maxMemory());
        logger.debug("已分配内存=" + Runtime.getRuntime().totalMemory());
        logger.debug("已分配内存中的剩余空间=" + Runtime.getRuntime().freeMemory());
		XTCardBlackDeltaListByte.buffer1 = null;
		XTCardBlackDeltaListByte.buffer2 = null;
		XTCardBlackDeltaListByte.buffer3 = null;
		XTCardBlackDeltaListByte.buffer4 = null;
		XTCardBlackDeltaListByte.buffer5 = null;
		XTCardBlackDeltaListByte.buffer6 = null;
		XTCardBlackDeltaListByte.buffer7 = null;
		XTCardBlackDeltaListByte.buffer8 = null;
		XTCardBlackDeltaListByte.buffer9 = null;
		XTCardBlackDeltaListByte.buffer10 = null;
        logger.debug("XTCardBlackBaseListByte\t清空后内存--------------------"+DateUtils.formatDateToString(DateUtils.getCurrentDate(),"yyyyMMddHHmmss"));
        logger.debug("最大内存=" + Runtime.getRuntime().maxMemory());
        logger.debug("已分配内存=" + Runtime.getRuntime().totalMemory());
        logger.debug("已分配内存中的剩余空间=" + Runtime.getRuntime().freeMemory());
	}

}
