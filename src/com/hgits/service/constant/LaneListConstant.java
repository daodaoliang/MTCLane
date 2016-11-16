/**
 *
 */
package com.hgits.service.constant;

/**
 * 车道流水相关的常量
 *
 * @author wh
 *
 */
public interface LaneListConstant extends BaseConstant {

    /**
     * 入口流水
     */
    public static final String TB_LANEENLIST = "tb_LaneEnList_Recv";
    /**
     * 出口流水
     */
    public static final String TB_LANEEXLIST = "tb_LaneExList_Recv";
    /**
     * 简单出口流水
     */
    public static final String TB_SIMPLELANEEXLIST = "tb_SimpleLaneExList_Recv";
    /**
     * 车道工班日志
     */
    public static final String TB_LANESHIFT = "tb_LaneShift";
    /**
     * 入口流水类型
     */
    public static final int EN_LIST_TYPE = 2;
    
    /**
     * 出口流水类型
     */
    public static final int EX_LIST_TYPE = 3;
    
}
