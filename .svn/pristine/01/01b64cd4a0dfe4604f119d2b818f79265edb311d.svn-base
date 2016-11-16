package com.hgits.hardware;

/**
 * 卡机设备
 *
 * @author zengzb
 *
 */
public interface CICM {

    /**
     * 降下后卡栓
     *
     * @throws Exception
     */
    public void backBoltLower() throws Exception;

    /**
     * 升起后卡栓
     *
     * @throws Exception
     */
    public void backBoltRaise() throws Exception;

    /**
     * 降下前卡栓
     *
     * @throws Exception
     */
    public void frontBoltLower() throws Exception;

    /**
     * 检测收发头状态 true 抬起 false合上
     *
     * @return
     */
    public boolean checkReceiveHead();

    /**
     * 检查卡机保护盖状态 true 打开 false 关闭
     *
     * @return
     */
    public boolean checkProtectCover();

    /**
     * 检测收发针状态 0 收发针降到底 1 收发针升到顶 2 收发针在卡箱中间
     *
     * @return
     */
    public int checkReceivePin();

    /**
     * 收发针下降
     *
     * @throws Exception
     */
    public void receivePinLower() throws Exception;

    /**
     * 保护盖开锁
     *
     * @throws Exception
     */
    public void protectiveCoverOpen() throws Exception;

    /**
     * 保护盖上锁
     *
     * @throws Exception
     */
    public void protectiveCoverLockUp() throws Exception;

    /**
     * 收发针上升
     *
     * @throws Exception
     */
    public void receivePinRaise();

    /**
     * 收发针下降两格
     *
     * @throws Exception
     */
    public void lowerTwoSteps() throws Exception;

    /**
     * 升起前卡栓
     *
     * @throws Exception
     */
    public void frontBoltRaise() throws Exception;

    /**
     * 收发针下降一格
     *
     * @throws Exception
     */
    public void lowerOneStep() throws Exception;

    /**
     * 获取卡机错误信息
     *
     * @return
     */
    public String getCicmErrorMsg();

    /**
     * 收发针上升到顶或收发头抬起
     *
     * @return true/false
     */
    public boolean isPinTop();

    /**
     * 收发针到底
     *
     * @return true/false
     */
    public boolean isPinBottom();
    /**
     * 当前卡机是否可用
     * @return true/false
     */
    public boolean isAvailable();
}
