package com.hgits.hardware.impl.vbd;

import MyComm.MyComm;
import com.hgits.task.MyTask;
import com.hgits.control.ThreadPoolControl;
import com.hgits.hardware.VBD;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * 防盗车取卡设备
 *
 * @author Wang Guodong
 */
public class VehBackDetector implements VBD {

    private final String portName;//串口名
    private final int baudRate;//波特率
    private final int dataBits;//数据位
    private final int stopBits;//停止位
    private final int parity;//校验位
    private final int logLevel;//日志级别 0普通 1详细 2非常详细
    private final String deviceName = "防倒车取卡设备";
    private static final Logger logger = Logger.getLogger(VehBackDetector.class);
    private boolean running = true;//运行标识
    private boolean vehBackFlag;//倒车标识符
    private MyComm myComm;//串口

    public VehBackDetector(String portName, int baudRate, int dataBits, int stopBits, int parity, int logLevel) {
        this.portName = portName;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        this.logLevel = logLevel;
    }

    @Override
    public void run() {
        myComm = new MyComm();
        int i = myComm.openComm(portName, baudRate, dataBits, stopBits, parity, logLevel);
        if (i != 0) {
            logger.error("打开串口" + portName + "失败");
            return;
        }
        ParseTask parseTask = new ParseTask();
        ThreadPoolControl.getThreadPoolInstance().execute(parseTask);
        List<MyTask> taskList = new ArrayList();
        taskList.add(parseTask);
        MonitorTask monitorTask = new MonitorTask(taskList);
        ThreadPoolControl.getThreadPoolInstance().execute(monitorTask);
    }

    @Override
    public boolean isVehBack() {
        return vehBackFlag;
    }

    @Override
    public void setVehBack(boolean flag) {
        vehBackFlag = flag;
    }

    @Override
    public String getErrorMsg() {
        if (myComm == null) {
            return null;
        } else {
            return deviceName + myComm.getErrorMsg();
        }
    }

    @Override
    public void stop() {
        logger.debug("退出" + deviceName + "与的通信");
        running = false;
        if (myComm != null) {
            myComm.closeComm();
        }
    }

    /**
     * 解析信息
     *
     * @param buffer 需要解析的信息
     */
    private void parseInfo(byte[] buffer) {
        //TODO 解析倒车检测器的信息
        setVehBack(true);
    }

    /**
     * 解析信息的任务
     */
    class ParseTask extends MyTask {

        @Override
        public void run() {
            setAlive(true);
            try {
                logger.debug(portName + "解析信息任务启动");
                while (running) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                    }
                    if (myComm == null) {
                        continue;
                    }
                    byte[] buffer = myComm.readByteArray();
                    if (buffer != null) {
                        parseInfo(buffer);
                    }
                }
                logger.debug(portName + "解析信息任务结束");
            } catch (Throwable ex) {
                logger.error(ex, ex);
            } finally {
                setAlive(false);
            }
        }
    }

    /**
     * 监控任务
     */
    class MonitorTask implements Runnable {

        private final List<MyTask> taskList;//需要监控的任务集合

        public MonitorTask(List<MyTask> taskList) {
            this.taskList = taskList;
        }

        @Override
        public void run() {
            logger.debug(portName + "监控任务启动");
            while (running) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                for (MyTask task : taskList) {
                    if (!task.isAlive()) {//监控任务
                        task.setAlive(running);
                        ThreadPoolControl.getThreadPoolInstance().execute(task);
                    }
                }
            }
            logger.debug(portName + "监控任务结束");
        }
    }

}
