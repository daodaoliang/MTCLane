package com.hgits.control;

import am.Acm;
import am.Avc;
import automachine.AutoMachine;
import com.hgits.hardware.CXP;
import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;
import com.hgits.vo.Lane;
import org.apache.log4j.Logger;
import ui.ExtJFrame;

/**
 * 自助发卡机控制类
 *
 * @author Wang Guodong
 */
public class AutoMachineControl {

    private boolean autoFlag;//自助发卡机启用标识，用于记录配置文件中是否配置了启用自助发卡标识
    private Avc avc;//车型分类器
    private Acm acm;//自助发卡机
    private AudioControl fa;//语音提示
    private String avcError = "";//车型分类器异常
    private String acmError = "";//自助发卡机异常
    private static AutoMachineControl amc;//唯一实例化对象

    /**
     * 获取唯一实例化对象
     *
     * @return 唯一实例化对象
     */
    public static synchronized AutoMachineControl getSingleInstance() {
        if (amc == null) {
            amc = new AutoMachineControl();
        }
        return amc;
    }

    private AutoMachineControl() {
        init();
    }

    public void setAutoFlag(boolean autoFlag) {
        this.autoFlag = autoFlag;
    }

    /**
     * 初始化
     */
    private void init() {
        loadConfig();
        if (autoFlag) {
            initDevice();
        }
    }

    /**
     * 加载配置文件,判断是否启用自助发卡
     *
     */
    public void loadConfig() {
        if (!Lane.getInstance().isEntry()) {//非入口，不启用自助发卡
            autoFlag = false;
            return;
        }
        String auto = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "autoMachineFlag", "0");
        if (null != auto) {
            switch (auto) {
                case "0":
                    LogControl.logInfo("不启用自助发卡");
                    autoFlag = false;
                    break;
                case "1":
                    LogControl.logInfo("启用自助发卡");
                    autoFlag = true;
                    break;
                default:
                    autoFlag = false;
                    break;
            }
        }

    }

    /**
     * 设备初始化
     */
    private void initDevice() {
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            //该线程用于监控自助卡机卡是否已发出以及卡是否已取走
            //卡已发出并且未取走，播放语音“请取卡”，5秒后若卡仍未取走，再次播放语音“请取卡”
            //卡取走后，播放语音“谢谢，一路顺风”，20秒后若仍然为卡被取走的状态，播放语音“谢谢，一路顺风”（车辆离开后需要将卡被取走设为未被取走状态）
            @Override
            public void run() {
                fa = AudioControl.getSingleInstance();
                String version = AutoMachine.getVersion();
                TempControl tempSvc = TempControl.getSingleInstance();
                tempSvc.generateTempVersion("AutoMachine.ver", version);
                String avcInfo = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_COMM, "AVC", null);//获取车型分类器的串口信息
                if (avcInfo == null) {
                    LogControl.logInfo("启用自助发卡，但车型分类器配置文件异常");
                    if (autoFlag) {
                        avcError = "车型分类器未配置";
                        throw new RuntimeException("启用自助发卡，但车型分类器配置文件异常");
                    }
                } else {
                    String[] buffer = avcInfo.split(",");
                    String portName = buffer[0].trim();//串口号
                    int baudRate = IntegerUtils.parseString(buffer[1].trim());//波特率
                    int dataBits = IntegerUtils.parseString(buffer[2].trim());//数据位
                    int stopBits = IntegerUtils.parseString(buffer[3].trim());//停止位
                    int parity = IntegerUtils.parseString(buffer[4].trim());//校验位
                    int logLevel = IntegerUtils.parseString(buffer[6].trim());//日志级别
                    avc = new Avc();
                    avc.init(portName, baudRate, dataBits, stopBits, parity, logLevel);
                }
                String acmInfo = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_COMM, "ACM", null);//获取自助发卡机的串口信息
                if (acmInfo == null) {
                    LogControl.logInfo("启用自助发卡，但配置文件中未配置自助发卡机");
                    if (autoFlag) {
                        acmError = "自助发卡机未配置";
                        throw new RuntimeException("启用自助发卡，但自助发卡机配置文件异常");
                    }
                } else {
                    String[] buffer = acmInfo.split(",");
                    String portName = buffer[0].trim();//串口号
                    int baudRate = IntegerUtils.parseString(buffer[1].trim());//波特率
                    int dataBits = IntegerUtils.parseString(buffer[2].trim());//数据位
                    int stopBits = IntegerUtils.parseString(buffer[3].trim());//停止位
                    int parity = IntegerUtils.parseString(buffer[4].trim());//校验位
                    int logLevel = IntegerUtils.parseString(buffer[6].trim());//日志级别
                    acm = new Acm();
                    acm.init(portName, baudRate, dataBits, stopBits, parity, logLevel);
                }
                //请取卡语音提示时间间隔
                String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "autoAudioInterval1", "5");
                int autoAudioInterval1 = IntegerUtils.parseString(str);
                if (autoAudioInterval1 < 1) {
                    autoAudioInterval1 = 1;
                }
                //谢谢一路顺风语音提示时间间隔
                str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "autoAudioInterval2", "10");
                int autoAudioInterval2 = IntegerUtils.parseString(str);
                if (autoAudioInterval2 < 1) {
                    autoAudioInterval2 = 1;
                }
                while (true) {
                    try {
                        Thread.sleep(100);
                        if (isCardOut() && !isCardTaken()) {//卡已发出,但尚未被取走
                            long now = System.currentTimeMillis();
                            if (now < start1) {//当前时间小于开始时间，重新开始计时
                                start1 = now;
                            }
                            if (now - start1 > autoAudioInterval1 * 1000) {
                                audioTakeCard();//播放语音请取卡
                                start1 = System.currentTimeMillis();
                            }
                        }
                        if (isCardTaken()) {//卡已被取走
                            long now = System.currentTimeMillis();
                            if (now < start2) {//当前时间小于开始时间，重新开始计时
                                start2 = now;
                            }
                            if (now - start2 > autoAudioInterval2 * 1000) {
                                audioThanks();//播放语音谢谢一路顺风
                                start2 = System.currentTimeMillis();
                            }
                        }
                        int i = getVehCnt();
                        ExtJFrame.getSingleInstance().updateWaitVehCount(i + "");
                    } catch (Exception ex) {
                        LogControl.logInfo("自助卡机语音控制异常", ex);
                    }
                }
            }
        });
    }
    long start1 = 0;//“请取卡”语音提示的时间标志
    long start2 = 0;//“谢谢一路顺风”语音提示的时间标志

    /**
     * 当前是否为自助发卡
     *
     * @return true 是；false 否
     */
    public boolean isActivated() {
        return autoFlag;
    }

    /**
     * 设置自助发卡
     *
     * @param flag true 是；false 否
     */
    public void setActivated(boolean flag) {
        autoFlag = flag;
    }

    /**
     * 配置文件中是否配置了自助发卡
     *
     * @return true 是；false 否
     */
    public boolean isAutoSet() {
        return autoFlag;
    }

    /**
     * 获取车型识别设备识别车型（并且删除当前车辆信息）
     *
     * @return 0表示无结果
     */
    public int getVehClass() {
        if (!autoFlag || avc == null) {
            return 0;
        }
        return avc.getVehClass();
    }

    /**
     * 获取自助发卡异常信息
     *
     * @return 自助发卡异常
     */
    public String getErrorMsg() {
        StringBuilder sb = new StringBuilder();
        sb.append(avcError).append(acmError);
        if (acm != null) {//优先判断自助发卡机的异常
            String msg1 = acm.getErrorMsg();
            if (msg1 != null && !msg1.trim().isEmpty()) {
                sb.append(msg1).append(" ");
            }
        }
        if (avc != null) {//车型分类器的异常
            String msg2 = avc.getErrorMsg();
            if (msg2 != null && !msg2.trim().isEmpty()) {
                sb.append(msg2);
            }
        }
        return sb.toString();
    }

    /**
     * 播放语音“请按键取卡”
     */
    public void audioPressKey() {
        try {
            if (fa != null) {
                fa.playAudioPressKey();
            }
        } catch (Exception ex) {
            Logger.getLogger(AutoMachineControl.class).error(ex);
        }

    }

    /**
     * 播放语音“请稍候”
     */
    public void audioWait() {
        try {
            if (fa != null) {
                fa.playAudioWait();
            }
        } catch (Exception ex) {
            Logger.getLogger(AutoMachineControl.class).error(ex);
        }
    }

    /**
     * 播放语音“请取卡”
     */
    public void audioTakeCard() {
        try {
            if (fa != null) {
                fa.playAudioTakeCard();
            }
        } catch (Exception ex) {
            Logger.getLogger(AutoMachineControl.class).error(ex);
        }

    }

    /**
     * 播放语音“谢谢，一路顺风”
     */
    public void audioThanks() {
        try {
            if (fa != null) {
                fa.playAudioThanks();
            }
        } catch (Exception ex) {
            Logger.getLogger(AutoMachineControl.class).error(ex);
        }

    }

    /**
     * 设置自助卡机按键
     *
     * @param flag true已按键 false未按键
     */
    public void setKeyPressed(boolean flag) {
        if (acm != null) {
            acm.setKeyPressed(flag);
        }

    }

    /**
     * 是否已按键（按键取卡）
     *
     * @return true 已按键 false未按键
     */
    public boolean isKeyPressed() {
        if (acm != null) {
            return acm.isKeyPressed();
        } else {
            return false;
        }
    }

    /**
     * 是否已出卡
     *
     * @return true已出卡 false未出卡
     */
    public boolean isCardOut() {
        if (acm != null) {
            return acm.isCardOut();
        } else {
            return false;
        }
    }

    /**
     * 卡是否已被取走
     *
     * @return true卡已被取走 false卡未被取走
     */
    public boolean isCardTaken() {
        if (acm != null) {
            return acm.isCardTaken();
        } else {
            return false;
        }
    }

    /**
     * 自助发卡机将卡从天线发放到出卡位置
     */
    public void sendCard() {
        if (acm != null) {
            acm.sendCard();
        }
    }

    /**
     * 坏卡
     */
    private void sendBadCard() {
        if (acm != null) {
            acm.sendBadCard();
        }
    }

    /**
     * 关闭自助发卡机
     */
    public void close() {
        if (acm != null) {
            acm.close();
        }
        if (avc != null) {
            avc.close();
        }
    }

    /**
     * 判断当前车辆是否倒车
     *
     * @return true当前车辆倒车 false当前车辆未倒车
     */
    boolean isVehBackOff() {
        if (avc == null) {
            return false;
        } else {
            return avc.isVehBackOff();
        }

    }

    /**
     * 交易初始化
     */
    public void initTransaction() {
        setKeyPressed(false);
        if (acm != null) {
            acm.initTransaction();
        }
        start1 = 0;//初始化请取卡语音提示的时间标志
        start2 = 0;//初始化一路顺风语音提示的时间标志
    }

    /**
     * 移除车型分类器中记录的第一辆车
     */
    public void removeFirstVeh() {
        if (avc != null) {
            avc.removeFirstVeh();
        }
    }

    /**
     * 获取车型分类器待处理的车辆数量
     *
     * @return 待处理的车辆数量
     */
    public int getVehCnt() {
        int cnt = 0;
        if (avc != null) {
            cnt = avc.getVehCnt();
        }
        return cnt;
    }

    /**
     * 设置当前车辆倒车标识
     *
     * @param flag true是 false否
     */
    public void setVehBack(boolean flag) {
        if (avc != null) {
            avc.setVehBack(flag);
        }
    }

    /**
     * 执行坏卡处理流程
     */
    public void runBadCard() {
        ExtJFrame.getSingleInstance().updateInfo("发现坏卡", "发现坏卡，等待自助卡机自动更换坏卡");
        LogControl.logInfo("发现坏卡，等待自助卡机自动更换坏卡");
        sendBadCard();//发送坏卡指令
        long start = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(10);
                long now = System.currentTimeMillis();
                if (now < start) {//当前时间小于开始时间，重新开始计时
                    start = now;
                }
                if (now - start > 5000) {
                    LogControl.logInfo("发送坏卡指令后等待5秒未收到按键指令，自动退出等待按键指令");
                    break;
                }
                if (isKeyPressed()) {
                    setKeyPressed(false);
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
    }

}
