package com.hgits.hardware.impl.cicm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;
import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;
import com.hgits.common.log.MTCLog;
import com.hgits.control.FunctionControl;
import com.hgits.control.ThreadPoolControl;
import com.hgits.hardware.CICM;
import com.hgits.task.MyTask;
import com.hgits.util.HexUtils;
import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.SetCOMs;
import com.hgits.vo.Constant;
import com.hgits.vo.Lane;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 西埃斯自助卡机模拟人工卡机
 *
 * @author Wang Guodong
 */
public class XasCICM implements CICM, SerialPortEventListener, Runnable {

    private InputStream inPut;
    private OutputStream outPut;
    public StringBuffer strBuff = new StringBuffer();                // 串口返回信息缓存空间
    private StringBuffer command = new StringBuffer();               // 存储向卡机发送的最后一条指令缓存空间
    private String CICMAction = "0A5330303031310D";//\nS00011\r";                        //0A5330303031310D                     //前后卡栓上升、保护盖锁上   
    private String pinRecieverRaise = "0A4D464646460D";//\nMFFFF\r";                   //0A4D464646460D                       //收发针上升直至收发头抬起
    private String pinRecieverLower = "0A44464646460D";//\nDFFFF\r";                   //0A44464646460D                       //收发针下降
    private String distributionFingerOneStepLower = "0A44303037330D";//\nD0073\r";     //0A44303037330D                       //收发头下降一格
    private String distributionFingertwoStepsLower = "0A44303135450D";//\nD015E\r";    //0A44303135450D                       //收发头下降两格
    private String statusCheckMSG = "0A450D";//\nE\r";                         //0A450D                                       //卡箱状态检测
    private boolean isTop = false;//收发针是否到头
    private boolean running = true;
    private ConcurrentLinkedQueue<String> sendQueue = new ConcurrentLinkedQueue<>();

    private boolean responsed;//指令已回应
    /**
     * true:收发针升到头
     */
    public boolean receiverPinRised;
    /**
     * true:收发针沉到底
     */
    public boolean receiverPinLowered;
    /**
     * true:打开保护盖
     */
    public boolean protectiveCover;
    /**
     * true:收发头抬起
     */
    public boolean receiverBolt;
    /**
     * 收发针状态 ，0 收发针降到底 1 收发针升到顶 2 收发针在卡箱中间
     */
    private int receivePin;
    /**
     * 通讯错误次数
     */
    private int times;
    /**
     * CICM错误信息
     */
    private String cicmErrorMsg = "";
    String writerLog = "0";
    private boolean isPinLowered;

    public XasCICM() {
        ThreadPoolControl.getThreadPoolInstance().execute(this);
    }

    /**
     * 卡机建立连接
     *
     */
    public void run() {
        MTCLog.log("XasCICM版本：V1.1.4");
        try {
            //加载串口信息
            String info = SetCOMs.getCICM();
            if (info == null) {
                MTCLog.log("未启用卡机");
                return;
            }
            //检测CICM通讯是否正常
            checkCicmConnection();
            String[] infos = info.split(",");
            String com = infos[0];
            int baudRate = Integer.parseInt(infos[1]);//波特率
            int dataBits = Integer.parseInt(infos[2]);//数据位
            int stopBits = Integer.parseInt(infos[3]);//停止位
            int parity = Integer.parseInt(infos[4]);//校验位
            writerLog = infos[6];
            command.append(CICMAction);
            CommPortIdentifier commPort = CommPortIdentifier.getPortIdentifier(com);
            SerialPort serialPort = (SerialPort) commPort.open("卡机", 1000);
            serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
            serialPort.setDTR(true);
            serialPort.setRTS(true);
            serialPort.notifyOnDataAvailable(true);
            serialPort.addEventListener(this);
            outPut = serialPort.getOutputStream();
            inPut = serialPort.getInputStream();
            checkStatus();
            SendTask sendTask = new SendTask();
            ThreadPoolControl.getThreadPoolInstance().execute(sendTask);
        } catch (NullPointerException e) {
            MTCLog.log("NullPointerException", e);
        } catch (PortInUseException e) {
            MTCLog.log("卡机：串口被占用", e);
            cicmErrorMsg = "卡机：串口被占用";
        } catch (NoSuchPortException e) {
            MTCLog.log("卡机：没有这个串口", e);
            cicmErrorMsg = "卡机：没有这个串口";
        } catch (IOException e) {
            MTCLog.log("卡机：IO异常", e);
            cicmErrorMsg = "卡机：IO异常";
        } catch (UnsupportedCommOperationException e) {
            MTCLog.log("卡机：IO异常", e);
            cicmErrorMsg = "卡机：UnsupportedCommOperationException";
        } catch (TooManyListenersException e) {
            MTCLog.log("卡机：TooManyListenersException", e);
            cicmErrorMsg = "卡机：TooManyListenersException";
        }
    }

    private void checkStatus() {
//        commandSender(CICMAction);
        write("检查卡机状态", CICMAction);
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            MTCLog.log("卡机：InterruptedException", ex);
        }

        new Thread(new Runnable() {
            /**
             * 状态检测线程
             */
            @Override
            public void run() {

                while (running) {
                    try {
                        statusCheck();
                        times++;
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex1) {
                            MTCLog.log("卡机：" + ex1.getMessage(), ex1);
                        }
                        MTCLog.log("卡机：" + ex.getMessage(), ex);
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                receivePinRaise();//receive pin initialization
            }
        }).start();

    }

    /**
     * 发送卡机状态检测指令
     */
    public void statusCheck() {
        if (outPut == null) {
            return;
        }
        write("发送卡机状态检测指令", statusCheckMSG);
    }

    /**
     * 串口返回指令监听器
     *
     * @param ev
     */
    @Override
    public void serialEvent(SerialPortEvent ev) {
        switch (ev.getEventType()) {
            case SerialPortEvent.BI://通讯中断
            case SerialPortEvent.OE://溢位错误
            case SerialPortEvent.FE://帧错误
            case SerialPortEvent.PE://奇偶校验错误
            case SerialPortEvent.CD://载波检测
            case SerialPortEvent.CTS://清除发送
            case SerialPortEvent.DSR://数据设备准备好
            case SerialPortEvent.RI://振铃指示
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY://输出缓冲区已清空
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                try {
                    //有数据到达 
                    while (inPut.available() > 0) {
                        byte[] byteArray = new byte[inPut.available()];
                        inPut.read(byteArray);

                        String temp = new String(byteArray);//\n....\r型
                        strBuff.append(temp);               //\n....\r型

                        temp = HexUtils.encode(temp.getBytes());//0A...0D型

                        statusCheckForCICM(temp);
                        writeLog("XasCICM返回信息", temp);
                    }
                } catch (IOException ex) {
                    MTCLog.log("卡机监听器：IOException ", ex);
                }
                break;
        }
    }

    /**
     * 保护盖上锁
     *
     * @throws Exception
     */
    public void protectiveCoverLockUp() throws Exception {
        if (outPut == null) {
            return;
        }
        write("保护盖上锁", command.replace(5, 6, "0").toString());
    }

    /**
     * 保护盖开锁
     *
     * @throws Exception
     */
    public void protectiveCoverOpen() throws Exception {
        if (outPut == null) {
            return;
        }
        write("保护盖开锁", command.replace(5, 6, "1").toString());
    }

    /**
     * 收发针上升
     *
     * @throws Exception
     */
    public void receivePinRaise() {
        receivePin = 1;
        if (outPut == null) {
            return;
        }
        isTop = false;
//        commandSender(pinRecieverRaise);
        write("收发针上升", pinRecieverRaise);
        long start = System.currentTimeMillis();
        while (true) {
            if (Lane.getInstance().isEntry() && FunctionControl.isManualAutoFunActive()) {//入口并且是接入了自助发卡机
                long now = System.currentTimeMillis();
                if (now - start > 1000 || now < start) {
                    break;
                }
            }
            if (isTop) {

                break;
            }
            if (receiverPinRised || receiverBolt) {
                break;
            } else {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                    MTCLog.log(ex.getMessage(), ex);
                }
            }

            if ((HexUtils.encode(strBuff.toString().getBytes())).equals("0A460D")) {
                writeLog("收发针上升到头", HexUtils.encode(strBuff.toString().getBytes()));
                break;
            }
            strBuff.delete(0, strBuff.length());
            if (times > 5) {
                break;
            }
        }

    }

    /**
     * 收发针下降
     *
     * @throws Exception
     */
    public void receivePinLower() throws Exception {
        receivePin = 0;
        if (outPut == null) {
            return;
        }
        isPinLowered = false;
        write("收发针下降", pinRecieverLower);
        long start = System.currentTimeMillis();
        while (true) {
            if (Lane.getInstance().isEntry() && FunctionControl.isManualAutoFunActive()) {//入口并且是接入了自助发卡机
                long now = System.currentTimeMillis();
                if (now - start > 1000 || now < start) {
                    break;
                }
            }
            if (isPinLowered) {

                break;
            }
            if (receiverPinLowered) {
                break;
            } else {

                Thread.sleep(5);
            }

            if ((HexUtils.encode(strBuff.toString().getBytes())).equals("0A460D")) {

                break;
            }

            strBuff.delete(0, strBuff.length());
            if (times > 5) {
                break;
            }
        }
    }

    /**
     * 升起前卡栓
     *
     * @throws Exception
     */
    public void frontBoltRaise() throws Exception {
        if (outPut == null) {
            return;
        }
        command.replace(9, 10, "0");
        write("升起前卡栓", command.toString());
    }

    /**
     * 降下前卡栓
     *
     * @throws Exception
     */
    public void frontBoltLower() throws Exception {
        if (outPut == null) {
            return;
        }
        // 0A5330303131310D
        command.replace(9, 10, "1");
//        commandSender(command.toString());
        write("降下前卡栓", command.toString());
    }

    /**
     * 降下后卡栓
     *
     * @throws Exception
     */
    public void backBoltLower() throws Exception {
        if (outPut == null) {
            return;
        }
        command.replace(7, 8, "1");
        write("降下后卡栓", command.toString());
    }

    /**
     * 升起后卡栓
     *
     * @throws Exception
     */
    public void backBoltRaise() throws Exception {
        if (outPut == null) {
            return;
        }
        command.replace(7, 8, "0");
        write("升起后卡栓", command.toString());
    }

    /**
     * 收发针下降一格
     *
     * @throws Exception
     */
    public void lowerOneStep() throws Exception {
        receivePin = 2;
        if (outPut == null) {
            return;
        }
        write("收发针下降一格", distributionFingerOneStepLower);
    }

    /**
     * 收发针下降两格
     *
     * @throws Exception
     */
    public void lowerTwoSteps() throws Exception {
        receivePin = 2;
        if (outPut == null) {
            return;
        }
        write("收发针下降两格", distributionFingertwoStepsLower);
    }

    /**
     * 检查卡机保护盖状态 true 打开 false 关闭
     *
     * @return
     */
    public boolean checkProtectCover() {

        return protectiveCover;
    }

    /**
     * 检测收发头状态 true 抬起 false合上
     *
     * @return
     */
    public boolean checkReceiveHead() {
        return receiverBolt;
    }

    /**
     * 检测收发针状态 0 收发针降到底 1 收发针升到顶 2 收发针在卡箱中间
     *
     * @return
     */
    public int checkReceivePin() {
        return receivePin;
    }

    /**
     * 获取卡机错误信息
     *
     * @return
     */
    public String getCicmErrorMsg() {
        String error = "";
        if (cicmErrorMsg.equals("")) {
            error = null;
        } else {
            error = cicmErrorMsg;
        }
        return error;
    }
    /**
     * 通讯正常时，每2秒更新状态 receiverBolt:收发头抬起	protectiveCover:打开保护盖
     *
     * receiverPinLowered:收发针沉到底	receiverPinRised:收发针升到头
     *
     * @param temp 串口返回值
     */
    StringBuilder sbRead = new StringBuilder();

    private void statusCheckForCICM(String temp) {

        times = 0;
        cicmErrorMsg = "";

        if (temp.contains("0A410D")) {
            responsed = true;
        }
        //处理异常信息
        if (!temp.startsWith("0A") || !temp.endsWith("0D")) {

            sbRead.append(temp);
            writeLog("异常信息", sbRead.toString());

            try {
                int i = sbRead.indexOf("0D");//没有0D
                if (i == -1) {
                    return;
                }
                int j = sbRead.indexOf("0A");//有0A

                while (j != -1 && i != -1) {//没有0A时，跳出循环

                    checkCicmStatus(sbRead.substring(j, i + 2));//解析第一个0A...0D

                    sbRead.delete(j, i + 2);//删除第一个0A...0D

                    j = sbRead.indexOf("0A");//有0A

                    i = sbRead.indexOf("0D");//没有0D

                }
            } catch (Exception e) {
                writeLog("字符串截取异常", e.getMessage());
            }
        } else {
            sbRead.delete(0, sbRead.length());
            sbRead.append(temp);
            while (sbRead.indexOf("0A") != -1) {

                checkCicmStatus(sbRead.substring(sbRead.indexOf("0A"), sbRead.indexOf("0D") + 2));
                sbRead.delete(sbRead.indexOf("0A"), sbRead.indexOf("0D") + 2);

            }

        }
    }

    /**
     * 检测卡机状态
     *
     * @param temp 卡机返回信息
     */
    private void checkCicmStatus(String temp) {
        if (temp.contains("453030303131313030")) {//453030303131313030
            receiverPinRised = false;
            receiverPinLowered = false;
            protectiveCover = false;
            receiverBolt = true;//收发头抬起
            isTop = true;
        }
        if (temp.contains("453030303031313030")) {//453030303031313030
            receiverPinRised = false;
            receiverPinLowered = false;
            protectiveCover = false;
            receiverBolt = false;
        }
        if (temp.contains("453130303031313030")) {//453130303031313030//\\45 30 30 30 30 31 31 30 30   
//                                                                               45 31 30 30 30 31 31 30 30
            receiverPinRised = false;
            receiverPinLowered = false;//!!!!!
            protectiveCover = true;//打开保护盖
            receiverBolt = false;
        }
        if (temp.contains("453130303131313030")) {//453130303131313030
            receiverPinRised = false;
            receiverPinLowered = false;
            protectiveCover = true;
            receiverBolt = true;
            isTop = true;

        }
        if (temp.contains("453030313031313030")) {//453030313031313030
            receiverPinRised = false;
            receiverPinLowered = true;//收发针沉到底
            protectiveCover = false;
            receiverBolt = false;
            isPinLowered = true;
        }
        if (temp.contains("453030313131313030")) {//453030313131313030
            receiverPinRised = false;
            receiverPinLowered = true;
            protectiveCover = false;
            receiverBolt = true;
            isTop = true;
            isPinLowered = true;
        }
        if (temp.contains("453130313031313030")) {//453130313031313030
            receiverPinRised = false;
            receiverPinLowered = true;
            protectiveCover = true;
            receiverBolt = false;
            isPinLowered = true;
        }
        if (temp.contains("453130313131313030")) {//453130313131313030
            receiverPinRised = false;
            receiverPinLowered = true;
            protectiveCover = true;
            receiverBolt = true;
            isTop = true;
            isPinLowered = true;
        }
        if (temp.contains("453031303031313030")) {//453031303031313030
            receiverPinRised = true;//收发针升到头
            receiverPinLowered = false;
            protectiveCover = false;
            receiverBolt = false;
        }
        if (temp.contains("453031303131313030")) {//453031303131313030
            receiverPinRised = true;
            receiverPinLowered = false;
            protectiveCover = false;
            receiverBolt = true;
            isTop = true;
        }
        if (temp.contains("453131303031313030")) {//453131303031313030
            receiverPinRised = true;
            receiverPinLowered = false;
            protectiveCover = true;
            receiverBolt = false;
        }
        if (temp.contains("453131303131313030")) {//453131303131313030
            receiverPinRised = true;
            receiverPinLowered = false;
            protectiveCover = true;
            receiverBolt = true;
            isTop = true;
//            newThread.interrupt();
        }
    }

    /**
     * 每5秒检测卡机通讯清况
     */
    private void checkCicmConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (times > 5) {
                        cicmErrorMsg = "卡机通讯异常";
                        MTCLog.log("卡机通讯异常");
                    }
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    MTCLog.log("InterruptedException", ex);
                }
            }
        }).start();
    }

    /**
     * 发送串口命令记录日志
     *
     * @param orderName
     * @param order
     */
    private void writeLog(String orderName, String order) {
        if ("1".equals(writerLog)) {
            MTCLog.log("XasCICM " + orderName + ":" + order);
        }
    }

    /**
     * 发送指令
     *
     * @param orderName 指令描述
     * @param order 指令
     */
    private synchronized void write(String orderName, String order) {
        StringBuilder sb = new StringBuilder();
        sb.append(orderName).append(splitReg).append(order);
        sendQueue.add(sb.toString());
    }

    /**
     * 发送指令
     *
     * @param orderName 指令描述
     * @param order 指令
     */
    private synchronized void write(String order) {
        try {
            if (outPut == null) {
                return;
            }
            outPut.write(HexUtils.decode(order));
            outPut.flush();
        } catch (IOException ex) {
            MTCLog.log("XasCICM :", ex);
        }
    }

    private final String splitReg = ",";
    private int reSendCnt;//指令重发次数

    @Override
    public boolean isPinTop() {
        return true;
    }

    @Override
    public boolean isPinBottom() {
        return true;
    }

    @Override
    public boolean isAvailable() {
        if (outPut == null) {
            return false;
        } else {
            return true;
        }
    }

    class SendTask extends MyTask {

        @Override
        public void run() {
            setAlive(true);
            try {
                String str = null;
                while (running) {
                    str = sendQueue.peek();
                    if (str != null) {
                        String[] buffer = str.split(splitReg);
                        String orderName = buffer[0];
                        String order = buffer[1];
                        if (order != null && orderName != null) {
                            responsed = false;
                            writeLog(orderName, order);
                            write(order);
                            String xasCicmOrders = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "XasCicmOrders", "");
                            if (xasCicmOrders.contains(order)) {//当前指令是否为需要重复发送指令
                                String interval = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "XasCicmInterval", "250");
                                int XasCicmInterval = IntegerUtils.parseString(interval);//获取等待卡机回应时间间隔
                                if (XasCicmInterval <= 0) {
                                    XasCicmInterval = 250;
                                }
                                long start = System.currentTimeMillis();
                                while (!responsed) {//卡机无回应
                                    long now = System.currentTimeMillis();
                                    if (now - start > XasCicmInterval) {//超过卡机等待时间间隔
                                        break;
                                    }
                                    Thread.sleep(1);
                                }
                                String cnt = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "XaxCicmResendMax", "1");
                                int max = IntegerUtils.parseString(cnt);
                                if (max <= 0) {
                                    max = 1;
                                }
                                if (!responsed && reSendCnt < max) {//指令无回应并且该指令已超过重发次数
                                    reSendCnt++;
                                } else {//指令有回应或已超过重发次数
                                    reSendCnt = 0;
                                    sendQueue.poll();//移除指令
                                }
                            } else {
                                reSendCnt = 0;
                                sendQueue.poll();//移除指令
                            }
                        }
                    }
                    Thread.sleep(100);
                }
            } catch (Throwable t) {
                MTCLog.log("XasCICM :", t);
            }
            setAlive(false);

        }
    }
}
