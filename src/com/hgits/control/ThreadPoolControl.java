package com.hgits.control;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池控制类
 *
 * @author Wang Guodong
 */
public class ThreadPoolControl {

    private static final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    /**
     * 获取唯一线程池
     *
     * @return 唯一线程池
     */
    public static synchronized ThreadPoolExecutor getThreadPoolInstance() {
        return threadPool;
    }
}
