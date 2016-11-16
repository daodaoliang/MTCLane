/**
 * 
 */
package com.hgits.util;

import org.apache.log4j.Logger;

/**
 * 检查定时任务执行情况
 * 
 * @author Administrator
 *
 */
public class CheckQuartzTimeThread implements Runnable {
	
	private static Logger logger = Logger.getLogger(CheckQuartzTimeThread.class);

	private String[]  triggerNames = null;
	
	public CheckQuartzTimeThread(String ... triggerNames){
		this.triggerNames = triggerNames;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			long old = System.currentTimeMillis();
			long now = 0;
			while (true) {
				now = System.currentTimeMillis(); // 当前时间
				if (now - old < 0) { // 当前时间晚于上一次检查时间
					QuartzManage
							.rescheduleJobByTriggerName(triggerNames);
				}
				old = System.currentTimeMillis();
				Thread.sleep(5 * 1000);
			}
		} catch (Exception e) {
			logger.error("CheckQuartzTime 执行错误，错误原因："+e.getMessage(),e);
		}

	}

}
