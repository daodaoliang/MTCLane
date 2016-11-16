package com.hgits.tool.driver;

import com.hgits.util.file.FileUtils;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.LongByReference;

import java.io.File;

/**
 * 本接口是用于调用ETC卡读写器的动态链接库，涵盖了对ETC卡读写器的所有的操作的抽象方法
 *
 * @author lq
 */
public interface IMiFare extends Library {

//    IMiFare INSTANCE = (IMiFare) Native.loadLibrary("IccReader_MingHua.dll", IMiFare.class);
//    final String EtcDllName = PropertiesCacheUtils.getPropertiesValue("ETCDLL", "IccReader_MingHua.dll");
    final String EtcDllName = MiFareService.getEtcDllName();
    IMiFare INSTANCE = (IMiFare) Native.loadLibrary(FileUtils.getRootPath() + File.separator + EtcDllName, IMiFare.class);

    /**
     * 将二进制的数据转换成16进制的字符串
     *
     * @param sbin 二进制的数据缓冲区
     * @param shex 转换后的结果，16进制字符串（以’\0’结尾），
     * 注意：定义和分配给shex缓冲区的长度至少为sbin_len的2倍，当取全部时至少是sbin长度的3倍）
     * @param sbin_len 二进制数据字节数（当sbin_len小于等于0时就取全部的长度）
     */
    public void BinToHex(byte[] sbin, byte[] shex, int sbin_len);

    /**
     * 将16进制的字符串转换成二进制的数据
     *
     * @param shex 16进制字符串（必须以‘\0’结尾的字符串）
     * @param sbin 二进制数据缓冲区（存放结果）
     * @param shex_len 16进制数据的字节数（当shex_len小于等于0时就取全部的长度）
     * @return 转换后的二进制数据
     */
    public int HexToBin(String shex, byte[] sbin, int shex_len);

    /**
     * 测试与com口相连的ETC卡读写器是否运转正常
     *
     * @param iComNo 串口号
     * @return 测试结果
     */
    public int RD_Test(byte iComNo);

    /**
     * 此方法使用于打开ETC卡的读写器
     *
     * @param iComNo 串口的编号
     * @param baudRate 串口的波特率
     * @param cmdType 卡座标志
     * @return 若返回值等于0，则打开成功；若返回值不等于0，则打开失败
     */
    public int Open_Reader(byte iComNo, int baudRate, int cmdType);

    /**
     * 此方法是关闭已打开的ETC卡读写器
     *
     * @param iComNo 串口的编号
     * @return 若返回值等于0，则关闭成功；若返回值不等于0，则关闭失败
     */
    public int Close_Reader(byte iComNo);

    /**
     * 设定ETC卡读写器串行口波特率
     *
     * @param iComNo 已打开的串口编号
     * @param baudrate 波特率，取9600,19200,38400,115200之一，加电后缺省值为38400
     * @return 若返回值等于0，则设定成功；若返回值不等于0，则设定失败
     */
    public int RD_SetBaud(byte iComNo, int baudrate);

    /**
     * 获取ETC卡读写器串行口波特率值
     *
     * @param iComNo 已打开的串口编号
     * @return 波特率，取9600,19200,38200,115200之一，加电后缺省值为38400
     */
    public int RD_GetBaud(byte iComNo);

    /**
     * 设定ETC卡读写器蜂鸣器和指示灯工作方式
     *
     * @param iComNo 已打开的串口号
     * @param beep 蜂鸣器工作方式,0x00 NO_CONTROL : 保持原状态，不控制； 0x80 BEEP_OFF : 关闭蜂鸣器；
     * 0x81 BEEP_SHORT : 蜂鸣器“D-D”鸣叫两声，间隔100毫秒； 0x82 BEEP_LASTING : 蜂鸣器鸣叫1秒后停止
     * @param green 绿指示灯工作方式,0x00 NO_CONTROL : 保持原状态，不控制； 0x80 LED_OFF : 关闭指示灯；
     * 0x81 LED_LASTING : 令指示灯一直亮； 0x82 LED_SHORT : 指示灯亮1秒后熄灭； 0x83 LED_TWINKLE
     * : 指示灯以1秒的间隔交互闪烁；
     * @param yellow 黄指示灯工作方式 同green灯；
     * @return 若返回值等于0，则设定成功；若返回值不等于0，则设定失败
     */
    public int RD_SetMMI(byte iComNo, int beep, int green, int yellow);

    /**
     * 获取最近发送的命令帧(取最近操作的数据帧信息,该函数可用于写现场LOG文件)
     *
     * @param iComNo 已打开的串口号
     * @param frame 当前帧的数据缓冲
     * @return 返回的帧长度
     */
    public int GetLastTxFrame(byte iComNo, byte[] frame);

    /**
     * 取最近回复的命令帧（取最近操作的数据帧信息,该函数可用于写现场LOG文件）
     *
     * @param iComNo 已打开的串口号
     * @param frame 当前帧的数据缓冲
     * @return 返回帧的长度
     */
    public int GetLastRxFrame(byte iComNo, byte[] frame);

    /**
     * 射频复位，相当于短时间关闭天线后重新开启。该操作执行后能使所有MIFARE
     * 卡能被ETC卡读写器打开（Open_Card），包含原来被锁定（MF1_Halt）的卡;
     *
     * @param iComNo 串口号
     * @return 若返回值等于0，则设定成功；若返回值不等于0，则设定失败
     */
    public int RF_Reset(byte iComNo);

    /**
     * 复位IC卡;复位SAM/PRO卡，对SAM/PRO卡进行操作前需进行复位；
     *
     * @param iComNo 已打开的串口号
     * @param cmdtype 卡座标志: 0x00(NO_PSAM)：无PSAM卡；0x01(PSAM1)：SAM卡座1；
     * 0x02(PSAM2)：SAM卡座2；0x03(PSAM3)：SAM卡座3；
     * @return 若返回值等于0，则为复位成功；若返回值不等于0，则复位失败
     */
    public int CPU_Reset(byte iComNo, int cmdtype);

    /**
     * 打开卡片 搜寻天线范围内是否存在MIFARE非接触式IC卡， 该操作是MIFARE ONE和PRO卡都必须进行的操作
     *
     * @param iComNo 已打开的串口号
     * @param cardNo 返回的IC卡号
     * @return 返回的结果 ，0则打开成功，非0则失败
     */
    public int Open_Card(byte iComNo, long[] cardNo);

    /**
     * 打开卡片 搜寻天线范围内是否存在MIFARE非接触式IC卡， 该操作是MIFARE ONE和PRO卡都必须进行的操作
     *
     * @param iComNo 已打开的串口号
     * @param cardno 返回的IC卡号
     * @param len 返回卡号的长度
     * @return 返回的结果 ，0则打开成功，非0则失败
     */
    public int Open_CardB(byte iComNo, byte[] cardno, int[] len);

    /**
     * 获取当前所选定的MIFARE卡类型
     *
     * @param iComNo 打开的串口号
     * @return 卡类型 -1 —— 未知类型 0, MIFARE_ONE—— MIFARE ONE卡； 1, MIFARE_PRO——
     * MIFARE PRO 卡
     */
    public int Get_CardType(byte iComNo);

    /**
     * 判别是否有卡在读写器上
     *
     * @param fd 串口号
     * @return 小于0 表示 错误； 0 表示 无卡 ； 1 表示有1张或多张
     */
    public int ICC_Present(int fd);

    /**
     * 获取错代码对应的错误文本信息
     *
     * @param error 错误代码
     * @return 返回错误的信息
     */
    public String Put_Error(int error);

    /**
     * 获取ETC卡读写器天线的错误文本信息
     *
     * @param iComNo 已打开的串口号
     * @param iErrCode 错误的代码
     * @return 读写器的错误信息
     */
    public String RD_GetErrInfo(int iComNo, int iErrCode);

    /**
     * 判别当前的动态连接库的版本
     *
     * @param iComNo 已打开的串口号
     * @param version 动态链接库的版本
     * @return 若返回值等于0，则为参数对应的版本；若返回值不等于0，则非与参数对应的版本
     */
    public int RD_GetVersion(byte iComNo, byte[] version);

    /**
     * 读写器是否打开
     *
     * @param iComNo 已打开的串口号
     * @return 若返回值等于0，则已打开；若返回值不等于0，未打开
     */
    public int IsReaderOpen(byte iComNo);

    /**
     * 返回TIMECOS指令的响应状态（SW12）信息
     * 备注：注意区分Put_Error函数和ICC_Message函数的功能。Put_Error函数用来显示几乎所有函数的操作状态，
     * 如是否正常执行、是否超时等信息；而ICC_Message是用来显示TIMECOS指令响应的SW12所对应的状态信息，
     * 适用于SAM卡和PRO卡操作。
     *
     * @param SW12 2个字节，SAM卡和PRO卡的timecos指令响应状态码SW1和SW2，SW1为低字节
     * @return SW1、SW2所指示的状态信息
     */
    public String ICC_Message(StringBuilder SW12);
//        public String ICC_Message(char[] SW12);

    /**
     * 对扇区进行认证
     *
     * @param iComNo 打开的串口号
     * @param keytype 密钥类型 0 为密钥A 1 为密钥B
     * @param sector 扇区号（0-15）注：每个扇区分4块（block）
     * @param key 12字节的密钥字符串
     * @param cardid ic卡的物理卡号
     * @return 若返回值等于0，则为认证成功；若返回值不等于0，则认证失败
     */
    public int MF1_AuthenticateWithKey(byte iComNo, int keytype, int sector,
            String key, String cardid);

    /**
     * 读取已经通过扇区认证后中块的数据
     *
     * @param iComNo 打开的串口号
     * @param block 块号 （0-63）
     * @param data 读取的数据
     * @return 若返回值等于0，则为读取成功；若返回值不等于0，则读取失败
     */
    public int MF1_ReadBlock(byte iComNo, int block, byte[] data);

    /**
     * 读取已经通过扇区认证后中块的数据（与MF1_ReadBlock用法区别是内部集成了MF1_LoadKey，
     * MF1_AuthenticateBlock等，可直接使用，而MF1_ReadBlock则需要先调用MF1_LoadKey，
     * MF1_AuthenticateBlock后才能用）
     *
     * @param iComNo 打开的串口号
     * @param block 块号 （0-63）
     * @param KeyType 密钥类型
     * @param KeyValue 密钥值
     * @param data 读取的块的数据，32字节字符
     * @return 若返回值等于0，则为读取成功；若返回值不等于0，则读取失败
     */
    public int MF1_ReadBlockEx(byte iComNo, int block, int KeyType,
            String KeyValue, byte[] data);

    /**
     * 写卡（将数据写入通过扇区认证后的块中）
     *
     * @param iComNo 打开的串口号
     * @param block 块号（0-63）
     * @param data 待写入的数据，16字节
     * @return 若返回值等于0，则为写入成功；若返回值不等于0，则写入失败
     */
    public int MF1_WriteBlock(byte iComNo, int block, String data);

    /**
     * 写卡（将数据写入通过扇区认证后的块中）（注：与MF1_WriteBlock用法区别是内部集成了MF1_LoadKey，
     * MF1_AuthenticateBlock等，可直接使用，而MF1_WriteBlock则需要先调用MF1_LoadKey，
     * MF1_AuthenticateBlock后才能用）
     *
     * @param iComNo 打开的串口号
     * @param block 块号 （0-63）
     * @param KeyType 密钥类型
     * @param KeyValue 密钥值
     * @param data 待写入的数据，16字节
     * @return 若返回值等于0，则为写入成功；若返回值不等于0，则写入失败
     */
    public int MF1_WriteBlockEx(byte iComNo, int block, int KeyType,
            String KeyValue, String data);

    /**
     * 挂起卡（挂起当前操作的MIFARE ONE卡）
     *
     * @param iComNo 已打开的串口号
     * @return 若返回值等于0，则为执行成功；若返回值不等于0，则执行失败
     */
    public int MF1_Halt(byte iComNo);

    /**
     * 本方法是MIFARE PRO卡指令的通用支持函数，可实现MIFARE PRO卡的任意TIMECOS(不含SW1和SW2)
     * 注：1.预留给response的长度要足够长，必须是所需要的到的二进制信息长度的
     * 2倍。如取8字节随机数，则response长度至少为20(含SW1和SW2)。
     * 2.预留给response和sw12的长度要足够长，必须是所需要的到的二进制信息长度的2倍。如取8字节随机数，则response长度至少为16。
     *
     * @param iComNo 已打开的串口号
     * @param command 指令帧（以’\0’结尾的16进制字符串或常量字符串）
     * @param response 响应帧（以’\0’结尾的16进制字符串或常量字符串）
     * @return 若返回值等于0，则为执行成功；若返回值不等于0，则执行失败
     */
    public int PRO_Command(byte iComNo, String command, byte[] response);

    /**
     * 本方法是MIFARE PRO卡指令的通用支持函数，可实现MIFARE PRO卡的任意TIMECOS(含SW1和SW2)
     * 注：1.预留给response的长度要足够长，必须是所需要的到的二进制信息长度的
     * 2倍。如取8字节随机数，则response长度至少为20(含SW1和SW2)。
     * 2.预留给response和sw12的长度要足够长，必须是所需要的到的二进制信息长度的2倍。如取8字节随机数，则response长度至少为16。
     *
     * @param iComNo 已打开的串口号
     * @param command 指令帧（以’\0’结尾的16进制字符串或常量字符串）
     * @param response 响应帧（以’\0’结尾的16进制字符串或常量字符串，含SW1和SW2）
     * @param sw12 响应状态码SW1和SW2的字符串显示，如“9000” （以’\0’结尾的16进制字符串或常量字符串）等
     * @return 若返回值等于0，则为执行成功；若返回值不等于0，则执行失败
     */
    public int PRO_CommandEx(byte iComNo, String command, byte[] response,
            byte[] sw12);

    /**
     * 写CPU卡文件
     *
     * @param iComNo 已打开的串口号
     * @param iSlot 认证用的PASM卡槽号,1,2,3,4
     * @param FileID 文件标示，如：0012、0019
     * @param szData 所写入的内容
     * @return 若返回值等于0，则为执行成功；若返回值不等于0，则执行失败
     */
    public int PRO_WriteFile(byte iComNo, int iSlot, String FileID,
            byte[] szData);

    /**
     * PRO卡复合消费。
     *
     * @param iComNo 已打开的串口号
     * @param iSlot 认证用的PASM卡槽号，1，2，3，4
     * @param sTime 消费时间 如20140826101520
     * @param Money 消费的金额（无符号型的数据）
     * @param TollInfor 联网收费信息文件0019的内容
     * @param sTermTradNo 终端脱机交易序号
     * @param sCardTradeNo 卡片交易序号
     * @param sTAC 复合消费Tac码
     * @param iBalance1 卡片消费前余额
     * @param iBalance2 卡片消费后余额
     * @return 若返回值等于0，则为执行成功；若返回值不等于0，则执行失败
     */
    public int PRO_Comsume(byte iComNo, int iSlot, String sTime, int Money,
            String TollInfor, byte[] sTermTradNo, byte[] sCardTradeNo,
            byte[] sTAC, LongByReference iBalance1, LongByReference iBalance2);

    /**
     * 本方法是SAM卡指令的通用支持函数，可实现SAM卡的任意TIMECOS指令（不含SW1和SW2）。
     * 注意：1.预留给resdata的长度要足够长，必须是所需要的到的二进制信息长度的2倍。如取8字节随机数，则resdata长度至少为20(含SW1和SW2)。
     * 2.预留给resdata和sw12的长度要足够长，必须是所需要的到的二进制信息长度的2倍。如取8字节随机数，则resdata长度至少为16。
     *
     * @param iComNo 已打开的串口号
     * @param command 指令帧（以’\0’结尾的16进制字符串或常量字符串），
     * @param resdata 响应帧（以’\0’结尾的16进制字符串或常量字符串，含SW1和SW2）
     * @return 若返回值等于0，则为执行成功；若返回值不等于0，则执行失败
     */
    public int SAM_Command(byte iComNo, String command, byte[] resdata);

    /**
     * 本方法是SAM卡指令的通用支持函数，可实现SAM卡的任意TIMECOS指令（含SW1和SW2）。
     * 注意：1.预留给resdata的长度要足够长，必须是所需要的到的二进制信息长度的2倍。如取8字节随机数，则resdata长度至少为20(含SW1和SW2)。
     * 2.预留给resdata和sw12的长度要足够长，必须是所需要的到的二进制信息长度的2倍。如取8字节随机数，则resdata长度至少为16。
     *
     * @param iComNo 已经打开的串口号
     * @param command 指令帧（以’\0’结尾的16进制字符串或常量字符串），
     * @param resdata 响应帧（以’\0’结尾的16进制字符串或常量字符串，含SW1和SW2）
     * @param sw12 响应状态码SW1和SW2的字符串显示，如“9000” （以’\0’结尾的16进制字符串或常量字符串）等
     * @return 若返回值等于0，则为执行成功；若返回值不等于0，则执行失败
     */
    public int SAM_CommandEx(byte iComNo, String command, byte[] resdata,
            byte[] sw12);

}
