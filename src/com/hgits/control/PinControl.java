package com.hgits.control;

import com.hgits.hardware.CICM;
import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;
import com.hgits.vo.Lane;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 用于保持出口车道卡机收发针的位置（确保能够将卡放入卡箱）
 *
 * @author wangguodong
 */
public class PinControl {

    private final CICM cicm;
    private boolean pinControlFlag;//控制出口车道卡机收发针位置的标识

    private PinControl(CICM cicm) {
        this.cicm = cicm;
    }

    private static PinControl instance;//唯一实例化对象

    /**
     * 获取唯一实例化对象
     *
     * @param cicm 卡机
     * @return 唯一实例化对象
     */
    public static synchronized PinControl getSingleInstance(CICM cicm) {
        if (instance == null) {
            instance = new PinControl(cicm);
        }
        return instance;
    }

    /**
     * 收发针上升
     */
    public void pinRaise() {
        cicm.receivePinRaise();
        if (Lane.getInstance().isExit() && !pinControlFlag) {
            pinControlFlag = true;
            ExPinControl();//确保出口每次收发针上升的指令完成后都要继续收发针下降两格（确保能够将卡收入卡箱）
        }
    }

    /**
     * 确保出口收发针上升的指令完成后继续收发针下降两格（确保能够将卡收入卡箱）
     */
    public void ExPinControl() {
        if (!pinControlFlag) {//当前并未进行收发针位置控制
            return;
        }
        if (!Lane.getInstance().isExit()) {//当前非出口车道
            return;
        }
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "CicmPinWait", "60");
                int interval = IntegerUtils.parseString(str);
                if (interval < 0) {
                    interval = 60;
                }
                long start = System.currentTimeMillis();
                while (true) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                    }
                    long now = System.currentTimeMillis();
                    if (now < start) {
                        start = now;
                    }
                    if (now - start > interval * 1000) {//等待收发针上升到顶超时
                        break;
                    }
                    if (cicm.isPinTop()) {//收发针已到顶
                        break;
                    }
                }
                try {
                    cicm.lowerTwoSteps();
                } catch (Exception ex) {
                    Logger.getLogger(PinControl.class.getName()).log(Level.SEVERE, null, ex);
                }
                pinControlFlag = false;
            }
        });

    }
}
