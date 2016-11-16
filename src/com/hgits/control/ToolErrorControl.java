package com.hgits.control;

import java.util.HashMap;
import java.util.Map;

import ui.ExtJFrame;

import com.hgits.common.log.MTCLog;
import com.hgits.hardware.CICM;
import com.hgits.hardware.CXP;
import com.hgits.hardware.CpuCardReader;
import com.hgits.hardware.IcReaderWriter;
import com.hgits.hardware.Keyboard;
import com.hgits.hardware.LprService;
import com.hgits.hardware.Printer;
import com.hgits.hardware.TFI;
import com.hgits.hardware.VBD;
import com.hgits.hardware.VideoInstruction;
import com.hgits.hardware.WeighSystem;
import com.hgits.util.DeviceErrorCacheUtils;
import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;
import com.hgits.vo.Lane;
import com.hgits.vo.Staff;

/**
 * 获取硬件异常并且在界面上显示
 *
 * @author Wang Guodong
 */
public class ToolErrorControl implements Runnable {

    private final ExtJFrame extJFrame;//收费界面
    private final Keyboard keyboard;//收费键盘
    private final IcReaderWriter mireader;//IC卡读卡器
    private final CXP cxp;//CXP板
    private final TFI tfi;//费显
    private final Printer printer;//打印机
    private final CICM cicm;//卡机
    private final WeighSystem weighSystem;//计重系统
    private final CpuCardReader cpuCardReader;//ETC卡读卡器
    private final LprService hvLPR;//车牌识别系统
    private VideoInstruction vi;//字符叠加设备
//    private AntiBackControl abc;//防盗车取卡控制
    private final CartControl cartControl;//卡箱管理
    private FlowControl fc;
    private AutoMachineControl amcControl;//自助发卡设备管理
    private static final String CART_NAME = "卡机";
    private static final String TFI_NAME = "费额显示器";
    private static final String ETC_NAME = "ETC卡读写器";
    private static final String LPR_NAME = "车牌识别设备";
    private static final String KEYBOARD_NAME = "收费键盘";
    private static final String WEIGH_NAME = "称重系统";
    private static final String PRINTER_NAME = "票据打印机";
    private static final String CXP_NAME = "连接板";
    private static final String MIREADER_NAME = "收发卡机天线";
    private static final String CICM_NAME = "收发卡机";
    private static final String AMC_NAME = "自助发卡设备";
    private static final String VI_NAME = "字符叠加设备";
    private static final String VBD_NAME = "防盗车取卡设备";

    public void setFc(FlowControl fc) {
        this.fc = fc;
    }

    public void setVi(VideoInstruction vi) {
        this.vi = vi;
    }

    public void setAmcControl(AutoMachineControl amcControl) {
        this.amcControl = amcControl;
    }

//    public void setAbc(AntiBackControl abc) {
//        this.abc = abc;
//    }

    public ToolErrorControl(ExtJFrame extJFrame, Keyboard keyboard, IcReaderWriter mireader, CXP cxp, TFI tfi, Printer printer, CICM cicm, WeighSystem weighSystem, CpuCardReader cpuCardReader, LprService hvLPR, CartControl cartControl) {
        this.extJFrame = extJFrame;
        this.keyboard = keyboard;
        this.mireader = mireader;
        this.cxp = cxp;
        this.tfi = tfi;
        this.printer = printer;
        this.cicm = cicm;
        this.weighSystem = weighSystem;
        this.cpuCardReader = cpuCardReader;
        this.hvLPR = hvLPR;
        this.cartControl = cartControl;
    }

    @Override
    public void run() {
        String str;
        int i = 0;
        while (true) {
            try {
                String interval = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "tollErrorInterval", "3");
                int time = IntegerUtils.parseString(interval);
                if (time < 1) {
                    time = 1;//间隔时间最短1秒
                }
                Thread.sleep(time * 1000);
                if (staff == null) {
                    staff = fc.getStaff();
                }
                checkVi();//检测视频字符叠加设备
                checkLPR();//检测车牌识别设备
                checkTFI();//检测费显
                checkPrinter();//检测打印机
                checkWeighSys();//检测计重系统
                checkCpuReader();//检测ETC卡读写器
                checkCart();//检测卡箱
                checkKeyboard();//检测收费键盘
                checkCicm();//检测卡机
                checkCxp();//检测CXP板
                checkMiReader();//检测天线
                checkAMC();//检测自助发卡设备异常
//                checkVBD();//检测防盗车取卡设备异常
                str = map.get(MIREADER_NAME);
                str = str == null ? map.get(CXP_NAME) : str;
                str = str == null ? map.get(CICM_NAME) : str;
                str = str == null ? map.get(KEYBOARD_NAME) : str;
                str = str == null ? map.get(AMC_NAME) : str;
                str = str == null ? map.get(CART_NAME) : str;
                str = str == null ? map.get(ETC_NAME) : str;
                str = str == null ? map.get(WEIGH_NAME) : str;
                str = str == null ? map.get(PRINTER_NAME) : str;
                str = str == null ? map.get(TFI_NAME) : str;
                str = str == null ? map.get(LPR_NAME) : str;
                str = str == null ? map.get(VI_NAME) : str;
                str = str == null ? map.get(VBD_NAME) : str;
                if (str != null && !"".equals(str)) {
                    String desc = AlarmControl.getDesc(str);
                    extJFrame.updateToolError(desc);
//                    fc.sendAlarmInfo(desc, 1);
                } else {
                    extJFrame.initToolError();
                }
            } catch (NumberFormatException | InterruptedException ex) {
                LogControl.logInfo("异常获取线程出现异常", ex);
            }
        }
    }

    Lane lane = Lane.getInstance();
    Staff staff;
    Map<String, String> map = new HashMap();

    /**
     * 检测卡箱异常信息
     */
    public void checkCart() {
        String device = CART_NAME;
        String str = cartControl.getErrorMsg();//设备新产生的异常（为null表示无异常）
        String str1 = map.get(device);//缓存记录的设备原来的异常（为null表示无异常）
        recordError(device, str, str1);
    }

    /**
     * 检测ETC卡读写器异常信息
     */
    public void checkCpuReader() {
        String device = ETC_NAME;
        String str = cpuCardReader.getErrorMsg();//硬件异常
        String str1 = map.get(device);//缓存中记录的异常
        recordError(device, str, str1);
    }

    /**
     * 检测费额显示器异常信息
     */
    public void checkTFI() {
        String device = TFI_NAME;
        String str = tfi.getErrorMsg();//硬件异常
        String str1 = map.get(device);//缓存中记录的异常
        recordError(device, str, str1);
    }

    /**
     * 检测车牌识别设备异常信息
     */
    public void checkLPR() {
        String device = LPR_NAME;
        String str = hvLPR.getErrorMsg();//硬件异常
        String str1 = map.get(device);//缓存中记录的异常
        recordError(device, str, str1);
    }

    /**
     * 检测收费键盘异常信息
     */
    public void checkKeyboard() {
        String device = KEYBOARD_NAME;
        String str = keyboard.getErrorMsg();//硬件异常
        String str1 = map.get(device);//缓存中记录的异常
        recordError(device, str, str1);
    }

    /**
     * 检测连接板异常信息
     */
    public void checkCxp() {
        String device = CXP_NAME;//设备名称
        String str = cxp.getCxpErrorMsg();//硬件异常
        String str1 = map.get(device);//缓存中记录的异常
        recordError(device, str, str1);
    }

    /**
     * 检测计重异常信息
     */
    public void checkWeighSys() {
        String device = WEIGH_NAME;
        String str = weighSystem.getErrorMsg();//硬件异常
        String str1 = map.get(device);//缓存中记录的异常
        recordError(device, str, str1);
    }

    /**
     * 检测票据打印机异常信息
     */
    public void checkPrinter() {
        String device = PRINTER_NAME;
        String str = printer.getPrintError();//硬件异常
        String str1 = map.get(device);//缓存中记录的异常
        recordError(device, str, str1);
    }

    /**
     * 检测卡机异常信息
     */
    public void checkCicm() {
        String device = CICM_NAME;
        String str = cicm.getCicmErrorMsg();//硬件异常
        String str1 = map.get(device);//缓存中记录的异常
        recordError(device, str, str1);
    }

    /**
     * 获取收发卡机天线异常信息
     */
    public void checkMiReader() {
        String device = MIREADER_NAME;
        String str = mireader.getErrorMessage();//硬件异常
        String str1 = map.get(device);//缓存中记录的异常
        recordError(device, str, str1);
    }

    /**
     * 读取自助发卡设备异常信息
     */
    private void checkAMC() {
        String device = AMC_NAME;
        String str = amcControl.getErrorMsg();//硬件异常
        String str1 = map.get(device);//缓存中记录的异常
        recordError(device, str, str1);
    }

    /**
     * 读取字符叠加设备异常信息
     */
    private void checkVi() {
        String device = VI_NAME;
        String str = vi.getErrorMsg();//硬件异常
        String str1 = map.get(device);//缓存中记录的异常
        recordError(device, str, str1);
    }
    /**
     * 读取防盗车取卡设备异常信息
     */
//    private void checkVBD(){
//        String device = VBD_NAME;
//        String str = abc.getErrorMsg();//硬件异常
//        String str1 = map.get(device);//缓存中记录的异常
//        recordError(device, str, str1);
//    }

    /**
     * 检测字符串是否为空或为null
     *
     * @param str 需检测字符串
     * @return 为空或为null返回true 否则返回false
     */
    public boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 记录异常
     *
     * @param device 设备名称
     * @param error 设备新产生的异常（为null表示无异常）
     * @param error1 缓存记录的设备原来的异常（为null表示无异常）
     */
    public void recordError(String device, String error, String error1) {
        String staffId;
        short shiftId;
        if (staff == null) {
            staffId = "000000";
            shiftId = 0;
        } else {
            staffId = staff.getStaffId();
            shiftId = (short) staff.getShiftId();
        }
        if (isEmpty(error) && isEmpty(error1)) {//无异常信息
        } else if (isEmpty(error) && !isEmpty(error1)) {//硬件无异常，但缓存中有异常表示异常已恢复
            map.put(device, null);
            DeviceErrorCacheUtils.remove(device, null, AlarmControl.getDesc(error1));
            MTCLog.log(device + "异常恢复：" + error1);//记录异常信息到日志中
            AlarmControl.generateAlarmInfo(lane, staffId, shiftId, error1, (short) 0);
            fc.sendAlarmInfo("", 0);//向监控室发送异常恢复信息
        } else if (!isEmpty(error) && isEmpty(error1)) {//硬件有异常，但缓存无异常表示新产生的异常
            map.put(device, error);
            DeviceErrorCacheUtils.put(device, AlarmControl.getDesc(error));
            MTCLog.log(device + "异常:" + error);//记录异常信息到日志中
            AlarmControl.generateAlarmInfo(lane, staffId, shiftId, error, (short) 1);
            String desc = AlarmControl.getDesc(error);
            fc.sendAlarmInfo(desc, 1);//向监控室发送异常信息
        } else if (!isEmpty(error) && !isEmpty(error1)) {//缓存有异常同时硬件有异常
            if (!error.equalsIgnoreCase(error1)) {//产生新的异常
                map.put(device, error);
                DeviceErrorCacheUtils.put(device, AlarmControl.getDesc(error));
                MTCLog.log(device + "出现新的异常:" + error);//记录异常信息到日志中
                AlarmControl.generateAlarmInfo(lane, staffId, shiftId, error, (short) 1);
                String desc = AlarmControl.getDesc(error);
                fc.sendAlarmInfo(desc, 1);//向监控室发送异常信息
            }
        }
    }
}
