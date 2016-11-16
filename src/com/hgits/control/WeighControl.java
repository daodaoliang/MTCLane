package com.hgits.control;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import ui.ExtJFrame;

import com.hgits.hardware.Keyboard;
import com.hgits.hardware.WeighSystem;
import com.hgits.service.MainService;
import com.hgits.util.AxleUtils;
import com.hgits.util.DoubleUtils;
import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Axle;
import com.hgits.vo.AxleGroup;
import com.hgits.vo.Constant;
import com.hgits.vo.VehInfoConfirm;

/**
 * 计重控制
 *
 * @author 王国栋
 */
public class WeighControl {

    WeighSystem weighSystem;
    ExtJFrame extJFrame;
    List<Map<Integer, AxleGroup>> vehList = Collections.synchronizedList(new ArrayList());//当前所有车辆的轴组集合
    List<List<Axle>> axleList = Collections.synchronizedList(new ArrayList());//当前车辆的轴集合
    Keyboard keyboard;//收费键盘
    private double weight;//第一辆车的检测重量
    private double limitWeight;//第一辆车的限重
    private int axleCount;//第一辆车的轴数
    private int axleGroupCount;//第一辆车的轴组数
    private String axleGroupStr;//第一辆车的轴组类型集合字符串
    private String AxisWeightDetail;//第一辆车的单轴轴重集合字符串
    List<String> speedList = Collections.synchronizedList(new ArrayList());//车辆通过称重设备的速度的集合
    MainService mainSvc;
    boolean confirmFlag = false;//等待监控室确认标识，监控室确认过程中缓存中的车辆信息不可变化
    private boolean modifyFlag;//车辆修改标志(含倒车)
    private boolean updateFlag;//更新轴组显示
    private LaneServerControl lc;//与监控室连接管控
    private final int maxVehCnt = 18;//缓存中记录的车辆最大数
    private boolean longCar; //长车标识
    private final Object obj = new Object();

    public boolean isLongCar() {
        return longCar;
    }

    public void setLongCar(boolean longCar) {
        this.longCar = longCar;
    }

    public void setLc(LaneServerControl lc) {
        this.lc = lc;
    }

    public void setMainSvc(MainService mainSvc) {
        this.mainSvc = mainSvc;
    }

    public int getAxleCount() {
        return axleCount;
    }

    public int getAxleGroupCount() {
        return axleGroupCount;
    }

    public double getWeight() {
        return DoubleUtils.div(weight, 100, 2);
    }

    public double getLimitWeight() {
        return DoubleUtils.div(limitWeight, 100, 1);
    }

    public boolean isModifyFlag() {
        return modifyFlag;
    }

    public String getAxleGroupStr() {
        return axleGroupStr;
    }

    public String getAxisWeightDetail() {
        return AxisWeightDetail;
    }

    public void setModifyFlag(boolean ModifyFlag) {
        this.modifyFlag = ModifyFlag;
    }

    /**
     * 获取第一辆车通过称重设备的速度
     *
     * @return 速度
     */
    public String getSpeed() {
        if (!speedList.isEmpty()) {
            return speedList.get(0);
        } else {
            return null;
        }

    }

    public WeighControl(final WeighSystem weighSystem, ExtJFrame extJFrame, Keyboard keyboard) {
        this.keyboard = keyboard;
        this.weighSystem = weighSystem;
        this.extJFrame = extJFrame;
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(200);

                        if (weighSystem.isReverseFlag() && vehList.size() > 0) {    //倒车
                            longCar = false; //重置长车标识
                            LogControl.logInfo("倒车");
//                            FlowControl.chargeObserCode = "D";
                            synchronized (obj) {
                                if (vehList.size() > 0) {
                                    vehList.remove(vehList.size() - 1);
                                }
                                if (axleList.size() > 0) {
                                    axleList.remove(axleList.size() - 1);
                                }
                                if (speedList.size() > 0) {
                                    speedList.remove(speedList.size() - 1);
                                }
                            }
                            weighSystem.setReverseFlag(false);
                            updateVehAxle();
                            modifyFlag = true;
                        }

                        int vehCnt = weighSystem.getCarNos();//获取计重设备中当前的车辆数量
                        if (vehCnt > 0) {//当前车辆数大于0
                            LogControl.logInfo("计重设备发现车辆信息，当前有" + vehCnt + "辆车待处理");
                            updateVehAxle();
                        }
//                        if (!weighSystem.getVehList().isEmpty()) {              //有车辆从计重系统上经过
//                            LogControl.logInfo("车辆经过计重设备，轴组更新");
//                            updateVehAxle();
//                        }
//                        if (!weighSystem.getSingleAxleInfo().isEmpty()) {       //有车辆从计重系统上经过
//                            LogControl.logInfo("车辆经过计重设备，轴更新");
//                            updateVehAxle();
//                        }
//                        if (!weighSystem.getSpeedList().isEmpty()) {            //有车辆从计重系统上经过
//                            LogControl.logInfo("车辆经过计重设备，速度更新");
//                            updateVehAxle();
//                        }
                        if (updateFlag) {
                            LogControl.logInfo("更新轴组显示");
                            updateVehAxle();
                            updateFlag = false;
                        }
                    } catch (Throwable ex) {
                        LogControl.logInfo("计重设备监控异常", ex);
                    }
                }
            }
        });
        //检测缓存中记录的车辆数量是否超过了最大车辆数
        ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);//每秒钟检测一次
                    } catch (InterruptedException ex) {
                    }
                    if (vehList.size() > maxVehCnt && axleList.size() > maxVehCnt && speedList.size() > maxVehCnt) {
                        updateAfterFirstVehLeft();
                        updateVehAxle();
                    }
                }
            }
        });
    }

    /**
     * 更新车辆轴信息并显示
     *
     * @author 王国栋 2014-9-2
     */
//    synchronized void updateVehAxle() {
//        long start = System.currentTimeMillis();
//        while (true) {
//            long now = System.currentTimeMillis();
//            if (now - start > 30 * 1000) {
//                break;
//            }
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException ex) {
//            }
//            if (confirmFlag) {//若正在进行监控室确认环节，暂时不进行车辆集合变化的更新
//                continue;
//            } else {
//                break;
//            }
//        }
//        try {
//            //获取单轴信息
//            List<List<Axle>> tempAxleList = AxleUtils.getAxleListListFromList(weighSystem.getSingleAxleInfo()); //list值传递
//            //获取轴组信息
//            List<Map<Integer, AxleGroup>> tempList = AxleUtils.getAxleGroupMapListFromList(weighSystem.getVehList()); //list值传递
//            //判断从计重设备传过来的车辆轴组信息和轴信息是否一致，若不一致，不会进行记录
//            if (tempAxleList.size() != tempList.size()) {
//                LogControl.logInfo("发现车辆轴集合与轴组集合不一致");
//                LogControl.logInfo("轴集合:" + tempAxleList);
//                LogControl.logInfo("轴组集合:" + tempList);
//                int len = tempAxleList.size();
//                for (int i = 0; i < len; i++) {                     //清除计重仪中的车辆单轴信息
//                    weighSystem.deleteFirstVehAxleInfo();
//                }
//                len = tempList.size();
//                for (int i = 0; i < len; i++) {                         //清除计重仪中的车辆轴组信息
//                    weighSystem.deleteFirstVehCarInfo();
//                }
//                return;
//            } else {
//                tempList = AxleUtils.checkAxleGroupByAxle(tempAxleList, tempList);
//                axleList.addAll(tempAxleList);//记录单轴信息
//                LogControl.logInfo("当前车辆单轴信息：" + axleList);
//                int len = tempAxleList.size();
//                for (int i = 0; i < len; i++) {                     //清除计重仪中的车辆单轴信息
//                    weighSystem.deleteFirstVehAxleInfo();
//                }
//                vehList.addAll(tempList);//记录轴组信息
//                LogControl.logInfo("当前车辆轴组信息：" + vehList);
//                len = tempList.size();
//                for (int i = 0; i < len; i++) {                         //清除计重仪中的车辆轴组信息
//                    weighSystem.deleteFirstVehCarInfo();
//                }
//            }
//            List<String> tempSpeedList = new ArrayList(weighSystem.getSpeedList());         //获取车辆通过称重设备的速度集合
//            if (!tempSpeedList.isEmpty()) {
//                speedList.addAll(tempSpeedList);                                //将车辆通过称重设备的速度加载到内存中
//            }
//            for (int i = 0; i < tempSpeedList.size(); i++) {                    //清除计重仪中的车辆信息
//                weighSystem.deleteFirstVehSpeetInfo();
//            }
//            getFirstVehWeight();                                                    //获取第一辆车的重量信息
//            extJFrame.updateWaitVehCount(vehList.size() + "");
//            extJFrame.initVehAxle();
//            for (int i = 0; i < vehList.size() && i < 3; i++) {                     //获取前三辆车的信息
//                Map<Integer, AxleGroup> tempMap = vehList.get(i);
//                Set set = tempMap.keySet();
//                List keyList = new ArrayList(set);
//                Collections.sort(keyList);
//                List<AxleGroup> axleGroupList = new ArrayList();
//                for (int j = 0; j < keyList.size(); j++) {
//                    Integer num = (Integer) keyList.get(j);
//                    AxleGroup a = (AxleGroup) tempMap.get(num);                     //获取车辆的轴组信息
//                    axleGroupList.add(a);
//                }
//                if (isOverWeight(axleList.get(i), vehList.get(i))) {//超载车辆
//                    extJFrame.updateOWVehAxle(i + 1, axleGroupList);                           //界面更新轴组及轴重信息
//                } else {//未超载车辆
//                    extJFrame.updateVehAxle(i + 1, axleGroupList);                           //界面更新轴组及轴重信息
//                }
//            }
//            if (axleCount != 0 && weight != 0) {
//                double temp = DoubleUtils.div(weight, 100, 2);
//                temp = DoubleUtils.mul(temp, 0.975);
//                temp = DoubleUtils.round(temp, 2, BigDecimal.ROUND_HALF_UP);//计费重量（单位吨）
//                extJFrame.showFirstVehWeight(temp + "", getLimitWeight() + "");
//            } else {
//                extJFrame.showFirstVehWeight(null, null);
//            }
//        } catch (Exception ex) {
//            LogControl.logInfo("更新车轴信息出现异常", ex);
//        }
//        LogControl.logInfo("weight:" + weight);
//        LogControl.logInfo("limitweight:" + limitWeight);
//    }
    synchronized void updateVehAxle() {
        long start = System.currentTimeMillis();
        while (true) {
            long now = System.currentTimeMillis();
            if (now < start) {//当前时间小于开始时间，重新开始计时
                start = now;
            }
            if (now - start > 30 * 1000) {
                break;
            }
            if (confirmFlag) {//若正在进行监控室确认环节，暂时不进行车辆集合变化的更新
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }
                continue;
            } else {
                break;
            }
        }
        try {
            //获取单轴信息
            List<List<Axle>> list1 = AxleUtils.getAxleListListFromList(weighSystem.getSingleAxleInfo()); //list值传递
            //获取轴组信息
            List<Map<Integer, AxleGroup>> list2 = AxleUtils.getAxleGroupMapListFromList(weighSystem.getVehList()); //list值传递
            int vehCnt = weighSystem.getCarNos();
            int axleCnt = list1.size();
            int axleGroupCnt = list2.size();
            if (axleCnt < vehCnt || axleGroupCnt < vehCnt) {//判断车辆数量与轴组轴数量是否异常
                LogControl.logInfo("当前计重设备中记录的车辆缓存数量" + vehCnt + "与车辆的轴组数量" + axleGroupCnt + "，轴数量" + axleCnt + "异常");
                return;
            }
            for (int i = 0; i < vehCnt; i++) {
                weighSystem.carNosDecline();
            }
            List<List<Axle>> tempAxleList = list1.subList(0, vehCnt);//获取计重设备已经处理结束的车辆单轴信息
            List<Map<Integer, AxleGroup>> tempList = list2.subList(0, vehCnt);//获取计重设备已经处理结束的车辆轴组信息
//            tempList = AxleUtils.checkAxleGroupByAxle(tempAxleList, tempList);
            axleList.addAll(tempAxleList);//记录单轴信息
            LogControl.logInfo("当前车辆单轴信息：" + axleList);
            int len = tempAxleList.size();
            for (int i = 0; i < len; i++) {                     //清除计重仪中的车辆单轴信息
                weighSystem.deleteFirstVehAxleInfo();
            }
            vehList.addAll(tempList);//记录轴组信息
            LogControl.logInfo("当前车辆轴组信息：" + vehList);
            len = tempList.size();
            for (int i = 0; i < len; i++) {                         //清除计重仪中的车辆轴组信息
                weighSystem.deleteFirstVehCarInfo();
            }
            List<String> tempSpeedList = new ArrayList(weighSystem.getSpeedList());         //获取车辆通过称重设备的速度集合
            if (!tempSpeedList.isEmpty()) {
                speedList.addAll(tempSpeedList);                                //将车辆通过称重设备的速度加载到内存中
            }
            for (int i = 0; i < tempSpeedList.size(); i++) {                    //清除计重仪中的车辆信息
                weighSystem.deleteFirstVehSpeetInfo();
            }
            getFirstVehWeight();                                                    //获取第一辆车的重量信息
            extJFrame.updateWaitVehCount(vehList.size() + "");
            extJFrame.initVehAxle();
            for (int i = 0; i < vehList.size() && i < 3; i++) {                     //获取前三辆车的信息
                Map<Integer, AxleGroup> tempMap = vehList.get(i);
                Set set = tempMap.keySet();
                List keyList = new ArrayList(set);
                Collections.sort(keyList);
                List<AxleGroup> axleGroupList = new ArrayList();
                for (int j = 0; j < keyList.size(); j++) {
                    Integer num = (Integer) keyList.get(j);
                    AxleGroup a = (AxleGroup) tempMap.get(num);                     //获取车辆的轴组信息
                    axleGroupList.add(a);
                }
                if (isOverWeight(axleList.get(i), vehList.get(i))) {//超载车辆
                    extJFrame.updateOWVehAxle(i + 1, axleGroupList);                           //界面更新轴组及轴重信息
                } else {//未超载车辆
                    extJFrame.updateVehAxle(i + 1, axleGroupList);                           //界面更新轴组及轴重信息
                }
            }
            if (axleCount != 0 && weight != 0) {
                double temp = DoubleUtils.div(weight, 100, 2);
                temp = DoubleUtils.mul(temp, 0.975);
                temp = DoubleUtils.round(temp, 2, BigDecimal.ROUND_HALF_UP);//计费重量（单位吨）
                extJFrame.showFirstVehWeight(temp + "", getLimitWeight() + "");
            } else {
                extJFrame.showFirstVehWeight(null, null);
            }
        } catch (Exception ex) {
            LogControl.logInfo("更新车轴信息出现异常", ex);
        }
        LogControl.logInfo("weight:" + weight);
        LogControl.logInfo("limitweight:" + limitWeight);
    }

    /**
     * 收费员按计重键之后的操作流程
     *
     * @author 王国栋 2014-9-2
     */
    public void runWeighControl() {
        try {
            LogControl.logInfo("计重菜单");
            extJFrame.showWeightPanel();                                            //显示计重菜单
            if (vehList.isEmpty()) {
                extJFrame.unableWeightPanelButtonWhileNoVeh();//无车辆信息时才可添加车辆
            } else {
                extJFrame.enableWeightPanelButton();//有车辆信息时不可添加车辆
            }
            extJFrame.updateInfo("", "选择模拟菜单中的一项\n然后确认");
            String str;
            Integer flag = null;
            outer:
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                str = keyboard.waitForOrder();
                if (str.matches("[1-6]")) {                                         //用户选择计重菜单中的按钮
                    extJFrame.updateWeightPanel(str);                               //根据用户的选择更新计重菜单中的按钮
                } else if ("取消".equals(str)) {
                    extJFrame.hideCentralPanel();
                    return;
                } else if ("确认".equals(str)) {
                    switch (extJFrame.getWeightPanelResult()) {                     //获取用户所选择的计重按钮
                        case '1':
                            flag = modifyVeh();                                     //进入修改车辆信息流程
                            break outer;
                        case '2':
                            flag = modifyAxle();                                    //进入修改轴组类型流程
                            break outer;
                        case '3':
                            flag = addVeh();                                        //进入添加车辆流程
                            break outer;
                        case '4':
                            flag = deleteVeh();                                     //进入删除车辆流程
                            break outer;
                        case '5':
                            flag = towedVeh();                                      //进入拖车流程
                            break outer;
                        case '6':
                            flag = 0;                                                       //长车
                            //设置长车标识
                            longCar = true;
                            LogControl.logInfo("设置长车标识为true");
                            break outer;
                        default:
                            break outer;
                    }
                }
            }
            if (flag == null) {
                runWeighControl();
            } else if (flag == 0) {
                extJFrame.hideCentralPanel();
                updateVehAxle();
                modifyFlag = true;
            } else if (flag == 1) {
                extJFrame.hideCentralPanel();
                extJFrame.updateInfo("计重修改异常", "计重修改异常，请检查计重设备是否正常");
                Thread.sleep(3000);
            }
        } catch (Exception ex) {
            LogControl.logInfo("计重菜单出现异常", ex);
        }
    }

    /**
     * 对应计重菜单中修改车辆流程
     *
     * @author 王国栋 2014-9-2
     * @return null用户取消操作 0 执行成功 1 执行失败
     */
    private Integer modifyVeh() {
        LogControl.logInfo("计重菜单-修改车辆");
        List<AxleGroup> agList = AxleUtils.getAllAxleGroupList(vehList);        //获取当前缓存中所有车辆的所有轴组信息
        extJFrame.showAxleListPanel(agList);
        String str;
        int i = 0;
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            extJFrame.updateInfo("", "请选择当前车辆的最后一个轴组");
            str = keyboard.waitForOrder();
            if (str.matches("[1-9]")) {                                         //用户选择当前车辆的最后一个轴组
                i = IntegerUtils.parseString(str);
                if (i > agList.size()) {
                    i = 0;
                    keyboard.sendAlert();
                } else {
                    extJFrame.updateAxleListPanel(str);
                }
            } else if ("取消".equals(str)) {                                    //用户取消操作
                extJFrame.hideCentralPanel();
                return null;
            } else if ("确认".equals(str)) {
                if (i == 0) {
                    keyboard.sendAlert();
                } else {
                    break;
                }
            } else {
                keyboard.sendAlert();
            }
        }
        LogControl.logInfo("选择当前车辆最后一轴:" + i);
        List<List<AxleGroup>> list = AxleUtils.getAllAxleGroup(vehList);
//        List<List<AxleGroup>> list = AxleUtils.getModifiedVehAxleGroup(vehList, i);//修改车辆信息
        waitForControlRoomConfirmVeh(list, "1", i + "");                         //等待监控室确认
        updateVehAxle();
        FlowControl.chargeObserCode = "Z";
        return 0;
    }

    /**
     * 对应计重菜单中修改轴组信息(此修改仅针对第一辆车)
     *
     * @author 王国栋 2014-9-4
     * @return null用户取消操作 0 执行成功 1 执行失败
     */
    private Integer modifyAxle() {
        LogControl.logInfo("计重菜单-修改轴组信息");
        Map<Integer, AxleGroup> firstVeh = vehList.get(0);
        List<AxleGroup> axleList = new ArrayList();
        for (int i = 0; i < firstVeh.size(); i++) {
            axleList.add(firstVeh.get(i + 1));
        }
        extJFrame.showAxleListPanel(axleList);                                  //显示第一辆车的轴组
        String str;
        int i = 0;
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            extJFrame.updateInfo("", "选择需要修改的轴组");
            str = keyboard.waitForOrder();
            if (str.matches("[1-9]")) {                                         //选择需要修改的轴组
                i = IntegerUtils.parseString(str);
                if (i > axleList.size()) {
                    i = 0;
                    keyboard.sendAlert();
                } else {
                    extJFrame.updateAxleListPanel(str);
                }
            } else if ("取消".equals(str)) {                                    //取消操作
                return null;
            } else if ("确认".equals(str)) {
                if (i == 0) {
                    keyboard.sendAlert();
                } else {
                    break;
                }
            } else {
                keyboard.sendAlert();
            }
        }
        Integer flag = modifyAxleType(i);                                       //针对选择的轴组进行更改
        if (flag == null) {                                                     //取消对选定轴组的修改
            return modifyAxle();                                                //返回第一辆车的轴组显示
        } else if (flag == 0) {                                                 //轴组修改成功
            updateVehAxle();                                                    //更新车辆的轴组显示
            FlowControl.chargeObserCode = "Z";
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * 对应计重菜单中添加车辆信息
     *
     * @author 王国栋 2014-9-2
     * @return null用户取消操作 0 执行成功 1 执行失败
     */
    private Integer addVeh() {
        LogControl.logInfo("计重菜单-添加车辆信息");
        extJFrame.hideCentralPanel();
        List<List<AxleGroup>> list = new ArrayList();
        AxleGroup ag = new AxleGroup();
        ag.setAxleType("03");
        List<AxleGroup> tempList = new ArrayList();
        tempList.add(ag);;
        list.add(tempList);
        waitForControlRoomConfirmVeh(list, "3", "1");          //等待监控室确认
        updateVehAxle();
        FlowControl.chargeObserCode = "Z";
        return 0;
    }

    /**
     * 对应计重菜单删除车辆信息
     *
     * @author 王国栋 2014-9-2
     * @return null用户取消操作 0 执行成功 1 执行失败
     */
    private Integer deleteVeh() {
        LogControl.logInfo("计重菜单-删除车辆信息");
        extJFrame.hideCentralPanel();
        List<List<AxleGroup>> list = AxleUtils.getAllAxleGroup(vehList);
        waitForControlRoomConfirmVeh(list, "4", list.get(0).size() + "");          //等待监控室确认
        updateVehAxle();
        FlowControl.chargeObserCode = "Z";
        return 0;
    }

    /**
     * 对应计重菜单拖车信息
     *
     * @author 王国栋 2014-9-2
     * @return 0 执行成功
     */
    private Integer towedVeh() {
        LogControl.logInfo("计重菜单-拖车");
        List<List<AxleGroup>> list = AxleUtils.getAllAxleGroup(vehList);

        //显示拖车信息，只处理第一辆车的信息
        List<Map<Integer, AxleGroup>> tempVehList = AxleUtils.getAxleGroupMapListFromList(vehList);
        List<AxleGroup> agList = AxleUtils.getAllAxleGroup(tempVehList).get(0);//第一辆车轴组集合
        LogControl.logInfo("选择将要拆分的拖车车辆的信息为:" + agList.toString());

        if (agList.size() < 3) { //如果轴组只有2个，则表示不为拖车
            extJFrame.updateInfo("", "拖车轴组信息不正确");
            keyboard.sendAlert();
            extJFrame.hideCentralPanel();
            LogControl.logInfo("拖车轴组信息不正确");
            return 0;
        }
        if (FunctionControl.isManualTowActive()) {
            extJFrame.showTowedVehAxleListPanel(agList); //显示拖车对应的轴组选择功能
        }
        String str = null;
        int i = 0;
        while (true) {
            if (!FunctionControl.isManualTowActive()) {
                i = 2;//未启用手动拆分拖车功能，自动拆分拖车
                LogControl.logInfo("自动拆分拖车");
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            extJFrame.updateInfo("", "请选择拖车的最后一个轴组");
            str = keyboard.waitForOrder();
            if (str.matches("[1-9]")) {                                         //用户选择当前车辆的最后一个轴组
                i = IntegerUtils.parseString(str);
                if (i > agList.size()) {
                    i = 0;
                    keyboard.sendAlert();
                } else if (i == 1 || i == agList.size()) {//第一个和最后一个轴组不能为拖车的拆分条件。
                    i = 0;
                    keyboard.sendAlert();
                } else {
                    extJFrame.updateAxleListPanel(str);
                }
            } else if ("取消".equals(str)) {                                    //用户取消操作
                extJFrame.hideCentralPanel();
                return null;
            } else if ("确认".equals(str)) {
                if (i == 0) {
                    keyboard.sendAlert();
                } else {
                    //必须确认轴组后，才能按确认键发到监控室确认
//                    boolean flag = waitForControlRoomConfirmVeh(list, "5", String.valueOf(i));
//                    if (flag) {
//                        FlowControl.logout.setTowingC(FlowControl.logout.getTowingC() + 1);
//                        FlowControl.vehObserCode = "B";
//                    }
                    break;
                }
            } else {
                keyboard.sendAlert();
            }
        }
        //必须确认轴组后，才能按确认键发到监控室确认
        boolean flag = waitForControlRoomConfirmVeh(list, "5", String.valueOf(i));
        if (flag) {
            FlowControl.logout.setTowingC(FlowControl.logout.getTowingC() + 1);
            FlowControl.vehObserCode = "B";
            FlowControl.chargeObserCode = "Z";
        }

        return 0;
    }

    /**
     * 根据用户选择的轴组进行修改
     *
     * @author 王国栋 2014-9-4
     * @param num 用户选定的轴组数
     * @return null用户取消操作 0 执行成功 1 执行失败
     */
    private Integer modifyAxleType(int num) {
        if (vehList.isEmpty() || axleList.isEmpty()) {
            LogControl.logInfo("修改轴组时发现车辆轴组信息为空，轴组集合:" + vehList + " 轴集合:" + axleList);
            return 1;
        }
        List<AxleGroup> agList = AxleUtils.getAxleGroupList(vehList.get(0), axleList.get(0), num);
        extJFrame.showAxleTypePanel(agList);//根据收费员选择轴数以及当前车的轴信息显示所能够改成的轴型
        String str;
        int i = 0;
        int len = agList.size();
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            extJFrame.updateInfo("", "选择需要的轴组");
            str = keyboard.waitForOrder();
            if (str.matches("[1-9]")) {                                         //用户选择轴组类型
                i = IntegerUtils.parseString(str);
                if (i > len) {
                    i = 0;
                    keyboard.sendAlert();
                    continue;
                }
                extJFrame.updateAxleTypePanel(str);
            } else if ("确认".equals(str)) {
                i = extJFrame.getAxleTypeChoice();
                if (i == 0) {
                    keyboard.sendAlert();
                } else {
                    break;
                }
            } else if ("取消".equals(str)) {                                    //用户取消操作
                return null;
            } else {
                keyboard.sendAlert();
            }
        }
        LogControl.logInfo("选择将当前车辆的第" + num + "个轴组修改为" + i + "型轴组");
        List<List<AxleGroup>> list = AxleUtils.modifyAxleGroup(vehList, axleList, num, "0" + i);
        waitForControlRoomConfirmVeh(list, "2", num + "");                              //等待监控室确认
        updateVehAxle();
        extJFrame.hideCentralPanel();
        return 0;
    }

    /**
     * 判断车辆是否超重
     *
     * @param list 车辆单轴集合
     * @param veh 车辆轴组集合
     * @return true超重 false不超重
     */
    public boolean isOverWeight(List<Axle> list, Map<Integer, AxleGroup> veh) {
        Iterator ite = veh.keySet().iterator();
        int vehAxleCount = 0;//车辆轴数
        double totalAxleWeight = 0;//车辆根据轴组限重计算出的总轴重
        double dweight = 0;//车辆检测重量
        while (ite.hasNext()) {
            AxleGroup ag = veh.get((Integer) ite.next());
            dweight += ag.getAxleWeight();
            String agType = ag.getAxleType();
            if ("01".equals(agType)) {
                vehAxleCount++;
                totalAxleWeight += 700;
            } else if ("02".equals(agType)) {
                vehAxleCount++;
                totalAxleWeight += 1000;
            } else if ("03".equals(agType)) {
                vehAxleCount = vehAxleCount + 2;
                totalAxleWeight += 1000;
            } else if ("04".equals(agType)) {
                vehAxleCount = vehAxleCount + 2;
                totalAxleWeight += 1400;
            } else if ("05".equals(agType)) {
                vehAxleCount = vehAxleCount + 2;
                totalAxleWeight += 1800;
            } else if ("06".equals(agType)) {
                vehAxleCount = vehAxleCount + 3;
                totalAxleWeight += 1200;
            } else if ("07".equals(agType)) {
                vehAxleCount = vehAxleCount + 3;
                totalAxleWeight += 1600;
            } else if ("08".equals(agType)) {
                vehAxleCount = vehAxleCount + 3;
                totalAxleWeight += 2000;
            } else if ("09".equals(agType)) {
                vehAxleCount = vehAxleCount + 3;
                totalAxleWeight += 2400;
            } else {
                if (agType.matches("[a-fA-F0-9]+")) {//四联轴以上的车轴
                    int i = Integer.parseInt(agType, 16);
                    i = i - 9;
                    totalAxleWeight += 2400 + i * 400;
                    vehAxleCount = vehAxleCount + 3 + (i - 9);
                }
            }
        }
        double vehWeight = 0;//根据轴数判断的车货总限重
        switch (vehAxleCount) {
            case 1:
                vehWeight = totalAxleWeight;
                break;
            case 2:                                                             //2轴车限重17吨
                vehWeight = 1700;
                break;
            case 3:                                                             //3轴车限重25吨
                vehWeight = 2500;
                break;
            case 4:                                                             //4轴车限重35吨
                vehWeight = 3500;
                break;
            case 5:                                                             //5轴车限重43吨
                vehWeight = 4300;
                break;
            case 6:                                                             //6轴车限重49吨
                vehWeight = 4900;
                break;
            default:                                                             //6轴以上车限重49吨
                vehWeight = 4900;
                break;
        }
        double lmweight = totalAxleWeight > vehWeight ? vehWeight : totalAxleWeight;//限重取较小值
        double fweight = DoubleUtils.mul(dweight, 0.975);
        return lmweight < fweight;
    }

    /**
     * 获取第一辆车的限重及载重
     *
     * @author 王国栋 2014-9-4
     */
    private void getFirstVehWeight() {
//        axleCount = 0;
//        axleGroupCount = 0;
//        weight = 0;
//        limitWeight = 0;

        if (vehList.isEmpty()) {
            axleCount = 0;
            axleGroupCount = 0;
            axleGroupStr = "";
            AxisWeightDetail = "";
            limitWeight = 0;
            weight = 0;
            return;
        }

        if (!axleList.isEmpty()) {
            StringBuilder sb1 = new StringBuilder();
            List<Axle> list = axleList.get(0);
            for (int i = 0; i < list.size(); i++) {//获取第一辆车的单轴轴重集合
                if (i == list.size() - 1) {
                    sb1.append(DoubleUtils.div(list.get(i).getWeight(), 100, 2));
//                    AxisWeightDetail += DoubleUtils.div(list.get(i).getWeight(), 100, 2);
                } else {
                    sb1.append(DoubleUtils.div(list.get(i).getWeight(), 100, 2)).append(",");
//                    AxisWeightDetail += DoubleUtils.div(list.get(i).getWeight(), 100, 2) + ",";
                }
            }
            AxisWeightDetail = sb1.toString();
        }

        Map<Integer, AxleGroup> veh = vehList.get(0);
        Iterator ite = veh.keySet().iterator();
        axleGroupCount = veh.size();
        int vehAxleCount = 0;//车辆轴数
        double totalAxleWeight = 0;//车辆总轴重
        StringBuilder sb2 = new StringBuilder();
        double tempWeight = 0;
        while (ite.hasNext()) {
            AxleGroup ag = veh.get((Integer) ite.next());
            tempWeight += ag.getAxleWeight();
            String agType = ag.getAxleType();
            if (ite.hasNext()) {
//                axleGroupStr += agType + ",";
                sb2.append(agType).append(",");
            } else {
//                axleGroupStr += agType;
                sb2.append(agType);
            }
            int intAgType = IntegerUtils.parseString(agType);
            switch (intAgType) {
                case 1://单轴单轮
                    vehAxleCount++;
                    totalAxleWeight += 700;
                    break;
                case 2://单轴双轮
                    vehAxleCount++;
                    totalAxleWeight += 1000;
                    break;
                case 3://双联轴单轮
                    vehAxleCount = vehAxleCount + 2;
                    totalAxleWeight += 1000;
                    break;
                case 4://双联轴单双轮
                    vehAxleCount = vehAxleCount + 2;
                    totalAxleWeight += 1400;
                    break;
                case 5://双联轴双轮
                    vehAxleCount = vehAxleCount + 2;
                    totalAxleWeight += 1800;
                    break;
                case 6://三联轴单轮
                    vehAxleCount = vehAxleCount + 3;
                    totalAxleWeight += 1200;
                    break;
                case 7:// 三联轴两单一双轮
                    vehAxleCount = vehAxleCount + 3;
                    totalAxleWeight += 1600;
                    break;
                case 8://三联轴一单两双轮
                    vehAxleCount = vehAxleCount + 3;
                    totalAxleWeight += 2000;
                    break;
                case 9://三连轴双轮
                    vehAxleCount = vehAxleCount + 3;
                    totalAxleWeight += 2400;
                    break;
                default:
                    if (agType.matches("[a-fA-F0-9]+")) {//四联轴以上的车轴
                        int i = Integer.parseInt(agType, 16);
                        i = i - 9;
                        if (i < 0) {
                            vehAxleCount++;
                            totalAxleWeight += 700;
                        } else {
                            totalAxleWeight += 2400 + i * 400;
                            vehAxleCount = vehAxleCount + 3 + i;
                        }
                    }
                    break;
            }
        }
        weight = tempWeight;
        axleGroupStr = sb2.toString();
        axleCount = vehAxleCount;
        double vehWeight = 0;//根据轴数判断的车货总重
        switch (vehAxleCount) {
            case 1:
                vehWeight = totalAxleWeight;
                break;
            case 2:                                                             //2轴车限重17吨
                vehWeight = 1700;
                break;
            case 3:                                                             //3轴车限重25吨
                vehWeight = 2500;
                break;
            case 4:                                                             //4轴车限重35吨
                vehWeight = 3500;
                break;
            case 5:                                                             //5轴车限重43吨
                vehWeight = 4300;
                break;
            case 6:                                                             //6轴车限重49吨
                vehWeight = 4900;
                break;
            default:                                                             //6轴以上车限重49吨
                vehWeight = 4900;
                break;
        }
        if (totalAxleWeight > vehWeight) {                                      //车辆总轴重和和根据轴数判断的车货总重(限重)取较小值
            limitWeight = vehWeight;
        } else {
            limitWeight = totalAxleWeight;
        }
    }

    /**
     * 等待监控室确认车辆信息 1- 修改车辆信息发送原始车辆信息，用户选择轴组给服务器,服务器返回修改后的车辆信息 2-
     * 修改轴组信息发送修改后的车辆信息，用户选择轴组给服务器，服务器返回修改后的车辆信息 监控室拒绝修改返回“N”
     *
     * @param agList 所有的车辆轴组信息
     * @param code 确认代码
     * @param userChoice 用户选择的轴组
     * @return true 监控室确认修改或连接断开 false 监控室拒绝修改
     */
    private synchronized boolean waitForControlRoomConfirmVeh(List<List<AxleGroup>> agList, String code, String userChoice) {
        confirmFlag = true;//监控室确认标识，监控室确认过程中缓存中的车辆信息不可变化
        try {
            LogControl.logInfo("等待监控室确认计重修改" + agList);
            extJFrame.updateInfo("等待监控室确认", "等待监控室管理员确认");
            JSONArray array = JSONArray.fromObject(agList);
            VehInfoConfirm vif = new VehInfoConfirm();
            vif.setAxleList(array);
            String roadid = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "roadId", "00");
            String stationid = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "stationId", "00");
            String laneid = MyPropertiesUtils.getProperties(Constant.PROP_MTCCONFIG, "laneNo", "000");
            vif.setLane(roadid + "_" + stationid + "_" + laneid);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            vif.setTime(sdf.format(new Date()));
            vif.setAlarmCode(code);
            vif.setAxleChooseByUser(userChoice);
            String str = lc.waitForVehConfirm(vif.toString());
            if (str != null) {//监控室确认信息
                if (str.equalsIgnoreCase("N")) {//监控室拒绝修改或超时未确认
                    LogControl.logInfo("监控室拒绝计重修改");
                    return false;
                }
                LogControl.logInfo("监控室确认计重修改");
                //监控室确认修改
                List<Map<Integer, AxleGroup>> list = AxleUtils.transform(str);
                if (list == null) {
                    LogControl.logInfo("监控室返回车辆轴组信息为空");
                    return false;
                }
                int intCode = IntegerUtils.parseString(code);
                switch (intCode) {
                    case 1://修改车辆信息
                        List<List<Axle>> list1 = AxleUtils.updateAxleFromAxleGroupList(list, axleList);//获取修改车型后轴的集合
                        if (list1 == null) {
                            LogControl.logInfo("根据监控室返回信息获取车辆单轴信息为空");
                            return false;
                        }
                        vehList = list;
                        axleList = list1;
                        break;
                    case 2://修改轴型,需要判断第一辆车的总轴数是否发生变化，若变化，不能接受
                        Map<Integer, AxleGroup> map1 = list.get(0);
                        Map<Integer, AxleGroup> map2 = vehList.get(0);
                        int i = AxleUtils.compareVehMap(map1, map2);
                        if (i == 0) {//监控室修改轴型后第一辆车的总轴数未发生变化
                            vehList = list;
                        } else {
                            LogControl.logInfo("监控室确认修改轴组信息第一辆车的总轴数发生变化，回复信息为:" + map1);
                            keyboard.sendAlert();
                        }
                        break;
                    case 3://添加车辆信息
                        Axle ax = new Axle();
                        ax.setAxleType("01");//单轴单胎
                        ax.setWeight(0);
                        List<Axle> tempAxleList = new ArrayList();
                        tempAxleList.add(ax);
                        tempAxleList.add(ax);
                        vehList.add(0, list.get(0));
                        axleList.add(0, tempAxleList);
                        break;
                    case 4://删除第一辆车信息
                        removeFirstVeh();
                        break;
                    case 5://拖车
                        //获取监控室修改的拖车轴组信息
                        JSONObject jo1 = JSONObject.fromObject(str);
                        Object o = JSONObject.toBean(jo1, VehInfoConfirm.class);
                        VehInfoConfirm vic = (VehInfoConfirm) o;
                        userChoice = vic.getAxleChooseByUser();//监控室选择的需拆分的轴号
                        List<Map<Integer, AxleGroup>> tempList = AxleUtils.splitTowedVeh(vehList, IntegerUtils.parseString(userChoice));//拆分拖车
                        List<List<Axle>> axList = AxleUtils.getAxleListAfterSplit(list, axleList);
                        if (tempList == null) {
                            LogControl.logInfo("轴组信息为空");
                            return false;
                        }
                        if (axList == null) {
                            LogControl.logInfo("单轴信息为空");
                            return false;
                        }
                        vehList = tempList;
                        axleList = axList;
                        break;
                }
            } else {
                //与监控室连接断开
                LogControl.logInfo("与监控室连接断开，默认确认计重修改");
                List<Map<Integer, AxleGroup>> list = AxleUtils.transform(vif.toString());
                if (list == null) {
                    LogControl.logInfo("轴组信息为空");
                    return false;
                }
                int intCode = IntegerUtils.parseString(code);
                switch (intCode) {
                    case 1://修改车辆信息
                        List<List<AxleGroup>> list2 = AxleUtils.getModifiedVehAxleGroup(list, IntegerUtils.parseString(userChoice));//修改车辆信息
                        vehList = AxleUtils.getMapFromList(list2);
                        axleList = AxleUtils.updateAxleFromAxleGroupList(vehList, axleList);
                        break;
                    case 2://修改轴型,需要判断第一辆车的总轴数是否发生变化，若变化，不能接受,车轴信息不发生变化
                        vehList = list;
                        break;
                    case 3://添加车辆信息,默认添加双轴单胎车辆
                        Axle ax = new Axle();
                        ax.setAxleType("01");//单轴单胎
                        ax.setWeight(0);
                        List<Axle> tempAxleList = new ArrayList();
                        tempAxleList.add(ax);
                        tempAxleList.add(ax);
                        vehList.add(0, list.get(0));
                        axleList.add(0, tempAxleList);
                        break;
                    case 4://删除第一辆车信息
                        removeFirstVeh();
                        break;
                    case 5://拖车，默认将第一辆车的第二个轴按照7:3比例分开
                        List<Map<Integer, AxleGroup>> tempList = AxleUtils.splitTowedVeh(vehList, IntegerUtils.parseString(userChoice));//拆分拖车
                        List<List<Axle>> axList = AxleUtils.getAxleListAfterSplit(vehList, axleList);
                        if (tempList == null) {
                            LogControl.logInfo("轴组信息为空");
                            return false;
                        }
                        if (axList == null) {
                            LogControl.logInfo("单轴信息为空");
                            return false;
                        }
                        vehList = tempList;
                        axleList = axList;
                        break;
                }
            }
        } catch (Exception ex) {
            LogControl.logInfo("等待监控室确认计重修改过程中出现异常", ex);
        } finally {
            confirmFlag = false;//监控室确认结束
            extJFrame.hideCentralPanel();
        }
        return true;
    }

    /**
     * 车辆离开车道后更新车辆以及重量信息
     */
    public void updateAfterFirstVehLeft() {
        removeFirstVeh();
        axleCount = 0;
        axleGroupCount = 0;
        weight = 0;
        limitWeight = 0;
        axleGroupStr = "";
        AxisWeightDetail = "";
        updateFlag = true;
        longCar = false; //将长车标识修改为false
//        updateVehAxle();
    }

    /**
     * 移除第一辆车的信息
     */
    private synchronized void removeFirstVeh() {
        synchronized (obj) {
            if (!vehList.isEmpty()) {
                vehList.remove(0);
            }
            if (!axleList.isEmpty()) {
                axleList.remove(0);
            }
            if (!speedList.isEmpty()) {
                speedList.remove(0);
            }
        }
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList();
        list.addAll(null);
    }
}