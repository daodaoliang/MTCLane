package com.hgits.hardware.impl.icreaderwriter;

import com.hgits.control.ThreadPoolControl;
import com.hgits.hardware.IcReaderWriter;
import com.hgits.task.MyTask;
import com.hgits.tool.driver.Mifare014Svc;
import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.ParseUtil;
import com.hgits.vo.Card;
import com.hgits.vo.Constant;
import com.hgits.vo.Mifare014Order;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Mifare014读卡器
 *
 * @author Wang Guodong
 */
public class Mifare014 implements IcReaderWriter {

    private boolean running;//运行标识
    private final Logger logger = Logger.getLogger(Mifare014.class.getName());
    private Card cardr;//读卡
    private Card cardw;//写卡
    private boolean sec5Flag;//05区写卡标识
    private boolean sec6Flag;//06区写卡标识
    private boolean sec8Flag;//08区写卡标识
    private boolean sec9Flag;//09区写卡标识
    private final String deviceName = "Mifare014";//设备名称
    private final Object obj = new Object();//加锁对象
    private String errorMsg;

    @Override
    public Byte getIndex() {
        return 0;
    }

    @Override
    public void increaseIndex() {
    }

    @Override
    public void setHasCard(boolean hasCard) {
    }

    @Override
    public boolean getHasCard() {
        return false;
    }

    @Override
    public boolean getCardIsRead() {
        return false;
    }

    @Override
    public void setCardIsRead(boolean cardIsRead) {
    }

    @Override
    public String getCardNo() {
        return null;
    }

    @Override
    public void setCardNo(String cardNo) {
    }

    @Override
    public boolean isFlag42() {
        return false;
    }

    @Override
    public void setFlag42(boolean flag42) {
    }

    @Override
    public void setCardIsWritten(boolean cardIsWritten) {
    }

    @Override
    public void setCard(Card card) {
        synchronized (obj) {
            this.cardr = card;
        }
    }

    @Override
    public Card getReadedCard() {
        synchronized (obj) {
            if (cardr != null) {
                return new Card(cardr);
            } else {
                return null;
            }
        }
    }

    @Override
    public Card getCardObj() {
        return null;
    }

    @Override
    public synchronized int write(Card card, boolean sec5Flag, boolean sec6Flag, boolean sec8Flag, boolean sec9Flag) {
        this.sec5Flag = sec5Flag;
        this.sec6Flag = sec6Flag;
        this.sec8Flag = sec8Flag;
        this.sec9Flag = sec9Flag;
        setCardW(new Card(card));
        synchronized (obj) {
            obj.notify();
        }
        long start = System.currentTimeMillis();
        int result = -1;
        while (running) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            long now = System.currentTimeMillis();
            if (now - start > 2000) {
                logger.warn("写卡超时，结束写卡等待");
                break;
            }
            if (cardr == null) {//找不到卡
                return 1;
            }
            if (cardw == null) {//写卡成功
                result = 0;
                break;
            }
        }
        setCardW(null);
        return result;
    }

    @Override
    public int exit() {
        running = false;
        return 0;
    }

    @Override
    public String getErrorMessage() {
        return errorMsg;
    }

    @Override
    public void write(byte[] buffer) {
    }

    @Override
    public void write(byte b) {
    }

    @Override
    public Byte readByte() {
        return null;
    }

    @Override
    public byte[] readByteArray() {
        return null;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void setCardW(Card cardw) {
        synchronized (obj) {
            this.cardw = cardw;
        }

    }

    @Override
    public void run() {
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_COMM, "MIF", null);
        if (str == null) {
            logger.warn("未启用" + deviceName);
            return;
        }
        str = str.replaceAll("，", ",");//中文逗号改为英文
        String[] buffer = str.split(",");
        if (buffer.length < 7) {
            logger.warn("启用" + deviceName + ",但是配置" + str + "异常,配置项不足7个");
            errorMsg = "M1卡天线配置错误";
            return;
        }
        String com = buffer[0].trim();
        String str1 = buffer[1].trim();
        if (!str1.matches("\\d*")) {
            logger.warn("启用" + deviceName + ",但baudrate=" + str1 + "配置错误");
            errorMsg = "M1卡天线配置错误";
            return;
        }
        int baudrate = Integer.parseInt(str1);
        String str2 = buffer[2].trim();
        if (!str2.matches("\\d*")) {
            logger.warn("启用" + deviceName + ",但databit=" + str2 + "配置错误");
            errorMsg = "M1卡天线配置错误";
            return;
        }
        int databit = Integer.parseInt(str2);
        String str3 = buffer[3].trim();
        if (!str3.matches("\\d*")) {
            logger.warn("启用" + deviceName + ",但stopbit=" + str3 + "配置错误");
            errorMsg = "M1卡天线配置错误";
            return;
        }
        int stopbit = Integer.parseInt(str3);
        String str4 = buffer[4].trim();
        if (!str4.matches("\\d*")) {
            logger.warn("启用" + deviceName + ",但parity=" + str4 + "配置错误");
            errorMsg = "M1卡天线配置错误";
            return;
        }
        int parity = Integer.parseInt(str4);
        String str6 = buffer[6].trim();
        if (!str6.matches("\\d*")) {
            logger.warn("启用" + deviceName + ",但logLevel=" + str6 + "配置错误");
            errorMsg = "M1卡天线配置错误";
            return;
        }
        errorMsg = null;
        int logLevel = Integer.parseInt(str6);
        logger.warn("启用" + deviceName + ",com=" + com + ",baudrate=" + baudrate + ",databit=" + databit + ",stopbit=" + stopbit + ",parity=" + parity + ",loglevel=" + logLevel);
        running = true;
        SendTask sendTask = new SendTask(com, baudrate, databit, stopbit, parity, logLevel);
        ThreadPoolControl.getThreadPoolInstance().execute(sendTask);
        MonitorTask monitorTask = new MonitorTask(sendTask);
        ThreadPoolControl.getThreadPoolInstance().execute(monitorTask);
    }

    class SendTask extends MyTask {

        Mifare014Svc svc = new Mifare014Svc();
        private final String com;//串口号
        private final int baudrate;///波特率
        private final int databit;//数据位
        private final int stopbit;//停止位
        private final int parity;//校验位
        private final int logLevel;//日志级别
        private String key01A;//当前卡已经验证过的01扇区A密钥，
        private String key01B;//当前卡已经验证过的01扇区B密钥
        private String key02A;//当前卡已经验证过的02扇区A密钥
        private String key02B;//当前卡已经验证过的02扇区B密钥

        public SendTask(String com, int baudrate, int databit, int stopbit, int parity, int logLevel) {
            this.com = com;
            this.baudrate = baudrate;
            this.databit = databit;
            this.stopbit = stopbit;
            this.parity = parity;
            this.logLevel = logLevel;
        }

        @Override
        public void run() {
            try {
                setAlive(true);
                logger.warn(deviceName + "通信线程启动");
                int i = svc.openDevice(com, baudrate, databit, stopbit, parity, logLevel);
                if (i != 0) {
                    logger.error(deviceName + "打开串口失败");
                    errorMsg = "M1卡天线通信异常,打开串口失败";
                    running = false;
                    return;
                }
                running = true;
                String version = svc.getVersion();
                logger.warn(deviceName + "读卡器版本：" + version);
                svc.loadKey05();
                svc.loadKey06();
                svc.loadKey10();
                String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "MIFARE014_INTERVAL", "1000");
                int interval = IntegerUtils.parseString(str);
                if (interval <= 0) {
                    interval = 1000;
                }
                while (running) {
                    synchronized (obj) {
                        obj.wait(interval);
                    }
                    String serial = svc.queryCard();
                    if ("-1".equals(serial)) {//读卡器无响应
                        initKey();//重置密钥
                        setCard(null);
                        logger.warn(deviceName + "读卡器无响应");
                        errorMsg = "M1卡天线通信异常";
                        return;
                    } else if ("0".equals(serial)) {//无卡
                        initKey();//重置密钥
                        errorMsg = null;
                        setCard(null);
                        continue;
                    } else if (checkSerial(cardr, serial)) {//当前卡已读
                        errorMsg = null;
                    } else {
                        initKey();//重置密钥
                        errorMsg = null;
                        logger.warn(deviceName + "发现新M1卡" + serial);
                        Card tempCard = readCard(serial);
                        setCard(tempCard);
                        tempCard = null;
                    }
                    if (cardw != null) {
                        boolean flag = writeCard(cardw);
                        svc.POFF();
                        svc.PON();
                        Card tempCard = readCard(serial);
                        setCard(tempCard);
                        tempCard = null;
                        if (flag || checkCard(cardr, cardw)) {//写卡成功
                            setCardW(null);
                        }
                    }
                }
            } catch (Throwable t) {
                logger.error(deviceName + "通信线程异常", t);
            } finally {
                setCard(null);
                setCardW(null);
                logger.warn(deviceName + "通信线程结束");
                svc.closeDevice();//关闭设备
                setAlive(false);
            }

        }

        /**
         * 判断卡序列号与是否对应当前卡
         *
         * @param card 当前卡
         * @param serial 卡序列号
         * @return true/false
         */
        private boolean checkSerial(Card card, String serial) {
            synchronized (obj) {//加锁，防止在判断过程中card内容发生变化
                if (card == null) {
                    return false;
                }
                String str = card.getCardSerial();
                if (str == null) {
                    return false;
                }
                return str.equalsIgnoreCase(serial);
            }
        }

        /**
         * 判断两张卡内容是否相同
         *
         * @param card1 卡1
         * @param card2 卡2
         * @return true/false
         */
        private boolean checkCard(Card card1, Card card2) {
            synchronized (obj) {
                if (card1 == null) {
                    return false;
                }
                return card1.equals(card2);
            }
        }

        /**
         * 读卡
         *
         * @return 读到的卡
         */
        private Card readCard(String serial) {
            int i = svc.openCard(serial);//打开指定M1卡
            if (i != 1) {
                return null;
            }
            if (!authKeyNew(1)) {//1扇区密钥认证失败
                return null;
            }
            String str4 = svc.readBLock(4);//读取04块数据
            String str5 = svc.readBLock(5);//读取05块数据
            String str6 = svc.readBLock(6);//读取06块数据
            if (str4 == null || str5 == null || str6 == null) {//读卡失败
                return null;
            }
            Card tempCard = new Card();
            tempCard.setCardSerial(serial);
            tempCard.setSection_04_info(str4);
            tempCard.setSection_05_info(str5);
            tempCard.setSection_06_info(str6);
            ParseUtil.parse04(tempCard);//解析04块数据
            ParseUtil.parse05(tempCard);//解析05块数据
            ParseUtil.parse06(tempCard);//解析06块数据
            String cardType = tempCard.getType();
            if (Constant.CARD_TYPE_01.equals(cardType)) {//通行卡才对2扇区数据进行读取
                boolean flag = authKeyNew(2);//验证2扇区密钥
                if (flag) {
                    String str08 = svc.readBLock(8);//读取08块数据
                    String str09 = svc.readBLock(9);//读取09块数据
                    if (str08 != null) {
                        tempCard.setSection_08_info(str08);
                        ParseUtil.parse08(tempCard);//解析08块数据
                    }
                    if (str09 != null) {
                        tempCard.setSection_09_info(str09);
                        ParseUtil.parse09(tempCard);//解析09块数据
                    }
                }
            }
            return tempCard;
        }

        /**
         * 验证密钥，集成了寻卡，打开卡片
         *
         * @param key 密钥
         * @param sec 扇区
         * @param keyType 密钥类型
         * @return true/false
         */
        private boolean authKey(String key, int sec, String keyType) {
            String serial = svc.queryCard();
            int i = svc.openCard(serial);//打开指定M1卡
            if (i != 1) {//打开卡失败
                return false;
            }
            return svc.keyAuth(key, sec, keyType);
        }

        /**
         * 认证密钥
         *
         * @param sec 扇区
         * @return true/false
         */
        private boolean authKeyNew(int sec) {
            boolean flag = false;
            if (sec == 1) {//01扇区
                if (key01A != null) {//01扇区A密钥已认证过
                    flag = svc.keyAuth(key01A, sec, Mifare014Order.KEY_TYPE_A);
                } else if (key01B != null) {//01扇区B密钥已认证过
                    flag = svc.keyAuth(key01B, sec, Mifare014Order.KEY_TYPE_B);
                }
            } else if (sec == 2) {//02扇区
                if (key02A != null) {//02扇区A密钥已认证过
                    flag = svc.keyAuth(key02A, sec, Mifare014Order.KEY_TYPE_A);
                } else if (key02B != null) {//02扇区B密钥已认证过
                    flag = svc.keyAuth(key02B, sec, Mifare014Order.KEY_TYPE_B);
                }
            }
            if (flag) {
                return true;
            }

            //无密钥认证
            flag = svc.keyAuth(null, sec, Mifare014Order.KEY_TYPE_10);
            if (flag) {
                return true;
            }
            List<String> keyAList = new ArrayList();
            List<String> keyBList = new ArrayList();
            keyAList.add(Mifare014Order.KEY_BLOCK01_01);
            keyAList.add(Mifare014Order.KEY_BLOCK01_02);
            keyAList.add(Mifare014Order.KEY_BLOCK01_00);
            keyBList.add(Mifare014Order.KEY_BLOCK01_05);
            keyBList.add(Mifare014Order.KEY_BLOCK01_06);
            keyBList.add(Mifare014Order.KEY_BLOCK01_04);
            for (String keyA : keyAList) {
                flag = authKey(keyA, sec, Mifare014Order.KEY_TYPE_A);
                if (flag) {
                    if (sec == 1) {//01扇区A密钥认证通过
                        key01A = keyA;
                    } else if (sec == 2) {//02扇区A密钥认证通过
                        key02A = keyA;
                    }
                    return true;
                }
            }

            for (String keyB : keyBList) {
                flag = authKey(keyB, sec, Mifare014Order.KEY_TYPE_B);
                if (flag) {
                    if (sec == 1) {//01扇区A密钥认证通过
                        key01B = keyB;
                    } else if (sec == 2) {//02扇区A密钥认证通过
                        key02B = keyB;
                    }
                    return true;
                }
            }
            return flag;
        }

        /**
         * 写卡
         *
         * @param card 需要写的卡
         * @return true/false;
         */
        private boolean writeCard(Card card) {
            boolean flag = false;
            Card tempCard;
            synchronized (obj) {
                if (card == null || cardr == null) {
                    return false;
                }
                String ser1 = card.getCardSerial();
                String ser2 = cardr.getCardSerial();
                if (!ser1.equals(ser2)) {//需要写的卡与当前卡卡号不同
                    return false;
                }
                tempCard = new Card(card);
            }
            String serial = tempCard.getCardSerial();
            int i = svc.openCard(serial);//打开指定M1卡
            if (i != 1) {
                return false;
            }
            if (sec5Flag || sec6Flag) {//是否需要对1扇区数据进行写操作
                boolean keyFlag = authKeyNew(1);//验证1扇区密钥
                if (!keyFlag) {//密钥认证失败
                    return false;
                }
            }
            int result5 = 1;
            int result6 = 1;
            int result8 = 1;
            int result9 = 1;
            if (sec5Flag) {
                String str05 = ParseUtil.unParse05(tempCard);
                result5 = svc.writeBlock(5, str05);//写05块数据
            }
            if (sec6Flag) {
                String str06 = ParseUtil.unParse06(tempCard);
                result6 = svc.writeBlock(6, str06);//写06块数据
            }
            if (sec8Flag || sec9Flag) {//是否需要对2扇区数据进行写操作
                boolean keyFlag = authKeyNew(2);//验证1扇区密钥
                if (!keyFlag) {//密钥认证失败
                    return false;
                }
            }
            if (sec8Flag) {
                String str08 = ParseUtil.unParse08(tempCard);
                result8 = svc.writeBlock(8, str08);//写08块数据
            }
            if (sec9Flag) {
                String str09 = ParseUtil.unParse09(tempCard);
                result9 = svc.writeBlock(9, str09);//写09块数据
            }
            if (result5 == 1 && result6 == 1 && result8 == 1 && result9 == 1) {
                flag = true;
            }
            return flag;
        }

        /**
         * 密钥初始化
         */
        private void initKey() {
            key01A = null;
            key01B = null;
            key02A = null;
            key02B = null;
        }
    }

    class MonitorTask extends MyTask {

        private final SendTask sendTask;

        public MonitorTask(SendTask sendTask) {
            this.sendTask = sendTask;
        }

        public void run() {
            logger.warn(deviceName + "监控线程启动");
            while (running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                if (!sendTask.isAlive()) {
                    sendTask.setAlive(true);
                    ThreadPoolControl.getThreadPoolInstance().execute(sendTask);
                }
            }
            logger.warn(deviceName + "监控线程结束");
        }
    }

}
