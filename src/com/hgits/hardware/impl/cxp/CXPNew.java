package com.hgits.hardware.impl.cxp;

import MyComm.MyComm;
import com.hgits.control.ThreadPoolControl;
import com.hgits.hardware.CXP;
import com.hgits.task.MyTask;
import com.hgits.util.HexUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.log4j.Logger;

/**
 *
 * @author Wang Guodong
 */
public class CXPNew implements CXP {

    private final Logger logger = Logger.getLogger(CXPNew.class.getName());
    private final String deviceName = "CXP";//设备名称
    private String errorMsg;//异常信息
    private boolean running;//运行标识
    private final ConcurrentLinkedQueue<byte[]> sendQueue = new ConcurrentLinkedQueue<>();//发送信息的队列
    private final Object sendObj = new Object();//发送信息加锁对象
    private MyComm myComm;//串口
    private boolean passCoilFlag;//通道线圈标识
    private boolean arriveCoilFlag;//到达线圈标识
    private int canopyFlag;//雨棚灯标识,0 灯灭；1 红；2 绿
    private int trafficFlag;//通行灯标识,0 灯灭；1 红；2 绿
    private final StringBuilder order = new StringBuilder("0A5330303030303030310D");//当前指令
    private final String ORDER_HEART = "0A5A0D";//心跳检测指令
    private final String ORDER_VERSION = "0A5665720D";//查询CXP板版本号指令
    private final String ORDER_LENGTH = "0A4E31320D";//设置CXP板信息长度指令
    private final String ORDER_QUERY = "0A450D";//查询CXP板状态指令
    private final String HEAD_STATUS = "45";//状态查询指令头
    private final String HEAD_HEART = "5A";//心跳检测指令头
    private final String HEAD_VERSION = "56";//版本查询指令投
    private final String HEAD_LENGTH = "4E";//设置长度指令头
    private int interval = 2000;//心跳检测时间间隔

    @Override
    public void changeCanopyLightRed() {
        canopyFlag = 1;
        if (trafficFlag == 2) {//通行灯绿
            order.replace(18, 20, "33");
        } else {//通行灯红
            order.replace(18, 20, "31");
        }
        byte[] buffer = HexUtils.decode(order.toString());
        write(buffer);
    }

    @Override
    public void lowerRailing() {
        order.replace(14, 16, "31");
        byte[] buffer = HexUtils.decode(order.toString());
        write(buffer);
    }

    @Override
    public void stopAlarm() {
        order.replace(16, 18, "30");
        byte[] buffer = HexUtils.decode(order.toString());
        write(buffer);
    }

    @Override
    public int getCanopyFlag() {
        return 0;
    }

    @Override
    public boolean checkPassageCoil() {
        return passCoilFlag;
    }

    @Override
    public void warningAlarm() {
        order.replace(16, 18, "31");
        byte[] buffer = HexUtils.decode(order.toString());
        write(buffer);
    }

    @Override
    public boolean checkArriveCoil() {
        return arriveCoilFlag;
    }

    @Override
    public void changeCanopyLightGreen() {
        canopyFlag = 2;
        if (trafficFlag == 2) {//通行灯绿
            order.replace(18, 20, "3F");
        } else {//通行灯红
            order.replace(18, 20, "3D");
        }
        byte[] buffer = HexUtils.decode(order.toString());
        write(buffer);
    }

    @Override
    public void liftRailing() {
        order.replace(14, 16, "30");
        byte[] buffer = HexUtils.decode(order.toString());
        write(buffer);
    }

    @Override
    public void changeTrafficLightGreen() {
        trafficFlag = 2;
        if (canopyFlag == 2) {//雨棚灯绿
            order.replace(18, 20, "3F");
        } else {//雨棚灯红
            order.replace(18, 20, "33");
        }
        byte[] buffer = HexUtils.decode(order.toString());
        write(buffer);
    }

    @Override
    public void changeTrafficLightRed() {
        trafficFlag = 1;
        if (canopyFlag == 2) {//雨棚灯绿
            order.replace(18, 20, "3D");
        } else {//雨棚灯红
            order.replace(18, 20, "31");
        }
        byte[] buffer = HexUtils.decode(order.toString());
        write(buffer);
    }

    @Override
    public String getCxpErrorMsg() {
        return errorMsg;
    }

    @Override
    public void setArrriveCoil(boolean flag) {
        arriveCoilFlag = true;
    }

    @Override
    public void setPassageCoil(boolean flag) {
        passCoilFlag = true;
    }

    /**
     * 记录日志
     *
     * @param str 日志内容
     */
    private void log(Object str) {
        logger.debug(deviceName + " " + str);
    }

    /**
     * 记录日志
     *
     * @param str 日志内容
     * @param t 异常
     */
    private void log(Object str, Throwable t) {
        logger.debug(deviceName + " " + str, t);
    }

    /**
     * 发送信息
     *
     * @param buffer 信息
     */
    private void write(byte[] buffer) {
        synchronized (sendObj) {
            sendQueue.add(buffer);
            sendObj.notify();
        }
    }

    @Override
    public void run() {
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_COMM, "CXP", null);
        if (str == null) {
            log("未启用" + deviceName);
            return;
        }
        str = str.replaceAll("，", ",");//中文逗号改为英文
        String[] buffer = str.split(",");
        if (buffer.length < 7) {
            log("启用" + deviceName + ",但是配置" + str + "异常,配置项不足7个");
            errorMsg = deviceName + "配置错误";
            return;
        }
        String com = buffer[0].trim();
        String str1 = buffer[1].trim();
        if (!str1.matches("\\d*")) {
            log("启用" + deviceName + ",但baudrate=" + str1 + "配置错误");
            errorMsg = deviceName + "配置错误";
            return;
        }
        int baudrate = Integer.parseInt(str1);
        String str2 = buffer[2].trim();
        if (!str2.matches("\\d*")) {
            log("启用" + deviceName + ",但databit=" + str2 + "配置错误");
            errorMsg = deviceName + "配置错误";
            return;
        }
        int databit = Integer.parseInt(str2);
        String str3 = buffer[3].trim();
        if (!str3.matches("\\d*")) {
            log("启用" + deviceName + ",但stopbit=" + str3 + "配置错误");
            errorMsg = deviceName + "配置错误";
            return;
        }
        int stopbit = Integer.parseInt(str3);
        String str4 = buffer[4].trim();
        if (!str4.matches("\\d*")) {
            log("启用" + deviceName + ",但parity=" + str4 + "配置错误");
            errorMsg = deviceName + "配置错误";
            return;
        }
        int parity = Integer.parseInt(str4);
        String str6 = buffer[6].trim();
        if (!str6.matches("\\d*")) {
            log("启用" + deviceName + ",但logLevel=" + str6 + "配置错误");
            errorMsg = deviceName + "配置错误";
            return;
        }
        errorMsg = null;
        int logLevel = Integer.parseInt(str6);
        logger.warn("启用" + deviceName + ",com=" + com + ",baudrate=" + baudrate + ",databit=" + databit + ",stopbit=" + stopbit + ",parity=" + parity + ",loglevel=" + logLevel);
        myComm = new MyComm();
        int i = myComm.openComm(com, baudrate, databit, stopbit, parity, logLevel);
        if (i != 0) {
            log("串口打开失败");
            errorMsg = deviceName + "串口打开失败";
            return;
        }
        running = true;
        SendTask sendTask = new SendTask();
        ParseTask parseTask = new ParseTask();
        MonitorTask monitorTask = new MonitorTask(sendTask, parseTask);
        ThreadPoolControl.getThreadPoolInstance().execute(sendTask);
        ThreadPoolControl.getThreadPoolInstance().execute(parseTask);
        ThreadPoolControl.getThreadPoolInstance().execute(monitorTask);

    }

    class SendTask extends MyTask {

        @Override
        public void run() {
            setAlive(true);
            log("sendTask start");
            try {
                sendInitOrder();
                byte[] buffer = null;
                long start = System.currentTimeMillis();
                while (running) {
                    synchronized (sendObj) {
                        if (!sendQueue.isEmpty()) {
                            buffer = sendQueue.poll();
                        } else {
                            sendObj.wait(1000);
                        }
                    }
                    if (buffer != null) {
                        myComm.write(buffer);//此方法可能会因为串口故障出现阻塞，不可将该方法放入锁内
                    }
                    long now = System.currentTimeMillis();
                    if (now - start >= interval) {
                        start = now;
                        byte[] tempBuffer = HexUtils.decode(ORDER_HEART);
                        write(tempBuffer);//发送心跳检测指令
                    }
                }
            } catch (Throwable t) {
                log(t, t);
            } finally {
                log("sendTask stop");
                setAlive(false);
            }

        }

        /**
         * 发送初始化指令
         */
        private void sendInitOrder() {
            QueryVersion();
            setLength();

        }

        /**
         * 查询CXP板版本
         */
        private void QueryVersion() {
            byte[] buffer = HexUtils.decode(ORDER_VERSION);
            write(buffer);
        }

        /**
         * 设置指令长度
         */
        private void setLength() {
            byte[] buffer = HexUtils.decode(ORDER_LENGTH);
            write(buffer);
        }
    }

    class ParseTask extends MyTask {

        @Override
        public void run() {
            try {
                setAlive(true);
                log("parseTask start");
                StringBuilder sb = new StringBuilder();//用于记录CXP信息
                byte[] buffer = null;
                long start = System.currentTimeMillis();
                while (running) {
                    buffer = myComm.readByteArray();
                    if (buffer != null) {
                        start = System.currentTimeMillis();
                        String str = HexUtils.encode(buffer);
                        if (sb.length() > 100) {//记录的CXP信息超长
                            sb.delete(0, sb.length());
                        }
                        sb.append(str);
                        int index1 = sb.lastIndexOf("0A");
                        int index2 = sb.lastIndexOf("0D");
                        if (index1 == -1 || index2 == -1 || index1 <= index2) {//开始，结束下标异常
                            //返回信息异常时，发送查询状态指令
                            queryStatus();
                            continue;
                        } else {
                            String status = sb.substring(index1, index2);
                            parseCxpStatus(status);
                            sb.delete(0, index2);
                        }
                    } else {
                        long now = System.currentTimeMillis();
                        if (now - start > interval * 5) {
                            errorMsg = "通信异常";
                        }
                        Thread.sleep(1);
                    }
                }
            } catch (Throwable t) {
                logger.error(t, t);
            } finally {
                log("parseTask stop");
                setAlive(false);
            }

        }

        /**
         * 解析0A开头，0D结尾的CXP板数据
         *
         * @param str
         */
        private void parseCxpStatus(String str) {
            int index1 = str.lastIndexOf("0A");
            int index2 = str.lastIndexOf("0D");
            if (index1 == -1 || index2 == -1 || index1 <= index2) {//开始，结束下标异常
                return;
            }
            int len = str.length();
            if (len == 6) {//心跳检测及指令回应信息
                return;
            } else if (len > 6) {//线圈信息
                String coilMsg = str.substring(index2 - 4, index2 - 2);//获取结束符前面一个字节的数据
                switch (coilMsg) {
                    case "30"://到达线圈有车，通道线圈有车
                        arriveCoilFlag = false;
                        passCoilFlag = false;
                        break;
                    case "31"://通道线圈有车
                        arriveCoilFlag = false;
                        passCoilFlag = true;
                        break;
                    case "34"://到达线圈有车
                        arriveCoilFlag = true;
                        passCoilFlag = false;
                        break;
                    case "35"://到达线圈有车，通道线圈有车
                        arriveCoilFlag = true;
                        passCoilFlag = true;
                        break;

                }
            }
        }

        /**
         * 发送查询CXP板状态查询指令
         */
        private void queryStatus() {
            byte[] buffer = HexUtils.decode(ORDER_QUERY);
            write(buffer);
        }
    }

    class MonitorTask extends MyTask {

        private final SendTask sendTask;
        private final ParseTask parseTask;

        public MonitorTask(SendTask sendTask, ParseTask parseTask) {
            this.sendTask = sendTask;
            this.parseTask = parseTask;
        }

        @Override
        public void run() {
            log("monitorTask start");
            while (running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    log(ex, ex);
                }
                if (!sendTask.isAlive()) {
                    sendTask.setAlive(running);
                    ThreadPoolControl.getThreadPoolInstance().execute(sendTask);
                }
                if (!parseTask.isAlive()) {
                    parseTask.setAlive(running);
                    ThreadPoolControl.getThreadPoolInstance().execute(parseTask);
                }
            }
            if (myComm != null) {
                myComm.closeComm();
            }
            log("monitorTask stop");
        }
    }

}
