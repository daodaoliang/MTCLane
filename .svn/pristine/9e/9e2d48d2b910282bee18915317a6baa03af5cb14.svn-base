package com.hgits.util;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.ParseException;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务定时器管理类
 * @author wh
 *
 */
public class QuartzManage {
	private static SchedulerFactory sf = new StdSchedulerFactory();
	private static String JOB_GROUP_NAME = "group";
	private static String TRIGGER_GROUP_NAME = "trigger";
	
	private static Logger log = LoggerFactory.getLogger(QuartzManage.class);

	/**
	 * 启动一个任务
	 * @param jobName 任务名称
	 * @param triggerName 触发器名城
	 * @param job 任务
	 * @param time 调度时间
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public static void startJob(String jobName,String triggerName, Job job, String time){
		try {
			Scheduler sched = sf.getScheduler();

			JobDetail tempJob = newJob(job.getClass()).withIdentity(jobName,
					JOB_GROUP_NAME).build();

			CronTrigger trigger = newTrigger()
					.withIdentity(triggerName, TRIGGER_GROUP_NAME)
					.withSchedule(cronSchedule(time)).build();

			sched.scheduleJob(tempJob, trigger);

			if (!sched.isShutdown()) {
				sched.start();
			}
		} catch (Exception e) {
			log.error("启动任务失败，错误原因："+e.getMessage());
		}
	}

	/**
	 * 从Scheduler 重置trigger状态
	 * 
	 * @param triggerNames 触发器名称集合
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public static void rescheduleJobByTriggerName(String ... triggerNames){
		try {
			Scheduler sched = sf.getScheduler();
			Trigger oldTrigger = null;
			TriggerBuilder tb = null;
			Trigger newTrigger = null;
			if(triggerNames != null){
				for (String triggerName : triggerNames) {
					oldTrigger = sched.getTrigger(new TriggerKey(triggerName,
							TRIGGER_GROUP_NAME));

					if (oldTrigger != null) {
						log.debug("reschedule Job: " + oldTrigger.getKey());
						tb = oldTrigger.getTriggerBuilder();
						newTrigger = tb
								.withIdentity(triggerName, TRIGGER_GROUP_NAME)
								.startNow().build();
						// 重新调度jobDetail
						sched.rescheduleJob(oldTrigger.getKey(), newTrigger);
					}
				}
			}
			
		} catch (Exception e) {
			log.error("重置任务失败，错误原因："+e.getMessage());
		}
	}

}