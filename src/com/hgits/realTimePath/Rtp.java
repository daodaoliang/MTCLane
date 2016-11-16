package com.hgits.realTimePath;

import com.hgits.control.ThreadPoolControl;
import com.hgits.realTimePath.vo.RequestPath;
import com.hgits.realTimePath.vo.PathInfo;
import com.hgits.realTimePath.vo.EntryStation;
import com.hgits.realTimePath.vo.CostRecord;
import com.hgits.realTimePath.vo.HeartBeat;
import com.hgits.exception.MTCException;
import com.hgits.realTimePath.vo.QueryPath;
import com.hgits.realTimePath.vo.QueryPathResp;
import com.hgits.realTimePath.vo.RealTimeRoad;
import com.hgits.realTimePath.vo.TimeSync;
import com.hgits.realTimePath.vo.TimeSyncResp;
import com.hgits.util.DoubleUtils;
import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.PriceCalculateUtils;
import com.hgits.util.rate.ParamCacheQuery;
import com.hgits.vo.Constant;
import com.hgits.vo.Vehicle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * 与路径识别服务器"+pathServerip+"通信，定期发送心跳检测指令，获取实时路径信息，收费信息上传
 *
 * @author Wang Guodong
 */
public class Rtp implements Runnable {

    HeartBeat hb;//心跳检测
    String pathServerip;//路径识别服务器"+pathServerip+"ip
    int pathPort;//路径识别服务器"+pathServerip+"通信端口
    Socket sc;//与路径识别服务器通信端口
    OutputStream os;//与路径识别服务器通信端口输出流
    InputStream is;//与路径识别服务器通信端口输入流
    String roadid;//本地路段号
    String stationid;//本地收费站号
    String laneid;//本地车道号
    int serial = 1;//消息流水号
    int version;//协议版本号
    int commandHeartBeat = 0x0001;//心跳检测指令
    int commandEntryStation = 0x0002;//车辆入站指令
    int commandRequestPath = 0x0003;//请求路径指令
    int commandCostRecord = 0x0004;//收费记录上传指令
    int commandQueryPath = 0x0009;//路径明细查询
    int commandSyncCostRecord;//同步收费记录指令
    int commandTimeSync = 0x0006;//时间同步指令
    int respHeartBeat = 0x80000001;//心跳检测反馈
    int respEntryStation = 0x80000002;//车辆入站反馈
    int respRequestPath = 0x80000003;//请求路径反馈
    int respCostRecord = 0x80000004;//同步收费记录反馈
    int respTimeSync = 0x80000006;//时间同步反馈
    int respQueryPath = 0x80000009;//路径明细查询反馈
    int respSyncCostRecord;//同步收费记录指令反馈
    Map<Integer, PathInfo> piMap = new HashMap();//记录按实际路径收费的集合 key对应消息流水号，value对应路径信息
    Map<Integer, QueryPathResp> qprMap = new HashMap();//请求路径明细的集合 key对应消息流水号，value对应路径明细
    boolean realTimeFlag;//与路径识别服务器通信标识
    private TimeSync ts;//时间同步实体类
    private static final Logger logger = Logger.getLogger(Rtp.class.getName());
    boolean running;
    private boolean testFlag;//与路径识别正常通信，但不采用路径识别的路径信息（测试用），也不进行时间同步
    public final String TEMPPATH = "temp/temp_realTime.txt";//临时信息文件
    ConcurrentLinkedDeque<Object> tempMsgQueue = new ConcurrentLinkedDeque<>();//临时信息队列
    private String rtrFlag;//路径识别标识号 0不启用路径识别 1启用路径识别 2与路径识别进行通信但不采用路径识别结果
    ConcurrentLinkedDeque<byte[]> msgQueue = new ConcurrentLinkedDeque<>();//向路径识别服务器服务器发送信息的队列
    RealTimeSvc rtSvc = new RealTimeSvc();//路径识别服务类
    private boolean timeSyncFlag;//与路径识别服务器进行时间同步标识
    private SocketChannel socketChannel;
    private ConcurrentLinkedQueue<byte[]> sendQueue = new ConcurrentLinkedQueue<>();//发送消息的队列
    private ConcurrentLinkedQueue<byte[]> recvQueue = new ConcurrentLinkedQueue<>();//接收消息的队列
    private int maxSendSize = 100;
    private int maxRecvSize = 100;

    /**
     * 获取序列号（加同步锁）
     *
     * @return 序列号
     */
    private synchronized int getSerial() {
        int i = serial;
        serial++;
        if (serial >= 65535) {//序列号最大为65535
            serial = 1;
        }
        return i;
    }

    public Rtp() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        FileInputStream fis = null;
        try {
            Properties prop = new Properties();
            fis = new FileInputStream(Constant.PROP_MTCLANE_RTP);
            prop.load(fis);
            String realTimeRoadFlag = prop.getProperty("realTimeRoadFlag", "0");//获取路径识别启用标识
            if (("1").equals(realTimeRoadFlag)) {//启用路径识别
                String flag = prop.getProperty("timeSyncFlag", "0");//获取时间同步标识 1时间同步 其他不进行时间同步
                timeSyncFlag = "1".equals(flag);//确认时间同步标识
                testFlag = false;
                running = true;
                String ver = prop.getProperty("pathVersion", "10");
                version = IntegerUtils.parseHexString(ver);
                pathServerip = prop.getProperty("pathServerip", "0.0.0.0");
                String temp = MyPropertiesUtils.getProperties(Constant.PROP_SOCKET, "pathSocket", "49777");
                pathPort = IntegerUtils.parseString(temp);
                roadid = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "roadId", "00");
                stationid = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "stationId", "00");
                laneid = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "laneNo", "000");
                hb = new HeartBeat();
                hb.setStationid(roadid + stationid);
                hb.setLaneid(laneid);
                ts = new TimeSync();
                ts.setStationID(roadid + stationid);
                ts.setChannelNo(laneid);
                if (!realTimeRoadFlag.equals(rtrFlag)) {//路径识别标志号发生变化时记录日志
                    logger.warn("启用按实际路径收费，路径识别服务器" + pathServerip + ":" + pathPort);
                }
            } else if (("2").equals(realTimeRoadFlag)) {//测试模式，与路径识别服务器进行通信，但不采用路径识别信息
                testFlag = true;
                running = true;
                String ver = prop.getProperty("pathVersion", "10");
                version = IntegerUtils.parseHexString(ver);
                pathServerip = prop.getProperty("pathServerip", "0.0.0.0");
                String temp = MyPropertiesUtils.getProperties(Constant.PROP_SOCKET, "pathSocket", "49777");
                pathPort = IntegerUtils.parseString(temp);
                roadid = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "roadId", "00");
                stationid = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "stationId", "00");
                laneid = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "laneNo", "000");
                hb = new HeartBeat();
                hb.setStationid(roadid + stationid);
                hb.setLaneid(laneid);
                ts = new TimeSync();
                ts.setStationID(roadid + stationid);
                ts.setChannelNo(laneid);
                if (!realTimeRoadFlag.equals(rtrFlag)) {//路径识别标志号发生变化时记录日志
                    logger.warn("测试模式，启用按实际路径收费，路径识别服务器" + pathServerip + ":" + pathPort + ",但不采用路径识别结果");
                }
            } else {//不启用路径识别信息
                if (!realTimeRoadFlag.equals(rtrFlag)) {//路径识别标志号发生变化时记录日志
                    logger.warn("不启用路径识别服务器");
                }
                realTimeFlag = false;
                running = false;
                testFlag = true;
            }
            rtrFlag = realTimeRoadFlag;
        } catch (Exception ex) {
            logger.error("路径识别服务初始化异常", ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                logger.error("关闭文件读取流异常", ex);
            }
        }
    }

    @Override
    public void run() {
        //向路径识别服务器发送信息的线程
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                    }
                    if (msgQueue.size() > 100) {//消息队列信息超过100条，清空消息队列
                        msgQueue.clear();
                    }
                    try {
                        if (!running) {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                            }
                            continue;
                        }
                        openSocket();
                        while (realTimeFlag) {
                            try {
                                Thread.sleep(1);//每毫秒检测一次
                            } catch (InterruptedException ex) {
                            }
                            if (!msgQueue.isEmpty()) {//首先检测信息队列，有信息先处理信息
                                sendMessage(msgQueue.poll());//消息队列，先进先出
                            }
                        }
                    } catch (Exception ex) {
                        logger.error("向路径识别服务器" + pathServerip + "发送信息异常:", ex);
                    } finally {
                        closeSocket();
                        try {
                            Thread.sleep(60 * 1000);
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            }
        });
        //接收路径识别服务器信息线程

        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                    }
                    try {
                        if (!running) {
                            Thread.sleep(3000);
                            continue;
                        }
                        openSocket();
                        while (realTimeFlag) {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException ex) {
                            }
                            if (is != null) {
                                byte[] b = new byte[4];
                                int len = is.read(b, 0, 4);//返回信息前四个字节表示此次返回信息的长度
                                if (len == -1) {
                                    logger.warn("路径识别服务器" + pathServerip + "已关闭");
                                    break;
                                } else if (len == 4) {
                                    len = FormatTransferUtil.hBytesToInt(b);
                                    byte[] b1 = new byte[len];
                                    is.read(b1, 4, len - 4);//获取除信息长度外的其他全部信息
                                    System.arraycopy(b, 0, b1, 0, 4);//信息合并
                                    checkResponse(b1);//解析服务器返回信息
                                } else {
                                    logger.warn("获取路径识别服务器" + pathServerip + "返回信息异常，返回信息不足4个字节，无法获取返回信息长度");
                                    break;
                                }

                            } else {
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        logger.error("获取路径识别服务器" + pathServerip + "返回信息异常", ex);
                    } finally {
                        closeSocket();
                        try {
                            Thread.sleep(60 * 1000);
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            }
        });
        //解析临时文件并发送给路径识别服务器线程
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (!running) {
                            Thread.sleep(3000);
                            continue;
                        }
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                    }
                    if (realTimeFlag) {
                        File file = new File(TEMPPATH);
                        if (file.exists()) {
                            FileInputStream fis = null;
                            InputStreamReader isr = null;
                            BufferedReader br = null;
                            try {
                                fis = new FileInputStream(file);
                                isr = new InputStreamReader(fis);
                                br = new BufferedReader(isr);
                                String str;
                                while ((str = br.readLine()) != null) {
                                    char[] buffer = {0};
                                    String regex = new String(buffer);//JSONOBJECT不能解析的乱码，需要进行排除
                                    str = str.replaceAll(regex, "");
                                    JSONObject jo = JSONObject.fromObject(str);
                                    if (jo.get(EntryStation.class.getSimpleName()) != null) {
                                        EntryStation esi = EntryStation.parseJSON(jo);
                                        sendEnterStationInfo(esi);
                                    } else if (jo.get(CostRecord.class.getSimpleName()) != null) {
                                        CostRecord cr = CostRecord.parseJSON(jo);
                                        sendCostRecordInfo(cr);
                                    }
                                }
                                fis.close();
                                isr.close();
                                br.close();
                                FileUtils.deleteQuietly(file);
                            } catch (Exception ex) {
                                logger.error("上传实时路径临时记录文件异常", ex);
                            } finally {
                                try {
                                    if (fis != null) {
                                        fis.close();
                                    }
                                    if (isr != null) {
                                        isr.close();
                                    }
                                    if (br != null) {
                                        br.close();
                                    }
                                } catch (IOException ex) {
                                    logger.error("关闭实时路径临时记录文件流异常", ex);
                                }
                            }
                        }
                    }
                }
            }
        });
        //加载配置信息线程
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                    }
                    try {
                        init();
                        if (piMap.size() > 100) {//记录按实际路径收费的集合内容过大，清理
                            piMap.clear();
                        }
                        Thread.sleep(60 * 1000);//每分钟判断一次
                    } catch (Exception ex) {
                        logger.error("路径识别服务器" + pathServerip + "信息异常", ex);
                    }
                }
            }
        });
        //临时文件处理线程
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1);
                        File file = new File(TEMPPATH);
                        if (file.exists() && file.isFile()) {
                            long len = file.length();
                            if (len > 1024 * 1024 * 3) {//临时文件大于3M，删除临时文件
                                logger.warn("发现临时文件大小为" + len + "大于3M，删除临时文件");
                                file.delete();
                            }
                        }
                        if (!running) {//未启用路径识别
                            Thread.sleep(3000);
                            continue;
                        }
                        if (!tempMsgQueue.isEmpty()) {//临时信息队列不为空
                            Object ob = tempMsgQueue.poll();
                            recordTemp(ob);//记录临时信息
                        }
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        logger.error("异常", ex);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ex1) {
                        }
                    }
                }
            }
        });
        //心跳检测控制线程
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sendHeartBeatMsg();
                        Thread.sleep(3000);//每3秒钟发送一次心跳检测
                    } catch (Exception ex) {
                        logger.warn("路径识别心跳检测线程异常", ex);
                    }
                }

            }
        });
        //时间同步线程
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sendTimeSync();
                        Thread.sleep(3600 * 1000);//每小时进行一次时间同步
                    } catch (Exception ex) {
                        logger.warn("路径识别时间同步线程异常", ex);
                    }
                }
            }
        });
    }

    /**
     * 记录临时文件（追加记录）
     */
    private void recordTemp(Object ob) {
        if (ob == null) {
            return;
        }
        String str = null;
        if (ob instanceof EntryStation) {
            logger.warn("记录车辆入站信息到临时文件");
            EntryStation esi = (EntryStation) ob;
            str = esi.toJSONString();
        } else if (ob instanceof CostRecord) {
            logger.warn("记录收费记录到临时文件");
            CostRecord cr = (CostRecord) ob;
            str = cr.toJSONString();
        } else {
            return;
        }
        char[] buffer = {0};
        String regex = new String(buffer);//JSONOBJECT不能解析的乱码，需要进行排除
        str = str.replaceAll(regex, "");//排除JSONOBJECT无法识别的字符
        File file = new File(TEMPPATH);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                logger.error("创建路径识别临时文件出现异常", ex);
            }
        }
        FileOutputStream fos = null;
        FileChannel fc = null;
        FileLock lock = null;
        try {
            fos = new FileOutputStream(file, true);//追加记录
            fc = fos.getChannel();
            lock = fc.tryLock();
            ByteBuffer bb = ByteBuffer.wrap((str + "\n").getBytes());
            fc.write(bb);
            fc.force(true);
        } catch (IOException ex) {
            logger.error("记录临时文件" + str + "时出现异常", ex);
        } finally {
            try {
                if (lock != null) {
                    lock.release();
                    lock.close();
                }
                if (fc != null) {
                    fc.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception ex) {
                logger.error("异常", ex);
            }
        }
    }

    /**
     * 创建与路径识别服务器"+pathServerip+"通信的端口
     */
    private synchronized void openSocket() {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.bind(new InetSocketAddress(pathPort));
            socketChannel.configureBlocking(false);//非阻塞模式
        } catch (IOException ex) {
            logger.error(ex, ex);
        }
    }

    /**
     * 关闭与路径识别服务器"+pathServerip+"通信的端口
     */
    private synchronized void closeSocket() {
        try {
            if (os != null) {
                os.close();
            }
            if (is != null) {
                is.close();
            }
            if (sc != null) {
                sc.close();
            }
            realTimeFlag = false;
        } catch (IOException ex) {
            logger.error("关闭与路径识别服务器" + pathServerip + "通信端口失败:", ex);
        }
    }

    /**
     * 发送心跳检测指令
     */
    private void sendHeartBeatMsg() throws IOException {
        if (!realTimeFlag || !running) {
            return;
        }
        byte[] buffer2 = ByteUtil.getByteArray(version);
        byte[] buffer3 = ByteUtil.getByteArray(commandHeartBeat);
        byte[] buffer4 = ByteUtil.getByteArray(getSerial());
        byte[] buffer5 = ByteUtil.getByteArray(new Date());
        String jsonStr = hb.toJSONString();
        String md5 = MD5Util.MD5(jsonStr);
        byte[] buffer6 = ByteUtil.getByteArray(md5);
        byte[] buffer7 = ByteUtil.getByteArray(jsonStr);
        int len = 4 + buffer2.length + buffer3.length + buffer4.length + buffer5.length + buffer6.length + buffer7.length;
        byte[] buffer1 = ByteUtil.getByteArray(len);
        List<byte[]> list = new ArrayList();
        list.add(buffer1);
        list.add(buffer2);
        list.add(buffer3);
        list.add(buffer4);
        list.add(buffer5);
        list.add(buffer6);
        list.add(buffer7);
        byte[] buffer = ByteUtil.mergeBytes(list);
        msgQueue.add(buffer);
    }

    /**
     * 检测路径识别服务器返回信息
     *
     * @param buffer
     * @return
     */
    private boolean checkResponse(byte[] buffer) {
        if (buffer == null || buffer.length < 4) {
            return false;
        }
        byte[] len = Arrays.copyOf(buffer, 4);
        int le = FormatTransferUtil.hBytesToInt(len);
        if (le != buffer.length) {
            logger.warn("路径识别服务器" + pathServerip + "返回信息长度异常" + Arrays.toString(buffer));
            return false;
        }
        byte[] ver = Arrays.copyOfRange(buffer, 4, 8);
        int ve = FormatTransferUtil.hBytesToInt(ver);
        byte[] command = Arrays.copyOfRange(buffer, 8, 12);
        int co = FormatTransferUtil.hBytesToInt(command);
        String commandStr = getCommandName(co);
        byte[] sn = Arrays.copyOfRange(buffer, 12, 16);
        int se = FormatTransferUtil.hBytesToInt(sn);
        byte[] dhm = Arrays.copyOfRange(buffer, 16, 33);
        String dhmStr = new String(dhm);
        byte[] md5Buffer = Arrays.copyOfRange(buffer, 33, 65);
        String md5Str = new String(md5Buffer);
        byte[] resp = Arrays.copyOfRange(buffer, 65, le);
        String respStr = new String(resp);
        if (!md5Str.equalsIgnoreCase(MD5Util.MD5(respStr))) {
            logger.warn("返回信息MD5验证错误");
            return false;
        }
        logger.debug("路径识别服务器" + pathServerip + "返回信息如下："
                + "数据包长度：" + le
                + " ,协议版本号：" + ve
                + " ,命令字：" + commandStr
                + " ,流水号：" + se
                + ",消息发送时间" + dhmStr
                + ",校验码" + md5Str
                + " ,返回结果：" + respStr);
        int re = getRespStatus(respStr);
        if (co == respHeartBeat) {//心跳检测
            realTimeFlag = (re == 0);
        } else if (co == respTimeSync) {//时间同步，非测试模式
            if (testFlag) {//测试模式下，不与路径识别服务器进行时间同步
                timeSyncFlag = false;
            }
            if (timeSyncFlag) {//当前与路径识别服务器进行时间同步
                TimeSyncResp tsr = TimeSyncResp.parseJSONStr(respStr);
                logger.warn("准备进行时间同步:" + tsr);
                if (tsr.getStatus() == 0) {
                    Date dateTime = tsr.getSystemTime();
                    if (dateTime != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat();
                        sdf.applyPattern("yyyy-MM-dd");
                        String date = sdf.format(dateTime);
                        sdf.applyPattern("HH:mm:ss");
                        String time = sdf.format(dateTime);
                        try {
                            Runtime.getRuntime().exec("cmd /c date " + date);
                            Runtime.getRuntime().exec("cmd /c time " + time);
                        } catch (IOException ex) {
                            logger.error("与路径识别服务器" + pathServerip + "时间同步异常", ex);
                        }
                    }
                }
            }

        } else if (co == respRequestPath) {//当前返回信息为请求实际通行路径信息
            logger.warn("路径识别服务器" + pathServerip + "返回路径信息" + Integer.toHexString(respRequestPath));
            PathInfo pi = PathInfo.parseJSON(respStr);
            int status = pi.getStatus();
            if (status != 0) {
                logger.warn("路径识别服务器" + pathServerip + "返回路径信息状态码异常：" + status);
            } else {
                piMap.put(se, pi);
            }
        } else if (co == respQueryPath) {//请求路径明细答复
            logger.warn("路径识别服务器" + pathServerip + "返回路径信息" + Integer.toHexString(respQueryPath));
            JSONObject jo = JSONObject.fromObject(respStr);
            QueryPathResp qpr = (QueryPathResp) JSONObject.toBean(jo, QueryPathResp.class);
            qprMap.put(se, qpr);
        } else {

        }
        return true;
    }

    /**
     * 向路径识别服务器发送信息
     *
     * @param buffer
     */
    private void sendMessage(byte[] buffer) throws IOException {
        if (realTimeFlag && sc != null && !sc.isClosed() && os != null) {
            logger.warn("向路径识别服务器" + pathServerip + "发送信息:" + new String(buffer));
            os.write(buffer);
            os.flush();
        }
    }

    /**
     * 获取错误码中文定义
     *
     * @param code 错误码
     * @return 中文定义
     */
    private String getErrorInfo(int code) {
        String str;
        switch (code) {
            case 0:
                str = "成功";
                break;
            case 1:
                str = "错误的消息包";
                break;
            case 2:
                str = "服务器存储空间不足";
                break;
            case 3:
                str = "与省中心的连接中断";
                break;
            case 4:
                str = "计费信息不完整，不能够进行计费";
                break;
            case 5:
                str = "业务异常";
                break;
            default:
                str = "未知返回代码" + code;
                break;
        }
        return str;
    }

    /**
     * 根据指令代码获取其对应的指令名称
     *
     * @param command 指令代码
     * @return 指令名称
     */
    private String getCommandName(int command) {
        String str;
        switch (command) {
            case 0x0001:
                str = "心跳检测";
                break;
            case 0x0002:
                str = "车辆入站";
                break;
            case 0x0003:
                str = "请求路径";
                break;
            case 0x0004:
                str = "收费记录上传";
                break;
            case 0x0005:
                str = "图片上传";
                break;
            case 0x0006:
                str = "时间同步";
                break;
            case 0x0009:
                str = "确认计费";
                break;
            case 0x80000001:
                str = "心跳检测反馈";
                break;
            case 0x80000002:
                str = "车辆入站反馈";
                break;
            case 0x80000003:
                str = "请求路径反馈";
                break;
            case 0x80000004:
                str = "收费记录上传";
                break;
            case 0x80000005:
                str = "图片上传反馈";
                break;
            case 0x80000006:
                str = "时间同步反馈";
                break;
            case 0x80000009:
                str = "确认计费反馈";
                break;
            default:
                str = "未知指令" + command;
                break;
        }
        return str;
    }

    /**
     * 获取路径信息
     *
     * @param buffer
     * @return
     */
    private PathInfo getPathInfo(byte[] buffer) {
        try {
            PathInfo pi = new PathInfo();
            int len = 0;
            byte[] temp;
            List<String> list = new ArrayList();
            for (int i = 0; i < 6; i++) {
                int a = len;
                temp = Arrays.copyOfRange(buffer, len, 4);
                len += temp.length;
                int tempLen = FormatTransferUtil.hBytesToInt(temp);
                len += tempLen;
                String str = new String(buffer, a + 4, tempLen);
                list.add(str);
            }
            pi.setOrderSN(list.get(0));
            pi.setTotalFee(DoubleUtils.parseString(list.get(1)));
            pi.setTotalDistance(DoubleUtils.parseString(list.get(2)));
            pi.setPath(list.get(3));
            pi.setStatus(IntegerUtils.parseString(list.get(4)));
            pi.setInfo(list.get(5));
            return pi;
        } catch (Exception ex) {
            logger.error("解析路径信息" + Arrays.toString(buffer) + "时出现异常:", ex);
            return null;
        }
    }

    /**
     * 车辆入站信息上传至路径识别服务器
     *
     * @param esi 车辆入站信息
     */
    public void sendEnterStationInfo(EntryStation esi) {
        if (!running) {//未启用路径识别
            return;
        }
        if (!realTimeFlag) {//与路径识别服务器连接中断
            tempMsgQueue.add(esi);
            return;
        }
        try {
            byte[] buffer2 = FormatTransferUtil.toHH(version);
            byte[] buffer3 = FormatTransferUtil.toHH(commandEntryStation);
            int i = getSerial();
            byte[] buffer4 = FormatTransferUtil.toHH(i);
            byte[] buffer5 = ByteUtil.getByteArray(esi.getEntryTime());
            byte[] buffer6 = ByteUtil.getByteArray(MD5Util.MD5(esi.toJSONString()));
            byte[] buffer7 = ByteUtil.getByteArray(esi.toJSONString());
            int n = 4 + buffer2.length + buffer3.length + buffer4.length + buffer5.length + buffer6.length + buffer7.length;
            byte[] buffer1 = FormatTransferUtil.toHH(n);
            List<byte[]> byteList = new ArrayList();
            byteList.add(buffer1);
            byteList.add(buffer2);
            byteList.add(buffer3);
            byteList.add(buffer4);
            byteList.add(buffer5);
            byteList.add(buffer6);
            byteList.add(buffer7);
            byte[] buffer = ByteUtil.mergeBytes(byteList);
            if (msgQueue != null) {
                msgQueue.add(buffer);//将需要发送的信息加入信息发送队列
            }
        } catch (Exception e) {
            logger.error("车辆入站信息上传至路径识别服务器" + pathServerip + "异常", e);
        }
    }

    /**
     * 收费记录上传至路径识别服务器
     *
     * @param record 收费记录
     */
    public void sendCostRecordInfo(CostRecord record) {
        if (!running) {//未启用路径识别
            return;
        }
        if (!realTimeFlag) {//与路径识别服务器连接中断
            tempMsgQueue.add(record);
            return;
        }
        try {
            byte[] buffer2 = FormatTransferUtil.toHH(version);
            byte[] buffer3 = FormatTransferUtil.toHH(commandCostRecord);
            int i = getSerial();
            byte[] buffer4 = FormatTransferUtil.toHH(i);
            byte[] buffer5 = ByteUtil.getByteArray(new Date());
            byte[] buffer6 = ByteUtil.getByteArray(MD5Util.MD5(record.toJSONString()));
            byte[] buffer7 = ByteUtil.getByteArray(record.toJSONString());
            int n = 4 + buffer2.length + buffer3.length + buffer4.length + buffer5.length + buffer6.length + buffer7.length;
            byte[] buffer1 = ByteUtil.getByteArray(n);
            List<byte[]> list = new ArrayList();
            list.add(buffer1);
            list.add(buffer2);
            list.add(buffer3);
            list.add(buffer4);
            list.add(buffer5);
            list.add(buffer6);
            list.add(buffer7);
            byte[] buffer = ByteUtil.mergeBytes(list);
            if (msgQueue != null) {
                msgQueue.add(buffer);//将需要发送的信息加入信息发送队列
            }
        } catch (Exception e) {
            logger.error("收费记录上传至路径识别服务器" + pathServerip + "异常", e);
        }
    }

    /**
     * 向路径识别服务器请求实际收费路径 合理利用写卡时间（写卡前请求收费路径，写卡后获取实时路径）
     *
     * @param pr 请求实际收费路径信息
     * @return 请求对应的消息流水号 0表示连接中断或未启用路径识别服务器
     */
    public int requestForPath(RequestPath pr) {
        int i = 0;
        try {
            if (!realTimeFlag || !running) {//未启用路径识别信息或与路径识别服务器连接中断
                return 0;
            }
            byte[] buffer2 = FormatTransferUtil.toHH(version);
            byte[] buffer3 = FormatTransferUtil.toHH(commandRequestPath);
            i = getSerial();
            piMap.remove(i);//每次发送指令前先从结果集中清除该指令对应的结果
            byte[] buffer4 = FormatTransferUtil.toHH(i);
            byte[] buffer5 = ByteUtil.getByteArray(new Date());
            byte[] buffer6 = ByteUtil.getByteArray(MD5Util.MD5(pr.toJSONString()));
            byte[] buffer7 = ByteUtil.getByteArray(pr.toJSONString());
            int n = 4 + buffer2.length + buffer3.length + buffer4.length + buffer5.length + buffer6.length + buffer7.length;
            byte[] buffer1 = FormatTransferUtil.toHH(n);
            List<byte[]> list = new ArrayList();
            list.add(buffer1);
            list.add(buffer2);
            list.add(buffer3);
            list.add(buffer4);
            list.add(buffer5);
            list.add(buffer6);
            list.add(buffer7);
            byte[] buffer = ByteUtil.mergeBytes(list);
            if (msgQueue != null) {
                msgQueue.add(buffer);//将需要发送的信息加入信息发送队列
            }
        } catch (Exception ex) {
            logger.error("请求实际路径异常", ex);
        }
        return i;
    }

    /**
     * 向路径识别服务器请求路径明细信息
     *
     * @param qp 路径明细查询实体类
     * @return 请求对应的消息流水号 0表示连接中断或未启用路径识别服务器
     */
    public int queryPath(QueryPath qp) {
        int i = 0;
        try {
            if (!realTimeFlag || !running) {//未启用路径识别信息或与路径识别服务器连接中断
                return 0;
            }
            byte[] buffer2 = FormatTransferUtil.toHH(version);
            byte[] buffer3 = FormatTransferUtil.toHH(commandQueryPath);
            i = getSerial();
            qprMap.remove(i);//每次发送指令前先从结果集中清除该指令对应的结果
            byte[] buffer4 = FormatTransferUtil.toHH(i);
            byte[] buffer5 = ByteUtil.getByteArray(new Date());
            byte[] buffer6 = ByteUtil.getByteArray(MD5Util.MD5(qp.toJSONString()));
            byte[] buffer7 = ByteUtil.getByteArray(qp.toJSONString());
            int n = 4 + buffer2.length + buffer3.length + buffer4.length + buffer5.length + buffer6.length + buffer7.length;
            byte[] buffer1 = FormatTransferUtil.toHH(n);
            List<byte[]> list = new ArrayList();
            list.add(buffer1);
            list.add(buffer2);
            list.add(buffer3);
            list.add(buffer4);
            list.add(buffer5);
            list.add(buffer6);
            list.add(buffer7);
            byte[] buffer = ByteUtil.mergeBytes(list);
            if (msgQueue != null) {
                msgQueue.add(buffer);//将需要发送的信息加入信息发送队列
            }
        } catch (Exception ex) {
            logger.error("请求实际路径异常", ex);
        }
        return i;
    }

    /**
     * 根据消息流水号获取路径明细信息
     *
     * @param i 消息流水号
     * @return 路径明细信息
     */
    public QueryPathResp getQueryPath(int i) {
        try {
            if (!realTimeFlag || testFlag) {//测试模式，不采用路径明细
                return null;
            }
            QueryPathResp qpr = qprMap.get(i);
            return qpr;
        } catch (Exception ex) {
            logger.error("获取实际路径异常", ex);
            return null;
        } finally {
            qprMap.remove(i);
        }
    }

    /**
     * 根据消息流水号获取实时路径信息 合理利用写卡时间（写卡前请求收费路径，写卡后获取实时路径）
     *
     * @param i 消息流水号
     * @return 实时路径信息
     */
    public PathInfo getPathInfo(int i) {
        try {
            if (!realTimeFlag || testFlag) {//测试模式，不采用路径识别结果
                return null;
            }
            PathInfo pi = piMap.get(i);
            return pi;
        } catch (Exception ex) {
            logger.error("获取实际路径异常", ex);
            return null;
        } finally {
            piMap.remove(i);
        }
    }

    /**
     * 发送时间同步指令,获取同步时间
     */
    private void sendTimeSync() {
        if (!running) {//未启用路径识别
            return;
        }
        if (!realTimeFlag) {
            logger.warn("向路径识别服务器发送时间同步指令时发现与路径识别服务器连接断开");
            return;
        }
        try {
            byte[] buffer2 = FormatTransferUtil.toHH(version);
            byte[] buffer3 = FormatTransferUtil.toHH(commandTimeSync);
            int i = getSerial();
            byte[] buffer4 = FormatTransferUtil.toHH(i);
            byte[] buffer5 = ByteUtil.getByteArray(new Date());
            byte[] buffer6 = ByteUtil.getByteArray(MD5Util.MD5(ts.toJSONString()));
            byte[] buffer7 = ByteUtil.getByteArray(ts.toJSONString());
            int n = 4 + buffer2.length + buffer3.length + buffer4.length + buffer5.length + buffer6.length + buffer7.length;
            byte[] buffer1 = ByteUtil.getByteArray(n);
            List<byte[]> list = new ArrayList();
            list.add(buffer1);
            list.add(buffer2);
            list.add(buffer3);
            list.add(buffer4);
            list.add(buffer5);
            list.add(buffer6);
            list.add(buffer7);
            byte[] buffer = ByteUtil.mergeBytes(list);
            msgQueue.add(buffer);
        } catch (Exception ex) {
            logger.error("发送时间同步请求到路径识别服务器" + pathServerip + "异常", ex);
        }
    }

    /**
     * 根据路段，里程以及车型车重信息获取费用
     *
     * @param roadid 路段惟一编码
     * @param dis 在对应路段上的行驶里程
     * @param vehClass 车型
     * @param weight 计费重量
     * @param limitWeight 检测重量
     * @return 路段费用，单位分
     */
    public double getFareByRoad(int roadid, double dis, int vehClass, double weight, double limitWeight) {
        double fare = 0;
        try {
            fare = PriceCalculateUtils.getFareByRoad(roadid, dis, vehClass, weight, limitWeight);//单位分
        } catch (MTCException ex) {
            logger.error("根据路段号" + roadid + "获取费用异常", ex);
        }
        return fare;
    }

    /**
     * 根据桥隧编号以及车型车重信息获取桥隧费用
     *
     * @param btNum 桥隧编号
     * @param vehClass 车型
     * @param weight 计费重量
     * @param limitWeight 限重
     * @return 桥隧费用，单位分
     */
    public double getFareByBT(String btNum, int vehClass, double weight, double limitWeight) {
        double fare = 0;
        try {
            String qsName = rtSvc.getQSNameFromQSid(btNum);//获取桥隧中文名称
            Integer i = ParamCacheQuery.queryBridgeOrTunnelId(qsName);//获取桥隧编号
            fare = PriceCalculateUtils.getFareByBT(i, vehClass, weight);//桥隧费用，单位为分
        } catch (MTCException ex) {
            logger.error("根据桥隧编号" + btNum + "获取费用异常", ex);
        }
        return fare;
    }

    /**
     * 根据车辆以及实时路径信息获取通行费
     *
     * @param pi 实时路径信息
     * @param veh 车辆信息
     * @return 通行费
     */
    public double getFare(PathInfo pi, Vehicle veh) {
        if (pi == null) {
            return 0.0;
        }
        String path = pi.getPath();
        if (path == null) {
            return 0.0;
        }
        String[] buffer = path.split(",");
        List<String> qsList = new ArrayList();
        List<RealTimeRoad> rtrList = new ArrayList();
        for (String str : buffer) {
            if (str.startsWith("L")) {//获取路段信息
                String[] tempBuffer = str.split(":");
                String twRoadid = tempBuffer[0].substring(1);
                String tempRoadid = rtSvc.getUniqueRoadFromRtRoad(twRoadid);
                String dis = tempBuffer[1];
                RealTimeRoad rtr = new RealTimeRoad();
                rtr.setRtRoadid(twRoadid);
                rtr.setDis(DoubleUtils.parseString(dis));
                rtr.setUniqueRoadid(tempRoadid);
                rtrList.add(rtr);
            } else if (str.startsWith("Q")) {//获取桥梁信息
                qsList.add(str.substring(1));
            } else if (str.startsWith("S")) {//获取隧道信息
                qsList.add(str.substring(1));
            }
        }
        double fare = 0;
        for (RealTimeRoad rtr : rtrList) {//计算路段费用
            int road = IntegerUtils.parseString(rtr.getUniqueRoadid());
            double temp = getFareByRoad(road, rtr.getDis(), veh.getVehicleClass(), veh.getFareWeight(), veh.getLimitWeight());
            if (temp < 0) {
                logger.warn("根据实际路径计费时出现异常，无法找到路段" + rtr + "对应收费信息");
                return -1;
            }
            fare = DoubleUtils.sum(fare, temp);
            logger.warn("根据实际路径计费，路径：" + rtr + "费用：" + temp);
        }
        for (String str : qsList) {//计算桥隧费用
            double temp = getFareByBT(str, veh.getVehicleClass(), veh.getFareWeight(), veh.getLimitWeight());
            if (temp < 0) {
                logger.warn("根据实际路径计费时出现异常，无法找到桥隧" + str + "对应收费信息");
                return -1;
            }
            fare = DoubleUtils.sum(fare, temp);
            logger.warn("根据实际路径计费，桥隧：" + str + "费用：" + temp);
        }
//        fare = pi.getTotalFee();
        fare = DoubleUtils.div(fare, 100, 0);//分转换为元，四舍五入
        return fare;
    }

    /**
     * 根据实际路径信息获取行驶路径如：羊楼司-临长-长永-雨花
     *
     * @param pi 实时路径信息
     * @return 行驶路径
     */
    public String getRealPath(PathInfo pi) {
        String path = pi.getPath();
        if (path == null) {
            return null;
        }
        String[] buffer = path.split(",");
        StringBuilder sb = new StringBuilder(1024);
        for (int i = 0; i < buffer.length; i++) {
            String str = buffer[i];
            if (str.startsWith("L")) {
                String[] tempBuffer = str.split(":");
                String twRoadid = tempBuffer[0].substring(1);
                sb.append(rtSvc.getRoadNameFromRtRoad(twRoadid)).append(DoubleUtils.parseString(tempBuffer[1])).append("公里");
            } else if (str.startsWith("Q") || str.startsWith("S")) {
                String qsid = str.substring(1);
                sb.append(rtSvc.getQSNameFromQSid(qsid));
            }
            if (i < buffer.length - 1) {
                sb.append("->");
            }
        }
        return "行驶路径：" + sb.toString();
    }

    /**
     * 获取回应信息的状态码
     *
     * @param str 信息
     * @return 状态码
     */
    public int getRespStatus(String str) {
        JSONObject jo = JSONObject.fromObject(str);
        Iterator it = jo.keys();
        if (it.hasNext()) {
            String name = (String) it.next();
            JSONObject jo1 = jo.getJSONObject(name);
            return jo1.getInt("Status");
        } else {
            throw new RuntimeException("json数据异常：" + str);
        }
    }

    class SendTask implements Runnable {

        @Override
        public void run() {
            try {
                while (running) {
                    Thread.sleep(1);
                    if (sendQueue != null && !sendQueue.isEmpty()) {
                        byte[] buffer = sendQueue.poll();
                        ByteBuffer bb = ByteBuffer.wrap(buffer);
                        socketChannel.write(bb);
                    }
                }
            } catch (Exception ex) {
                logger.error(ex, ex);
            }
        }
    }

    class RecvTask implements Runnable {

        @Override
        public void run() {
            try {
                while (running) {
                    Thread.sleep(1);
                    byte[] buffer = new byte[1024];
                    ByteBuffer bb = ByteBuffer.wrap(buffer);
                    int len = socketChannel.read(bb);
                    if (len == -1) {
                        logger.warn("路径识别服务端已关闭");
                        break;
                    } else if (len == 0) {
                        continue;
                    }
                    recvQueue.add(Arrays.copyOf(buffer, len));
                }
            } catch (Exception ex) {
                logger.error(ex, ex);
            }
        }

    }

    class ParseTask implements Runnable {

        @Override
        public void run() {
            try {
                while (running) {
                    Thread.sleep(1);
                    if (recvQueue != null && !recvQueue.isEmpty()) {
                        byte[] buffer = recvQueue.poll();
                        parseRtpInfo(buffer);
                    }
                }
            } catch (Exception ex) {
                logger.error(ex, ex);
            }

        }
    }

    class MonitorTask implements Runnable {

        @Override
        public void run() {
            try {
                while (running) {
                    Thread.sleep(1);
                    if (sendQueue != null && sendQueue.size() > maxSendSize) {
                        sendQueue.poll();
                    }
                    if (recvQueue != null && recvQueue.size() > maxRecvSize) {
                        recvQueue.poll();
                    }
                }
            } catch (Exception ex) {
                logger.error(ex, ex);
            }
        }
    }

    private void parseRtpInfo(byte[] buffer) {

    }

    /**
     * 退出
     */
    public void stop() {
        running = false;
    }
}