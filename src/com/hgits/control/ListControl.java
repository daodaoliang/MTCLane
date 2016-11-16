package com.hgits.control;

import com.hgits.util.LaneListUtils;
import com.hgits.vo.ColList;
import com.hgits.vo.LaneEnList;
import com.hgits.vo.LaneExList;
import com.hgits.vo.LaneLogin;
import com.hgits.vo.LaneLogout;
import com.hgits.vo.LaneShift;
import com.hgits.vo.SimpleLaneExList;
import org.apache.log4j.Logger;
import ui.ExtJFrame;

/**
 * 流水生成控制类
 *
 * @author Wang Guodong
 */
public class ListControl {

    private static final ExtJFrame extJFrame = ExtJFrame.getSingleInstance();
    private static final Logger logger = Logger.getLogger(ListControl.class.getName());
    private static final String LANEEXLIST = "出口交易流水";
    private static final String LANEENLIST = "入口交易流水";
    private static final String LANELOGOUT = "下班流水";
    private static final String LANESHIFT = "工班流水";
    private static final String LANELOGIN = "上班流水";
    private static final String LANECOLLIST = "代收流水";
    private static final String SIMPLELANEEXLIST = "简单出口交易流水";

    /**
     * 产生出口交易流水
     *
     * @param exList 出口交易流水
     */
    public static void generateLaneExList(LaneExList exList) {
        String listName = LANEEXLIST;
        extJFrame.updateInfo("", "正在生成" + listName);
        logger.debug("正在生成" + listName + exList);
        int i = 0;
        while (true) {
            try {
                LaneListUtils.generationExList(exList);
                break;
            } catch (Exception ex) {
                i++;
                extJFrame.updateInfo("", "第" + i + "次生成" + listName + "失败\n正在重新生成" + listName);
                logger.error("第" + i + "次生成" + listName + "失败", ex);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.error(ex, ex);
            }
        }
        extJFrame.updateInfo("", listName + "生成完成");
        logger.debug(listName + "生成完成");
    }

    /**
     * 产生入口交易流水
     *
     * @param enList 入口交易流水
     */
    public static void generateLaneEnList(LaneEnList enList) {
        String listName = LANEENLIST;
        extJFrame.updateInfo("", "正在生成" + listName);
        logger.debug("正在生成" + listName + enList);
        int i = 0;
        while (true) {
            try {
                LaneListUtils.generationEnList(enList);
                break;
            } catch (Exception ex) {
                i++;
                extJFrame.updateInfo("", "第" + i + "次生成" + listName + "失败\n正在重新生成" + listName);
                logger.error("第" + i + "次生成" + listName + "失败", ex);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.error(ex, ex);
            }
        }
        extJFrame.updateInfo("", listName + "生成完成");
        logger.debug(listName + "生成完成");
    }

    /**
     * 生成下班流水
     *
     * @param logoutList 下班流水
     */
    public static void generateLaneLogout(LaneLogout logoutList) {
        String listName = LANELOGOUT;
        extJFrame.updateInfo("", "正在生成" + listName);
        logger.debug("正在生成" + listName + logoutList);
        int i = 0;
        while (true) {
            try {
                LaneListUtils.generationLaneLogoutList(logoutList);
                break;
            } catch (Exception ex) {
                i++;
                extJFrame.updateInfo("", "第" + i + "次生成" + listName + "失败\n正在重新生成" + listName);
                logger.error("第" + i + "次生成" + listName + "失败", ex);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.error(ex, ex);
            }
        }
        extJFrame.updateInfo("", listName + "生成完成");
        logger.debug(listName + "生成完成");
    }

    /**
     * 生成工班流水
     *
     * @param shiftList 工班流水
     */
    public static void generateLaneShift(LaneShift shiftList) {
        String listName = LANESHIFT;
        extJFrame.updateInfo("", "正在生成" + listName);
        logger.debug("正在生成" + listName + shiftList);
        int i = 0;
        while (true) {
            try {
                LaneListUtils.generationLaneShiftList(shiftList);
                break;
            } catch (Exception ex) {
                i++;
                extJFrame.updateInfo("", "第" + i + "次生成" + listName + "失败\n正在重新生成" + listName);
                logger.error("第" + i + "次生成" + listName + "失败", ex);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.error(ex, ex);
            }
        }
        extJFrame.updateInfo("", listName + "生成完成");
        logger.debug(listName + "生成完成");
    }

    /**
     * 产生上班流水
     *
     * @param loginList 上班流水
     */
    public static void generateLaneLogin(LaneLogin loginList) {
        String listName = LANELOGIN;
        extJFrame.updateInfo("", "正在生成" + listName);
        logger.debug("正在生成" + listName + loginList);
        int i = 0;
        while (true) {
            try {
                LaneListUtils.generationLaneLoginList(loginList);
                break;
            } catch (Exception ex) {
                i++;
                extJFrame.updateInfo("", "第" + i + "次生成" + listName + "失败\n正在重新生成" + listName);
                logger.error("第" + i + "次生成" + listName + "失败", ex);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.error(ex, ex);
            }
        }
        extJFrame.updateInfo("", listName + "生成完成");
        logger.debug(listName + "生成完成");
    }

    /**
     * 生成代收流水
     *
     * @param colList 代收流水
     */
    public static void generateLaneColList(ColList colList) {
        String listName = LANECOLLIST;
        extJFrame.updateInfo("", "正在生成" + listName);
        logger.debug("正在生成" + listName + colList);
        int i = 0;
        while (true) {
            try {
                LaneListUtils.genColList(colList);
                break;
            } catch (Exception ex) {
                i++;
                extJFrame.updateInfo("", "第" + i + "次生成" + listName + "失败\n正在重新生成" + listName);
                logger.error("第" + i + "次生成" + listName + "失败", ex);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.error(ex, ex);
            }
        }
        extJFrame.updateInfo("", listName + "生成完成");
        logger.debug(listName + "生成完成");
    }

    /**
     * 生成简单出口交易流水
     *
     * @param simpleLaneExList 简单出口交易流水
     */
    public static void generateLaneSimpleLaneExList(SimpleLaneExList simpleLaneExList) {
        String listName = SIMPLELANEEXLIST;
        extJFrame.updateInfo("", "正在生成" + listName);
        logger.debug("正在生成" + listName + simpleLaneExList);
        int i = 0;
        while (true) {
            try {
                LaneListUtils.generationSimpleExList(simpleLaneExList);
                break;
            } catch (Exception ex) {
                i++;
                extJFrame.updateInfo("", "第" + i + "次生成" + listName + "失败\n正在重新生成" + listName);
                logger.error("第" + i + "次生成" + listName + "失败", ex);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.error(ex, ex);
            }
        }
        extJFrame.updateInfo("", listName + "生成完成");
        logger.debug(listName + "生成完成");
    }

}
