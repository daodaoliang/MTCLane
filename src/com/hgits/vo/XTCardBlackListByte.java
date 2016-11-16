/**
 *
 */
package com.hgits.vo;

import org.apache.log4j.Logger;

import com.hgits.util.DateUtils;

/**
 * 湘通卡黑名单
 *
 * @author wh
 *
 */
public class XTCardBlackListByte {

    private static Logger logger = Logger.getLogger(XTCardBlackListByte.class);
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
        XTCardBlackListByte.buffer1 = buffer1;
    }

    public static byte[] getBuffer2() {
        return buffer2;
    }

    public static void setBuffer2(byte[] buffer2) {
        XTCardBlackListByte.buffer2 = buffer2;
    }

    public static byte[] getBuffer3() {
        return buffer3;
    }

    public static void setBuffer3(byte[] buffer3) {
        XTCardBlackListByte.buffer3 = buffer3;
    }

    public static byte[] getBuffer4() {
        return buffer4;
    }

    public static void setBuffer4(byte[] buffer4) {
        XTCardBlackListByte.buffer4 = buffer4;
    }

    public static byte[] getBuffer5() {
        return buffer5;
    }

    public static void setBuffer5(byte[] buffer5) {
        XTCardBlackListByte.buffer5 = buffer5;
    }

    public static byte[] getBuffer6() {
        return buffer6;
    }

    public static void setBuffer6(byte[] buffer6) {
        XTCardBlackListByte.buffer6 = buffer6;
    }

    public static byte[] getBuffer7() {
        return buffer7;
    }

    public static void setBuffer7(byte[] buffer7) {
        XTCardBlackListByte.buffer7 = buffer7;
    }

    public static byte[] getBuffer8() {
        return buffer8;
    }

    public static void setBuffer8(byte[] buffer8) {
        XTCardBlackListByte.buffer8 = buffer8;
    }

    public static byte[] getBuffer9() {
        return buffer9;
    }

    public static void setBuffer9(byte[] buffer9) {
        XTCardBlackListByte.buffer9 = buffer9;
    }

    public static byte[] getBuffer10() {
        return buffer10;
    }

    public static void setBuffer10(byte[] buffer10) {
        XTCardBlackListByte.buffer10 = buffer10;
    }

    /**
     * 一行湘通卡黑名单全量表的字节数
     */
    public static final int lineLength = 57;

    public static final int bufferSize = lineLength * 500 * 1000; // 全量表黑名单一行记录的字节数

    public static void clean() {
    	logger.debug("XTCardBlackListByte\t清空前内存--------------------"+DateUtils.formatDateToString(DateUtils.getCurrentDate(),"yyyyMMddHHmmss"));
        logger.debug("最大内存=" + Runtime.getRuntime().maxMemory());
        logger.debug("已分配内存=" + Runtime.getRuntime().totalMemory());
        logger.debug("已分配内存中的剩余空间=" + Runtime.getRuntime().freeMemory());
        try {
        	cleanObj(buffer1);
			cleanObj(buffer2);
			cleanObj(buffer3);
			cleanObj(buffer4);
			cleanObj(buffer5);
			cleanObj(buffer6);
			cleanObj(buffer7);
			cleanObj(buffer8);
			cleanObj(buffer9);
			cleanObj(buffer10);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
        logger.debug("XTCardBlackListByte\t清空后内存--------------------"+DateUtils.formatDateToString(DateUtils.getCurrentDate(),"yyyyMMddHHmmss"));
        logger.debug("最大内存=" + Runtime.getRuntime().maxMemory());
        logger.debug("已分配内存=" + Runtime.getRuntime().totalMemory());
        logger.debug("已分配内存中的剩余空间=" + Runtime.getRuntime().freeMemory());
    }
    
    /**
	 * 清除单个对象信息
	 * @param obj 需要清楚的对象
	 * @throws InterruptedException
	 */
	private static void cleanObj(Object obj) throws InterruptedException{
		if(obj != null){
			obj = null;
			System.gc();
			Thread.sleep(500);
		}
	}
}