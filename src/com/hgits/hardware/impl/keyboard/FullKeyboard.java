package com.hgits.hardware.impl.keyboard;

import MyComm.MyComm;
import java.util.HashMap;
import java.util.Map;
import com.hgits.common.log.MTCLog;
import com.hgits.control.FunctionControl;
import com.hgits.control.LogControl;
import com.hgits.control.ThreadPoolControl;
import com.hgits.hardware.Keyboard;
import com.hgits.service.KeyBoardService;
import com.hgits.task.MyTask;
import com.hgits.tool.driver.MyKeyAdapter;
import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.SetCOMs;
import com.hgits.util.file.UnicodeReader;
import com.hgits.util.keyborad.KeyBoardUitls;
import com.hgits.vo.Constant;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * 全键盘
 *
 * @author Wang Guodong
 */
public class FullKeyboard implements Keyboard {
    //监控电脑键盘

    private MyKeyAdapter mka;
    public Integer order;//键盘按键
    Map<String, String> map = new HashMap<>();//键值对集合
    Map<String, String> ceMap = new HashMap<>();//中英文对应按键集合
    boolean flag;//按键标志，当启用getSuperMessage指令时，其他获取按键指令停止响应（当多线程同时想要获取键盘按键时，以getSuperMessage优先）
    private KeyBoardService keyBoardService;
    private MyComm myComm;
    private boolean running = true;
    private static final Logger logger = Logger.getLogger(FullKeyboard.class);
    private String deviceName = "收费键盘";
    /**
     * 标准键盘个数
     */
    private static int KEY_SIZE = 29;

    /**
     * 加载按键对应信息
     */
    public void init() {
        ceMap.put("湘", "A");
        ceMap.put("粤", "B");
        ceMap.put("鄂", "C");
        ceMap.put("鲁", "D");
        ceMap.put("豫", "E");
        ceMap.put("赣", "F");
        ceMap.put("冀", "G");
        ceMap.put("皖", "H");
        ceMap.put("晋", "I");
        ceMap.put("沪", "J");
        ceMap.put("京", "K");
        ceMap.put("吉", "L");
        ceMap.put("蒙", "M");
        ceMap.put("宁", "N");
        ceMap.put("渝", "O");
        ceMap.put("藏", "P");
        ceMap.put("琼", "Q");
        ceMap.put("闽", "R");
        ceMap.put("苏", "S");
        ceMap.put("青", "T");
        ceMap.put("川", "U");
        ceMap.put("辽", "V");
        ceMap.put("甘", "W");
        ceMap.put("新", "X");
        ceMap.put("云", "Y");
        ceMap.put("浙", "Z");
        ceMap.put("陕", "黑");
        ceMap.put("津", "湘");
        ceMap.put("贵", "桂");
        map.put("A", EMER);
        map.put("B", ONDUTY);
        map.put("C", OFFDUTY);
        map.put("D", GREEN);
        map.put("E", RED);
        map.put("F", SIMU);
        map.put("G", CART);
        map.put("H", WEIGHT);
        map.put("I", IOU);
        map.put("P", CONFIRM);
        map.put("O", CANCEL);
        map.put("K", IMG);
        map.put("X", MILITARY);
        map.put("J", UP);
        map.put("M", DOWN);
        map.put("7", SEVEN);
        map.put("8", EIGHT);
        map.put("9", NINE);
        map.put(".", DOT);
        map.put("0", ZERO);
        map.put("*", ZEROZERO);
        map.put("1", ONE);
        map.put("2", TWO);
        map.put("3", THREE);
        map.put("4", FOUR);
        map.put("5", FIVE);
        map.put("6", SIX);
        map.put("N", SYSTEM);
        map.put("L", UNIONPAY);
        map.put("a", "湘");
        map.put("b", "粤");
        map.put("c", "鄂");
        map.put("d", "鲁");
        map.put("e", "豫");
        map.put("f", "赣");
        map.put("g", "冀");
        map.put("h", "皖");
        map.put("i", "晋");
        map.put("j", "沪");
        map.put("k", "京");
        map.put("l", "吉");
        map.put("m", "蒙");
        map.put("n", "宁");
        map.put("o", "渝");
        map.put("p", "藏");
        map.put("q", "琼");
        map.put("r", "闽");
        map.put("s", "苏");
        map.put("t", "青");
        map.put("u", "川");
        map.put("v", "辽");
        map.put("w", "甘");
        map.put("x", "新");
        map.put("y", "云");
        map.put("z", "浙");
        map.put("{", "陕");
        map.put("|", "津");
        map.put("~", "贵");
        map.put("@", "1型");
        map.put(">", "2型");
        map.put("<", "3型");
        map.put(":", "4型");
        map.put("-", "5型");
        map.put("Q", "6型");
        map.put("?", "7型");
        map.put("=", "8型");
        map.put(";", "9型");
        map.put(")", "中/英");
        Map<String, String> loadMap = loadKey();
        if (loadMap != null) {
            map.putAll(loadMap);
        }
        boolean isActivePCKeyBor = FunctionControl.isPcKeyboardActive();//判断是否启用的键盘
        if (isActivePCKeyBor) {
            Map<String, String> tempMap = KeyBoardUitls.getInstance()
                    .getKeyMap(); // 从配置文件里面读取键盘映射关系
            if (!tempMap.isEmpty() && tempMap.size() >= KEY_SIZE) {
                map.putAll(tempMap);
            }
        }
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void setMka(MyKeyAdapter mka) {
        this.mka = mka;
    }

    public void setKeyBoardService(KeyBoardService keyBoardService) {
        this.keyBoardService = keyBoardService;
    }

    /**
     * 构造函数,静态快之后运行,开启串口
     */
    public FullKeyboard() {

    }

    /**
     * 获取按键信息，该方法启用后，getMessage方法无效
     *
     * @return 按键信息，无按键返回null
     */
    public String getSuperMessage() {
        flag = true;
        return getKey();
    }

    /**
     * 取消对于getMessage方法的限制
     */
    public void cancelSuperMessage() {
        flag = false;
    }

    /**
     * 一直等待，直至收费键盘输入指令
     *
     * @return 返回收费键盘输入指令对应的收费键盘按键
     */
    public String waitForOrder() {
        String str;
        while ((str = getMessage()) == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
        return str;
    }

    /**
     * 获取收费键盘指令对应的按键值(包括电脑键盘按键)
     *
     * @return 返回收费键盘指令对应的按键，如果没有按键，返回null
     */
    public synchronized String getMessage() {
        if (flag) {
            return null;
        } else {
            return getKey();
        }
    }

    /**
     * 获取收费键盘指令对应的按键值(包括电脑键盘按键)
     *
     * @return 返回收费键盘指令对应的按键，如果没有按键，返回null
     */
    private synchronized String getKey() {
        String str = mka.getKey();//电脑键盘指令
        String str2 = null; //收费键盘指令
        if (str == null) {
            if (order != null) {            //如果按键不为空,也就是说已经按过键盘
//                String key = String.valueOf((char) (int) order);
                str2 = map.get(String.valueOf((char) (int) order));     //那么将按键所对应的指令赋给str
                order = null;             //随后将按键清空
            }
//            if ("确认".equals(str2) || "取消".equals(str2)) {
            if (str2 != null) {
                LogControl.logInfo(deviceName + ":" + str2);
            }
//            }
            if ("系统".equals(str2)) { //按过系统键后，清空按键
                order = null;
                str2 = null;
            }
            return str2;
        } else if (str.matches("[0-9]") || str.equals(".")) {
            LogControl.logInfo("电脑键盘:"+str);
            return str;
        } else {
            str = map.get(str);
//            if ("确认".equals(str) || "取消".equals(str)) {
//                LogControl.logInfo(str);
//            }
            LogControl.logInfo("电脑键盘:"+str);
            return str;
        }

    }

    /**
     * 向收费键盘发送报警指令
     *
     * @return 成功true，失败false
     */
    public boolean sendAlert() {
        order = null; //需要报警的话，将按键设置为空
        if (myComm == null || !myComm.isRunning()) {
            return false;
        }
        MTCLog.log("keyboard send alert");
        myComm.write("\nB\r".getBytes());//响警告音
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
        myComm.write("\nb\r".getBytes());//停止警告音
        return true;
    }

    /**
     * 等待接到确认指令
     */
    public void waitForConfirm() {

        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            if ("确认".equals(waitForOrder())) {
                break;
            }
        }

    }

    /**
     * 获取异常信息
     *
     * @return 异常信息
     */
    public String getErrorMsg() {
        if (myComm == null) {
            return null;
        }
        String str = myComm.getErrorMsg();
        if (str == null || str.trim().isEmpty()) {
            return null;
        } else {
            return deviceName + str;
        }
//        if (myComm != null) {
//            String str = myComm.getErrorMsg();
//            return deviceName+myComm.getErrorMsg();
//        }
//        return null;
    }

    @Override
    public void run() {
        init();
        String[] infos;//从配置文件中读取到的有关串口信息的字符串
        int baudRate;//波特率
        int dataBits;//数据位
        int stopBits;//停止位
        int parity;//校验位
        try {
            String info = SetCOMs.getKeyboard();
            if (info != null) {
                running = true;
                MTCLog.log("启用收费键盘" + info);
                infos = info.split(",");
                String portName = infos[0];
                baudRate = IntegerUtils.parseString(infos[1]);
                dataBits = IntegerUtils.parseString(infos[2]);
                stopBits = IntegerUtils.parseString(infos[3]);
                parity = IntegerUtils.parseString(infos[4]);
                int logLevel = IntegerUtils.parseString(infos[6]);
                logLevel=2;
                //开启串口
                myComm = new MyComm();
                int i = myComm.openComm(portName, baudRate, dataBits, stopBits, parity, logLevel);
                if (i != 0) {
                    logger.error("打开串口" + portName + "失败");
                    return;
                }
                ParseTask parseTask = new ParseTask();
                ThreadPoolControl.getThreadPoolInstance().execute(parseTask);
            } else {
                running = false;
                MTCLog.log("未启用收费键盘");
            }
        } catch (Exception e) {
            MTCLog.log("收费键盘异常", e);
        }
    }

    @Override
    public String getEnKeyByChKey(String chKey) {
        return ceMap.get(chKey);
    }

    /**
     * 加载获取键值对集合
     *
     * @return 键值对集合
     */
    private Map<String, String> loadKey() {
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_KEYBOARD, "fullKeyboardPath", "path1");
        String filePath = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_KEYBOARD, str, "resource/template/WangluKeyboard.properties");
//        String filePath = "resource/template/WangluKeyboard.properties";
        File file = new File(filePath);
        if (!file.exists()) {
            MTCLog.log("keyboard：未发现" + filePath + "文件");
            return null;
        }
        Map<String, String> map = new HashMap<>();
        FileInputStream fis = null;
        UnicodeReader reader = null;
        try {
            fis = new FileInputStream(file);
            reader = new UnicodeReader(fis, "UTF-8");
            Properties prop = new Properties();
            prop.load(reader);
            Set<Object> set = prop.keySet();
            for (Object obj : set) {
                String key = obj.toString().trim();//16进制键
                String value = prop.getProperty(key);//值
                if (value != null) {
                    value = value.trim();
                }
                if (key.matches("[0-9A-Fa-f]+")) {//键满足16进制要求
                    key = String.valueOf((char)(byte)Integer.parseInt(key, 16));
                }else{
                    continue;
                }
                map.put(key, value);
            }
            logger.debug(filePath + "内容如下：" + map);
        } catch (FileNotFoundException ex) {
            logger.error(ex, ex);
        } catch (IOException ex) {
            logger.error(ex, ex);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    logger.error(ex, ex);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    logger.error(ex, ex);
                }
            }
        }
        return map;
    }

    class ParseTask extends MyTask {

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
                if (myComm == null) {
                    continue;
                }
                Byte b = myComm.readByte();
                if (b == null) {
                    continue;
                }
                order = (int) b;
                Integer tempOrder = order;
                if (keyBoardService.specialHand(tempOrder)) {
                    order = null;
                }
            }
        }
    }
}