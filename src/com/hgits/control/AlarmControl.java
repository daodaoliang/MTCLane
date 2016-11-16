package com.hgits.control;

import com.hgits.common.log.MTCLog;
import com.hgits.util.LaneListUtils;
import com.hgits.util.ShortUtils;
import com.hgits.vo.AlarmCode;
import com.hgits.vo.Lane;
import com.hgits.vo.LaneAlarm;
import com.hgits.vo.LaneAlarmPK;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于管理车道向监控室报警信息
 *
 * @author Wang Guodong
 */
public class AlarmControl {

    private static final Map<String, AlarmCode> alarmCodeMap = new HashMap();

    static {
        initAlarmCode();
    }

    /**
     * 生成车道报警信息
     *
     * @param lane 车道
     * @param staffId 员工号
     * @param groupId 班次
     * @param serial 报警序号
     * @param deviceStatus 设备状态 1异常发生 0异常消失
     */
    public static void generateAlarmInfo(Lane lane, String staffId, short groupId, String serial, short deviceStatus) {
        try {
            AlarmCode ac = alarmCodeMap.get(serial);
            if (ac == null) {
                MTCLog.log("未知报警序号：" + serial);
                return;
            }
            LaneAlarm la = new LaneAlarm();
            Date date = new Date();
            LaneAlarmPK pk = new LaneAlarmPK();
            pk.setOpTime(date);
            pk.setRoadId(ShortUtils.parseString(lane.getRoadId()));
            pk.setStationId(ShortUtils.parseString(lane.getStationId()));
            pk.setLaneNo(lane.getLaneId());
            la.setLaneAlarmPK(pk);
            la.setDescribe(ac.getDescribe());
            la.setDeviceName(ac.getDeviceName());
            la.setDeviceStatus(deviceStatus);
            la.setLaneId(ShortUtils.parseString(lane.getLaneId().substring(1, 3)));
            la.setLaneType((short) lane.getLaneType());
            la.setLaneTypeName(ac.getLaneTypeName());
            la.setOperatorNo(staffId);
            la.setOrder(ac.getOrder());
            la.setRoaduniqueId((short) 0);
            la.setSerialId(ac.getSerialId());
            la.setSpare(null);
            la.setSquadId(groupId);
            LaneListUtils.generationLaneAlarmList(la);
            MTCLog.log("产生报警流水:" + la);
        } catch (Exception ex) {
            MTCLog.log("产生报警信息时出现异常" + lane + "-" + staffId + "-" + groupId + "-" + serial + "-" + deviceStatus, ex);
        }
    }

    /**
     * 初始化报警信息
     */
    private static void initAlarmCode() {
        String str = "resource/tb_alarmcode.txt";
        MTCLog.log("加载" + str);
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(str);
            isr = new InputStreamReader(fis, "utf-8");
            br = new BufferedReader(isr);
            br.readLine();
            AlarmCode ac;
            while ((str = br.readLine()) != null) {
                String[] buffer = str.split(",");
                String serial = buffer[0];
                String laneType = buffer[1];
                String laneName = buffer[2];
                String deviceName = buffer[3];
                String code = buffer[4];
                String describe = buffer[5];
                ac = new AlarmCode();
                ac.setDescribe(describe);
                ac.setDeviceName(deviceName);
                ac.setLaneType(ShortUtils.parseString(laneType));
                ac.setLaneTypeName(laneName);
                ac.setOrder(code);
                ac.setSerialId(ShortUtils.parseString(serial));
                alarmCodeMap.put(serial, ac);
            }
        } catch (Exception ex) {
            MTCLog.log("加载resource/tb_alarmcode.txt异常", ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                MTCLog.log("关闭resource/tb_alarmcode.txt读取流异常", ex);
            }

        }
    }

    /**
     * 获取报警序号所对应的设备+报警描述信息
     * @param serial 报警序号
     * @return 若能获取报警描述信息，则显示报警描述信息，否则返回报警序号
     */
    public static String getDesc(String serial){
        if(alarmCodeMap==null||alarmCodeMap.isEmpty()){
            return serial;//报警序号无记录，直接返回报警序号
        }
        AlarmCode ac = alarmCodeMap.get(serial);
        if(ac==null){
            return serial;
        }
        return ac.getDeviceName()+ac.getDescribe();
    }
}
