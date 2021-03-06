package com.hgits.control;

import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;

/**
 * 功能控制类
 *
 * @author Wang Guodong
 */
public class FunctionControl {

    /**
     * 是否启用代收功能
     *
     * @return true/false
     */
    public static boolean isCollActive() {
        //代收流程标志（机场高速）0不启用 1启用（默认0）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "collectFlag", "0");
        return "1".equals(str);
    }

    /**
     * 是否启用打印车牌号码功能
     *
     * @return true/false
     */
    public static boolean isPlatePrintActive() {
        //#打印标识，0不打印车牌 1 打印车牌（默认0）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "printflag", "0");
        return "1".equals(str);
    }

    /**
     * 是否启用电脑键盘
     *
     * @return true/false
     */
    public static boolean isPcKeyboardActive() {
        //电脑键盘启用标识  0不启用 1 启用（默认0）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "pcKeyboardCode", "0");
        return "1".equals(str);
    }

    /**
     * 是否启用ETC车型差异判断
     *
     * @return
     */
    public static boolean isEtcClassDiffActive() {
        //新国标ETC卡车型差异处理标识 0 入口etc卡车型差异不处理，出口etc卡车型差异走监控室确认流程 1 出入口提示车型差异，要求司机出示正确ETC卡或收费员修改车型
        //本标识只针对ETC卡版本号以4开头的的新国标ETC卡，对于旧国标ETC卡（版本号为10）按原有流程处理（默认0）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "etcClassDiffFlag", "0");
        return "1".equals(str);
    }

    /**
     * 是否启用自助发卡
     *
     * @return true/false
     */
    public static boolean isAutoMachineActive() {
        //自助发卡机标识 0 不启用自助发卡机 1 启用自助发卡机（默认0）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "autoMachineFlag", "0");
        return "1".equals(str);
    }

    /**
     * 是否启用ETC卡出口检验放行
     *
     * @return true/false
     */
    public static boolean isEtcExitActive() {
        //ETC卡出口检验放行标识 0不启用 1 启用（默认0）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "etcExitCode", "0");
        return "1".equals(str);
    }

    /**
     * ETC卡出口检验放行时是否记录简单流水
     *
     * @return true/false
     */
    public static boolean isetcExitListActive() {
        //#ETC出口检验放行时是否记录简单交易流水 0 不记录 1 记录（默认0）
        //etcExitListCode=0
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "etcExitListCode", "0");
        return "1".equals(str);
    }

    /**
     * 是否车型车牌差异跳最远站
     *
     * @return true/false
     */
    public static boolean isDiffJumpActive() {
        //车型差异车牌差异跳最远站标识,0-不跳最远站，其他-跳最远站（默认0）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "diffFlag", "0");
        return "1".equals(str);
    }

    /**
     * 是否下班发现升级文件自动升级
     *
     * @return true/false
     */
    public static boolean isAutoUpdateActive() {
        //下班状态下发现升级文件是否自动升级，0-自动升级，1-不自动升级（默认0）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "autoUpdateFlag", "0");
        return "0".equals(str);
    }

    /**
     * 是否手动拆分拖车
     *
     * @return true/false
     */
    public static boolean isManualTowActive() {
        //拖车标识符0自动拆分拖车，1手动拆分拖车(默认0)
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "towFlag", "0");
        return "1".equals(str);
    }

    /**
     * 是否启用防盗车取卡功能
     *
     * @return true/false
     */
    public static boolean isAntiVehBackActive() {
        //防盗车取卡启用标识 0不启用 1启用
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "AntiVehBackFlag", "0");
        return "1".equals(str);
    }

    /**
     * 入口是否支持6型车的输入
     *
     * @return true/false
     */
    public static boolean isEntry6Available() {
        //入口支持6型车输入，0支持 1 不支持，默认0
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "entry6Flag", "0");
        return "0".equals(str);
    }

    /**
     * 自助发卡车道是否支持黑名单语音提示
     *
     * @return true/false
     */
    public static boolean isAutoBlkVehAlertActive() {
        //自助发卡车道黑名单车辆语音提示，0不支持 1支持，默认1
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "autoBlackVehAlert", "1");
        return "1".equals(str);
    }

    /**
     * 自助发卡车道是否支持灰名单语音提示
     *
     * @return true/false
     */
    public static boolean isAutoGrayVehAlertActive() {
        //自助发卡车道黑名单车辆语音提示，0不支持 1支持，默认1
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "autoGrayVehAlert", "1");
        return "1".equals(str);
    }

    /**
     * 是否启用纸券功能
     *
     * @return true/false
     */
    public static boolean isPaperCardActive() {
        //纸券功能，0支持 1不支持，默认0
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "paperCardFlag", "0");
        return "0".equals(str);
    }

    /**
     * 是否启用在线收费功能
     *
     * @return true/false
     */
    public static boolean isOnlineActive() {
        //在线收费功能 0启用 1不启用 默认1
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "onlineMode", "1");
        return "0".equals(str);
    }

    /**
     * 是否启用模拟通行监控室确认
     *
     * @return true/false
     */
    public static boolean isSimuConfirmActive() {
        //模拟通行监控室确认 0 启用 1不启用 默认1
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "simuConfrimFlag", "1");
        return "0".equals(str);
    }

    /**
     * 是否启用子轨输入功能
     *
     * @return true/false
     */
    public static boolean isTrackActive() {
        //字轨号输入功能 0 启用 1 不启用 默认1
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "trackFunction", "1");
        return "0".equals(str);
    }

    /**
     * 是否启用通行卡加密功能
     *
     * @return true/false
     */
    public static boolean isCscEncryptActive() {
        //通行卡加密功能 0 启用 1 不启用 默认1
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "encryptFunction", "1");
        return "0".equals(str);
    }

    /**
     *
     * 是否启用通行卡加密校验失败处理功能
     *
     * @return true启用；false不启用
     */
    public static boolean isCodeErrorFunActive() {
        //是否启用通行卡加密校验失败处理功能， 0启用 1 不启用，默认1
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "encryptErrorFunction", "1");
        return "0".equals(str);
    }

    /**
     * 是否启用通行卡未加密处理功能
     *
     * @return true启用；false不启用
     */
    public static boolean isUnCodeFunActive() {
        //是否启用通行卡未加密处理功能， 0启用 1 不启用，默认1
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "unEncryptFunction", "1");
        return "0".equals(str);
    }

    /**
     * 是否启用ETC读写器读写M1通行卡功能
     *
     * @return true启用；false不启用
     */
    public static boolean isEtcTscFunActive() {
        //ETC读写器读写M1通行卡功能  0启用 1不启用 默认1
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "etcTscFunction", "1");
        return "0".equals(str);
    }

    /**
     * 是否启用自助发卡车道凌晨禁止大客车取卡功能
     *
     * @return true/false
     */
    public static boolean isAutoForbidActive() {
        //自助发卡车道凌晨禁止大客车取卡功能0-不启用 1-启用（默认0）
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "autoForbidFunction", "0");
        return "1".equals(str);
    }

    /**
     * 是否启用人工发卡车道接入自助发卡机功能
     *
     * @return true/false
     */
    public static boolean isManualAutoFunActive() {
        //人工发卡车道接入自助发卡机 0-启用 1-不启用，默认1
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "manualAutoFunction", "1");
        return "0".equals(str);
    }

    /**
     * 是否启用货车湘通储值卡打折功能
     *
     * @return true/false
     */
    public static boolean isTruckETCDisFunActive() {
        //是否启用货车湘通储值卡打折功能 0-启用 1-不启用，默认0
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "truckETCDisFun", "0");
        return "0".equals(str);
    }

    /**
     * 是否启用入口已发通行卡再刷ETC卡功能
     *
     * @return true/false
     */
    public static boolean isCscToEtcActive() {
        //入口已发通行卡再刷ETC卡的功能 0-启用 1-不启用 默认0
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "cscToEtcFun", "0");
        return "0".equals(str);
    }

    /**
     * 是否启用出口现金付款时必须输入收取通行费并且输入金额需大于等于应收通行费功能
     *
     * @return true/false
     */
    public static boolean isFareInputFunActive() {
        //出口现金付款时必须输入收取通行费并且输入金额需大于等于应收通行费功能 0-启用 1-不启用 默认1
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "fareInputFun", "1");
        return "0".equals(str);

    }

    /**
     * 是否启用全键盘
     *
     * @return true/false
     */
    public static boolean isFullKeyboardFunActive() {
//        是否启用全键盘 0-启用 1-不启用 默认1
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "fullKeyboardFunc", "1");
        return "0".equals(str);
    }

    /**
     * 是否启用季票卡功能
     *
     * @return true/false
     */
    public static boolean isServiceCardFunActive() {
        //是否启用季票卡功能 0-启用 1-不启用 ，默认1 
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "serviceCardFunc", "1");
        return "0".equals(str);
    }

}
