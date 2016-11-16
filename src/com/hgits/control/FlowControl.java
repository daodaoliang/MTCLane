package com.hgits.control;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import ui.ExtJFrame;
import ui.InfoJFrame;
import ui.RunningModeJFrame;
import com.hgits.hardware.CICM;
import com.hgits.hardware.CXP;
import com.hgits.hardware.Keyboard;
import com.hgits.hardware.LprService;
import com.hgits.hardware.Printer;
import com.hgits.hardware.TFI;
import com.hgits.hardware.VideoInstruction;
import com.hgits.realTimePath.RTUtil;
import com.hgits.realTimePath.RealTimePath;
import com.hgits.realTimePath.vo.CostRecord;
import com.hgits.realTimePath.vo.EntryStation;
import com.hgits.realTimePath.vo.PathInfo;
import com.hgits.realTimePath.vo.RequestPath;
import com.hgits.service.AutoForbidService;
import com.hgits.service.CpuCardService;
import com.hgits.service.EncryptService;
import com.hgits.service.FullKeyboardService;
import com.hgits.service.MainService;
import com.hgits.service.TFIService;
import com.hgits.tool.socket.entity.EnInfo;
import com.hgits.tool.socket.entity.ExitInfo;
import com.hgits.tool.socket.entity.SimulateRun;
import com.hgits.util.DoubleUtils;
import com.hgits.util.IntegerUtils;
import com.hgits.util.LaneListUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.ShortUtils;
import com.hgits.util.SquadUtils;
import com.hgits.util.StringUtils;
import com.hgits.util.file.FileUtils;
import com.hgits.util.rate.ParamCacheFileRead;
import com.hgits.util.rate.ParamCacheQuery;
import com.hgits.util.rate.RateParamErrorUtils;
import com.hgits.util.rate.RecvParamUtils;
import com.hgits.vo.Card;
import com.hgits.vo.CardBoxOpList;
import com.hgits.vo.CartInfo;
import com.hgits.vo.CartOpType;
import com.hgits.vo.ChargeType;
import com.hgits.vo.ColList;
import com.hgits.vo.Constant;
import com.hgits.vo.CpuCard;
import com.hgits.vo.Lane;
import com.hgits.vo.LaneEnList;
import com.hgits.vo.LaneExList;
import com.hgits.vo.LaneLogin;
import com.hgits.vo.LaneLogout;
import com.hgits.vo.LaneShift;
import com.hgits.vo.Operator;
import com.hgits.vo.ServiceCard;
import com.hgits.vo.SpeVehFlag;
import com.hgits.vo.Staff;
import com.hgits.vo.Station;
import com.hgits.vo.Vehicle;

/**
 * 创建于2014-8-13 该类用于车道流程控制
 *
 * @author Wang Guodong
 */
public class FlowControl {
    
    public static final int LOGOUT_AUTO = 1;//自动下班
    public static final int LOGOUT_MANUAL = 0;//手动下班
    private ExtJFrame extJFrame;//主界面
    private Keyboard keyboard;//收费键盘
    private CXP cxp;//CXP板
    private TFI tfi;//费显
    private Printer printer;//打印机
    private CICM cicm;//卡机
    private VideoInstruction vi;//视频字符叠加
    private Lane lane;//车道
    private static int canopyFlag;//雨棚灯标识 0 灯灭；1 红；2 绿
    private Vehicle veh;//车辆信息
    private Card card2;//要写的卡
    private Card card1;//读到的卡
//    private Card card3;//写卡之后从卡内读到的信息
    private CpuCard cpuCard1;//读到的ETC卡
    private CpuCard cpuCard2;//要写的ETC卡
    private CpuCard cpuCard3;//写卡之后读到的ETC卡
    MainService mainSvc;//主服务类
    CpuCardService cpuCardSvc;//ETC卡读卡器服务类
    private String ticketNum;//发票号
    public static boolean runWhileVehLeave;//等待车辆离开
    CartControl cartControl;//卡箱控制
    WeighControl weighControl;//计重控制
    SimulateControl simControl;//模拟控制
    private double orderPay;//应付通行费
    private double pay;//实付通行费
    private String exitMop = "00";//出口付款方式01现金 07欠款 12银行卡 17军警车  37储值卡 38公务拖车 23紧急车 39个人公务车 40免费车 51ETC卡
    private String entryMop = "00";//入口通行方式48入口 51ETC卡 60 纸券
    JobControl jobControl;//上下班控制
    public static boolean onduty = false;//上班标识
    public static Staff staff;//员工号
    Card card;//入口记录已发通行卡,但司机出示ETC通行卡后记录已发的通行卡
    int vehCount = 0;//交易号(无上限,满足实际情况下交易号超过9999的情况)
    LprService lprSvc;//车牌识别服务类
    boolean cpuFlag = true;//ETC卡标识符 0-正常 1-车型车牌不符等异常
    boolean m1Flag = true;//通行卡标识符 0-正常 1-卡类型错误
    private RealTimePath rtSvc;//实时路径服务
    LaneShift ls;//班次日志
    short violateClose;//下班后至上班前这段时间内的违章车数量
    public static LaneLogout logout;//下班流水
    public static String vehObserCode = "0";//车辆通行观察码
    public static String chargeObserCode = "0";//收费操作观察码
    String mopObserCode = "0";//付款方式观察码
    public static String cardObserCode = "0";//通行卡观察码
    String errCardSerial;//记录已经处理过的异常卡号
    private boolean violationFlag;//违章车标识
    String path = "";//行驶路径
    public static PathInfo pi;//路径识别信息
    private int piSerial;//路径识别信息序列号
    private int pathDetailSerial;//路径明细信息序列号
    public static String trafficEnRoadid;//入口物理路段
    public static String trafficEnStationid;//入口物理收费站
    String axleGroupPreStr;//修改前的车的轴组类型字符串
    double preWeight;//修改前车重
    private boolean printFlag = true;//是否打印发票
    UpdateControl update;//自动更新
    private int recordId = 1;//交易记录编码,最大为9999,9999之后从1开始（对应交易流水中的交易记录编码）
    private double dist;//总里程
    private double etcDis;//ETC卡折扣率
    private short chargeType;//收费类型
    private String totalF;//支付总金额
    private String changeF;//找零金额
    private Date squadDate;//当前工班日期
    private int squadId;//当前工班号
    private TempControl tempCtrl;//生成临时文件的服务类
    private AutoMachineControl amc;//自助发卡控制
    private LaneServerControl lc;//与监控室通信的车道客户端控制
    private boolean collectFlag;//是否有代收通行费流程的标志
    private int collectFee;//代收通行费
    private RecvParamUtils rpu = new RecvParamUtils();//新版本参数文件工具类
    private String tempImg1;//临时交易图片1的名称
    private String tempImg2;//临时交易图片2的名称
    private boolean warning;//报警标识
    private boolean autoPlateFlag;//自助发卡模式下手动修改车牌号码标识
    private OffLineControl offLineControl;//离线模式控制
    private final TFIService tfiSvc = new TFIService();
//    private AntiBackControl abc;//防盗车取卡控制
    private String trackNo = "0";//字轨号
    private int exitTicketType;//出口通行券类型：0无卡(紧急、车队过车)   1 通行卡   2 湘通卡 4 纸券
    private int entryTicketType; //入口通行券类型：0其他车 1 通行卡  2 湘通卡 3紧急车 4 纸券
    private M1Control m1Control;//m1卡控制
    private CollectControl cc;//代收控制
    private PinControl pinControl;//卡机收发针控制
    private boolean chargeFlag;//出口收费标识（true已收费；false未收费）
    private String serviceCardSerial = "";//公务卡卡号

    private AvcControl avcControl;//用于控制与西埃斯对接的车型分类器
    private final Object vehObject = new Object();//用于控制车辆信息的加锁对象
    private final Object violateObj = new Object();//用于控制违章车的的加锁对象

    public static String getStaffId() {
        Staff tempStaff = staff;
        if (tempStaff == null) {
            return null;
        } else {
            return tempStaff.getStaffId();
        }
    }
    
    public void setCc(CollectControl cc) {
        this.cc = cc;
    }
    
    public void setM1Control(M1Control m1Control) {
        this.m1Control = m1Control;
    }
    
    public void setVeh(Vehicle veh) {
        synchronized (vehObject) {
            this.veh = veh;
        }
    }
    
    private ParamCacheFileRead pcfru = ParamCacheFileRead.getInstance();

//    public void setAbc(AntiBackControl abc) {
//        this.abc = abc;
//    }
    public void setOffLineControl(OffLineControl offLineControl) {
        this.offLineControl = offLineControl;
    }
    
    public void setLc(LaneServerControl lc) {
        this.lc = lc;
    }
    
    public void setAmc(AutoMachineControl amc) {
        this.amc = amc;
    }
    
    public void setVi(VideoInstruction vi) {
        this.vi = vi;
    }
    
    public Lane getLane() {
        return lane;
    }
    
    public Staff getStaff() {
        return staff;
    }
    
    public void setUpdate(UpdateControl update) {
        this.update = update;
    }
    
    public void setRtSvc(RealTimePath rtSvc) {
        this.rtSvc = rtSvc;
    }
    
    public void setLprSvc(LprService lprSvc) {
        this.lprSvc = lprSvc;
    }
    
    public void setMainSvc(MainService mainSvc) {
        this.mainSvc = mainSvc;
    }
    
    public void setCpuCardSvc(CpuCardService cpuCardSvc) {
        this.cpuCardSvc = cpuCardSvc;
    }
    
    public void setCartControl(CartControl cartControl) {
        this.cartControl = cartControl;
    }
    
    public void setWeighControl(WeighControl weighControl) {
        this.weighControl = weighControl;
    }
    
    public void setSimControl(SimulateControl simControl) {
        this.simControl = simControl;
    }
    
    public void setLogControl(JobControl jobControl) {
        this.jobControl = jobControl;
    }
    
    public void setLane(Lane lane) {
        this.lane = lane;
    }
    
    public void setExtJFrame(ExtJFrame extJFrame) {
        this.extJFrame = extJFrame;
    }
    
    public void setKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }
    
    public void setCxp(CXP cxp) {
        this.cxp = cxp;
    }
    
    public void setTfi(TFI tfi) {
        this.tfi = tfi;
    }
    
    public void setPrinter(Printer printer) {
        this.printer = printer;
    }
    
    public void setCicm(CICM cicm) {
        this.cicm = cicm;
    }
    
    public void setTempSvc(TempControl tempCtrl) {
        this.tempCtrl = tempCtrl;
    }

    /**
     * 是否上班
     *
     * @return true 是；false 否
     */
    public boolean isOnduty() {
        return onduty;
    }

    /**
     * 车道流程控制主程序,由上班前流程+上班流程+上班后流程+下班流程组成循环
     *
     * @throws java.lang.Exception 异常
     */
    public void run() throws Exception {
        try {
            LogControl.logInfo("车道控制逻辑启动");
            sendInfo("车道软件启动");
            pinControl = PinControl.getSingleInstance(cicm);
            checkDir();
            LogControl.logInfo("a");
            deleteTempImg();//软件启动时删除临时图片
            LogControl.logInfo("b");
            checkTempInfo();//处理临时文件信息
            LogControl.logInfo("c");
            warnControl();//报警控制线程启动
            LogControl.logInfo("d");
            vehControl();//车辆控制线程启动
            LogControl.logInfo("e");
            violationControl();//违章车控制线程启动
            LogControl.logInfo("f");
            tfiControl();
            LogControl.logInfo("g");
            vi.showLane(lane.getLaneId());//字符叠加显示车道
            LogControl.logInfo("h");
            cxp.changeCanopyLightRed();//雨棚灯变红
            LogControl.logInfo("i");
            cxp.lowerRailing();//自动栏杆下降
            LogControl.logInfo("j");
            cxp.changeCanopyLightRed();//通信灯变红
            LogControl.logInfo("k");
            cxp.stopAlarm();//停止报警
            LogControl.logInfo("l");
            canopyFlag = cxp.getCanopyFlag();
            collectFlag = FunctionControl.isCollActive();
            if (lane.isEntry() && amc.isActivated()) {
                collectFlag = false;//自助发卡车道不进行代收通行费
            }

            //by zengzb add
            if (lane.isEntry() && isXasCICM()) {
                collectFlag = false;//自助发卡车道不进行代收通行费
            }
            LogControl.logInfo("m");
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                LogControl.logInfo("n");
                extJFrame.init(lane);
                LogControl.logInfo("o");
//                cicm.receivePinRaise();
                pinControl.pinRaise();
                LogControl.logInfo("p");
                Card tempCard = runBeforeBoj();
                String staffId = tempCard.getId();
                String cardSerial = tempCard.getCardSerial();
                if (cartControl.isHasCart()) {
                    if (staff == null) {
                        staff = jobControl.login(cardSerial, staffId, 0); //卡箱信息正常,正常登录模式
                    }
                } else {
                    extJFrame.updateInfo("卡箱空", "卡箱空,请装入卡箱");
                    keyboard.sendAlert();
                    Thread.sleep(2000);
                }
                if (staff == null) {//上班成功,结束上班前的流程,进入上班后的流程,否则继续上班前的流程
                    continue;
                } else {
                    if (lane.isEntry()) {//入口时上班后重新初始化卡箱内卡总数
                        cartControl.reInitTotalCardQty();
                    }
                    if (squadDate == null) {//初次上班,工班交易记录号归一
                        squadDate = staff.getSquadDate();
                        squadId = staff.getSquadId();
                        recordId = 1;//初次上班,工班交易记录号归一
                    } else if (!squadDate.equals(staff.getSquadDate()) || squadId != staff.getSquadId()) {
                        squadDate = staff.getSquadDate();
                        squadId = staff.getSquadId();
                        recordId = 1;//不同工班,工班交易记录号归一
                    } else {
                        //若为同一工班,工班日期,工班号,工班交易记录号不会重置
                    }
                    
                    LogControl.logInfo("当前工班：" + squadDate + squadId);
                    try {
                        if (rpu.isNewRateVaild()) {
                            boolean isRefresh = rpu.refreshPrice();//上下班只是重新加载费率
                            if (isRefresh) {
                                ExtJFrame.appendTitle(""); //清空界面标题栏提示
                            }
                        }
                    } catch (Exception ex) {
                        if (lane.isExit()) {
                            ExtJFrame.appendTitle(ex.getMessage());
//                                throw new RuntimeException(ex);
                        }
                    }
                    ls = new LaneShift();
                    LaneShift.createLaneShiftNo();
                    ls.setRoadId(IntegerUtils.parseString(lane.getRoadId()));
                    ls.setStationId(IntegerUtils.parseString(lane.getStationId()));
                    ls.setLaneNo(lane.getLaneId());
                    ls.setLaneId(IntegerUtils.parseString(lane.getLaneId().substring(1)));
                    ls.setOperatorId(staff.getStaffId());
                    ls.setOperatorName(staff.getStaffName());
                    ls.setOpCardNo(cardSerial);
                    ls.setSquadDate(staff.getSquadDate());
                    ls.setSquadId(staff.getSquadId());
                    ls.setLoginTime(staff.getJobStartTime());
                    ls.setEnExFlag(lane.getLaneType());
                    ls.setListName(ls.getLaneShiftNo());
                    generateLoginInfo();
                    logout = new LaneLogout();
                    logout.setRoadId(ShortUtils.parseString(lane.getRoadId()));
                    logout.setStationId(ShortUtils.parseString(lane.getStationId()));
                    logout.setLane(lane.getLaneId());
                    logout.setJobStart(staff.getJobStartTime());
                    logout.setLaneType((short) lane.getLaneType());
                    logout.setStaff(staff.getStaffId());
                    logout.setJobNum((short) staff.getShiftId());
                    logout.setBoxUsed(logout.getBoxUsed() + 1);
                    logout.setViolationClose(violateClose);
                    generateTempLogoutInfo();//记录临时下班流水
                    onduty = true;
                    violateClose = 0;
                    vehCount = 1;//每次上班后所处理的交易次数归零（违章车会导致交易次数变化）
                    //上班时产生卡箱流动单
                    cartControl.generateCadboxList(staff.getJobStartTime(), CartOpType.RECV, staff.getStaffId(), null, 0);
                    //产生下班卡箱临时流动单
                    cartControl.generateTempCadboxList(new Date(), CartOpType.SEND, null, staff.getStaffId(), 0);
                }
                while (onduty) {
                    runAfterBoj();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                    }
                }
                vehCount = 0;//每次下班后所处理的交易次数归零
                if (lane.isEntry()) {//入口时下班后重新初始化卡箱内卡总数
                    cartControl.reInitTotalCardQty();
                }
            }
        } catch (Exception ex) {
            LogControl.logInfo("软件运行异常", ex);
        }
    }

    /**
     * 检查运行参数流程
     *
     * @throws InterruptedException
     */
    private void runCheckParam() throws InterruptedException {
        //增加参数异常信息的判断。
        while (true) {
            Thread.sleep(10);
            if (pcfru.isLoadParam() && RateParamErrorUtils.getErrors().isEmpty()) {
                break;
            } else {
                Vector<Object> vec = RateParamErrorUtils.getErrors();
                if (vec == null || vec.isEmpty()) {
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                String error = null;
                int length = -1;
                for (Object o : vec) {
                    Vector v = (Vector) o;
                    error = (String) v.get(2);
                    length = error.indexOf("，");
                    if (length != -1) {
                        sb.append(error.substring(0, length)).append("\n");
                    } else {
                        sb.append(error).append("\n");
                    }
                    
                }
                if (sb.length() > 0) {
                    sb.delete(sb.lastIndexOf("\n"), sb.length());
                }
                extJFrame.updateInfo("参数表异常", sb.toString());
                keyboard.getMessage();
            }
            
        }
        
        Station sta = ParamCacheQuery.queryStation(IntegerUtils.parseString(lane.getRoadId()), IntegerUtils.parseString(lane.getStationId()));
        if (sta == null) {
            throw new RuntimeException("收费站配置错误，未找到该收费站：" + lane.getRoadId() + lane.getStationId());
        }
        lane.setStationName(sta.getStationName());
        extJFrame.setFrameTitle(lane);
    }

    /**
     * 违章车辆控制线程
     */
    private void violationControl() {
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                int test = 0;
                boolean confirmFlag = false;//违章信息是否已被确认 true已确认,不需再报警 false未确认需要报警
                while (true) {
                    try {
                        Thread.sleep(500);
                        boolean passFlag = cxp.checkPassageCoil();
                        if (passFlag && !runWhileVehLeave) {          //交易过程中通道线圈感应到有车
                            if (confirmFlag) {//本次违章已处理
                                continue;
                            }
//                            if (lane.isEntry() && !amc.isActivated() || !onduty) {//入口未启用自助发卡或未上班时,车辆冲关时需要自动删除车型分类器中记录的车型
//                                amc.removeFirstVeh();
//                            }
                            LogControl.logInfo("长车标识为" + weighControl.isLongCar());
                            if (weighControl.isLongCar()) { //如果是长车的话
                                logout.setWoodTruck(logout.getWoodTruck() + 1);
                                confirmFlag = true; //标识已处理。
                                continue;
                            }
                            
                            if (!onduty) { //未上班的情况下,默认为一般违章车
                                violationCarHandle(3);
                                confirmFlag = true;//当前违章信息已被处理2015-8-2增加
                                continue;
                            }
                            LogControl.logInfo("触发违章流程");
//                            cxp.warningAlarm();
                            extJFrame.updateSuperMsg("违章菜单", "选择违章菜单中的一项\n然后确认");
                            extJFrame.showAlarmPanel();
                            String str;
                            int i;
                            long start = System.currentTimeMillis();
                            warning = true;//报警
                            synchronized (violateObj) {//加锁
                                while (true) {
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException ex) {
                                    }
                                    long now = System.currentTimeMillis();
                                    if (now < start) {//当前时间小于开始时间,重新开始计时
                                        start = now;
                                    }
//                                if (now - start > 5000) {//报警器5秒后自动停止报警
//                                    if (warning) {
//                                        cxp.stopAlarm();
//                                        warning = false;
//                                    }
//                                }
                                    if (now - start > 60 * 1000) {//违章信息若在一分钟之内不确认,自动确认为误报警
                                        i = 4;
                                        break;
                                    }
                                    str = keyboard.getSuperMessage();//绑定按键,目前仅违章车辆控制线程可以获取按键信息
                                    if (str == null) {
                                        continue;
                                    } else if (str.matches("[1-6]")) {
                                        extJFrame.updateAlarmPanel(str);
                                    } else if (str.equals(Keyboard.CONFIRM)) {
                                        i = extJFrame.getAlarmResult();
                                        break;
                                    } else if (str.equals(Keyboard.CANCEL)) {//按【取消】键按误报警处理
                                        i = 4;
                                        break;
                                    } else {
                                        keyboard.sendAlert();
                                    }
                                    Thread.sleep(100);
                                }
                                
                                keyboard.cancelSuperMessage();//取消对于按键的锁定
                                confirmFlag = true;//违章信息已被确认
                                if (i == 6) {
                                    LogControl.logInfo("被拖车");
                                    vehObserCode = "8";
                                } else if (i == 3) {//违章车
                                    violationCarHandle(i);
                                } else {
                                    switch (i) {
                                        case 1:
                                            LogControl.logInfo("倒车");
                                            if (logout != null) {//已上班
                                                logout.setReversing(logout.getReversing() + 1);
                                            }
                                            break;
                                        case 2:
                                            LogControl.logInfo("长车");
                                            if (logout != null) {
                                                logout.setWoodTruck(logout.getWoodTruck() + 1);
                                            }
                                            break;
                                        case 4:
                                            LogControl.logInfo("误报警");
                                            if (logout != null) {
                                                logout.setWithoutMotif(logout.getWithoutMotif() + 1);
                                            }
                                            break;
                                        case 5:
                                            LogControl.logInfo("用户匆忙");
                                            if (logout != null) {
                                                logout.setUserInHurry(logout.getUserInHurry() + 1);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
//                                vehObserCode = i + "";
                                }
                                cxp.stopAlarm();
                                extJFrame.hideAlarmPanel();
                                extJFrame.cancelSuperMsg();
                                extJFrame.showLastMessage();
                            }
                        } else {
                            confirmFlag = false;//车辆离开后,刷新违章确认标识
                        }
                    } catch (Exception ex) {
                        LogControl.logInfo("报警及车辆检测异常", ex);
                        confirmFlag = false;//因异常退出时,刷新违章确认标识
                    } finally {
                        keyboard.cancelSuperMessage();//因异常退出时,取消对于键盘的绑定
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            }

            /**
             * 违章车处理方法
             *
             * @param i
             */
            private void violationCarHandle(int i) {
                LogControl.logInfo("违章车");
                sendInfo("违章车");
                vehObserCode = i + "";
                if (logout == null) {//未上班
                    violateClose++;
                } else {//已上班
                    logout.setViolation(logout.getViolation() + 1);
                }
                if (card2 == null) {
                    card2 = new Card();
                }
                if (card2.getDhm() == null) {
                    //入口交易时间为写卡时间,需要从通行卡实体类中获取,出口交易时间是记录交易的时间,不必特别声明
                    card2.setDhm(new Date(System.currentTimeMillis()));
                }
                generateTransInfo();//产生交易流水
                weighControl.updateAfterFirstVehLeft();//车辆从通道线圈离开后,删除此车辆称重信息
                generateImg();//记录图片
                violationFlag = true;
                increaseRecordId();
                if (onduty) {
                    amc.removeFirstVeh();//自助发卡车道移除第一辆车
                    updateLastVehInfo();//更新上一辆车的信息
                    generateTempLogoutInfo();//记录临时下班信息
                    extJFrame.updateVehCount(vehCount + "");
                    vehCount++;
                }
                //违章车处理结束,重置card2
                card2 = null;
            }
        });
    }
    private boolean vehLeaving = false;//车辆正在离开
    private boolean vehLeft = false;//车辆已离开

    /**
     * 报警控制线程
     */
    private void warnControl() {
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                        if (warning) {
                            warning = false;
                            cxp.warningAlarm();
                            Thread.sleep(5000);
                            cxp.stopAlarm();
                        }
                    } catch (Exception ex) {
                        LogControl.logInfo("warnControlThread异常", ex);
                    }
                }
            }
        });
    }

    /**
     * 车辆检测控制线程
     */
    private void vehControl() {
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                boolean vehArrived = false;//车辆到达
                boolean flag = true;//下班时是否需要抓拍车辆图片
                while (true) {
                    try {
                        Thread.sleep(100);
                        if (cxp.checkArriveCoil()) {                                //车辆到达到达线圈
                            synchronized (vehObject) {
                                if (veh == null) {
                                    veh = new Vehicle();
                                }
                            }
                            if (flag) {//车辆到达线圈,抓拍一张图片备用（若车辆在后续交易过程中冲关而无法正常产生交易图片,此图片将作为交易图片使用）
                                extJFrame.grab();//抓拍视频图像,生成临时图像文件
                                flag = false;//车辆经过到达线圈时已经抓拍图像
                            }
                            if (onduty && !amc.isActivated() && !vehArrived) {//已上班,非自助发卡车道,车辆第一次到达
                                AudioControl.getSingleInstance().playAudioWelcome();
                                vehArrived = true;
                            }
                            extJFrame.updateArriveCoilBlack();                      //到达线圈变黑
                        } else {
                            flag = true;//车辆已离开,可以再次抓拍图像
                            vehArrived = false;
                            extJFrame.updateArriveCoilWhite();                      //到达线圈变白
                        }
                        if (cxp.checkPassageCoil()) {//通道线圈有车
                            if (runWhileVehLeave) {
                                vehLeaving = true;//车辆正在离开
                                vehLeft = false;
                                extJFrame.updatePassCoilBlack2();//车辆离开时
                            } else {
                                extJFrame.updatePassCoilBlack1();//违章车辆
                            }
                        } else {//通道线圈无车
                            if (runWhileVehLeave) {
                                if (vehLeaving) {//车辆已离开
                                    vehLeaving = false;
                                    vehLeft = true;
                                    vehArrived = false;
                                }
                                extJFrame.updatePassCoilWhite2();//等待车辆离开
                            } else {
                                vehLeaving = false;
                                vehLeft = false;
                                extJFrame.updatePassCoilWhite1();//一般情况
                            }
                        }
                        synchronized (vehObject) {
                            if (veh != null && !chargeFlag) {//尚未收费时可以修改当前车辆信息，收费后，不可修改当前车辆信息
                                if (veh.getDate() == null) {
                                    veh.setDate(new Date(System.currentTimeMillis()));
                                }
                                if (axleGroupPreStr == null || axleGroupPreStr.trim().isEmpty()) {//轴组类型集合
                                    axleGroupPreStr = weighControl.getAxleGroupStr();
                                }
                                if (preWeight == 0) {//修改前轴重
                                    preWeight = weighControl.getWeight();
                                }
                                if (veh.getAxleCount() == 0 || weighControl.isModifyFlag()) {
                                    veh.setAxleCount(weighControl.getAxleCount());      //获取轴数
                                }
                                if (veh.getAxleGroupCount() == 0 || weighControl.isModifyFlag()) {
                                    veh.setAxleGroupCount(weighControl.getAxleGroupCount());//获取轴组数
                                }
                                if (veh.getWeight() == 0.0 || weighControl.isModifyFlag()) {
                                    veh.setWeight(weighControl.getWeight());            //获取检测重量
                                }
                                if (veh.getLimitWeight() == 0.0 || weighControl.isModifyFlag()) {
                                    veh.setLimitWeight(weighControl.getLimitWeight());  //获取限重
                                }
                                if (veh.getAxleGroupStr() == null || veh.getAxleGroupStr().trim().isEmpty() || weighControl.isModifyFlag()) {
                                    veh.setAxleGroupStr(weighControl.getAxleGroupStr());
                                }
                                if (veh.getAxleWeightDetail() == null || veh.getAxleWeightDetail().trim().isEmpty() || weighControl.isModifyFlag()) {
                                    veh.setAxleWeightDetail(weighControl.getAxisWeightDetail());
                                }
                                
                                if (weighControl.isModifyFlag()) {
                                    weighControl.setModifyFlag(false);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        LogControl.logInfo("车辆检测异常", ex);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex1) {
                        }
                    }
                }
                
            }
        });
    }

    /**
     * 用于费显上显示重量信息以及支付找零信息
     */
    private void tfiControl() {
        //该线程用于显示重量信息,支付找零信息及代收信息,因为信息的位置重叠,因此每4秒钟进行一次更新
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    try {
                        Thread.sleep(100);
                        if (i == Integer.MAX_VALUE) {
                            i = 0;
                        }
                        i++;
                        if (i % 3 == 0) {
                            if (veh != null && veh.getVehicleClass() >= 7) {//货车才显示重量信息
                                //费显显示总轴重及限重
                                tfi.showTotalWeightAndLimitWeight(veh.getFareWeight(), veh.getLimitWeight());
                            } else {
                                if (collectFlag) {
                                    i++;
                                }
                                continue;
                            }
                        } else if (i % 2 == 0) {
                            //费显显示支付总金额及找零金额
                            if (totalF != null || changeF != null) {
                                tfi.showTotalAndChange(totalF, changeF);
                            } else {
                                continue;
                            }
                        } else {
                            if (collectFee != 0) {
                                tfi.showCollectFee(collectFee);//显示代收通行费
                            } else {
                                continue;
                            }
                        }
                        Thread.sleep(4000);
                    } catch (Exception ex) {
                        LogControl.logInfo("费显更新异常", ex);
                    }
                }
            }
        });
    }

    /**
     * 上班之前的流程 1 在收费界面上显示车控器(手动栏杆,自动栏杆,雨棚灯,通行灯等)状态,若手动栏杆未打开不能上班 <br>
     * 2 收费界面显示当前车道信息 <br>
     * 3 收费界面显示等待上班信息,当前时间 <br>
     * 4 收费界面显示异常信息<br>
     * 5 收费界面显示卡箱信息
     *
     * @return 身份卡,若返回null表示读取身份卡失败
     */
    private Card runBeforeBoj() {
        try {
            extJFrame.hideVideo();//上班之前隐藏视频界面
            LogControl.logInfo("等待上班");
            sendInfo("等待上班");
            extJFrame.updateInfo("等待上班", "等待上班");
            tfi.clearScreen();
            String str;
            int i = 300;
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                runCheckParam();
                extJFrame.updateInfo("等待上班", "等待上班");
                runConfirmFareParamError();
                if (!lc.isBureauOnLine() && offLineControl.isTerminated()) {
                    //离线,并且离线倒计时已归0
                    extJFrame.updateInfo("离线倒计时已结束", "离线倒计时已结束,需联网才能上班");
                }
                i++;
                if (update.checkUpdate("MTCLane") && i >= 300) {//发现升级文件
                    boolean flag = update.confirmUpdate();//进行升级
                    if (flag) {//确认进行升级
                        System.exit(0);
                    } else {//取消升级,5分钟之后再做提示
                        extJFrame.updateInfo("等待上班", "等待上班");
                        i = 1;
                    }
                }
                str = keyboard.getMessage();
                if (str != null) {
                    break;
                }
                Thread.sleep(100);
            }
            switch (str) {
                case Keyboard.ONDUTY:
                    if (!lc.isBureauOnLine() && offLineControl.isTerminated()) {
                        //离线,并且离线倒计时已归0
                        extJFrame.updateInfo("离线倒计时已结束", "离线倒计时已结束,需联网才能上班");
                        return runBeforeBoj();
                    }
                    extJFrame.updateInfo("身份识别", "将身份卡放在天线上\n输入密码");
                    String temp;
                    Card tempCard;
                    int times = 0;
                    while (true) {                                                  //无卡时等待,直至有卡并且卡片已读
                        Thread.sleep(100);
                        if ((temp = keyboard.getMessage()) != null) {
                            if (temp.equals(Keyboard.CANCEL)) {//退回等待上班
                                return runBeforeBoj();
                            }
                            keyboard.sendAlert();
                        }
//                        tempCard = mireader.getReadedCard();
                        tempCard = m1Control.getReadedCard();
                        
                        if (tempCard == null) {
                            tempCard = cpuCardSvc.getM1Card();//上班时支持ETC读卡器上班
                        }
                        if (tempCard == null) {//无卡
                            extJFrame.updateInfo("身份识别", "将身份卡放在天线上\n输入密码");
                            if (TestControl.isAutoLogin()) {
                                LogControl.logInfo("测试模式,000018自动上班");
                                tempCard = new Card();
                                tempCard.setId("00001");
                                tempCard.setCardSerial("0000000000");
                                break;//测试模式可以不放身份卡自动上班
                            } else {
                                continue;//正常模式等待身份卡
                            }
                        } else {//有卡
                            if (times++ == 0) {//第一次出现
                                LogControl.logInfo("读取身份卡" + tempCard.getId());
                                cicm.backBoltLower();//后卡栓下降,防止卡在卡槽内无法取出
                            }
                            if (!Constant.CARD_TYPE_03.equals(tempCard.getType())) {//检验卡类型
                                extJFrame.updateInfo("卡类型错误", "请将身份卡放到天线上\n输入密码");
                                keyboard.sendAlert();
                                continue;
                            } else {
                                String staffid = tempCard.getId();
                                Operator op = ParamCacheQuery.queryOperator(StringUtils.encodeID(staffid));
                                if (op == null) {//验证员工号有效性及操作范围
                                    extJFrame.updateInfo("未知员工号", "未知员工号\n请放置正确的员工身份卡");
                                    continue;
                                } else if (!op.getAuthorizeStationSerial().contains(lane.getRoadId() + lane.getStationId())) {
                                    extJFrame.updateInfo("未授权员工号", "该员工无权在本站操作\n请放置正确的员工身份卡");
                                    continue;
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                    return tempCard;
                case Keyboard.CART:
                    cartControl.run(0, 1);
                    return runBeforeBoj();
                default:
                    keyboard.sendAlert();
                    return runBeforeBoj();
            }
        } catch (Exception ex) {
            LogControl.logInfo("上班前流程异常", ex);
            return runBeforeBoj();
        }
    }

    /**
     * 交易初始化,车辆离开通道线圈时触发
     *
     * @return 0 完成；1下班；-1异常
     */
    private int initTransaction() {
        LogControl.logInfo("重置交易信息");
        serviceCardSerial = "";//公务卡卡号重置
        tempImg1 = null;
        tempImg2 = null;
        vehLeaving = false;
        vehLeft = false;
        virtualEnt = null;
        cpuCard1 = null;
        cpuCard2 = null;
        cpuCard3 = null;
        card1 = null;
        card2 = null;
//        card3 = null;
        setVeh(null);
        cpuFlag = true;
        m1Flag = true;
        entryMop = "00";
        exitMop = "00";
        vehObserCode = "0";//车辆通行观察码
        chargeObserCode = "0";//收费操作观察码
        mopObserCode = "0";//付款方式观察码
        cardObserCode = "0";//通行卡观察码
        path = "";//实际行驶路径
        pi = null;//实际路径信息
        piSerial = 0;
        pathDetailSerial = 0;
        pay = 0;
        orderPay = 0;
        preWeight = 0;
        axleGroupPreStr = null;
        trafficEnRoadid = null;
        trafficEnStationid = null;
        violationFlag = false;
        chargeFlag = false;
        dist = 0.0;
        etcDis = 1;
        chargeType = ChargeType.NORMAL;
        tfi.showWelcome();
        extJFrame.hideMilitary();
//        extJFrame.hideCentralPanel();
        extJFrame.initCardAndWeight();
        if (lane.isEntry()) {
            extJFrame.hideCollectFee();//隐藏代收界面
        }
        totalF = null;
        changeF = null;
        vi.hideVeh();
        vi.hideEntry();
        vi.hidePlate();
        vi.hideVehType();
        if (lane.isEntry()) {
            vi.showVehType(entryMop);
        }
        extJFrame.initEntyImg();
        extJFrame.clearRtpInfo();
        extJFrame.updateSpecialInfo(null, null);
        lprSvc.initPlateInfo();//初始化车牌识别信息
        extJFrame.initLprPlate();//初始化抓拍车牌
        if (!runWhileVehLeave) {//若车辆正在离开，不进行车牌识别
            lprSvc.startCaptureImg();//交易开始,开始进行车牌识别
        }
        if (cartControl.isEmpty() && lane.isEntry()) {//入口卡箱已空
            if (!cartControl.isRunning()) {//上一辆车离开时,正在进行卡箱操作,需要结束或取消卡箱操作才能继续交易
                extJFrame.updateInfo("", "卡箱已空,请更换卡箱");
            }
            keyboard.sendAlert();
        } else if (cartControl.isFull() && lane.isExit()) {//出口卡箱已满
            if (!cartControl.isRunning()) {//上一辆车离开时,正在进行卡箱操作,需要结束或取消卡箱操作才能继续交易
                extJFrame.updateInfo("", "卡箱已满,请更换卡箱");
            }
            keyboard.sendAlert();
        }
        try {
            cicm.backBoltRaise();//后卡栓上升
            cicm.frontBoltRaise();//前卡栓上升
        } catch (Exception ex) {
            LogControl.logInfo("", ex);
        }
        try {
            if (recordId % 10 == 0 && lane.isExit() && !cartControl.isRunning()) {//出口每10条交易重置一次收发针,保持收发针位置
//                cicm.receivePinRaise();
                pinControl.pinRaise();
            }
        } catch (Exception ex) {
            LogControl.logInfo("", ex);
        }
        amc.initTransaction();//交易初始化,重置自助卡机信息
        collectFee = 0;

        //by zengzb add
        if (lane.isEntry() && isXasCICM()) {
            collectFlag = false;//自助发卡车道不进行代收通行费
        } else if (lane.isEntry() && amc.isActivated()) {
            collectFlag = false;//自助发卡车道不进行代收通行费
        } else {
            collectFlag = FunctionControl.isCollActive();
        }
        int temp = 0;
        if (lane.isEntry() && !amc.isActivated() && ticketNum == null && collectFlag) {//入口代收通行费非自助发卡车道,尚未输入发票号码
            if (FunctionControl.isTrackActive()) {
                temp = getTicketAndTrackNum();//输入发票号及字轨号
            } else {
                temp = getTicketNum();//输入发票号码
            }
        }
        rtSvc.initPathInfo();
        return temp;
    }

    /**
     * 上班之后的流程,包括读卡前流程+读卡时流程+读卡后流程
     */
    private void runAfterBoj() {
        try {
            extJFrame.showVideo();
            cicm.protectiveCoverLockUp();
            cicm.backBoltRaise();
            cicm.frontBoltRaise();
//            cicm.receivePinRaise();
            pinControl.pinRaise();
//            if (lane.isExit() && cicm.isPinTop()) {
//                cicm.lowerTwoSteps();
//            }
            weighControl.updateVehAxle();
            LogControl.logInfo(staff.getStaffId() + "已上班");
            sendInfo("已上班");
            vi.showStaff(staff.getStaffId());//字符叠加显示收费员员工号
            extJFrame.updateAfterBOJ(lane, staff); //上班之后的操作
            if (lane.isEntry()) {
                amc.loadConfig();//初始化
                if (amc.isActivated()) {
                    extJFrame.updateRunningMode(ExtJFrame.Auto);
                } else {
                    extJFrame.updateRunningMode(ExtJFrame.Manual);
                }
            }
            //启用，增加对上一次下班时间校验
            int tmp;
            if(!TimeCheckControl.isVaildTime()){
            	Integer Interval = TimeCheckControl.getConfigDifference().intValue();
            	extJFrame.updateInfo("", "与上一次下班时间跨度大于"+Interval+"小时，请检查。\n请按【取消】键继续");
            	LogControl.logInfo("与上一次下班时间跨度大于"+Interval+"小时，请检查");
            	tmp = tipsTimeDifference();
            }
            
            int temp;
            if (FunctionControl.isTrackActive()) {
                temp = getTicketAndTrackNum();//输入发票号及字轨号
            } else {
                temp = getTicketNum();//输入发票号码
            }
            if (!onduty) {
                return;
            }
            sendInfo("雨棚灯红灯");
            red://雨棚灯为红灯时
            while (canopyFlag == 1) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                if (!lc.isBureauOnLine() && offLineControl.isTerminated()) {
                    //离线,并且离线倒计时已归0,自动下班
                    runChangeCanopyRed();//自动改为红灯
                    logout(LOGOUT_AUTO);
                    return;
                }
                extJFrame.updateInfo("", "等待雨棚灯变绿");
                String str = keyboard.getMessage();
                if (str == null) {
                    continue;
                }
                switch (str) {
                    case Keyboard.OFFDUTY:
                        boolean flag = logout(LOGOUT_MANUAL);
                        if (flag) {//下班成功
                            return;
                        }
                        break;
                    case Keyboard.GREEN:
                        LogControl.logInfo("雨棚灯变绿");
                        cxp.changeCanopyLightGreen();
                        extJFrame.setCanopyLightGreen();
                        canopyFlag = cxp.getCanopyFlag();
                        sendInfo("雨棚灯绿灯");
                        break;
                    case Keyboard.CART:
                        cartControl.run(0, 1);
                        break;
                    default:
                        keyboard.sendAlert();
                        continue;
                }
            }
            
            green://雨棚灯为绿灯时
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                int flag = initTransaction();//交易初始化
                if (flag == 1) {//选择下班,退出上班后流程
                    onduty = false;
                    return;
                }
                flag = runBeforeReadCard(true);//读卡之前的流程
                synchronized (violateObj) {
                    if (violationFlag) {
                        continue;
                    }
                }
                if (flag == 1) {//选择下班,退出上班后流程
                    onduty = false;
                    return;
                } else if (flag == -2) {//违章车
                    continue;
                } else if (flag == 2) {
                    continue;
                }
                runAntiBack();
                extJFrame.updateInfo("等待读取通行卡", "等待读取通行卡");
                autoPlateFlag = false;//进入读卡流程,不可再手动修改车牌
                flag = runWhileReadCard();                                      //读卡时流程
                synchronized (violateObj) {
                    if (violationFlag) {
                        continue;
                    }
                }
                if (flag == 3) {//自助发卡等待读卡时按【取消】键
                    //by zengzb
                    if (isXasCICM() && lane.isEntry()) {
                        autoPlateFlag = true;//自助发卡模式下手动修改车牌
                    }
                    if (amc.isActivated() && lane.isEntry()) {
                        autoPlateFlag = true;//自助发卡模式下手动修改车牌
                    }
                    continue;
                }
                if (flag == 1 || flag == -2) {                 //等待通行卡时违章车或异常
                    continue;
                }
                if (flag == 2) {//直接放行车辆
                    if (FunctionControl.isetcExitListActive()) {//ETC卡放行车辆记录简单流水
                        generateSimpleTransInfo();
                    }
                    runWhileVehLeaveNew(1);//仅进行车辆离开处理,不做任何记录
                    continue;
                } else if (flag == 4) {//纸券模式

                    generateTempTransImg();//创建临时交易图片
                    runWhileVehLeaveNew(0);
                    continue;
                }
                if (lane.isExit()) {                               //出口流程
                    extJFrame.updateAfterReadingCard(pay);                      //显示收费信息（出口）
                    flag = runChargeFare();                                     //读卡之后的流程（出口） 
                    synchronized (violateObj) {
                        if (violationFlag) {
                            continue;
                        }
                    }
                    if (flag == -2) {                          //违章车
                        continue;
                    }
                    chargeFlag = true;//收费成功
                    runPrint();//打印票据
                    extJFrame.updateInfo("接受付款", "接受付款");
                    LogControl.logInfo("等待付款确认");
                    String str;
                    while (true) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                        }
                        if (lane.isExit()) {
                            extJFrame.showCardAndWeight();
                        }
                        Thread.sleep(100);
                        synchronized (violateObj) {
                            if (violationFlag) {
                                continue green;
                            }
                        }
                        str = keyboard.getMessage();
                        if (str == null) {
                            continue;
                        } else if (Keyboard.CONFIRM.equals(str)) {                        //等待付款确认(出口)
                            break;
                        } else if (Keyboard.SIMU.equals(str)) {
                            int tempFlag = 0;
//                            if ("01".equals(exitMop)) {//现金付款时才有重打印发票功能
                            if (("01".equals(exitMop) && pay > 0) || (collectFlag && collectFee > 0)) {
                                tempFlag = simControl.runSimulate(5);
                            } else {//非现金付款无重打印发票功能
                                keyboard.sendAlert();
                            }
                            switch (tempFlag) {
                                case '0'://取消操作
                                    break;
                                case '4':
//                                    runPrint();//重打印发票
                                    LogControl.logInfo("重打印发票");
                                    runPrint();//打印票据
                                    logout.setReceiptMore(logout.getReceiptMore() + 1);//重打票次数加1
                                    FlowControl.chargeObserCode = "7";
                                    ls.addBadInVoiceCnt();//废票数加1
                                    break;
                            }
                            extJFrame.updateInfo("接受付款", "接受付款");
                        } else if (str.equals(Keyboard.RED)) {//收费过程中雨棚灯变红
                            runChangeCanopyRed();
                        } else {
                            keyboard.sendAlert();
                        }
                    }
                }
//                createImg();
                generateTempTransImg();//创建临时交易图片
//                runWhileVehLeave(0);                                                 //等待车辆离开
                runWhileVehLeaveNew(0);
            }
        } catch (Exception ex) {
            LogControl.logInfo("上班后流程异常", ex);
        }
    }

    /**
     * 提示收费员存在时间差异
     */
    private int tipsTimeDifference() {
        
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Logger.getLogger(FlowControl.class.getName()).log(Level.SEVERE,
                        null, e);
            }
            String str = keyboard.getMessage();
            if (str == null) {
                continue;
            }
            switch (str) {
                case Keyboard.CANCEL:
                    break;
                default:
                    keyboard.sendAlert();
                    continue;
            }
            return 0;
        }
    }

    /**
     * 读卡前流程<br>
     * 1 车辆到达后,收费员输入车型及车牌颜色 <br>
     * 2 车牌识别获得车牌,收费员确认车牌识别获得的车牌,如有不同需要更改车牌,收费员确认,卡机中后卡栓下降<br>
     * 3 若出现无卡,残卡,走无卡,残卡流程,否则进入读卡流程<br>
     *
     * @param auto true 自动获取识别车牌<br>
     * false 强制人工确认车牌
     * @return 0执行成功<br>
     * 1下班<br>
     * 2 取消<br>
     * -1 其他异常<br>
     * -2冲关车
     */
    private int runBeforeReadCard(boolean auto) {
        try {
            vi.hideVeh();//字符叠加隐藏车辆信息
            vi.hidePlate();//字符叠加隐藏车牌信息
            extJFrame.setVehClassBLU();//车型设置为蓝色,表示尚未确认
            extJFrame.initTransaction();//收费界面初始化
            //进入车型输入流程
            int i = runGetVehClass();
            if (i == 1) {                                           //下班
                LogControl.logInfo("下班");
                return 1;
            } else if (i == -2) {
                return -2;
            } else if (i == 3) {//取消
                return 2;
            }
            sendInfo("车型确认:" + veh.getVehicleClass());
            LogControl.logInfo("车型确认:" + veh.getVehicleClass());
            extJFrame.updateVehClass(veh.getVehicleClass() + "");
            extJFrame.setVehClassBLK();
            if (lane.isEntry() && amc.isActivated()) {//自助发卡车道,显示入口站及车型
                tfiSvc.showStationAndVehClass(lane.getStationName(), veh.getVehicleClass());
            } else {
                tfi.showVehClass(veh.getVehicleClass() + "");//费显显示车型
            }
            vi.showVehClass(veh.getVehicleClass() + "");//字符叠加显示车型
            if (lane.isEntry() && amc.isActivated() && FunctionControl.isAutoForbidActive()) {//入口自助发卡车道启用了禁止通行功能
                AutoForbidService afbsvc = new AutoForbidService(keyboard, extJFrame, cxp);
                boolean flag = afbsvc.checkAutoForbitVeh(veh.getVehicleClass() + "");
                if (flag) {
                    return 2;
                }
            }
            //进入车牌颜色选择流程
            i = runGetPlateColor(auto);
            if (i == -2) {//冲关车
                return -2;
            } else if (i == 1) {                                     //取消选择车牌颜色
                LogControl.logInfo("取消选择车牌颜色");
//                return runBeforeReadCard(true);
                return 2;
            }
            sendInfo("车牌颜色确认:" + veh.getKeyedPlateColor());
            LogControl.logInfo("自动识别车牌颜色：" + veh.getLprPlateColor());
            LogControl.logInfo("车牌颜色确认:" + veh.getKeyedPlateColor());
            if (lane.isEntry() && amc.isActivated()) {//自助发卡车道
                tfiSvc.showVehPlateColor(veh.getKeyedPlateColor());
            }
            //进入全车牌输入流程
            i = runGetPlateNum();
            if (i == -2) {//冲关车
                return -2;
            } else if (i == 1) {                                               //取消车牌号码选择,退回车牌颜色选择
                LogControl.logInfo("取消车牌号码选择,退回车牌颜色选择");
                return runBeforeReadCard(false);
            }
            LogControl.logInfo("自动识别全车牌：" + veh.getLprFullVehPlateNum());
            LogControl.logInfo("全车牌确认:" + veh.getFullVehPlateNum());
            sendInfo("全车牌确认:" + veh.getFullVehPlateNum());
            if (lane.isEntry() && amc.isActivated()) {//自助发卡车道
                tfiSvc.showVehPlateColor(veh.getKeyedPlateColor());//全车牌确认期间,车牌颜色可能会发生变化,因此费显需要更新车牌颜色
                tfiSvc.showVehPlateNum(veh.getFullVehPlateNum());//费显显示车牌号码
            }
            vi.showPlate(veh.getFullVehPlateNum());//字符叠加显示车牌后三位
            int flag = mainSvc.checkSpeVeh(veh);
            if (flag == 2) {                                                        //违禁车辆禁止通行
                FlowControl.chargeObserCode = "6";
                LogControl.logInfo("违禁车辆,禁止通行");
                sendInfo("违禁车辆,禁止通行");
                setVeh(null);
                if (lane.isEntry() && amc.isActivated()) {//自助发卡模式下黑灰名单车辆禁止通行时
                    amc.initTransaction();//重置自助发卡机信息
                }
                return runBeforeReadCard(true);
            } else if (flag == 1) {
                LogControl.logInfo("白名单车辆");
                sendInfo("白名单车辆");
                veh.setVehicleType(Constant.INSIDE_VEH);
                extJFrame.hideMilitary();//白名单车辆和军警车不共存
            } else {
                veh.setSpeVehFlag(0);//普通车辆
                LogControl.logInfo("普通车辆");
            }
            return 0;
        } catch (Exception ex) {
            LogControl.logInfo("读卡前流程异常", ex);
            return -1;
        }
    }

    /**
     * 设置虚拟入口站,该流程的用意在于允许收费员通过选择虚拟入口站改变写卡时的入口站（用于榔梨东机场高速代收问题）
     *
     * @return 收费员选择的虚拟入口站代号,如0401,若返回null表示退出虚拟入口站选择
     *
     */
    public String setVirtualEnt() {
        return null;
//        String virEnt = PropertiesCacheUtils.getPropertiesValue("virtualEnt", lane.getRoadId() + lane.getStationId());
//        LogControl.logInfo("虚拟入口:" + virEnt);
//        if (virEnt.equals(lane.getRoadId() + lane.getStationId())) {//虚拟入口为当前站,不需设置虚拟入口站
//            return null;
//        } else {
//            String[] buffer = virEnt.split(",");
//            Map<String, String> map = new HashMap();
//            for (String str : buffer) {
//                String road = str.substring(0, 2);
//                String station = str.substring(2, 4);
//                String stationName = mainSvc.getStationName(road, station);
//                map.put(str, stationName);
//            }
//            extJFrame.setVirtualEnt(map);
//            extJFrame.showVirtualEnt();
//            extJFrame.updateInfo("选择虚拟入口站", "请按数字键选择虚拟入口站\n\r或按【取消】键取消虚拟站选择");
//            while (true) {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException ex) {
//                }
//                String str = keyboard.getMessage();
//                if (str == null) {
//                    continue;
//                } else if (str.matches("[1-9]")) {//选择虚拟入口站
//                    Set<String> set = map.keySet();
//                    List<String> list = new ArrayList(set);
//                    Collections.sort(list);
//                    int i = IntegerUtils.parseString(str) - 1;
//                    if (i > list.size() - 1) {//收费员按键超过虚拟入口选择范围
//                        keyboard.sendAlert();
//                        continue;
//                    }
//                    return list.get(i);//获取收费员选择的虚拟入口
//                } else if (str.equals(Keyboard.CANCEL)) {//退出虚拟入口站选择（默认本站）
//                    return null;
//                } else if (str.equals(Keyboard.CONFIRM)) {//确认虚拟入口站
//                    String result = extJFrame.getVirtualEnt();//获取收费员选择的虚拟入口站
//                    return result;
//                } else {
//                    keyboard.sendAlert();
//                }
//            }
//        }
    }

    /**
     * 进入车型确认流程
     *
     * @return 0成功 <br>
     * 1选择下班 <br>
     * 2并未选择车型<br>
     * 3取消<br>
     * -1异常<br>
     * -2冲关车
     * @throws java.lang.Exception
     */
    private int runGetVehClass() throws Exception {
        try {
            tfi.showWelcome();//此处显示欢迎信息是为了在收费员取消输入车型后再次显示欢迎信息
            extJFrame.cleanCursor();
            int flag;
            boolean lightFlag = true;
            boolean firstFlag = true;
            int temp = IntegerUtils.parseString(ticketNum);
            StringBuilder sb = new StringBuilder();
            if (temp >= 999999999) {//发票号码超过999999999
                sb.append("\n发票已用完，请更换发票");//更换发票提示信息
            }
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                synchronized (violateObj) {
                    if (violationFlag) {//冲关车
                        return -2;
                    }
                }
                if (cartControl.isRunning()) {//上一辆车离开时,正在进行卡箱操作,需要结束或取消卡箱操作才能继续交易
                    continue;
                }
                if (!lc.isBureauOnLine() && offLineControl.isTerminated()) {
                    //离线,并且离线倒计时已归0,自动下班
                    runChangeCanopyRed();//自动改为红灯
                    logout(1);
                    return 1;
                }
//                if (lane.isEntry() && amc.isActivated() && abc.isVehBack()) {//自助发卡车道发现防盗车取卡被激活
//                    if (vehCount == 1) {//上班后第一辆车倒车不进行锁定处理
//                        LogControl.logInfo(staff.getStaffId() + "上班后第一辆车发现倒车,不进行锁定");
//                        abc.setVehBack(false);
//                    } else {
//                        abc.lock();
//                    }
//                }
                runConfirmFareParamError();
                extJFrame.hideCentralPanel();
//                runConfirmFareParamError();
                synchronized (vehObject) {
                    if (veh == null) {
                        veh = new Vehicle();
                    }
                }
                if (veh.getVehicleClass() != 0) {
                    flag = 0;
                    break;
                }
                if (!cartControl.isHasCart()) {
                    extJFrame.updateInfo("", "请装入卡箱" + sb);
                    if (firstFlag) {//第一次触发,键盘报警
                        keyboard.sendAlert();
                        firstFlag = false;
                    }
                } else if (cartControl.isEmpty() && lane.isEntry()) {//入口卡箱已空
                    extJFrame.updateInfo("", "卡箱已空,请更换卡箱" + sb);
                    if (firstFlag) {//第一次触发,键盘报警
                        keyboard.sendAlert();
                        firstFlag = false;
                    }
                } else if (cartControl.isFull() && lane.isExit()) {//出口卡箱已满
                    extJFrame.updateInfo("", "卡箱已满,请更换卡箱" + sb);
                    if (firstFlag) {//第一次触发,键盘报警
                        keyboard.sendAlert();
                        firstFlag = false;
                    }
                } else if (!lightFlag) {
                } else if (canopyFlag == 2) {
                    extJFrame.updateInfo("", "等待输入车型" + sb);
                } else if (canopyFlag == 1) {
                    extJFrame.updateInfo("", "等待雨棚灯变绿" + sb);
                }
                String str = keyboard.getMessage();
                //******************************自助发卡**********************************************//
                if (lane.isEntry() && amc.isActivated()) {//判断是否启用自助发卡机
                    int i = amc.getVehClass();//获取自助发卡机判断车型
                    if (i == 0) {
                        //无车型识别结果,但卡机按键并且到达线圈被激活,默认1型车
                        if (amc.isKeyPressed() && cxp.checkArriveCoil()) {
                            LogControl.logInfo("无车型识别,卡机按键以及到达线圈被激活,默认1型车");
                            str = "1";
                        }
                    } else if ((i >= 1 && i <= 6) || i == 7) {//有效车型为1-6,7
                        str = String.valueOf(i);
                    } else {
                        //车型识别异常,超出规定范围,默认1型车
                        LogControl.logInfo("车型识别异常：" + i);
                        str = "1";
                    }
                }

                //by zengzb add
                String tempStr = str;//暂时存放键盘输入
                if (lane.isEntry() && isXasCICM()) {
                    //LogControl.logInfo("西埃斯自助卡机键盘输入车型 >>>>>"+str);
                    if (cxp.checkArriveCoil()) {
                        int vehclass = avcControl.getAvc().getVehClass();
                        LogControl.logInfo("车型分类器返回车型：" + vehclass);
                        if (vehclass == 0) {
                            String defVehClass = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "manualDefVehClass", "0");
                            if ("1".equals(defVehClass)) {//开启使用默认车型
                                LogControl.logInfo("无车型识别,到达线圈被激活,系统默认1型车");
                                str = "1";
                            } else {//0：关闭使用默认车型
                                //LogControl.logInfo("无车型识别,到达线圈被激活,系统且已关闭默认1型车");
                                str = "";
                            }
                        } else if ((vehclass >= 1 && vehclass <= 6) || vehclass == 7) {//有效车型为1-6,7
                            str = String.valueOf(vehclass);
                        } else {
                            //车型识别异常,超出规定范围,默认1型车
                            LogControl.logInfo("车型识别异常：" + vehclass);
                            str = "1";
                        }
                    }
                }
                //******************************自助发卡**********************************************//
                if (str == null) {
                    Thread.sleep(100);
                    continue;
                } else {
                    str = transVehClassByCh(str);
                    if ("".equals(str) && (tempStr != null && tempStr.matches("[1-7]"))) {//by zengzb 
                        str = tempStr;
                    }
                }
                if (str.matches("[1-7]")) {                                         //如果输入的是数字
                    if ("6".equals(str)) {
                        if (lane.isEntry() && FunctionControl.isEntry6Available()) {
                            //入口是否支持6型车的输入
                        } else {
                            keyboard.sendAlert();
                            extJFrame.updateInfo("无效车型", "无效车型,请输入有效车型");
                            lightFlag = false;
                            continue;
                        }
                    }
                    if (lane.isExit()) {
                        if ("7".equals(str)) {                                          //出口无重量信息时不可按计重收费
                            if (weighControl.getLimitWeight() == 0 || weighControl.getWeight() == 0) {
                                keyboard.sendAlert();
                                extJFrame.updateInfo("无效车型", "无效车型,请输入有效车型");
                                lightFlag = false;
                                continue;
                            }
                        }
                    }
                    extJFrame.updateVehClass(str);                                  //界面显示车型
//                    tfi.showVehClass(str);                                          //费显显示车型
                    veh.setVehicleClass(IntegerUtils.parseString(str));
                    lprSvc.startCaptureImgNew();//车型已确认，触发车牌识别
                    flag = 0;
                    break;
                } else if (Keyboard.CART.equals(str)) {
                    lightFlag = true;
                    cartControl.run(0, 1);
                } else if (Keyboard.WEIGHT.equals(str)) {                                  //按计重键（删除车辆信息）
                    lightFlag = true;
                    if (lane.isEntry()) {//入口封闭计重功能
                        keyboard.sendAlert();
                    } else {
                        weighControl.runWeighControl();
                    }
                } else if (Keyboard.EMER.equals(str)) {                                  //进入紧急车流程
                    lightFlag = true;
                    runEmerVeh();
                } else if ((Keyboard.RED).equals(str)) {
                    lightFlag = true;
                    extJFrame.updateInfo("", "等待雨棚灯变绿");
                    runChangeCanopyRed();
                } else if (Keyboard.GREEN.equals(str)) {
                    lightFlag = true;
                    cxp.changeCanopyLightGreen();
                    extJFrame.setCanopyLightGreen();
                    LogControl.logInfo("雨棚灯绿灯");
                    canopyFlag = cxp.getCanopyFlag();
                    sendInfo("雨棚灯绿灯");
                } else if ((Keyboard.OFFDUTY).equals(str)) {
                    if (canopyFlag == 1) {
                        flag = logout(LOGOUT_MANUAL) ? 1 : 2;
                        if (flag == 1) {//下班成功
                            onduty = false;
                            break;
                        }
                    } else {
                        lightFlag = false;
                        extJFrame.updateInfo("", "拒绝下班\n应首先将雨棚信号灯置为红色");
                    }
                } else if (Keyboard.SIMU.equals(str)) {
                    if (lane.isEntry()) {
                        runChangeRunningMode();//更改发卡模式
                        return 3;
                    } else {
                        keyboard.sendAlert();
                    }
                } else {
                    keyboard.sendAlert();
                }
            }
            return flag;
        } catch (Exception ex) {
            LogControl.logInfo("确认车型异常", ex);
            return -1;
        }
    }

    /**
     * 进入车牌颜色确认流程
     *
     * @param auto true自动获取识别车牌 false强制人工确认车牌
     * @return 0 成功 <br>
     * 1 取消选择<br>
     * -1 异常<br>
     * -2冲关车
     */
    private int runGetPlateColor(boolean auto) {
        try {
            int flag = 0;
            extJFrame.cleanCursor();
            extJFrame.showPlateColOption();
            extJFrame.updateInfo("", "等待选择车牌颜色");
            long start = System.currentTimeMillis();
            long s1 = System.currentTimeMillis();
            while (true) {
                Thread.sleep(200);
                synchronized (violateObj) {
                    if (violationFlag) {//冲关车
                        return -2;
                    }
                }
                if (lprSvc.getPlateColor() != null) {
                    veh.setLprPlateColor(lprSvc.getPlateColor());
                    if (auto) {//自动获取车牌时
                        veh.setKeyedPlateColor(lprSvc.getPlateColor());
                        flag = 0;
                        break;
                    }
                }
                
                String str = keyboard.getMessage();
                //******************************自助发卡**********************************************//
                if (lane.isEntry() && amc.isActivated()) {//启用自助发卡
                    if (!cxp.checkArriveCoil()) {//车辆到达线圈尚未被激活
                        start = System.currentTimeMillis();
                        if (start < s1) {
                            s1 = start;
                        }
                        String inter = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "autoClassInterval", "5");
                        int interval = IntegerUtils.parseString(inter);
                        if (interval < 1) {
                            interval = 1;
                        }
                        if (start - s1 > interval * 60 * 1000) {//自助发卡模式下车型确认后超过指定时间线圈未被触发,自动取消当前车辆信息
                            LogControl.logInfo("自助发卡模式下车型确认后超过" + interval + "分钟线圈未被触发,自动取消当前车辆信息");
                            str = Keyboard.CANCEL;
                        }
                    }
                    long now = System.currentTimeMillis();
                    if (now < start) {//当前时间小于开始时间,重新开始计时
                        start = now;
                    }
                    String inter = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "autoPlateInterval2", "2");
                    int interval = IntegerUtils.parseString(inter);
                    if (interval < 1) {//等待车牌识别结果时间间隔,最短1秒
                        interval = 1;
                    }
                    if (now - start > interval * 1000) {//车辆到达线圈后过指定时间后仍然没有车牌识别结果,自助发卡机默认为无车牌识别
                        str = "0";
                    }
                    if (amc.isVehBackOff()) {//当前车辆倒车
                        amc.setVehBack(false);
                        str = Keyboard.CANCEL;
                    }
                    
                }

                //by zengzb add
                if (lane.isEntry() && isXasCICM()) {
                    if (!cxp.checkArriveCoil()) {//车辆到达线圈尚未被激活
                        start = System.currentTimeMillis();
                        if (start < s1) {
                            s1 = start;
                        }
                        String iinter = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "autoClassInterval", "5");
                        int iinterval = IntegerUtils.parseString(iinter);
                        if (iinterval < 1) {
                            iinterval = 1;
                        }
                        if (start - s1 > iinterval * 60 * 1000) {//自助发卡模式下车型确认后超过指定时间线圈未被触发,自动取消当前车辆信息
                            LogControl.logInfo("西埃斯自助发卡模式下车型确认后超过" + iinterval + "分钟线圈未被触发,自动取消当前车辆信息");
                            str = Keyboard.CANCEL;
                        }
                    }
                    long lnow = System.currentTimeMillis();
                    if (lnow < start) {//当前时间小于开始时间,重新开始计时
                        start = lnow;
                    }
                    String iinter = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "autoPlateInterval2", "2");
                    int iinterval = IntegerUtils.parseString(iinter);
                    if (iinterval < 1) {//等待车牌识别结果时间间隔,最短1秒
                        iinterval = 1;
                    }
                    if (lnow - start > iinterval * 1000) {//车辆到达线圈后过指定时间后仍然没有车牌识别结果,自助发卡机默认为无车牌识别
                        str = "0";
                    }

                    //处理倒车
                    if (isXasCICM() && avcControl.getAvc().isVehBackOff()) {
                        avcControl.getAvc().setVehBack(false);
                        str = Keyboard.CANCEL;
                    }
                }
                //******************************自助发卡**********************************************//
                if (null == str) {
                    continue;
                } else if (Keyboard.CANCEL.equals(str)) {
                    veh.setVehicleClass(0);
                    veh.setVehicleType(Constant.NORMAL_VEHICLE);
                    extJFrame.hideMilitary();
                    extJFrame.hideCentralPanel();
                    flag = 1;
                    break;
                } else if (Keyboard.CONFIRM.equals(str)) {
                    veh.setKeyedPlateColor("U");
                    flag = 0;
                    break;
                } else if (str.matches(Constant.PLATE_COL_OPTION)) {
                    FullKeyboardService fks = new FullKeyboardService();
                    veh.setKeyedPlateColor(fks.getPlateColor(str));
                    if (Keyboard.ZERO.equals(str)) {
                        veh.setFullVehPlateNum("...");
                    }
                    flag = 0;
                    break;
                } else if (str.equals(Keyboard.ZEROZERO)) {
                    LogControl.logInfo("手动抓拍车牌");
                    lprSvc.initPlateInfo();//手动抓拍车牌,先清空车牌识别信息
                    lprSvc.startCaptureImg();//手动抓拍车牌
                    lprSvc.startCaptureImgNew();//手动抓拍触发车牌识别
                    auto = true;
                    continue;
                } else if (Keyboard.MILITARY.equals(str)) {
                    LogControl.logInfo("军警");
                    runMilitaryVeh();
                } else if (Keyboard.DOT.equals(str)) {
                    veh.setKeyedPlateColor("");
                    flag = 0;
                    break;
                } else {
                    keyboard.sendAlert();
                }
            }
            return flag;
        } catch (Exception ex) {
            LogControl.logInfo("车牌颜色确认异常", ex);
            return -1;
        }
    }

    /**
     * 进入车牌确认流程
     *
     * @return 0 成功 <br>
     * 1 取消输入车牌,进入上一个流程<br>
     * -1 异常<br>
     * -2 冲关车
     */
    private int runGetPlateNum() {
        try {
            veh.setLprFullVehPlateNum(lprSvc.getPlate());
            if (veh.getFullVehPlateNum() == null) {
                veh.setFullVehPlateNum(lprSvc.getPlate());
            }
            int flag;
//            if (veh.getKeyedPlateColor().trim().equals("")) {//无车牌识别,默认车牌为...
//                veh.setFullVehPlateNum("...");
//            }
            extJFrame.updateLprPlate(veh.getLprFullVehPlateNum());
            extJFrame.updateLprPlateCol(veh.getLprPlateColor());
            extJFrame.updateKeyedPlate(veh.getFullVehPlateNum());
            extJFrame.updateKeyedPlateCol(veh.getKeyedPlateColor());
            if (FunctionControl.isFullKeyboardFunActive()) {
                extJFrame.showFullPlateOption1();
            } else {
                extJFrame.showFullPlateOption();
            }
            
            extJFrame.updateInfo("输入车牌号", "请输入完整车牌号码\n或连续按【.】键复制车牌识别结果\n无车牌请输入【...】并按【确认】键继续操作");
            String order;
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                synchronized (violateObj) {
                    if (violationFlag) {//冲关车
                        return -2;
                    }
                }
                if ((order = keyboard.getMessage()) != null) {//收费员按键表示收费员手动键入车牌,停止获取车牌识别
                    break;
                }
                if (veh.getFullVehPlateNum() == null || veh.getFullVehPlateNum().isEmpty()) {//无车牌识别信息,继续进行车牌识别
                    Thread.sleep(200);
                    veh.setLprFullVehPlateNum(lprSvc.getPlate());
                    veh.setFullVehPlateNum(lprSvc.getPlate());
                    veh.setLprPlateColor(lprSvc.getPlateColor());
                } else {
                    break;
                }
            }
            extJFrame.updateLprPlate(veh.getLprFullVehPlateNum());
            extJFrame.updateLprPlateCol(veh.getLprPlateColor());
            extJFrame.updateKeyedPlate(veh.getFullVehPlateNum());
            extJFrame.updateKeyedPlateCol(veh.getKeyedPlateColor());
            if (FunctionControl.isFullKeyboardFunActive()) {
                extJFrame.showFullPlateOption1();
            } else {
                extJFrame.showFullPlateOption();
            }
            extJFrame.updateInfo("输入车牌号", "请输入完整车牌号码\n或连续按【.】键复制车牌识别结果\n无车牌请输入‘...’并按【确认】键继续操作");
            //******************************自助发卡***********************************//
            if (lane.isEntry() && amc.isActivated()) {//启用自助发卡
                String temp = veh.getFullVehPlateNum();
                if (temp != null && temp.length() >= 3) {//当前车牌号码非空
                    if (!autoPlateFlag) {//当前并非手动输入车牌
                        order = Keyboard.CONFIRM;//自动确认车牌
                    }
                }
            }

            //by zengzb add
            if (lane.isEntry() && isXasCICM()) {
                String temp = veh.getFullVehPlateNum();
                if (temp != null && temp.length() >= 3) {//当前车牌号码非空
                    if (!autoPlateFlag) {//当前并非手动输入车牌
                        order = Keyboard.CONFIRM;//自动确认车牌
                    }
                }
            }
            //******************************自助发卡***********************************//
            FullKeyboardService fks = new FullKeyboardService();
            fks.setFc(this);
            String plate = fks.getFullPlate(extJFrame, keyboard, veh.getLprFullVehPlateNum(), veh.getFullVehPlateNum(), order, autoPlateFlag);
            veh.setFullVehPlateNum(plate);
            if (plate == null) {//取消车牌选择
                flag = 1;
                veh.setKeyedPlateColor(null);
                veh.setLprFullVehPlateNum(null);
                veh.setLprPlateColor(null);
            } else {
                lprSvc.stopCaptureImg();//车牌已经确认,停止车牌识别
                flag = 0;
            }
            return flag;
        } catch (Exception ex) {
            LogControl.logInfo("车牌确认异常", ex);
            return -1;
        }
    }
    /**
     * 虚拟入口站,如0401
     */
    String virtualEnt = null;

    /**
     * 读卡时的流程 1 收费员将通行卡放入卡机后,读卡器读取通行卡中卡状态信息,入口以及车辆信息,若为不可读卡或坏卡,进入无可读卡或坏卡流程 <br>
     * 2 确认卡状态是否正常,出入口车辆信息是否一致,以及车辆是否超时 <br>
     * 3 如一切正常,进入收费流程;如以上确认存在异常,进入监控室确认流程,监控室确认后再进入收费流程
     *
     * @return 成功返回0 <br>
     * 取消操作返回1 <br>
     * 当前车辆直接放行2 <br>
     * 自助发卡模式下取消操作返回3<br>
     * 纸券模式返回4<br>
     * 车辆违章返回-2 <br>
     * 异常返回-1<br>
     */
    private int runWhileReadCard() {
        try {
            String temp = null;
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                //若上一辆车未离开,需要等待上一辆车离开后才能进行后续交易
                if (runWhileVehLeave) {//上一辆车尚未离开
                    LogControl.logInfo("等待上一辆车离开");
                    extJFrame.updateInfo("", "等待上一辆车离开");
                } else {
                    break;
                }
                temp = keyboard.getMessage();
                if (temp == null) {
                } else if (temp.equals(Keyboard.CANCEL)) {
                    if (lane.isEntry() && amc.isActivated()) {//自助发卡
                        return 3;
                    } else if (lane.isEntry() && isXasCICM()) {//西埃斯自助发卡
                        return 3;
                    } else {
                        return 1;
                    }
                    
                } else {
                    keyboard.sendAlert();
                }
                Thread.sleep(200);
            }
            if (lane.isEntry()) {//入口设置虚拟入口站
                virtualEnt = setVirtualEnt();
            }
            LogControl.logInfo("等待读取通行卡");
            if (lane.isExit()) {//出口
                cicm.backBoltLower();//后卡栓下降（出口）
                cicm.frontBoltRaise();//前卡栓上升（出口）
            } else if (lane.isEntry()) {//入口
//                cicm.receivePinRaise();//收发针上升（入口）
                pinControl.pinRaise();//收发针上升（入口）
                cicm.backBoltRaise();//后卡栓上升（入口）
                if (card == null) {//若之前一张卡尚未使用,此次不可从卡箱内取出通行卡
                    cicm.frontBoltLower();//前卡栓下降（入口）
                }
            }
            extJFrame.hideCentralPanel();
            String str;
            int m1, cpu;
            boolean firstFlag = true;
            long s1 = 0, s2 = 0;//自助发卡用于提示“请按键取卡”“请稍候”
            OUTER:
            while (true) {
                Thread.sleep(10);
                synchronized (violateObj) {
                    if (violationFlag) {//冲关车
                        return -2;
                    }
                }
                if (lane.isExit()) {//仅用于出口
                    if (veh.getWeight() == 0 || veh.getLimitWeight() == 0) {//重量信息变0,7型车回退到车型输入界面
                        if (veh.getVehicleClass() == 7) {
                            return 1;
                        }
                    }
                }
                if (!cpuFlag || !m1Flag) {//若ETC卡标识异常,则不必更改提示信息
                } else if (card == null) {
                    if (virtualEnt == null) {
                        extJFrame.updateFare(null);
                        extJFrame.updateEnt(null, null);
                        extJFrame.updateInfo("", "等待读取通行卡\n  将通行卡放在天线上");
                        extJFrame.hideCentralPanel();
                    } else {
                        String road = virtualEnt.substring(0, 2);
                        String station = virtualEnt.substring(2, 4);
                        String stationName = mainSvc.getStationName(road, station);
                        extJFrame.updateInfo("", "等待读取通行卡\n  将通行卡放在天线上\n当前虚拟入口站" + virtualEnt + "-" + stationName);
                        extJFrame.hideCentralPanel();
                    }
                    if (cartControl.isEmpty() && lane.isEntry()) {//入口卡箱已空
                        extJFrame.updateInfo("", "卡箱已空,请更换卡箱");
                        if (firstFlag) {//第一次触发,键盘报警
                            keyboard.sendAlert();
                            firstFlag = false;
                        }
                    } else if (cartControl.isFull() && lane.isExit()) {//出口卡箱已满
                        extJFrame.updateInfo("", "卡箱已满,请更换卡箱");
                        if (firstFlag) {//第一次触发,键盘报警
                            keyboard.sendAlert();
                            firstFlag = false;
                        }
                    }
                } else {
                    extJFrame.updateInfo("", "等待读取先前通行卡或ETC卡\n卡号:" + card.getCardSerial() + "\n  请把通行卡或ETC卡放在天线上");
                    extJFrame.hideCentralPanel();
                }
                if (!cartControl.isHasCart()) {
                    extJFrame.updateInfo("请装入卡箱", "请装入卡箱");
                    keyboard.sendAlert();
                    return 1;
                }
//                if (cartControl.isEmpty() && lane.isEntry()) {//入口卡箱已空
//                    extJFrame.updateInfo("", "卡箱已空,请更换卡箱");
//                    if (firstFlag) {//第一次触发,键盘报警
//                        keyboard.sendAlert();
//                        firstFlag = false;
//                    }
//                } else if (cartControl.isFull() && lane.isExit()) {//出口卡箱已满
//                    extJFrame.updateInfo("", "卡箱已满,请更换卡箱");
//                    if (firstFlag) {//第一次触发,键盘报警
//                        keyboard.sendAlert();
//                        firstFlag = false;
//                    }
//                }
                //***************************************自助发卡*********************************************//
                if (lane.isEntry() && amc.isActivated()) {//入口自助发卡启用
                    if (amc.isVehBackOff()) {//当前车辆倒车,取消车型,车牌信息
                        amc.setVehBack(false);
                        return 1;
                    }
                    boolean flag1 = cxp.checkArriveCoil();
                    boolean flag2 = amc.isKeyPressed();
                    if (flag1 && !flag2) {//到达线圈被激活但是自助卡机按键未被激活
                        long now = System.currentTimeMillis();
                        if (now < s1) {
                            s1 = now;
                        }
                        if (now - s1 > 10000) {//每10秒提示一次
                            amc.audioPressKey();//语音提示请按键取卡
                            s1 = System.currentTimeMillis();
                        }
                    } else if (flag1 && flag2) {//到达线圈被激活并且自助卡机按键被激活
                        long now = System.currentTimeMillis();
                        if (now < s2) {
                            s2 = now;
                        }
                        if (now - s2 > 10000) {//每10秒提示一次
                            amc.audioWait();//语音提示请稍候
                            s2 = System.currentTimeMillis();
                        }
                    }
                }

                //by zengzb add
                if (lane.isEntry() && isXasCICM()) {
                    if (null != avcControl && null != avcControl.getAvc()) {//当前车辆倒车,取消车型,车牌信息
                        if (avcControl.getAvc().isVehBackOff()) {
                            avcControl.getAvc().setVehBack(false);
                            return 1;
                        }
                    }
                }
                //***************************************自助发卡*********************************************//

                str = keyboard.getMessage();
                if (str == null) {
                    m1 = runM1Card();                                       //检测普通通行卡
                    if (m1 == -1) {
                        card1 = null;
                        return -1;
                    } else if (m1 == 0) {
                        exitTicketType = 1;//M1卡通行券类型1
                        entryTicketType = 1;
                        if (lane.isExit()) {                            //出口检测
                            card1 = mainSvc.check(card1, card2);
                        }
                        return runAfterReadCard(1); //进入读卡后流程
                    } else if (m1 == 1) {//无卡
                        card1 = null;
                    } else if (m1 == 2) {//有卡但不能使用
                        errCardSerial = card1.getCardSerial();
                        card1 = null;
                    }
                    cpu = runCpuCard();  //检测ETC卡
                    if (cpu == -1) {//异常
                        cpuCard1 = null;
                        return -1;
                    } else if (cpu == 0) {//交易成功
                        exitTicketType = 2;//ETC卡通行券为2
                        entryTicketType = 2;//ETC卡通行券为2
                        return runAfterReadCard(1);
                    } else if (cpu == 1) {//无卡
                        cpuCard1 = null;
                    } else if (cpu == 2) {//有卡但不能使用
                        if (lane.isEntry() && amc.isActivated()) {
                            tfiSvc.showTransFail();//自助发卡显示交易失败
                        }
                        errCardSerial = cpuCard1.getCardSerial();
                        cpuCard1 = null;
                    } else if (cpu == 3) {//直接放行
                        return 2;
                    }
                    if (cpu == 1 && m1 == 1) {
                        errCardSerial = null;
                    }
                } else if (Keyboard.CART.equals(str)) {
                    int i = 0;
                    if (lane.isEntry()) {
                        i = cartControl.run(2, 1);
                    } else {
                        i = cartControl.run(1, 1);
                    }
                    synchronized (violateObj) {
                        if (violationFlag) {//若在进行卡箱操作时出现违章操作,待卡箱操作结束,重新开始交易
                            return -2;
                        }
                    }
                    if (i == 5 || i == 6 || i == 7) {//无卡,残卡,不可读卡
                        card1 = cartControl.getCard();
                        card2 = new Card(card1);
                        card2.setLane(lane);
                        card2.setStaffId(staff.getStaffId());
                        card2.setVehicle(veh);
                        card2.setDhm(new Date(System.currentTimeMillis()));
                        if (veh != null) {
                            veh.setDate(card2.getDhm());
                        }
                        if (lane.isExit()) {                            //出口检测
                            card1 = mainSvc.check(card1, card2);
                            runGetFare();
                            int result = runConfirmNoCard();//收费员确认无卡,残卡,不可读卡操作
                            if (result == -2) {//冲关车
                                return -2;
                            } else if (result != 0) {//取消无卡,残卡,不可读卡操作
                                extJFrame.updateSpecialInfo(null, null);
                                FlowControl.cardObserCode = "0";
                                if (i == 5) {//无卡
                                    FlowControl.logout.setLostC(FlowControl.logout.getLostC() - 1);
                                } else if (i == 6) {//不可读卡
                                    FlowControl.logout.setCantReadTTC(FlowControl.logout.getCantReadTTC() - 1);//不可读卡-1
                                    FlowControl.logout.setHandInTTC(FlowControl.logout.getHandInTTC() - 1);//ic卡总数-1
                                    FlowControl.logout.setPressTTIDC(FlowControl.logout.getPressTTIDC() - 1);//键入通行卡号-1
                                } else if (i == 7) {//残卡
                                    FlowControl.logout.setHandInTTC(FlowControl.logout.getHandInTTC() - 1);
                                    FlowControl.logout.setIncompleteTTC(FlowControl.logout.getIncompleteTTC() - 1);
                                }
                                card1 = null;
                                card2 = null;
                                continue;
                            } else {//确认无卡,残卡,不可读卡操作
                                ls.addnoCardVehCnt();
                            }
                        }
                        if (i == 5 || i == 7) {//无卡，残卡
                            exitTicketType = 0;
                        } else if (i == 6) {//不可读卡
                            exitTicketType = 1;
                        }
                        return runAfterReadCard(1);
                    }
                    extJFrame.updateInfo("", "等待读取通行卡\n  将通行卡放在天线上");
                } else if (Keyboard.WEIGHT.equals(str)) {
                    if (lane.isEntry()) {//入口封闭计重功能
                        keyboard.sendAlert();
                    } else {
                        weighControl.runWeighControl();
                        extJFrame.updateInfo("", "等待读取通行卡\n  将通行卡放在天线上");
                    }
                } else if (Keyboard.CANCEL.equals(str)) {
                    LogControl.logInfo("取消等待通行卡");
                    //by zengzb add
                    if (lane.isEntry() && isXasCICM()) {
                        return 3;
                    } else if (lane.isEntry() && amc.isActivated()) {//自助发卡
                        return 3;
                    } else {
                        return 1;
                    }
                } else if ((Keyboard.RED).equals(str)) {//交易过程中可雨棚灯变红
                    runChangeCanopyRed();
                } else if (Keyboard.ZERO.equals(str)) {//【0】键,纸券功能
                    if (!FunctionControl.isPaperCardActive()) {//未启用纸券功能
                        keyboard.sendAlert();
                        continue;
                    }
                    LogControl.logInfo("选择纸券功能");
                    if (lane.isEntry()) {
                        boolean flag = PaperCardControl.getSingleInstance().runPaperCardControl(veh.getVehicleClass());//入口自动弹出纸券选择
                        if (flag) {
                            entryMop = "48";
                            entryTicketType = 4;
                            card1 = new Card();
                            card2 = new Card(card1);
                            card2.setLane(lane);
                            card2.setStaffId(staff.getStaffId());
                            card2.setVehicle(veh);
                            card2.setDhm(new Date(System.currentTimeMillis()));
                            if (veh != null) {
                                veh.setDate(card2.getDhm());
                            }
                            if (FunctionControl.isCollActive()) {//启用了代收功能
                                return runAfterReadCard(1);
                            } else {//未启用代收功能
                                return 4;
                            }
                        }
                    } else {
                        String station = PaperCardControl.getSingleInstance().runInputEntry();//输入入口信息
                        if (station == null) {//取消选择
                            continue;
                        } else {
                            exitTicketType = 4;//通行卡类型为纸券
                            trafficEnRoadid = station.substring(0, 2);
                            trafficEnStationid = station.substring(2, 4);
                            card1 = new Card();
                            card1.setRoadid(trafficEnRoadid);
                            card1.setStationid(trafficEnStationid);
                            card2 = new Card(card1);
                            card2.setLane(lane);
                            card2.setStaffId(staff.getStaffId());
                            card2.setVehicle(veh);
                            card2.setDhm(new Date(System.currentTimeMillis()));
                            return runAfterReadCard(1);
                        }
                    }
                } else {
                    keyboard.sendAlert();
                }
            }
        } catch (Exception ex) {
            LogControl.logInfo("等待读取通行卡异常", ex);
            return -1;
        }
    }

    /**
     * 检测是否有ETC通行卡,发现ETC通行卡核对卡与车辆信息,写卡,等待收费员将ETC通行卡从ETC卡读卡器上拿开
     *
     * @return 发现ETC通行卡并交易成功返回0；<br>
     * 无卡返回1；<br>
     * 有卡但不能使用(车型车牌差异等)返回2；<br>
     * 当前ETC卡直接放行3；<br>
     * 异常返回-1；<br>
     * 车辆违章返回-2
     */
    private int runCpuCard() {
        try {
            cpuCard1 = cpuCardSvc.getCpuCard();
            if (cpuCard1 != null) {//若ETC卡读卡器上有卡
                if (!cpuFlag && cpuCard1.getCardSerial().equals(errCardSerial)) {//当前读卡器上的卡已处理过
                    return 2;
                }
                
            } else {
                cpuFlag = true;
                return 1;
            }
            LogControl.logInfo("发现ETC卡：" + cpuCard1);
            sendInfo("ETC卡");
            int flag = 1;
            if (TestControl.isCardStatusIgnored()) {//卡状态判断标识,0表示当前不需要判断卡标识(做卡读写压力测试时使用)
                LogControl.logInfo("当前忽略对卡状态的判断");
            } else if (!cpuCardSvc.checkETCCardStatus1(lane, cpuCard1)) {//检测ETC卡内的车道状态,若已在出口读过,不可再次在出口读取
                if (lane.isExit()) {
                    if (FunctionControl.isEtcExitActive()) {//启用ETC卡出口检验放行
                        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "etcExitInterval", "10");
                        int interval = IntegerUtils.parseString(str);
                        boolean flag1 = checkEtcExit(cpuCard1, lane, interval);//判断出口是否入口站,如U型车、特殊情况
                        if (flag1) {
                            LogControl.logInfo("发现当前ETC卡上一次交易在本站,间隔时间也在指定范围" + interval + "分钟内");
                            flag1 = runEtcExitControl();
                            if (flag1) {
                                LogControl.logInfo("当前ETC车辆确认直接放行");
                                return 3;
                            }
                        }
                    }
                }
                LogControl.logInfo("ETC卡状态异常,出口已读卡");
                sendInfo("ETC卡状态异常,出口已读卡");
//                FlowControl.chargeObserCode = "4";
//                FlowControl.cardObserCode = "H";
                extJFrame.updateInfo("ETC卡状态异常(无入口信息)", "ETC卡状态异常(无入口信息)\n请出示有效ETC卡\n或通行卡");
                cpuFlag = false;
                return 2;
            }
            cpuFlag = cpuCardSvc.checkValidDate(cpuCard1);
            LogControl.logInfo("ETC卡有效时间" + cpuFlag + ",启用时间:" + cpuCard1.getStartDate() + " 到期时间:" + cpuCard1.getEndDate());
            if (!cpuFlag) {
//                FlowControl.chargeObserCode = "4";
                int temp = cpuCardSvc.checkValidDate1(cpuCard1);
                if (temp == -1) {
                    extJFrame.updateInfo("ETC卡未启用", "ETC卡未启用\n请出示有效ETC卡\n或通行卡");
                    LogControl.logInfo("ETC卡未启用");
                    sendInfo("ETC卡未启用");
                } else if (temp == 1) {
                    extJFrame.updateInfo("ETC卡已过期", "ETC卡已过期\n请出示有效ETC卡\n或通行卡");
                    LogControl.logInfo("ETC卡已过期");
                    sendInfo("ETC卡已过期");
                }
                return 2;
            }
            Card tempCard = new Card(cpuCard1);
            tempCard = new Card(cpuCard1);//重复？
            tempCard.setStatus(Constant.TRANSIT_CSC_STATUS_04);
            tempCard.setLane(lane);
            tempCard.setStaffId(staff.getStaffId());
            tempCard.setVehicle(veh);
            tempCard.setDhm(new Date(System.currentTimeMillis()));
            cpuFlag = cpuCardSvc.checkETCCard(cpuCard1, tempCard);//检测ETC卡状态以及车型车牌
            if (cpuFlag) {

                /**
                 * 出口读卡时,系统读取ETC卡以后如果发现发行方为501或0501,则弹窗提示“军警车”,
                 * 收费员点击确认按钮,后续车道软件按车道软件收费
                 * 规则：当收费员已经确认当前车种为军警车类型时,则不进行提示；需在ETC自身校验之后再进行提示 modify by yili
                 * 2016/05/30
                 *
                 */
                if (lane.isExit()) {
                    if (veh.getVehicleType() != Constant.MILITARY_VEHICLE) {//如果收费员已确认了军警车车型,就不必要弹出提示框了
                        String netNo = cpuCard1.getIssuer().substring(8, 12);//取4位  例：issuer=BAFEC4CF43010001
                        if (netNo != null) {
                            LogControl.logInfo("出口军车卡：" + netNo);
                            if (netNo.equals("0501")) {// 当ETC卡发行方为0501时,按军警车0元收费
                                // 弹出弹框
                                boolean confirmFlag = jobControl.militaryVehHint();
                                if (confirmFlag == true) {
                                    LogControl.logInfo("当前ETC卡为军车,确认军警车收费");
                                    runMilitaryVeh();
                                }
                            }
                        }
                    }
                }
                
                trafficEnRoadid = cpuCard1.getRoadid();
                trafficEnStationid = cpuCard1.getStationid();
                cpuCard2 = new CpuCard(cpuCard1);
                cpuCard2.setLane(lane);
                cpuCard2.setVehicle(veh);
                cpuCard2.setStaffId(staff.getStaffId());
                cpuCard2.setEnJobNum(staff.getShiftId() + "");
                cpuCard2.setDhm(new Date(System.currentTimeMillis()));
                if (veh != null) {
                    veh.setDate(cpuCard2.getDhm());
                }
                requestRealTimePath(cpuCard1, lane, veh, cpuCard2.getDhm());//请求实时路径信息 合理利用写卡时间（写卡前请求收费路径,写卡后获取实时路径）
                boolean flag1;
                if (TestControl.isCardWriteIgnored()) {//确认当前是否为测试模式
                    flag1 = true;
                } else {
                    CpuCard cc = new CpuCard(cpuCard2);
                    if (lane.isEntry() && virtualEnt != null) {//入口并且有虚拟入口站信息
                        cc.setRoadid(virtualEnt.substring(0, 2));
                        cc.setStationid(virtualEnt.substring(2, 4));
                    }
                    if (lane.isExit()) {
//                        generateTempETCInfo(cpuCard1.getInfo19(), ParseUtil.unParse19(cc));//出口记录0019临时文件
                        generateTempETCInfo(cpuCard1.getCardSerial(), cpuCard1.getInfo19());//出口记录0019临时文件
                    }
                    flag1 = cpuCardSvc.writeCpuCard(cc);//写卡
                }
                if (!flag1 //|| !cpuCard3.equals(cpuCard2)
                        ) {
//                    card2 = null;
//                    cpuCard1 = null;
                    cpuCard2 = null;
                    cpuCard3 = null;
                    LogControl.logInfo("ETC卡写卡失败");
                    keyboard.sendAlert();
                    sendInfo("ETC卡写卡失败");
                    extJFrame.updateInfo("写卡失败", "ETC卡写卡失败,请检查ETC卡是否放置到读卡器上\n或请出示有效ETC卡");
                    extJFrame.updateEnt(null, null);//除去入口信息
                    cpuFlag = false;
                    Thread.sleep(2000);
                    return 1;
                } else {
//                    tempCpuCard = null;
                    LogControl.logInfo("ETC卡写卡成功");
                    card2 = new Card(cpuCard1);
                    card2.setStatus(Constant.TRANSIT_CSC_STATUS_04);
                    card2.setLane(lane);
                    card2.setStaffId(staff.getStaffId());
                    card2.setVehicle(veh);
                    card2.setDhm(cpuCard2.getDhm());
                }
                long start = System.currentTimeMillis();
                while (true) {
                    Thread.sleep(50);
                    cpuCard3 = cpuCardSvc.getCpuCard();
                    if (cpuCard3 == null) {
                        extJFrame.updateInfo("", "请将ETC卡放置在读卡器上");
                    } else {
                        if (lane.isExit()) {
//                            generateTempETCInfo(cpuCard1.getInfo19(), cpuCard3.getInfo19());//出口记录0019临时文件
//                            generateTempETCInfo(cpuCard1.getCardSerial(), cpuCard1.getInfo19());//出口记录0019临时文件
                        }
                        break;
                    }
                    long now = System.currentTimeMillis();
                    if (now - start > 60 * 1000) {
                        LogControl.logInfo("ETC卡写卡成功后1分钟内无法读到卡，退出等待");
                        return 1;
                    }
                }
                if (card1 == null) {//此处判断card1是否为null是为了已发通行卡但司机又出示ETC卡的情况下,原通行卡信息不能被覆盖
                    card1 = new Card(cpuCard1);
                }
                if (lane.isEntry()) {
                    entryMop = "51";//入口刷ETC卡付款方式为51,出口需要等到付款时才能确认付款方式
                    logout.setMop2count(logout.getMop2count() + 1);
                }
                ls.addXtCardUseCnt();//工班表ETC卡总数增加
                exitTicketType = 2;//ETC卡通行券为2
                entryTicketType = 2;//ETC卡通行券为2
                generateTempTransInfo();//产生临时交易信息
                generateTempLogoutInfo();//产生临时下班信息
                generateTempTransImg();//产生临时交易图片
                LogControl.logInfo("ETC卡读写成功：" + cpuCard3);
                getRealTimePath();//获取实时路径信息,为下一步检测超时车辆做准备 合理利用写卡时间（写卡前请求收费路径,写卡后获取实时路径）
                mainSvc.checkStation(cpuCard1);
                cpuCard1 = cpuCardSvc.checkUVehAndTripTime(cpuCard1, card2);//检测U型车以及超时
                if (lane.isExit()) {
                    card1 = new Card(cpuCard1);
                }
                if (lane.isEntry()) {
                    extJFrame.updateInfo("", "卡读写成功,请将卡拿离天线\n卡号：" + cpuCard2.getCardSerial() + "\n然后确认");
                    if (runWhileVehLeave) {
                        extJFrame.updateInfo("", "卡读写成功,请将卡拿离天线\n卡号：" + cpuCard2.getCardSerial() + "\n\n等待车辆离开");
                    }
                }
                
                flag = 0;
            } else {
                
                cpuFlag = false;
                return 2;
            }
            return flag;
        } catch (Exception ex) {
            LogControl.logInfo("读取ETC卡异常", ex);
            return -1;
        }
    }

    /**
     * 检测是否有通行卡,发现通行卡核对卡与车辆信息,写卡,等待收费员将通行卡从天线上拿开
     *
     * @return 发现通行卡并交易成功返回0 <br>
     * 无卡返回1 <br>
     * 有卡不能用返回2<br>
     * 其他异常返回-1 <br>
     * 车辆违章返回-2
     */
    private int runM1Card() {
        try {
//            Card tempCard = mireader.getReadedCard();
            Card tempCard = m1Control.getReadedCard();
            if (tempCard == null) {
                m1Flag = true;
                return 1;
            } else {
                card1 = new Card(tempCard);
                if (card1.getType() == null) {//对卡类型进行空值判断
                    m1Flag = true;
                    return 1;
                }
                if (!Constant.CARD_TYPE_01.equals(card1.getType())) {//非通行卡
                    m1Flag = false;
                    extJFrame.updateInfo("卡类型错误", "卡类型错误\n请出示通行卡");
                    //*******************************自助发卡**********************************//
                    if (lane.isEntry() && amc.isActivated()) {
                        amc.runBadCard();
                    }
                    //*******************************自助发卡**********************************//
                    return 2;
                } else if (card1.getCardSerial().equals(errCardSerial)) {//已处理异常卡,不再处理
                    keyboard.sendAlert();
                    return 2;
                } else {
                    if (lane.isExit() && FunctionControl.isCscEncryptActive()) {
                        int i = checkEncrypteMsg(card1);
                        if (i == -1) {
                            LogControl.logInfo("卡内容加密错误：" + card1);
                            boolean flag = runEncryptError();
                            if (!flag) {//禁止使用
                                return 2;
                            }
                        } else if (i == 1) {
                            LogControl.logInfo("卡内容未加密：" + card1);
                            boolean flag = runUnEncryptCard();
                            if (!flag) {//禁止使用
                                return 2;
                            }
                        }
//                        Card tempCard1 = mireader.getReadedCard();
                        Card tempCard1 = m1Control.getReadedCard();
                        if (tempCard1 == null || tempCard1.getCardSerial() == null || !tempCard1.getCardSerial().equals(tempCard.getCardSerial())) {
                            return 1;
                        }
                    }
                }
            }
            LogControl.logInfo("读取通行卡:" + card1);
            sendInfo("读取通行卡");
            if (lane.isExit()) {//出口
                cicm.backBoltRaise();                                           //后卡栓上升(出口)
            } else if (lane.isEntry()) {//入口
                if (card != null) {
                    boolean flag = card.getCardSerial().equals(card1.getCardSerial());
                    if (!flag) {
                        keyboard.sendAlert();
                        return 1;
                    }
                } else {
                    String cartId = card1.getCartId();//卡箱标签卡号码
                    String cycleNum = card1.getCartCycleNum();//卡箱循环次数
                    String orderNum = card1.getCscNum();//通行卡顺序号
                    if (cartControl.checkCscCard(cartId, cycleNum, orderNum)) {//当前卡在当前卡箱内正常
                        cartControl.updateCartNum(-1);//卡箱内卡数量减1,界面更新卡数量
                    } else {
                        LogControl.logInfo("散卡：" + card1);
                        cartControl.increaseExtraCardCnt();//额外卡数量加1
                        //记录下班临时卡箱流动单
                        cartControl.generateTempCadboxList(new Date(), CartOpType.SEND, null, staff.getStaffId(), 0);
                    }
                    
                }
//                cicm.frontBoltRaise();                                          //前卡栓上升(入口)
                int flag = checkEntryCardStatus();                              //检测入口通行卡状态
                if (flag == 1) {//入口卡状态异常,用户拿开坏卡
                    return 2;
                }
            }
            trafficEnRoadid = card1.getRoadid();
            trafficEnStationid = card1.getStationid();
            
            card2 = new Card(card1);
            if (lane.isExit()) {
                card2.setStatus(Constant.TRANSIT_CSC_STATUS_04);//出口应将卡状态改为卡箱内
            } else if (lane.isEntry()) {
                card2.setStatus(Constant.TRANSIT_CSC_STATUS_01);//入口应将卡状态改为已发放
            }
            card2.setLane(lane);
            card2.setStaffId(staff.getStaffId());
            card2.setVehicle(veh);
            if (lane.isExit()) {//出口才需要改变卡内记录的卡箱号,卡箱循环号以及卡顺序号,入口不需要做改变
                CartInfo cartInfo = cartControl.getCartInfo();
                card2.setCartId(cartInfo.getCartId());
                card2.setCartCycleNum(cartInfo.getCartCycleNum());
                card2.setCscNum(IntegerUtils.parseString(cartInfo.getCardOrderNum()) + 1 + "");//出口卡顺序号加1
            }
            card2.setDhm(new Date(System.currentTimeMillis()));
            card2.setTransNum(recordId);
            if (veh != null) {
                veh.setDate(card2.getDhm());
            }
            requestRealTimePath(card1, lane, veh, card2.getDhm());//发送实时路径请求信息 合理利用写卡时间（写卡前请求收费路径,写卡后获取实时路径）
//            requestPathDetail(card1, lane, veh, card2.getDhm());//请求路径明细
            int flag;
            if (TestControl.isCardWriteIgnored()) {////确认当前是否为测试模式
                flag = 0;
            } else {
                String tempNum = card2.getWritingNum();
                int num = IntegerUtils.parseString(tempNum) + 1;
                Card tempCard2 = new Card(card2);
                tempCard2.setWritingNum(StringUtils.toLength(num, 6));//写卡次数+1
                if (lane.isEntry() && virtualEnt != null) {//入口站有虚拟入口信息
                    tempCard2.setRoadid(virtualEnt.substring(0, 2));
                    tempCard2.setStationid(virtualEnt.substring(2, 4));
                } else if (lane.isExit()) {
                    //出口车道需要抹掉全车牌及车牌颜色信息
                    //（旧版本GEA软件不支持全车牌写入,此处清空全车牌信息是为了避免卡流通到旧版本车道软件上时,残留全车牌信息到了出口导致车牌差异）
                    tempCard2.setFullVehPlateNum("");
                    tempCard2.setPlateColor("");
                }
                LogControl.logInfo("写卡：" + tempCard2.toString());
                if (lane.isEntry()) {//入口需要对卡内信息进行加密
//                    flag = mireader.write(tempCard2, true, true, true, true);
                    flag = m1Control.write(tempCard2, true, true, true, true);
                } else {//出口不对卡内信息进行加密
//                    flag = mireader.write(tempCard2, true, true, true, false);
                    flag = m1Control.write(tempCard2, true, true, true, false);
                }
                getRealTimePath();//获取实时路径信息,为后续检测超时车辆做准备 合理利用写卡时间（写卡前请求收费路径,写卡后获取实时路径）
            }
            if (flag == 1 && lane.isEntry()) {//入口写卡时发现当前无卡，退出写卡流程
                return 2;
            }
            if (flag != 0) {                            //写卡失败
                runManualAutoMode();
                LogControl.logInfo("通行卡写卡失败");
                String str;
                while (lane.isEntry()) {//入口写卡失败,需将坏卡拿出,出口写卡失败不影响收费流程,暂不处理
                    Thread.sleep(10);
//                    if (mireader.getReadedCard() == null) {
//                    if (m1Control.getReadedCard() == null) {
//                        return 2;
//                    }
                    extJFrame.updateInfo("写卡失败", "此为坏卡,请按【卡机】键并选择拿开坏卡将卡取出");
                    //*******************************自助发卡**********************************//
                    if (lane.isEntry() && amc.isActivated()) {
                        amc.runBadCard();
                        return 2;//自助取卡,不需要按卡机拿开坏卡操作
                    }
                    //*******************************自助发卡**********************************//
                    str = keyboard.getMessage();
                    if (str == null) {
                        
                    } else if ("卡机".equals(str)) {
                        int i = cartControl.run(2, 1);//进入卡机菜单操作流程
                        if (i != 0 && i != -1) {
                            m1Flag = true;
                            return 2;
                        }
                    } else {
                        keyboard.sendAlert();
                    }
                    Thread.sleep(100);
                }
            } else {//写卡成功
                LogControl.logInfo("通行卡写卡成功");
                sendInfo("通行卡写卡成功");
            }
            entryMop = lane.isEntry() ? "48" : "00";//入口付款方式普通为48
            if (lane.isExit()) {
                cicm.lowerOneStep();//收发针下降一格（出口）
                cicm.frontBoltLower();//前卡栓下降（出口）,等待收费员将卡滑入卡箱
                cartControl.updateCartNum(1);//出口卡箱内卡数量加1
            } else {
                extJFrame.updateInfo("", "卡读写成功,请将卡拿离天线\n卡号：" + card1.getCardSerial() + "\n然后确认");
                cicm.frontBoltRaise();//前卡栓上升(入口)
                cicm.backBoltLower();//后卡栓下降（入口）,等待收费员将卡取出卡箱
            }
            mainSvc.checkStation(card1);
            if (lane.isEntry()) {
                logout.setMop3count(logout.getMop3count() + 1);
            }
            logout.setTtC(logout.getTtC() + 1);//下班表IC卡总数增加
            ls.addPassCardUseCnt();//工班表IC卡总数增加
            generateTempTransInfo();//产生临时交易信息
            generateTempLogoutInfo();//产生临时下班信息
            generateTempTransImg();//记录临时交易图片
            return 0;
        } catch (Exception ex) {
            LogControl.logInfo("读取通行卡异常", ex);
            return -1;
        }
    }

    /**
     * 检测入口通行卡状态
     *
     * @return 入口通行卡状态正常返回0<br>
     * 否则返回1 <br>
     * 异常返回-1 <br>
     * 车辆违章返回-2
     */
    private int checkEntryCardStatus() {
        try {
            if (card1 == null) {
                return 1;
            }
            boolean flag = mainSvc.checkCardStatus(card1.getStatus(), lane.getLaneType());
            if (!flag) {     //入口读到卡状态为已发卡,状态异常
                if (card == null || !card1.getCardSerial().equals(card.getCardSerial())) {//排除入口已发通行卡但司机出示ETC通行卡的情况
                    sendInfo("卡状态异常");
                    LogControl.logInfo("入口发卡发现卡状态异常");
                    runManualAutoMode();
                    String str;
                    while (true) {
                        Thread.sleep(10);
                        extJFrame.updateInfo("卡状态异常", "请按【卡机】键并选择“拿出坏卡”");
                        //*****************************************自助发卡************************************************//
                        if (lane.isEntry() && amc.isActivated()) {
                            cicm.backBoltLower();
                            cicm.frontBoltLower();
                            amc.runBadCard();
                            return 1;//自助取卡,不需要按卡机拿开坏卡操作
                        }
                        //*****************************************自助发卡************************************************//
//                        if (mireader.getReadedCard() != null) {
//                            extJFrame.updateInfo("卡状态异常", "请按【卡机】键并选择“拿出坏卡”");
//                        } else {//异常卡已拿开
//                            return 1;
//                        }
                        str = keyboard.getMessage();
                        if (str == null) {
                        } else if (str.equals("卡机")) {
                            int i = cartControl.run(2, 1);
                            if (i == 0) {//用户取消操作
                                continue;
                            } else if (i != -1) {
                                return 1;
                            }
                        } else {
                            keyboard.sendAlert();
                        }
                        Thread.sleep(100);
                    }
                } else {
                    LogControl.logInfo("检测到已发通行卡再刷ETC卡后留下来的通行卡");
                    return 0;
                }
            } else {
                return 0;
            }
        } catch (Exception ex) {
            LogControl.logInfo("检测入口通行卡状态异常", ex);
            return -1;
        }
    }

    /**
     * 读取通行卡（ETC卡）之后一直到读卡流程结束
     *
     * @param collParam 代收标志 0无代收流程,1有代收流程
     * @return 成功返回0<br>
     * 异常返回-1<br>
     * 车辆违章返回-2
     *
     */
    private int runAfterReadCard(int collParam) {
        try {
            checkVirtualStation();//检测最远站
            generateTempTransInfo();
            generateTempLogoutInfo();
            generateTempTransImg();
            LogControl.logInfo("进入读卡后流程");
            if (lane.isExit()) {
                runGetFare();
            }
            if (collParam == 1 && collectFlag && cpuCard1 == null) {//当前要求代收并且非ETC卡
                collectFee = getCollectMoney();
            }
            orderPay = 0.0;
            if (lane.isExit()) {                                                //出口流程
                runGetFare();
                extJFrame.updateInfo("等待确认入口信息", "按【确认】键以确认入口数据\n\n" + path);
                String str;
                double weight = veh.getWeight();
                double limitWeight = veh.getLimitWeight();
                while (true) {
                    Thread.sleep(10);
                    synchronized (violateObj) {
                        if (violationFlag) {//冲关车
                            return -2;
                        }
                    }
                    if (weight != veh.getWeight() || limitWeight != veh.getLimitWeight()) {//车辆重量信息发生变化,重新计算收费额
                        if (veh.getVehicleClass() >= 7) {//当前车辆必须为货车
                            LogControl.logInfo("计重信息发生变化,重新计算收费额,变化前重量及限重：" + weight + " " + limitWeight);
                            weight = veh.getWeight();
                            limitWeight = veh.getLimitWeight();
                            LogControl.logInfo("计重信息发生变化,重新计算收费额,变化后重量及限重：" + weight + " " + limitWeight);
                            runGetFare();//车辆重量信息发生变化,重新计算收费额
                            //费显显示总轴重及限重
                            tfi.showTotalWeightAndLimitWeight(veh.getFareWeight(), veh.getLimitWeight());
                        }
                    }
                    str = keyboard.getMessage();
                    if (str == null) {
                        continue;
                    } else if (str.equals(Keyboard.CONFIRM)) {                            //收费员确认入口信息
                        if (TestControl.isCardStatusIgnored()) {//是否忽略卡状态
                            LogControl.logInfo("当前忽略确认入口信息时卡是否在天线上的判断");
                            break;
                        }
//                        if (mireader.getReadedCard() != null) {                       //卡还在读卡器上时不能确认入口信息
                        if (m1Control.getReadedCard() != null) {                       //卡还在读卡器上时不能确认入口信息
                            extJFrame.updateInfo("", "卡在读卡器上时不能确认入口信息,请先将卡放入卡箱");
                            if (lane.isExit()) {//确保前卡栓下降，卡能够放入卡箱
                                cicm.frontBoltLower();
                            }
                            continue;
                        } else {
                            cicm.frontBoltRaise();
                            LogControl.logInfo("收费员确认入口信息：" + card1.getRoadid() + card1.getStationid());
                            vi.showEntry(card1.getRoadid() + card1.getStationid());//字符叠加显示入口
                            break;
                        }
                    } else if (str.equals(Keyboard.SIMU)) {//按【模拟】键
                        char temp = '0';
                        if (collectFlag && cpuCard1 == null) {
                            temp = simControl.runSimulate(6);
                        } else {
                            temp = simControl.runSimulate(1);
                        }
                        switch (temp) {
                            case '0'://取消操作
                                extJFrame.updateInfo("等待确认入口信息", "按【确认】键以确认入口数据\n\n" + path);
                                extJFrame.showCardAndWeight();
                                break;
                            case '2'://更改车型车种
                                return runModifyVehClassOrType();
                            case '5'://更改代收
                                return runAfterReadCard(1);
                        }
                    } else if (str.equals(Keyboard.IMG)) {//【图片】键
                        LogControl.logInfo("查询入口图片");
//                        mainSvc.runShowEntryInfo(card1);                            //出口调入口图片
                        runShowPathInfo(card1);
                        keyboard.getMessage();//清空按键信息,防止等待期间重复按图片键重复调用图像
                    } else if (str.equals(Keyboard.UP) || str.equals(Keyboard.DOWN)) {//【上】【下】键
                        mainSvc.updateEntryPhoto();
                    } else if (str.equals(Keyboard.WEIGHT)) {//【计重】键
                        if (lane.isEntry()) {//入口封闭计重功能
                            keyboard.sendAlert();
                        } else {
                            weighControl.runWeighControl();
                            return runAfterReadCard(0);
                        }
                    } else if (str.equals(Keyboard.RED)) {//【红灯】键
                        runChangeCanopyRed();
                    } else {
                        keyboard.sendAlert();
                    }
                }
                runAudioFare();//语音报价
                extJFrame.showInputFare();                                          //显示收费员输入收费金额界面
            } else if (lane.isEntry()) {                                   //入口流程
                if (collectFlag && cpuCard1 == null) {//要求代收并且当前交易非ETC卡交易
                    runAudioFare();//语音报价
                    runGetEnCollectFee();//收取代收通行费
//                    runPrintCollFee();//打印票据
                }
                runPrint();//打印票据
                if (entryTicketType != 4) {//纸券模式不显示
                    extJFrame.updateInfo("", "卡读写成功,请将卡拿离天线\n卡号：" + card1.getCardSerial() + "\n然后确认");
                } else {//纸券模式不需要收费员将卡拿开后确认这一步操作
                    return 0;
                }
                if (amc.isActivated()) {
                    tfiSvc.showTransDone();//自助发卡显示交易成功
                }
                switch (entryMop) {
                    case "48":
                        String str;
                        while (true) {
                            Thread.sleep(10);
                            synchronized (violateObj) {
                                if (violationFlag) {//冲关车
                                    return -2;
                                }
                            }
                            str = keyboard.getMessage();
                            //*********************************自助发卡******************************************//
                            if (lane.isEntry() && amc.isActivated()) {//自助发卡机模式自动确认
                                amc.sendCard();//自助卡机发卡
                                if (amc.isKeyPressed()) {
                                    amc.setKeyPressed(false);
                                }
                                long start = System.currentTimeMillis();
                                while (true) {
                                    Thread.sleep(10);
                                    if (amc.isCardTaken()) {//卡已发出并且卡被取走
                                        LogControl.logInfo("自助发卡,卡已被取走");
                                        break;
                                    }
                                    if (amc.isVehBackOff()) {//当前车辆倒车报警
                                        amc.setVehBack(false);
                                        LogControl.logInfo("自助发卡,卡已发出,车辆倒车");
                                        warning = true;
                                    }
                                    str = keyboard.getMessage();
                                    if (Keyboard.CONFIRM.equals(str)) {//收费键盘按键确认
                                        break;
                                    }
                                    Thread.sleep(100);
                                    long now = System.currentTimeMillis();
                                    if (now < start) {//当前时间小于开始时间,重新开始计时
                                        start = now;
                                    }
                                    if (now - start > 5000 && !amc.isCardOut()) {//超过5秒钟仍未出卡,语音提示按键
                                        LogControl.logInfo("发送出卡指令5秒后卡仍未发出,重新播放按键提示");
                                        amc.audioPressKey();
                                        start = System.currentTimeMillis();
                                    }
                                    if (amc.isKeyPressed() && !amc.isCardOut()) {//司机按键并且当前未出卡
                                        amc.sendCard();
                                        amc.setKeyPressed(false);
                                    }
                                }
                                str = Keyboard.CONFIRM;
                            }
                            //*********************************自助发卡******************************************//
                            if (str == null) {
                                if ("48".equals(entryMop) && !amc.isActivated() && entryTicketType == 1 && FunctionControl.isCscToEtcActive()) {//设置entryMop条件,避免反复读取ETC卡;自助发卡模式已发通行卡不可再刷湘通卡
                                    int temp = runCpuCard();                            //已发通行卡,但未按【确认】键,此时司机出示ETC卡
                                    if (temp == 0) {
                                        card = card1;
                                        ls.decreasePassCardUseCnt();//工班表减少通行卡使用数量
                                        logout.setTtC(logout.getTtC() - 1);//下班表减少通行卡使用数量
                                        logout.setMop3count(logout.getMop3count() - 1);//下班表减少通行卡使用数量
                                        LogControl.logInfo("已发通行卡" + card.getCardSerial() + ",司机出示ETC卡" + cpuCard1.getCardSerial());
                                        entryTicketType = 2;
                                    } else if (temp == 2) {
                                        cpuCard1 = null;
                                        cpuCard2 = null;
                                        cpuCard3 = null;
                                        extJFrame.updateInfo("ETC卡禁止使用", "ETC卡禁止使用,请将ETC卡拿开然后确认");
                                    }
                                }
                            } else if (str.equals(Keyboard.CONFIRM)) {
//                                if (mireader.getReadedCard() != null && !amc.isActivated()) {//卡还在读卡器上时不能确认入口信息,自助发卡车道不做此判断
                                if (m1Control.getReadedCard() != null && !amc.isActivated()) {//卡还在读卡器上时不能确认入口信息,自助发卡车道不做此判断
                                    if (TestControl.isCardStatusIgnored()) {//忽略卡状态
                                        LogControl.logInfo("当前忽略卡是否在天线上的判断");
                                        if (entryMop.equals("48") && card != null && entryTicketType == 1) {//若当前通行卡为之前已发通行卡司机又出示ETC卡之后遗留下来的通行卡,写卡后将card卡内容清空
                                            card = null;
                                        }
                                        break;
                                    } else {
                                        LogControl.logInfo("卡在天线上时不能确认,请先将卡拿离天线,然后确认");
                                        extJFrame.updateInfo("", "卡在天线上时不能确认,请先将卡拿离天线,然后确认");
                                        continue;
                                    }
                                } else {
                                    if (entryMop.equals("48") && card != null && entryTicketType == 1) {//若当前通行卡为之前已发通行卡司机又出示ETC卡之后遗留下来的通行卡,写卡后将card卡内容清空
                                        card = null;
                                    }
                                    break;
                                }
                            } else if (str.equals(Keyboard.RED)) {//收费过程中雨棚灯变红
                                runChangeCanopyRed();
                            } else if (Keyboard.SIMU.equals(str)) {
                                int tempFlag = 0;
//                            if ("01".equals(exitMop)) {//现金付款时才有重打印发票功能
                                if (("01".equals(exitMop) && pay > 0) || (collectFlag && collectFee > 0)) {
                                    tempFlag = simControl.runSimulate(5);
                                } else {//非现金付款无重打印发票功能
                                    keyboard.sendAlert();
                                }
                                switch (tempFlag) {
                                    case '0'://取消操作
                                        break;
                                    case '4':
//                                    runPrint();//重打印发票
                                        runPrint();//打印票据
                                        logout.setReceiptMore(logout.getReceiptMore() + 1);//重打票次数加1
                                        FlowControl.chargeObserCode = "7";
                                        LogControl.logInfo("重打印发票");
                                        ls.addBadInVoiceCnt();//废票数加1
                                        break;
                                }
                                extJFrame.updateInfo("", "卡读写成功,请将卡拿离天线\n卡号：" + card1.getCardSerial() + "\n然后确认");
//                                extJFrame.updateInfo("接受付款", "接受付款");
                            } else {
                                keyboard.sendAlert();
                            }
                        }
                        break;
                    case "51":
                        while (true) {
                            Thread.sleep(10);
                            synchronized (violateObj) {
                                if (violationFlag) {
                                    return -2;
                                }
                            }
                            str = keyboard.getMessage();
                            if (str == null) {
                                continue;
                            } else if (Keyboard.CONFIRM.equals(str)) {                        //等待付款确认(出口)
                                break;
                            }
                        }
                        break;
                }
                cicm.backBoltRaise();                                              //后卡栓上升
            }
            return 0;
        } catch (Exception ex) {
            LogControl.logInfo("读卡后流程异常", ex);
            return -1;
        }
    }

    /**
     * 出口读卡后的流程,界面显示收费信息直至收费员确认收费信息
     *
     * @return 成功返回0<br>
     * 异常返回-1 <br>
     * 车辆违章返回-2
     */
    private int runChargeFare() {
        try {
            mainSvc.stopRequestEntryPhoto();//停止请求入口图片
            generateTempTransInfo();
            generateTempLogoutInfo();
            generateTempTransImg();
            extJFrame.updateFare(pay, collectFee);
            extJFrame.initInputFare();
            extJFrame.showInputFare();
            sendInfo("未付款");
            String msg = "现金付款\n-实收金额若等于车费,直接按【确认】键\n-否则输入实收金额,再按【确认】键\n其他付款方式\n-选择使用";
            if (FunctionControl.isFareInputFunActive()) {
                msg = "现金付款\n-请输入实收金额，再按【确认】键\n其他付款方式\n-选择使用";
            }
            extJFrame.updateInfo("付款方式？", msg);
            StringBuilder sb = new StringBuilder();
            //按【确认】键之前
            String total = "";//支付金额
            String change = "";//找零金额
            String str;
            CpuCard tempCard;
            String ignoreCardSer = null;//付款用ETC卡异常卡号,对于异常卡,仅处理一次
            double weight = veh.getWeight();
            double limitWeight = veh.getLimitWeight();
            ServiceCard svcCard;
            while (true) {
                Thread.sleep(10);
                synchronized (violateObj) {
                    if (violationFlag) {//冲关车
                        return -2;
                    }
                }
                svcCard = runServiceCard();
                if (svcCard != null && svcCard.getCardSerial() != null) {
                    LogControl.logInfo("使用公务卡" + svcCard.getId() + "付款");
                    serviceCardSerial = svcCard.getCardSerial();
                    exitMop = "39";
                    pay = 0;
                    collectFee = 0;
                    veh.setVehicleType(Constant.SERVICE_VEH);
                    break;
                }
                if (weight != veh.getWeight() || limitWeight != veh.getLimitWeight()) {//车辆重量信息发生变化,重新计算收费额
                    if (veh.getVehicleClass() >= 7) {//当前车辆必须为货车
                        LogControl.logInfo("计重信息发生变化,重新计算收费额,变化前重量及限重：" + weight + " " + limitWeight);
                        weight = veh.getWeight();
                        limitWeight = veh.getLimitWeight();
                        LogControl.logInfo("计重信息发生变化,重新计算收费额,变化后重量及限重：" + weight + " " + limitWeight);
                        runGetFare();//车辆重量信息发生变化,重新计算收费额
                        //费显显示总轴重及限重
                        tfi.showTotalWeightAndLimitWeight(veh.getFareWeight(), veh.getLimitWeight());
                        sb = new StringBuilder();
                        extJFrame.initInputFare();
                        extJFrame.showInputFare();
                    }
                }
                str = keyboard.getMessage();
                tempCard = cpuCardSvc.getCpuCard();
                if (tempCard != null) {
                    if (tempCard.getCardSerial().equals(ignoreCardSer)) {//异常卡,不做二次处理
                        continue;
                    }
                    extJFrame.hideCentralPanel();//检测到ETC卡付款时,隐藏输入金额界面
                    if (cpuCard3 == null) {
                        ignoreCardSer = tempCard.getCardSerial();
//                        FlowControl.chargeObserCode = "4";
                        keyboard.sendAlert();
                        extJFrame.updateInfo("拒绝ETC卡付款", "当前ETC卡并未作为通行卡使用,不可用于付款\n请出示有效ETC卡或选择其他付款方式");//ETC卡作为通行卡使用时才可以用ETC卡付款
                        continue;
                    } else if (!cpuCard3.getCardSerial().equals(tempCard.getCardSerial())) {
                        ignoreCardSer = tempCard.getCardSerial();
//                        FlowControl.chargeObserCode = "4";
                        keyboard.sendAlert();
                        extJFrame.updateInfo("拒绝ETC卡付款", "当前ETC卡并未作为通行卡使用,不可用于付款\n请出示有效ETC卡或选择其他付款方式");//ETC卡作为通行卡使用时才可以用ETC卡付款
                        continue;
                    }
                    int vehClass = veh.getVehicleClass();//根据收费员输入车型判断折扣率
                    int userType = IntegerUtils.parseString(cpuCard3.getUserType());
                    String issuer = cpuCard3.getIssuer();
                    double tempFareWeight = veh.getFareWeight();
                    double tempLimitWeight = veh.getLimitWeight();
                    String cardType = cpuCard3.getCardType();
                    etcDis = cpuCardSvc.getETCDiscount(userType, vehClass, issuer, cardType, tempFareWeight, tempLimitWeight);
//                    etcDis = cpuCardSvc.getETCDiscount(userType, vehClass, issuer);//获取ETC卡折扣率
                    LogControl.logInfo("ETC卡折扣率：" + etcDis);
                    double cash = DoubleUtils.mul(pay, etcDis);//按折扣进行扣费
                    LogControl.logInfo("ETC卡余额:" + cpuCard3.getBalance());
                    if (cpuCard3.getBalance() < cash) {
                        ignoreCardSer = tempCard.getCardSerial();
                        FlowControl.chargeObserCode = "3";
                        keyboard.sendAlert();
                        LogControl.logInfo("ETC卡余额:" + cpuCard3.getBalance() + ",余额不足");
                        sendInfo("ETC卡余额不足");
                        extJFrame.updateInfo("ETC卡余额不足", "ETC卡余额不足,请出示有效ETC卡或选择其他付款方式");
                        cpuCardSvc.waitForNoCpuCard();
                        keyboard.getMessage();//清空按键结果,避免此时因为多按【确认】键导致
                        continue;
                    }
                    extJFrame.updateAttachInfo("折后金额:" + cash);
//                    extJFrame.updateInfo("付款确认", "按【确认】键确认使用ETC卡付款\n拿开ETC卡,按【取消】键选择其他付款方式");
                    String serviceCardSerial;//公务卡卡序列号
                    while (true) {
                        Thread.sleep(10);
                        synchronized (violateObj) {
                            if (violationFlag) {
                                return -2;
                            }
                        }
                        extJFrame.updateInfo("付款确认", "按【确认】键确认使用ETC卡付款\n拿开ETC卡,按【取消】键选择其他付款方式");
                        if (weight != veh.getWeight() || limitWeight != veh.getLimitWeight()) {//车辆重量信息发生变化,重新计算收费额
                            if (veh.getVehicleClass() >= 7) {//当前车辆必须为货车
                                LogControl.logInfo("计重信息发生变化,重新计算收费额,变化前重量及限重：" + weight + " " + limitWeight);
                                weight = veh.getWeight();
                                limitWeight = veh.getLimitWeight();
                                LogControl.logInfo("计重信息发生变化,重新计算收费额,变化后重量及限重：" + weight + " " + limitWeight);
                                runGetFare();//车辆重量信息发生变化,重新计算收费额
                                //费显显示总轴重及限重
                                tfi.showTotalWeightAndLimitWeight(veh.getFareWeight(), veh.getLimitWeight());
                                return runChargeFare();
//                                sb = new StringBuilder();
//                                extJFrame.initInputFare();
//                                extJFrame.showInputFare();
                            }
                        }
                        str = keyboard.getMessage();
                        if (str == null) {
                            continue;
                        } else if (Keyboard.CONFIRM.equals(str)) {
                            if (veh.isTruck() && veh.getWeight() == 0) {//当前为货车但无计重信息（倒车）
                                keyboard.sendAlert();
                                extJFrame.updateInfo("无车辆重量信息", "等待车辆到达");
                                Thread.sleep(2000);
                                continue;
                            }
                            break;
                        } else if (Keyboard.CANCEL.equals(str)) {
                            return runChargeFare();
                        } else {
                            keyboard.sendAlert();
                        }
                    }
                    int money = (int) DoubleUtils.mul(cash, 100);                   //ETC卡扣款以分为单位
                    cpuCard2.setMoney(money);
                    boolean flag;
                    if (TestControl.isCardWriteIgnored()) {//忽略写卡
                        flag = true;
                    } else {//运营模式
                        double balance1 = cpuCard3.getBalance();//付款前余额
                        double balance2 = tempCard.getBalance();//当前余额
                        if (cash != 0 && balance2 <= DoubleUtils.sub(balance1, cash)) {//兼容代收通行费时毅丰动态库自动增加代收费用的问题
                            LogControl.logInfo("付款前余额：" + balance1 + ",当前余额：" + balance2 + ",应付通行费：" + cash + ",ETC卡扣费时发现当前ETC卡已经扣费，不再进行扣费");
                            extJFrame.updateInfo("", "ETC卡扣费时发现当前ETC卡已经扣费，不再进行扣费");
                            Thread.sleep(3000);
//                            JOptionPane.showMessageDialog(extJFrame, "ETC卡扣费时发现当前ETC卡已经扣费");
                            flag = true;
                        } else {
                            flag = cpuCardSvc.writeCpuCard(cpuCard2);                   //复合消费指令进行扣款 
                        }
                        
                    }
                    if (!flag) {
                        FlowControl.chargeObserCode = "5";
                        keyboard.sendAlert();
                        LogControl.logInfo("ETC卡扣费失败");
                        sendInfo("ETC卡扣费失败");
                        extJFrame.updateInfo("ETC卡扣费失败", "ETC卡扣费失败,请出示有效ETC卡或选择其他付款方式");
                        Thread.sleep(2000);
                        keyboard.getMessage();
                        continue;
                    }
                    pay = cash;  //确认扣款成功后再将应付金额改为ETC卡扣款金额
                    extJFrame.updateFare(pay + "");
                    LogControl.logInfo("ETC卡扣费成功:" + pay);
                    sendInfo("ETC卡付款:" + pay);
                    exitMop = "51";                                                 //ETC卡付款
                    extJFrame.updateMop(exitMop);
                    cpuCard3 = cpuCardSvc.waitForCpuCard();
                    cpuCard3.setDiscount(etcDis);
                    keyboard.getMessage();
                    LogControl.logInfo("ETC卡扣费后余额:" + cpuCard3.getBalance());
                    if ("22".equals(cpuCard1.getCardType())) {//储值卡才显示余额
                        extJFrame.updateAttachInfo("余额:" + DoubleUtils.round(cpuCard3.getBalance(), 2, BigDecimal.ROUND_HALF_UP));
                    } else {
                        extJFrame.updateAttachInfo(null);//清除折后金额显示
                    }
                    extJFrame.updatePayInfo(cpuCard3.getCardSerial());
                    break;
                } else {
                    extJFrame.showInputFare();//显示输入金额界面
                    if (str == null) {
                        if (collectFee != 0) {
                            extJFrame.updateAttachInfo("含代收:" + collectFee + "元");
                        } else {
                            extJFrame.updateAttachInfo(null);
                        }
                        String tempMsg = "现金付款\n-实收金额若等于车费,直接按【确认】键\n-否则输入实收金额,再按【确认】键\n其他付款方式\n-选择使用";
                        if (FunctionControl.isFareInputFunActive()) {
                            tempMsg = "现金付款\n-请输入实收金额，再按【确认】键\n其他付款方式\n-选择使用";
                        }
                        extJFrame.updateInfo("付款方式？", tempMsg);
                        continue;
                    } else if (str.equals(Keyboard.CANCEL) && sb.length() >= 1) {
                        sb.delete(sb.length() - 1, sb.length());
                    } else if (str.matches(Constant.REGIX_NUM)) {
                        if (sb.length() == 0 && IntegerUtils.parseString(str) == 0) {//收费金额不能以0开头
                            keyboard.sendAlert();
                            continue;
                        } else if (sb.length() > 5) {//收费金额不能超过100万
                            keyboard.sendAlert();
                            extJFrame.updateInfo("", "实收金额不能超过100万");
                            continue;
                        }
                        sb.append(str);
                    } else if (str.equals(Keyboard.CONFIRM)) {
                        if (collectFee != 0) {
                            extJFrame.updateAttachInfo("含代收:" + collectFee + "元");
                        } else {
                            extJFrame.updateAttachInfo(null);
                        }
                        if (veh.isTruck() && veh.getWeight() == 0) {//当前为货车但无计重信息（倒车）
                            keyboard.sendAlert();
                            extJFrame.updateInfo("无车辆重量信息", "等待车辆到达");
                            Thread.sleep(2000);
                            continue;
                        } else if (change.equals("") && !total.equals("")) {//有总金额但无找零金额,表示当前总金额不足
                            if (!FunctionControl.isFareInputFunActive()) {//未启用现金付款时输入金额限制功能
                                sb.delete(0, sb.length());
                                extJFrame.updateChange("", "");
                                total = "";
                            }
                            keyboard.sendAlert();
                            continue;
                        } else {
                            //启用现金付款输入金额限制功能
                            if (change.equals("") && total.equals("") && FunctionControl.isFareInputFunActive() && pay > 0) {
                                keyboard.sendAlert();
                                continue;
                            }
                            int vehType = veh.getVehicleType();
                            if (vehType == Constant.MILITARY_VEHICLE) {
                                LogControl.logInfo("军警车：" + pay);
                                exitMop = "17";//军警车
                            } else if (vehType == Constant.INSIDE_VEH) {//内部车
                                LogControl.logInfo("免费车：" + pay);
                                exitMop = "40";//免费车
                            } else if (vehType == Constant.GREEN_VEH) {//绿通车
                                if (pay == 0) {
                                    LogControl.logInfo("绿通车：" + pay);
                                    exitMop = "01";//绿通车付款方式仍为01(GEA设定)
                                } else {//绿通车超载后仍然按现金收费
                                    LogControl.logInfo("绿通超载,现金付款：" + pay);
                                    sendInfo("绿通超载,现金付款");
                                    exitMop = "01";
                                }
                            } else if (vehType == Constant.EMERGENCY_VEH) {
                                exitMop = "23";//紧急车
                            } else if (exitMop.equals("00")) {
                                LogControl.logInfo("现金付款:" + pay);
                                sendInfo("现金付款:" + pay);
                                exitMop = "01";                                         //现金付款
                            }
                            extJFrame.updateMop(exitMop);//收费界面显示付款方式
                            break;
                        }
                    } else if (str.equals(Keyboard.SIMU)) {
                        sb.delete(0, sb.length());
                        char temp = '0';
                        if (collectFlag && cpuCard1 == null) {
                            temp = simControl.runSimulate(6);
                        } else {
                            temp = simControl.runSimulate(1);
                        }
                        switch (temp) {
                            case '0':                                                   //取消操作
//                            extJFrame.updateFare(pay + "");                         //显示收费界面
                                extJFrame.updateFare(pay, collectFee);
                                extJFrame.showInputFare();                              //显示收费员输入收费金额界面
                                String tempMsg = "现金付款\n-实收金额若等于车费,直接按【确认】键\n-否则输入实收金额,再按【确认】键\n其他付款方式\n-选择使用";
                                if (FunctionControl.isFareInputFunActive()) {
                                    tempMsg = "现金付款\n-请输入实收金额，再按【确认】键\n其他付款方式\n-选择使用";
                                }
                                extJFrame.updateInfo("付款方式？", tempMsg);
                                break;
                            case '2':                                                   //更改车型车种
                                runModifyVehClassOrType();
                                return runChargeFare();
                            case '5'://更改代收
                                runAfterReadCard(1);
                                return runChargeFare();
                        }
                    } else if (str.equals(Keyboard.RED)) {//收费过程中雨棚灯变红
                        runChangeCanopyRed();
                    } else {
                        keyboard.sendAlert();
                    }
                    if (sb.length() > 0) {
                        total = sb.toString();
                        double temp = DoubleUtils.parseString(total);
//                    if (temp >= pay) {
                        double tempPay = DoubleUtils.sum(pay, collectFee);
                        if (temp >= tempPay) {
                            change = String.valueOf(temp - tempPay);
                        } else {
                            change = "";
                        }
                    } else {
                        total = "";
                        change = "";
                    }
                    extJFrame.updateChange(total, change);                              //更新界面中间收费及找零信息
                }
            }
            if (sb.length() == 0) {
                total = String.valueOf(DoubleUtils.sum(pay, collectFee));
                change = "0";
            }
            vi.showVeh(veh.getVehicleClass() + "", (DoubleUtils.sum(pay, collectFee)) + "");//字符叠加显示车型及收费额
            vi.showVehType(exitMop);//字符叠加显示车种
            tfi.showFare(DoubleUtils.sum(pay, collectFee));//更新实际收费金额
            extJFrame.updateFareInfo(change); //屏幕上方更新收费及找零信息
            ls.addTotalMoney((int) DoubleUtils.mul(pay, 100));
            switch (exitMop) {
                case "01"://现金付款
                    totalF = total;
                    changeF = change;
                    //费显显示支付总金额及找零金额
                    tfi.showTotalAndChange(totalF, changeF);
                    FlowControl.logout.setMop1count(FlowControl.logout.getMop1count() + 1);
                    FlowControl.logout.setMop1amount(FlowControl.logout.getMop1amount() + (int) DoubleUtils.mul(pay, 100));
                    FlowControl.logout.setTotalM(logout.getTotalM() + (int) DoubleUtils.mul(pay, 100));//下班记录中的现金总收费额更新
                    ls.addCashMoney((int) DoubleUtils.mul(pay, 100));
                    break;
                case "51"://ETC卡付款
                    FlowControl.logout.setMop2count(FlowControl.logout.getMop2count() + 1);
                    FlowControl.logout.setMop2amount(FlowControl.logout.getMop2amount() + (int) DoubleUtils.mul(pay, 100));
                    ls.addXtCardMoney((int) DoubleUtils.mul(pay, 100));
                    break;
                case "48"://入口
                    FlowControl.logout.setMop3count(FlowControl.logout.getMop3count() + 1);
                    FlowControl.logout.setMop3amount(FlowControl.logout.getMop3amount() + (int) DoubleUtils.mul(pay, 100));
                    break;
                case "12"://银联卡付款
                    FlowControl.logout.setMop4count(FlowControl.logout.getMop4count() + 1);
                    FlowControl.logout.setMop4amount(FlowControl.logout.getMop4amount() + (int) DoubleUtils.mul(pay, 100));
                    ls.addUpCardMoney((int) DoubleUtils.mul(pay, 100));
                    break;
                case "37"://储值卡付款
                    FlowControl.logout.setMop6count(FlowControl.logout.getMop6count() + 1);
                    FlowControl.logout.setMop6amount(FlowControl.logout.getMop6amount() + (int) DoubleUtils.mul(pay, 100));
                    break;
                case "07"://欠款
                    FlowControl.logout.setMop7count(FlowControl.logout.getMop7count() + 1);
                    FlowControl.logout.setMop7amount(FlowControl.logout.getMop7amount() + (int) DoubleUtils.mul(pay, 100));
                    break;
                case "17"://军警车
                    FlowControl.logout.setMop8count(FlowControl.logout.getMop8count() + 1);
                    FlowControl.logout.setMop8amount(FlowControl.logout.getMop8amount() + (int) DoubleUtils.mul(pay, 100));
                    ls.addFreeVehCnt();
                    break;
                case "38"://公务车拖车
                    FlowControl.logout.setMop9count(FlowControl.logout.getMop9count() + 1);
                    FlowControl.logout.setMop9amount(FlowControl.logout.getMop9amount() + (int) DoubleUtils.mul(pay, 100));
                    break;
                case "23"://紧急车
                    FlowControl.logout.setMop10count(FlowControl.logout.getMop10count() + 1);
                    FlowControl.logout.setMop10amount(FlowControl.logout.getMop10amount() + (int) DoubleUtils.mul(pay, 100));
                    ls.addFreeVehCnt();
                    break;
                case "39"://公务车个人
                    FlowControl.logout.setMop11count(FlowControl.logout.getMop11count() + 1);
                    FlowControl.logout.setMop11amount(FlowControl.logout.getMop11amount() + (int) DoubleUtils.mul(pay, 100));
                    break;
                case "40"://免费车
                    FlowControl.logout.setMop12count(FlowControl.logout.getMop12count() + 1);
                    FlowControl.logout.setMop12amount(FlowControl.logout.getMop12amount() + (int) DoubleUtils.mul(pay, 100));
                    ls.addFreeVehCnt();
                    break;
            }
            return 0;
        } catch (Exception ex) {
            LogControl.logInfo("收费异常", ex);
            return -1;
        } finally {
            tempCtrl.deleteTempEtcInfo();
        }
    }

    /**
     * 等待车辆离开
     *
     * @param level 0记录流水等信息<br>
     * 1 仅处理车辆离开,不做任何记录
     */
    private void runWhileVehLeaveNew(final int level) {
        try {
            if (level == 0) {
                generateTempTransInfo();
                generateTempLogoutInfo();
            }
            LogControl.logInfo("等待车辆离开");
            sendInfo("等待车辆离开");
            runWhileVehLeave = true;
            cxp.liftRailing(); //升起自动栏杆
            cxp.changeTrafficLightGreen();//通行灯变绿
            extJFrame.updateWaitForVehLeave(); //主界面提示等待车辆离开,通行灯变绿,自动栏杆抬起
            if (lane.isExit()) {
                extJFrame.showCardAndWeight();
            }
            ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
                @Override
                public void run() {
                    String str;
                    while (runWhileVehLeave && !cxp.checkPassageCoil()) {
                        try {
                            //通道线圈上无车
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                        }
                        //等待车辆离开时发现车辆已离开
                        //主要是用于在等待车辆离开时收费员触发“模拟”或“卡机”,在相应操作过程中车辆离开,操作结束后,需要结束等待车辆离开的流程
                        if (vehLeft) {
                            break;
                        }
                        str = keyboard.getMessage();
                        //**********************************自助发卡*****************************************//
                        if (lane.isEntry() && amc.isActivated()) {
                            if (amc.isVehBackOff()) {//等待车辆离开时车辆倒车,报警
                                amc.setVehBack(false);
                                LogControl.logInfo("自助发卡,等待车辆离开时车辆倒车");
                                extJFrame.updateInfo("车辆倒车", "自助发卡,等待车辆离开时车辆倒车");
                                warning = true;
                            }
                        }
                        //**********************************自助发卡*****************************************//
                        if (str == null) {
                            if (lane.isEntry()) {
                                if ("48".equals(entryMop) && !amc.isActivated() && entryTicketType == 1 && FunctionControl.isCscToEtcActive()) {//仅用于入口且已刷通行卡,自助发卡车道不做此判断
                                    int flag = runCpuCard();//等待车辆离开时司机出示ETC卡,可继续刷卡
                                    if (flag == 0) {//发现ETC卡并且能够正常读写
                                        card = card1;
                                        LogControl.logInfo("已发通行卡" + card.getCardSerial() + ",司机出示ETC卡" + cpuCard1.getCardSerial());
                                        if (level == 0) {
                                            ls.decreasePassCardUseCnt();//工班表减少通行卡使用数量
                                            logout.setTtC(logout.getTtC() - 1);//下班表减少通行卡使用数量
                                            logout.setMop3count(logout.getMop3count() - 1);//下班表减少通行卡使用数量
                                            generateTempLogoutInfo();
                                            entryTicketType = 2;
                                        }
                                    } else if (flag == 2) {//发现ETC卡但不能使用
                                        cpuCard1 = null;
                                        cpuCard2 = null;
                                        cpuCard3 = null;
                                        extJFrame.updateInfo("ETC卡禁止使用", "ETC卡禁止使用,请将ETC卡拿开");
                                    } else if (flag == 1) {
                                        extJFrame.updateInfo("等待车辆离开", "等待车辆离开");
                                    }
                                }
                            }
                        } else if (Keyboard.SIMU.equals(str)) {
                            int tempFlag = 0;
                            if (lane.isEntry()) {
                                if (collectFlag && collectFee > 0) {
                                    tempFlag = simControl.runSimulate(2);//代收通行费有重打印发票功能
                                } else {
                                    tempFlag = simControl.runSimulate(4);//入口封闭重打印发票功能
                                }
                            } else if (lane.isExit()) {
                                if (("01".equals(exitMop) && pay > 0) || (collectFlag && collectFee > 0)) {//现金付款或有代收通行费时可重打印发票
                                    tempFlag = simControl.runSimulate(2);
                                } else {//非现金付款无重打印发票功能
                                    tempFlag = simControl.runSimulate(4);
                                }
                            }
                            switch (tempFlag) {
                                case '0'://取消操作
                                    break;
                                case '1':
                                    boolean flag = true;
                                    if (FunctionControl.isSimuConfirmActive()) {//启用模拟通行监控室确认
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                                        Date date = new Date();
                                        String time = sdf.format(date);
                                        SimulateRun sr = new SimulateRun();
                                        sr.setRoad(lane.getRoadId());
                                        sr.setStation(lane.getStationId());
                                        sr.setLane(lane.getLaneId());
                                        sr.setTime(time);
                                        sr.setTimeout(String.valueOf(LaneServerControl.getOverTimeInterval()));
                                        extJFrame.updateInfo("等待监控室确认", "模拟通行\n等待监控室确认");
                                        LogControl.logInfo("模拟通行请求监控室确认");
                                        flag = lc.waitForSimuPassConfirm(sr.toString());
                                        keyboard.getMessage();
                                        if (flag) {
                                            if (lane.isExit()) {
                                                extJFrame.showCardAndWeight();
                                            }
                                            extJFrame.updateInfo("模拟通行", "按【确认】键确认");
                                            String order;
                                            while (true) {
                                                try {
                                                    Thread.sleep(100);
                                                } catch (InterruptedException ex) {
                                                }
                                                order = keyboard.getMessage();
                                                if (order == null) {
                                                    
                                                } else if (Keyboard.CONFIRM.equals(order)) {
                                                    break;
                                                } else {
                                                    keyboard.sendAlert();
                                                }
                                            }
                                        }
                                    }
                                    if (flag) {
                                        runWhileVehLeave = false;//通行模拟
                                        if (level == 0) {
                                            vehObserCode = "7";
                                            FlowControl.logout.setSimuC(FlowControl.logout.getSimuC() + 1);
                                        }
                                        LogControl.logInfo("确认模拟通行");
                                    } else {
                                        LogControl.logInfo("拒绝模拟通行");
                                    }
                                    break;
                                case '2'://更改车型车种,此时不允许此操作
                                    break;
                                case '3'://被拖车
                                    if (level == 0) {
                                        vehObserCode = "8";
                                        FlowControl.logout.setTowedC(FlowControl.logout.getTowedC() + 1);
                                    }
                                    break;
                                case '4':
                                    runPrint();//打印票据
                                    LogControl.logInfo("重打印发票");
                                    if (level == 0) {
                                        logout.setReceiptMore(logout.getReceiptMore() + 1);//重打票次数加1
                                        FlowControl.chargeObserCode = "7";
                                        ls.addBadInVoiceCnt();//废票数加1
//                                        generateTempTransInfo();
//                                        generateTempLogoutInfo();
                                    }
                                    break;
                            }
                            extJFrame.updateInfo("等待车辆离开", "等待车辆离开");
                            if (lane.isExit()) {
                                extJFrame.showCardAndWeight();
                            }
                        } else if (Keyboard.RED.equals(str)) {//收费过程中雨棚灯变红
                            runChangeCanopyRed();
                        } else if ("卡机".equals(str)) {//等待车辆离开时更换卡箱
                            cartControl.run(0, 1);
                            extJFrame.updateInfo("等待车辆离开", "等待车辆离开");
                            if (lane.isExit()) {
                                extJFrame.showCardAndWeight();
                            }
                        } else {
                            keyboard.sendAlert();
                        }
                    }
                }
            });
            while (runWhileVehLeave && !cxp.checkPassageCoil()) {//等待车辆离开
                Thread.sleep(1);
            }
            lc.stopWaitingConfirm();//停止监控室确认请求
            if (!amc.isActivated()) {
                AudioControl.getSingleInstance().playAudioByebye();//语音提示祝您一路顺风（自助发卡车道不做此提示）
            }
            deleteTempTransInfo();//删除临时交易流水
            deleteTempCollectList();//删除临时代收流水
            if (level == 1) {//此代码是为了确保ETC车辆不做处理直接放行时不会产生多余的临时交易图片
                deleteTempTransImg();
            }
            if (level == 0) {
                String htmlMsg = extJFrame.getHtmlMsg();
                //充分利用车辆离开的时间记录流水等
                generateTempLogoutInfo();//记录临时下班信息
                generateImg();//产生交易图片
                generateTransInfo();//产生交易流水
                generateCollectList();//生成代收流水
                extJFrame.updateVehCount(vehCount + "");
                if (cartControl.isRunning()) {//恢复卡箱提示信息
                    extJFrame.updateHtmlInfo(null, htmlMsg);
                }
                sendEnterStationInfo();                                         //发送车辆入站信息给路径识别服务器
                sendCostRecordInfo();                                           //发送收费记录给路径识别服务器
                vehCount++;
                increaseRecordId();
                updateLastVehInfo();                                                //更新上一辆车的信息
            }
            weighControl.updateAfterFirstVehLeft();                             //车辆从通道线圈离开后,删除此车辆称重信息
            if (lane.isEntry() && !amc.isActivated()) {//入口未启用自助发卡时,车辆离开时需要自动删除车型分类器中记录的车型
                amc.removeFirstVeh();
            }
            cxp.changeTrafficLightRed();//通行灯变红
            ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {//此处将车辆离开通道线圈的逻辑采用单独线程处理,不影响收费流程,这样可以快速开始下一条交易,减少收费时间
                @Override
                public void run() {
                    if (runWhileVehLeave) {//若检测到线圈有车并且是等待车辆离开的情况下,等待2秒钟之后再进行检测,避免线圈出现故障时车辆尚未离开栏杆落下出现砸车的情况
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                        }
                    }
                    while (runWhileVehLeave && cxp.checkPassageCoil()) {//通道线圈上有车
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                        }
                    }
                    String plateNum = lastVehPlateNum;
                    lprSvc.recordLastPlateNum(plateNum);
//                    lprSvc.initPlateInfo();//初始化车牌识别信息
                    lprSvc.startCaptureImg();//交易开结束,开始进行车牌识别
                    LogControl.logInfo("车辆已离开");
                    runWhileVehLeave = false;
                    cxp.lowerRailing();//自动栏杆降下
                }
            });
            sendInfo("");
        } catch (Exception ex) {
            LogControl.logInfo("等待车辆离开异常", ex);
        }
    }

    /**
     * 上班后输入发票号码流程(出口流程)
     *
     * @return 0 完成；1下班；-1异常
     */
    private int getTicketNum() {
        try {
            ticketNum = null;
            if ((!lane.isExit() && !collectFlag) || amc.isActivated()) {//出口才需要获取发票号码,自助发卡模式下默认无此功能
                return 0;
            }
            sendInfo("等待输入1-9位发票号码");
            LogControl.logInfo("等待输入1-9位发票号码");
            String str;
            extJFrame.showTicketNoPanel();
            extJFrame.hideTicketJPanel2();
            extJFrame.updateInfo("", "等待输入1-9位发票号码");
            StringBuilder sb1 = new StringBuilder();
            while (true) {
                Thread.sleep(10);
                if (!lc.isBureauOnLine() && offLineControl.isTerminated()) {
                    //离线,并且离线倒计时已归0,自动下班
                    runChangeCanopyRed();//自动改为红灯
                    logout(1);
                    return 1;
                }
                int len = sb1.length();
                str = keyboard.getMessage();
                if (str == null) {
                    continue;
                } else if (str.matches("[0-9]")) {
                    if (sb1.length() == 0 && str.equals("0")) {
                        extJFrame.updateInfo("", "发票号码不能以0开头");
                        keyboard.sendAlert();
                        continue;
                    }
                    if (sb1.length() >= 9) {
                        extJFrame.updateInfo("", "发票号码不能超过9位");
                        keyboard.sendAlert();
                        continue;
                    }
                    sb1.append(str);
                    extJFrame.updateInfo("", "等待输入1-9位发票号码");
                } else if (Keyboard.CANCEL.equals(str)) {
                    if (len > 0) {
                        sb1.delete(len - 1, len);
                        extJFrame.updateInfo("", "等待输入1-9位发票号码");
                    } else {
                        keyboard.sendAlert();
                    }
                } else if (Keyboard.CONFIRM.equals(str)) {
                    if (len >= 1 && len <= 9) {//发票号1-9位
                        break;
                    } else {
                        extJFrame.updateInfo("", "等待输入1-9位发票号码");
                        keyboard.sendAlert();
                    }
                } else if (Keyboard.OFFDUTY.equals(str)) {
                    boolean flag = logout(LOGOUT_MANUAL);
                    if (flag) {//下班成功
                        return 1;
                    }
                }
                extJFrame.updateTicketNo(sb1.toString());
                
            }
            extJFrame.hideCentralPanel();
            ticketNum = sb1.toString();
            LogControl.logInfo("获取发票号:" + ticketNum);
            ls.setInvStartId(IntegerUtils.parseString(ticketNum == null ? "0" : ticketNum));
            sendInfo("");
            return 0;
        } catch (Exception ex) {
            LogControl.logInfo("获取发票号码异常", ex);
            return -1;
        }
    }

    /**
     * 获取发票号码及字轨号码
     *
     * @return 0 完成；1下班；-1异常
     */
    private int getTicketAndTrackNum() {
        try {
            ticketNum = null;
            trackNo = null;
            if ((!lane.isExit() && !collectFlag) || amc.isActivated()) { //出口或者代收通行费时才需要获取发票号码,自助发卡模式下默认无此功能
                return 0;
            }
            sendInfo("等待输入1-9位发票号码及4位字轨号");
            LogControl.logInfo("等待输入1-9位发票号码及4位字轨号");
            String str;
            extJFrame.showTicketNoPanel();
            extJFrame.showTicketJPanel2();
            extJFrame.updateInfo("", "等待输入1-9位发票号码及4位字轨号");
            StringBuilder sb1 = new StringBuilder();//发票号
            StringBuilder sb2 = new StringBuilder();//字轨号
            boolean flag1 = true;//发票号输入标识
            boolean flag2 = false;//字轨号输入标识
            while (true) {
                Thread.sleep(10);
                if (!lc.isBureauOnLine() && offLineControl.isTerminated()) {
                    //离线,并且离线倒计时已归0,自动下班
                    runChangeCanopyRed();//自动改为红灯
                    logout(1);
                    return 1;
                }
                int len1 = sb1.length();
                int len2 = sb2.length();
                str = keyboard.getMessage();
                if (str == null) {
                    continue;
                } else if (str.matches("[0-9]")) {
                    if (sb1.length() == 0 && str.equals("0")) {
                        extJFrame.updateInfo("", "发票号码不能以0开头");
                        keyboard.sendAlert();
                        continue;
                    } else if (sb1.length() < 9 && flag1) {
                        sb1.append(str);
                    } else if (sb1.length() == 9 && sb2.length() < 4) {
                        sb2.append(str);
                    } else if (sb2.length() == 4) {
                        keyboard.sendAlert();
                    } else if (flag2 && sb2.length() < 4) {
                        sb2.append(str);
                    }
                    extJFrame.updateInfo("", "等待输入1-9位发票号码及4位字轨号");
                } else if (Keyboard.CANCEL.equals(str)) {
                    if (sb2.length() > 0) {
                        flag1 = false;//不能输入发票号
                        flag2 = true;//可以输入字轨号
                        sb2.delete(sb2.length() - 1, sb2.length());
                        extJFrame.updateInfo("", "等待输入1-9位发票号码及4位字轨号");
                    } else if (len1 > 0) {
                        flag1 = true;//可以输入发票号
                        flag2 = false;//不能输入字轨号
                        sb1.delete(len1 - 1, len1);
                        extJFrame.updateInfo("", "等待输入1-9位发票号码及4位字轨号");
                    } else {
                        keyboard.sendAlert();
                    }
                } else if (Keyboard.CONFIRM.equals(str)) {
                    if (len1 >= 1) {//已输入至少1位发票号
                        flag2 = true;
                        flag1 = false;
                    } else {
                        flag1 = true;
                        flag2 = false;
                    }
                    if (len1 >= 1 && len1 <= 9 && len2 == 4) {
                        break;
                    } else {
                        extJFrame.updateInfo("", "等待输入1-9位发票号码及4位字轨号");
                        keyboard.sendAlert();
                    }
                } else if (Keyboard.OFFDUTY.equals(str)) {
                    boolean flag = logout(LOGOUT_MANUAL);
                    if (flag) {//下班成功
                        return 1;
                    }
                }
                extJFrame.updateTicketNo(sb1.toString());
                extJFrame.updateTicketJPanel2(sb2.toString());
                
            }
            extJFrame.hideCentralPanel();
            ticketNum = sb1.toString();
            trackNo = sb2.toString();
            LogControl.logInfo("获取发票号:" + ticketNum + ",字轨号" + trackNo);
            ls.setInvStartId(IntegerUtils.parseString(ticketNum == null ? "0" : ticketNum));
            sendInfo("");
            return 0;
        } catch (Exception ex) {
            LogControl.logInfo("获取发票号码异常", ex);
            return -1;
        }
    }

    /**
     * 进入紧急车流程
     *
     */
    private void runEmerVeh() {
        try {
            LogControl.logInfo("紧急车开始");
            sendInfo("紧急车");
            entryMop = "23";
            exitMop = "23";
            exitTicketType = 0;
            entryTicketType = 3;
            vi.showVehType(exitMop);//字符叠加显示车种
            cxp.liftRailing();                                                      //升起自动栏杆
            cxp.changeTrafficLightGreen();                                          //通行灯变绿
            extJFrame.updateWaitForVehLeave();                                      //主界面提示等待车辆离开,通行灯变绿,自动栏杆抬起
            extJFrame.updateInfo("紧急车", "等待紧急车离开");
            EmerControl emerControl = new EmerControl(keyboard);
            ThreadPoolControl.getThreadPoolInstance().execute(emerControl);
            runWhileVehLeave = true;
            int emeCnt = 0;//紧急车数量
            boolean flag = false;//紧急车车辆正在通过收费车道标志
            while (emerControl.getRunWhileEmer()) {
                Thread.sleep(10);
                setVeh(new Vehicle());
                card2 = new Card();
                lprSvc.initPlateInfo();
                lprSvc.startCaptureImg();//等待紧急车通过,开始车牌识别
                while (emerControl.getRunWhileEmer()) {
                    Thread.sleep(10);
                    String plateNum = lprSvc.getPlate();
                    String plateCol = lprSvc.getPlateColor();
                    veh.setLprFullVehPlateNum(plateNum);
                    veh.setFullVehPlateNum(plateNum);
                    veh.setLprPlateColor(plateCol);
                    veh.setKeyedPlateColor(plateCol);
                    extJFrame.updateLprPlate(plateNum);
                    extJFrame.updateLprPlateCol(plateCol);
                    extJFrame.updateKeyedPlate(plateNum);
                    extJFrame.updateKeyedPlateCol(plateCol);
                    if (!flag) {
                        //到达线圈第一次被激活,抓拍图像,一直到该车辆离开,都不再抓拍车辆图像
                        if (cxp.checkArriveCoil()) {//车辆到达到达线圈
                            card2.setDhm(new Date(System.currentTimeMillis()));//记录紧急车交易时间
                            flag = true;//车辆正在离开收费通道
                            generateImg();//抓拍图像
                        }
                    }
                    if (cxp.checkPassageCoil()) { //车辆到达通道线圈
                        //充分利用车辆离开的时间记录流水等
                        if (card2 != null && card2.getDhm() == null) {//到达线圈未触发导致车辆交易时间未记录并且为记录车辆图片
                            card2.setDhm(new Date(System.currentTimeMillis()));//记录紧急车交易时间
                            generateImg();//抓拍图像
                        }
                        if (lane.isEntry()) {//紧急车车辆离开时需要自动删除车型分类器中记录的车型
                            amc.removeFirstVeh();
                        }
                        ls.addFreeVehCnt();
                        sendEnterStationInfo();                                         //发送车辆入站信息给路径识别服务器
                        sendCostRecordInfo();                                           //发送收费记录给路径识别服务器
                        extJFrame.updateLastVehInfo(null, plateNum, null, null, null, null);//更新上一辆车的信息
                        veh.setVehicleType(Constant.EMERGENCY_VEH);
                        veh.setVehicleClass(1);//紧急车默认车型为1
                        if (card2 != null) {
                            card2.setVehClass("1");//紧急车默认车型为1
                        }
                        FlowControl.logout.setMop10count(FlowControl.logout.getMop10count() + 1);
                        generateTempLogoutInfo();//记录临时下班信息
                        generateTransInfo();
                        extJFrame.updateVehCount(vehCount + "");
                        emeCnt++;
                        extJFrame.updateInfo("紧急车", "等待紧急车离开\n紧急车数量 " + emeCnt);
                        vehCount++;
                        increaseRecordId();
                        weighControl.updateAfterFirstVehLeft();                             //车辆从通道线圈离开后,删除此车辆称重信息
                        while (cxp.checkPassageCoil()) {                                //车辆离开通道线圈
                            Thread.sleep(50);
                        }
                        setVeh(null);
                        card2 = null;
                        extJFrame.updateInfo("紧急车", "等待紧急车离开\n紧急车数量 " + emeCnt);
                        LogControl.logInfo("紧急车通过");
                        flag = false;//车辆已离开收费通道
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                }
            }
            runWhileVehLeave = false;
            entryMop = "00";
            exitMop = "00";
            exitTicketType = 0;
            entryTicketType = 0;
            vi.hideVehType();
            cxp.lowerRailing();
            cxp.changeTrafficLightRed();
            tfi.showWelcome();
            extJFrame.initTransaction();
            extJFrame.updateInfo("", "等待输入车型");
            extJFrame.hideCentralPanel();
            LogControl.logInfo("紧急车结束");
            sendInfo("");
            initTransaction();
        } catch (Exception ex) {
            LogControl.logInfo("紧急车异常", ex);
        }
    }

    /**
     * 进入军警车控制
     *
     */
    public void runMilitaryVeh() {
        try {
            if (lane.isEntry()) {                                          //入口无军警车流程
                keyboard.sendAlert();
            } else if (lane.isExit()) {                                   //出口时进入军警车流程
                if (veh.getVehicleType() != Constant.MILITARY_VEHICLE) {            //当前车型不是军警车,则将当前车型改为军警车
                    LogControl.logInfo("确认军警车");
                    sendInfo("军警车");
                    veh.setVehicleType(Constant.MILITARY_VEHICLE);
                    extJFrame.showMilitary();
                } else {                                                            //若当前车型是军警车,则将当前车型改为非军警车
                    LogControl.logInfo("取消军警车");
                    sendInfo("");
                    veh.setVehicleType(Constant.NORMAL_VEHICLE);
                    extJFrame.hideMilitary();
                }
            }
        } catch (Exception ex) {
            LogControl.logInfo("军警车异常", ex);
        }
    }

    /**
     * 语音报价
     */
    public void runAudioFare() {
        if ((lane.isExit() && pay > 0) || (collectFlag && collectFee > 0)) {
            //出口收费,语音报价,单独线程启动,不影响收费流程
            ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
                @Override
                public void run() {
                    AudioControl.getSingleInstance().playAudioFare((int) pay + collectFee);
                }
            });
        }
    }

    /**
     * 打印含代收通行费的发票
     */
    private void runPrint() {
        try {
            generateTempCollectList();//记录临时代收流水
            if (("01".equals(exitMop) && pay > 0) || (collectFlag && collectFee > 0)) {//现金付款且实付金额不为0或者当前有代收通行费
                LogControl.logInfo("打印发票");
                sendInfo("打印发票");
                String plate = "";
                if (FunctionControl.isPlatePrintActive()) {//启用打印车牌号码功能
                    plate = veh.getFullVehPlateNum();
                }
                //现金付款打印发票
                if (printer.getPrintError() != null) {//打印机异常时不能正常打印
                    LogControl.logInfo("打印机异常,无法正常打印发票");
//                    keyboard.sendAlert();
                    extJFrame.updatePayInfo("票据号：000000000");
                    printFlag = false;
                } else {
                    printFlag = true;
                    extJFrame.updatePayInfo("票据号：" + ticketNum);
                    LogControl.logInfo("票据号：" + ticketNum);
                    String limitWeight = "0";//发票上打印的限重
                    String fareWeight = "0";//发票上打印的收费重量
                    if (veh.getVehicleClass() >= 7) {//货车时发票上才打印重量信息,客车为0
                        limitWeight = veh.getLimitWeight() + "";
                        fareWeight = DoubleUtils.round(veh.getFareWeight(), 2, BigDecimal.ROUND_HALF_UP) + "";
                    }
                    String collRoad = "";
                    String collFee = "";
                    if (collectFlag) {
                        collRoad = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "collectRoad", "");
                        collFee = String.valueOf(collectFee);
                    }
                    LogControl.logInfo(collRoad + "代收通行费：" + collFee);
                    printer.print(limitWeight, fareWeight, lane.getLaneId(), staff.getStaffId(), veh.getVehicleClass()
                            + "", (pay + collectFee) + "", mainSvc.getStationName(card1.getRoadid(), card1.getStationid()), lane.getStationName(), collRoad, collFee, plate);//打印票据
                    ticketNum = IntegerUtils.parseString(ticketNum) + 1 + "";//打印发票后发票号码+1,这句代码位置不可变,确保重打印发票时发票号码需要继续加1
                    ls.addInvPrintCnt();//发票打印数加1
                }
            } else {
                printFlag = false;
            }
            sendInfo("");
        } catch (Exception ex) {
            LogControl.logInfo("打印发票时出现异常", ex);
        } finally {
            generateTempTransInfo();//记录临时交易信息
            generateTempLogoutInfo();//记录临时交易信息
        }
    }

    /**
     * 更改车型车种操作
     *
     * @return 成功返回0,异常返回-1 车辆违章返回-2
     */
    private int runModifyVehClassOrType() {
        try {
            LogControl.logInfo("模拟菜单-更改车型车种");
            sendInfo("更改车型车种");
            extJFrame.hideCentralPanel();
            extJFrame.updateInfo("", "等待输入车型");
            String order;
            while (true) {
                Thread.sleep(10);
                synchronized (violateObj) {
                    if (violationFlag) {//冲关车
                        return -2;
                    }
                }
                int vehClass = veh.getVehicleClass();
                order = keyboard.waitForOrder();
                order = transVehClassByCh(order);
                if (order.matches("[1-9]")) {
                    if ("6".equals(order)) {
                        keyboard.sendAlert();
                        extJFrame.updateInfo("无效车型", "无效车型,请输入有效车型");
                        continue;
                    }
                    if ("7".equals(order) || "8".equals(order) || "9".equals(order)) {
                        if (weighControl.getLimitWeight() == 0 || weighControl.getWeight() == 0) {
                            keyboard.sendAlert();
                            extJFrame.updateInfo("无效车型", "无效车型,请输入有效车型");
                            continue;
                        }
                    }
                    if (vehClass < 7 && ("8".equals(order) || "9".equals(order))) {
                        keyboard.sendAlert();
                        extJFrame.updateInfo("", "当前车辆为客车,客车不可修改为绿通车\n请重新输入要修改的车型或车种");
                        continue;
                    }
                    if ("8".equals(order) || "9".equals(order)) {
                        if (veh.getVehicleType() == Constant.MILITARY_VEHICLE) {
                            keyboard.sendAlert();
                            extJFrame.updateInfo("", "当前车辆为军警车,军警不可修改为绿通车\n请重新输入要修改的车型或车种");
                            continue;
                        }
                    }
                    extJFrame.updateVehClass(order);                                //界面显示车型
                    tfi.showVehClass(order);                                        //费显显示车型
                    veh.setVehicleClass(IntegerUtils.parseString(order));
                    if ("8".equals(order) || "9".equals(order)) {//绿通车
                        veh.setVehicleType(Constant.GREEN_VEH);
                    } else {
                        int vehType = veh.getVehicleType();
                        if (vehType != Constant.MILITARY_VEHICLE && vehType != Constant.INSIDE_VEH && vehType != Constant.EMERGENCY_VEH) {//非军警,内部,紧急车
                            veh.setVehicleType(Constant.NORMAL_VEHICLE);
                        }
                        //改为普通车时，注意如果是免费车，需要按照免费车处理
                        if (veh.getVehicleType() == Constant.NORMAL_VEHICLE && veh.getSpeVehFlag() == SpeVehFlag.WHITE_VEHICLE) {//免费车
                            veh.setVehicleType(Constant.INSIDE_VEH);
                        }
                    }
                    FlowControl.chargeObserCode = "F";
                    FlowControl.logout.setChangeClassC(FlowControl.logout.getChangeClassC() + 1);
                    card1.setObservationCode("10");
                    break;
                } else if (order.equals("军警")) {
                    if (vehClass == 8 || vehClass == 9) {
                        extJFrame.updateInfo("", "当前车为绿通车,不可修改为军警车\n请重新输入要修改的车型或车种");
                        continue;
                    }
                    runMilitaryVeh();
                    //从军警修改为普通车，但是当前车辆是免费车
                    if (veh.getVehicleType() == Constant.NORMAL_VEHICLE && veh.getSpeVehFlag() == SpeVehFlag.WHITE_VEHICLE) {//免费车
                        veh.setVehicleType(Constant.INSIDE_VEH);
                    }
                    FlowControl.chargeObserCode = "G";
                    FlowControl.logout.setChangeKindC(FlowControl.logout.getChangeKindC() + 1);
                    card1.setObservationCode("11");
                    break;
                } else {
                    keyboard.sendAlert();
                }
            }
            AlarmControl.generateAlarmInfo(Lane.getInstance(), FlowControl.staff.getStaffId(), (short) FlowControl.staff.getShiftId(), "31", (short) 1);
            card2.setVehClass(veh.getVehicleClass() + "");
            extJFrame.updateInfo("等待监控室管理员确认", "等待监控室管理员确认");
            LogControl.logInfo("等待监控室管理员确认");
            extJFrame.updateFare(null);                                             //显示收费界面
            card1 = mainSvc.waitForControlRoomConfirmEntry(card1, veh.getVehicleClass() + "");                  //监控室确认
            AlarmControl.generateAlarmInfo(Lane.getInstance(), FlowControl.staff.getStaffId(), (short) FlowControl.staff.getShiftId(), "31", (short) 0);
            LogControl.logInfo("入口信息:" + card1.getRoadid() + card1.getStationid());
            extJFrame.updateInfo("等待确认入口信息", "按【确认】键以确认入口数据");
            return runAfterReadCard(1);
        } catch (Exception ex) {
            LogControl.logInfo("更改车型车种异常", ex);
            return -1;
        }
    }

    /**
     * 计算通行费,里程,行程时间等
     */
    public void runGetFare() {
        try {
            veh.setWeight(weighControl.getWeight());
            veh.setLimitWeight(weighControl.getLimitWeight());
            veh.setAxleGroupCount(weighControl.getAxleGroupCount());
            veh.setAxleCount(weighControl.getAxleCount());
            if (card1 == null && cpuCard1 != null) {
                card1 = new Card(cpuCard1);
            }
            String enRoadid = card1.getRoadid();
            String enStationid = card1.getStationid();
            String enStationName = mainSvc.getStationName(enRoadid, enStationid);
//            double temp = getFareByRealTimePath();//按实际路径收费
            if ("99".equals(enRoadid) || pi == null //                    ||(lane.getRoadId().equals(enRoadid)&&lane.getStationId().equals(enStationid))
                    ) {//入口站为虚拟站或者无路径识别信息或U型车,按最短路径计费
                orderPay = mainSvc.getFare(enRoadid, enStationid, lane.getRoadId(), lane.getStationId(), veh);//计算通行费
                if (orderPay < 0) {//通行费计算结果为-1表示找不到该入口信息对应的计费标准
                    LogControl.logInfo("通行费计算异常,找不到该入口" + enRoadid + enStationid + "的计费信息,按未知入口站9996进行计费");
                    if (card1 != null) {
                        card1.setRoadid("99");
                        card1.setStationid("96");
                    }
                    if (cpuCard1 != null) {
                        cpuCard1.setRoadid("99");
                        cpuCard1.setStationid("96");
                    }
                    enRoadid = card1.getRoadid();
                    enStationid = card1.getStationid();
                    enStationName = mainSvc.getStationName(enRoadid, enStationid);
                    orderPay = mainSvc.getFare(enRoadid, enStationid, lane.getRoadId(), lane.getStationId(), veh);//计算通行费;

                }
                dist = mainSvc.getDistance(enRoadid, enStationid, lane.getRoadId(), lane.getStationId(), veh.getVehicleClass());     //获取里程信息
            } else {
                double temp = getFareByRealTimePath();//按实际路径收费
                if (temp != 0 && temp >= 5) {
                    LogControl.logInfo("按实际路径计费：" + temp);
                    orderPay = temp;
                    dist = pi.getTotalDistance();
                } else {//按最短路径收费
                    LogControl.logInfo("获取到实际路径信息,但按路径计算收费额为：" + temp + ",此收费额不符合规定,采用最短路径计费");
                    orderPay = mainSvc.getFare(enRoadid, enStationid, lane.getRoadId(), lane.getStationId(), veh);
                    if (orderPay < 0) {//通行费计算结果为-1表示找不到该入口信息对应的计费标准
                        LogControl.logInfo("通行费计算异常,找不到该入口" + enRoadid + enStationid + "的计费信息,按未知入口站9996进行计费");
                        if (card1 != null) {
                            card1.setRoadid("99");
                            card1.setStationid("96");
                        }
                        if (cpuCard1 != null) {
                            cpuCard1.setRoadid("99");
                            cpuCard1.setStationid("96");
                        }
                        enRoadid = card1.getRoadid();
                        enStationid = card1.getStationid();
                        enStationName = mainSvc.getStationName(enRoadid, enStationid);
                        orderPay = mainSvc.getFare(enRoadid, enStationid, lane.getRoadId(), lane.getStationId(), veh);//计算通行费;
                    }
                    dist = mainSvc.getDistance(enRoadid, enStationid, lane.getRoadId(), lane.getStationId(), veh.getVehicleClass());     //获取里程信息
                }
            }
            if (veh.isTruck() && veh.getWeight() == 0) {
                LogControl.logInfo("货车无计重信息");
                orderPay = 0;
            }
            LogControl.logInfo("应付通行费：" + orderPay);
            if (cpuCard3 != null) {
                int vehClass = veh.getVehicleClass();//根据出口收费员输入车型判断折扣率
                int userType = IntegerUtils.parseString(cpuCard3.getUserType());
                String issuer = cpuCard3.getIssuer();
                double tempFareWeight = veh.getFareWeight();
                double tempLimitWeight = veh.getLimitWeight();
                String cardType = cpuCard3.getCardType();
                double dis = cpuCardSvc.getETCDiscount(userType, vehClass, issuer, cardType, tempFareWeight, tempLimitWeight);
//                double dis = cpuCardSvc.getETCDiscount(userType, vehClass, issuer);
                LogControl.logInfo("ETC卡折扣率：" + dis);
                if (dis != 1) {//ETC折扣率不为1
                    double cash = DoubleUtils.mul(orderPay, dis);                        //折后金额
                    extJFrame.updateAttachInfo("折后金额:" + cash);//收费界面显示折后金额
                } else if (collectFee == 0) {//代收金额为0
                    extJFrame.updateAttachInfo(null);
                }
            }
            pay = orderPay;
            sendInfo("通行费" + pay + "元");
            
            int vehType = veh.getVehicleType();
            if (vehType == Constant.MILITARY_VEHICLE || vehType == Constant.INSIDE_VEH || vehType == Constant.GREEN_VEH || vehType == Constant.EMERGENCY_VEH) {//军警车,免费车时,费用为0
                pay = 0.0;
            }
            int vehClass = veh.getVehicleClass();
            if (vehClass == 8 || vehClass == 9) {//绿通车
                double fareWeight = veh.getFareWeight();//计费重量
                double limitWeight = veh.getLimitWeight();//称重
                if (fareWeight > DoubleUtils.mul(limitWeight, 1.05)) {//绿通车超载超过5%才按计重收费
                    pay = orderPay;//绿通车超限,按照正常计重收费
                }
            }
            if (pay == 0.0) {
                extJFrame.updateAttachInfo(null);//免费车清空附加信息
            }
//            if (exitTicketType == 4 && PaperCardControl.getSingleInstance().isPaperCardAvailable(veh.getVehicleClass())) {//纸券有效期
//                pay = 0;
//                extJFrame.updateAttachInfo("纸券");
//                exitMop = "60";
//            }
//            extJFrame.updateFare(pay + "");                                         //显示收费界面
            extJFrame.updateFare(pay, collectFee);
            extJFrame.updateEnt(enStationName, enRoadid + enStationid);             //界面显示入口站号级站名
            vi.showEntry(enRoadid + enStationid);//字符叠加显示入口站号
            tfi.showFare(DoubleUtils.sum(pay, collectFee));
            String duration = mainSvc.getDuration(card1.getDhm(), veh.getDate());//获取行驶时间
            double speed = mainSvc.getSpeed(dist, card1.getDhm(), veh.getDate());
            boolean overTimeFlag = false;//超时标志
            if ("2".equals(card1.getObservationCode())) {//超时车辆
                overTimeFlag = true;
            }
            extJFrame.showVehInfo(enStationName, dist, duration, speed, overTimeFlag);          //中间界面显示入口站,行驶时间,里程及行驶速度
            double fareWeight = DoubleUtils.round(veh.getFareWeight(), 2, BigDecimal.ROUND_HALF_UP);
            double limitWeight = DoubleUtils.round(veh.getLimitWeight(), 2, BigDecimal.ROUND_HALF_UP);
            extJFrame.showWeightInfo(veh.getAxleGroupCount() + "", fareWeight + "", limitWeight + "", weighControl.getSpeed());//显示称重信息
        } catch (Exception ex) {
            LogControl.logInfo("获取费用异常", ex);
        }
    }

    /**
     * 更新收费界面上一辆车的信息
     */
    private void updateLastVehInfo() {
        try {
            LogControl.logInfo("更新上一辆车的信息");
            String stationName = "";
            if (card1 != null) {
                stationName = mainSvc.getStationName(card1.getRoadid(), card1.getStationid());
            }
            String payInfo;
            if ("51".equals(exitMop)) {
                payInfo = "ETC卡：" + cpuCard3.getCardSerial();
            } else if ("01".equals(exitMop)) {
                if (printFlag) {
                    payInfo = "现金 票据号：" + (IntegerUtils.parseString(ticketNum) - 1);
                } else {
                    payInfo = "现金 票据号：000000000";
                }
                
            } else if ("17".equals(exitMop)) {
                payInfo = "军警车";
            } else if ("40".equals(exitMop)) {
                payInfo = "免费车";
            } else if ("23".equals(exitMop)) {
                payInfo = "紧急车";
            } else {
                payInfo = null;
            }
            String vehClass = "";
            String vehPlate = "";
            String ent = "";
            String weigh = "";
            if (veh != null) {
                vehClass = veh.getVehicleClass() + "";
                vehPlate = veh.getFullVehPlateNum();
                weigh = veh.getAxleCount() + ":" + DoubleUtils.round(veh.getFareWeight(), 2, BigDecimal.ROUND_HALF_UP);
            }
            if (card1 != null) {
                ent = card1.getRoadid() + card1.getStationid() + stationName;
            }
            
            extJFrame.updateLastVehInfo(vehClass, vehPlate,
                    ent,
                    DoubleUtils.sum(pay, collectFee) + "", payInfo, weigh); //车辆通过后将该车作为上一辆车的信息显示
            lastVehPlateNum = vehPlate;
        } catch (Exception ex) {
            LogControl.logInfo("更新上一辆车信息出现异常", ex);
        }
    }

    /**
     * 清空上一辆车的信息
     */
    private void initLastVehInfo() {
        extJFrame.updateLastVehInfo(null, null, null, null, null, null);
    }

    /**
     * 设置车牌颜色
     *
     * @param color 车牌颜色
     */
    public void setVehKeyedPlateColor(String color) {
        veh.setKeyedPlateColor(color);
    }

    /**
     * 产生交易流水,采用单独线程处理,不影响收费流程
     */
    private void generateTransInfo() {
        try {
            if (lane.isEntry()) {//生成入口流水
                if (card == null) {
                    mainSvc.generateEntTransInfo(cpuCard3, lane, staff, card2, veh, vehCount, entryMop, vehObserCode, chargeObserCode, mopObserCode, cardObserCode, recordId, trackNo, entryTicketType);
                } else {
                    if (cpuCard3 != null && cpuCard3.getFullVehPlateNum().equals(card.getFullVehPlateNum())) {//已发通行卡,司机出示ETC卡
                        mainSvc.generateEntTransInfo(cpuCard3, lane, staff, card2, veh, vehCount, entryMop, vehObserCode, chargeObserCode, mopObserCode, cardObserCode, recordId, trackNo, entryTicketType);
                    } else {//已发通行卡,司机出示ETC卡,第二次交易时司机再次出示ETC卡
                        mainSvc.generateEntTransInfo(cpuCard3, lane, staff, card2, veh, vehCount, entryMop, vehObserCode, chargeObserCode, mopObserCode, cardObserCode, recordId, trackNo, entryTicketType);
                    }
                }
            } else if (lane.isExit()) {//生成出口流水
//                String axleGroupStr = weighControl.getAxleGroupStr();
//                String axleWeightDetail = weighControl.getAxisWeightDetail();
                int invoiceNo;//本次交易发票号码
                if (printFlag && ticketNum != null) {//正常打印
                    if (StringUtils.isBlank(ticketNum)) {
                        invoiceNo = 0;
                    } else {
                        invoiceNo = IntegerUtils.parseString(ticketNum) - 1;
                    }
                } else {//打印异常
                    invoiceNo = 0;
                }
                LogControl.logInfo("veh:" + veh);
                mainSvc.generateExtTransInfo(trafficEnRoadid, trafficEnStationid, vehObserCode, chargeObserCode, mopObserCode, cardObserCode, axleGroupPreStr, preWeight,
                        staff, lane, card1, card2, veh, invoiceNo + "", cpuCard1, cpuCard3, orderPay, pay, exitMop, vehCount, recordId, trackNo, exitTicketType, serviceCardSerial);
            }
        } catch (Exception ex) {
            LogControl.logInfo("记录流水时出现异常", ex);
        }
    }

    /**
     * 生成简单的交易流水（出口ETC车辆直接放行时使用）
     */
    private void generateSimpleTransInfo() {
        mainSvc.generateSimpleTransInfo(cpuCard1, lane, staff, veh);
    }

    /**
     * 产生临时交易流水
     */
    private void generateTempTransInfo() {
        try {
            if (lane.isEntry()) {//生成入口流水
                if (card == null) {
                    mainSvc.generateTempEntTransInfo(cpuCard3, lane, staff, card2, veh, vehCount, entryMop, vehObserCode, chargeObserCode, mopObserCode, cardObserCode, recordId, trackNo, entryTicketType);
                } else {
                    if (cpuCard3 != null && cpuCard3.getFullVehPlateNum().equals(card.getFullVehPlateNum())) {//已发通行卡,司机出示ETC卡
                        mainSvc.generateTempEntTransInfo(cpuCard3, lane, staff, card, veh, vehCount, entryMop, vehObserCode, chargeObserCode, mopObserCode, cardObserCode, recordId, trackNo, entryTicketType);
                    } else {//已发通行卡,司机出示ETC卡,第二次交易时司机再次出示ETC卡
                        mainSvc.generateTempEntTransInfo(cpuCard3, lane, staff, card2, veh, vehCount, entryMop, vehObserCode, chargeObserCode, mopObserCode, cardObserCode, recordId, trackNo, entryTicketType);
                    }
                }
            } else if (lane.isExit()) {//生成出口流水
//                String axleGroupStr = weighControl.getAxleGroupStr();
//                String axleWeightDetail = weighControl.getAxisWeightDetail();
                int invoiceNo;//本次交易发票号码
                if (printFlag && ticketNum != null) {//正常打印
                    if (StringUtils.isBlank(ticketNum)) {
                        invoiceNo = 0;
                    } else {
                        invoiceNo = IntegerUtils.parseString(ticketNum) - 1;
                    }
                } else {//打印异常
                    invoiceNo = 0;
                }
                double getMoney = 0;//实收金额，未付款时实收金额为0
                if (chargeFlag) {//已付款
                    getMoney = pay;
                }
                mainSvc.generateTempExtTransInfo(trafficEnRoadid, trafficEnStationid, vehObserCode, chargeObserCode, mopObserCode, cardObserCode, axleGroupPreStr, preWeight,
                        staff, lane, card1, card2, veh, invoiceNo + "", cpuCard1, cpuCard3, orderPay, getMoney, exitMop, vehCount, recordId, trackNo, exitTicketType, serviceCardSerial);
            }
        } catch (Exception ex) {
            LogControl.logInfo("记录流水时出现异常", ex);
        }
    }

    /**
     * 删除临时交易流水
     */
    private void deleteTempTransInfo() {
        if (lane.isEntry()) {
            tempCtrl.deleteTempEnlist();
        } else if (lane.isExit()) {
            tempCtrl.deleteTempExlist();
        }
    }

    /**
     * 检测临时文件
     */
    private void checkTempInfo() {
        if (lane.isExit()) {
            LaneExList exList = tempCtrl.parseTempExList();
            if (exList != null) {
                try {
                    LogControl.logInfo("发现临时出口交易流水" + exList + ",产生出口交易流水");

//                    LaneListUtils.generationExList(exList);
                    ListControl.generateLaneExList(exList);
                    tempCtrl.deleteTempExlist();
                    String img1 = exList.getVehImageNo();
                    String img2 = exList.getPlateImageNo();
                    File file = new File("temp" + File.separator + img1 + ".jpg");
                    if (file.exists() && file.isFile()) {
                        LogControl.logInfo("发现临时出口交易流水对应交易图片" + img1);
                        File destFile = new File("img" + File.separator + img1 + ".jpg");
                        FileUtils.copyFile(file, destFile);
                        file.delete();
                    }
                    file = new File("temp" + File.separator + img2 + ".jpg");
                    if (file.exists() && file.isFile()) {
                        File destFile = new File("img" + File.separator + img2 + ".jpg");
                        FileUtils.copyFile(file, destFile);
                        file.delete();
                    }
                    
                } catch (IOException ex) {
                    LogControl.logInfo(ex.toString(), ex);
                }
            }
        }
        if (lane.isEntry()) {
            LaneEnList enList = tempCtrl.parseTempEnList();
            if (enList != null) {
                try {
                    LogControl.logInfo("发现临时入口交易流水" + enList + ",产生入口交易流水");
//                    LaneListUtils.generationEnList(enList);
                    ListControl.generateLaneEnList(enList);
                    tempCtrl.deleteTempEnlist();
                    String img1 = enList.getVehImageNo();
                    String img2 = enList.getPlateImageNo();
                    File file = new File("temp" + File.separator + img1 + ".jpg");
                    if (file.exists() && file.isFile()) {
                        File destFile = new File("img" + File.separator + img1 + ".jpg");
                        FileUtils.copyFile(file, destFile);
                        file.delete();
                    }
                    file = new File("temp" + File.separator + img2 + ".jpg");
                    if (file.exists() && file.isFile()) {
                        File destFile = new File("img" + File.separator + img2 + ".jpg");
                        FileUtils.copyFile(file, destFile);
                        file.delete();
                    }
                } catch (IOException ex) {
                    LogControl.logInfo(ex.toString(), ex);
                }
            }
        }
        LaneLogout tempLogout = tempCtrl.parseTempLogout();
        if (tempLogout != null) {
            LogControl.logInfo("发现临时下班流水" + tempLogout + ",产生下班流水");
//                LaneListUtils.generationLaneLogoutList(tempLogout);
            ListControl.generateLaneLogout(tempLogout);
            tempCtrl.deleteTempLogout();
        }
        LaneShift tempShift = tempCtrl.parseTempShift();
        if (tempShift != null) {
            LogControl.logInfo("发现临时工班流水" + tempShift + ",产生工班流水");
//                LaneListUtils.generationLaneShiftList(tempShift);
            ListControl.generateLaneShift(tempShift);
            tempCtrl.deleteTempShift();
        }
        CardBoxOpList cbList = tempCtrl.parseTempCardBoxoplist();
        if (cbList != null) {
            try {
                if (tempLogout != null && tempLogout.getJobEnd() != null) {
                    cbList.setOpTime(tempLogout.getJobEnd());//下班时间与下班记录中的下班时间保持一致
                }
                LaneListUtils.generationCardBoxOpList(cbList);
            } catch (Exception ex) {
                LogControl.logInfo(ex.toString(), ex);
            }
            tempCtrl.deleteTempCardBoxoplist();
        }
        ColList colList = tempCtrl.parseTempColList();
        if (colList != null) {
            try {
                LogControl.logInfo("发现临时代收流水" + colList + ",产生代收流水");
//                LaneListUtils.genColList(colList);
                ListControl.generateLaneColList(colList);
                tempCtrl.deleteTempCollist();
            } catch (Exception ex) {
                LogControl.logInfo(ex.toString(), ex);
            }
        }
        
    }

    /**
     * 生成车辆图片
     */
    private void generateImg() {
        //TODO 生成图片后续需要采用单独线程,不影响收费流程,但是需要修正两条交易图片冲突的问题,此问题后续修正
        try {
            LogControl.logInfo("生成交易图片");
            File file = new File("img");
            if (!file.exists() || !file.isDirectory()) {
                file.mkdirs();
            }
            String roadid = lane == null ? "000" : lane.getRoadId();
            String stationid = lane == null ? "000" : lane.getStationId();
            String laneid = lane == null ? "000" : lane.getLaneId();
            Date ts = card2 == null ? new Date(System.currentTimeMillis()) : card2.getDhm();
            String listNum = StringUtils.getListNo(roadid, stationid, laneid, ts, recordId);
            String vehImgFilePath = "img" + File.separator + listNum + "-1.jpg";
            String tempPath1 = "temp" + File.separator + listNum + "-1.jpg";
            File tempImgFile1 = new File(tempPath1);
            if (tempImgFile1.exists() && tempImgFile1.isFile()) {
                File img1 = new File(vehImgFilePath);
                FileUtils.copyFile(tempImgFile1, img1);
                tempImgFile1.delete();//删除临时图片文件
            } else {
                lprSvc.createPicture(vehImgFilePath);//获取车牌识别图像
            }
            String plateImgFilePath = "img" + File.separator + listNum + "-2.jpg";
            String tempPath2 = "temp" + File.separator + listNum + "-2.jpg";
            File tempImgFile2 = new File(tempPath2);
            File imgFile2 = new File(plateImgFilePath);
            if (onduty && !violationFlag) {//已上班并且为非违章车辆
                if (tempImgFile2.exists() && tempImgFile2.isFile()) {
                    FileUtils.copyFile(tempImgFile2, imgFile2);
                } else {
                    extJFrame.captureVideo(plateImgFilePath);//获取视频图像 
                }
            } else {//获取备用临时图片
                File srcFile = new File("temp/videoTemp.jpg");
                if (srcFile.exists() && srcFile.isFile()) {
                    FileUtils.copyFile(srcFile, imgFile2);
                    srcFile.delete();
                }
            }
            if (tempImgFile2.exists() && tempImgFile2.isFile()) {
                tempImgFile2.delete();//删除临时图片文件
            }
            mainSvc.createEntryImg(listNum);//获取入口图像
        } catch (Exception ex) {
            LogControl.logInfo("生成车辆图片异常", ex);
        }
    }

    /**
     * 记录车辆临时图片
     */
    public void generateTempTransImg() {
        try {
            File file = new File("temp");
            if (!file.exists() || !file.isDirectory()) {
                file.mkdirs();
            }
            String roadid = lane == null ? "000" : lane.getRoadId();
            String stationid = lane == null ? "000" : lane.getStationId();
            String laneid = lane == null ? "000" : lane.getLaneId();
            Date ts = card2 == null ? new Date(System.currentTimeMillis()) : card2.getDhm();
            String listNum = StringUtils.getListNo(roadid, stationid, laneid, ts, recordId);
            String tempImgPath1 = "temp" + File.separator + listNum + "-1.jpg";
            if (tempImg1 == null) {//尚未记录车牌识别临时图片,车牌识别的临时图片仅记录一次
                boolean flag = lprSvc.createPicture(tempImgPath1);//获取车牌识别图像
                if (flag) {//获取车牌识别图像成功
                    tempImg1 = tempImgPath1;//记录临时交易图片1的信息
                }
            } else if (!tempImgPath1.equalsIgnoreCase(tempImg1)) {//已记录车牌识别临时图片,再次记录并且文件名发生变化
                renameFile(tempImg1, tempImgPath1);
                tempImg1 = tempImgPath1;//记录临时交易图片1的信息
            }
            String plateImgFilePath = "temp" + File.separator + listNum + "-2.jpg";
            extJFrame.captureVideo(plateImgFilePath);//获取视频图像
            if (!plateImgFilePath.equalsIgnoreCase(tempImg2) && tempImg2 != null) {//新记录的交易图片与原来记录的交易图片命名不同,需要删除原来的交易图片
                File temp2 = new File(tempImg2);
                if (temp2.exists() && temp2.isFile()) {
                    temp2.delete();
                }
            }
            tempImg2 = plateImgFilePath;//记录临时交易图片2的信息
        } catch (Throwable ex) {
            LogControl.logInfo("生成车辆临时图片异常", ex);
        }
    }

    /**
     * 删除临时交易图片
     */
    public void deleteTempTransImg() {
        if (tempImg1 != null) {
            FileUtils.deleteFile(tempImg1);
        }
        if (tempImg2 != null) {
            FileUtils.deleteFile(tempImg2);
        }
    }

    /**
     * 将源文件改名为目标文件<br>
     * 若目标文件已存在,直接删除源文件<br>
     * 若源文件路径或目标文件路径为null,直接结束该方法不做任何修改
     *
     * @param srcFilePath 源文件路径
     * @param destFilePath 目标文件路径
     */
    public void renameFile(String srcFilePath, String destFilePath) {
        if (srcFilePath == null || destFilePath == null) {
            return;
        }
        File srcFile = new File(srcFilePath);
        File destFile = new File(destFilePath);
        if (destFile.exists() && destFile.isFile()) {//目标文件已存在,删除源文件
            if (srcFile.exists() && srcFile.isFile()) {
                srcFile.delete();
            }
        } else if (srcFile.exists() && srcFile.isFile()) {
            srcFile.renameTo(destFile);//将源文件改名为目标文件
        }
    }

    /**
     * 发送实时监控信息给监控室
     *
     * @param info 实时监控信息
     */
    public void sendInfo(String info) {
        sendAlarmInfo(info, 0);
    }

    /**
     * 发送实时监控信息给监控室
     *
     * @param info 实时监控信息
     * @param alarmFlag 0非报警信息 1 报警信息
     */
    public void sendAlarmInfo(String info, int alarmFlag) {
        try {
            if (lane.isEntry()) {//入口
                EnInfo ei = new EnInfo();
                ei.setAlarmInformation(info);
                ei.setAlarmType(alarmFlag + "");
                ei.setLane(lane.getLaneId());
                ei.setEnStation(lane.getRoadId() + lane.getStationId());
                if (onduty) {
                    ei.setCanopyLight(canopyFlag + "");
                    ei.setClasses(staff == null ? "" : staff.getShiftId() + "");
                    ei.setEnVehClass(veh == null ? "" : veh.getVehicleType() + "");
                    ei.setEnVehType(veh == null ? "" : veh.getVehicleClass() + "");
                    ei.setTradeId(vehCount + "");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    ei.setTradeTime(sdf.format(new Date()));
                    ei.setUserId(staff == null ? "" : staff.getStaffId());
                }
                
                lc.sendInfoMessage(ei.toString());
            } else if (lane.isExit()) {//出口
                ExitInfo exi = new ExitInfo();
                exi.setAlarmInformation(info);
                exi.setAlarmType(alarmFlag + "");
                exi.setLane(lane.getLaneId());
                exi.setExStation(lane.getRoadId() + lane.getStationId());
                if (onduty) {
                    exi.setCanopyLight(canopyFlag + "");
                    exi.setClasses(staff == null ? "" : staff.getShiftId() + "");
                    exi.setEnStation(card1 == null ? "" : card1.getRoadid() + card1.getStationid());
                    exi.setEnVehClass("");
                    exi.setEnVehType(card1 == null ? "" : card1.getVehClass());
                    exi.setExVehClass(veh == null ? "" : veh.getVehicleType() + "");
                    exi.setExVehType(veh == null ? "" : veh.getVehicleClass() + "");
                    exi.setFee(pay + "");
                    exi.setPaymentMethod(exitMop == null ? "" : exitMop);
                    exi.setStatus("");
                    exi.setTradeId(vehCount + "");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    exi.setTradeTime(sdf.format(new Date()));
                    exi.setUserId(staff == null ? "" : staff.getStaffId());
                }
                lc.sendInfoMessage(exi.toString());
            }
        } catch (Exception ex) {
            LogControl.logInfo("发送实时监控信息给监控室异常", ex);
        }
    }

    /**
     * 请求实时路径信息 合理利用写卡时间（写卡前请求收费路径,写卡后获取实时路径）
     *
     * @param entryCard 入口所发通行卡
     * @param exitLane 出口车道
     * @param veh 车辆信息
     * @param exitTime 出口交易时间
     */
    private void requestRealTimePath(final Card entryCard, final Lane exitLane, final Vehicle veh, final Date exitTime) {
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (lane.isExit() && pi == null) {
                        RequestPath pr = new RequestPath();
                        pr.setCardNum(RTUtil.getString(entryCard.getCardSerial()));
                        pr.setEnLane(RTUtil.getString(entryCard.getLaneId()));
                        pr.setEnStation(RTUtil.getString(trafficEnRoadid) + RTUtil.getString(trafficEnStationid));
                        pr.setEntryTime(entryCard.getDhm());
                        pr.setExitLane(RTUtil.getString(exitLane.getLaneId()));
                        pr.setExitStation(RTUtil.getString(exitLane.getRoadId() + exitLane.getStationId()));
                        pr.setExitTime(exitTime);
                        pr.setExitChannelClass((short) 1);
                        pr.setExitVehColor(RTUtil.getString(veh.getKeyedPlateColor()));
                        pr.setFareWeight(RTUtil.getString(DoubleUtils.mul(veh.getFareWeight(), 100)));//单位10千克
                        pr.setLimitWeight(RTUtil.getString(DoubleUtils.mul(veh.getLimitWeight(), 100)));//单位10千克
                        pr.setPlateNum(RTUtil.getString(veh.getFullVehPlateNum()));
                        pr.setVehClass((short) veh.getVehicleClass());
                        piSerial = rtSvc.requestForPath(pr);
                    }
                } catch (Exception ex) {
                    LogControl.logInfo("请求实际路径异常", ex);
                }
            }
        });
    }

    /**
     * 请求路径明细
     *
     * @param entryCard 入口所发通行卡
     * @param exitLane 出口车道
     * @param veh 车辆信息
     * @param exitTime 出口交易时间
     */
    private void requestPathDetail(final Card entryCard, final Lane exitLane, final Vehicle veh, final Date exitTime) {
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (!lane.isExit()) {//仅出口做此处理
                    return;
                }
                RtpControl rtpControl = RtpControl.getSingleInstance();
                String enRoadid = entryCard.getRoadid();
                String enStationid = entryCard.getStationid();
                String enLane = entryCard.getLaneId();
                Date enDhm = entryCard.getDhm();
                String exVehCol = veh.getKeyedPlateColor();
                int exVehClass = veh.getVehicleClass();
                String exPlateNum = veh.getFullVehPlateNum();
                String exRoadid = exitLane.getRoadId();
                String exStationid = exitLane.getStationId();
                String exLane = exitLane.getLaneId();
                String exCardNo = entryCard.getCardSerial();
                String realWeight = veh.getFareWeight() + "";
                String limitWeight = veh.getLimitWeight() + "";
                pathDetailSerial = rtpControl.requestPathDetail(enRoadid, enStationid, enLane, enDhm, exVehCol, exVehClass, exPlateNum, exRoadid, exStationid, exLane, exitTime, exCardNo, realWeight, limitWeight);
            }
        });
        
    }

    /**
     * 获取实际路径信息,该信息可用于计算最大行程时间 合理利用写卡时间（写卡前请求收费路径,写卡后获取实时路径）
     */
    private void getRealTimePath() {
        try {
            if (piSerial == 0) {
                LogControl.logInfo("无实时路径信息,路径信息消息流水号为0");
                return;
            }
            pi = rtSvc.getPathInfo(piSerial);
            if (pi == null) {
                return;
            }
            path = rtSvc.getRealPath(pi);
            String info = pi.getInfo();
            if (path != null && info != null) {
                path = path + "\n\n" + pi.getInfo();
            }
            LogControl.logInfo("获取实时路径信息:" + pi);
        } catch (Exception ex) {
            LogControl.logInfo("获取实时路径异常", ex);
        }
    }

    /**
     * 根据实时路径信息获取费用
     *
     * @return 费用
     */
    private double getFareByRealTimePath() {
        try {
            if (pi == null) {
                LogControl.logInfo("无实时路径信息,无法根据实时路径计算费用");
                return 0;
            } else {
                double temp = rtSvc.getFare(pi, veh);
                LogControl.logInfo("获取实际路径[" + path + "],根据实际路径计算费用：" + temp);
                return temp;
            }
        } catch (Exception ex) {
            LogControl.logInfo("获取实际路径信息出现异常", ex);
            return 0;
        }
    }

    /**
     * 发送车辆入站信息给路径识别服务器(入口发送)
     */
    private void sendEnterStationInfo() {
        try {
            if (lane.isEntry()) {//入口发送
                EntryStation esi = new EntryStation();
                if (lane != null) {
                    esi.setStationid(lane.getRoadId() + lane.getStationId());
                    esi.setLaneid(lane.getLaneId());
                }
                if (card2 != null) {
                    esi.setEntryTime(card2.getDhm());
                    esi.setCardNo(card2.getCardSerial());
                }
                if (staff != null) {
                    esi.setStaffid(staff.getStaffId());
                }
                if (veh != null) {
                    esi.setVehClass((short) veh.getVehicleClass());
                    esi.setPlateNum(veh.getFullVehPlateNum());
                    esi.setVehColor(veh.getKeyedPlateColor());
                }
                String serverip = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_SERVER, "fileUploadServerip", "0.0.0.0");
                String roadid = lane == null ? "000" : lane.getRoadId();
                String stationid = lane == null ? "000" : lane.getStationId();
                String laneid = lane == null ? "000" : lane.getLaneId();
                Date ts = card2 == null ? new Date(0) : card2.getDhm();
                String listNum = StringUtils.getListNo(roadid, stationid, laneid, ts, recordId);
                String month = StringUtils.getMonth(listNum.substring(13, 15));
                esi.setImgPath("http://" + serverip + ":8080/MTCStation/img/" + month + "/" + listNum + "-2.jpg");
                rtSvc.sendEnterStationInfo(esi);
            }
        } catch (Exception ex) {
            LogControl.logInfo("发送车辆入站信息给路径识别服务器异常", ex);
        }
    }

    /**
     * 收费记录上传到路径识别服务器
     */
    private void sendCostRecordInfo() {
        try {
            if (lane.isExit()) {//出口上传收费记录
                CostRecord cr = new CostRecord();
                if (pi != null) {//有路径识别信息
                    cr.setOrderSN(pi.getOrderSN());
                    cr.setFareVer(pi.getFareVer());
                    cr.setTotalDis(pi.getTotalDistance() + "");
                    cr.setDownLevel(0);//有路径识别信息,非降级处理
                    cr.setCollectionFee(pi.getCollectionFee() + "");
                } else {
                    String priceVer = ParamCacheQuery.queryFareVer();//获取费率版本号
                    if (priceVer != null && priceVer.matches("^[0-9]*$")) {//验证费率版本号是否是数字
                        cr.setFareVer(priceVer);//费率版本号
                    } else {
                        cr.setFareVer("0");//费率版本号
                    }
                    cr.setDownLevel(1);//无路径识别信息,降级处理
                    cr.setTotalDis(dist + "");
                }
                cr.setChargeType(chargeType);
                cr.setSaleRate(etcDis + "");
                if (card1 != null) {
                    cr.setEntryStationid(card1.getRoadid() + card1.getStationid());
                    cr.setEntryLaneid(card1.getLaneId());
                    cr.setEntryTime(card1.getDhm());
                    cr.setEntryStaffid(card1.getStaffId());
                }
                if (trafficEnRoadid != null && trafficEnStationid != null) {
                    cr.setTrafficStationid(trafficEnRoadid + trafficEnStationid);
                }
                if (veh != null) {
                    cr.setVehColor(veh.getKeyedPlateColor());
                    cr.setVehClass((short) veh.getVehicleClass());
                    cr.setPlateNum(veh.getFullVehPlateNum());
                    cr.setDetectWeight(DoubleUtils.mul(veh.getWeight(), 100) + "");//单位10千克
                    cr.setFareWeight(DoubleUtils.mul(veh.getFareWeight(), 100) + "");//单位10千克
                    cr.setLimitWeight(DoubleUtils.mul(veh.getLimitWeight(), 100) + "");//单位10千克
                }
                if (lane != null) {
                    cr.setExitStationid(lane.getRoadId() + lane.getStationId());
                    cr.setExitLaneid(lane.getLaneId());
                }
                cr.setExitChannelClass((short) 1);
                if (card2 != null) {
                    cr.setExitTime(card2.getDhm());
                    cr.setCardNum(card2.getCardSerial());
                    if (card2.getType() != null) {
                        cr.setCardType(IntegerUtils.parseString(card2.getType()));
                    }
                }
                if (cpuCard2 != null && cpuCard2.getCardType() != null) {//ETC卡
                    cr.setCardType(IntegerUtils.parseString(cpuCard2.getCardType()));
                } else {//通行卡
                    if (card2 != null && card2.getType() != null) {
                        cr.setCardType(IntegerUtils.parseString(card2.getType()));
                    }
                }
                if (staff != null) {
                    cr.setJobStart(staff.getJobStartTime());
                    cr.setExitStaffid(staff.getStaffId());
                }
                cr.setTranNum(vehCount);
                String serverip = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_SERVER, "fileUploadServerip", "0.0.0.0");
                String roadid = lane == null ? "000" : lane.getRoadId();
                String stationid = lane == null ? "000" : lane.getStationId();
                String laneid = lane == null ? "000" : lane.getLaneId();
                Date ts = card2 == null ? new Date(0) : card2.getDhm();
                String listNum = StringUtils.getListNo(roadid, stationid, laneid, ts, recordId);
                String month = StringUtils.getMonth(listNum.substring(13, 15));
                cr.setImgPath("http://" + serverip + ":8080/MTCStation/img/" + month + "/" + listNum + "-2.jpg");
                cr.setOrderPay(orderPay + "");
                cr.setRealFee(pay + "");
                cr.setCollectionPay(collectFee + "");
                cr.setCollectionFee(collectFee + "");
                if (exitMop != null) {
                    cr.setExitMop(IntegerUtils.parseString(exitMop));
                }
                rtSvc.sendCostRecordInfo(cr);
            }
        } catch (Exception ex) {
            LogControl.logInfo("收费记录上传到路径识别服务器异常", ex);
        }
    }

    /**
     * 下班
     *
     * @param autoFlag 自动下班标识,0表示手动下班 其他表示自动下班（当关闭软件时,若发现当前未下班,则自动下班）
     * @return 成功 true 失败 false
     */
    private boolean logout(int autoFlag) {
        try {
            boolean flag = true;
            if (autoFlag == 0) {
                sendInfo("等待下班");
                flag = jobControl.logout();
            }
            //下班
            if (flag == true) {
                extJFrame.stopVideoMonitor();//停止对视频的监控
                vi.hideStaff();//视频字符叠加隐藏收费员
                initLastVehInfo();//已下班,清空上一辆车的显示信息
                LogControl.logInfo("已下班");
                Date jobEndTime = new Date();
                //记录卡箱流动单
                cartControl.generateCadboxList(jobEndTime, CartOpType.SEND, null, staff.getStaffId(), 0);
                //删除临时卡箱流动单
                TempControl.getSingleInstance().deleteTempCardBoxoplist();
                ls.setLogoutTime(jobEndTime);
                ls.setListCnt(vehCount - 1);
                ls.setVehCnt(vehCount - 1);
                if (ls.getCashMoney() > 0) {//有打印发票,结束发票号未打印的最后一张发票
                    ls.setInvEndId(IntegerUtils.parseString(ticketNum == null ? "1" : ticketNum) - 1);
                } else {//未打印发票,起始于结束发票号相同
                    ls.setInvEndId(IntegerUtils.parseString(ticketNum == null ? "0" : ticketNum));
                }
                ls.setEndShiftFlag(1);
                LogControl.logInfo("产生工班日志：" + ls);
//                LaneListUtils.generationLaneShiftList(ls);
                ListControl.generateLaneShift(ls);
                tempCtrl.deleteTempShift();
                logout.setJobEnd(jobEndTime);
                logout.setTotalC(vehCount - 1);
//                LaneListUtils.generationLaneLogoutList(logout);
                ListControl.generateLaneLogout(logout);
                tempCtrl.deleteTempLogout();
                ls = null;
                onduty = false;
                staff = null;
                extJFrame.setFrameTitle(lane);
                deleteTempImg();//下班时删除临时图片
                sendInfo("");//已下班,清空上一次发送信息
                ticketNum = null;//下班后发票后为null
                trackNo = null;//下班后字轨号为null
                lastVehPlateNum = null;
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            LogControl.logInfo("下班异常", ex);
            return false;
        }
    }

    /**
     * 产生上班流水
     */
    private void generateLoginInfo() {
        try {
            LaneLogin login = new LaneLogin();
            login.setJobNum(staff.getShiftId());
            login.setJobstart(staff.getJobStartTime());
            login.setLane(lane.getLaneId());
            login.setLaneType(lane.getLaneType());
            login.setRoadId(ShortUtils.parseString(lane.getRoadId()));
            login.setStaff(staff.getStaffId());
            login.setStationId(ShortUtils.parseString(lane.getStationId()));
            login.setViolatInclose(violateClose);
//            LaneListUtils.generationLaneLoginList(login);
            ListControl.generateLaneLogin(login);
            LogControl.logInfo("生成上班流水：" + login);
        } catch (Exception ex) {
            LogControl.logInfo("生成上班流水时出现异常", ex);
        }
    }

    /**
     * 退出程序
     */
    public void exit() {
        try {
            LogControl.logInfo("自动退出软件");
            if (onduty) {
//                logout(LOGOUT_AUTO);//自动下班
                checkTempInfo();
                deleteTempImg();//下班时删除临时图片
            }
            amc.close();//自助发卡相应程序退出
            cxp.stopAlarm();//停止报警
            cxp.changeCanopyLightRed();//雨棚灯变红
            cxp.changeTrafficLightRed();//通行灯变红
            tfi.clearScreen();//费显清屏
            vi.exit();//字符叠加退出
//            mireader.exit();//天线退出
            m1Control.exit();//天线退出
            cpuCardSvc.closeCpuCardReader();//ETC卡读写器退出
        } catch (Exception ex) {
            LogControl.logInfo("自动退出异常", ex);
        }
        System.exit(0);
    }

    /**
     * 记录编码增长从1开始,最大为9999,9999之后从1开始
     */
    public void increaseRecordId() {
        if (recordId >= 9999) {
            recordId = 1;
        } else {
            recordId++;
        }
    }

    /**
     * 雨棚灯变红
     */
    public void runChangeCanopyRed() {
        cxp.changeCanopyLightRed();                                     //主界面雨棚灯变为红色
        extJFrame.setCanopyLightRed();
        LogControl.logInfo("雨棚灯红灯");
        canopyFlag = cxp.getCanopyFlag();
        sendInfo("雨棚灯红灯");
    }

    /**
     * 雨棚灯变绿
     */
    public void runChangeCanopyGreen() {
        cxp.changeCanopyLightGreen();                                     //主界面雨棚灯变为绿色
        extJFrame.setCanopyLightGreen();
        LogControl.logInfo("雨棚灯绿灯");
        canopyFlag = cxp.getCanopyFlag();
        sendInfo("雨棚灯绿灯");
    }

    /**
     * 处理临时下班信息
     */
    public void generateTempLogoutInfo() {
        Date jobEndTime = new Date();
        ls.setLogoutTime(jobEndTime);
//        ls.setListCnt(vehCount - 1);
//        ls.setVehCnt(vehCount - 1);
        ls.setListCnt(vehCount);//交易过程记录,处理车辆数量不必发生变化
        ls.setVehCnt(vehCount);
        if (ls.getCashMoney() > 0) {//有打印发票,结束发票号未打印的最后一张发票
            ls.setInvEndId(IntegerUtils.parseString(ticketNum == null ? "1" : ticketNum) - 1);
        } else {//未打印发票,起始于结束发票号相同
            ls.setInvEndId(IntegerUtils.parseString(ticketNum == null ? "0" : ticketNum));
        }
        ls.setEndShiftFlag(1);
        LogControl.logInfo("产生临时工班流水");
        tempCtrl.generateTempShift(ls);
        logout.setJobEnd(jobEndTime);
//        logout.setTotalC(vehCount - 1);
        logout.setTotalC(vehCount);
        LogControl.logInfo("产生临时下班流水");
        tempCtrl.generateTempLogout(logout);
    }

    /**
     * 删除临时图片
     */
    private void deleteTempImg() {
        try {
            File file = new File("temp/videoTemp.jpg");
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception ex) {
            LogControl.logInfo("删除临时图片异常", ex);
        }
        
    }

    /**
     * 等待确认无卡,残卡,不可读卡等
     *
     * @return 0 确认<br>
     * -2冲关车<br>
     * 其他取消
     */
    private int runConfirmNoCard() {
        extJFrame.updateInfo("等待确认收费数据", "按【确认】键来确认\n或\n按【取消】键来取消");
        String str;
        int i;
        while (true) {
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
            }
            synchronized (violateObj) {
                if (violationFlag) {
                    return -2;
                }
            }
            str = keyboard.getMessage();
            if (str == null) {
                continue;
            } else if (Keyboard.CONFIRM.equals(str)) {
                i = 0;
                break;
            } else if (Keyboard.CANCEL.equals(str)) {
                i = -1;
                return i;
            } else {
                keyboard.sendAlert();
            }
        }
        return i;
    }

    /**
     * 判断当前ETC卡上一次交易入口站是否为本站并且上一次交易时间是否在指定时间内
     *
     * @param cpuCard ETC卡
     * @param lane 当前收费站
     * @param interval 指定时间间隔（分钟）
     * @return true符合要求 false不符合要求
     */
    private boolean checkEtcExit(CpuCard cpuCard, Lane lane, int interval) {
        String roadid1 = cpuCard.getExitRoadId();
        String stationid1 = cpuCard.getExitStationId();
        String roadid2 = lane.getRoadId();
        String stationid2 = lane.getStationId();
        if (roadid1 == null || stationid1 == null || roadid2 == null || stationid2 == null) {
            return false;
        }
        if (roadid1.equals(roadid2) && (stationid1.equals(stationid2))) {//路段号相同,收费站相同
            Date date1 = cpuCard.getDhm();
            long time1 = 0;
            if (date1 != null) {
                time1 = date1.getTime();
            }
            long now = System.currentTimeMillis();
            if (now - time1 < interval * 60 * 1000) {//上一次读写卡时间与当前时间间隔在指定时间范围内
                return true;
            }
        }
        return false;
    }

    /**
     * 本站已交易过的ETC卡是否直接放行
     *
     * @return true收费员确认放行 false收费员拒绝放行
     */
    private boolean runEtcExitControl() {
        extJFrame.updateInfo("本站交易ETC卡检测", "检测到当前ETC卡已在本站收费\n按【确认】键放行\n或\n按【取消】键拒绝通过");
        String str;
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            str = keyboard.getMessage();
            if (str == null) {
                continue;
            } else if (Keyboard.CONFIRM.equals(str)) {
                return true;
            } else if (Keyboard.CANCEL.equals(str)) {
                return false;
            } else {
                keyboard.sendAlert();
            }
        }
    }

    /**
     * 目录检测
     */
    private void checkDir() {
        File file = new File("img");
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        file = new File("list");
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        file = new File("logs");
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        file = new File("temp");
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 选择代收通行费
     */
    private int getCollectMoney() {
//        extJFrame.showCollectionPanel();//显示代收面板
//        extJFrame.updateInfo("选择代收通行费", "请按数字键选择对应代收通行费并确认");
//        String str;
//        while (true) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException ex) {
//            }
//            str = keyboard.getMessage();
//            if (str == null) {
//                continue;
//            } else if (Keyboard.CONFIRM.equals(str)) {
//                break;
//            } else if (str.matches("[1-4]")) {
//                extJFrame.updateCollectButton(str);
//            } else {
//                keyboard.sendAlert();
//            }
//        }
//        extJFrame.hideCentralPanel();
//        int result = extJFrame.getCollectResult();
//        if (result == 0) {
//            extJFrame.updateAttachInfo(null);
//        } else {
//            extJFrame.updateAttachInfo("含代收:" + result + "元");
//        }
        LogControl.logInfo("选择代收通行费");
        int result = cc.getCollectMoney();
        LogControl.logInfo("代收通行费：" + result);
        sendInfo("代收通行费：" + result);
        extJFrame.updateFare(pay, result);
        tfi.showFareAndCollectFee(pay + result, result);
//        createTempCollectList();//记录临时代收流水
        return result;
    }

    /**
     * 生成代收流水
     */
    private void generateCollectList() {
        ColList colList = new ColList();
        try {
            if (!collectFlag) {//代收未启用
                return;
            }
            colList.setAxleWeight((int) DoubleUtils.mul(veh.getFareWeight(), 1000));
            colList.setAxleWeightLimit((int) DoubleUtils.mul(veh.getLimitWeight(), 1000));
            colList.setCarType(veh.getVehicleClass());
            colList.setChargeTime(card2.getDhm());
            colList.setCollMoney(collectFee);
            String enRoadid = card1.getRoadid();
            String enStationid = card1.getStationid();
            StringBuilder sb1 = new StringBuilder();
            if (enRoadid != null) {
                sb1.append(enRoadid);
            }
            if (enStationid != null) {
                sb1.append(enStationid);
            }
            colList.setInStationNo(sb1.toString());
            colList.setInStationName(mainSvc.getStationName(enRoadid, enStationid));
            colList.setLaneNo(lane.getLaneId());
            int laneType = lane.isEntry() ? 1 : 2;
            colList.setLaneType(laneType);
            colList.setListNo(StringUtils.getListNo(lane.getLaneId(), card2.getDhm(), recordId));
            colList.setRecordNo(recordId);
            colList.setOperatorName(staff.getStaffName());
            colList.setOperatorNo(staff.getStaffId());
            colList.setPassMoney((int) pay);
            int vehType = veh.getVehicleType();
            switch (vehType) {
                case 2://军警车
                    colList.setSpEvent(1);
                    break;
                case 4://绿通车
                    colList.setSpEvent(2);
                    break;
                default:
                    break;
            }
            if ("7".equals(FlowControl.chargeObserCode)) {//重打印发票
                colList.setSpEvent(4);
            }
            colList.setSquad(SquadUtils.getSquad(staff.getJobStartTime()));
            colList.setSquadNo(staff.getSquadId());
            colList.setSquadDate(staff.getSquadDate());
            colList.setStationName(lane.getStationName());
            colList.setStationNo(lane.getRoadId() + lane.getStationId());
            colList.setTructType(0);
            colList.setVehCnt(1);
            String priceVer = ParamCacheQuery.queryFareVer();//获取费率版本号
            if (priceVer != null && priceVer.matches("^[0-9]*$")) {//验证费率版本号是否是数字
                colList.setVerNo(IntegerUtils.parseString(priceVer));//费率版本号
            } else {
                colList.setVerNo(0);//费率版本号
            }
//            colList.setVerNo(1);
//            LaneListUtils.genColList(colList);
            ListControl.generateLaneColList(colList);
        } catch (Throwable t) {
            LogControl.logInfo("记录代收流水异常:" + colList, t);
        }
    }

    /**
     * 生成临时代收流水
     */
    private void generateTempCollectList() {
        ColList colList = new ColList();
        try {
            if (!collectFlag) {//代收未启用
                return;
            }
            colList.setAxleWeight((int) DoubleUtils.mul(veh.getFareWeight(), 1000));
            colList.setAxleWeightLimit((int) DoubleUtils.mul(veh.getLimitWeight(), 1000));
            colList.setCarType(veh.getVehicleClass());
            colList.setChargeTime(card2.getDhm());
            colList.setCollMoney(collectFee);
            colList.setInStationNo(card1.getRoadid() + card1.getStationid());
            colList.setInStationName(mainSvc.getStationName(card1.getRoadid(), card1.getStationid()));
            colList.setLaneNo(lane.getLaneId());
            int laneType = lane.isEntry() ? 1 : 2;
            colList.setLaneType(laneType);
            colList.setListNo(StringUtils.getListNo(lane.getLaneId(), card2.getDhm(), recordId));
            colList.setRecordNo(recordId);
            colList.setOperatorName(staff.getStaffName());
            colList.setOperatorNo(staff.getStaffId());
            colList.setPassMoney((int) pay);
            int vehType = veh.getVehicleType();
            switch (vehType) {
                case 2://军警车
                    colList.setSpEvent(1);
                    break;
                case 4://绿通车
                    colList.setSpEvent(2);
                    break;
                default:
                    break;
            }
            if ("7".equals(FlowControl.chargeObserCode)) {//重打印发票
                colList.setSpEvent(4);
            }
            colList.setSquad(SquadUtils.getSquad(staff.getJobStartTime()));
            colList.setSquadNo(staff.getSquadId());
            colList.setSquadDate(staff.getSquadDate());
            colList.setStationName(lane.getStationName());
            colList.setStationNo(lane.getRoadId() + lane.getStationId());
            colList.setTructType(0);
            colList.setVehCnt(1);
            String priceVer = ParamCacheQuery.queryFareVer();//获取费率版本号
            if (priceVer != null && priceVer.matches("^[0-9]*$")) {//验证费率版本号是否是数字
                colList.setVerNo(IntegerUtils.parseString(priceVer));//费率版本号
            } else {
                colList.setVerNo(0);//费率版本号
            }
//            colList.setVerNo(1);
            tempCtrl.generateTempColList(colList);
        } catch (Throwable t) {
            LogControl.logInfo("记录临时代收流水异常:" + colList, t);
        }
    }

    /**
     * 清空临时代收流水
     */
    private void deleteTempCollectList() {
        tempCtrl.deleteTempCollist();
    }

    /**
     * 入口代收通行费
     */
    private void runGetEnCollectFee() {
        if (!lane.isEntry()) {//当前车道非入口
            return;
        }
        extJFrame.updateInfo("接受付款", "接受付款");
        extJFrame.updateFare(pay, collectFee);
        String str;
        while (true) {
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
            }
            str = keyboard.getMessage();
            if (str == null) {
                continue;
            } else if (str.equals(Keyboard.CONFIRM)) {
                if (collectFee > 0) {
                    extJFrame.updateMop("01");
                }
                break;
            } else if (str.equals(Keyboard.SIMU)) {
                char temp = simControl.runSimulate(0);
                switch (temp) {
                    case '0'://取消更改
                        break;
                    case '5':
                        collectFee = getCollectMoney();//重新获取代收通行费
                        runAudioFare();//更改代收后重新语音报价
                        extJFrame.updateInfo("接受付款", "接受付款");
                        break;
                    default:
                        break;
                }
            } else {
                keyboard.sendAlert();
            }
        }
    }
    private long t1;//费率升级文件异常的提示时间
    private final InfoJFrame info = new InfoJFrame();//费率升级文件异常的提示界面

    /**
     * 确认收费参数异常
     */
    public void runConfirmFareParamError() {
//        try {
//            if (lane.isEntry()) {//入口不需要校验收费参数异常情况
//                return;
//            }
//            Vector<Object> vec = RateParamErrorUtils.errors;
//            if (vec == null || vec.isEmpty()) {
//                return;
//            }
//            StringBuilder sb = new StringBuilder();
//            for (Object o : vec) {
//                Vector v = (Vector) o;
//                sb.append(v.get(2));
//            }
//            info.updateAlertInfo(sb.toString());
//            //获取参数文件中配置的时间间隔
//            String paramAlertInterval = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "paramAlertInterval", "5");
//            int interval = IntegerUtils.parseString(paramAlertInterval);
//            if (interval <= 0) {
//                interval = 5;
//            }
//            long t2 = System.currentTimeMillis();
//            if (t2 < t1) {//当前时间小于开始时间,重新开始计时
//                t1 = t2;
//            }
//            if (t2 - t1 < interval * 60 * 1000) {//上一次提示距现在间隔时间短于规定时间
//                return;
//            }
//            LogControl.logInfo("费率升级文件异常");
//            info.updateAlertInterval(interval);
//            info.init();//显示提示界面
//            info.setAlwaysOnTop(true);
//            extJFrame.setAlwaysOnTop(false);
//            extJFrame.requestFocus();
//            String str;
//            while (true) {
//                Thread.sleep(10);
//                if (violationFlag) {//冲关车
//                    return;
//                }
//                str = keyboard.getMessage();
//                if (RateParamErrorUtils.errors == null || RateParamErrorUtils.errors.isEmpty()) {
//                    //费率异常已消失
//                    str = Keyboard.CANCEL;
//                }
//                if (str == null) {
//                } else if (Keyboard.CANCEL.equals(str)) {
//                    t1 = System.currentTimeMillis();//更新提示时间
//                    info.setVisible(false);//隐藏提示界面
////                    extJFrame.setAlwaysOnTop(true);
//                    extJFrame.showOnTop();
//                    break;
//                } else {
//                    keyboard.sendAlert();
//                }
//            }
//        } catch (Exception ex) {
//            LogControl.logInfo("确认收费参数异常出现异常", ex);
//        }
    }

    /**
     * 入口为虚拟站最远站时,需要按模拟键解锁
     */
    private void checkVirtualStation() {
        if (lane.isExit()) {
            if (card1.getRoadid().equals("99")) {
                if (card1.getStationid().equals("97") || card1.getStationid().equals("94")) {
                    runGetFare();
                    while (true) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                        }
                        extJFrame.updateInfo("请按【模拟】键确认入口信息", "当前入口站为最远站\n请按【模拟】键确认入口信息\n然后再进行其他操作");
                        String temp = keyboard.waitForOrder();
                        if (temp.equals(Keyboard.SIMU)) {
                            break;
                        } else {
                            keyboard.sendAlert();
                        }
                    }
                }
            }
        }
        
    }

    /**
     * 更改发卡模式
     */
    private void runChangeRunningMode() {
        if (!lane.isEntry()) {//仅限于入口
            return;
        }
        if (!FunctionControl.isAutoMachineActive()) {//未启用自助发卡功能
            return;
        }
        RunningModeJFrame lmj = RunningModeJFrame.getSingleInstance();
        lmj.setVisible(true);
        extJFrame.setAlwaysOnTop(false);
        extJFrame.requestFocus();
        extJFrame.setAutoRequestFocus(true);
        String str;
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            synchronized (violateObj) {
                if (violationFlag) {//冲关车
                    break;
                }
            }
            str = keyboard.getMessage();
            if (str == null) {
                continue;
            } else if ("1".equals(str)) {//自助发卡
                LogControl.logInfo("自助发卡");
                sendInfo("自助发卡");
                amc.setActivated(true);
                extJFrame.updateRunningMode(ExtJFrame.Auto);
                card = null;//人工发卡转自助发卡时,若人工发卡出现已发通行卡再刷湘通卡但通行卡未使用即转为自助发卡,此时需要将此通行卡信息清空
                break;
            } else if ("2".equals(str)) {//人工发卡
                LogControl.logInfo("人工发卡");
                sendInfo("人工发卡");
                amc.setActivated(false);
                extJFrame.updateRunningMode(ExtJFrame.Manual);
                break;
            } else if (Keyboard.CANCEL.equals(str)) {
                break;
            } else {
                keyboard.sendAlert();
            }
        }
        lmj.dispose();
//        extJFrame.setAlwaysOnTop(true);
        extJFrame.showOnTop();
    }

    /**
     * 记录ETC卡写卡前和写卡后的0019文件到临时文件中
     *
     * @param before0019 写卡前的0019文件
     * @param after0019 写卡后的0019文件
     */
    private void generateTempETCInfo(String before0019, String after0019) {
        StringBuilder sb = new StringBuilder();
        String split = "\t";
        sb.append(before0019).append(split).append(after0019);
        tempCtrl.generateTempEtcInfo(sb.toString());
    }

    /**
     * 显示路径明细
     */
    private void showRtpInfo() {
        if (pathDetailSerial != 0) {
            RtpControl rtpControl = RtpControl.getSingleInstance();
            Map<String, String> map = rtpControl.getPathDetail(piSerial);
            map.put("test", "http://192.168.1.1/pic/stest.jpg");
            Set<String> set = map.keySet();
            List<String> list = new ArrayList(set);
            int startIndex = 0;
            int index = 1;
            extJFrame.showRtpPanel(list, startIndex, startIndex + 9);
            extJFrame.selectRtpPanel(index);
            extJFrame.updateInfo("路径信息", "按数字键选择识别点\n按【确认】键查看图片\n按【取消】键返回\n按【↓】键翻页");
            boolean imgFlag = false;
            String str;
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                }
                str = keyboard.getMessage();
                if (null == str) {
                    
                } else if (Keyboard.CONFIRM.equals(str)) {//确认
                    imgFlag = true;//显示图片
                    int i = startIndex + index - 1;
                    if (i > list.size()) {
                        continue;
                    }
                    String key = list.get(i);
                    String urlPath = map.get(key);
                    extJFrame.showRtpImgPanel(key, urlPath);
                    extJFrame.updateInfo("识别点图片", "识别点图片\n按【取消】键回到路径信息列表");
                } else if (Keyboard.CANCEL.equals(str)) {//取消
                    if (imgFlag) {//显示图片后取消,返回识别点信息界面
                        extJFrame.updateInfo("路径信息", "按数字键选择识别点\n按【确认】键查看图片\n按【取消】键返回\n按【↓】键翻页");
                        imgFlag = false;
                        extJFrame.showRtpPanel(list, startIndex, startIndex + 9);
                        extJFrame.selectRtpPanel(index);
                    } else {
                        break;
                    }
                } else if (Keyboard.DOWN.equals(str)) {//向下翻页
                    startIndex += 10;
                    if (startIndex >= list.size()) {//超过信息范围,从头开始
                        startIndex = 0;
                    }
                    extJFrame.showRtpPanel(list, startIndex, startIndex + 9);
                } else if (str.matches(Constant.REGIX_NUM)) {//选择数字
                    int i = startIndex + index - 1;
                    if (i > list.size()) {
                        continue;
                    }
                    index = IntegerUtils.parseString(str);
                    extJFrame.selectRtpPanel(index);
                } else {
                    keyboard.sendAlert();
                }
            }
        }
    }

    /**
     * 显示入口信息或者显示实际路径信息
     *
     * @param card
     */
    private void runShowPathInfo(Card card) {
        pathDetailSerial = 1;
        String realTimeRoadFlag = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_RTP, "realTimeRoadFlag", "0");//获取路径识别启用标识
        if ("1".equals(realTimeRoadFlag) && pathDetailSerial != 0) {//启用路径识别功能
            showRtpInfo();
            extJFrame.updateInfo("等待确认入口信息", "按【确认】键以确认入口数据\n\n" + path);
            extJFrame.showCardAndWeight();
        } else {
            mainSvc.runShowEntryInfo(card);
        }
    }

    /**
     * 检验卡加密信息
     *
     * @param card 需要校验的通行卡
     * @return 0 校验通过<br>
     * 1 未加密通行卡<br>
     * -1 校验失败
     */
    private int checkEncrypteMsg(Card card) {
        if (card == null) {
            return -1;
        }
        String sb5 = card.getSection_05_info();
        String sb6 = card.getSection_06_info();
        String sb8 = card.getSection_08_info();
        String sb9 = card.getSection_09_info();
        if (sb5 == null || sb6 == null || sb8 == null || sb9 == null) {
            return -1;
        }
        if (sb5.length() != 32 || sb6.length() != 32 || sb8.length() != 32 || sb9.length() != 32) {
            return -1;
        }
        if (!sb9.toUpperCase().startsWith("A")) {//未加密卡
            return 1;
        }
        EncryptService svc = new EncryptService();
        String str = sb5 + sb6 + sb8;
        String encryptInfo = sb9.substring(1, 17);
        boolean flag = svc.checkEncryption(str, encryptInfo);
        if (flag) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 卡加密错误处理流程
     *
     * @return true同意使用 false禁止使用
     */
    private boolean runEncryptError() {
        if (!FunctionControl.isCodeErrorFunActive()) {//不进行卡加密错误处理
            return true;
        }
        extJFrame.updateInfo("通行卡信息加密错误", "通行卡信息加密错误\n按【模拟】键继续使用\n按【取消】键拒绝使用");
        String str;
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            str = keyboard.getMessage();
            if (str == null) {
                
            } else if (Keyboard.SIMU.equals(str)) {
                return true;
            } else if (Keyboard.CANCEL.equals(str)) {
                return false;
            } else {
                keyboard.sendAlert();
            }
        }
    }

    /**
     * 未加密通行卡处理流程
     *
     * @return true允许使用<br>
     * false 拒绝使用
     */
    private boolean runUnEncryptCard() {
        if (!FunctionControl.isUnCodeFunActive()) {//不进行通行卡未加密处理
            return true;
        }
        extJFrame.updateInfo("未加密通行卡", "未加密通行卡\n按【模拟】键继续交易\n按【取消】键拒绝使用");
        String str;
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            str = keyboard.getMessage();
            if (str == null) {
                
            } else if (Keyboard.SIMU.equals(str)) {
                return true;
            } else if (Keyboard.CANCEL.equals(str)) {
                return false;
            } else {
                keyboard.sendAlert();
            }
        }
    }
    
    private String lastVehPlateNum;//上一辆车的车牌号码(用于自助发卡车道根据车牌号码放倒车取卡功能)

    /**
     * 自助取卡车道根据连续两辆车的车牌号码判断是否倒车取卡
     */
    private void runAntiBack() {
        if (!FunctionControl.isAntiVehBackActive()) {//未启用防倒车取卡功能
            return;
        }
        if (!lane.isEntry() || !amc.isActivated()) {
            return;//仅用于入口自助发卡
        }
        if (veh == null) {//无车辆信息
            return;
        }
        String plateNum = veh.getFullVehPlateNum();
        if (plateNum == null) {//无车牌信息
            return;
        }
        if (plateNum.equals(lastVehPlateNum)) {
            LogControl.logInfo("发现本次交易车牌号码" + plateNum + "与上一次交易车牌号码相同,锁定收费界面");
            sendInfo("车道已锁定");
            extJFrame.updateInfo("", "系统已锁定，请按【模拟】键解锁");
            extJFrame.hideCentralPanel();
            cxp.warningAlarm();
            String str;
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FlowControl.class.getName()).log(Level.SEVERE, null, ex);
                }
                str = keyboard.getMessage();
                if (str == null) {
                    continue;
                } else if (Keyboard.SIMU.equals(str)) {//按模拟键解锁
                    break;
                } else {
                    keyboard.sendAlert();
                }
            }
            cxp.stopAlarm();
        }
    }

    /**
     * 人工发卡发现坏卡时的处理（用于兼容不同厂商的自助发卡机）
     */
    private void runManualAutoMode() {
        if (!lane.isEntry()) {//仅用于入口
            return;
        }
        String manualAutoLevel = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "manualAutoLevel", "0");
        switch (manualAutoLevel) {
            case "0": {
                try {
                    //郎为模式
                    cicm.frontBoltRaise();//前卡栓上升，防止收费员将坏卡放入卡箱
                } catch (Exception ex) {
                    LogControl.logInfo(path, ex);
                }
            }
            break;
            case "1"://优创模式
                break;
            default:
                break;
        }
    }

    /**
     * 获取雨棚灯的状态
     *
     * @return
     */
    public static int getCanopyFlag() {
        return canopyFlag;
    }
    
    public AvcControl getAvcControl() {
        return avcControl;
    }
    
    public void setAvcControl(AvcControl avcControl) {
        this.avcControl = avcControl;
    }

    /**
     * 是否为西埃斯的自助卡机模拟人工卡机方式(永蓝及邵永路段特殊处理)
     *
     * @return
     */
    private boolean isXasCICM() {
        String manualAutoFunc = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "manualAutoFunction", "");
        String manualAutoLevel = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "manualAutoLevel", "");
        if ("0".equals(manualAutoFunc) && "2".equals(manualAutoLevel)) {
            return true;
        }
        return false;
    }

    /**
     * 根据中文车型返回数字车型，例如一型返回1； 其他内容返回自身
     *
     * @param chClass 中文车型
     * @return 数字车型
     */
    private String transVehClassByCh(String chClass) {
        if (!FunctionControl.isFullKeyboardFunActive()) {//未启用全键盘功能
            return chClass;
        }
        if ("1型".equals(chClass)) {
            return "1";
        } else if ("2型".equals(chClass)) {
            return "2";
        } else if ("3型".equals(chClass)) {
            return "3";
        } else if ("4型".equals(chClass)) {
            return "4";
        } else if ("5型".equals(chClass)) {
            return "5";
        } else if ("6型".equals(chClass)) {
            return "6";
        } else if ("7型".equals(chClass)) {
            return "7";
        } else if ("8型".equals(chClass)) {
            return "8";
        } else if ("9型".equals(chClass)) {
            return "9";
        } else {
            return chClass;
        }
    }

    /**
     * 进行公务卡付费检测流程
     *
     * @return 公务卡,否则返回null
     */
    private ServiceCard runServiceCard() {
        if (exitTicketType == 2) {//ETC卡无公务卡付费流程
            return null;
        }
        if (!lane.isExit()) {
            return null;
        }
        
        String trEnRoadid = trafficEnRoadid;
        String trEnStationid = trafficEnStationid;
        String enRoadid = card1.getRoadid();
        String enStationid = card1.getStationid();
        String exRoadid = lane.getRoadId();
        String exStationid = lane.getStationId();
        String plateNum = veh.getFullVehPlateNum();
        String cardStatus = card1.getStatus();
        boolean flag1 = Constant.TRANSIT_CSC_STATUS_01.equals(cardStatus);//已发卡
        boolean flag2 = Constant.TRANSIT_CSC_STATUS_02.equals(cardStatus);//已发预编码卡
        boolean flag3 = Constant.TRANSIT_CSC_STATUS_03.equals(cardStatus);//已发维修卡
        boolean flag6 = Constant.TRANSIT_CSC_STATUS_06.equals(cardStatus);//预编码卡
        if (!(flag1 || flag2 || flag3 || flag6)) {//卡状态异常
            trEnRoadid = enRoadid;
            trEnStationid = enStationid;
        }
        return ServiceCardControl.getSingleInstance().runServiceCard(trEnRoadid, trEnStationid, enRoadid, enStationid, exRoadid, exStationid, plateNum);
    }
}
