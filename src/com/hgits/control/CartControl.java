package com.hgits.control;

import com.hgits.common.log.MTCLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import ui.EntMsgJFrame;
import ui.ExtJFrame;
import com.hgits.hardware.CICM;
import com.hgits.hardware.Keyboard;
import com.hgits.service.CartControlService;
import com.hgits.service.ParamService;
import com.hgits.task.MyTask;
import com.hgits.util.IntegerUtils;
import com.hgits.util.LaneListUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.NullUtils;
import com.hgits.util.ShortUtils;
import com.hgits.util.StringUtils;
import com.hgits.util.rate.ParamCacheQuery;
import com.hgits.vo.Card;
import com.hgits.vo.CardBoxOpList;
import com.hgits.vo.CartInfo;
import com.hgits.vo.CartOpType;
import com.hgits.vo.Constant;
import com.hgits.vo.Lane;
import com.hgits.vo.Station;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 对应卡机菜单及卡箱控制
 *
 * @author 王国栋
 */
public class CartControl {

    private final String CART_STATUS_IN_LANE = "01";//车道使用中
    private final String CART_STATUS_IN_TRANS = "04";//站内流通

    protected ExtJFrame extJFrame;
    protected Keyboard keyboard;
    protected Card card;
    protected CICM cicm;
    protected M1Control m1Control;
    private boolean empty;//卡箱空
    private boolean full;//卡箱满
    private boolean hasCart;//有卡箱
    private Lane lane;
    private CartInfo cartInfo;//卡箱信息
    private EntMsgJFrame emj = EntMsgJFrame.getSingleInstance();//不可读卡界面
    private Set<Integer> cardOrderSet = new HashSet();//记录卡箱内卡顺序号的集合
    private static final String PATH1 = "temp/temp_cardOrder.txt";//记录卡箱内卡顺序的临时文件
    private boolean running;//卡箱操作进行中
    private final ConcurrentLinkedQueue<CartInfo> recordQueue;

    /**
     * 额外卡数量加1
     */
    public synchronized void increaseExtraCardCnt() {
        if (cartInfo == null) {
            return;
        } else {
            String str = cartInfo.getSpare();
            int i = IntegerUtils.parseString(str);
            i++;
            cartInfo.setSpare(String.valueOf(i));
            CartInfo ci = new CartInfo(cartInfo);
            recordQueue.add(ci);
        }
    }

    public final synchronized void setCartInfo(CartInfo cartInfo) {
        this.cartInfo = cartInfo;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public boolean isHasCart() {
        return hasCart;
    }

    public void setHasCart(boolean hasCart) {
        this.hasCart = hasCart;
    }

    public CartControl(ExtJFrame extJFrame, Keyboard keyboard, CICM cicm, M1Control m1Control, final Lane lane) {
        recordQueue = new ConcurrentLinkedQueue();
        this.extJFrame = extJFrame;
        this.keyboard = keyboard;
        this.cicm = cicm;
        this.m1Control = m1Control;
        this.lane = lane;
        setCartInfo(loadCartInfo());

        if (lane.isEntry()) {//入口解析卡箱内卡顺序号
            cardOrderSet = getCardOrder(PATH1);
        }

        //检测卡箱数量的线程
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        if (cartInfo == null) {//卡箱是否已装入
                            setHasCart(false);
                            if (lane.isEntry()) {
                                errorMsg = "50";//入口无卡箱报警序号
                            } else if (lane.isExit()) {
                                errorMsg = "27";//出口无卡箱报警序号
                            }
                            continue;
                        } else {
                            setHasCart(true);
                            setEmpty(false);
                            setFull(false);
                        }
                        int totalQty = IntegerUtils.parseString(cartInfo.getTotalCardQty());//卡箱容量
                        int orderNum = IntegerUtils.parseString(cartInfo.getCardOrderNum());//卡箱内卡数量
                        setEmpty(orderNum == 0);//判断卡箱是否已空
                        setFull(orderNum >= totalQty);//判断卡箱是否已满
                        if (lane.isExit()) {//出口
                            if (orderNum >= totalQty) {
                                errorMsg = "24";//出口车道卡箱已满报警序号
                            } else if (orderNum + 50 > totalQty) {
                                errorMsg = "23";//出口车道卡箱将满报警序号
                            } else {
                                errorMsg = null;
                            }
                        } else if (lane.isEntry()) {//入口
                            if (orderNum == 0) {
                                errorMsg = "45";//入口车道卡箱已空报警序号
                            } else if (orderNum < 50) {
                                errorMsg = "44";//入口车道卡箱将空报警序号
                            } else {
                                errorMsg = null;
                            }
                        }
                    } catch (Throwable ex) {
                        LogControl.logInfo(null, ex);
                    }
                }

            }
        });
        RecordTask recordTask = new RecordTask();
        ThreadPoolControl.getThreadPoolInstance().execute(recordTask);
    }

    public CartControl() {
        this.recordQueue = new ConcurrentLinkedQueue<>();
    }

    public CartInfo getCartInfo() {
        return cartInfo;
    }

    public Card getCard() {
        return card;
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * 用户按【卡机】键后进入卡机菜单控制流程
     *
     * @param flag 0无卡，不可读卡，残卡，预编码卡，拿开坏卡均不可选 1 无卡，不可读卡，残卡，预编码卡可选 2 拿开坏卡可选 3
     * 仅拿开坏卡可选
     * @param paramFlag 0 不可查看营运参数 1 可查看营运参数
     * @return -1 其他异常 0取消操作 1更换卡箱2强行取出卡箱3装入卡箱4交接卡箱5无卡6不可读卡7残卡8拿开坏卡9预编码卡10查看营运参数
     */
    public int run(int flag, int paramFlag) {
        running = true;//卡箱操作过程中
        LogControl.logInfo("进入卡机菜单流程");
        extJFrame.updateInfo("卡机菜单", "选择卡机菜单中的一项\n然后确认");
        try {
            showCartPanel(flag, paramFlag);
            int result = 0;
            int i = runCartOption();
            if (i == '5' || i == '7') {                                         //无卡，残卡
                card = new Card();
                card.setRoadid("99");
                card.setStationid("97");
                card.setStatus("00");
                card.setType(Constant.CARD_TYPE_01);
                if (i == '5') {
                    LogControl.logInfo("无卡");
                    card.setObservationCode("8");
                } else if (i == '7') {
                    LogControl.logInfo("残卡");
                    card.setObservationCode("12");
                }
//                LogControl.logInfo("无卡/残卡");
//                card.setObservationCode("8");
                result = 5;
            } else if (i == '6') {                                             //不可读卡
                LogControl.logInfo("不可读卡");
                emj.showFrame();
                extJFrame.setAlwaysOnTop(false);
                extJFrame.requestFocus();
                extJFrame.updateInfo("等待输入", "输入通行卡序列号\n并在用户能够提供的情况下输入入口站号\n然后确认");
                card = runUnreadCard(emj);
                if (card == null) {//取消不可读卡操作
                    result = 0;
                } else {
                    result = 6;
                }
            } else if (i == '8') {
                //拿开坏卡
                LogControl.logInfo("等待拿开坏卡");
                String manualAutoLevel = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "manualAutoLevel", "0");
                switch (manualAutoLevel) {
                    case "2"://西埃斯模式
                        try {
                            //西埃斯模式
                            cicm.frontBoltRaise();//前卡栓上升
                        } catch (Exception ex) {
                            LogControl.logInfo(ex.toString(), ex);
                        }
                    default:
                        break;
                }
                cicm.backBoltLower();
                extJFrame.updateInfo("拿开坏卡", "拿开坏卡后并确认");
                String temp;
                while (true) {
                    Thread.sleep(100);
                    temp = keyboard.getMessage();
                    if (temp == null) {
                        continue;
                    } else if (Keyboard.CONFIRM.equals(temp)) {//按确认键
                        if (m1Control.getReadedCard() == null) {//等待将卡拿开
                            break;
                        } else {
                            keyboard.sendAlert();
                        }
                    } else {
                        keyboard.sendAlert();
                    }
//                    if (m1Control.getReadedCard() == null) {//等待将卡拿开
//                        break;
//                    }
//                    if (keyboard.getMessage() != null) {
//                        keyboard.sendAlert();
//                    }
                }
//                keyboard.waitForConfirm();
                LogControl.logInfo("确认拿开坏卡");
                cicm.backBoltRaise();
                if (lane.isEntry()) {//入口前卡栓下降（收费员可以继续发卡）
                    cicm.frontBoltLower();
                    cicm.receivePinRaise();
                }
                result = 8;
            } else if (i == '3') {                                            //装入卡箱
                result = runLoadCart();
            } else if (i == '1') {                                            //更换卡箱
                result = runChangeCart();
            } else if (i == '2') {                                            //强行取出卡箱
                result = runUnloadCart();
            } else if (i == '4') {                                            //交接卡箱
                result = runHandoverCart();
            } else if (i == '0') {                                            //取消操作
                extJFrame.hideCentralPanel();
                result = 0;
            } else if (i == 'A') {//查看营运参数
                ParamService svc = new ParamService(keyboard, extJFrame);
                svc.showParamVersion();
                return 10;
            }
            extJFrame.hideCentralPanel();                                      //收费员选择完毕后隐藏卡机菜单选项
            if (FlowControl.logout != null) {
                if (result == 1) {//更换卡箱
                    FlowControl.logout.setBoxUsed(FlowControl.logout.getBoxUsed() + 1);
                    FlowControl.logout.setInBoxNum(FlowControl.logout.getInBoxNum() + 1);
                } else if (result == 2) {//强行取出卡箱
//                    FlowControl.logout.setBoxUsed(FlowControl.logout.getInBoxNum() + 1);
                    FlowControl.logout.setInQBoxNum(FlowControl.logout.getInQBoxNum() + 1);
                } else if (result == 3) {//装入卡箱
                    FlowControl.logout.setBoxUsed(FlowControl.logout.getBoxUsed() + 1);
                } else if (result == 5) {//无卡
                    FlowControl.cardObserCode = "I";
                    FlowControl.logout.setLostC(FlowControl.logout.getLostC() + 1);
                } else if (result == 6) {//不可读卡
                    FlowControl.chargeObserCode = "P";
                    FlowControl.logout.setCantReadTTC(FlowControl.logout.getCantReadTTC() + 1);//不可读卡+1
                    FlowControl.logout.setHandInTTC(FlowControl.logout.getHandInTTC() + 1);//ic卡总数+1
                    FlowControl.logout.setPressTTIDC(FlowControl.logout.getPressTTIDC() + 1);//键入通行卡号+1
                } else if (result == 7) {//残卡
                    FlowControl.chargeObserCode = "M";
                    FlowControl.logout.setHandInTTC(FlowControl.logout.getHandInTTC() + 1);
                    FlowControl.logout.setIncompleteTTC(FlowControl.logout.getIncompleteTTC() + 1);
                } else if (result == 8) {//坏卡
                    FlowControl.cardObserCode = "1";
                    FlowControl.chargeObserCode = "O";
                    FlowControl.logout.setBadTTC(FlowControl.logout.getBadTTC() + 1);
                    FlowControl.logout.setBtC(FlowControl.logout.getBtC() + 1);
                    FlowControl.logout.setHandInTTC(FlowControl.logout.getHandInTTC() + 1);
                } else if (result == 9) {//预编码卡
                    FlowControl.cardObserCode = "Q";
                    FlowControl.logout.setPrecodedTTC(FlowControl.logout.getPrecodedTTC() + 1);
                }
            }
            return result;
        } catch (Exception ex) {
            LogControl.logInfo("卡机菜单操作出现异常", ex);
            return -1;
        } finally {
            running = false;//卡箱操作结束
        }
    }

    /**
     * 显示卡机界面
     *
     * @param flag1 0无选项 1 无卡，不可读卡，残卡，预编码卡 2 拿开坏卡 3 仅拿开坏卡可选
     * @param flag3 0 不可查看营运参数 1 可查看营运参数
     */
    public void showCartPanel(int flag1, int flag3) {
        if (flag1 == 3) {
            extJFrame.showCartPanel(2, 4, 0);
        } else if (errorMsg == null) {//卡箱无异常
            extJFrame.showCartPanel(flag1, 3, flag3);
        } else if (errorMsg.equals("50") || errorMsg.equals("27")) {//无卡箱
            extJFrame.showCartPanel(flag1, 1, flag3);
        } else if (errorMsg.equals("24")) {//出口卡箱已满
            extJFrame.showCartPanel(flag1, 2, flag3);
        } else if (errorMsg.equals("45")) {//入口卡箱已空
            extJFrame.showCartPanel(flag1, 2, flag3);
        } else {//卡箱正常使用
            extJFrame.showCartPanel(flag1, 3, flag3);
        }
    }

    /**
     * 对应收费员按【卡机】键后卡机菜单显示，收费员选择卡机菜单并确认（或取消选择）
     *
     * @return 0 收费员按【取消】键 1 更换卡箱 2 强行取出卡箱 3 装入卡箱 4 交接卡箱 5 无卡 6 不可读卡 7 残卡 8 拿开坏卡
     * 9 预编码卡 A纸券
     */
    private int runCartOption() {
        int i = 6;//默认不可读卡
        extJFrame.updateCartPanel(i + "");
        String str;
        while (true) {
            try {
                Thread.sleep(100);
            } catch (Exception ex) {

            }
            str = keyboard.getMessage();//通过数字或者上下箭头键选择卡机菜单相应内容，直至收费员按【确认】键或【取消】键
            if (str == null) {
                continue;
            } else if (Keyboard.ZEROZERO.equals(str)) {//纸券模式
                i = IntegerUtils.parseString(str);
            } else if (str.matches("[1-9]")) {
                i = IntegerUtils.parseString(str);
            } else if (Keyboard.CONFIRM.equals(str)) {
                i = extJFrame.getCartPanelOption();
                break;
            } else if (Keyboard.CANCEL.equals(str)) {
                i = '0';
                break;
//            } else if (Keyboard.UP.equals(str)) {
//                i--;
//            } else if (Keyboard.CANCEL.equals(str)) {
//                i++;
            } else {
                keyboard.sendAlert();
                continue;
            }
            extJFrame.updateCartPanel(i + "");
        }
        extJFrame.hideCentralPanel();
        return i;
    }

    /**
     * 执行不可读卡流程
     *
     * @param emj 不可读卡时收费员键入入口信息界面
     * @return 返回卡信息，若收费员取消选择，返回null
     */
    private Card runUnreadCard(EntMsgJFrame emj) {
        Card tempCard = new Card();
        String str;
        StringBuilder cardNum1 = new StringBuilder();//记录第一次输入卡序列号
        StringBuilder cardNum2 = new StringBuilder();//记录第二次输入卡序列号
        StringBuilder entId = new StringBuilder();//记录入口站号
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            extJFrame.updateInfo("等待输入", "输入通行卡序列号\n并在用户能够提供的情况下输入入口站号\n然后确认");
            str = keyboard.waitForOrder();
            if (str.matches("[0-9]")) {
                if (cardNum1.length() < 10) {//第一次输入通行卡序列号
                    cardNum1.append(str);
                } else if (cardNum1.length() == 10 && cardNum2.length() < 10) {//第二次输入卡序列号
                    cardNum2.append(str);
                } else if (cardNum1.length() == 10 && cardNum2.length() == 10 && entId.length() < 4) {//输入入口站号
                    entId.append(str);
                } else {
                    keyboard.sendAlert();
                }
            } else if ("确认".equals(str)) {
                if (cardNum1.length() == 10 && cardNum2.length() == 10 && entId.length() == 4) {
                    String str1 = cardNum1.toString();
                    String str2 = cardNum2.toString();
                    int enRoadid = IntegerUtils.parseString(entId.substring(0, 2));
                    int enStationid = IntegerUtils.parseString(entId.substring(2, 4));
                    Station sta = ParamCacheQuery.queryStation(enRoadid, enStationid);
                    if (!str1.equals(str2)) {//第一次输入的通行卡序列号和第二次输入的通行卡序列号要相同
                        keyboard.sendAlert();
                        extJFrame.updateInfo("", "两次输入的通行卡序列号不同,请重新输入");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                        }
                    } else if (sta == null) {
                        keyboard.sendAlert();
                        extJFrame.updateInfo("", "入口站不存在，请重新输入");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                        }
                    } else {
                        tempCard.setCardSerial(str1);
                        tempCard.setRoadid(entId.substring(0, 2));
                        tempCard.setStationid(entId.substring(2, 4));
                        tempCard.setLaneId("000");
                        tempCard.setType(Constant.CARD_TYPE_01);
                        tempCard.setObservationCode("9");
                        break;
                    }
                } else {
                    keyboard.sendAlert();
                }
            } else if ("取消".equals(str)) {//收费员删除键入信息
                int len1 = cardNum1.length();
                int len2 = cardNum2.length();
                int len3 = entId.length();
                if (len3 > 0 && len1 == 10 && len2 == 10) {//删除入口站信息
                    entId.delete(len3 - 1, len3);
                } else if (len3 == 0 && len2 > 0 && len1 == 10) {//删除重复输入卡序列号
                    cardNum2.delete(len2 - 1, len2);
                } else if (len3 == 0 && len2 == 0 && len1 > 0) {//删除通行卡序列号
                    cardNum1.delete(len1 - 1, len1);
                } else {
                    emj.setVisible(false);
                    tempCard = null;
                    break;
                }
            }
            emj.updateNum1(cardNum1.toString());
            emj.updateNum2(cardNum2.toString());
            emj.updateEntNum(entId.toString());
        }
        emj.setVisible(false);
//        extJFrame.setAlwaysOnTop(true);
        extJFrame.showOnTop();
        return tempCard;
    }

    /**
     * 交接卡箱流程
     *
     * @return 4 操作成功 0 取消操作 -1 其他异常
     */
    private int runHandoverCart() {
        String sendStaffId;//送卡箱传递员
        String recvStaffId;//收卡箱传递员
        LogControl.logInfo("交接卡箱");
        LogControl.logInfo("等待出示卡箱传递员1身份卡");
        CartControlService cartSvc = new CartControlService(extJFrame, cicm, m1Control, keyboard);
        Card idCard1;
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            extJFrame.updateInfo("等待出示卡箱传递员身份卡", "将卡箱传递员1的身份卡放在天线上");
            idCard1 = cartSvc.waitForStaffCard(lane);//等待卡箱传递员身份卡
            if (idCard1 == null) {
                LogControl.logInfo("取消交接卡箱");
                return 0;
            } else {
                break;
            }
        }
        String staffId1 = StringUtils.encodeID(idCard1.getId());
        LogControl.logInfo("卡箱传递员1:" + staffId1);
        Card idCard2;
        LogControl.logInfo("等待出示卡箱传递员2身份卡");
        extJFrame.updateInfo("等待出示卡箱传递员身份卡", "将卡箱传递员2的身份卡放在天线上");
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            idCard2 = cartSvc.waitForStaffCard(lane);
            if (idCard2 == null) {//取消操作
                LogControl.logInfo("取消交接卡箱");
                return 0;
            } else if (idCard2.getCardSerial().equals(idCard1.getCardSerial())) {//出示同一只身份卡
            } else {
                break;
            }
        }
        String staffId2 = StringUtils.encodeID(idCard2.getId());
        LogControl.logInfo("卡箱传递员2:" + staffId2);
        LogControl.logInfo("等待出示卡箱标签卡");
        Card cart;
        String message;
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            extJFrame.updateInfo("等待出示卡箱标签卡", "将卡箱标签卡放在天线上");
            cart = cartSvc.waitForCartCard(lane);
            LogControl.logInfo("卡箱标签卡:" + cart);
            if (cart == null) {                                                 //取消操作
                LogControl.logInfo("取消交接卡箱");
                return 0;
            } else {
                //验证卡箱标签卡状态是否为“站内流通04”                
                //验证当前卡箱标签卡中记录的员工号与出示的卡箱传递员身份卡员工号是否一致
                String status = cart.getStatus();
                String staffId = cart.getStaffId();
                if (CART_STATUS_IN_TRANS.equals(status) && staffId != null && staffId.equals(staffId1)) {
                    sendStaffId = staffId1;//卡箱传递员1为送卡箱传递员
                    recvStaffId = staffId2;
                    break;
                } else if (CART_STATUS_IN_TRANS.equals(status) && staffId != null && staffId.equals(staffId2)) {
                    sendStaffId = staffId2;//卡箱传递员2为送卡箱传递员
                    recvStaffId = staffId1;
                    break;
                } else {
                    LogControl.logInfo("卡箱标签卡被拒绝");
                    extJFrame.updateInfo("卡箱标签卡被拒绝", "卡箱标签卡被拒绝\n按【取消】键退出");
                    String str;
                    while (true) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                        }
                        str = keyboard.getMessage();
                        if (str == null) {
                            continue;
                        } else if ("取消".equals(str)) {
                            return 0;
                        } else {
                            keyboard.sendAlert();
                        }
                    }
                }
            }
        }
        cart.setStaffId(recvStaffId);
        //卡箱交接时卡箱循环数不变
        cart.setWritingNum(IntegerUtils.parseString(cart.getWritingNum()) + 1 + "");     //标签卡写卡次数加1
        cart.setDhm(new Date(System.currentTimeMillis()));
        LogControl.logInfo("卡箱标签卡写卡" + cart);
        int i = m1Control.write(cart, true, true, false, false);
        String str;
        while (i != 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            extJFrame.updateInfo("写卡失败", "卡箱标签卡写卡失败,按【确认】键重新写卡，按【取消】键退出卡箱交接流程");
            str = keyboard.getMessage();
            if (str == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            } else if ("确认".equals(str)) {
                LogControl.logInfo("卡箱标签卡写卡失败:" + cart);
                i = m1Control.write(cart, true, true, false, false);
            } else if ("取消".equals(str)) {
                LogControl.logInfo("卡箱标签卡写卡失败:" + cart);
                return -1;
            } else {
                keyboard.sendAlert();
            }
        }
        message = "完成交接卡箱" + cart.getId() + "\n送卡箱传递员" + sendStaffId + "\n收卡箱传递员" + recvStaffId + "\n按【确认】键以继续";
        LogControl.logInfo(message);
        extJFrame.updateInfo("本次卡箱交接信息总结", message);
        generateCadboxList(cart, CartOpType.TRAN, recvStaffId, sendStaffId, 0);
        outer:
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            switch (keyboard.waitForOrder()) {
                case "确认":
                    break outer;
                default:
                    keyboard.sendAlert();
            }
        }
        return 4;
    }

    /**
     * 装入卡箱流程
     *
     * @return 3操作成功 0 取消操作 -1 其他异常
     * @throws java.lang.Exception
     */
    private int runLoadCart() throws Exception {
        try {
            LogControl.logInfo("装入卡箱");
            LogControl.logInfo("等待出示卡箱传递员身份卡");
            CartControlService cartSvc = new CartControlService(extJFrame, cicm, m1Control, keyboard);
            Card idCard = null;
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                extJFrame.updateInfo("等待出示卡箱传递员身份卡", "将卡箱传递员的身份卡放到天线上");
                idCard = cartSvc.waitForStaffCard(lane);                        //出示卡箱传递员身份卡
                if (idCard == null) {                                           //用户取消操作
                    LogControl.logInfo("取消卡箱操作");
                    return 0;
                } else {
                    break;
                }
            }
            LogControl.logInfo("读取卡箱传递员身份卡:" + idCard);
            LogControl.logInfo("等待卡箱收发针降到底");
            extJFrame.updateInfo("等待卡箱收发针降到底", "等待卡箱收发针降到底");
//            cicm.receivePinLower();                                             //收发针下降
            int pin = waitCicmPinBottom();//等待收发针降到底
            if (pin == -1) {
                LogControl.logInfo("取消卡箱操作");
                return 0;
            }
//            cartSvc.waitForReceivePinBottom();                                  //等待收发针降到底
            cicm.protectiveCoverOpen();                                         //解锁卡机保护盖
            LogControl.logInfo("等待打开卡机保护盖");
            extJFrame.updateInfo("等待打开卡机保护盖", "打开卡机保护盖");
            if (!cartSvc.waitForProtectCoverUnlocked()) {                       //确认卡机保护盖已打开
                cicm.protectiveCoverLockUp();                                   //用户取消操作,锁住卡机保护盖
                return 1;
            }
            LogControl.logInfo("等待出示卡箱标签卡");
            Card cart;
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                extJFrame.updateInfo("等待出示卡箱标签卡", "将卡箱标签卡放到天线上");
                cart = cartSvc.waitForCartCard(lane);                           //出示卡箱标签卡
                if (cart == null) {                                             //用户取消操作
                    LogControl.logInfo("取消卡箱操作");
                    return 0;
                } else if (!CART_STATUS_IN_TRANS.equals(cart.getStatus())) {
                    LogControl.logInfo("装入卡箱时卡箱标签卡状态" + cart.getStatus() + "错误，非站内流通状态");
                    extJFrame.updateInfo("卡箱标签卡状态错误", "卡箱标签卡状态错误\n请出示正确的卡箱标签卡");
                    Thread.sleep(2000);
                } else if (cart.getStaffId() == null || !cart.getStaffId().equals(StringUtils.encodeID(idCard.getId()))) {//卡箱标签卡中记录员工号与员工卡中记录员工号不一致
                    String info = "当前卡箱传递员" + StringUtils.encodeID(idCard.getId()) + "与卡箱标签卡中记录的" + cart.getStaffId() + "不符";
                    LogControl.logInfo(info);//卡箱传递员不一致
                    extJFrame.updateInfo("卡箱标签卡被拒绝", info + "\n请出示另一张卡箱标签卡");
                    Thread.sleep(2000);
                } else if (cart.getCscNum() == null || IntegerUtils.parseString(cart.getCscNum()) == 0) {//空卡箱
                    if (lane.isEntry()) {
                        LogControl.logInfo("空卡箱");//入口不可装入空卡箱
                        extJFrame.updateInfo("空卡箱", "卡箱为空\n请出示正确的卡箱标签卡");
                        Thread.sleep(2000);
                    } else if (lane.isExit()) {
                        cart.setCscNum("500");
                        break;
                    }
                } else if (cart.getCscNum() != null && IntegerUtils.parseString(cart.getCscNum()) != 0) {//非空卡箱
                    if (lane.isExit()) {
                        LogControl.logInfo("非空卡箱");//出口不可装入非空卡箱
                        extJFrame.updateInfo("非空卡箱", "卡箱非空\n请出示正确的卡箱标签卡");
                        Thread.sleep(2000);
                    } else if (lane.isEntry()) {
                        break;
                    }
                } else {
                    break;
                }
            }
            LogControl.logInfo("读取卡箱标签卡:" + cart);
            cart.setLane(lane);
            cart.setStaffId(StringUtils.encodeID(idCard.getId()));
//            cart.setLaneId(lane.getLaneId());
            cart.setDhm(new Date(System.currentTimeMillis()));
            cart.setStatus(CART_STATUS_IN_LANE);
            if (lane.isExit()) {//出口装入卡箱时卡箱循环数加1
                cart.setCartCycleNum(IntegerUtils.parseString(cart.getCartCycleNum()) + 1 + ""); //卡箱循环数加1
            }
            cart.setWritingNum(IntegerUtils.parseString(cart.getWritingNum()) + 1 + ""); //标签卡写卡次数加1
            LogControl.logInfo("卡箱标签卡写卡" + cart);
            int i = m1Control.write(cart, true, true, false, false);                                       //将新的信息写入卡箱标签卡
            for (int j = 0; j < 3; j++) {
                if (i == 0) {
                    break;
                } else {//标签卡写卡失败,最多再尝试三次
                    LogControl.logInfo("卡箱标签卡写卡失败");
                    i = m1Control.write(cart, true, true, false, false);
                }
            }
            String str;
            while (i != 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                extJFrame.updateInfo("写卡失败", "卡箱标签卡写卡失败,按【确认】键重新写卡，按【取消】键退出卡箱流程");
                str = keyboard.getMessage();
                if (str == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                } else if ("确认".equals(str)) {
                    LogControl.logInfo("卡箱标签卡写卡失败:" + cart);
                    i = m1Control.write(cart, true, true, false, false);
                } else if ("取消".equals(str)) {
                    LogControl.logInfo("卡箱标签卡写卡失败:" + cart);
                    return -1;
                } else {
                    keyboard.sendAlert();
                }
            }
            LogControl.logInfo("卡箱标签卡写卡成功");
            CartInfo cartInfo = new CartInfo();
            cartInfo.setCartId(cart.getId());
            cartInfo.setCartCycleNum(cart.getCartCycleNum());
            cartInfo.setTotalCardQty(cart.getCscNum());
            cartInfo.setCartStaffId(cart.getStaffId());
            cartInfo.setCardSerial(cart.getCardSerial());
            cartInfo.setCurOpRoadid(ShortUtils.parseString(lane.getRoadId()));
            cartInfo.setCurOpStationid(ShortUtils.parseString(lane.getStationId()));
            cartInfo.setCurOpLaneNo(lane.getLaneId());
            cartInfo.setStatus(ShortUtils.parseString(cart.getStatus()));
            cartInfo.setCurOpTime(new Date());
            int laneType = lane.getLaneType();
            if (laneType == 1) {//入口装入满卡箱
                cartInfo.setCardOrderNum(cart.getCscNum());
            } else if (laneType == 2) {//出口装入空卡箱
                cartInfo.setCardOrderNum("0");
            }
            setCartInfo(cartInfo);
            updateCartInfo(cartInfo);
            generateCadboxList(cartInfo.getCurOpTime(), CartOpType.RECV, FlowControl.getStaffId(), cartInfo.getCartStaffId(), 0);
            if (FlowControl.getStaffId() != null) {//当前已上班
                //更新临时卡箱流动单
                generateTempCadboxList(new Date(), CartOpType.SEND, null, FlowControl.getStaffId(), 0);
            }
            LogControl.logInfo("等待放好卡机收发帽并合上保护盖");
            extJFrame.updateInfo("等待放好卡机收发帽并合上保护盖", "放好卡机收发帽并合上保护盖");
            cartSvc.waitForReceiveHeadCovered();                                //等待放好卡机收发帽
            cartSvc.waitForProtectCoverLocked();                                //等待合上卡机保护盖
            cicm.protectiveCoverLockUp();                                       //锁住卡机保护盖
            extJFrame.updateInfo("等待卡机收发针上升", "等待卡机收发针上升");
            LogControl.logInfo("等待卡机收发针上升");
//            cicm.receivePinRaise();                                             //收发针上升
            waitCicmPinTop();//等待收发针升到顶
            return 3;
        } catch (Exception ex) {
            LogControl.logInfo("卡箱操作出现异常", ex);
            return -1;
        } finally {
            extJFrame.updateInfo("等待收发针上升", "等待收发针上升");
//            cicm.receivePinRaise();                                             //收发针上升
            waitCicmPinTop();//等待收发针升到顶
            if (lane.isExit()) {//出口收发针下降两格
                cicm.lowerTwoSteps();
            }
        }
    }

    /**
     * 等待卡机收发针升到顶
     */
    private void waitCicmPinTop() {
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "CicmPinWait", "60");
        int interval = IntegerUtils.parseString(str);
        if (interval < 0) {
            interval = 60;
        }
        cicm.receivePinRaise();
        long start = System.currentTimeMillis();
        while (cicm.isAvailable()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            long now = System.currentTimeMillis();
            if (now - start > interval * 1000) {//等待超时
                break;
            }
            if (cicm.isPinTop()) {//收发针已到顶
                break;
            }
        }
    }

    /**
     * 等待卡机收发针降到底
     *
     * @return 0下降到底 -1 取消
     */
    private int waitCicmPinBottom() {
        try {
            String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "CicmPinWait", "60");
            int interval = IntegerUtils.parseString(str);
            if (interval < 0) {
                interval = 60;
            }
            cicm.receivePinLower();
            long start = System.currentTimeMillis();
            String order;
            while (cicm.isAvailable()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                long now = System.currentTimeMillis();
                if (now < start) {
                    start = now;
                } else if (now - start > interval * 1000) {//等待超时
                    break;
                }
                if (cicm.isPinBottom()) {//收发针已到顶
                    break;
                }
                order = keyboard.getMessage();
                if (Keyboard.CANCEL.equals(order)) {
                    LogControl.logInfo("取消收发针降到底的等待");
                    return -1;
                }
            }
        } catch (Exception ex) {
            MTCLog.log(ex, ex);
        }
        return 0;
    }

    /**
     * 强行取出卡箱流程
     *
     * @return 2操作成功 0 取消操作 -1 其他异常
     * @throws java.lang.Exception
     */
    private int runUnloadCart() throws Exception {
        LogControl.logInfo("强行取出卡箱");
        LogControl.logInfo("等待出示卡箱传递员身份卡");
        try {
            CartControlService cartSvc = new CartControlService(extJFrame, cicm, m1Control, keyboard);
            Card idCard;
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                extJFrame.updateInfo("等待出示卡箱传递员身份卡", "将卡箱传递员的身份卡放到天线上");
                idCard = cartSvc.waitForStaffCard(lane);                        //出示卡箱传递员身份卡
                if (idCard == null) {                                           //用户取消操作
                    LogControl.logInfo("取消卡箱操作");
                    return 0;
                } else if (!StringUtils.encodeID(idCard.getId()).equals(cartInfo.getCartStaffId())) {
                    extJFrame.updateInfo("", "当前卡箱传递员与当前卡箱标签卡不符");
                    Thread.sleep(2000);
                } else {
                    break;
                }
            }
            LogControl.logInfo("读取卡箱传递员身份卡:" + idCard);
            LogControl.logInfo("等待卡箱收发针降到底");
            extJFrame.updateInfo("等待卡箱收发针降到底", "等待卡箱收发针降到底");
//            cicm.receivePinLower();                                             //收发针下降
//            cartSvc.waitForReceivePinBottom();                                  //等待收发针降到底
            int pin = waitCicmPinBottom();//等待收发针降到底
            if (pin == -1) {
                LogControl.logInfo("取消卡箱操作");
                return 0;
            }
            cicm.protectiveCoverOpen();                                         //解锁卡机保护盖
            LogControl.logInfo("等待打开卡机保护盖");
            extJFrame.updateInfo("等待打开卡机保护盖", "打开卡机保护盖");
            if (!cartSvc.waitForProtectCoverUnlocked()) {                       //确认卡机保护盖已打开
                LogControl.logInfo("取消卡箱操作");
                cicm.protectiveCoverLockUp();                                   //用户取消操作,锁住卡机保护盖
                return 0;
            }
            LogControl.logInfo("等待出示卡箱标签");
            Card cart;
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                extJFrame.updateInfo("等待出示卡箱标签", "将卡箱标签卡放到天线上");
                cart = cartSvc.waitForCartCard(lane);                              //出示卡箱标签卡
                if (cart == null) {                                                //用户取消操作
                    LogControl.logInfo("取消卡箱操作");
                    return 0;
                } else if (!cart.getId().equals(cartInfo.getCartId())) {
                    extJFrame.updateInfo("", "当前卡箱标签卡与当前卡箱不一致，请出示正确卡箱标签卡");
                    Thread.sleep(2000);
                } else {
                    break;
                }
            }
            LogControl.logInfo("读取卡箱标签卡:" + cart);
//            cart.setLaneId(lane.getLaneId());
            cart.setLane(lane);
            String tempid = idCard.getId();
            if (tempid != null && tempid.length() == 5) {
                cart.setStaffId(StringUtils.encodeID(idCard.getId()));
            }
            cart.setDhm(new Date(System.currentTimeMillis()));
            cart.setStatus(CART_STATUS_IN_TRANS);//站内流通
            if (lane.isEntry()) {//入口取出卡箱时卡箱循环数加1
                cart.setCartCycleNum(IntegerUtils.parseString(cart.getCartCycleNum()) + 1 + ""); //卡箱循环数加1
            }
            cart.setWritingNum(IntegerUtils.parseString(cart.getWritingNum()) + 1 + "");     //标签卡写卡次数加1
            if (lane.isEntry()) {//入口强行取出，卡箱数量为空
                cart.setCscNum("0");
            } else if (lane.isExit()) {//出口强行取出，标签卡内记录当前卡箱内卡数量
                cart.setCscNum(cartInfo.getCardOrderNum());
            }
            LogControl.logInfo("卡箱标签卡写卡:" + cart);
            int i = m1Control.write(cart, true, true, false, false);                                                   //将新的信息写入卡箱标签卡
            for (int j = 0; j < 3; j++) {
                if (i == 0) {
                    break;
                } else {//标签卡写卡失败,最多再尝试三次
                    LogControl.logInfo("卡箱标签卡写卡失败");
                    i = m1Control.write(cart, true, true, false, false);
                }
            }
            String str;
            while (i != 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                extJFrame.updateInfo("写卡失败", "卡箱标签卡写卡失败,按【确认】键重新写卡，按【取消】键退出卡箱流程");
                str = keyboard.getMessage();
                if (str == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                } else if ("确认".equals(str)) {
                    LogControl.logInfo("卡箱标签卡写卡失败:" + cart);
                    i = m1Control.write(cart, true, true, false, false);
                } else if ("取消".equals(str)) {
                    LogControl.logInfo("卡箱标签卡写卡失败:" + cart);
                    return -1;
                } else {
                    keyboard.sendAlert();
                }
            }
            cardOrderSet.clear();//清空卡箱内卡顺序集合
            recordOrderTemp(cardOrderSet, PATH1);//记录临时文件
            LogControl.logInfo("卡箱标签卡写卡成功");
            cartInfo.setStatus(ShortUtils.parseString(cart.getStatus()));
            cartInfo.setCurOpLaneNo("");
            cartInfo.setCurOpTime(cart.getDhm());
            cartInfo.setOpTime(new Date());
            LaneListUtils.generationCardBoxStockList(cartInfo);//记录所取出卡箱的信息
            //生成卡箱流动单时需要将卡箱内卡数量清0
            int lostNum = 0;//丢失卡数量
            if (Lane.getInstance().isEntry()) {//入口丢失卡数量为取出卡箱时当前卡箱剩余卡数量
                lostNum = IntegerUtils.parseString(cartInfo.getCardOrderNum());
            }
            cartInfo.setCartCycleNum(cart.getCartCycleNum());//更新卡箱循环号
            cartInfo.setCardOrderNum(cart.getCscNum());//更新卡箱内卡数量
            generateCadboxList(cartInfo.getCurOpTime(), CartOpType.SEND, cartInfo.getCartStaffId(), FlowControl.getStaffId(), lostNum);
            if (FlowControl.getStaffId() != null) {//当前已上班
                //取出卡箱时删除临时记录
                TempControl.getSingleInstance().deleteTempCardBoxoplist();
            }
            setCartInfo(null);
            updateCartInfo(cartInfo);
            LogControl.logInfo("等待放好卡机收发帽并合上保护盖");
            extJFrame.updateInfo("等待放好卡机收发帽并合上保护盖", "放好卡机收发帽并合上保护盖");
            cartSvc.waitForReceiveHeadCovered();                                //等待放好卡机收发帽
            cartSvc.waitForProtectCoverLocked();                                //等待合上卡机保护盖
            cicm.protectiveCoverLockUp();                                            //锁住卡机保护盖
            return 2;
        } catch (Exception ex) {
            LogControl.logInfo("卡箱操作出现异常", ex);
            return -1;
        } finally {
            extJFrame.updateInfo("等待收发针上升", "等待收发针上升");
            LogControl.logInfo("等待收发针上升");
//            cicm.receivePinRaise();
            waitCicmPinTop();//等待收发针升到顶
            if (lane.isExit()) {//出口收发针下降两格
                cicm.lowerTwoSteps();
            }
        }
    }

    /**
     * 更换卡箱流程（该流程实际上是由取出卡箱和装入卡箱两部分组成）
     *
     * @return 1操作成功 0 取消操作 -1 其他异常 2 强行取出卡箱
     */
    private int runChangeCart() throws Exception {
        LogControl.logInfo("更换卡箱");
        LogControl.logInfo("等待出示卡箱传递员身份卡");
        try {
            CartControlService cartSvc = new CartControlService(extJFrame, cicm, m1Control, keyboard);
            Card idCard;
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                extJFrame.updateInfo("等待出示卡箱传递员身份卡", "将卡箱传递员的身份卡放到天线上");
                idCard = cartSvc.waitForStaffCard(lane);                        //出示卡箱传递员身份卡
                if (idCard == null) {                                           //用户取消操作
                    LogControl.logInfo("取消卡箱操作");
                    return 0;
                } else if (!StringUtils.encodeID(idCard.getId()).equals(cartInfo.getCartStaffId())) {
                    extJFrame.updateInfo("", "当前卡箱传递员与当前卡箱标签卡不符");
                    Thread.sleep(2000);
                } else {
                    break;
                }
            }
            LogControl.logInfo("读取卡箱传递员身份卡:" + idCard);
            LogControl.logInfo("等待卡箱收发针降到底");
            extJFrame.updateInfo("等待卡箱收发针降到底", "等待卡箱收发针降到底");
//            cicm.receivePinLower();                                             //收发针下降
//            cartSvc.waitForReceivePinBottom();                                  //等待收发针降到底
            int pin = waitCicmPinBottom();//等待收发针降到底
            if (pin == -1) {
                LogControl.logInfo("取消卡箱操作");
                return 0;
            }
            cicm.protectiveCoverOpen();                                         //解锁卡机保护盖
            LogControl.logInfo("等待打开卡机保护盖");
            extJFrame.updateInfo("等待打开卡机保护盖", "打开卡机保护盖");
            if (!cartSvc.waitForProtectCoverUnlocked()) {                       //确认卡机保护盖已打开
                LogControl.logInfo("取消卡箱操作");
                cicm.protectiveCoverLockUp();                                   //用户取消操作,锁住卡机保护盖
                return 0;
            }
            LogControl.logInfo("等待出示卡箱标签");
            Card cart;
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                extJFrame.updateInfo("等待出示卡箱标签", "将卡箱标签卡放到天线上");
                cart = cartSvc.waitForCartCard(lane);                              //出示卡箱标签卡
                if (cart == null) {                                                //用户取消操作
                    LogControl.logInfo("取消卡箱操作");
                    return 0;
                } else if (!cart.getId().equals(cartInfo.getCartId())) {
                    extJFrame.updateInfo("", "当前卡箱标签卡与当前卡箱不一致，请出示正确卡箱标签卡");
                    Thread.sleep(2000);
                } else {
                    break;
                }
            }
            LogControl.logInfo("读取卡箱标签卡:" + cart);
//            cart.setLaneId(lane.getLaneId());
            cart.setLane(lane);
            cart.setDhm(new Date(System.currentTimeMillis()));
            cart.setStatus("04");//站内流通
            if (lane.isEntry()) {//入口取出卡箱时卡箱循环数加1
                cart.setCartCycleNum(IntegerUtils.parseString(cart.getCartCycleNum()) + 1 + ""); //卡箱循环数加1
            }
            cart.setWritingNum(IntegerUtils.parseString(cart.getWritingNum()) + 1 + "");     //标签卡写卡次数加1
            if (lane.isEntry()) {//入口强行取出，卡箱数量为空
                cart.setCscNum("0");
            } else if (lane.isExit()) {//出口强行取出，标签卡记录当前卡箱内卡数量
                cart.setCscNum(cartInfo.getCardOrderNum());
            }
            LogControl.logInfo("卡箱标签卡写卡：" + cart);
            int i = m1Control.write(cart, true, true, false, false);                                                   //将新的信息写入卡箱标签卡
            for (int j = 0; j < 3; j++) {
                if (i == 0) {
                    break;
                } else {//标签卡写卡失败,最多再尝试三次
                    LogControl.logInfo("卡箱标签卡写卡失败");
                    i = m1Control.write(cart, true, true, false, false);
                }
            }
            String str;
            while (i != 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                extJFrame.updateInfo("写卡失败", "卡箱标签卡写卡失败,按【确认】键重新写卡，按【取消】键退出卡箱流程");
                str = keyboard.getMessage();
                if (str == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                } else if ("确认".equals(str)) {
                    LogControl.logInfo("卡箱标签卡写卡失败:" + cart);
                    i = m1Control.write(cart, true, true, false, false);
                } else if ("取消".equals(str)) {
                    LogControl.logInfo("卡箱标签卡写卡失败:" + cart);
                    return -1;
                } else {
                    keyboard.sendAlert();
                }
            }
            cardOrderSet.clear();//清空卡箱内卡顺序集合
            recordOrderTemp(cardOrderSet, PATH1);//记录临时文件
            LogControl.logInfo("卡箱标签卡写卡成功");
            cartInfo.setStatus(ShortUtils.parseString(cart.getStatus()));
            cartInfo.setCurOpLaneNo("");
            cartInfo.setCurOpTime(cart.getDhm());
            cartInfo.setOpTime(new Date());
            LaneListUtils.generationCardBoxStockList(cartInfo);////记录所取出卡箱的信息
            cartInfo.setCartCycleNum(cart.getCartCycleNum());//更新卡箱循环号
            cartInfo.setCardOrderNum(cart.getCscNum());//更新卡箱内卡数量
            generateCadboxList(cartInfo.getCurOpTime(), CartOpType.SEND, cartInfo.getCartStaffId(), FlowControl.getStaffId(), 0);
            if (FlowControl.getStaffId() != null) {//当前已上班
                //取出卡箱后删除临时记录
                TempControl.getSingleInstance().deleteTempCardBoxoplist();
            }
            setCartInfo(null);
            updateCartInfo(cartInfo);
            extJFrame.updateInfo("完成取出卡箱", "取出卡箱" + cart.getId() + "\n按【确认】键以继续\n或\n按【取消】键以结束操作");
            LogControl.logInfo("完成取出卡箱");
            outer:
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                switch (keyboard.waitForOrder()) {
                    case "确认":
                        break outer;
                    case "取消":
                        extJFrame.updateInfo("等待放好卡机收发帽并合上保护盖", "放好卡机收发帽并合上保护盖");
                        cartSvc.waitForReceiveHeadCovered();                        //等待放好卡机收发帽
                        cartSvc.waitForProtectCoverLocked();                        //等待合上卡机保护盖
                        cicm.protectiveCoverLockUp();                                    //锁住卡机保护盖
                        return 2;
                    default:
                        keyboard.sendAlert();
                }
            }
            i = runLoadCart();
            if (i == 3) {
                return 1;
            } else if (i == 0) {
                return 2;
            } else {
                return i;
            }
        } catch (Exception ex) {
            LogControl.logInfo("卡箱操作出现异常", ex);
            return -1;
        } finally {
            extJFrame.updateInfo("等待收发针上升", "等待收发针上升");
            LogControl.logInfo("等待收发针上升");
//            cicm.receivePinRaise();
            waitCicmPinTop();//等待收发针升到顶
            if (lane.isExit()) {//出口收发针下降两格
                cicm.lowerTwoSteps();
            }
        }
    }

    /**
     * 从配置文件Cartridge.properties中加载卡箱信息cartInfo 王国栋 2014-9-10
     *
     * @return 返回配置文件中记录的cartInfo，若无信息，则返回null
     */
    private CartInfo loadCartInfo() {
        FileInputStream fis = null;
        CartInfo ci = null;
        Properties prop = new Properties();
        try {
            LogControl.logInfo("加载卡箱信息");
            String path = Constant.PROP_MTCLANE_CARTRIDGE;
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }

            fis = new FileInputStream(path);
            prop.load(fis);
        } catch (Exception ex) {
            LogControl.logInfo("加载卡箱信息时出现异常", ex);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    LogControl.logInfo("加载卡箱信息时出现异常", ex);
                }
            }
        }
        String cartId = prop.getProperty("id");                                 //检测标志位确认卡箱是否已装入
        if (cartId != null && !"...".equals(cartId)) {//1表示已装入卡箱
            ci = new CartInfo();
            ci.setCartId(prop.getProperty("id", "..."));
            ci.setTotalCardQty(prop.getProperty("totalCscQty", "0"));
            ci.setCardOrderNum(prop.getProperty("cscOrderNum", "0"));
            ci.setCartCycleNum(prop.getProperty("cycleNum", "0"));
            ci.setCartStaffId(prop.getProperty("cartStaffId", "000000"));
            ci.setCardSerial(prop.getProperty("cardSerial", "0000000000"));
            ci.setSpare("0");
            ci.setStatus(ShortUtils.parseString(prop.getProperty("status", "00")));
            ci.setCurOpRoadid(ShortUtils.parseString(prop.getProperty("curOpRoadid", "00")));
            ci.setCurOpStationid(ShortUtils.parseString(prop.getProperty("curOpStationid", "00")));
            ci.setCurOpLaneNo(prop.getProperty("curOpLaneNo", "000"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                Date date = sdf.parse(prop.getProperty("curOpTime", "19700101000000"));
                if (date.getTime() <= 0) {
                    date = new Date();
                }
                ci.setCurOpTime(date);
            } catch (ParseException ex) {
                LogControl.logInfo(ex.getMessage(), ex);
            }
        }
        updateCartInfo(ci);
        return ci;
    }

    /**
     * 将卡箱信息cartInfo写入配置文件Cartridge.properties中
     */
    private void recordCartInfo(CartInfo cartInfo) {
        if (cartInfo != null) {
            cartInfo.setOpTime(new Date());
        }
        LogControl.logInfo("记录卡箱信息:" + cartInfo);
        String path = Constant.PROP_MTCLANE_CARTRIDGE;
        Properties prop = new Properties();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fc = null;
        try {
            fis = new FileInputStream(path);
            prop.load(new FileInputStream(path));
            fis.close();
            if (cartInfo == null || cartInfo.getCartId() == null) {
                prop.setProperty("id", "...");
                prop.setProperty("totalCscQty", "0");
                prop.setProperty("cscOrderNum", "0");
                prop.setProperty("cartStaffId", "00000");
                prop.setProperty("cycleNum", "0");
                prop.setProperty("curOpRoadid", "00");
                prop.setProperty("curOpStationid", "00");
                prop.setProperty("curOpLaneNo", "000");
                prop.setProperty("status", "0");
                prop.setProperty("curOpTime", "00000000000000");
                prop.setProperty("cardSerial", "0000000000");
                prop.setProperty("spare", "");
            } else {
                String cartId = cartInfo.getCartId();
                cartId = cartId == null ? "..." : cartId;
                prop.setProperty("id", cartId);
                String cartQty = cartInfo.getTotalCardQty();
                cartQty = cartQty == null ? "0" : cartQty;
                prop.setProperty("totalCscQty", cartQty);
                String cartNum = cartInfo.getCardOrderNum();
                cartNum = cartNum == null ? "0" : cartNum;
                prop.setProperty("cscOrderNum", cartNum);
                String cartStaffId = cartInfo.getCartStaffId();
                prop.setProperty("cartStaffId", cartStaffId == null ? "00000" : cartStaffId);
                String cycleNum = cartInfo.getCartCycleNum();
                prop.setProperty("cycleNum", cycleNum == null ? "0" : cycleNum);
                prop.setProperty("curOpRoadid", "" + cartInfo.getCurOpRoadid());
                prop.setProperty("curOpStationid", "" + cartInfo.getCurOpStationid());
                prop.setProperty("curOpLaneNo", cartInfo.getCurOpLaneNo());
                prop.setProperty("status", "" + cartInfo.getStatus());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                prop.setProperty("curOpTime", sdf.format(cartInfo.getCurOpTime()));
                prop.setProperty("cardSerial", cartInfo.getCardSerial());
                prop.setProperty("spare", "");
                LaneListUtils.generationCardBoxStockList(cartInfo);
            }
            Iterator it = prop.keySet().iterator();
            StringBuilder sb = new StringBuilder();
            while (it.hasNext()) {
                String str = (String) it.next();
                sb.append(str).append("=").append(prop.getProperty(str)).append("\r\n");
            }
            fos = new FileOutputStream(path);
            fc = fos.getChannel();
            fc.write(ByteBuffer.wrap(sb.toString().getBytes()));//使用nio写文件，强制刷新将文件内容写入设备
            fc.force(true);
        } catch (Exception ex) {
            LogControl.logInfo("记录卡箱信息时出错:", ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fc != null) {
                    fc.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException ex) {
                LogControl.logInfo("记录卡箱信息时出错:", ex);
            }
        }
    }

    /**
     * 更新卡箱信息显示，并将卡箱信息写入配置文件中
     *
     * @param cartInfo 卡箱信息
     */
    private void updateCartInfo(CartInfo cartInfo) {
        if (cartInfo == null) {//本车道尚未装入卡箱
            extJFrame.updateCartId("...");
            extJFrame.updateTotalCartNum("0");
            extJFrame.updateCartNum("0");
            recordQueue.add(new CartInfo());
        } else {
            extJFrame.updateCartId(cartInfo.getCartId());
            extJFrame.updateTotalCartNum(cartInfo.getTotalCardQty());
            extJFrame.updateCartNum(cartInfo.getCardOrderNum());
            recordQueue.add(cartInfo);
        }
//        recordCartInfo(cartInfo);
    }

    /**
     * 更新卡箱内卡数量，出口增加，入口减少
     *
     * @param num 变化的数量正数为增加，负数为减少
     */
    public void updateCartNum(int num) {
        try {
            String cartNum = cartInfo.getCardOrderNum();
            if (cartNum == null) {
                return;
            }
            int temp = IntegerUtils.parseString(cartNum);
            temp = temp + num;
            if (temp < 0) {
                temp = 0;
            }
            cartInfo.setCardOrderNum(temp + "");
            updateCartInfo(cartInfo);
        } catch (Exception ex) {
            LogControl.logInfo("更新卡箱内卡数量时出现异常", ex);
        }
    }

    /**
     * 检测当前是否有卡箱 出口检查卡箱内数量是否已满，如已满，需要提示收费员更换卡箱 入口检查卡箱内数量是否已空，如已空，需要提示收费员更换卡箱
     *
     */
//    private void checkCartQty() {
//        if (cartInfo == null) {
//            if (lane.isEntry()) {
//                errorMsg = "50";//入口无卡箱报警序号
//            } else if (lane.isExit()) {
//                errorMsg = "27";//出口无卡箱报警序号
//            }
//            LogControl.logInfo("无卡箱");
//            String str;
//            while (true) {
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException ex) {
//                }
//                extJFrame.updateInfo("无卡箱", "请按【卡机】键执行装入卡箱操作");
//                str = keyboard.waitForOrder();
//                if ("卡机".equals(str)) {
//                    int i = run(1,0);
//                    checkCartQty();
//                    break;
//                } else {
//                    keyboard.sendAlert();
//                }
//            }
//            return;
//        }
//        if (TestControl.isCardStatusIgnored()) {
//            LogControl.logInfo("测试模式，不对卡箱容量进行判断");
//            return;
//        }
//        int totalQty = IntegerUtils.parseString(cartInfo.getTotalCardQty());
//        int orderNum = IntegerUtils.parseString(cartInfo.getCardOrderNum());
//        if (lane.isExit()) {//出口
//            if (orderNum >= totalQty) {
//                errorMsg = "24";//出口车道卡箱已满报警序号
//                LogControl.logInfo("出口车道卡箱已满");
//                String str;
//                while (true) {
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException ex) {
//                    }
//                    extJFrame.updateInfo("出口车道卡箱已满", "卡箱已满，请更换卡箱");
//                    str = keyboard.waitForOrder();
//                    if ("卡机".equals(str)) {
//                        int i = run(2,0);
//                        checkCartQty();
//                        break;
//                    } else {
//                        keyboard.sendAlert();
//                    }
//                }
//            } else if (orderNum + 50 > totalQty) {
//                errorMsg = "23";//出口车道卡箱将满报警序号
//            } else {
//                errorMsg = null;
//            }
//        } else if (lane.isEntry()) {//入口
//            if (orderNum == 0) {
//                LogControl.logInfo("入口车道卡箱已空");
//                errorMsg = "45";//入口车道卡箱已空报警序号
//                String str;
//                while (true) {
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException ex) {
//                    }
//                    extJFrame.updateInfo("入口车道卡箱已空", "卡箱已空，请更换卡箱");
//                    str = keyboard.waitForOrder();
//                    if ("卡机".equals(str)) {
//                        int i = run(3,0);
//                        checkCartQty();
//                        break;
//                    } else {
//                        keyboard.sendAlert();
//                    }
//                }
//            } else if (orderNum < 50) {
//                errorMsg = "44";//入口车道卡箱将空报警序号
//            } else {
//                errorMsg = null;
//            }
//        }
//
//    }
    String errorMsg;//卡箱异常信息报警序号

    /**
     * 获取卡箱异常信息报警序号
     *
     * @return 异常信息
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 将卡箱总数设为当前卡箱内卡数量
     */
    public void reInitTotalCardQty() {
        if (cartInfo != null) {
            String totalCardQty = cartInfo.getCardOrderNum();
            cartInfo.setTotalCardQty(totalCardQty);
            updateCartInfo(cartInfo);
        }
    }

    /**
     * 检测通行卡的卡箱标签卡号码，卡箱循环次数，以及顺序号是否符合要求
     *
     * @param cartId 通行卡内记录的卡箱标签卡号码
     * @param cycleNum 通行卡内记录的卡箱循环次数
     * @param orderNum 通行卡内记录的顺序号
     * @return true符合要求 false 不符合要求
     */
    public boolean checkCscCard(String cartId, String cycleNum, String orderNum) {
        if (cartId == null || cycleNum == null || orderNum == null) {
            return false;
        }
        boolean flag1 = cartId.equals(cartInfo.getCartId());//检测卡箱标签卡号码
//        boolean flag2 = cycleNum.equals(cartInfo.getCartCycleNum());//检测卡箱循环次数
        boolean flag2 = true;//为兼容卡箱循环次数错误，此标志位暂时不检测
        int a1 = IntegerUtils.parseString(orderNum);
        int a2 = IntegerUtils.parseString(cartInfo.getTotalCardQty());
        a2 = a2 > 500 ? a2 : 500;
        boolean flag3 = a1 <= a2;//检测当前卡顺序号与当前卡箱标签卡容量
        boolean flag4 = false;
        if (flag1 && flag2 && flag3) {//通过前三次检验的才检验卡顺序号
            flag4 = !cardOrderSet.contains(a1);//检测当前卡顺序号是否已经出现过
            if (flag4) {
                cardOrderSet.add(a1);//将卡顺序号记录入缓存
                recordOrderTemp(cardOrderSet, PATH1);//将卡顺序号记录入临时文件
            }
        }
        return flag1 && flag2 && flag3 && flag4;
    }

    /**
     * 记录通行卡顺序号临时文件
     *
     * @param set 通行卡顺序号
     * @param filePath 临时文件路径
     */
    private void recordOrderTemp(Set<Integer> set, String filePath) {
        FileOutputStream fos = null;
        FileChannel fc = null;
        try {
            fos = new FileOutputStream(filePath);
            fc = fos.getChannel();
            StringBuilder sb = new StringBuilder();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                int i = (Integer) it.next();
                sb.append(i).append(",");//将集合中的数据拼接成字符串并以逗号隔开
            }
            if (sb.indexOf(",") > 0) {
                sb.delete(sb.length() - 1, sb.length());//删除最后一位逗号
            }
            ByteBuffer bb = ByteBuffer.wrap(sb.toString().getBytes());
            fc.write(bb);
            fc.force(true);//强制刷新文件内容
            fos.flush();
        } catch (Exception ex) {
            LogControl.logInfo("记录通行卡顺序号临时文件异常", ex);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (fc != null) {
                    fc.close();
                }
            } catch (Exception ex) {
                LogControl.logInfo("异常", ex);
            }
        }
    }

    /**
     * 通过解析临时文件获取当前卡箱已发出的通行卡的顺序号
     */
    private Set<Integer> getCardOrder(String filePath) {
        Set<Integer> set = new HashSet();//记录卡箱内卡顺序号的集合
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                return set;
            }
            fis = new FileInputStream(file);//解析临时文件
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String str = br.readLine();//获取文件记录卡顺序号
            if (str == null) {//临时文件为空
                return set;
            }
            String[] buffer = str.split(",");
            for (String buffer1 : buffer) {
                if (buffer1.matches("[0-9]*")) {
                    set.add(IntegerUtils.parseString(buffer1));//将卡顺序号解析后记录入缓存中
                }
            }
        } catch (Exception ex) {
            LogControl.logInfo("解析临时文件获取卡箱内卡顺序集合异常", ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                LogControl.logInfo("异常", ex);
            }
        }
        return set;

    }

    class RecordTask extends MyTask {

        public void run() {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
                CartInfo cartInfo = recordQueue.poll();
                if (cartInfo != null) {
                    recordCartInfo(cartInfo);
                }
            }
        }
    }

    /**
     * 产生卡箱流动单流水
     *
     * @param optime 时间
     * @param optype 操作类型 0发送 1接收
     * @param receiver 接收者
     * @param sender 发送者
     * @param lostNum 丢失的卡数量
     */
    public void generateCadboxList(Date optime, int optype, String receiver, String sender, int lostNum) {
        CartInfo cartInfo = getCartInfo();
        if (cartInfo == null) {
            return;
        }
        CardBoxOpList list = new CardBoxOpList();
        list.setCardBoxCycleTimes(NullUtils.parseNull(cartInfo.getCartCycleNum()));
        list.setCardBoxLabelNo(NullUtils.parseNull(cartInfo.getCartId()));
        list.setCardBoxNo(NullUtils.parseNull(cartInfo.getCartId()));
        list.setCardNum(IntegerUtils.parseString(cartInfo.getCardOrderNum()));
        list.setOpTime(optime);
        list.setOpType(optype);
        list.setRecLaneNo(NullUtils.parseNull(Lane.getInstance().getLaneId()));
        list.setRecRoadId(IntegerUtils.parseString(Lane.getInstance().getRoadId()));
        list.setRecStationId(IntegerUtils.parseString(Lane.getInstance().getStationId()));
        list.setReceiverNo(NullUtils.parseNull(receiver));
        list.setSenderNo(NullUtils.parseNull(sender));
        list.setSpare1(-1);//spare1字段采用默认值-1
        list.setSpare2(lostNum);//丢失卡数量
        list.setSpare3(0);
        list.setSpare4(NullUtils.parseNull(cartInfo.getSpare()));//额外卡数量
        list.setStatus((int) cartInfo.getStatus());
        LogControl.logInfo("记录卡箱流动单" + list);
        try {
            LaneListUtils.generationCardBoxOpList(list);
        } catch (Exception ex) {
            LogControl.logInfo("产生卡箱流动单时出现异常", ex);
        }
    }

    /**
     * 产生卡箱流动单流水（交接卡箱时使用）
     *
     * @param cart 卡箱标签卡
     * @param optype 操作类型 0发送 1接收
     * @param receiver 接收者
     * @param sender 发送者
     * @param lostNum 丢失的卡数量
     */
    public void generateCadboxList(Card cart, int optype, String receiver, String sender, int lostNum) {
        CardBoxOpList list = new CardBoxOpList();
        list.setCardBoxCycleTimes(NullUtils.parseNull(cart.getCartCycleNum()));
        list.setCardBoxLabelNo(NullUtils.parseNull(cart.getId()));
        list.setCardBoxNo(NullUtils.parseNull(cart.getId()));
        list.setCardNum(IntegerUtils.parseString(cart.getCscNum()));
        list.setOpTime(cart.getDhm());
        list.setOpType(optype);
        list.setRecLaneNo(NullUtils.parseNull(cart.getLaneId()));
        list.setRecRoadId(IntegerUtils.parseString(cart.getRoadid()));
        list.setRecStationId(IntegerUtils.parseString(cart.getStationid()));
        list.setReceiverNo(NullUtils.parseNull(receiver));
        list.setSenderNo(NullUtils.parseNull(sender));
        list.setSpare1(-1);//spare1字段采用默认值-1
        list.setSpare2(lostNum);//丢失卡数量
        list.setSpare3(0);
        list.setSpare4("0");//额外卡数量
        list.setStatus(IntegerUtils.parseString(cart.getStatus()));
        LogControl.logInfo("记录卡箱流动单" + list);
        try {
            LaneListUtils.generationCardBoxOpList(list);
        } catch (Exception ex) {
            LogControl.logInfo("产生卡箱流动单时出现异常", ex);
        }
    }

    /**
     * 产生临时卡箱流动单流水
     *
     * @param optime 时间
     * @param optype 操作类型 0发送 1接收
     * @param receiver 接收者
     * @param sender 发送者
     * @param lostNum 丢失的卡数量
     */
    public void generateTempCadboxList(Date optime, int optype, String receiver, String sender, int lostNum) {
        CartInfo cartInfo = getCartInfo();
        if (cartInfo == null) {
            return;
        }
        CardBoxOpList list = new CardBoxOpList();
        list.setCardBoxCycleTimes(NullUtils.parseNull(cartInfo.getCartCycleNum()));
        list.setCardBoxLabelNo(NullUtils.parseNull(cartInfo.getCartId()));
        list.setCardBoxNo(NullUtils.parseNull(cartInfo.getCartId()));
        list.setCardNum(IntegerUtils.parseString(cartInfo.getCardOrderNum()));
        list.setOpTime(optime);
        list.setOpType(optype);
        list.setRecLaneNo(NullUtils.parseNull(Lane.getInstance().getLaneId()));
        list.setRecRoadId(IntegerUtils.parseString(Lane.getInstance().getRoadId()));
        list.setRecStationId(IntegerUtils.parseString(Lane.getInstance().getStationId()));
        list.setReceiverNo(NullUtils.parseNull(receiver));
        list.setSenderNo(NullUtils.parseNull(sender));
        list.setSpare1(-1);//spare1字段采用默认值-1
        list.setSpare2(lostNum);//丢失卡数量
        list.setSpare3(0);
        list.setSpare4(NullUtils.parseNull(cartInfo.getSpare()));//额外卡数量
        list.setStatus((int) cartInfo.getStatus());
        LogControl.logInfo("记录卡箱流动单" + list);
        try {
            TempControl.getSingleInstance().generateTempCardBoxoplist(list);
        } catch (Exception ex) {
            LogControl.logInfo("产生卡箱流动单时出现异常", ex);
        }
    }

}