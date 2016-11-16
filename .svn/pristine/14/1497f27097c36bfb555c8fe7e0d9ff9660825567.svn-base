package com.hgits.service;

import com.hgits.control.LogControl;
import com.hgits.hardware.Keyboard;
import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.util.rate.ParamCacheQuery;
import com.hgits.vo.Constant;
import com.hgits.vo.Version;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import ui.ExtJFrame;
import ui.ParaJFrame;

/**
 * 营运参数查看服务类
 *
 * @author Wang Guodong
 */
public class ParamService {

    private final Keyboard keyboard;
    private final ExtJFrame extJFrame;

    public ParamService(Keyboard keyboard, ExtJFrame extJFrame) {
        this.keyboard = keyboard;
        this.extJFrame = extJFrame;
    }

    /**
     * 显示运营参数
     */
    public void showParamVersion() {
        LogControl.logInfo("显示运营参数");
        Map<String, Version> map = ParamCacheQuery.queryParamVersion();
        Set<String> set = map.keySet();
        List<String> keyList = new ArrayList(set);
        Collections.sort(keyList);
        List<String> list = new ArrayList();
        Map<String, String> paramNameMap = getParamNameMap();
        for (String key : keyList) {
            StringBuilder sb = new StringBuilder();
            Version version = map.get(key);
            String ver = version.getVersion();
            Date date = version.getStartDate();
            if(ver==null||ver.trim().isEmpty()||date==null){
                continue;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String name = paramNameMap.get(key.toUpperCase());
            if (name == null) {
                continue;
            }
            sb.append(name);
            sb.append(",").append(ver);
            sb.append(",").append(sdf.format(date));
            list.add(sb.toString());
        }
        ParaJFrame paraJFrame = ParaJFrame.getSingleInstance();
        paraJFrame.setVisible(true);
        paraJFrame.updateParamTable(list);
        extJFrame.setAlwaysOnTop(false);
        extJFrame.updateInfo("运营参数", "运营参数");
        extJFrame.requestFocus();
        String str;
        while (true) {
            str = keyboard.getMessage();
            if (Keyboard.CANCEL.equals(str)) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
        paraJFrame.dispose();
        extJFrame.showOnTop();
        LogControl.logInfo("取消显示运营参数");
    }

    /**
     * 获取参数表名称集合
     *
     * @return 参数表名称集合
     */
    private Map<String, String> getParamNameMap() {
        Map<String, String> map = new HashMap();
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE, "paramCnt", "0");
        int cnt = IntegerUtils.parseString(str);
        for (int i = 1; i <= cnt; i++) {
            String key = "param" + i;
            String value = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE, key, null);
            if (value == null || value.trim().isEmpty()) {
                continue;
            }
            value = StringUtils.replace(value, "；", ";");
            String[] buffer = StringUtils.split(value, ";");
            map.put(buffer[0].toUpperCase(), buffer[1]);
        }
        return map;
    }

}
