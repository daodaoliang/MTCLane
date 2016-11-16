package com.hgits.realTimePath;

import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.rate.ParamCacheQuery;
import com.hgits.vo.Constant;

import org.apache.log4j.Logger;

/**
 * 路径识别服务类
 *
 * @author WangGuodong
 */
public class RealTimeSvc {

    private static final Logger logger = Logger.getLogger(RealTimeSvc.class.getName());

    /**
     * 根据路径识别的路段编号获取路段名称
     *
     * @param rtRoad 路径识别的路段编号
     * @return 路段名称
     */
    public String getRoadNameFromRtRoad(String rtRoad) {
        String road = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_RTP, rtRoad, "");
        if (road != null && !road.trim().isEmpty()) {
            String[] buffer = road.split(",");
            return buffer[0];
        }
        return rtRoad;
    }

    /**
     * 根据路径识别桥隧编号获取桥隧名称
     *
     * @param qsid 路径识别桥隧编号
     * @return 桥隧名称
     */
    public String getQSNameFromQSid(String qsid) {
        String qs = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_RTP, qsid, "");
        if (qs != null && !qs.trim().isEmpty()) {
            String[] buffer = qs.split(",");
            return buffer[0];
        }
        return qsid;
    }

    /**
     * 根据路径识别的路段编号获取实际的路段唯一编号
     *
     * @param twRoadid 路径识别路段编号
     * @return 路段唯一编号
     */
    public String getUniqueRoadFromRtRoad(String twRoadid) {
        String road = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_RTP, twRoadid, "");
        if (road != null && !road.trim().isEmpty()) {
            String[] buffer = road.split(",");
            String roadName = buffer[2];
            Integer uniqueRoadid = ParamCacheQuery.queryRoadUniqueId(roadName);
            logger.debug("twRoadid="+twRoadid+",roadName="+roadName+",uniqueRoadid="+uniqueRoadid);
            return String.valueOf(uniqueRoadid);
        } else {
            logger.warn("根据路径识别编号" + twRoadid + "获取路段惟一编号失败");
            return null;
        }
    }

}
