package com.hgits.tool.driver;

import com.hgits.common.log.MTCLog;
import com.hgits.control.LogControl;
import com.hgits.control.TempControl;
import com.hgits.util.IntegerUtils;
import com.hgits.util.ParseUtil;
import com.hgits.util.StringUtils;
import com.hgits.vo.Card;
import com.hgits.vo.Constant;
import java.util.Arrays;
import com.hgits.hardware.IcReaderWriter;
import com.hgits.hardware.impl.icreaderwriter.GeaIcReaderWriter;
import com.hgits.util.HexUtils;
import com.hgits.util.MyPropertiesUtils;

/**
 * M1卡读写器的辅助类，包含读写器与主机的主要的信息交互的过程，写卡的不同区域的方法
 *
 * @author lq
 */
public class IcRWService {

    private final int interval;//读卡器寻卡等待时间间隔
    private IcReaderWriter icrw;
    private final int DLE = 0x10;//数据链路转义
    private final int STX = 0x02;//正文开始
    private final int ETX = 0x03;//正文结束
    private final int NAK = 0x15;//否定应答
    private final int m1QueryInterval;

    public IcRWService(IcReaderWriter icrw) {
        String temp = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "mifReadInterval", "250");
        int i = IntegerUtils.parseString(temp);
        if (i <= 250) {
            i = 250;
        }
        interval = i;
        LogControl.logInfo("mifReadInterval=" + interval);
        this.icrw = icrw;
        temp = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "m1QueryInterval", "500");
        i = IntegerUtils.parseString(temp);
        if (i <= 250) {
            i = 250;
        }
        m1QueryInterval = i;
        LogControl.logInfo("m1QueryInterval=" + m1QueryInterval);
    }

    /**
     * 主机向串口发送数据链路转义DLE指令‘10’
     *
     */
    public void sendDLE() {
        icrw.write((byte) 16);     //先向串口发送数据链路转义的指令‘10’
    }

    /**
     * 主机向串口发送文本开始STX的指令‘02’
     *
     */
    public void sendSTX() {
        icrw.write((byte) 2);      //再向串口发送文本开始的指令‘02’
    }

    /**
     * 主机向读卡机发送指令 1.主机给卡片读写器发指令的流程：开始通信前，主机和卡片读写器均处于就绪状态。
     * 首先主机发出STX（文本开始指令‘02’）表示主机要开始向读卡器发指令， 然后等待卡片读写器回应DLE(数据联路转义
     * '10')表示卡片读写器已经准备好接收指令； 收到卡片读写器回应的DLE后，主机将命令信息数据 + DLE +
     * ETX（文本结束'03'）一起发送给卡片读写器； 卡片读写器已经接收到了指令，卡片读写器回应DLE给主机表示已经接收到了指令，然后执行命令。
     * 2.卡片读写器给主机的回复指令的流程：卡片读写器执行完命令后，将执行结果返回主机，
     * 首先卡片读写器先向主机发送STX（文本开始指令‘02’）表示卡片读写器要对于指令进行回复了，
     * 主机接收到STX后，向卡片读写器发送DLE（数据联路转义'10'）表示主机已经准备好接收回复了，
     * 收到主机回应的DLE后，卡片读写器将对应指令的回复信息数据 + DLE + ETX(文本结束'03')一起发送给主机；
     * 主机接收到卡片读写器后，回复DLE给卡片读写器表示已经接收到了指令回复信息。
     *
     * @param order 需要发送的指令
     * @return 指令对应的序号
     */
    public int sendOrder(String order) {
        int i = icrw.getIndex();
        icrw.increaseIndex();
        order = StringUtils.changeIntToHex(i, 2) + order.replaceAll(" ", "");  //将数据交互的序号转换成16进制，并保留2位，并拼接在指令的最前面
        order = StringUtils.fillBCC(order, 16);                   //将指令转换成16进制的字符串
        StringBuilder sb = new StringBuilder(order);             //将指令字符串转换成能够方便拼接的字符序列
        sb.append("1003");                                       //在指令的末尾，添加固定的‘1003’
        StringUtils.doubleStr(sb, Constant.DLE);                  //在指令的偶数位插入表示数据链路转义的‘10’
        byte[] buffer = HexUtils.decode(sb.toString());
        icrw.write(buffer);        //先将向指令转换成16进制的字节数组，然后向串口发出指令
        return i;
    }

    /**
     * 加载主机向串口发送的非读卡指令（包括STX和DLE等指令）
     *
     * @param order 主机向串口发送的核心指令
     * @return 返回执行的结果，-1为未返回核心信息，-2为信息校验错误，0为信息正确 -4返回的卡信息长度出错，-5执行的命令失败
     */
    public int loadOrder(String order) {
        if (order != null) {
            order = order.replaceAll(" ", "");
        }
        int serial;
        int len;
        int result;
        int a, a1, a2, k;
        int index;//发送指令的序号
        byte[] buffer;
        int crc;
        long start1 = System.currentTimeMillis();
        int errCnt = 0;
        Outer:
        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
            }
            long now1 = System.currentTimeMillis();
            if (now1 < start1) {//当前时间小于开始时间，重新开始计时
                start1 = now1;
            } else if (now1 - start1 > 5000) {//一条指令执行5秒钟仍然没有结果，直接判断为失败
                return -5;
            }
            clearInput();//从头发送指令，清空读取数据
            sendSTX();//发送文本开始的指令‘02’
            a = readByteFromIcrw();
            if (a == 16) {//如果返回数据链路转义的回复‘10’
                errCnt = 0;
                clearInput();
                index = sendOrder(order);//向串口发送对应核心的指令
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                MTCLog.log("天线返回信息异常，等待0x10，实际返回" + a);
                if (errCnt++ >= 10) {
                    MTCLog.log("天线返回信息连续十次异常，等待0x10，实际返回" + a);
                    return -5;
                }
                continue;
            }
            a = readByteFromIcrw();
            if (a == 16) {                          //如果返回数据链路转义的回复‘10’,表示读写器接收到了指令，接着等待读写器返回指令的回复
                errCnt = 0;
                long start = System.currentTimeMillis();
                long s1 = start;
                long now, s2;
                Byte b;
                while (true) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                    }
                    now = System.currentTimeMillis();
                    s2 = now;
                    if (s2 - s1 > m1QueryInterval) {
                        s1 = s2;
                        if (order.startsWith("410181")) {//当前指令为寻卡指令
                            icrw.setCard(null);
                            icrw.setCardIsRead(false);
                            icrw.setHasCard(false);
                            icrw.setFlag42(false);
                        }
                    } else if (s2 < s1) {
                        s1 = s2;
                    }
                    if (now < start) {//当前时间小于开始时间，重新开始计时
                        start = now;
                    }
                    if (now - start > interval) {//等待超时
                        return -1;
                    }
                    b = icrw.readByte();
                    if (b != null) {
                        if (b == 2) {//如果返回的数据是02表示将要返回回复核心指令的信息
//                            clearInput();//发送之前先清空接收内容
                            sendDLE(); //主机回一个10表示已经准备好接收返回的信息
                            break;
                        } else {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                            }
                            MTCLog.log("天线返回信息异常，等待0x02，实际返回" + a);
                            continue Outer;             //返回指令异常，重新发送指令
                        }
                    }
                }
            } else {//天线返回信息异常，重新发送指令
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
                MTCLog.log("天线返回信息异常，等待0x10，实际返回" + a);
                if (errCnt++ >= 10) {
                    MTCLog.log("天线返回信息连续十次异常，等待0x10，实际返回" + a);
                    return -5;
                }
                continue;
            }
            byte[] recvBuff = new byte[128];
            int tempIndex;
            for (tempIndex = 0; tempIndex < 128; tempIndex++) {
                byte tempByte1 = (byte) readByteFromIcrw();
                recvBuff[tempIndex] = tempByte1;
                if (tempByte1 == DLE) {//收到转义字符
                    byte tempByte2 = (byte) readByteFromIcrw();
                    if (tempByte2 == ETX) {//收到结尾字符
                        tempIndex++;
                        recvBuff[tempIndex] = tempByte2;
                        break;
                    } else if (tempByte2 == DLE) {//转义字符
                        continue;
                    } else {//正常字符
                        tempIndex++;
                        recvBuff[tempIndex] = tempByte2;
                    }
                }
            }
            if (tempIndex >= 128) {
                MTCLog.log("icrw返回信息异常：" + HexUtils.encode(recvBuff));
                return -5;//返回数据内容异常
            }
            clearInput();
            sendDLE();                                //主机回一个10表示已经完接收返回的信息
            byte[] resultBuff = Arrays.copyOf(recvBuff, tempIndex + 1);
            serial = getSerial(resultBuff, (byte) index);
            result = getResult(resultBuff, (byte) index);
            len = getDataLen(resultBuff, (byte) index);
            buffer = getDataBuffer(resultBuff, (byte) index);
            crc = getCrc(resultBuff, (byte) index);
            break;
        }
        k = check(serial, result, len, buffer, order, crc);
        switch (k) {
            case 0:
                k = 0;
                break;
            case -3:
                k = -2;
                break;
            case -4:
                k = -4;
                break;
            case -5:
                k = -5;
                break;
        }
        return k;
    }

    /**
     * 读取天线信息（按字节读取）
     *
     * @return 天线回应信息
     */
    private int readByteFromIcrw() {
        Byte b = null;
        long start = System.currentTimeMillis();
        while (true) {
            b = icrw.readByte();
            if (b != null) {
                break;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
            }
            long now = System.currentTimeMillis();
            if (now < start) {//当前时间小于开始时间，重新开始计时
                start = now;
            } else if (now - start > 5000) {
                throw new RuntimeException("天线通信异常，超过5秒无响应");
            }
        }
        return b;
    }

    /**
     * 清空读取的数据
     *
     */
    public void clearInput() {
        while (icrw.isRunning()) {
            byte[] buffer = icrw.readByteArray();
            if (buffer == null) {
                break;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * 校验读写器返回的信息的CRC码
     *
     * @param index 主机与读写器信息交互的下标
     * @param result 读写器执行命令的结果
     * @param len 读写器返回的可用信息的长度
     * @param data 读写器返回的可用信息
     * @param crc 读写器返回当前的信息校验码
     * @return 校验的结果 0表示正确，-1表示校验失败
     */
    public int checkMsgCRC(int index, int result, int len, String data, String crc) {
        StringBuilder msg = new StringBuilder();
        msg.append(StringUtils.changeIntToHex(index, 2));
        msg.append(StringUtils.changeIntToHex(result, 2));
        msg.append(StringUtils.changeIntToHex(len, 2));
        msg.append(data);
        String str = StringUtils.fillBCC(msg.toString().trim(), 16);
        str = str.substring(str.length() - 2, str.length());
        if (str.equals(crc)) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 检查接收到读写器对核心指令的回复信息是否正确
     *
     * @param index 主机与读写器信息交互的下标
     * @param result 读写器执行命令的结果
     * @param len 读写器返回的可用信息的长度
     * @param buffer 读写器返回的的可用信息字节数组
     * @param order 对应主机发送的指令
     * @param crc 读写器返回的CRC校验码
     * @return 返回执行的结果，-1为未返回核心信息，-2为信息校验错误，0为信息正确 -4返回的卡信息长度出错，-5执行的命令失败
     */
    public int check(int index, int result, int len, byte[] buffer, String order, int crc) {
        int flag = 0;
        order = order.replaceAll(" ", "").trim();
        String data = HexUtils.encode(buffer).replaceAll(" ", "").trim();
        int i = checkMsgCRC(index, result, len, data, StringUtils.changeIntToHex(crc, 2));
//        MTCLog.log("icrw返回信息：" + index + "\t" + result + "\t" + len + "\t" + data + "\t" + crc);
        if (i == 0) {
            if (order.startsWith("41")) {                   //寻卡指令
                if ("81".equals(order.substring(4, 6))) {       // 0x81 — 寻卡 
                    if (result == 0) {                          //如果结果是0，返回的结果是已经有卡在读写器上
                        icrw.setHasCard(true);
                    } else {
                        icrw.setCardIsRead(false);
                        icrw.setHasCard(false);
                        icrw.setCard(null);
                    }
                }
//                else if ("00".equals(order.substring(4, 6))) {    // 0x00 — 卡是否存在
//                if (result == 0) {
//                    icrw.setHasCard(true);
//                } 
//                    else {
//                    flag = -1;
//                    icrw.setCardIsRead(false);
//                    icrw.setHasCard(false);
//                    icrw.setCard(null);
//                }
            } else if (order.startsWith("42")) {             //防碰撞指令，返回卡序列号的指令
                if (result == 0) {
                    if (icrw.getCardObj() == null || !data.equals(icrw.getCardNo())) {
                        icrw.setCardIsRead(false);
                        icrw.setCardIsWritten(false);
                        icrw.setCardNo(data);
                        icrw.setCard(new Card());
                        icrw.getCardObj().setCardSerial(ParseUtil.parseCardNo(data));
                    } else {
                        icrw.setCardIsRead(true);
                    }
                    icrw.setFlag42(true);
                } else {
                    icrw.setCardIsRead(false);
                    icrw.setHasCard(false);
                    icrw.setFlag42(false);
                    icrw.setCard(null);
                }
            } else if (order.startsWith("46")) {             //读取块信息
                if (result == 0) {
                    String sectionNum = order.replaceAll(" ", "").substring(4, 6);              //将发出前的命令的指定扇区编号取出
                    if (data.length() == 32) {
                        switch (sectionNum) {
                            case "04":
                                icrw.getCardObj().setSection_04_info(data);
                                break;
                            case "05":
                                icrw.getCardObj().setSection_05_info(data);
                                break;
                            case "06":
                                icrw.getCardObj().setSection_06_info(data);
                                break;
                            case "08":
                                icrw.getCardObj().setSection_08_info(data);
                                break;
                            case "09":
                                icrw.getCardObj().setSection_09_info(data);
                                break;
                        }
                    } else {
                        flag = -1;
                        MTCLog.log(order + "M1卡天线返回的" + sectionNum + "块区信息" + data + "长度不正确。");
                    }
                } else {
                    flag = -1;
                    MTCLog.log(order + "M1卡天线读卡失败,读卡器返回" + index + " " + result + " " + len + " " + data + " " + crc);
                }

            } else if (order.startsWith("43") || order.startsWith("56")) {         //如果从读到的读写器的回应中的帧序列是对应选择卡的序列号43指令或者通过下载密钥的验证56指令
                if (result == 0) {
                    icrw.setHasCard(true);
                } else {
                    flag = -1;
                    MTCLog.log(order + "M1卡天线选择卡失败,读卡器返回" + index + " " + result + " " + len + " " + data + " " + crc);
                }
            } else if (order.startsWith("4F")) {
                if (result != 0) {
                    flag = -1;
                } else {
                    if (buffer != null) {
                        String version = new String(buffer);
                        TempControl svc = TempControl.getSingleInstance();
                        svc.generateTempVersion("M1Reader.ver", version);
                        MTCLog.log("M1卡读写器型号：" + version);
                    }
                }
            } else if (order.startsWith("52")
                    || order.startsWith("4C")
                    || order.startsWith("4E")
                    || order.startsWith("45")) {
                if (result != 0) {
                    flag = -1;
                }

            } else if (order.startsWith("47")) {
                if (result != 0) {
                    flag = -1;
                    MTCLog.log(order + "M1卡天线写卡失败,读卡器返回" + index + " " + result + " " + len + " " + data + " " + crc);
                }
            } else if (order.startsWith("40")) {
                if (result != 0) {
                    flag = -1;
                    MTCLog.log(order + "M1卡天线密钥验证失败,读卡器返回" + index + " " + result + " " + len + " " + data + " " + crc);
                }
            }
        } else {
            flag = -1;
            MTCLog.log(order + "M1卡天线返回信息crc校验错误,读卡器返回" + index + " " + result + " " + len + " " + data + " " + crc);
        }
        return flag;
    }

    /**
     * 向块05区写入卡状态，写卡时间，路段好，站代号，车道编号，员工号，卡的被写次数
     *
     * @param card 待写卡
     * @return -1为未返回核心信息，-2为信息校验错误，0为信息正确 -4返回的卡信息长度出错，-5执行的命令失败
     */
    public int write05(Card card) {
        String data = ParseUtil.unParse05(card);
        if (data == null) {
            return -2;
        }
        String order = GeaIcReaderWriter.ORDER_47_05.replaceAll(" ", "") + data;
        card.setSection_05_info(data);
        return loadOrder(order);
    }

    /**
     * 向通行卡06块写入相应数据
     *
     * @param card 待写卡
     * @return 返回执行的结果，-1为未返回核心信息，-2为信息校验错误，0为信息正确 -4返回的卡信息长度出错，-5执行的命令失败
     */
    public int write06(Card card) {
        String data = ParseUtil.unParse06(card);
        if (data == null) {
            return -2;
        }
        String order = GeaIcReaderWriter.ORDER_47_06.replaceAll(" ", "") + data;
        card.setSection_06_info(data);
        return loadOrder(order);
    }

    /**
     * 写入通行卡块8区域的信息，主要是全车牌和车牌颜色
     *
     * @param card 待写卡
     * @return 返回执行的结果，-1为未返回核心信息，-2为信息校验错误，0为信息正确 -4返回的卡信息长度出错，-5执行的命令失败
     */
    public int write08(Card card) {
        String data = ParseUtil.unParse08(card);
        if (data == null) {
            return -2;
        }
        String order = GeaIcReaderWriter.ORDER_47_08.replaceAll(" ", "") + data;
        card.setSection_08_info(data);
        return loadOrder(order);
    }

    /**
     * 写入通行卡块9区域的信息，主要是全车牌和车牌颜色
     *
     * @param card 待写卡
     * @return 返回执行的结果，-1为未返回核心信息，-2为信息校验错误，0为信息正确 -4返回的卡信息长度出错，-5执行的命令失败
     */
    public int write09(Card card) {
        String data = ParseUtil.unParse09(card);
        if (data == null) {
            return -2;
        }
        String order = GeaIcReaderWriter.ORDER_47_09.replaceAll(" ", "") + data;
        card.setSection_09_info(data);
        return loadOrder(order);
    }

    /**
     * 从返回信息中获取返回指令下标
     *
     * @param buffer 返回信息
     * @param index 发送指令下标
     * @return 返回指令下标
     */
    private byte getSerial(byte[] buffer, byte index) {
        int serial = buffer[0];
        if (index != 2 && buffer[0] == 2) {//指令序号不为2，但返回指令序号为2
            serial = buffer[1];
        }
        return (byte) serial;
    }

    /**
     * 从返回信息中获取指令执行结果
     *
     * @param buffer 返回信息
     * @param index 指令下标
     * @return 指令执行结果
     */
    private byte getResult(byte[] buffer, byte index) {
        int result = buffer[1];//第二位为返回结果
        if (index != 2 && buffer[0] == 2) {//指令序号不为2，但返回指令序号为2
            result = buffer[2];
        } else if (index == 2 && buffer[1] == 2) {//当前序号为2并且返回值中序号在第二位
            result = buffer[2];
        }
        return (byte) result;
    }

    /**
     * 从返回信息中获取指令执行数据的长度
     *
     * @param buffer 返回信息
     * @param index 指令下标
     * @return 指令执行数据的长度
     */
    private byte getDataLen(byte[] buffer, byte index) {
        int len = buffer[2];//第三位为数据长度
        if (index != 2 && buffer[0] == 2) {//指令序号不为2，但返回指令序号为2
            len = buffer[3];
        } else if (index == 2 && buffer[1] == 2) {//当前序号为2并且返回值中序号在第二位
            len = buffer[3];
        }
        return (byte) len;
    }

    /**
     * ‘
     * 从返回信息中获取指令执行数据
     *
     * @param buffer 返回信息
     * @param index 指令下标
     * @return 指令执行数据
     */
    private byte[] getDataBuffer(byte[] buffer, byte index) {
        int len = buffer[2];//第三位为数据长度
        int startIndex = 3;//数据区起始下标
        if (index != 2 && buffer[0] == 2) {//指令序号不为2，但返回指令序号为2
            len = buffer[3];
            startIndex = 4;
        } else if (index == 2 && buffer[1] == 2) {//当前序号为2并且返回值中序号在第二位
            len = buffer[3];
            startIndex = 4;
        }
        int endIndex = startIndex + len;//数据区结尾下标
        if (endIndex > buffer.length - 2) {//数据区结尾下标超过范围
            endIndex = buffer.length - 2;
        }
        if (endIndex < startIndex) {//结尾下标小于起始下标
            endIndex = startIndex;
        }
        byte[] result = Arrays.copyOfRange(buffer, startIndex, endIndex);
        return result;
    }

    /**
     * 获取CRC校验值
     *
     * @param buffer 返回信息
     * @param index 指令下标
     * @return CRC校验值
     */
    private byte getCrc(byte[] buffer, byte index) {
        int len = buffer.length;
        if (len < 3) {//长度错误
            return 0;
        }
        return buffer[len - 3];
    }

}
