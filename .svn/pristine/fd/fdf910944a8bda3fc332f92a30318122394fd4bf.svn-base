package com.hgits.control;

import com.hgits.hardware.CXP;
import com.hgits.hardware.Keyboard;
import com.hgits.hardware.VBD;
import com.hgits.hardware.impl.vbd.VehBackDetector;
import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;
import ui.ExtJFrame;

/**
 * 防盗车取卡控制
 *
 * @author Wang Guodong
 */
public class AntiBackControl implements Runnable {

    private final Keyboard keyboard;//收费键盘
    private final ExtJFrame extJFrame;//显示界面
    private final CXP cxp;//CXP板
    private boolean running;//运行标识
    private VBD vbd;//倒车检测器
    private String errorMsg;//异常信息
    private int autoBackAlertInterval = 5;//自助取卡倒车语音提示时间间隔，最少1秒，单位秒

    public AntiBackControl(Keyboard keyboard, ExtJFrame extJFrame, CXP cxp) {
        this.keyboard = keyboard;
        this.extJFrame = extJFrame;
        this.cxp = cxp;
    }

    @Override
    public void run() {
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "AntiVehBackFlag", "0");//获取防盗车取卡启用标识，1启用，其他不启用
        running = "1".equals(str);
        if (running) {
            LogControl.logInfo("启用防倒车取卡功能");
            str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_COMM, "VBD", "");
            if (str.isEmpty()) {
                errorMsg = "配置文件未启用防倒车取卡设备";
                LogControl.logInfo("未启用防倒车取卡设备");
                return;
            }
            str = str.replaceAll("，", ",");
            String[] buffer = str.split(",");
            if (buffer.length < 7) {
                LogControl.logInfo("倒车检测器配置文件内容有误：" + str);
                return;
            }
            //获取自助取卡倒车语音提示时间间隔，最少1秒，单位秒
            str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "autoBackAlertInterval", "5");
            int i = IntegerUtils.parseString(str);
            autoBackAlertInterval = i<=0?1000:i;
            String portName = buffer[0];
            int baudRate = IntegerUtils.parseString(buffer[1]);
            int dataBits = IntegerUtils.parseString(buffer[2]);
            int stopBits = IntegerUtils.parseString(buffer[3]);
            int parity = IntegerUtils.parseString(buffer[4]);
            int logLevel = IntegerUtils.parseString(buffer[6]);
            vbd = new VehBackDetector(portName, baudRate, dataBits, stopBits, parity, logLevel);
            ThreadPoolControl.getThreadPoolInstance().execute(vbd);
        } else {
            LogControl.logInfo("未启用防倒车取卡功能");
        }
    }

    /**
     * 车辆倒车
     *
     * @return
     */
    public boolean isVehBack() {
        boolean flag = false;
        if (running && vbd != null) {
            flag = vbd.isVehBack();
        }
        return flag;
    }

    /**
     * 锁定
     */
    public void lock() {
        if (!running) {
            return;
        }
        LogControl.logInfo("防倒车取卡激活，车道已锁定");
        extJFrame.updateInfo("车道已锁定", "车道已锁定，请按【模拟】键解锁");
        vehBackAlert();
        long start = System.currentTimeMillis();
        while (running) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            long now = System.currentTimeMillis();//当前时间小于开始时间，重新开始计时
            if(now<start){
                start = now;
                continue;
            }
            if (now - start >= autoBackAlertInterval*1000) {//每5秒进行一次车辆倒车提示
                vehBackAlert();
                start = now;
            }
            String str = keyboard.getMessage();
            if (str == null) {
                continue;
            } else if (Keyboard.SIMU.equals(str)) {
                LogControl.logInfo("防倒车取卡激活后解锁");
                break;
            } else {
                keyboard.sendAlert();
            }
        }
        setVehBack(false);//重置倒车标识
    }

    /**
     * 设置倒车标识
     *
     * @param flag 倒车标识
     */
    public void setVehBack(boolean flag) {
        if (vbd != null) {
            vbd.setVehBack(flag);
        }
    }

    /**
     * 车辆倒车报警(语音提示：倒车，请注意)
     */
    private void vehBackAlert() {
        if (!running) {
            return;
        }
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            public void run() {
                AudioControl.getSingleInstance().playAudioVehBack();
            }
        });
        cxp.warningAlarm();
    }

    /**
     * 获取硬件异常信息
     *
     * @return 硬件异常信息
     */
    public String getErrorMsg() {
        return vbd == null ? errorMsg : vbd.getErrorMsg();
    }
}
