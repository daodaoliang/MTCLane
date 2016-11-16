package com.hgits.hardware.impl.cxp;

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
import com.hgits.hardware.CXP;
import com.hgits.util.HexUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.SetCOMs;
import com.hgits.vo.Constant;
import com.hgits.vo.Lane;
import java.util.Arrays;

public class GeaCXP implements SerialPortEventListener, CXP {

    private final String QUERY_ORDER = "0A450D";//查询CXP状态指令
    //向串口发送的初始化指令
    private StringBuilder order = new StringBuilder("0A5330303030303130340D");
    //串口号
    public SerialPort port;
    //获得串口通讯的输入流
    private InputStream in;
    //获得串口通讯的输出流
    private static OutputStream out;
    //监听器读取的数值
    public static String readString = "";
    //判断comm3是否运行
    private boolean running = true;
    //到达线圈是否有车 false表示无车
    private boolean reachCoilFlag;
    //通道线圈是否有车 false表示无车
    private boolean channelCoilFlag;
    //栏杆是否抬起
    public boolean liftFlag;
    //通行灯是绿色还是红色
    public boolean trafficLightFlag;
    //雨棚灯是红色还是熄灭
    public boolean canopyLightRedFlag;
    //雨棚灯是绿色还是熄灭
    public boolean canopyLightGreenFlag;
    /**
     * 雨棚灯标识 0 灯灭；1 红；2 绿
     */
    private int canopyFlag = 1;
    private boolean ok;
    /**
     * 控制柜盖子
     */
    public boolean controlBox;
    /**
     * 报警计数器
     */
    private int times;
    /**
     * CXP连接异常
     */
    private String cxpConnectionError = "";
    private boolean isLift;//自动栏杆是否抬起，true：抬起，false：没有抬起
    private String writerLog;//记录详细日志 1 记录 0 不记录
    private boolean lightGreen;//通行灯状态 true 绿灯 false 红灯
    private Thread hartBitThread = null;
    //栏杆抬起
    boolean lift = false;
    //栏杆关闭
    boolean unLift = false;
    //报警器报警
    boolean alarm = false;
    //报警器停止报警
    boolean unAlarm = false;
    //雨棚灯变绿
    boolean lightTurnGreen = false;
    //雨棚灯变红
    boolean lightTurnRed = false;
    //通行灯变绿
    boolean tifcLightGreen = false;
    //通行灯变红
    boolean tifcLightRed = false;

    public boolean isOk() {
        return ok;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public OutputStream getOut() {
        return out;
    }

    //无参构造器
    public GeaCXP() {
       
    }
    
    @Override
    public void run(){
        try {
            initiallize();
        } catch (InterruptedException ex) {
            MTCLog.log("CXP初始化异常", ex);
        } 
    }

    /**
     *
     * 初始化串口信息
     */
    public int initiallize() throws InterruptedException {
        int InitSuccess = 1;

        try {
            String info = SetCOMs.getCXP();
            String[] infos = info.split(",");
            String com = infos[0];

            int baudRate = Integer.parseInt(infos[1]);//波特率
            int dataBits = Integer.parseInt(infos[2]);//数据位
            int stopBits = Integer.parseInt(infos[3]);//停止位
            int parity = Integer.parseInt(infos[4]);//校验位
            writerLog = infos[6];
            //获得端口号
            CommPortIdentifier portName = CommPortIdentifier.getPortIdentifier(com);
            port = (SerialPort) portName.open(com, 2000);
            port.setSerialPortParams(baudRate, dataBits, stopBits, parity);
            port.setDTR(true);
            port.setRTS(true);
            port.addEventListener(this);
            port.notifyOnDataAvailable(true);

            in = port.getInputStream();
            out = port.getOutputStream();

            sendInitiallizeOrder();
            changeCanopyLightRed();
        } catch (NullPointerException e) {
            MTCLog.log("CXP：执行构造器出现空指针异常：", e);
        } catch (PortInUseException e) {
            cxpConnectionError = "CXP：串口被占用";
            MTCLog.log("CXP：串口被占用：", e);
        } catch (NoSuchPortException e) {
            cxpConnectionError = "CXP：没有这个串口";
            MTCLog.log("CXP：没有这个串口：", e);
        } catch (IOException e) {
            cxpConnectionError = "CXP：IO异常";
            MTCLog.log("CXP：IO异常：", e);
        } catch (UnsupportedCommOperationException e) {
            cxpConnectionError = "CXP：UnsupportedCommOperationException";
            MTCLog.log("CXP：UnsupportedCommOperationException：", e);
        } catch (TooManyListenersException e) {
            MTCLog.log("CXP：TooManyListenersException：", e);
            cxpConnectionError = "CXP：TooManyListenersException";
        }

        return InitSuccess;
    }

    /**
     * 串口监听器
     *
     * @param event
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
        times = 0;
        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                byte[] byt = null;
                try {
//                    while (in.available() > 0) {
//                        byt = new byte[in.available()];
//                        in.read(byt);
//                    }
                    byte[] buffer = new byte[128];
                    int len = in.read(buffer);
                    if (len <= 0) {
                        break;
                    } else {
                        byt = Arrays.copyOfRange(buffer, 0, len);
                    }
//                    readStr = new String(byt);//readStr 为\n....\r型
//
//                    byte[] bts = readStr.getBytes();
                    String order = HexUtils.encode(byt);
                    writeLog("CXP返回信息:", order);
                    checkCoil(order);
//                    readStr = HexUtils.encode(byt);//readStr 为0A....0D型 16进制
                    readString = new String(order);

                } catch (Exception ex) {
                    MTCLog.log("串口监听器IO异常：", ex);
                }
                break;
        }
    }
    /**
     * 判断线圈是否感应到车,
     */
    StringBuilder sbRead = new StringBuilder();

    public void checkCoil(String readStr) {
        if (out == null) {
            MTCLog.log("CXP:输出流为null");
            return;
        }

        //处理异常信息
        if (!readStr.startsWith("0A") || !readStr.endsWith("0D")) {
            //王国栋2016-10-12增加，发现异常信息时重新查询CXP状态
            queryStatus();
            //////////////////////////////////////////////////
            sbRead.append(readStr);
            writeLog("异常信息", sbRead.toString());

            try {
                int i = sbRead.indexOf("0D");//没有0D
                if (i == -1) {
                    return;
                }
                int j = sbRead.indexOf("0A");//有0A

                while (j != -1 && i != -1) {//没有0A时，跳出循环

                    checkHasCar(sbRead.substring(j, i + 2));//解析第一个0A...0D

                    //王国栋2016-10-12增加，排除"0D0A"数据导致死循环情况
                    if (j == i + 2) {//发现"0D0A"数据,删除0D
                        sbRead.delete(0, i + 2);
                    } else {
                        sbRead.delete(j, i + 2);//删除第一个0A...0D
                    }
                    j = sbRead.indexOf("0A");//有0A

                    i = sbRead.indexOf("0D");//没有0D

                }
            } catch (Exception e) {
                writeLog("字符串截取异常", e.getMessage());
                MTCLog.log("CXP " + e.getMessage(), e);
            }
        } else {
            try {
                sbRead.delete(0, sbRead.length());
                sbRead.append(readStr);
                //王国栋2016-10-13增加，排除"0D0A"数据导致死循环情况
                int i = sbRead.indexOf("0D");//0D下标
                int j = sbRead.indexOf("0A");//0A下标
//                while (sbRead.indexOf("0A") != -1) {
                while (j != -1 && i != -1) {//没有0A时，跳出循环
                    checkHasCar(sbRead.substring(sbRead.indexOf("0A"), sbRead.indexOf("0D") + 2));
                    sbRead.delete(sbRead.indexOf("0A"), sbRead.indexOf("0D") + 2);
                    //王国栋2016-10-13增加，排除"0D0A"数据导致死循环情况
                    // sbRead.delete(sbRead.indexOf("0A"), sbRead.indexOf("0D") + 2);
                    if (j == i + 2) {//发现"0D0A"数据,删除0D
                        sbRead.delete(0, i + 2);
                    } else {
                        sbRead.delete(j, i + 2);//删除第一个0A...0D
                    }

                    j = sbRead.indexOf("0A");//有0A

                    i = sbRead.indexOf("0D");//没有0D

                }
            } catch (Exception e) {
                writeLog("字符串截取异常", e.getMessage());
                MTCLog.log("CXP " + e.getMessage(), e);
            }
        }
    }

    private void checkHasCar(String readStr) {
        //检测指令长度，小于判断线圈是否有车指令长度，则 不需要判断
        if (readStr.length() < "0A453030303031300D".length()) {//0A453030303030303030303031300D
//                               0A4530303031313130300D   2015-05-17 15:43:51 875
//                               0A453030303030303030303031300D
            return;
        }
        if (!readStr.startsWith("0A45")) {
            return;
        }
        if (readStr.length() > "0A453030303031300D".length()) {
            write("收到线圈返回信息过长，发送", "0A4E32340D");
        }
        writeLog("判断线圈是否有车", readStr);
        //判断线圈是否感应到车
        if (readStr.contains("3030303031300D")) {//0A45
            write("主机答应", "0A410D");
            reachCoilFlag = false;
            channelCoilFlag = false;
            controlBox = true;
        }
        if (readStr.contains("3030303031310D")) {
            write("主机答应", "0A410D");
            reachCoilFlag = false;
            channelCoilFlag = true;
            controlBox = true;
        }

        if (readStr.contains("3030303031340D")) {
            write("主机答应", "0A410D");
            reachCoilFlag = true;
            channelCoilFlag = false;
            controlBox = true;
        }
        if (readStr.contains("3030303031350D")) {
            write("主机答应", "0A410D");
            reachCoilFlag = true;
            channelCoilFlag = true;
            controlBox = true;
        }
        if (readStr.contains("3130303031300D")) {
            write("主机答应", "0A410D");
            reachCoilFlag = false;
            channelCoilFlag = false;
        }
        if (readStr.contains("3130303031310D")) {
            write("主机答应", "0A410D");
            reachCoilFlag = false;
            channelCoilFlag = true;
        }
        if (readStr.contains("3130303031340D")) {
            write("主机答应", "0A410D");
            reachCoilFlag = true;
            channelCoilFlag = false;
        }
        if (readStr.contains("3130303031350D")) {
            write("主机答应", "0A410D");
            reachCoilFlag = true;
            channelCoilFlag = true;
        }
        if (readStr.contains("3031303031300D")) {
            write("主机答应", "0A410D");
            reachCoilFlag = false;
            channelCoilFlag = false;
            controlBox = false;
        }
        if (readStr.contains("3031303031310D")) {
            write("主机答应", "0A410D");
            reachCoilFlag = false;
            channelCoilFlag = true;
            controlBox = false;
        }
        if (readStr.contains("3031303031340D")) {
            write("主机答应", "0A410D");
            reachCoilFlag = true;
            channelCoilFlag = false;
            controlBox = false;
        }
        if (readStr.contains("3031303031350D")) {
            write("主机答应", "0A410D");
            reachCoilFlag = true;
            channelCoilFlag = true;
            controlBox = false;
        }
    }

    /**
     * 初始化的时候发送初始化指令
     */
    public void sendInitiallizeOrder() throws IOException, InterruptedException {
        /**
         * 此线程每5秒向CXP板发送心跳指令
         */
        hartBitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    if (lift) {

                        write("自动栏杆抬起", order.replace(15, 16, "0").toString());
                        lift = false;
                    }
                    if (unLift) {
                        write("自动栏杆放下", order.replace(15, 16, "1").toString());
                        unLift = false;
                    }

                    if (alarm) {
                        String alarmCoder = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION,"alarm", "1");

                        if (alarmCoder.equals("2")) {
                            write("报警", order.replace(17, 18, "2").toString());
                        } else if (alarmCoder.equals("1")) {
                            write("报警", order.replace(17, 18, "1").toString());
                        }
                        alarm = false;
                    }
                    if (unAlarm) {
                        write("停止报警", order.replace(17, 18, "0").toString());
                        unAlarm = false;
                    }

                    if (lightTurnGreen) {//雨棚灯绿
                        if (lightGreen) {//通行灯绿灯
                            write("雨棚灯灭掉", order.replace(19, 20, "7").toString());
                            write("雨棚灯变绿", order.replace(19, 20, "F").toString());
                        } else {
                            write("雨棚灯灭掉", order.replace(19, 20, "5").toString());
                            write("雨棚灯变绿", order.replace(19, 20, "D").toString());
                        }

                        lightTurnGreen = false;
                    }
                    if (lightTurnRed) {//雨棚灯变红
                        if (lightGreen) {//通行灯状态
                            write("雨棚灯灭掉", order.replace(19, 20, "7").toString());
                            write("雨棚灯变红", order.replace(19, 20, "3").toString());
                        } else {
                            write("雨棚灯灭掉", order.replace(19, 20, "5").toString());
                            write("雨棚灯变红", order.replace(19, 20, "1").toString());
                        }

                        lightTurnRed = false;
                    }

                    if (tifcLightGreen) {//通行灯绿
                        if (canopyFlag == 1) {//雨棚灯 1红 2绿
                            if (isLift) {//栏杆抬起
                                order.replace(15, 16, "0");
                            } else {
                                order.replace(15, 16, "1");
                            }
                            write("通行灯变绿", order.replace(19, 20, "3").toString());

                        } else if (canopyFlag == 2) {
                            if (isLift) {//栏杆抬起
                                order.replace(15, 16, "0");
                            } else {
                                order.replace(15, 16, "1");
                            }
                            write("通行灯变绿", order.replace(19, 20, "F").toString());
                        }

                        tifcLightGreen = false;
                    }
                    if (tifcLightRed) {//通行灯红

                        if (canopyFlag == 1) {//雨棚灯红
                            write("通行灯变红", order.replace(19, 20, "1").toString());
                        } else if (canopyFlag == 2) {//雨棚灯绿
                            write("通行灯变红", order.replace(19, 20, "D").toString());
                        }

                        tifcLightRed = false;
                    }
                    try {
                        write("心跳指令", "0A5A0D");
                        //CXP板回应 5A0D0A
                        //判断CXP板是否回应5A0D0A
                        if (readString.contains("0A5A0D")) {
                            writeLog("收到心跳回应", "0A5A0D");
                            times = 0;
                            cxpConnectionError = "";
                        } else {
                            times++;
                            if (times > 5) {
                                cxpConnectionError = "CXP板通讯异常";
                                if (Lane.getInstance().isEntry()) {
                                    cxpConnectionError = "39";
                                }
                                if (Lane.getInstance().isExit()) {
                                    cxpConnectionError = "14";
                                }
                                MTCLog.log("CXP板通讯异常");
                            }
                        }

                        if (lift || unLift || alarm || unAlarm || lightTurnGreen || lightTurnRed || tifcLightGreen || tifcLightRed) {
                            continue;
                        }
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
//                        MTCLog.log("InterruptedException", ex);
                    }
                }
            }
        }, "hartBitThread");

        //发送初始化指令
        write("发送初始化指令", "0A433135313031353130313531303135313039393130393931303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031300D");
        write("初始化时发送", "0A4E32340D");
        long t1 = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        //根据接收的指令发送数据
        while (true) {
            long t2 = System.currentTimeMillis();
            if ((t2 - t1) > 5000) {
                MTCLog.log("CXP 初始化时没有收到0A4E32340D");
                break;
            }
            sb.append(readString);
            String str = getStartWith0AEndWith0D(sb, "0A4E32340D".length());
            if (sb.toString().contains(str)) {//0A4E32340D

                write("收到串口0A4E32340D 发送", order.toString());
                sb.delete(0, sb.length());
                break;
            } else {
                Thread.sleep(50);
            }
        }
        write("获取设备状态", "0A450D");

        long t3 = System.currentTimeMillis();
        while (true) {
            long t4 = System.currentTimeMillis();
            if ((t4 - t3) > 5000) {
                MTCLog.log("CXP 初始化时没有收到线圈返回");
                break;
            }
            sb.append(readString);
            String str = getStartWith0AEndWith0D(sb, "0A453030303031300D".length());
            if (sb.toString().contains(str)) {
                sb.delete(0, sb.length());
                break;
            } else {
                Thread.sleep(50);
            }
        }
        hartBitThread.start();
    }

    /**
     * 获取指定长度的指令
     *
     * @param order 指令
     * @param length 指定长度
     * @return
     */
    private String getStartWith0AEndWith0D(StringBuilder sb1, int length) {
        StringBuilder sb = new StringBuilder(sb1);

        //取出0A....0D
        while (sb.indexOf("0A") != -1) {
            //删除0A前面的字符
            sb.delete(0, sb.indexOf("0A"));

            if (sb.toString().contains("0D")) {
                String str = sb.substring(sb.indexOf("0A"), sb.indexOf("0D") + 2);
                sb.delete(sb.indexOf("0A"), sb.indexOf("0D") + 2);
                if (str.length() == length) {
                    return str;
                }
            } else {
                break;
            }
        }
        return "-1";
    }

    /**
     * 雨棚灯变红
     */
    public void changeCanopyLightRed() {
        canopyFlag = 1;
        lightTurnRed = true;
//        hartBitThread.interrupt();
        interruptHartBitThread();
    }

    /**
     * 雨棚灯变绿
     */
    public void changeCanopyLightGreen() {
        canopyFlag = 2;
        lightTurnGreen = true;
//        hartBitThread.interrupt();
        interruptHartBitThread();
    }

    /**
     * 自动栏杆抬起
     */
    public void liftRailing() {
        lift = true;
//        hartBitThread.interrupt();
        interruptHartBitThread();
        isLift = true;
    }

    /**
     * 自动栏杆放下
     */
    public void lowerRailing() {
        unLift = true;
//        hartBitThread.interrupt();
        interruptHartBitThread();
        isLift = false;
    }

    /**
     * 通行灯变绿
     */
    public void changeTrafficLightGreen() {
        tifcLightGreen = true;
        lightGreen = true;
//        hartBitThread.interrupt();
        interruptHartBitThread();

    }

    /**
     * 通行灯变红
     */
    public void changeTrafficLightRed() {
        tifcLightRed = true;
        lightGreen = false;//
//        hartBitThread.interrupt();
        interruptHartBitThread();

//        trafficLightFlag = !check();
    }

    /**
     * 检测通道线圈上是否有车
     */
    public boolean checkPassageCoil() {
        return channelCoilFlag;
    }

    /**
     * 检测到达线圈上是否有车
     */
    public boolean checkArriveCoil() {
        return reachCoilFlag;
    }

    /**
     * 警报器报警
     */
    public void warningAlarm() {
        alarm = true;
//        hartBitThread.interrupt();
        interruptHartBitThread();

    }

    /**
     * 警报器停止报警
     */
    public void stopAlarm() {
        unAlarm = true;
//        hartBitThread.interrupt();
        interruptHartBitThread();

    }

    private void interruptHartBitThread() {
        try {
            hartBitThread.interrupt();
        } catch (Exception e) {
            MTCLog.log(e.getMessage(), e);
        }
    }

    /**
     * 获取雨棚等状态标识
     *
     * @return 0 雨棚灯灭 1 雨棚灯红色 2 雨棚等绿色
     */
    public int getCanopyFlag() {
        return canopyFlag;
    }

    /**
     * 获取CXP通讯异常信息
     *
     * @return
     */
    public String getCxpErrorMsg() {
        String error = "";
        if (cxpConnectionError.equals("")) {
            error = null;
        } else {
            error = cxpConnectionError;
        }
        return error;
    }

    /**
     * 发送串口命令记录日志
     *
     * @param orderName
     * @param order
     */
    private void writeLog(String orderName, String order) {
        if ("1".equals(writerLog)) {
            MTCLog.log("CXP " + orderName + ":" + order);
        }
    }

    /**
     * 发送指令
     *
     * @param orderName 指令描述
     * @param order 指令
     */
    private synchronized void write(String orderName, String order) {
        try {
            if (out == null) {
                MTCLog.log("CXP out为null！");
                return;
            }

            out.write(HexUtils.decode(order));
            out.flush();
            writeLog("发送" + orderName, order);
            //制定线圈是否有车指令长度
            if (order.equals("0A4E32340D")) {
                return;
            }
            //主机主动查询线圈上是否有车指令
            if (order.equals("0A450D")) {
                return;
            }
            //主机应答指令
            if (order.equals("0A410D")) {
                return;
            }
            //心跳指令
            if (order.equals("0A5A0D")) {
                return;
            }
            if (order.equals("0A433135313031353130313531303135313039393130393931303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031300D")) {
                return;
            }

            for (int i = 2; i <= 5; i++) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    writeLog(ex.getMessage(), Thread.currentThread().getName());
                }
                if (!readString.contains("0A410D")) {

                    out.write(HexUtils.decode(order));
                    out.flush();

                    writeLog("发送" + orderName + "第" + i + " 次", order);
                } else {
                    readString = "";
                    break;
                }

            }

        } catch (IOException ex) {
            MTCLog.log("CXP " + orderName, ex);
        }
    }

    @Override
    public void setArrriveCoil(boolean flag) {
        reachCoilFlag = flag;
    }

    @Override
    public void setPassageCoil(boolean flag) {
        this.channelCoilFlag = flag;
    }

    /**
     * 王国栋 2016-10-12增加 发送查询CXP板状态指令
     */
    private void queryStatus() {
        write("查询CXP状态", QUERY_ORDER);
    }
}
