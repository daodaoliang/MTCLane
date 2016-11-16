package com.hgits.control;

import com.hgits.hardware.Keyboard;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import ui.ExtJFrame;

/**
 * 代收控制类
 *
 * @author Wang Guodong
 */
public class CollectControl implements Runnable {

    private final ExtJFrame extJFrame;
    private final Keyboard keyboard;
    private List<String> collectBuffer = new ArrayList<String>();
    private static CollectControl instance;//唯一实例化对象

    /**
     * 获取唯一实例化对象
     *
     * @param extJFrame
     * @param keyboard
     * @return 唯一实例化对象
     */
    public static synchronized CollectControl getSingleInstance(ExtJFrame extJFrame, Keyboard keyboard) {
        if (instance == null) {
            instance = new CollectControl(extJFrame, keyboard);
        }
        return instance;
    }

    private CollectControl(ExtJFrame extJFrame, Keyboard keyboard) {
        this.extJFrame = extJFrame;
        this.keyboard = keyboard;
    }

    @Override
    public void run() {
        String collectMoney = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_CONSTANT, "collectMoney", null);
        if (collectMoney == null) {
            return;
        }else if(collectMoney.trim().isEmpty()){
            return;
        }
        collectMoney = StringUtils.replace(collectMoney, "，", ",");//中文逗号更换为英文逗号
        String[] buffer = StringUtils.split(collectMoney, ",");
        List<String> list = new ArrayList();
        for (String str : buffer) {
            if (str == null) {
                continue;
            }
            String temp = str.trim();
            if (temp.matches("[0-9]{1,6}")) {//检验代收金额是否符合要求
                list.add(temp);
            }
        }
        int len = list.size();
        if(len>10){//最多接收10项代收金额配置
            for(int i=len-1;i>=10;i--){
                list.remove(i);
            }
        }
        collectBuffer.addAll(list);
        extJFrame.initCollectPanel(list);
        
    }

    /**
     * 获取代收金额
     *
     * @return 代收金额
     */
    public int getCollectMoney() {
        if (!FunctionControl.isCollActive()) {//未启用代收功能
            return 0;
        }
        if (collectBuffer == null || collectBuffer.isEmpty()) {//无代收金额配置
            return 0;
        }
        int len = collectBuffer.size();
        StringBuilder regex = new StringBuilder();
        regex.append("[0-");
        if(len>9){
            regex.append(9);
        }else{
            regex.append(len);
        }
        regex.append("]");
        extJFrame.showCollectionPanel();//显示代收面板
        extJFrame.updateInfo("选择代收通行费", "请按数字键选择对应代收通行费并确认");
        String str;
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            str = keyboard.getMessage();
            if (str == null) {
                continue;
            } else if (Keyboard.CONFIRM.equals(str)) {
                break;
            } else if (str.matches(regex.toString())) {
                extJFrame.updateCollectButtonNew(str);
            } else {
                keyboard.sendAlert();
            }
        }
        extJFrame.hideCentralPanel();
        int result = extJFrame.getCollectResultNew();
        if (result == 0) {
            extJFrame.updateAttachInfo(null);
        } else {
            extJFrame.updateAttachInfo("含代收:" + result + "元");
        }
        return result;
    }
}
