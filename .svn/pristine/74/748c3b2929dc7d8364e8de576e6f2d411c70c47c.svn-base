package com.hgits.control;

import com.hgits.hardware.CpuCardReader;
import com.hgits.hardware.IcReaderWriter;
import com.hgits.hardware.Keyboard;
import com.hgits.vo.Card;
import java.util.logging.Level;
import java.util.logging.Logger;
import ui.ExtJFrame;

/**
 * m1卡控制
 *
 * @author Wang Guodong
 */
public class M1Control {

    private final IcReaderWriter icReader;
    private final CpuCardReader cpuReader;
    private Keyboard keyboard;
    private CartControl cartControl;

    public M1Control(IcReaderWriter icReader, CpuCardReader cpuReader) {
        this.icReader = icReader;
        this.cpuReader = cpuReader;
    }

    public void setKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public void setCartControl(CartControl cartControl) {
        this.cartControl = cartControl;
    }

    /**
     * 获取M1卡（从m1读卡器及CPU读卡器获取）
     *
     * @return m1卡信息
     */
    public Card getReadedCard() {
        Card card = icReader.getReadedCard();
        if (card != null) {
            return card;
        }
        if (FunctionControl.isEtcTscFunActive()) {//启用ETC读卡器读写通行卡功能
            card = cpuReader.getM1Card();
            if (card != null) {
                return card;
            }
        }
        return null;
    }

    /**
     * 将需要写入卡中的车辆信息载入并且将卡片状态更改为readyToWrite,是卡片进入写卡,直至写卡成功
     *
     * @param card 需要写入的卡片信息
     * @param sec5Flag 05区写卡标识 true/写卡 false/不写卡
     * @param sec6Flag 06区写卡标识 true/写卡 false/不写卡
     * @param sec8Flag 08区写卡标识 true/写卡 false/不写卡
     * @param sec9Flag 09区写卡标识 true/写卡 false/不写卡
     * @return 写卡成功返回0，找不到所写卡返回1，返回其他写卡失败
     */
    public int write(Card card, boolean sec5Flag, boolean sec6Flag, boolean sec8Flag, boolean sec9Flag) {
        if (card == null) {
            return -1;
        }
        String cardTypeCH = card.getCardTypeCH();
        String str;
        keyboard.getMessage();
        int result;
        int cnt = 0;
        while (true) {
            result = 1;
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(M1Control.class.getName()).log(Level.SEVERE, null, ex);
            }
            Card tempCard1 = icReader.getReadedCard();
            if (tempCard1 != null && card.getCardSerial().equalsIgnoreCase(tempCard1.getCardSerial())) {
                cnt = 0;
                result = icReader.write(card, sec5Flag, sec6Flag, sec8Flag, sec9Flag);
            }
            if (FunctionControl.isEtcTscFunActive()) {//启用ETC读卡器读写通行卡功能
                Card tempCard2 = cpuReader.getM1Card();
                if (tempCard2 != null && card.getCardSerial().equalsIgnoreCase(tempCard2.getCardSerial())) {
                    cnt = 0;
                    result = cpuReader.write(card, sec5Flag, sec6Flag, sec8Flag, sec9Flag);

                }
            }
            if (result == 1) {//写卡时发现卡丢失
                ExtJFrame.getSingleInstance().updateInfo(cardTypeCH + "写卡失败", cardTypeCH + "写卡失败\r\n将" + cardTypeCH + "重新放在天线上");
                if (cnt++ == 0) {
                    LogControl.logInfo(cardTypeCH + "写卡时发现卡丢失");
                }
            } else {
                return result;
            }
            str = keyboard.getMessage();
            if (Keyboard.CART.equalsIgnoreCase(str) && card.isCSCCard()) {//通行卡，按【卡机】键拿开坏卡
                int i = cartControl.run(3, 0);
                if (i == 8) {//拿开坏卡
                    LogControl.logInfo("写卡时拿开坏卡");
                    break;
                }
            } else if (Keyboard.CANCEL.equals(str) && card.isCartCard()) {//卡箱标签卡，按【取消】键退出
                return -1;
            }
        }
        return result;
    }

    /**
     * 退出
     */
    public void exit() {
        icReader.exit();
    }
}
