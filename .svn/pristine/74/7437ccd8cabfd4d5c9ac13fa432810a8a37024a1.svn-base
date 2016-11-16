package com.hgits.control;

import ui.ExtJFrame;

import com.hgits.hardware.Keyboard;

/**
 * 模拟菜单控制
 *
 * @author 王国栋
 */
public class SimulateControl {

    private Keyboard keyboard;//键盘
    private ExtJFrame extJFrame;//收费界面

    public SimulateControl(Keyboard keyboard, ExtJFrame extJFrame) {
        this.keyboard = keyboard;
        this.extJFrame = extJFrame;
    }

    /**
     * @param flag 0更改代收 1 更改车型/车种 2 重打印发票，模拟通行 3 被拖车,4 模拟通行 5重打印发票 6更改车型车种，更改代收
     * @return 收费员选择
     */
    public char runSimulate(int flag) {
        LogControl.logInfo("进入模拟菜单");
        extJFrame.updateInfo("模拟菜单", "选择模拟菜单中的一项\n然后确认");
        extJFrame.showSimulate(flag);
        String str;
        outer:
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            if (flag == 2 || flag == 4) {//等待车辆离开时
                if (!FlowControl.runWhileVehLeave) {//车辆已离开，默认取消
                    return '0';
                }
            }
            str = keyboard.getMessage();
            if (str == null) {

            } else if (str.equals("取消")) {
                extJFrame.hideCentralPanel();
                return '0';
            } else if (str.equals("确认")) {
                char c = extJFrame.getSimulateResult();
                if (c == '0') {
                    keyboard.sendAlert();
                } else {
                    extJFrame.hideCentralPanel();
                    return c;
                }
            } else if (str.matches("[1-5]")) {
                extJFrame.updateSimulate(str);
            } else {
                keyboard.sendAlert();
            }
        }
    }

}
