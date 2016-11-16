package com.hgits.tool.driver;

import MyComm.MyComm;
import com.hgits.util.HexUtils;
import com.hgits.util.ParseUtil;
import com.hgits.util.StringUtils;
import com.hgits.vo.Mifare014Order;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 * Mifare014读卡器服务类
 *
 * @author Wang Guodong
 */
public class Mifare014Svc {

    private MyComm myComm;//串口
    private final Logger logger = Logger.getLogger(Mifare014Svc.class.getName());
    private boolean running = true;//运行标识

    /**
     * 打开设备
     *
     * @param comm 串口号
     * @param baudrate 波特率
     * @param databit 数据位
     * @param parity 校验位
     * @param stopbit 停止位
     * @param logLevel 日志级别
     * @return 0 成功 其他 失败
     */
    public int openDevice(String comm, int baudrate, int databit, int stopbit, int parity, int logLevel) {
        myComm = new MyComm();
        int i = myComm.openComm(comm, baudrate, databit, stopbit, parity, logLevel);
        if (i == 0) {
            running = true;
        } else {
            running = false;
        }
        return i;
    }

    /**
     * 获取读卡器版本号
     *
     * @return 读卡器版本号,读卡器无响应返回null
     */
    public String getVersion() {
        String order = Mifare014Order.QUERY_VERSION;
        order = order.replaceAll(" ", "");
        byte[] buffer = HexUtils.decode(order);
        byte[] buff = loadOrder(buffer);
        if (buff == null) {
            return null;
        } else {
            return new String(buff);
        }
    }

    /**
     * 关闭设备
     */
    public void closeDevice() {
        myComm.closeComm();
        running = false;
    }

    /**
     * 加载密钥05
     *
     * @return true/false
     */
    public boolean loadKey05() {
        String order = Mifare014Order.LOADKEY05;
        order = order.replaceAll(" ", "");
        byte[] buffer = HexUtils.decode(order);
        byte[] buff = loadOrder(buffer);
        if (buff == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 加载密钥06
     *
     * @return true/false
     */
    public boolean loadKey06() {
        String order = Mifare014Order.LOADKEY06;
        order = order.replaceAll(" ", "");
        byte[] buffer = HexUtils.decode(order);
        byte[] buff = loadOrder(buffer);
        if (buff == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 加载密钥10
     *
     * @return true/false
     */
    public boolean loadKey10() {
        String order = Mifare014Order.LOADKEY10;
        order = order.replaceAll(" ", "");
        byte[] buffer = HexUtils.decode(order);
        byte[] buff = loadOrder(buffer);
        if (buff == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 寻卡
     *
     * @return -1 读卡器无响应 0 无卡 >0 10位卡号(10进制)
     */
    public String queryCard() {
        String order = Mifare014Order.QUERY_CARD;
        order = order.replaceAll(" ", "");
        byte[] buffer = HexUtils.decode(order);
        byte[] buff = loadOrder(buffer);
        if (buff == null) {//读卡器无响应
            return "-1";
        } else if (buff.length == 8) {//返回8字节卡号
            byte[] buff1 = readLine();
            if(buff1.length==8){//读卡器上二张卡
                readLine();
            }
            String str = new String(buff).trim();
            String serial = ParseUtil.parseCardNo(str);
            return serial;
        } else {//无卡
            return "0";
        }
    }

    /**
     * 打开指定卡片
     *
     * @param cardSerial 卡号（10位 10进制）
     * @return -1读卡器无响应 0 失败 1 成功
     */
    public int openCard(String cardSerial) {
        boolean flag;
        StringBuilder sb = new StringBuilder();
        sb.append(Mifare014Order.OPEN_CARD);
        String temp = ParseUtil.unParseCardNo(cardSerial);//获取卡号的16进制表示
        byte[] tempBuff = temp.getBytes();//转换为需要发送的字节数组
        sb.append(HexUtils.encode(tempBuff));
        byte[] buff = HexUtils.decode(sb.toString());
        byte[] resBuff = loadOrder(buff);
        if (resBuff == null) {//读卡器无响应
            return -1;
        } else if (resBuff.length != 8) {//返回信息长度异常
            flag = false;
        } else {
            String str = new String(resBuff);
            String tempSer = ParseUtil.parseCardNo(str);
            flag = cardSerial.equals(tempSer);
        }
        if (flag) {
            return 1;//成功
        } else {
            return 0;//失败
        }
    }

    /**
     * 认证密钥
     *
     * @param key 密钥(16进制12位字符串，例如：FFFFFFFFFFFF)
     * @param section 扇区号
     * @param keyType 密钥类型（AA表示A密钥 BB标识B密钥）
     * @return true/false
     */
    public boolean keyAuth(String key, int section, String keyType) {
        boolean flag = false;
        StringBuilder sb = new StringBuilder();
        sb.append(new String(HexUtils.decode(Mifare014Order.KEY_AUTH)));
        String sec = StringUtils.toLength(section, 2);
        sb.append(sec);
        sb.append(keyType);
        if (key != null) {//带密钥认证
            sb.append(key);
        }
        byte[] buffer = loadOrder(sb.toString().getBytes());
        if (buffer != null && Mifare014Order.KEY_AUTH_SUCCESS.equalsIgnoreCase(HexUtils.encode(buffer))) {
            flag = true;
        }
        return flag;
    }

    /**
     * 读取指定块数据
     *
     * @param block 指定块号
     * @return 指定块号的数据，16进制显示,null表示读卡器无响应，-1表示读卡失败
     */
    public String readBLock(int block) {
        StringBuilder sb = new StringBuilder();
        sb.append((Mifare014Order.READ_BLOCK));
        String str = StringUtils.toLength(block, 2);
        byte[] buffer = str.getBytes();
        String hexStr = HexUtils.encode(buffer);
        sb.append(hexStr);
        byte[] buff = HexUtils.decode(sb.toString());
        byte[] resBuff = loadOrder(buff);
        if (resBuff == null) {
            return null;
        } else if (resBuff.length != 32) {
            return "-1";
        } else {
            return new String(resBuff);
        }
    }

    /**
     * 向指定的块写数据
     *
     * @param block 指定的块
     * @param data 需要写的数据（16进制,32位）
     * @return -1-读卡器无响应 0-写失败 1-写成功
     */
    public int writeBlock(int block, String data) {
        StringBuilder sb = new StringBuilder();
        sb.append(Mifare014Order.WRITE_BLOCK);
        String str = StringUtils.toLength(block, 2);
        byte[] tempBuff = str.getBytes();
        String hexStr = HexUtils.encode(tempBuff);
        sb.append(hexStr);
        byte[] tempBuff1 = data.getBytes();
        sb.append(HexUtils.encode(tempBuff1));
        byte[] buffer = HexUtils.decode(sb.toString());
        byte[] buff = loadOrder(buffer);
        if (buff == null) {
            return -1;//无响应
        } else {
            String str1 = new String(buff).trim();
//            logger.debug("writeBlock:block=" + block + ",data=" + data + ",返回信息" + str1);
            if (str1.equalsIgnoreCase(data)) {
                return 1;//成功
            } else {
                return 0;//失败
            }
        }
    }

    /**
     * POFF指令
     *
     * @return -1 失败 1 成功
     */
    public int POFF() {
        byte[] buffer = HexUtils.decode(Mifare014Order.POFF);
        byte[] buff = loadOrder(buffer);
        if (buff == null) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * PON指令
     *
     * @return -1 失败 1 成功
     */
    public int PON() {
        byte[] buffer = HexUtils.decode(Mifare014Order.PON);
        byte[] buff = loadOrder(buffer);
        if (buff == null) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * 加载指令并且返回读卡器回应结果
     *
     * @param buffer 读卡器回应结果
     * @return null如果读卡器无反应
     */
    public byte[] loadOrder(byte[] buffer) {
        myComm.readByteArray();
        myComm.write(buffer);
        return readLine();

    }

    /**
     * 读取数据不包含结束符0D0A
     *
     * @return 读取读卡器返回的数据
     */
    private byte[] readLine() {
        byte[] buffer = new byte[128];
        boolean flag = false;
        int index = 0;
        long start = System.currentTimeMillis();
        while (true) {
            if (!running) {
                return null;
            }
            long now = System.currentTimeMillis();
            if (now - start > 2000) {
                return null;
            }
            Byte b = myComm.readByte();
            if (b == null) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
                continue;
            } else if (b == 0x0d) {//收到结束符第一个字节0x0d
                flag = true;
            } else if (flag && b != 0x0a) {//收到结束符第一个字节，但并未收到第二个字节
                flag = false;
            } else if (b == 0x0a && flag) {//结束符全部收到
                break;
            } else {
                buffer[index] = b;
                index++;
                if (index >= buffer.length) {//读取数据超过了缓存范围
                    break;
                }
            }
        }
        byte[] tempBuffer = new byte[index + 2];
        System.arraycopy(buffer, 0, tempBuffer, 0, index);
        tempBuffer[index] = 0x0D;
        tempBuffer[index + 1] = 0x0A;
        return Arrays.copyOf(buffer, index);

    }

}
