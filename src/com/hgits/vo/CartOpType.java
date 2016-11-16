package com.hgits.vo;

/**
 * 卡箱操作类型
 *
 * @author Wang Guodong
 */
public class CartOpType {

    /**
     * 发送(下班，取出卡箱算发送)
     */
    public static final int SEND = 0;
    /**
     * 接收(上班，装入卡箱算接收)
     */
    public static final int RECV = 1;
    /**
     * 交接卡箱
     */
    public static final int TRAN = 2;

}
