package com.hgits.control;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import ui.ExtJFrame;
import com.hgits.hardware.Keyboard;

/**
 * 检测项目update文件夹下是否有相应的升级文件并进行升级
 * 升级策略：关闭当前虚拟机，然后新开一个虚拟机Restart.jar，通过新的虚拟机来控制车道程序重新启动。利用软件重新启动时会把update文件夹下的文件拿来进行更新
 *
 * @author Wang Guodong
 */
public class UpdateControl {

    private Keyboard keyboard;
    private ExtJFrame extJFrame;

    public void setKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public void setExtJFrame(ExtJFrame extJFrame) {
        this.extJFrame = extJFrame;
    }

    public UpdateControl() {
        startMonitor();
    }

    /**
     * 检测指定项目是否有升级文件
     *
     * @param projectName 指定项目
     * @return true有升级文件，false无升级文件
     */
    public boolean checkUpdate(String projectName) {
        boolean flag = false;
        File file = new File("update" + File.separator + projectName);
        if (!file.exists()) {
            LogControl.logInfo(file.getAbsolutePath() + "文件夹不存在");
            file.mkdirs();
            return false;
        }
        File[] fileList = file.listFiles();
        for (File f : fileList) {
            if (!f.isFile() || f.isHidden()) {//不判断文件夹或者隐藏文件
                continue;
            }
            if (f.getName().equalsIgnoreCase("update")) {//升级标识文件是否存在
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 执行升级
     *
     * @return 升级结果
     */
    public boolean confirmUpdate() {
        LogControl.logInfo("发现升级文件");
        extJFrame.updateInfo("发现升级文件", "发现升级文件\n按【确认】键进行升级\n或\n按【取消】键5分钟之后再做提示\n该升级提示只在上班前进行");
        String str;
        while (true) {
            try {
                Thread.sleep(500);
                str = keyboard.getMessage();
                if (FunctionControl.isAutoUpdateActive()) {//自动升级
                    LogControl.logInfo("是否自动升级");
                    str = "确认";
                }
                if ("确认".equals(str)) {
                    return restart();
                } else if ("取消".equals(str)) {
                    return false;
                } else if (null != str) {
                    keyboard.sendAlert();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(UpdateControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * 执行重启软件
     */
    private boolean restart() {
        boolean flag = false;
        try {
            File file1 = new File("Restart.jar");
            if (file1.exists()) {
                Process pr = Runtime.getRuntime().exec("java -jar Restart.jar");
                flag = true;
            } else {
                LogControl.logInfo("升级文件丢失，未找到Restart.jar文件");
                extJFrame.updateInfo("升级文件丢失", "升级文件丢失，未找到Restart.jar文件");
                Thread.sleep(5000);
            }
        } catch (Exception ex) {
            LogControl.logInfo("执行重启软件时出现异常", ex);
        }
        return flag;
    }

    /**
     * 开始监控升级文件
     */
    private void startMonitor() {
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        if (checkUpdate("MTCLane")) {
                            ExtJFrame.appendTitle("               发现新版本，请下班。               ");
                        } else {
//                            ExtJFrame.appendTitle("");
                        }
                    } catch (Exception ex) {
                        LogControl.logInfo("升级文件检测异常");
                    }
                }
            }
        });
    }
}
