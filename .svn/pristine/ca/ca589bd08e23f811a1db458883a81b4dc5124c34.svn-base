package com.hgits.control;

import com.hgits.hardware.Keyboard;

/**
 * 紧急车控制
 *
 * @author 王国栋 2014-8-30
 */
public class EmerControl implements Runnable {

    private Keyboard keyboard;//键盘
    private boolean runWhileEmer = true;//紧急车标志

    public EmerControl(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public void setRunWhileEmer(boolean runWhileEmer) {
        this.runWhileEmer = runWhileEmer;
    }

    public boolean getRunWhileEmer() {
        return runWhileEmer;
    }

    @Override
    public void run() {
        String str;
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            str = keyboard.getMessage();
            if (str == null) {

            } else if ("紧急".equals(str)) {//收费员第二次按紧急键表示结束紧急车流程
                setRunWhileEmer(false);
                break;
            } else {
                keyboard.sendAlert();
            }
        }

    }

}
