package com.hgits.hardware;

import com.hgits.vo.Card;

/**
 * M1读卡器设备
 *
 * @author zengzb
 *
 */
public interface IcReaderWriter extends Runnable {

    /**
     * 获取index的值
     *
     * @return
     */
    public Byte getIndex();
    /**
     * index的值加1
     */
    public void increaseIndex();

    /**
     * 设置判断是否有卡在读写器上
     *
     * @param hasCard 是否有卡
     */
    public void setHasCard(boolean hasCard);

    /**
     * 获取判断是否有卡在读写器上
     *
     * @return
     */
    public boolean getHasCard();

    /**
     * 获取通行卡是否已被读
     *
     * @return
     */
    public boolean getCardIsRead();

    /**
     * 设置通行卡是否已被读
     *
     * @param cardIsRead 卡是否已读
     */
    public void setCardIsRead(boolean cardIsRead);

    /**
     * 获取读取的卡号
     *
     * @return
     */
    public String getCardNo();

    /**
     * 设置卡号
     *
     * @param cardNo
     */
    public void setCardNo(String cardNo);

    /**
     * 获取是否读到卡序号标识
     *
     * @return
     */
    public boolean isFlag42();

    /**
     * 设置
     *
     * @param flag42
     */
    public void setFlag42(boolean flag42);

    /**
     * 设置卡片是否已写
     *
     * @param cardIsWritten 卡是否已写
     */
    public void setCardIsWritten(boolean cardIsWritten);

    /**
     * 设置card的值
     *
     * @param card
     */
    public void setCard(Card card);

    /**
     * 从读卡器获取装载通行卡信息的实例
     *
     * @return 装载通行卡信息的实例，无卡时返回null
     */
    public Card getReadedCard();

    /**
     * 直接返回 Card对象
     *
     * @return
     */
    public Card getCardObj();

    /**
     * 将需要写入卡中的车辆信息载入并且将卡片状态更改为readyToWrite,是卡片进入写卡,直至写卡成功
     *
     * @param card 需要写入的卡片信息
     * @return 写卡成功返回0，失败返回非0
     */
//    public int write(Card card);

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
     * 关闭读写器
     *
     * @return 0表示成功；-1表示失败
     */
    public int exit();

    /**
     * 获取M1卡的读写器的错误信息
     *
     * @return 错误信息
     */
    public String getErrorMessage();

    /**
     * 向串口发送相应数据
     *
     * @param buffer 需要发送的数据
     */
    public void write(byte[] buffer);

    /**
     * 向串口发送相应数据
     *
     * @param b 需要发送的数据
     */
    public void write(byte b);

    /**
     * 读取串口数据，若无数据返回null
     *
     * @return 串口数据
     */
    public Byte readByte();

    /**
     * 读取串口数据，若无数据返回null
     *
     * @return 串口数据
     */
    public byte[] readByteArray();

    /**
     * 返回运行状态
     *
     * @return 运行状态
     */
    public boolean isRunning();

}
