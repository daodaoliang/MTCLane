package com.hgits.service;

import com.hgits.vo.CardBoxOpList;
import com.hgits.vo.ColList;
import com.hgits.vo.LaneEnList;
import com.hgits.vo.LaneExList;
import com.hgits.vo.LaneLogout;
import com.hgits.vo.LaneShift;
import org.apache.log4j.Logger;

/**
 * 用于记录临时文件解析临时文件的接口
 *
 * @author Wang Guodong
 */
public interface TempServiceInterface {

    public final String TEMP_LOGOUT_PATH = "temp/temp_logout.txt";//记录下班信息的临时文件
    public final String TEMP_SHIFT_PATH = "temp/temp_shift.txt";//记录工班日志的临时文件
    public final String TEMP_EXLIST_PATH = "temp/temp_exlist.txt";//记录出口交易流水的临时文件
    public final String TEMP_ENLIST_PATH = "temp/temp_enlist.txt";//记录入口交易流水的临时文件
    public final String TEMP_COLLIST_PATH = "temp/temp_collist.txt";//记录代收流水的临时文件
    public final String TEMP_BALANCE_OFFLINE_PATH = "temp/temp_offLine.txt";//记录剩余离线倒计时时间的临时文件
    public final String TEMP_ETCINFO = "temp/temp_etcInfo.txt";//记录ETC卡信息的临时文件
    public final String TEMP_CARDBOXOPLIST = "temp/temp_cardboxoplist.txt";
    public static final Logger logger = Logger.getLogger(TempServiceInterface.class.getName());

    /**
     * 生成下班临时文件
     *
     * @param logout 下班记录
     */
    public void generateTempLogout(LaneLogout logout);

    /**
     * 生成临时工班记录文件
     *
     * @param shift 工班记录
     */
    public void generateTempShift(LaneShift shift);

    /**
     * 生成临时出口流水记录文件
     *
     * @param list 出口流水记录
     */
    public void generateTempExList(LaneExList list);

    /**
     * 生成临时入口流水记录文件
     *
     * @param list 入口流水记录
     */
    public void generateTempEnList(LaneEnList list);

    /**
     * 生成代收流水临时记录文件
     *
     * @param list 代收流水文件
     */
    public void generateTempColList(ColList list);

    /**
     * 生成剩余离线倒计时时间文件
     *
     * @param str 剩余离线倒计时时间
     */
    public void generateTempOffLine(String str);

    /**
     * 生成记录ETC卡信息的临时文件
     *
     * @param etcInfo
     */
    public void generateTempEtcInfo(String etcInfo);

    /**
     * 解析离线倒计时文件，获取剩余离线倒计时时间
     *
     * @return 剩余离线倒计时时间
     */
    public String parseTempOffLine();

    /**
     * 解析下班临时文件，获取下班信息
     *
     * @return 下班信息
     */
    public LaneLogout parseTempLogout();

    /**
     * 解析工班临时文件，获取工班信息
     *
     * @return 工班信息
     */
    public LaneShift parseTempShift();

    /**
     * 解析出口流水临时文件，获取出口流水信息
     *
     * @return 出口流水信息
     */
    public LaneExList parseTempExList();

    /**
     * 解析入口流水临时文件，获取入口流水信息
     *
     * @return 入口流水信息
     */
    public LaneEnList parseTempEnList();

    /**
     * 解析代收流水临时文件，获取代收流水信息
     *
     * @return 代收流水信息
     */
    public ColList parseTempColList();

    /**
     * 解析并获取ETC临时文件信息
     *
     * @return 解析后的信息
     */
    public String parseTempEtcInfo();

    /**
     * 清空临时下班流水文件
     */
    public void deleteTempLogout();

    /**
     * 清空临时工班流水文件
     */
    public void deleteTempShift();

    /**
     * 清空临时出口流水文件
     */
    public void deleteTempExlist();

    /**
     * 清空临时入口流水文件
     */
    public void deleteTempEnlist();

    /**
     * 清空临时代收流水文件
     */
    public void deleteTempCollist();

    /**
     * 清空倒计时剩余时间文件
     */
    public void deleteTempOffLine();

    /**
     * 清空ETC临时文件
     */
    public void deleteTempEtcInfo();

    /**
     * 临时文件中记录版本号信息
     *
     * @param fileName 版本号文件名称
     * @param version 版本号
     */
    public void generateTempVersion(String fileName, String version);

    /**
     * 产生临时卡箱流动单
     *
     * @param list 卡箱流动单
     */
    public void generateTempCardBoxOpList(CardBoxOpList list);

    /**
     * 删除临时卡箱流动单
     */
    public void deleteTempCardBoxOpList();

    /**
     * 解析卡箱流动单临时文件
     *
     * @return 卡箱流动单
     */
    public CardBoxOpList parseTempCardBoxOpList();
}
