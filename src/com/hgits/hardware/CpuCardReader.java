package com.hgits.hardware;

import com.hgits.vo.Card;
import com.hgits.vo.CpuCard;

/**
 * ETC读卡器
 *
 * @author zengzb
 *
 */
public interface CpuCardReader {

    public final int NO_ERROR = 0;//操作成功执行
    public final int SAM_NOT_EXIST = -226;//发现湘通卡读写器，但湘通卡读写器中无PSAM卡
    public final int INVALID_PSAM = -228;//PSAM卡不可用

    /**
     * 获取CPU卡信息
     *
     * @return CPU卡信息，无卡返回null
     */
    public CpuCard getCpuCard();

    /**
     * 获取M1卡信息
     *
     * @return m1卡
     */
    public Card getM1Card();

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
    public int write(Card card, boolean sec5Flag, boolean sec6Flag, boolean sec8Flag, boolean sec9Flag);

    /**
     * 写卡
     *
     * @param card 装载信息的卡片实体类
     * @return 写卡结果 0则成功，非0则失败
     */
    public int write(CpuCard card);

    /**
     * 关闭读写器
     *
     * @return 操作的结果
     */
    public int closeReader();

    /**
     * 获取异常消息
     *
     * @return
     */
    public String getErrorMsg();
}