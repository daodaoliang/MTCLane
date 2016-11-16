package com.hgits.control;

import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;

/**
 * 测试功能控制类
 *
 * @author Wang Guodong
 */
public class TestControl {

    /**
     * 是否启用模拟冲关车功能(默认不启用)
     *
     * @return true/false
     */
    public static boolean isVioSimuActive() {
        //#违章标识（0 每30秒左右自动出现违章 1 正常）
        String violateCode = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_TEST, "violateCode", "1");
        return "0".equalsIgnoreCase(violateCode);
    }

    /**
     * 是否忽略写卡，默认不忽略
     *
     * @return true/false
     */
    public static boolean isCardWriteIgnored() {
        //测试模式（ 0开启测试模式，屏蔽通行卡，ETC卡写卡 1关闭测试模式，正常运行）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_TEST, "testCode", "1");
        return "0".equals(str);
    }

    /**
     * 是否忽略车型车牌差异
     *
     * @return true/false
     */
    public static boolean isVehDiffIgnored() {
        //车型车牌差异忽略标识（0忽略车型车牌都差异，1车型车牌差异时跳最远站，ETC卡无法使用）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_TEST, "vehCode", "1");
        return "0".equals(str);
    }

    /**
     * 是否忽略卡状态（默认不忽略）
     *
     * @return true/false
     */
    public static boolean isCardStatusIgnored() {
        //卡状态判断标识（0 忽略卡状态（出入口）,忽略卡箱容量 ,忽略写卡后卡是否在卡箱上判断 1判断卡状态（出入口）,判断卡箱容量，判断写卡后卡是否在卡箱上）读写卡压力测试时忽略卡状态,
        String cardCode = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_TEST, "cardCode", "1");
        return "0".equalsIgnoreCase(cardCode);
    }

    /**
     * 是否忽略U型车，默认不忽略
     *
     * @return true/false
     */
    public static boolean isUVehIgnored() {
        //U型车判断标识（0忽略U型车，1判断U型车）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_TEST, "uCode", "1");
        return "0".equals(str);

    }

    /**
     * 是否忽略ETC卡发行方
     *
     * @return true/false
     */
    public static boolean isETCIssuerIgnored() {
        //#ETC卡发行方标识 1判断ETC卡发行方； 0不判断ETC卡发行方（默认为1）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_TEST, "issuerCode", "1");
        return "0".equals(str);
    }

    /**
     * 是否ETC卡强制打1折,默认否
     *
     * @return true/false
     */
    public static boolean isETCDisActive() {
        //#ETC卡发行方标识 1判断ETC卡发行方； 0不判断ETC卡发行方（默认为1）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_TEST, "disCode", "1");
        return "0".equals(str);
    }

    /**
     * 是否不用刷身份卡自动上班，默认否
     *
     * @return true/false
     */
    public static boolean isAutoLogin() {
        //上班标识 (0不需要刷身份卡，自动弹出上班窗口，默认员工号000018；1需要刷身份卡才能上班)
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_TEST, "loginCode", "1");
        return "0".equals(str);
    }
}
