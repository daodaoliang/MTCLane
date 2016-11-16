package com.hgits.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hgits.service.PcService;
import com.hgits.service.constant.ParamConstant;
import com.hgits.task.MyTask;
import com.hgits.tool.socket.LaneSocketChannel;
import com.hgits.tool.socket.entity.LaneInfo;
import com.hgits.tool.socket.entity.ParamVersion;
import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.rate.ParamCacheQuery;
import com.hgits.vo.Constant;
import com.hgits.vo.Lane;
import com.hgits.vo.Version;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;

import org.apache.log4j.Logger;

import ui.ExtJFrame;

/**
 * 与监控室进行通信的车道客户端控制程序（支持与多个监控室同时进行通信）
 *
 * @author WangGuodong
 */
public class LaneServerControl implements Runnable {

    private final List<LaneSocketChannel> list = Collections.synchronizedList(new ArrayList());
    private static int overTimeInterval = 30;//监控室确认超时时间

	private static final Logger logger = Logger.getLogger(LaneServerControl.class);
    private ServerSocketChannel ssc;//服务端端口
    private boolean running = true;
    private int maxMonitorCnt = 10;//最大连接数量
    private boolean waitingConfirm;//等待监控室确认

    @Override
    public void run() {
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "overTimeInterval", "30");
        LogControl.warn("监控室确认超时时间：" + str + "秒");
        overTimeInterval = IntegerUtils.parseString(str);
        if (overTimeInterval <= 0) {
            overTimeInterval = 1;
        }
        ServerTask serverTask = new ServerTask();
        ThreadPoolControl.getThreadPoolInstance().execute(serverTask);
        PcInfoTask pcinfoTask = new PcInfoTask();
        ThreadPoolControl.getThreadPoolInstance().execute(pcinfoTask);
        MonitorTask monitorTask = new MonitorTask(pcinfoTask, serverTask);
        ThreadPoolControl.getThreadPoolInstance().execute(monitorTask);

    }

    /**
     * 获取配置的超时时间
     * @return
     */
    public static int getOverTimeInterval() {
		return overTimeInterval;
	}


	/**
     * 发送车道实时信息给监控室
     *
     * @param info 信息
     */
    public void sendInfoMessage(String info) {
        synchronized (list) {
            if (list != null && !list.isEmpty()) {
                int cnt = list.size();
                for (int i = 0; i < cnt; i++) {
                    LaneSocketChannel ts = list.get(i);
                    ts.sendInfoMessage(info);
                }
            }
        }
    }

    /**
     * 将所需要确认的黑名单信息发送给监控室，等待监控室确认
     *
     * @param blkInfo 黑名单信息
     * @return 返回监控室确认信息，监控室决绝修改返回N,与监控室断开连接返回null
     */
    public String waitForBCKListConfirm(String blkInfo) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        String result = null;
        synchronized (list) {//请求监控室确认期间不可对监控室连接集合修改
            List<String> serialList = new ArrayList();//记录请求信息序列号的集合
            int cnt = list.size();
            boolean connectFlag = false;//与监控室连接标志
            for (int i = 0; i < cnt; i++) {
                LaneSocketChannel ts = list.get(i);
                String serial = null;
                if (!ts.isInBureau() && ts.isRunning()) {//省中心监控室不参与监控室确认,离线不参与确认
                    connectFlag = true;
                    serial = ts.sendBCKListInfo(blkInfo);//向监控室发送请求并记录请求所对应的序列号
                }
                serialList.add(serial);
            }
            if (!connectFlag) {//与所有监控室均连接失败
                LogControl.logInfo("请求站级监控室确认时，发现与站级监控室连接失败");
            }
            long start = System.currentTimeMillis();
            outer:
            while (connectFlag) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
                long now = System.currentTimeMillis();
                if (now < start) {//当前时间小于开始时间，重新开始计时
                    start = now;
                }
                if (now - start > overTimeInterval * 1000) {//30秒确认超时
                    LogControl.logInfo("等待监控室确认超时");
                    break;
                }
                connectFlag = false;
                for (int i = 0; i < cnt; i++) {
                    LaneSocketChannel ts = list.get(i);
                    if (ts.isInBureau() || !ts.isRunning()) {//省中心监控室不参与监控室确认,已掉线不参与确认
                        continue;
                    } else {
                        connectFlag = true;//监控室保持连接
                    }
                    String serial = serialList.get(i);
                    if (serial == null) {
                        continue;
                    }
                    result = ts.getConfirmResult(serial);
                    if (result != null) {
                        break outer;
                    }
                }
            }
            for (LaneSocketChannel ts : list) {
                if (ts.isInBureau() || !ts.isRunning()) {//省中心监控室不参与监控室确认,已掉线不参与确认
                    continue;
                }
                ts.sendConfirmDone();
            }
        }
        return result;
    }

    /**
     * 将所需确认的信息发送给监控室，等待监控室确认 监控室确认返回信息中命令内容前四个字符为确认的入口站代号，如：0501，也是本方法返回值
     *
     * @param entryInfo 所需确认的信息
     * @return 返回监控室确认信息，监控室拒绝修改返回N,与监控室断开连接返回null
     */
    public String waitForEntryConfirm(String entryInfo) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        String result = null;
        synchronized (list) {//请求监控室确认期间不可对监控室连接集合进行修改
            List<String> serialList = new ArrayList();//记录请求信息序列号的集合
            int cnt = list.size();
            boolean connectFlag = false;//与监控室连接标志
            for (int i = 0; i < cnt; i++) {
                LaneSocketChannel ts = list.get(i);
                //省中心监控室不参与监控室确认,离线不参与确认
                if (ts.isInBureau() || !ts.isRunning()) {
                    serialList.add(null);
                    continue;
                } else {
                    connectFlag = true;
                }
                String serial = ts.sendEntryInfo(entryInfo);//向监控室发送请求并记录请求所对应的序列号
                serialList.add(serial);
            }
            if (!connectFlag) {//与所有监控室均连接失败
                LogControl.logInfo("与监控室连接失败");
            }
            long start = System.currentTimeMillis();

            outer:
            while (connectFlag) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
                long now = System.currentTimeMillis();
                if (now < start) {//当前时间小于开始时间，重新开始计时
                    start = now;
                }
                if (now - start > overTimeInterval * 1000) {//超时自动确认
                    LogControl.logInfo("等待监控室确认超时");
                    break;
                }
                connectFlag = false;
                for (int i = 0; i < cnt; i++) {
                    LaneSocketChannel ts = list.get(i);
                    //省中心监控室不参与监控室确认,离线不参与确认
                    if (ts.isInBureau() || !ts.isRunning()) {
                        continue;
                    } else {
                        connectFlag = true;//监控室保持连接
                    }
                    String serial = serialList.get(i);
                    if (serial == null) {
                        continue;
                    }
                    result = ts.getConfirmResult(serial);
                    if (result != null) {
                        break outer;
                    }
                }
            }
            for (LaneSocketChannel ts : list) {
                //省中心监控室不参与监控室确认,离线不参与确认
                if (ts.isInBureau() || !ts.isRunning()) {
                    continue;
                }
                ts.sendConfirmDone();
            }
        }
        return result;
    }

    /**
     * 将所需要确认的车辆信息发送给监控室，等待监控室确认 监控室确认返回信息为vehInfoConfirm实体类内容，也是本方法返回值
     *
     * @param vehInfo 需要确认的信息，是vehInfoConfirm的实体类
     * @return 返回监控室确认信息，监控室拒绝修改返回N,与监控室断开连接返回null
     */
    public String waitForVehConfirm(String vehInfo) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        String result = null;
        synchronized (list) {//list加锁，等待监控室确认期间不可对list进行操作
            List<String> serialList = new ArrayList();//记录请求信息序列号的集合
            int cnt = list.size();
            boolean connectFlag = false;//与监控室连接标志
            for (int i = 0; i < cnt; i++) {
                LaneSocketChannel ts = list.get(i);
                //省中心监控室不参与监控室确认,离线不参与确认
                if (ts.isInBureau() || !ts.isRunning()) {
                    serialList.add(null);
                    continue;
                } else {
                    connectFlag = true;
                }
                String serial = ts.sendVehInfo(vehInfo);//向监控室发送请求并记录请求所对应的序列号
                serialList.add(serial);
            }
            if (!connectFlag) {//与所有监控室均连接失败
                LogControl.logInfo("与监控室连接失败");
            }
            long start = System.currentTimeMillis();
            outer:
            while (connectFlag) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
                long now = System.currentTimeMillis();
                if (now < start) {//当前时间小于开始时间，重新开始计时
                    start = now;
                }
                if (now - start > overTimeInterval * 1000) {//超时
                    LogControl.logInfo("等待监控室确认超时");
                    break;
                }
                connectFlag = false;
                for (int i = 0; i < cnt; i++) {
                    LaneSocketChannel ts = list.get(i);
                    //省中心监控室不参与监控室确认,离线不参与确认
                    if (ts.isInBureau() || !ts.isRunning()) {
                        continue;
                    } else {
                        connectFlag = true;//监控室保持连接
                    }
                    String serial = serialList.get(i);
                    if (serial == null) {
                        continue;
                    }
                    result = ts.getConfirmResult(serial);
                    if (result != null) {
                        break outer;
                    }
                }
            }
            for (LaneSocketChannel ts : list) {
                //省中心监控室不参与监控室确认,离线不参与确认
                if (ts.isInBureau() || !ts.isRunning()) {
                    continue;
                }
                ts.sendConfirmDone();
            }
        }
        return result;
    }

    /**
     * 等待模拟通行确认
     *
     * @param info 需确认信息
     * @return true/false
     */
    public boolean waitForSimuPassConfirm(String info) {
        waitingConfirm = true;//等待监控室确认标识
        boolean flag = true;
        if (list == null || list.isEmpty()) {
            return flag;
        }
        String result = null;
        synchronized (list) {//list加锁，等待监控室确认期间不可对list进行操作
            List<String> serialList = new ArrayList();//记录请求信息序列号的集合
            int cnt = list.size();
            boolean connectFlag = false;//与监控室连接标志
            for (int i = 0; i < cnt; i++) {
                LaneSocketChannel ts = list.get(i);
                //省中心监控室不参与监控室确认,离线不参与确认
                if (ts.isInBureau() || !ts.isRunning()) {
                    serialList.add(null);
                    continue;
                } else {
                    connectFlag = true;
                }
                String serial = ts.sendSimuPassInfo(info);//向监控室发送请求并记录请求所对应的序列号
                serialList.add(serial);
            }
            if (!connectFlag) {//与所有监控室均连接失败
                LogControl.logInfo("与监控室连接失败");
            }
            long start = System.currentTimeMillis();
            outer:
            while (connectFlag && waitingConfirm) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
                long now = System.currentTimeMillis();
                if (now < start) {//当前时间小于开始时间，重新开始计时
                    start = now;
                }
                if (now - start > overTimeInterval * 1000) {//超时
                    LogControl.logInfo("等待监控室确认超时");
                    break;
                }
                connectFlag = false;
                for (int i = 0; i < cnt; i++) {
                    LaneSocketChannel ts = list.get(i);
                    //省中心监控室不参与监控室确认,离线不参与确认
                    if (ts.isInBureau() || !ts.isRunning()) {
                        continue;
                    } else {
                        connectFlag = true;//监控室保持连接
                    }
                    String serial = serialList.get(i);
                    if (serial == null) {
                        continue;
                    }
                    result = ts.getConfirmResult(serial);
                    if (result != null) {
                        break outer;
                    }
                }
            }
            for (LaneSocketChannel ts : list) {
                //省中心监控室不参与监控室确认,离线不参与确认
                if (ts.isInBureau() || !ts.isRunning()) {
                    continue;
                }
                ts.sendConfirmDone();
            }
        }
        if (result != null && !"0".equalsIgnoreCase(result)) {//监控室拒绝修改
            flag = false;
        } else if (!waitingConfirm) {//不再等待监控室确认
            flag = false;
        }
        waitingConfirm = false;
        return flag;
    }

    /**
     * 停止等待监控室确认
     */
    public void stopWaitingConfirm() {
        waitingConfirm = false;
    }

    /**
     * 是否与省中心TCO连线
     *
     * @return true/false
     */
    public boolean isBureauOnLine() {
        boolean flag = false;
        synchronized (list) {
            for (LaneSocketChannel ts : list) {
                if (ts.isInBureau() && ts.isRunning()) {//是否为省中心TCO,是否连接正常(多个省中心时，任意一个在线都可以)
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 获取参数表版本详细信息
     *
     * @return 参数版本详细信息
     */
    private ParamVersion getParamVersion() {
        ParamVersion pv = new ParamVersion();
        Map<String, Version> map = ParamCacheQuery.queryParamVersion();
        Version vec;
        vec = map.get(ParamConstant.PARAM_ISSUER);
        if (vec != null) {
            pv.setTb_Issuer(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_OPERATOR);
        if (vec != null) {
            pv.setTb_Operator(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_OVERLOADPRICE);
        if (vec != null) {
            pv.setTb_OverloadPrice(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_ROADCHARGESTD);
        if (vec != null) {
            pv.setTb_RoadChargeStd(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_STATION);
        if (vec != null) {
            pv.setTb_Station(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_TRAVELTIME);
        if (vec != null) {
            pv.setTb_TravelTime(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_USERRATE);
        if (vec != null) {
            pv.setTb_UserRate(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_BWG_LIST);
        if (vec != null) {
            pv.setTb_VehPlateBWG(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_VEHROUTE + 1);
        if (vec != null) {
            pv.setTb_VehRoute1(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_VEHROUTE + 2);
        if (vec != null) {
            pv.setTb_VehRoute2(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_VEHROUTE + 3);
        if (vec != null) {
            pv.setTb_VehRoute3(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_VEHROUTE + 4);
        if (vec != null) {
            pv.setTb_VehRoute4(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_VEHROUTE + 5);
        if (vec != null) {
            pv.setTb_VehRoute5(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_VEHROUTE + 7);
        if (vec != null) {
            pv.setTb_VehRoute7(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_XTCARD_BLACK_BASE_LIST);
        if (vec != null) {
            pv.setTb_XTCardBlackBaseList_send(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_XTCARD_BLACK_DELTA_LIST);
        if (vec != null) {
            pv.setTb_XTCardBlackDeltaList_send(vec.getVersion());
        }
        vec = map.get(ParamConstant.PARAM_BRIDGE_EXTRA_CHARGE_STD);
        if (vec != null) {
            pv.setTb_bridgeextrachargestd(vec.getVersion());
        }
        return pv;
    }

    /**
     * 获取车道信息
     *
     * @return 车道信息
     */
    private LaneInfo getLaneInfo() {
        LaneInfo info = new LaneInfo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        info.setClintTime(sdf.format(new Date()));
        PcService svc = new PcService();
        info.setCpuType(svc.getCpuType());
        info.setCpuUse(svc.getCpuUse());
        info.setMemoryUse(svc.getMemoryUse());
        info.setNetworkStatus("联网");
        info.setOperatingSystem(svc.getOperatingSystem());
        info.setLaneNO(Lane.getInstance().getLaneId());
        info.setParamVersion(getParamVersion());
        info.setStation(Lane.getInstance().getRoadId() + Lane.getInstance().getStationId());
        info.setVersion(Constant.LANE_SOFT_VERSION);
        return info;
    }
    //发送电脑信息的任务

    class PcInfoTask extends MyTask {

        @Override
        public void run() {
            setAlive(true);
            try {
                //获取向省中心TCO发送车道电脑内存信息的时间间隔，单位：秒，默认60秒
                String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "lanePcInfoInterval", "60");
                int interval = IntegerUtils.parseString(str);
                if (interval <= 0) {
                    interval = 60;
                }
                while (running) {
                    synchronized (list) {
                        for (LaneSocketChannel ts : list) {
                            if (ts.isInBureau() && ts.isRunning()) {//判断当前监控是否在局中心
                                LaneInfo laneInfo = getLaneInfo();
                                ts.sendLaneInfo(laneInfo.toString());
                            }
                        }
                    }
                    Thread.sleep(interval * 1000);
                }
            } catch (Throwable ex) {
                LogControl.logInfo("发送车道电脑硬件信息至局TCO异常", ex);
            }
            setAlive(false);
        }
    }

    class ServerTask extends MyTask {

        @Override
        public void run() {
            try {
                setAlive(true);
                String roadid = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "roadId", "00");
                String stationid = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "stationId", "00");
                String laneid = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "laneNo", "000");
                String port = MyPropertiesUtils.getProperties(Constant.PROP_SOCKET, "TCOSocket", "55557");
                int portNum = IntegerUtils.parseString(port);
                String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "maxMonitorCnt", "10");
                maxMonitorCnt = IntegerUtils.parseString(str);
                ssc = ServerSocketChannel.open();//创建socket通道
                ssc.configureBlocking(false);//设置socket通道为非阻塞模式
                ssc.bind(new InetSocketAddress(portNum));//绑定端口
                logger.warn("监控室控制模块监听端口：" + ssc);
                while (running) {
                    Thread.sleep(10);
                    if (list.size() >= maxMonitorCnt) {
                        continue;
                    }
                    SocketChannel sc = ssc.accept();
                    if (sc != null) {
                        logger.warn("与监控室" + sc + "建立连接");
                        LaneSocketChannel lc = new LaneSocketChannel(sc, roadid + "_" + stationid + "_" + laneid);
                        ThreadPoolControl.getThreadPoolInstance().execute(lc);
                        list.add(lc);
                    }
                }
            } catch (Throwable ex) {
                logger.error(ex, ex);
            } finally {
                if (ssc != null) {
                    try {
                        ssc.close();
                    } catch (IOException ex) {
                        logger.error(ex, ex);
                    }
                }
            }
            setAlive(false);
        }
    }

    class MonitorTask extends MyTask {

        private final PcInfoTask pcInfoTask;
        private final ServerTask serverTask;

        public MonitorTask(PcInfoTask pcInfoTask, ServerTask serverTask) {
            this.pcInfoTask = pcInfoTask;
            this.serverTask = serverTask;
        }

        @Override
        public void run() {
            outer:
            while (running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                if (!pcInfoTask.isAlive()) {
                    pcInfoTask.setAlive(running);
                    ThreadPoolControl.getThreadPoolInstance().execute(pcInfoTask);
                }
                if (!serverTask.isAlive()) {
                    serverTask.setAlive(running);
                    ThreadPoolControl.getThreadPoolInstance().execute(serverTask);
                }
                synchronized (list) {
                    int len = list.size();
                    for (int i = 0; i < len; i++) {
                        LaneSocketChannel lc = list.get(i);
                        if (lc == null) {
                            LogControl.logInfo("移除监控室连接" + lc);
                            list.remove(lc);
                            continue outer;
                        } else if (!lc.isRunning()) {
                            LogControl.logInfo("移除已经断开的监控室连接" + lc.getRemoteAddress());
                            list.remove(lc);
                            continue outer;
                        }
                    }
                }
                checkTcoOnline();
            }
        }

    }

    /**
     * TCO监控室是否在线（不判断省中心TCO）
     *
     * @return TRUE/FALSE
     */
    private boolean isTcoOnline() {
        boolean flag = false;
        synchronized (list) {
            for (LaneSocketChannel lsc : list) {
                if (lsc.isRunning() && !lsc.isInBureau()) {//非省中心TCO在线
//                    logger.warn("lsc.isRunning():"+lsc.isRunning());
//                    logger.warn("lsc.isInBureau():"+lsc.isInBureau());
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    private void checkTcoOnline() {
        ExtJFrame extJFrame = ExtJFrame.getSingleInstance();
        if (!isTcoOnline()) {
            extJFrame.updateAfterTcoOffLine();
        } else {
            extJFrame.updateAfterTcoOnLine();
        }
    }

    /**
     * 停止运行
     */
    public void stop() {
        running = false;
        synchronized (list) {
            for (LaneSocketChannel lc : list) {
                lc.stop();
            }
        }
    }
}
