package com.hgits.hardware.impl.weighsystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

import com.hgits.common.log.MTCLog;
import com.hgits.hardware.WeighSystem;
import com.hgits.util.AxleUtils;
import com.hgits.util.CRC16;
import com.hgits.util.DoubleUtils;
import com.hgits.util.HexUtils;
import com.hgits.util.SetCOMs;
import com.hgits.util.StringUtils;
import com.hgits.vo.Axle;
import com.hgits.vo.AxleGroup;

/**
 * 德鲁泰计重仪实现类(随岳专用)
 * @author zengzb
 *
 */
public class DltWeighSystem implements SerialPortEventListener,WeighSystem {
    private int carNos;
    private String carType;
    private Map<Integer, String> mapError = new HashMap<Integer, String>();//称重仪状态
    private InputStream input;
    private OutputStream output;
    private StringBuffer sb2 = new StringBuffer();//数据源；监听器返回的
    private String length; //当前数据长度 16进制
    private static String order00;//00指令
    private String order04;//04指令
    private String order06;//06指令
    private ExecutorService javaThreadPool = Executors.newFixedThreadPool(4);
    public String errerMSG = "";//称重仪错误信息
    private boolean connection;//判断主从机是否通讯正常
    private int times;//报错次数
    private List<Map<Integer, AxleGroup>> carInfos = new ArrayList<Map<Integer, AxleGroup>>();
    private List<List<Axle>> singleAxleInfo = new ArrayList<List<Axle>>();
    private List<String> speedList = new ArrayList<String>();
    private boolean reverseFlag;//有车到出计重仪
    private String writerLog;
    private boolean singleAxleInfoIsOk;//单轴信息已经处理完毕
    private static String lastOrder00 = null;
    private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
    
    public boolean isReverseFlag() {
        return reverseFlag;
    }
    
    /**
     * 初始化
     */
    public DltWeighSystem() {
        try {
            //加载设备状态故障代号
            loadErrorCode();
            
            //加载串口配置
            String info = SetCOMs.getPAT();
            String[] infos = info.split(",");
            String com = infos[0];

            int baudRate = Integer.parseInt(infos[1]);//波特率
            int dataBits = Integer.parseInt(infos[2]);//数据位
            int stopBits = Integer.parseInt(infos[3]);//停止位
            int parity = Integer.parseInt(infos[4]);//校验位
            writerLog = infos[6];
            SerialPort port = (SerialPort) CommPortIdentifier.getPortIdentifier(com).open("计重", 2000);

            port.setDTR(true);
            port.setRTS(true);
            port.notifyOnDataAvailable(true);
            port.setSerialPortParams(baudRate, dataBits, stopBits, parity);
            port.addEventListener(this);

            input = port.getInputStream();
            output = port.getOutputStream();

            sendCheckConnection();//发送设备连接状态检查命令
        } catch (NullPointerException e) {
            MTCLog.log("串口未配置：", e);
        } catch (PortInUseException e) {
            MTCLog.log("计重：串口被占用：", e);
            errerMSG = "计重：串口被占用";
        } catch (NoSuchPortException e) {
            MTCLog.log("计重：没有这个串口", e);
            errerMSG = "计重：没有这个串口";
        } catch (IOException e) {
            MTCLog.log("计重：IO异常", e);
            errerMSG = "计重：IO异常";
        } catch (UnsupportedCommOperationException e) {
            MTCLog.log("计重：UnsupportedCommOperationException", e);
            errerMSG = "计重：UnsupportedCommOperationException";
        } catch (TooManyListenersException e) {
            MTCLog.log("计重：TooManyListenersException", e);
            errerMSG = "计重：TooManyListenersException";
        } catch (Exception e) {
            MTCLog.log("计重：Exception:", e);
        }
    }

    /**
     * 监听车辆到达计重仪。 1、计重仪处理完车辆信息后，主动发送车辆信息数据给本系统 2、有车辆倒车，本系统自动删除最后一辆车信息
     *
     * @param event 串口事件
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
        errerMSG = "";
        //计数器有数据时归零
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
                try {
                    //有数据，表明通讯正常，将连接状态置为true
                    connection = true;
                    
                    int length = 0;
                    while ((length = input.available()) > 0) {                    	
                        byte[] buffer = new byte[length];
                        input.read(buffer);
                        for (int a = 0; a < buffer.length; a++) {
                            String str = StringUtils.changeIntToHex(buffer[a], 2);
                            sb2.append(str);
                        }
                        
                        queue.add(sb2.toString());
                        
                        writeLog("接收计重设备返回信息:", sb2.toString());
                        if (sb2.toString().startsWith("ff0000")) {
                        	//由于德鲁泰对 00指令会重发三次，系统只要接收到一次即可，故增加相应判断处理
                        	if(lastOrder00 == null || !lastOrder00.equals(sb2.toString())){
                        		lastOrder00 = sb2.toString();
                        	}else{
                        		continue;
                        	}                       	

                            //处理从机发来的0命令
                            reverseFlag = false;
                            int length1 = 0;
                            try {
                                String length_str = sb2.substring(6, 8);
                                length1 = Integer.parseInt(length_str, 16);
                            } catch (Exception e) {
                            	MTCLog.log("转换order00指令长度异常!"+e.getMessage(), e);
                                continue;
                            }
                            try {
                                order00 = sb2.substring(0, length1 * 2);
                                writeLog("收到order00", order00);

                                //判断指令中的长度值是否合法，不合法则删除整个串中从0开始至下一个ff0000开始的位置字符串
                                if(length1 >= 19){
                                	sb2.delete(0, length1 * 2);
                                }else{
                                	int ipos = sb2.toString().indexOf("ff", 2);
                                	if(ipos == -1){
                                		sb2.delete(0, sb2.length());
                                	}else{
                                		sb2.delete(0, ipos);
                                	}
                                }                              
                                //write("主机发送命令1", "FF0001DF42");//by zengzb 要求去掉不能发送
                            } catch (Exception e) {
                            	MTCLog.log("截取order00指令异常!"+e.getMessage(), e);
                                continue;
                            }
                        } else if (sb2.toString().startsWith("ff0001")) {//
                            //处理从机发来的1命令
                            try {
                                String length_str = sb2.substring(6, 8);
                                int length1 = Integer.parseInt(length_str, 16);

                                String order01 = sb2.substring(0, length1 * 2);
                                writeLog("收到车辆信息order01", order01);
                                List<Axle> axles = null;
                                boolean ok = true;
                                try {
                                    //封装车辆信息，添加轴组信息
                                    ok = packCarInfo(order00);
                                    
                                    writeLog("解析order00完毕", order00);
                                    axles = paserOder01(order01);
                                    writeLog("解析order01完毕", order01);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MTCLog.log(e.getMessage(), e);
                                }
                                if (axles != null && !axles.isEmpty()) {
                                    //添加单轴信息
                                    singleAxleInfo.add(axles);
                                    singleAxleInfoIsOk = true;
                                    writeLog("添加单轴信息完毕", "");
                                }else if(ok){//ordre00解析成功，但是roder01解析失败
                                    writeLog("ordre00解析成功，但是roder01解析失败","移除最后的车辆信息（第一次）");
                                    carInfos.remove(carInfos.size()-1);
                                }
                                //主机发送删除命令
                                write("主机发送删除命令", "FF00010168B3");
                                
                                //判断指令中的长度值是否合法，不合法则删除整个串中从0开始至下一个ff0001开始的位置字符串
                                if(length1 >= 19){
                                	sb2.delete(0, length1 * 2);
                                }else{
                                	int ipos = sb2.toString().indexOf("ff", 2);
                                	if(ipos == -1){
                                		sb2.delete(0, sb2.length());
                                	}else{
                                		sb2.delete(0, ipos);
                                	}
                                }                               
                                writeLog("单轴信息", singleAxleInfo + "");

                            } catch (Exception e) {
                            	MTCLog.log("解析01指令异常!"+e.getMessage(), e);
                                continue;
                            }
                        } else if (sb2.toString().startsWith("ff0004")) {
                            try {
                                String length_str = sb2.substring(6, 8);
                                //获取数据长度
                                int length1 = Integer.parseInt(length_str, 16);

                                order04 = sb2.substring(0, length1 * 2);
                                writeLog("收到order04", order04);
                                String crc16 = CRC16.getCRC16("FF0000");//ff0000?
                                //判断缓存中是否有车；00表示没有车
                                if (!order04.substring(order04.length() - 6, order04.length() - 4).equals("00")) {
                                    //缓存中有车辆
                                    //主机发送命令0
                                    write("缓存中有车,主机发送00指令", "FF0000" + crc16);
                                }
                                
                                //判断指令中的长度值是否合法，不合法则删除整个串中从0开始至下一个ff0004开始的位置字符串
                                if(length1 >= 14){
                                	sb2.delete(0, 28);
                                }else{
                                	int ipos = sb2.toString().indexOf("ff", 2);
                                	if(ipos == -1){
                                		sb2.delete(0, sb2.length());
                                	}else{
                                		sb2.delete(0, ipos);
                                	}
                                }
                            } catch (Exception e) {
                            	MTCLog.log("解析04指令异常!"+e.getMessage(), e);
                                continue;
                            }
                        } else if (sb2.toString().startsWith("ff0005")) {
                            try {
                                //收到命令5时，则表示通讯正常
                                connection = true;
                                //获取状态码，正常工作默认0
                                String stst = sb2.substring(8, 10);
                                String str = decoder05(stst);
                                errerMSG = str;

                                String order05 = sb2.substring(0, 14);
                                writeLog("收到order05", order05);
                                
                                String length_str = sb2.substring(6, 8);
                                //获取数据长度
                                int length1 = Integer.parseInt(length_str, 16);
                                
                                //判断指令中的长度值是否合法，不合法则删除整个串中从0开始至下一个ff0005开始的位置字符串
                                if(length1 >= 7){
                                	sb2.delete(0, 14);
                                }else{
                                	int ipos = sb2.toString().indexOf("ff", 2);
                                	if(ipos == -1){
                                		sb2.delete(0, sb2.length());
                                	}else{
                                		sb2.delete(0, ipos);
                                	}
                                }
                                writeLog("称重仪状态", getErrorMsg());
                            } catch (Exception e) {
                            	MTCLog.log("解析05指令异常!"+e.getMessage(), e);
                                continue;
                            }
                        } else if (sb2.toString().startsWith("ff0006")) {
                            try {
                                reverseFlag = true;
                                sb2.substring(0, 26);
                                writeLog("收到order06", sb2.toString());
                                
                                String length_str = sb2.substring(6, 8);
                                //获取数据长度
                                int length1 = Integer.parseInt(length_str, 16);
                                
                                //判断指令中的长度值是否合法，不合法则删除整个串中从0开始至下一个ff0006开始的位置字符串
                                if(length1 >= 13){
                                	sb2.delete(0, 26);
                                }else{
                                	int ipos = sb2.toString().indexOf("ff", 2);
                                	if(ipos == -1){
                                		sb2.delete(0, sb2.length());
                                	}else{
                                		sb2.delete(0, ipos);
                                	}
                                }                                
                            } catch (Exception e) {
                            	MTCLog.log("解析06指令异常!"+e.getMessage(), e);
                                continue;
                            }
                        } else {
                            MTCLog.log("计重其他命令" + sb2);
                            //2015-8-1增加，异常指令会影响正常指令的删除异常指令，
                            int index = sb2.lastIndexOf("ff00");
                            if (index == -1) {
                                index = sb2.lastIndexOf("FF00");
                            }
                            if (index > 0) {
                                sb2.delete(0, index);
                            }
                            if (sb2.length() > 1024) {//指令过长，删除
                                sb2.delete(0, sb2.length());
                            }
                        }
                    }
                } catch (Exception ex) {
                    MTCLog.log("计重：Exception:"+ex.getMessage(), ex);
                }
        }
    }



    /**
     * 发送计重设备状态指令
     * @author zengzb
     */
    private void sendCheckConnection(){
        //每10秒称重仪自检，主机发送命令5
        javaThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String crc16 = CRC16.getCRC16("FF0005");
                    while (true) {
                        write("设备状态查询指令", "FF0005" + crc16);
                        connection = false;
                        Thread.sleep(10000);
                    }
                } catch (Exception e) {
                    MTCLog.log("计重：Exception:"+e.getMessage(), e);
                }
            }
        });

        //称重仪通讯异常时，在界面显示
        javaThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        times++;
                        if (!connection && times > 10) {
                            MTCLog.log("称重仪通讯故障-------------------");
                            errerMSG = "称重仪通讯故障...";
                        }
                    }
                } catch (InterruptedException e) {
                    MTCLog.log("计重：Exception:"+e.getMessage(), e);
                }
            }
        });    	
    }
    
    /**
     * 封装车辆信息
     *
     * @param order00 计重仪返回的00信息
     */
    private boolean packCarInfo(String order00) {
        boolean ok = false;
        Map<Integer, AxleGroup> map = new HashMap<Integer, AxleGroup>();
        List<String> carInfo = paserOder00(order00);
        if(carInfo.isEmpty()){
            return false;
        }
        //获得此车辆的总体轴型，例如0102，每2位一个单元
        String axleType = carInfo.get(7);
        //获得此车辆的总体轴量，例如00320060，每4位一个单元
        String weight = carInfo.get(5);

        //赋值
        int j = 0;
        //轴组的位置
        int k = 1;
        for (int i = 0; i < axleType.length(); i += 2) {

            AxleGroup axleGroup = new AxleGroup();

            axleGroup.setAxleType(axleType.substring(i, i + 2));
            axleGroup.setAxleWeight(Integer.parseInt(weight.substring(j, j += 4), 16));
            map.put(k, axleGroup);
            k++;
        }
        if (!map.isEmpty()) {
            carInfos.add(map);
            ok = true;
        }else{
            ok = false;
        }
        writeLog("车辆信息", carInfos + "");
        writeLog("解析order00是否成功", ok+"");
        return ok;
    }

    /**
     * 解析计重００指令
     *
     * @param orders
     * @return List（[指令，时间，超载，速度，轴数，重量（16进制），桥距，轴型（010203）,重量（10进制）]）
     *
     */
    public List paserOder00(String orders) {
        List<String> carInfos = new ArrayList<String>();
        try {

            String str = orders.replace(" ", "");
            //获得00指令
            String order = str.substring(4, 6);
            //获得时间数据
            String time = str.substring(8, 22);
            //获得超载信息
            String overLoad = str.substring(22, 24);
            //获得速度信息
            String speed = str.substring(24, 28);

            speedList.add(DoubleUtils.mul(Integer.parseInt(speed, 16), 0.1) + "");
            //获得桥数
            String axleNo = str.substring(30, 32);

            if(Integer.parseInt(axleNo,16)>=50){
                return carInfos;
            }
            //获得轴组数
            String axleGroupNo = str.substring(32, 34);//ff00001a07de0c0c111f0c0100000003010e1007000000009207
            //获得整车重量
            String weight = "";
            //获得桥距
            String axleLenght = "";
            String axleType = "";
//            int totalAxleNo = Integer.parseInt(axleNo, 16);
            int i = 34;
            int j = 42;
            switch (axleGroupNo) {
                case "01":
                    weight = str.substring(34, 38);
                    axleType = str.substring(38, 40);
                    break;
                case "02":
                    weight = str.substring(i, j);
                    if (!str.substring(j, j + 2).equals("00")) {
                        int a = 0;
                        axleLenght = str.substring(a = j += 4, j += 4);
                        axleType = str.substring(a - 4, a);
                    }
                    break;
                case "03":
                    weight = str.substring(i, j += 4);
                    if (!str.substring(j, j + 2).equals("00")) {
                        int a = 0;
                        axleLenght = str.substring(a = j += 6, j += 8);
                        axleType = str.substring(a - 6, a);
                    }
                    break;
                case "04":
                    weight = str.substring(i, j += 8);
                    if (!str.substring(j, j + 2).equals("00")) {
                        int a = 0;
                        axleLenght = str.substring(a = j += 8, j += 12);
                        axleType = str.substring(a - 8, a);
                    }
                    break;
                case "05":
                    weight = str.substring(i, j += 12);
                    if (!str.substring(j, j + 2).equals("00")) {
                        int a = 0;
                        axleLenght = str.substring(a = j += 10, j += 16);
                        axleType = str.substring(a - 10, a);
                    }
                    break;
                case "06":
                    weight = str.substring(i, j += 16);
                    if (!str.substring(j, j + 2).equals("00")) {
                        int a = 0;
                        axleLenght = str.substring(a = j += 12, j += 20);
                        axleType = str.substring(a - 12, a);
                    }
                    break;
                case "07":
                    weight = str.substring(i, j += 20);
                    if (!str.substring(j, j + 2).equals("00")) {
                        int a = 0;
                        axleLenght = str.substring(a = j += 14, j += 24);
                        axleType = str.substring(a - 14, a);

                    }
                    break;
                case "08":
                    weight = str.substring(i, j += 24);
                    if (!str.substring(j, j + 2).equals("00")) {
                        int a = 0;
                        axleLenght = str.substring(a = j += 16, j += 28);
                        axleType = str.substring(a - 16, a);
                    }

                    break;
                case "09":
                    weight = str.substring(i, j += 28);
                    if (!str.substring(j, j + 2).equals("00")) {
                        int a = 0;
                        axleLenght = str.substring(a = j += 18, j += 32);
                        axleType = str.substring(a - 18, a);
                    }
                    break;
                case "0a":
                    weight = str.substring(i, j += 32);
                    if (!str.substring(j, j + 2).equals("00")) {
                        int a = 0;
                        axleLenght = str.substring(a = j += 20, j += 36);
                        axleType = str.substring(a - 20, a);
                    }
                    break;
                case "0b":
                    weight = str.substring(i, j += 36);
                    if (!str.substring(j, j + 2).equals("00")) {
                        int a = 0;
                        axleLenght = str.substring(a = j += 22, j += 40);
                        axleType = str.substring(a - 22, a);
                    }
                    break;
                /**
                 * 王国栋 2017-10-17增加，用于处理11轴以上的轴组
                 */
                default:
                    String reg = "[0-9A-Fa-f]{2}";
                    if (axleGroupNo.matches(reg)) {
                        int cnt = Integer.parseInt(axleGroupNo, 16);//轴组数量
                        int len = str.length();
                        int countLen = 34 + cnt * 4 + cnt * 2 + (cnt - 1) * 4 + 4;
                        if (len < countLen) {//计重信息长度错误
                            MTCLog.log("轴组信息长度" + len + "与理论长度" + countLen + "不符,无法获取轴组信息");
                        } else {
                            weight = str.substring(34, 34 + cnt * 4);
                            axleType = str.substring(34 + cnt * 4, 34 + cnt * 4 + cnt * 2);
                            axleLenght = str.substring(34 + cnt * 4 + cnt * 2, 34 + cnt * 4 + cnt * 2 + (cnt - 1) * 4);
                        }
                    } else {
                        MTCLog.log("轴组信息中轴组数量" + axleGroupNo + "异常,无法获取轴组信息");
                    }
                    break;

            }
            int w = getWeight(Integer.parseInt(axleGroupNo, 16), weight);

            carInfos.add(order);
            carInfos.add(time);
            carInfos.add(overLoad);
            carInfos.add(speed);
            carInfos.add(axleNo);
            carInfos.add(weight);
            carInfos.add(axleLenght);
            carInfos.add(axleType);
            
            this.carType = axleType;

            carInfos.add(w + "");

            return carInfos;
        } catch (Exception e) {
            MTCLog.log("计重：Exception:", e);
        }
        return carInfos;
    }

    /**
     * 将16进制的字符串转换成int型
     *
     * @param axle：轴数
     * @param w :长度是4的倍数，每4位为一个数
     * @return
     */
    public int getWeight(int axle, String w) {
        int weight = 0;
        int j = 0;
        for (int i = 0; i < axle; i++) {
            weight += Integer.parseInt(w.substring(j, j += 4), 16);
        }
        return weight;
    }

    /**
     * 经过计重设备的车辆信息
     *
     * @return List<Map<Integer, AxleGroup>>
     */
    public synchronized List<Map<Integer, AxleGroup>> getVehList() {
        List<Map<Integer, AxleGroup>> tempList = AxleUtils.getAxleGroupMapListFromList(carInfos); //list值传递

        return tempList;
    }

    /**
     * 删除第一辆车轴组信息
     */
    public void deleteFirstVehCarInfo() {
        if (!carInfos.isEmpty()) {
            carInfos.remove(0);
        }
    }

    /**
     * 删除第一辆车单轴信息
     */
    public void deleteFirstVehAxleInfo() {
        if (!singleAxleInfo.isEmpty()) {
            singleAxleInfo.remove(0);
        }
    }

    /**
     * 删除第一辆车速度信息
     */
    public void deleteFirstVehSpeetInfo() {
        if (!speedList.isEmpty()) {
            speedList.remove(0);
        }
    }

    /**
     * 获取车辆通过称重仪速度集合
     *
     * @return
     */
    public List<String> getSpeedList() {
        return new ArrayList(speedList);
    }

    /**
     * 获取单轴信息，单轴轴型，单轴轴重
     *
     * @return
     */
    public List<List<Axle>> getSingleAxleInfo() {
        List<List<Axle>> tempAxleList = AxleUtils.getAxleListListFromList(singleAxleInfo); //list值传递
        return tempAxleList;
    }

    public void setSingleAxleInfo(List<List<Axle>> singleAxleInfo) {
        this.singleAxleInfo = singleAxleInfo;
    }

    /**
     * 设置倒车标志
     *
     * @param reverseFlag
     */
    public void setReverseFlag(boolean reverseFlag) {
        this.reverseFlag = reverseFlag;
    }

    /**
     * 加载称重仪故障代码
     */
    private void loadErrorCode() {
        String str = "11111111";
        String[] error_coder = {"秤台传感器故障", "主车辆分离器故障", "辅助车辆分离器故障", "轮胎识别器故障", "通讯故障", "其它错误", "其它错误", "其它错误"};
        for (int i = 0; i < 8; i++) {
            String c = String.valueOf(str.charAt(i));
            if (c.equals("1")) {//0：正常  1：异常（具体见错误描述）
                mapError.put(7-i, error_coder[i]);
            }
        }
    }

    /**
     * 解析05指令返回的状态值
     * @param coder
     * @return
     */
    private String decoder05(String coder) {
        //转二进制字符串
        String order = Integer.toBinaryString(Integer.parseInt(coder, 16));
        while (order.length() < 8) {
            order = "0" + order;
        }

        MTCLog.log("设备返回状态信息："+order);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            String c = String.valueOf(order.charAt(i));
            if (c.equals("1")) {
                sb.append(mapError.get(7-i)+"_");
            }
        }
        return sb.toString();
    }

    /**
     * 获取计重仪的错误信息
     *
     * @return 计重仪的错误信息
     */
    public String getErrorMsg() {
        String error = "";
        if ("".equals(errerMSG)) {
            error = null;
        } else {
            error = errerMSG.split("_")[0];
        }
        return error;
    }

    /**
     * 解析01指令，获取单轴信息
     *
     * @param order01
     * @return
     */
    private List<Axle> paserOder01(String order01) {
        List<Axle> list = new ArrayList<Axle>();
        String carInfo = "";
        String singleTotalWeight = "";
        String axleType = "";
        int totalAxle = 0;
        try{
            carInfo = order01.substring(30);//从轴数开始...
            totalAxle = Integer.parseInt(carInfo.substring(0, 2), 16);//此处改为对轴数进行16进制解析（计重设备发送轴数可能超过9轴）
            //单轴总重量
            singleTotalWeight = carInfo.substring(4, (totalAxle + 1) * 4);
            //单轴轴型
            axleType = carInfo.substring((totalAxle + 1) * 4, (totalAxle + 1) * 4 + totalAxle * 2);
            //如果有00轴型，则将01变成02，将00变成01
            if (axleType.contains("00")) {
                axleType = axleType.replace("01", "02").replace("00", "01");
            }
        }catch(Exception e){
            MTCLog.log(e.getMessage(), e);
            return list;
        }

        int j = 0;
        int n = 0;
        writeLog("解析01指令，获取单轴信息", order01);

//        String axleDistance = order01.substring(order01.length() - totalAxle * 4, order01.length() - 4);
        String totalSingleAxleType = "";

        for (int i = 0; i < totalAxle; i++) {
            try {
                Axle axle = new Axle();
                //获取单轴轴重
                String singleWeight = singleTotalWeight.substring(j, j += 4);//16进制的重量
                //获取单轴轴型
                String singleAxleType = axleType.substring(n, n += 2);

                axle.setWeight(Integer.parseInt(singleWeight, 16));
                axle.setAxleType(singleAxleType);

                totalSingleAxleType += singleAxleType;
                list.add(axle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final String str = totalSingleAxleType;
        writeLog("当前单轴信息", order01);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getAxleType(str);
                } catch (Exception e) {
                    MTCLog.log("计重，解析三联轴异常：" + str, e);
                }
            }
        }).start();
        //获得轴型
        return list;
    }

    /**
     * 根据单轴信息获取轴组信息
     *
     * @param totalSingleAxleType
     */
    private void getAxleType(String totalSingleAxleType) throws Exception {
        if(carType.equals("")){
            writeLog("计重返回order00为空", "");
            writeLog("已处理缓存车辆数 carNos="+carNos, "");
            writeLog("车辆轴组集合 carInfos="+carInfos.size(), "");
            writeLog("车辆单轴集合 singleAxleInfo="+singleAxleInfo.size(), "");
            return;
        }
        if(totalSingleAxleType.equals("")){
            return;
        }
//        totalSingleAxleType = "01020202020202";
        //根据轴组类型分解成单轴信息
        StringBuilder sb = new StringBuilder();

        StringBuilder sb1 = new StringBuilder(totalSingleAxleType);
        //新的单轴信息
        StringBuilder sb3 = new StringBuilder();

        //获取轴型
        int start = 0;
        int j = 0;
        for (int i = 0; i < carType.length() / 2; i++) {//010507
            String s = carType.substring(j, j += 2);
            switch (s) {
                case "01":
                    start = start + 1;
                    sb.append("01,");
                    break;
                case "02":
                    start = start + 1;
                    sb.append("02,");
                    break;
                case "03":
                    start = start + 2;
                    sb.append("0101,");
                    break;
                case "04":
                    start = start + 2;
                    sb.append("0102,");
                    break;
                case "05":
                    start = start + 2;
                    sb.append("0202,");
                    break;
                case "06":
                    start = start + 3;
                    sb.append("010101,");
                    break;
                case "07":
                    start = start + 3;
                    sb.append("000000,");
                    break;
                default:
                    //有4联轴以上的轴组信息
                    //获取单轴轴重
                    int a = 0;
                    while (!singleAxleInfoIsOk) {//一直等待单轴信息处理完成
                        try {
                            Thread.sleep(1);
                            a++;
                            if (a == 1000 * 5) {
                                writeLog("5秒钟仍未解析完单轴信息", "");
                                break;
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    //当前单轴信息
                    List<Axle> axleList = singleAxleInfo.get(singleAxleInfo.size() - 1);
                    for(Axle axle : axleList){
                        if(axle.getWeight() == 0){
                            carNos++;
                            writeLog("计重返回错误信息，单轴重量为0，取消本次多轴解析", "");
                            return;
                        }
                    }
                    //判断此轴组在哪个位置
                    int position = i + 1;
                    //获取此轴组的重量

                    //计重返回数据精度丢失
                    double weight = carInfos.get(carInfos.size() - 1).get(position).getAxleWeight() * 10;
                    //累加单轴轴重，判断是否与weight相等
                    //从哪一个单轴开始加？？
                    writeLog("从" + start + " 开始累加轴重", "");
                    double w = 0;
                    int k = 0;
//
                    long ww = Math.round(weight);
                    boolean match = false;
                    for (int m = start; m < axleList.size(); m++) {
                        double sWeight = axleList.get(m).getWeight();
                        w = w+sWeight;
                        k++;
                        long w1 = Math.round(w * 10);
                        if(w1 > ww){
                            carNos++;
                            writeLog("计重返回错误信息，单轴重量大于轴组数量，取消本次多轴解析", "");
                            return;
                        }
                        if (w1 == ww /*|| Math.abs(ww/10-w1/10) <= 5*/) {
                            writeLog("单轴累加重量等于轴重重量", "");
                            match = true;
                            break;
                        }
                    }
                    if(!match){
                        carNos++;
                        writeLog("计重返回错误信息，累加单轴重量时，没有匹配上轴组重量，取消本次多轴解析", "");
                        return;
                    }
                    writeLog("获得3轴以上的轴组的轴数:" + k, "");
                    start = start + k;
//                    sb.append(k)
                    StringBuilder sb2 = new StringBuilder();
                    for (int n = 0; n < k; n++) {
                        sb2.append("00");
                    }
                    sb.append(sb2.append(","));
            }
        }
        singleAxleInfoIsOk = false;
        String[] ss = sb.toString().split(",");
        //0102020202020202
        for (String s : ss) {
            try {
                String sss = sb1.substring(0, s.length()).toString();
                sb1.delete(0, s.length());
                sb3.append(sss + ",");
            } catch (Exception e) {
                e.printStackTrace();
//                writeLog("解析三连轴"+ e);
                MTCLog.log(e.getMessage(), e);
            }
        }

        totalSingleAxleType = sb3.toString().substring(0, sb3.length() - 1);

        String[] strs = totalSingleAxleType.split(",");
        writeLog("解析三连轴", totalSingleAxleType);
        //获取新的轴组信息
        int i = 1;
        for (String s : strs) {
            switch (s) {
                case "010102":
                    Map<Integer, AxleGroup> map1 = carInfos.get(getVehList().size() - 1);
                    AxleGroup a1 = map1.get(i);
                    a1.setAxleType("07");
                    break;
                case "010201":
                    Map<Integer, AxleGroup> map6 = carInfos.get(getVehList().size() - 1);
                    AxleGroup a6 = map6.get(i);
                    a6.setAxleType("07");
                    break;
                case "020101":
                    Map<Integer, AxleGroup> map2 = carInfos.get(getVehList().size() - 1);
                    AxleGroup a2 = map2.get(i);
                    a2.setAxleType("07");
                    break;
                case "020201":
                    Map<Integer, AxleGroup> map3 = carInfos.get(getVehList().size() - 1);
                    AxleGroup a3 = map3.get(i);
                    a3.setAxleType("08");
                    break;
                case "010202":
                    Map<Integer, AxleGroup> map4 = carInfos.get(getVehList().size() - 1);
                    AxleGroup a4 = map4.get(i);
                    a4.setAxleType("08");
                    break;
                case "020102":
                    Map<Integer, AxleGroup> map7 = carInfos.get(getVehList().size() - 1);
                    AxleGroup a7 = map7.get(i);
                    a7.setAxleType("08");
                    break;
                case "020202":
                    Map<Integer, AxleGroup> map5 = carInfos.get(getVehList().size() - 1);
                    AxleGroup a5 = map5.get(i);
                    a5.setAxleType("09");
                    break;
                default:
                    if (s.length() >= 8) {
                        Map<Integer, AxleGroup> map0 = carInfos.get(getVehList().size() - 1);
                        AxleGroup a0 = map0.get(i);
                        a0.setAxleType(Integer.toHexString(6 + s.length() / 2));
                    }
            }
            i++;
        }
        carNos++;
        
        writeLog("已处理缓存车辆数 carNos="+carNos, "");
        writeLog("车辆轴组集合 carInfos="+carInfos.size(), "");
        writeLog("车辆单轴集合 singleAxleInfo="+singleAxleInfo.size(), "");
        
        if (carNos != carInfos.size()) {
            writeLog("==============================================================", "");
        }
    }

    /**
     * 发送串口命令记录日志
     *
     * @param orderName
     * @param order
     */
    private void writeLog(String orderName, String order) {
        if ("1".equals(writerLog)) {
            MTCLog.log("Weight " + orderName + ":" + order);
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
            output.write(HexUtils.decode(order));
            output.flush();

            writeLog(orderName, order);
        } catch (IOException ex) {
            MTCLog.log("计重 :" + orderName, ex);
        }
    }

    /**
     * 车辆信息减一
     */
    public void carNosDecline() {
        carNos--;
    }

    /**
     * 获取缓存中的车辆数
     *
     * @return
     */
    public int getCarNos() {
        return carNos;
    }

	public String getReturnMsg() {
		return queue.poll();
	}

	public boolean isConnection() {
		return connection;
	}
}