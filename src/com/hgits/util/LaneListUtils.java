/**
 *
 */
package com.hgits.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.hgits.service.constant.DateConstant;
import com.hgits.service.constant.LaneListConstant;
import com.hgits.util.file.FileUtils;
import com.hgits.util.rate.ParamCache;
import com.hgits.util.rate.ParamCacheQuery;
import com.hgits.util.rate.ParamVersionUtils;
import com.hgits.vo.CardBoxOpList;
import com.hgits.vo.CartInfo;
import com.hgits.vo.ColList;
import com.hgits.vo.Constant;
import com.hgits.vo.LaneAlarm;
import com.hgits.vo.LaneEnList;
import com.hgits.vo.LaneExList;
import com.hgits.vo.LaneLogin;
import com.hgits.vo.LaneLogout;
import com.hgits.vo.LaneParaVer;
import com.hgits.vo.LaneParam;
import com.hgits.vo.LaneShift;
import com.hgits.vo.SimpleLaneExList;
import com.hgits.vo.Version;

/**
 * 车道流水工具类
 *
 * @author wh
 *
 */
public class LaneListUtils {

    public static final Logger logger = Logger.getLogger(LaneListUtils.class);

    /**
     * 生成入口流水
     *
     * @param lane 入口流水信息
     * @throws Exception 
     */
    public static void generationEnList(LaneEnList lane) throws Exception {
        String head = LaneListConstant.TB_LANEENLIST;

        String content = generationLaneEn(lane);  //生成入口流水文件内容

        genEnListFinallyFile(head, content); //生成入口流水最终文件
//        genEnListTempFile(head, content); //生成入口流水临时文件

    }

    /**
     * 入口最终文件
     *
     * @param head 文件头
     * @param content 文件内容
     * @throws Exception 
     */
    public static void genEnListFinallyFile(String head, String content) throws Exception {
        String file = getFileName(LaneListConstant.TB_LANEENLIST);
        writeListFile(file, head, content);
    }

//    /**
//     * 入口临时文件
//     *
//     * @param head 文件头
//     * @param content 文件内容
//     */
//    public static String genEnListTempFile(String head,String content) {
//        String file = getTempFileName(head+"_"+DateUtils.formatDateToString(
//                DateUtils.getCurrentDate(), "yyyyMMddHHmmssSSS")); //临时流水文件名
//        FileUtils.write(file, LaneListConstant.TB_LANEENLIST, true); // 添加文件头
//        FileUtils.write(file, content, true); // 添加文件内容
//        return file;
//    }
    /**
     * 生成简单出口流水(ETC卡直接放行时使用)
     *
     * @param lane 简单出口流水
     * @throws Exception 
     */
    public static void generationSimpleExList(SimpleLaneExList lane) throws Exception {
        String head = LaneListConstant.TB_SIMPLELANEEXLIST;
        String content = generationSimpleLaneEx(lane); //生成出口流水文件内容
        genSimpleExListFinallyFile(head, content); //生成出口流水最终文件
    }

    /**
     * 生成一条简单出口流水
     *
     * @param lane 简单出口流水记录
     * @return 简单出口流水内容
     */
    private static String generationSimpleLaneEx(SimpleLaneExList lane) {
        StringBuilder sbf = new StringBuilder(LaneListConstant.STRING_BUFFER_SIZE);
        sbf.append(parseNullInteger(lane.getRecordId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getListNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getEnListNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnRoadId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnStationId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getEnStationName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getEnLaneNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnLaneId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnLaneType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnVehType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnVehClass())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getEnVehPlate())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnVehPlateColor())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnSquadId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getEnOperatorNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(lane.getEnOpTime())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getRoadId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getStationId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getStationName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getLaneNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getLaneId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getLaneType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getVehType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getVehClass())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getVehPlate())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getVehPlateColor())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(lane.getLoginTime())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getSquadId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(lane.getSquadDate())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOperatorNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOperatorName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOpCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(lane.getOpTime())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getTicketType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getChargeType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getComputerMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getPayCardType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getPassCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getCardBoxNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getuPCardType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getuPCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullLong(lane.getuPCardBalanceBf())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullLong(lane.getuPCardBalanceAf())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getuPCardDiscount())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getuPCardMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getoBUId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getxTCardType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getxTCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullLong(lane.getxTCardBalanceBf())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullLong(lane.getxTCardBalanceAf())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getxTCardDiscount())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getxTCardMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getpDiscountToll())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getCashMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getFreeMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getOfficeMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getUnpayMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getGetMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getWeightFlag())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getAxisNum())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getAxisGroupNum())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getAxisType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getPreAxisType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getTotalWeight())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getPreTotalWeight())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getAxisWeightDetail())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getTotalWeightForCharge())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getTotalWeightLimit())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getOverWeight())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getOverRatio())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getPreVehType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getPreVehMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getPlateImageNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getVehImageNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getSpEvent())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getCardSelcetCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOpSelcetCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getVehChargeSelcetCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getVehPassSelcetCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getInvoiceId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getSectionPath())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getSectionCharge())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getVehTypeAuto())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getVehPlateAuto())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getPlateAutoColor())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getpSAMCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getProgramVer())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getPriceVer())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOfficialCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getFreeRoadIdSer())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.geteTCTradeNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.geteTCTac())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.geteTCTermTradNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.geteTCTermCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getSpare1())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getSpare2())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getSpare3())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getSpare4())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getTransferTag())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getVerifyCode()));
//		sbf.append(LaneListConstant.WRAP_STRING);
        return sbf.toString();
    }

    /**
     * 简单出口流水最终文件
     *
     * @param head 文件头
     * @param content 文件内容
     * @throws Exception 
     */
    private static void genSimpleExListFinallyFile(String head, String content) throws Exception {
        String file = getFileName(LaneListConstant.TB_SIMPLELANEEXLIST);
        writeListFile(file, head, content);
    }

    /**
     * 生成出口流水
     *
     * @param lane 出口流水信息
     * @throws Exception 
     */
    public static void generationExList(LaneExList lane) throws Exception {
        String head = LaneListConstant.TB_LANEEXLIST;

        String content = generationLaneEx(lane); //生成出口流水文件内容

        genExListFinallyFile(head, content); //生成出口流水最终文件
//        genExListTempFile(head,content); //生成出口流水临时文件

    }

    /**
     * 生成代收流水
     *
     * @param colList 代收信息
     * @throws Exception 
     */
    public static void genColList(ColList colList) throws Exception {
        String head = "tb_ColList_Recv";
        String content = generationColList(colList); //生成出口流水文件内容
        String file = getFileName(head);
        writeListFile(file, head, content);
    }

    /**
     * 生成代收流水文件内容
     *
     * @param colList 代收信息
     * @return 代收流水文件内容
     */
    private static String generationColList(ColList colList) {
        StringBuilder sbf = new StringBuilder(LaneListConstant.STRING_BUFFER_SIZE);
        sbf.append(parseNullInteger(colList.getRecordNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(colList.getListNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getSquadNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(colList.getSquadDate())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(colList.getSquad())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(colList.getOperatorName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(colList.getOperatorNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getCollMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getPassMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(colList.getChargeTime())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getVerNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getCarType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getTructType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(colList.getInStationNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(colList.getInStationName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getAxleWeight())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getAxleWeightLimit())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(colList.getLaneNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(colList.getStationNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(colList.getStationName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getLaneType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getVehCnt())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getSpEvent())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getBackUp1())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(colList.getBackUp2())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(colList.getBackUp3())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(colList.getBackUp4()));
        return sbf.toString();
    }

    /**
     * 出口最终文件
     *
     * @param head 文件头
     * @param content 文件内容
     * @throws Exception 
     */
    public static void genExListFinallyFile(String head, String content) throws Exception {
        String file = getFileName(LaneListConstant.TB_LANEEXLIST);
        writeListFile(file, head, content);
    }

//    /**
//     *  合并流水文件
//     * @param tbName 需要合并的表名
//     * @throws IOException 
//     */
//	public synchronized static void mergeListFile(String tbName) {
//		Map<String, List<String>> tempFiles = FileUtils.getFiles(getTempPath());
//		List<String> tempFileNames = tempFiles.get("file");
//		if (tempFileNames.isEmpty()) {
//			for (String tempFilePath : tempFileNames) {
//				File tempFile = new File(tempFilePath);
//				if (tempFile.getName().startsWith(tbName)) {
//					int i = 0;
//					while (true) {
//						try {
//							i++;
//							if (i > 5) {
//								break;
//							}
//							String content = FileUtils.readFileToString(tempFile);
//							String file = getFileName(tbName);
//							writeListFile(file, tbName, content);
//							FileUtils.deleteFile(tempFilePath);
//							break;
//						} catch (IOException ex) {
//							logger.error("将临时文件" + tempFilePath
//									+ "流水数据写入最终文件失败，异常原因:" + ex.getMessage(),
//									ex);
//							continue;
//						} finally {
//							try {
//								Thread.sleep(1000);// 休眠1秒
//							} catch (InterruptedException e) {
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//    /**
//     * 出口临时文件
//     *
//     * @param head 文件头
//     * @param content 文件内容
//     */
//    public static String genExListTempFile(String head,String content) {
//        String file = getTempFileName(head+"_"+DateUtils.formatDateToString(
//                DateUtils.getCurrentDate(), "yyyyMMddHHmmssSSS"));
//        FileUtils.write(file, LaneListConstant.TB_LANEEXLIST, true); // 添加文件头
//        FileUtils.write(file, content, true); // 添加文件内容
//        return file;
//    }
    /**
     * 生成车道工班
     *
     * @param lane 车道工班信息
     * @throws Exception 
     */
    public static String generationLaneShiftList(LaneShift lane) throws Exception {
        String head
                = "tb_LaneShift";
        String content = generationLaneShift(lane);
//        String file = "c:\\" + LaneListConstant.TB_LANESHIFT
//                + LaneListConstant.POINT + LaneListConstant.FILE_POSTFIX_TXT;
        String file = getFileName(LaneListConstant.TB_LANESHIFT);
        writeListFile(file, head, content);
        return content;
    }

    /**
     * 生成车道上班日志
     *
     * @param laneLogin 上班日志信息
     * @return
     * @throws Exception 
     */
    public static String generationLaneLoginList(LaneLogin laneLogin) throws Exception {
        String head
                = "tb_LaneLogin";
        String content = generationLaneLogin(laneLogin);
        String file = getFileName("tb_LaneLogin");
        writeListFile(file, head, content);
        return content;
    }

    /**
     * 生成卡箱库存日志
     *
     * @param cbs 卡箱库存
     * @return
     * @throws Exception 
     */
    public static String generationCardBoxStockList(CartInfo cartInfo) throws Exception {
        String head = "tb_CardBoxStock_Recv";
        String content = generationCardBoxStock(cartInfo);
        String file = getFileName("tb_CardBoxStock_Recv");
        writeListFile(file, head, content);
        return null;
    }

    /**
     * 生成车道参数版本表日志
     *
     * @param laneParaVer 车道参数版本
     * @return
     * @throws Exception 
     */
    public static void generationLaneParaVerList(LaneParaVer laneParaVer) throws Exception {
        String head = "tb_LaneParaVer";
        String content = generationLaneParaVer(laneParaVer);
        String file = getFileName("tb_LaneParaVer");
        writeListFile(file, head, content);
    }

    /**
     * 生成报警信息流水
     *
     * @param la 报警信息
     */
    public static void generationLaneAlarmList(LaneAlarm la) throws Exception {
        String head = "tb_LaneAlarm";
        String content = generationLaneAlarm(la);
        String file = getFileName("tb_LaneAlarm");
        writeListFile(file, head, content);
    }
    
    
    /**
     * 生成卡箱操作流动单
     *
     * @param cbol 卡箱操作流动单
     */
    public static void generationCardBoxOpList(CardBoxOpList cbol) throws Exception {
        String head = "tb_CardBoxOpList";
        String content = generationCardBoxOp(cbol);
        String file = getFileName("tb_CardBoxOpList");
        writeListFile(file, head, content);
    }
    
    /**
     * 产生一条卡箱操作流动单
     *
     * @param cbol 卡箱操作流动单信息
     * @return
     */
    private static String generationCardBoxOp(CardBoxOpList cbol) {
        StringBuffer sb = new StringBuffer(LaneListConstant.STRING_BUFFER_SIZE);
        sb.append(parseNullString(cbol.getCardBoxLabelNo())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(cbol.getCardBoxNo())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(cbol.getCardBoxCycleTimes())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(cbol.getCardNum())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(cbol.getSenderNo())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(cbol.getReceiverNo())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(cbol.getRecRoadId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(cbol.getRecStationId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(cbol.getRecLaneNo())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullDate(cbol.getOpTime())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(cbol.getOpType())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(cbol.getStatus())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(cbol.getSpare1())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(cbol.getSpare2())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(cbol.getSpare3())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(cbol.getSpare4()));
        return sb.toString();
    }

    /**
     * 产生一条报警信息
     *
     * @param la 报警信息
     * @return
     */
    private static String generationLaneAlarm(LaneAlarm la) {
        StringBuffer sb = new StringBuffer(LaneListConstant.STRING_BUFFER_SIZE);
        sb.append(parseNullShort(la.getRoaduniqueId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullShort(la.getLaneAlarmPK().getRoadId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullShort(la.getLaneAlarmPK().getStationId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullShort(la.getLaneType())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(la.getLaneTypeName())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(la.getLaneAlarmPK().getLaneNo())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullShort(la.getLaneId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(la.getDeviceName())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullShort(la.getSerialId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullDate(la.getLaneAlarmPK().getOpTime())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(la.getOperatorNo())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullShort(la.getSquadId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullShort(la.getDeviceStatus())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(la.getOrder())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(la.getDescribe())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(la.getSpare()));
        return sb.toString();
    }

    /**
     * 生成一条车道卡箱库存日志
     *
     * @param cartInfo
     * @return
     */
    private synchronized static String generationCardBoxStock(CartInfo cartInfo) {
        StringBuffer sb = new StringBuffer(LaneListConstant.STRING_BUFFER_SIZE);
        sb.append(parseNullString(cartInfo.getCardSerial())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(cartInfo.getCartId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(cartInfo.getCartCycleNum())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(cartInfo.getCardOrderNum())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullShort(cartInfo.getStatus())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullDate(cartInfo.getCurOpTime())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullShort(cartInfo.getCurOpRoadid())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullShort(cartInfo.getCurOpStationid())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(cartInfo.getCurOpLaneNo())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(cartInfo.getSpare()));//.append(LaneListConstant.TAB_STRING);
        //sb.append(parseNullDate(cartInfo.getOptime()));
//        sb.append(parseNullDate(cartInfo.getOpTime()));
        return sb.toString();
    }

    /**
     * 生成一条上班信息
     *
     * @param laneLogin 上班流水
     * @return
     */
    public synchronized static String generationLaneLogin(LaneLogin laneLogin) {
        StringBuffer sb = new StringBuffer(LaneListConstant.STRING_BUFFER_SIZE);
        sb.append(parseNullShort(laneLogin.getRoadId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullShort(laneLogin.getStationId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(laneLogin.getLane())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullDate(laneLogin.getJobstart())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(laneLogin.getGroupTime())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogin.getGroupId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullDate(laneLogin.getInfoGetTime())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(laneLogin.getStaff())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogin.getLaneType())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogin.getJobNum())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullShort(laneLogin.getViolatInclose())).append(LaneListConstant.TAB_STRING);
        sb.append(1);//insertbz,1表示未上传
        return sb.toString();
    }

    /**
     * 生成车道下班日志
     *
     * @param laneLogout 下班日志信息
     * @return
     * @throws Exception 
     */
    public static String generationLaneLogoutList(LaneLogout laneLogout) throws Exception {
        String head
                = "tb_LaneLogout";
        String content = generationLaneLogout(laneLogout);
        String file = getFileName("tb_LaneLogout");
        writeListFile(file, head, content);
        return content;
    }

    /**
     * 生成一条下班信息
     *
     * @param laneLogout 下班流水
     * @return
     */
    public synchronized static String generationLaneLogout(LaneLogout laneLogout) {
        StringBuffer sb = new StringBuffer(LaneListConstant.STRING_BUFFER_SIZE);
        sb.append(parseNullInteger(laneLogout.getRoadId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getStationId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(laneLogout.getLane())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullDate(laneLogout.getJobStart())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(laneLogout.getGroupTime())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getGroupId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullDate(laneLogout.getInfoGetTime())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getLaneType())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(laneLogout.getStaff())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getJobNum())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullDate(laneLogout.getJobEnd())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getTotalC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getTotalM())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getViolationClose())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getTtC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getReceiptMore())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getReversing())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getWoodTruck())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getViolation())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getWithoutMotif())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getUserInHurry())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getReturnTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getPrecodedTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getServiceOverTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getCantReadTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getUturnC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getOverTimeC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getQuickTimeC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getLostC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getHandInTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getBtC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getBoxUsed())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getCLBoxNum())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getCLQBoxNum())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getInBoxNum())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getInQBoxNum())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getNbtC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getTryTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getUnTypeTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getErTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getNullTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getCheckErTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getErAfWriteTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getErKeyTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getInvaliDateC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getNotInSystemC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getErStationC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getErTravelTimeC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getOverMaxC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getLowMinC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getUnNormalC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getPressTTIDC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getIncompleteTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getLeaveOverTime())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getPayTypeBlack())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getPayTypeOverTime())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getForcePass())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getAdjustTimeC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getStatuErrC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getNotAdjustTimeC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getClassErrC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getPlateErrC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getChangeClassC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getChangeKindC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getBadTTC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getSimuC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getTowedC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getTowingC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getFleetC())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getInsertBZ())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop1count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop1amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop2count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop2amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop3count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop3amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop4count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop4amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop5count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop5amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop6count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop6amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop7count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop7amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop8count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop8amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop9count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop9amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop10count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop10amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop11count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop11amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop12count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop12amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop13count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop13amount())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop14count())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneLogout.getMop14amount()));
        return sb.toString();
    }

    /**
     * 生成一条入口流水信息
     *
     * @param laneEnList 入口流水
     * @return
     */
    private synchronized static String generationLaneEn(LaneEnList laneEnList) {
        StringBuilder sbf = new StringBuilder(LaneListConstant.STRING_BUFFER_SIZE);

        sbf.append(parseNullInteger(laneEnList.getRecordId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getListNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getRoadId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getStationId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getStationName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getLaneNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getLaneId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getLaneType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getVehType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getVehClass())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getVehPlate())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getVehPlateColor())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(laneEnList.getLoginTime())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getSquadId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(laneEnList.getSquadDate())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getOperatorNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getOperatorName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getOpCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(laneEnList.getOpTime())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getTicketType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getPassCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getCardBoxNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getxTCardType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getxTCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getoBUId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getPlateImageNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getVehImageNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getSpEvent())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getCardSelcetCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getOpSelcetCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getVehChargeSelcetCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getVehPassSelcetCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getVehTypeAuto())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getVehPlateAuto())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getPlateAutoColor())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getpSAMCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getProgramVer())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getSpare1())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getSpare2())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getSpare3())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(laneEnList.getSpare4())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(laneEnList.getTransferTag())).append(LaneListConstant.TAB_STRING);
//        sbf.append(parseNullInteger(laneEnList.getVerifyCode()));
        sbf.append(CRCUtils.getCrc(laneEnList));
//		sbf.append(LaneListConstant.WRAP_STRING);
        return sbf.toString();
    }

    /**
     * 生成一条出口流水信息
     *
     * @param LaneExList 出口流水
     * @return
     */
    private synchronized static String generationLaneEx(LaneExList lane) {
        StringBuilder sbf = new StringBuilder(LaneListConstant.STRING_BUFFER_SIZE);

        sbf.append(parseNullInteger(lane.getRecordId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getListNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getEnListNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnRoadId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnStationId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getEnStationName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getEnLaneNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnLaneId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnLaneType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnVehType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnVehClass())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getEnVehPlate())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnVehPlateColor())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnSquadId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getEnOperatorNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(lane.getEnOpTime())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getRoadId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getStationId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getStationName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getLaneNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getLaneId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getLaneType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getVehType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getVehClass())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getVehPlate())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getVehPlateColor())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(lane.getLoginTime())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getSquadId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(lane.getSquadDate())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOperatorNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOperatorName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOpCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(lane.getOpTime())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getTicketType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getChargeType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getComputerMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getPayCardType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getPassCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getCardBoxNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getuPCardType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getuPCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullLong(lane.getuPCardBalanceBf())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullLong(lane.getuPCardBalanceAf())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getuPCardDiscount())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getuPCardMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getoBUId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getxTCardType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getxTCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullLong(lane.getxTCardBalanceBf())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullLong(lane.getxTCardBalanceAf())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getxTCardDiscount())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getxTCardMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getpDiscountToll())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getCashMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getFreeMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getOfficeMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getUnpayMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getGetMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getWeightFlag())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getAxisNum())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getAxisGroupNum())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getAxisType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getPreAxisType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getTotalWeight())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getPreTotalWeight())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getAxisWeightDetail())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getTotalWeightForCharge())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getTotalWeightLimit())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getOverWeight())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDouble(lane.getOverRatio())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getPreVehType())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getPreVehMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getPlateImageNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getVehImageNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getSpEvent())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getCardSelcetCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOpSelcetCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getVehChargeSelcetCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getVehPassSelcetCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getInvoiceId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getSectionPath())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getSectionCharge())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getVehTypeAuto())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getVehPlateAuto())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getPlateAutoColor())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getpSAMCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getProgramVer())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getPriceVer())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOfficialCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getFreeRoadIdSer())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.geteTCTradeNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.geteTCTac())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.geteTCTermTradNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.geteTCTermCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getSpare1())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getSpare2())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getSpare3())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getSpare4())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getTransferTag())).append(LaneListConstant.TAB_STRING);
//        sbf.append(parseNullInteger(lane.getVerifyCode()));
        sbf.append(CRCUtils.getCrc(lane));
//		sbf.append(LaneListConstant.WRAP_STRING);
        return sbf.toString();
    }

    /**
     * 生成一条车道工班信息
     *
     * @param lane 车道工班信息
     * @return
     */
    private static String generationLaneShift(LaneShift lane) {
        StringBuffer sbf = new StringBuffer(LaneListConstant.STRING_BUFFER_SIZE);
        sbf.append(parseNullInteger(lane.getLaneShiftNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getRoadId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getStationId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getLaneNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getLaneId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOperatorId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOperatorName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getOpCardNo())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(lane.getSquadDate())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getSquadId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(lane.getLoginTime())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullDate(lane.getLogoutTime())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEnExFlag())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getInvStartId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getInvEndId())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getInvPrintCnt())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getBadInVoiceCnt())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getPassCardUseCnt())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getXtCardUseCnt())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getFreeVehCnt())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getNoCardVehCnt())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getVehCnt())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getListCnt())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getUpCardMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getXtCardMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getCashMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getTotalMoney())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getEndShiftFlag())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getListName())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getVerifyCode())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getSpare1())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullInteger(lane.getSpare2())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getSpare3())).append(LaneListConstant.TAB_STRING);
        sbf.append(parseNullString(lane.getSpare4()));
//		sbf.append(LaneListConstant.WRAP_STRING);
        return sbf.toString();
    }

    /**
     * 生成一条车道卡箱库存日志
     *
     * @param laneParaVer
     * @return
     */
    private synchronized static String generationLaneParaVer(LaneParaVer laneParaVer) {
        StringBuffer sb = new StringBuffer(LaneListConstant.STRING_BUFFER_SIZE);
        sb.append(parseNullString(laneParaVer.getId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneParaVer.getRoadUniqueId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneParaVer.getRoadId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneParaVer.getStationId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(laneParaVer.getLaneNo())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullInteger(laneParaVer.getLaneId())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(laneParaVer.getLaneIp())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(laneParaVer.getParaTableName())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(laneParaVer.getPresentVer())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullDate(laneParaVer.getPresentStartTime())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullString(laneParaVer.getFutureVer())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullDate(laneParaVer.getFutureStartTime())).append(LaneListConstant.TAB_STRING);
        sb.append(parseNullDate(laneParaVer.getGenerateDate()));
        return sb.toString();
    }

    /**
     * 将数字转换成为字符串
     *
     * @param s 数字
     * @return 如果没有则返回""，有则返回对应的字符表示
     */
    private static String parseNullInteger(Integer s) {
        return s == null ? LaneListConstant.EMPTY_STRING : String.valueOf(s);
    }

    /**
     * 将数字转换成为字符串
     *
     * @param s 数字
     * @return 如果没有则返回""，有则返回对应的字符表示
     */
    private static String parseNullLong(Long s) {
        return s == null ? LaneListConstant.EMPTY_STRING : String.valueOf(s);
    }

    /**
     * 将Date类型转换成为字符串
     *
     * @param s 日期
     * @return 如果没有则返回""，有则返回对应的字符表示
     */
    private static String parseNullDate(Date s) {
        String tempDate = null;
        if (s != null) {
            if ("0".equals(FileUtils.getDataEncodeMode())) { // 湖南模式
                tempDate = DateUtils.formatDateToString(s,
                        DateConstant.DATE_FORMAT_YYYYMMDDHHMMSS);
            } else {
                tempDate = DateUtils.formatDateToString(s,
                        DateConstant.DATE_FORMAT_YYYMMDD_SPACE_HHMMSS);
            }
        }
        return s == null ? LaneListConstant.EMPTY_STRING : tempDate;
    }

    /**
     * 将Double类型转换成为字符串
     *
     * @param s 日期
     * @return 如果没有则返回""，有则返回对应的字符表示
     */
    private static String parseNullDouble(Double s) {
        return s == null ? LaneListConstant.EMPTY_STRING : String.valueOf(s);
    }

    /**
     * 将Short类型转换成为字符串
     *
     * @param s
     * @return如果没有则返回""，有则返回对应的字符表示
     */
    private static String parseNullShort(Short s) {
        return s == null ? LaneListConstant.EMPTY_STRING : String.valueOf(s);
    }

    /**
     * 将Null字符串转换成为""字符串
     *
     * @param s 日期
     * @return 如果没有则返回""，有则返回对应的字符表示
     */
    private static String parseNullString(String s) {
        return s == null ? LaneListConstant.EMPTY_STRING : s;
    }

    public static void main(String[] args) throws IOException {
////		String string = "123213213\t123213\t\t\t12312321\t\t ";
////		String[] s = string.split("\t");
////		for (String string2 : s) {
////		}
//        LaneExList laneExList = null;
//        for (int i = 0; i < 100; i++) {
//            laneExList = new LaneExList();
//            laneExList.setLaneNo("X" + i);
//            laneExList.setEnStationName("云龙北");
//            LaneListUtils.generationExList(laneExList);
//        }
//        LaneEnList ent = new LaneEnList();
//        ent.setStationId(111);
//        ent.setStationName("中文");
//        LaneListUtils.generationEnList(ent);
////        ent.setStationId(2222);
////        ent.setStationName("中文");
////        LaneListUtils.generationEnList(ent);
//        //checkTempFile();
    }

    /**
     * 写文件的方法
     *
     * @param file
     * @param head
     * @param content
     * @throws Exception 
     */
    private static void writeListFile(String file, String head, String content) throws Exception {
        if (FileUtils.isExist(file)) {
            FileUtils.write(file, true, true, content); // 文件存在，只写文件内容
        } else {
            FileUtils.write(file, true, true, head, content); // 文件不存在，则需要文件头和文件内容都要写入
        }

        createBackUpFile(file, head, content);
    }

	/**
	 * 创建备份文件
	 * @param file 文件
	 * @param head 文件头
	 * @param content 文件内容
	 * @throws Exception 
	 */
	private static void createBackUpFile(String file, String head,
			String content) throws Exception {
		File tempFile = new File(file);
		String fileName = tempFile.getName();
		 if ("1".equals(FileUtils.getDataEncodeMode())) { //广东模式的流水备份
			 fileName = fileName.substring(0, fileName.indexOf("."));
		     fileName = getBackupPath()
		                + fileName
		                + "_"
		                + DateUtils.formatDateToString(DateUtils.getCurrentDate(),
		                        "yyyyMMdd") + LaneListConstant.POINT
		                + LaneListConstant.FILE_POSTFIX_TXT;
		 }else{
			 fileName = getBackupPath()
		                + fileName;
		 }
       
        if (FileUtils.isExist(fileName)) {
            FileUtils.write(fileName, true, true, content); // 文件存在，只写文件内容
        } else {
            FileUtils.write(fileName, true, true, head, content); // 文件不存在，则需要文件头和文件内容都要写入
        }
	}

    /**
     * 获取文件存储路径
     *
     * @param name 文件名
     * @return
     */
    private static String getFileName(String name) throws IOException {
        String filename = getPath() + name;
        StringBuffer sbf = new StringBuffer(filename);
        if ("0".equals(FileUtils.getDataEncodeMode())) { //湖南模式功能
        	sbf.append("_").append(DateUtils.formatDateToString(DateUtils.getCurrentDate(), "yyyyMMdd"));
        }
        sbf.append(LaneListConstant.POINT ).append(LaneListConstant.FILE_POSTFIX_TXT);
        return sbf.toString();
    }

    /**
     * 获取文件存储路径 读取配置文件的lane.list.filePath，如果没有，将文件放在工程根目录下的list文件夹下
     *
     * @return
     */
    private static String getPath() throws IOException {
        String parentPath = PropertiesUtils.getKey("lane.list.filePath");
        if (StringUtils.isEmpty(parentPath)) {
            parentPath = FileUtils.getRootPath() + "/list/";
        }
        return parentPath;
    }

    /**
     * 获取备份路径
     *
     * @return
     */
    private static String getBackupPath() throws IOException {
        String parentPath = PropertiesUtils.getKey("lane.list.filePath");
        if (StringUtils.isEmpty(parentPath)) {
            parentPath = FileUtils.getRootPath() + "/list/BackUp/";
        }
        return parentPath;
    }

    /**
     * 生成车道版本文件
     */
    public static void genLaneParaVer() {
        try {
            //生成参数版本流水
            LaneParaVer laneParaVer = null;
            Map<String, Version> currentVersion = ParamCache.getCurrentVersionMap();
            String roadId = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "roadId", "000");
            String stationId = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "stationId", "000");
            String laneNo = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "laneNo", "000");
            LaneParam lane = null;
            Vector<LaneParaVer> runLaneParaVers = ParamCache.getRunLaneParaVers();
            runLaneParaVers.clear(); //清除之前的历史运行参数
            //打印当前版本
            for (Entry<String, Version> entry : currentVersion.entrySet()) {
                laneParaVer = new LaneParaVer();
                laneParaVer.setId(UUID.randomUUID().toString());
                lane = ParamCacheQuery.queryLane(laneNo, IntegerUtils.parseInteger(stationId), IntegerUtils.parseInteger(roadId));
                laneParaVer.setRoadUniqueId(0);
                laneParaVer.setRoadId(IntegerUtils.parseInteger(roadId));
                laneParaVer.setStationId(IntegerUtils.parseInteger(stationId));
                laneParaVer.setLaneNo(laneNo);
                laneParaVer.setLaneId(lane == null ? 0 : lane.getLaneId());
                laneParaVer.setLaneIp(lane == null ? null : lane.getLaneIp());
                laneParaVer.setParaTableName(entry.getKey());
                laneParaVer.setPresentVer(entry.getValue().getVersion());
                laneParaVer.setPresentStartTime(entry.getValue().getStartDate());
                laneParaVer.setGenerateDate(DateUtils.getCurrentDate());
//                laneParaVer.setFutureVer(futureVersion == null ? null : futureVersion.getVersion());
//                laneParaVer.setFutureStartTime(futureVersion == null ? null : futureVersion.getStartDate());
                runLaneParaVers.add(laneParaVer);
                LaneListUtils.generationLaneParaVerList(laneParaVer);
            }
//            //将内存中的当前版本，放入到显示版本号的版本
            ParamVersionUtils.replaceDisplayVersion(
                    currentVersion,
                    ParamCache.getDisplayVersionMap());
            currentVersion.clear(); //打印完后，则需要清空版本表

        } catch (Exception e) {
            logger.error("生成参数版本流水异常，错误原因：" + e.getMessage(), e);
        }
    }

    /**
     * 生成车道未来版本文件
     */
    public static void genLaneParaFutureVer() {
        try {
            //生成参数版本流水
            LaneParaVer laneParaVer = null;
            Map<String, Version> futureVersion = ParamCache.getFutureVersionMap();
            Version displayVersion = null; //当前启用版本
            //打印当前版本
            for (Entry<String, Version> entry : futureVersion.entrySet()) {
                displayVersion = ParamCache.getDisplayVersionMap().get(entry.getKey());
                if (displayVersion != null) {//判断是否产生过版本数据
                    laneParaVer = findLaneParaVer(entry);
                    if (laneParaVer != null) {//判断是否需要产生未来版本数据
                        laneParaVer.setId(UUID.randomUUID().toString());
                        laneParaVer.setGenerateDate(DateUtils.getCurrentDate());
                        laneParaVer.setFutureVer(entry.getValue().getVersion());
                        laneParaVer.setFutureStartTime(entry.getValue().getStartDate());
                        LaneListUtils.generationLaneParaVerList(laneParaVer);
                    }
                }
            }
            ParamCache.getFutureVersionMap().clear(); //打印参数版本完成后，清除掉未来版本的缓存信息。
        } catch (Exception e) {
            logger.error("生成参数未来版本流水异常，错误原因：" + e.getMessage(), e);
        }
    }

    /**
     * 查找运行参数版本，生成参数版本表
     *
     * @param entry
     * @return
     */
    public static LaneParaVer findLaneParaVer(Entry<String, Version> entry) {
        LaneParaVer tempLaneParaVer = null;
        Vector<LaneParaVer> runLaneParaVer = ParamCache.getRunLaneParaVers();
        for (LaneParaVer laneParaVer : runLaneParaVer) {
            if (laneParaVer.getParaTableName().equalsIgnoreCase(entry.getKey())) {
                if (!entry.getValue().getVersion().equalsIgnoreCase(laneParaVer.getFutureVer())) {
                    tempLaneParaVer = laneParaVer;
                    break;
                }
            }

        }
        return tempLaneParaVer;
    }

}