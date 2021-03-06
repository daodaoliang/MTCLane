package com.hgits.common.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SyslogAppender;

public class MTCLog {

    private static final Logger logger = Logger.getLogger(MTCLog.class);
    /**
     * 自定义级别名称，以及级别范围
     */
    private static final Level MTC_LEVEL = new MTCLevel(15000, "MTCHD",
            SyslogAppender.LOG_LOCAL0);

    /**
     * 使用日志打印logger中的log方法
     *
     * @param logger
     * @param objLogInfo
     */
    public static void log(Logger logger, Object objLogInfo) {
        logger.log(MTC_LEVEL, objLogInfo);
    }

    public static void log(Object objLogInfo) {
        logger.log(MTC_LEVEL, objLogInfo);
    }

    public static void log(Object objLogInfo, Throwable t) {
        logger.log(MTC_LEVEL, objLogInfo,t);
    }
}