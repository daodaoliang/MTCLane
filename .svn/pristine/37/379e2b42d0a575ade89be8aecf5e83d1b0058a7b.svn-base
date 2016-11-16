package com.hgits.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.hgits.vo.Axle;
import com.hgits.vo.AxleGroup;
import com.hgits.vo.VehInfoConfirm;

/**
 * 轴组计算的工具类
 *
 * @author Wang Guodong
 */
public class AxleUtils {
	
	private static final Logger logger = Logger.getLogger(AxleUtils.class);

    /**
     * 根据轴组数，将当前车辆指定的轴组改为指定的轴组
     *
     * @param list 当前车辆轴组集合
     * @param axleList 当前车辆轴集合
     * @param num 需要更改的轴组的轴组数
     * @param targetAxleGroup 目标轴组
     * @return 修改后的车辆轴组集合
     */
    public static List<AxleGroup> updateAxleGroupList(List<AxleGroup> list, List<Axle> axleList, int num, String targetAxleGroup) {
        List<AxleGroup> tempAgList = getAxleGroupListFromList(list);
        List<Axle> tempAxList = getAxleListFromList(axleList);
        List<AxleGroup> agList = new ArrayList();
        AxleGroup srcAg = tempAgList.get(num - 1);//获取用户要求更改的轴组
        AxleGroup axleGroup = new AxleGroup();//更改之后的轴组
        axleGroup.setAxleType(targetAxleGroup);//修改指定的轴组类型
        String srcType = srcAg.getAxleType();//获取轴组类型
        int num1 = getAxleCountFromAxleGroup(srcType);//获取轴组对应的轴数
        int num2 = getAxleCountFromAxleGroup(targetAxleGroup);//获取用户要求改成的轴组的轴数
        if (num1 == num2) {//轴数未发生变化
            axleGroup.setAxleWeight(srcAg.getAxleWeight());//修改指定的轴组重量
            agList.addAll(tempAgList.subList(0, num - 1));
            agList.add(axleGroup);
            agList.addAll(tempAgList.subList(num, tempAgList.size()));
        } else if (num1 > num2) {//修改后的轴组轴数比修改之前小（轴组减少轴数）
            List<Axle> tempList = new ArrayList();
            int temp = 0;
            for (int i = 0; i < num - 1; i++) {//获取所需修改轴组之前的轴组的轴数
                String type = tempAgList.get(i).getAxleType();
                temp += getAxleCountFromAxleGroup(type);
            }
            for (int i = temp + num2; i < temp + num1; i++) {//获取被修改轴组修改后剩下来的轴集合
                tempList.add(tempAxList.get(i));
            }
            AxleGroup tempAG = getAxleGroupFromAxleList(tempList);
            double srcAgWeight = srcAg.getAxleWeight();
            double tempAgWeight = tempAG.getAxleWeight();
            axleGroup.setAxleWeight(srcAgWeight - tempAgWeight);//修改指定的轴组重量
            agList.addAll(tempAgList.subList(0, num - 1));
            agList.add(axleGroup);
            agList.add(tempAG);
            agList.addAll(tempAgList.subList(num, tempAgList.size()));
        } else if (num1 < num2) {//修改后的轴组轴数比修改之前大（轴组增加轴数）
            int temp = 0;
            for (int i = 0; i < num - 1; i++) {
                String type = tempAgList.get(i).getAxleType();
                temp += getAxleCountFromAxleGroup(type);//获取当前车辆所需修改轴组之前的轴组的轴数
            }
            double weight = 0;
            for (int i = temp; i < temp + num2; i++) {
                weight = DoubleUtils.sum(weight, tempAxList.get(i).getWeight());//获取被修改轴组修改后的重量
            }
            axleGroup.setAxleWeight(weight);
            agList.addAll(tempAgList.subList(0, num - 1));
            agList.add(axleGroup);
            //需要对被修改轴组的下一个轴组进行判断，判断该轴组是否被拆开
            if (num2 == num1 + 1) {//被修改轴组仅增加一个轴
                AxleGroup ag1 = tempAgList.get(num);//获取被修改轴组的下一个轴组
                int a1 = getAxleCountFromAxleGroup(ag1.getAxleType());//获取修改轴组的下一个轴组轴数
                if (a1 > 1) {//修改轴组的下一个轴组会被拆开
                    List<Axle> tempList = tempAxList.subList(temp + num2, temp + num1 + a1);//修改轴组的下一个轴组被拆开后剩下的轴数
                    AxleGroup nextAg = getAxleGroupFromAxleList(tempList);//修改轴组的下一个轴组被拆开后变成的轴组
                    agList.add(nextAg);
                }
                agList.addAll(tempAgList.subList(num + 1, tempAgList.size()));
            } else if (num2 == num1 + 2) {//被修改轴组增加两个轴
                AxleGroup ag1 = tempAgList.get(num);//被修改轴组后面第一个轴组，以下简称后一
                int a1 = getAxleCountFromAxleGroup(ag1.getAxleType());
                if (a1 == 2) {//后一被吃
                    agList.addAll(tempAgList.subList(num + 1, tempAgList.size()));
                } else if (a1 > 2) {//后一被拆
                    List<Axle> tempList = tempAxList.subList(temp + num2, temp + num1 + a1);
                    AxleGroup nextAg = getAxleGroupFromAxleList(tempList);//修改轴组的下一个轴组被拆开后变成的轴组
                    agList.add(nextAg);
                    agList.addAll(tempAgList.subList(num + 1, tempAgList.size()));
                } else if (a1 == 1) {//后一不能满足修改轴组的需求
                    AxleGroup ag2 = tempAgList.get(num + 1);//被修改轴组后面第一个轴组，以下简称后二
                    int a2 = getAxleCountFromAxleGroup(ag2.getAxleType());
                    if (a2 == 1) {//后二被吃
                        agList.addAll(tempAgList.subList(num + 2, tempAgList.size()));
                    } else if (a2 > 1) {//后二被拆
                        List<Axle> tempList = tempAxList.subList(temp + num2, temp + num1 + a1 + a2);
                        AxleGroup nextAg = getAxleGroupFromAxleList(tempList);//修改轴组的下一个轴组被拆开后变成的轴组
                        agList.add(nextAg);
                        agList.addAll(tempAgList.subList(num + 2, tempAgList.size()));
                    }
                }
            }
        }
        return agList;
    }

    /**
     * 从给定的轴组中减少指定的轴数（从第一根轴开始减少）
     *
     * @param ag 给定的轴组
     * @param num 需要减少的轴数
     * @return
     */
    public AxleGroup reduceAxleFromGroup(AxleGroup ag, int num) {
        return null;
    }

    /**
     * 根据轴组类型获取轴数
     *
     * @param axleGroupType 轴组类型
     * @return 轴数
     */
    public static int getAxleCountFromAxleGroup(String axleGroupType) {
        int a = 0;
        int temp = IntegerUtils.parseHexString(axleGroupType);
        switch (temp) {
            case 1:
                a = 1;
                break;
            case 2:
                a = 1;
                break;
            case 3:
                a = 2;
                break;
            case 4:
                a = 2;
                break;
            case 5:
                a = 2;
                break;
            case 6:
                a = 3;
                break;
            case 7:
                a = 3;
                break;
            case 8:
                a = 3;
                break;
            case 9:
                a = 3;
                break;
            default:
                if (temp > 9) {
                    a = 3 + (temp - 9);
                }
                break;
        }
        return a;
    }

    /**
     * 将所提供的轴集合合并为一个轴组
     *
     * @param axleList 需要合并的轴集合
     * @return 合并后的轴组
     */
    public static AxleGroup getAxleGroupFromAxleList(List<Axle> axleList) {
        List<Axle> tempAxList = getAxleListFromList(axleList);
        AxleGroup ag = new AxleGroup();
        int len = tempAxList.size();//获取需要合并的轴数
        if (len == 1) {//单轴
            ag.setAxleType(tempAxList.get(0).getAxleType());
            ag.setAxleWeight(tempAxList.get(0).getWeight());
        } else if (len == 2) {//双联轴
            int type1 = IntegerUtils.parseString(tempAxList.get(0).getAxleType());
            int type2 = IntegerUtils.parseString(tempAxList.get(1).getAxleType());
            if (type1 + type2 == 2) {//双联轴单轮
                ag.setAxleType("03");
            } else if (type1 + type2 == 3) {//双联轴单双轮
                ag.setAxleType("04");
            } else if (type1 + type2 == 4) {//双联轴双轮
                ag.setAxleType("05");
            }
            ag.setAxleWeight(DoubleUtils.sum(tempAxList.get(0).getWeight(), tempAxList.get(1).getWeight()));
        } else if (len == 3) {//三联轴
            Axle a1 = tempAxList.get(0);
            Axle a2 = tempAxList.get(1);
            Axle a3 = tempAxList.get(2);
            int type1 = IntegerUtils.parseString(a1.getAxleType());
            int type2 = IntegerUtils.parseString(a2.getAxleType());
            int type3 = IntegerUtils.parseString(a3.getAxleType());
            int temp = type1 + type2 + type3;
            if (temp == 3) {//三联轴单轮
                ag.setAxleType("06");
            } else if (temp == 4) {//三联轴一双两单轮
                ag.setAxleType("07");
            } else if (temp == 5) {//三联轴一单两双轮
                ag.setAxleType("08");
            } else if (temp == 6) {
                ag.setAxleType("09");//三联轴双轮
            }
            ag.setAxleWeight(DoubleUtils.sum(a1.getWeight(), a2.getWeight(), a3.getWeight()));
        }
        return ag;
    }

    /**
     * 将给定的轴组信息map集合转换为list集合
     *
     * @param map 给定的轴组信息map集合
     * @return 转换后的轴组list
     */
    public static List<AxleGroup> getAxleGroupListFromMap(Map<Integer, AxleGroup> map) {
        Map<Integer, AxleGroup> tempMap = getMapFromMap(map);
        List<AxleGroup> list = new ArrayList();
        for (int i = 1; i <= tempMap.size(); i++) {
            list.add(tempMap.get(i));
        }
        return list;
    }

    /**
     * 获取所有轴组MAP集合的轴组信息集合
     *
     * @param vehList 所有轴组信息map集合
     * @return 轴组信息集合
     */
    public static List<AxleGroup> getAllAxleGroupList(List<Map<Integer, AxleGroup>> vehList) {
        List<Map<Integer, AxleGroup>> tempVehList = getAxleGroupMapListFromList(vehList);
        List<AxleGroup> list = new ArrayList();
        for (int i = 0; i < tempVehList.size(); i++) {
            List<AxleGroup> tempList = getAxleGroupListFromMap(tempVehList.get(i));
            list.addAll(tempList);
        }
        return list;
    }

    /**
     * 根据所提供的当前车辆的轴组信息以及当前车辆需要更改的轴组数获得当前轴组可以更改的轴组集合 不能超过该车辆的轴数限制
     *
     * @param axleGroupMap 当前车辆轴组信息map
     * @param axleMap 当前车辆轴信息集合
     * @param num 需要修改的轴组的位置
     * @return
     */
    public static List<AxleGroup> getAxleGroupList(Map<Integer, AxleGroup> axleGroupMap, List<Axle> axleMap, int num) {
        Map<Integer, AxleGroup> tempMap = getMapFromMap(axleGroupMap);
        List<Axle> tempAxList = getAxleListFromList(axleMap);
        List<AxleGroup> agList = new ArrayList();
        List<Axle> axList = new ArrayList();
        int temp = 0;
        for (int i = 1; i < num; i++) {//获得当前车辆需要修改的轴组数对应的轴数
            AxleGroup ag = tempMap.get(i);
            String at = ag.getAxleType();//轴组类型
            int a = IntegerUtils.parseString(at);
            if (a <= 2) {//单轴
                temp += 1;
            } else if (a <= 5) {//双联轴
                temp += 2;
            } else {//三联轴
                temp += 3;
            }
        }
        for (int i = temp; i < tempAxList.size(); i++) {//从收费员选定的轴获得车辆后续的最多三个轴
            Axle ax = tempAxList.get(i);
            axList.add(ax);
            if (i == temp + 2) {
                break;
            }
        }
        int len = axList.size();
        if (len == 1) {
            AxleGroup ag1 = new AxleGroup();
            ag1.setAxleType("01");
            ag1.setAxleWeight(axList.get(0).getWeight());
            agList.add(ag1);
            AxleGroup ag2 = new AxleGroup();
            ag2.setAxleType("02");
            ag2.setAxleWeight(axList.get(0).getWeight());
            agList.add(ag2);
        } else if (len == 2) {
            AxleGroup ag1 = new AxleGroup();
            ag1.setAxleType("01");
            ag1.setAxleWeight(axList.get(0).getWeight());
            agList.add(ag1);
            AxleGroup ag2 = new AxleGroup();
            ag2.setAxleType("02");
            ag2.setAxleWeight(axList.get(0).getWeight());
            agList.add(ag2);
            AxleGroup ag3 = new AxleGroup();
            ag3.setAxleType("03");
            ag3.setAxleWeight(axList.get(0).getWeight() + axList.get(1).getWeight());
            agList.add(ag3);
            AxleGroup ag4 = new AxleGroup();
            ag4.setAxleType("04");
            ag4.setAxleWeight(axList.get(0).getWeight() + axList.get(1).getWeight());
            agList.add(ag4);
            AxleGroup ag5 = new AxleGroup();
            ag5.setAxleType("05");
            ag5.setAxleWeight(axList.get(0).getWeight() + axList.get(1).getWeight());
            agList.add(ag5);
        } else if (len == 3) {
            AxleGroup ag1 = new AxleGroup();
            ag1.setAxleType("01");
            ag1.setAxleWeight(axList.get(0).getWeight());
            agList.add(ag1);
            AxleGroup ag2 = new AxleGroup();
            ag2.setAxleType("02");
            ag2.setAxleWeight(axList.get(0).getWeight());
            agList.add(ag2);
            AxleGroup ag3 = new AxleGroup();
            ag3.setAxleType("03");
            ag3.setAxleWeight(axList.get(0).getWeight() + axList.get(1).getWeight());
            agList.add(ag3);
            AxleGroup ag4 = new AxleGroup();
            ag4.setAxleType("04");
            ag4.setAxleWeight(axList.get(0).getWeight() + axList.get(1).getWeight());
            agList.add(ag4);
            AxleGroup ag5 = new AxleGroup();
            ag5.setAxleType("05");
            ag5.setAxleWeight(axList.get(0).getWeight() + axList.get(1).getWeight());
            agList.add(ag5);
            AxleGroup ag6 = new AxleGroup();
            ag6.setAxleType("06");
            ag6.setAxleWeight(axList.get(0).getWeight() + axList.get(1).getWeight() + axList.get(2).getWeight());
            agList.add(ag6);
            AxleGroup ag7 = new AxleGroup();
            ag7.setAxleType("07");
            ag7.setAxleWeight(axList.get(0).getWeight() + axList.get(1).getWeight() + axList.get(2).getWeight());
            agList.add(ag7);
            AxleGroup ag8 = new AxleGroup();
            ag8.setAxleType("08");
            ag8.setAxleWeight(axList.get(0).getWeight() + axList.get(1).getWeight() + axList.get(2).getWeight());
            agList.add(ag8);
            AxleGroup ag9 = new AxleGroup();
            ag9.setAxleType("09");
            ag9.setAxleWeight(axList.get(0).getWeight() + axList.get(1).getWeight() + axList.get(2).getWeight());
            agList.add(ag9);
        }
        return agList;
    }

    /**
     * 将VehInfoConfirm转换为的String字符串转换为List<Map<Integer,AxleGroup>>的集合
     *
     * @param str VehInfoConfirm转换为的String字符串
     * @return List<Map<Integer,AxleGroup>>的集合
     */
    public static List<Map<Integer, AxleGroup>> transform(String str) {
        List<Map<Integer, AxleGroup>> vehList = new ArrayList();
        JSONObject jo1 = JSONObject.fromObject(str);
        Object o = JSONObject.toBean(jo1, VehInfoConfirm.class);
        VehInfoConfirm vic = (VehInfoConfirm) o;
        List<List<AxleGroup>> list = vic.getAxleList();
        for (int i = 0; i < list.size(); i++) {
            List<AxleGroup> tempList = list.get(i);
            Map<Integer, AxleGroup> map = new HashMap();
            for (int j = 0; j < tempList.size(); j++) {
                JSONObject jo2 = JSONObject.fromObject(tempList.get(j));
                AxleGroup ag = (AxleGroup) JSONObject.toBean(jo2, AxleGroup.class);
                map.put(j + 1, ag);
            }
            vehList.add(map);
        }
        return vehList;
    }

    /**
     * 获取轴组信息集合中所有轴组的总轴数(轴数并非轴组数)
     *
     * @param map 轴组信息集合
     * @return 总轴数
     */
    public static int getAxleCount(Map<Integer, AxleGroup> map) {
        Map<Integer, AxleGroup> tempMap = getMapFromMap(map);
        List<AxleGroup> list = getAxleGroupListFromMap(tempMap);
        int i = 0;
        for (AxleGroup ag : list) {
            i += getAxleCountFromAxleGroup(ag.getAxleType());
        }
        return i;
    }

    /**
     * 比较两个车辆轴组map的总轴数
     *
     * @param map1
     * @param map2
     * @return map1等于map2 返回0 map1大于map2 返回1 map1小于map2 返回-1
     */
    public static int compareVehMap(Map<Integer, AxleGroup> map1, Map<Integer, AxleGroup> map2) {
        int i = getAxleCount(map1);
        int j = getAxleCount(map2);
        if (i > j) {
            return 1;
        } else if (i == j) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 将拖车拆分（第二个轴组按照7:3比例拆开）
     *
     * @param vehList 当前车辆信息集合
     * @param splitAxleGroup 指定轴组进行拆分
     * @return 拆分后的车辆信息集合
     */
    public static List<Map<Integer, AxleGroup>> splitTowedVeh(List<Map<Integer, AxleGroup>> vehList, Integer splitAxleGroup) {
        if (vehList.isEmpty()) {
            return null;
        }
        List<Map<Integer, AxleGroup>> tempVehList = getAxleGroupMapListFromList(vehList);
        Map<Integer, AxleGroup> map = tempVehList.get(0);//获取第一辆车
        if (map.size() < 2) {//单轴车辆不可能是拖车
            return null;
        }
//        AxleGroup ag = map.get(2);//获取第二个轴组 //老版本处理逻辑
        AxleGroup ag = null;
        if (null == splitAxleGroup) {
            splitAxleGroup = 2;
            ag = map.get(2);//获取第二个轴组
        } else {
            ag = map.get(splitAxleGroup);//获取指定轴组
        }
        double weight = ag.getAxleWeight();
        ag.setAxleWeight(DoubleUtils.mul(weight, 0.7));//拖车第二个轴组占被拆分轴组重量的70%
        AxleGroup ag1 = new AxleGroup();//拆分后被拖车的第一个轴组，默认为单轴单胎
        ag1.setAxleType("01");
        ag1.setAxleWeight(DoubleUtils.mul(weight, 0.3));//被拖车第一个轴组占被拆分轴组重量的30%
        List<Map<Integer, AxleGroup>> list = new ArrayList();
        Map<Integer, AxleGroup> map2 = new HashMap();//被拖车
        if (map.size() == 2) {//若拖车只有两个轴组
            map2.put(1, ag1);
        } else if (map.size() > 2) {//拖车超过两个轴组
            map2.put(1, ag1);
            int len = map.size();
            int index = 2;
            for (int i = splitAxleGroup + 1; i <= len; i++) {
                map2.put(index, map.remove(i));// 将拖车第二个轴组之后的轴组转移到被拖车
                index++;
            }
        }
        list.add(map);//拖车
        list.add(map2);//被拖车
        if (tempVehList.size() > 1) {//剩余车辆信息
            list.addAll(tempVehList.subList(1, tempVehList.size()));
        }
        return list;
    }

    /**
     * 获取拖车处理之后的轴集合
     *
     * @param vehList 拖车处理前的轴组集合
     * @param axleList 拖车处理前的轴集合
     * @return 拖车处理后的轴集合
     */
    public static List<List<Axle>> getAxleListAfterSplit(List<Map<Integer, AxleGroup>> vehList, List<List<Axle>> axleList) {
        List<Map<Integer, AxleGroup>> tempVehList = getAxleGroupMapListFromList(vehList);
        List<List<Axle>> tempAxleList = getAxleListListFromList(axleList);
        int alen = tempAxleList.size();
        List<Axle> tempVeh = tempAxleList.get(0);//第一辆车轴集合
        List<AxleGroup> agList = getAllAxleGroup(tempVehList).get(0);//第一辆车轴组集合
        List<List<Axle>> list = new ArrayList();
        if (agList.size() < 2) {
            return null;
        }
        AxleGroup ag = agList.get(1);//获取第二个轴组
        int temp = getAxleCountFromAxleGroup(ag.getAxleType());
        if (temp == 1) {//单轴
            int len1 = getAxleCountFromAxleGroup(agList.get(0).getAxleType());//第一轴组的轴数
            Axle a1 = tempVeh.get(len1);
            double weight = a1.getWeight();
            a1.setWeight(DoubleUtils.mul(weight, 0.7));
            List<Axle> veh1 = new ArrayList();//拆分后拖车轴集合
            veh1.addAll(tempVeh.subList(0, len1));
            veh1.add(a1);
            List<Axle> veh2 = new ArrayList();//拆分后被拖车轴集合
            Axle a2 = new Axle();
            a2.setAxleType("01");
            a2.setWeight(DoubleUtils.mul(weight, 0.3));
            veh2.add(a2);
            veh2.addAll(tempVeh.subList(len1 + 1, tempVeh.size()));
            list.add(veh1);
            list.add(veh2);
            list.addAll(tempAxleList.subList(1, alen));
        } else if (temp == 2) {//双联轴
            int len1 = getAxleCountFromAxleGroup(agList.get(0).getAxleType());//第一轴组的轴数
            Axle a1 = tempVeh.get(len1);
            Axle a2 = tempVeh.get(len1 + 1);
            a1.setWeight(DoubleUtils.mul(a1.getWeight(), 0.7));
            a2.setWeight(DoubleUtils.mul(a2.getWeight(), 0.7));
            double weight = ag.getAxleWeight();
            List<Axle> veh1 = new ArrayList();//拆分后拖车轴集合
            veh1.addAll(tempVeh.subList(0, len1));
            veh1.add(a1);
            veh1.add(a2);
            List<Axle> veh2 = new ArrayList();//拆分后被拖车轴集合
            Axle a3 = new Axle();
            a3.setAxleType("01");
            a3.setWeight(DoubleUtils.mul(weight, 0.3));
            veh2.add(a3);
            veh2.addAll(tempVeh.subList(len1 + 2, tempVeh.size()));
            list.add(veh1);
            list.add(veh2);
            list.addAll(tempAxleList.subList(1, alen));
        } else if (temp == 3) {//三联轴
            int len1 = getAxleCountFromAxleGroup(agList.get(0).getAxleType());//第一轴组的轴数
            Axle a1 = tempVeh.get(len1);
            Axle a2 = tempVeh.get(len1 + 1);
            Axle a3 = tempVeh.get(len1 + 2);
            a1.setWeight(DoubleUtils.mul(a1.getWeight(), 0.7));
            a2.setWeight(DoubleUtils.mul(a2.getWeight(), 0.7));
            a3.setWeight(DoubleUtils.mul(a3.getWeight(), 0.7));

            double weight = ag.getAxleWeight();
            List<Axle> veh1 = new ArrayList();//拆分后拖车轴集合
            veh1.addAll(tempVeh.subList(0, len1));
            veh1.add(a1);
            veh1.add(a2);
            veh1.add(a3);
            List<Axle> veh2 = new ArrayList();//拆分后被拖车轴集合
            Axle a4 = new Axle();
            a4.setAxleType("01");
            a4.setWeight(DoubleUtils.mul(weight, 0.3));
            veh2.add(a4);
            veh2.addAll(tempVeh.subList(len1 + 3, tempVeh.size()));
            list.add(veh1);
            list.add(veh2);
            list.addAll(tempAxleList.subList(1, alen));
        }
        return list;
    }

    /**
     * 将MAP映射的集合转换为LIST对应的集合
     *
     * @param vehList MAP映射集合
     * @return LIST对应的集合
     */
    public static List<List<AxleGroup>> getAllAxleGroup(List<Map<Integer, AxleGroup>> vehList) {
        List<Map<Integer, AxleGroup>> tempVehList = getAxleGroupMapListFromList(vehList);
        List<List<AxleGroup>> list = new ArrayList();
        for (int i = 0; i < tempVehList.size(); i++) {
            Map<Integer, AxleGroup> map = tempVehList.get(i);
            List<AxleGroup> agList = getAxleGroupListFromMap(map);
            list.add(agList);
        }
        return list;
    }

    /**
     * 将车辆轴组信息的LIST集合转换为MAP集合
     *
     * @param list 车辆轴组信息的LIST
     * @return MAP 集合
     */
    public static List<Map<Integer, AxleGroup>> getMapFromList(List<List<AxleGroup>> list) {
        List<List<AxleGroup>> tempAgList = getAxleGroupListListFromList(list);
        List<Map<Integer, AxleGroup>> tempList = new ArrayList();
        for (int i = 0; i < tempAgList.size(); i++) {
            List<AxleGroup> agList = tempAgList.get(i);
            Map<Integer, AxleGroup> map = new HashMap();
            for (int j = 0; j < agList.size(); j++) {
                AxleGroup ag = agList.get(j);
                map.put(j + 1, ag);
            }
            tempList.add(map);
        }
        return tempList;
    }

    /**
     * 将指定的车辆信息中第一辆车的轴组改为指定轴组类型
     *
     * @param vehList 车辆轴组信息
     * @param axleList 车辆轴信息
     * @param agNum 第一辆车需要修改的轴组数
     * @param targetType 目标轴组类型
     * @return 修改后的车辆信息
     */
    public static List<List<AxleGroup>> modifyAxleGroup(List<Map<Integer, AxleGroup>> vehList, List<List<Axle>> axleList, int agNum, String targetType) {
        if (vehList.isEmpty() || axleList.isEmpty()) {
            return null;
        }
        List<Map<Integer, AxleGroup>> tempList = getAxleGroupMapListFromList(vehList);
        List<List<Axle>> tempAxleList = getAxleListListFromList(axleList);
        List<List<AxleGroup>> agList = getAllAxleGroup(tempList);
        List<AxleGroup> list = updateAxleGroupList(agList.get(0), tempAxleList.get(0), agNum, targetType);
        agList.remove(0);
        agList.add(0, list);
        return agList;
    }

    /**
     * 根据给定的轴组映射集合以及给定的轴组数，重新组合，将给定的轴组数定位第一辆车的最后一个轴组
     *
     * @param vehList 给定的车辆轴组映射集合
     * @param num 给定的轴组数
     * @return 重组后的车辆轴组集合
     */
    public static List<List<AxleGroup>> getModifiedVehAxleGroup(List<Map<Integer, AxleGroup>> vehList, int num) {
        List<Map<Integer, AxleGroup>> tempVehList = getAxleGroupMapListFromList(vehList);
        List<List<AxleGroup>> list = getAllAxleGroup(tempVehList);
        List<List<AxleGroup>> resultList = new ArrayList();
        List<AxleGroup> veh1 = new ArrayList();
        int len = 0;
        for (int i = 0; i < list.size(); i++) {
            List<AxleGroup> tempList = list.get(i);
            len += tempList.size();
            if (len > num) {
                int len1 = veh1.size();
                veh1.addAll(tempList.subList(0, num - len1));
                List<AxleGroup> veh2 = tempList.subList(num - len1, tempList.size());
                resultList.add(veh1);
                resultList.add(veh2);
                resultList.addAll(list.subList(i + 1, list.size()));
                break;
            } else if (len == num) {
                veh1.addAll(tempList);
                resultList.add(veh1);
                resultList.addAll(list.subList(i + 1, list.size()));
                break;
            } else if (len < num) {
                veh1.addAll(tempList);
            }

        }
        return resultList;
    }

    /**
     * 根据所有车辆轴组信息更新所有车辆轴信息
     *
     * @param vehList 所有车辆轴组信息
     * @param axleList 所有车辆轴信息
     * @return 更新后的车辆轴信息
     */
    public static List<List<Axle>> updateAxleFromAxleGroupList(List<Map<Integer, AxleGroup>> vehList, List<List<Axle>> axleList) {
        List<Map<Integer, AxleGroup>> tempVehList = getAxleGroupMapListFromList(vehList);
        List<List<Axle>> tempAxleList = getAxleListListFromList(axleList);
        List<List<Axle>> list = new ArrayList();
        List<Axle> axList = new ArrayList();
        for (int i = 0; i < tempAxleList.size(); i++) {
            axList.addAll(tempAxleList.get(i));
        }
        int len = 0;
        for (int i = 0; i < tempVehList.size(); i++) {
            Map<Integer, AxleGroup> map = tempVehList.get(i);
            int temp = getAxleCount(map);//获取车辆总轴数
            List<Axle> tempList = new ArrayList();
            if (axList.isEmpty()) {//若轴数为空，自动按照单轴添加轴信息
                for (int j = 0; j < temp; j++) {
                    Axle ax = new Axle();
                    ax.setAxleType("01");
                    tempList.add(ax);
                }
            } else {
                tempList.addAll(axList.subList(len, len + temp));

            }
            len += temp;
            list.add(tempList);
        }
        return list;
    }

    /**
     * list+list+Axle嵌套值传递,对嵌套的list+list+Axle进行依次遍历，确保用过此方法后目标list与源list之间不会相互影响
     *
     * @param list 源list
     * @return 目标list
     */
    public static List<List<Axle>> getAxleListListFromList(List<List<Axle>> list) {
        List<List<Axle>> axList = new ArrayList();
        for (List<Axle> l : list) {
            List<Axle> tempList = new ArrayList();
            for (Axle a : l) {
                if (a == null) {
                	logger.info("发现为null的车轴：" + list);
                } else {
                    Axle tempa = new Axle(a.getAxleType(), a.getWeight());
                    tempList.add(tempa);
                }
            }
            if (tempList.isEmpty()) {
                logger.info("发现为空的车辆轴集合：" + list);
            } else {
                axList.add(tempList);
            }
        }
        return axList;
    }

    /**
     * list+map+AxleGroup嵌套值传递,对嵌套的list+map+AxleGroup进行依次遍历，确保用过此方法后目标list与源list之间不会相互影响
     *
     * @param list 源list
     * @return 目标list
     */
    public static List<Map<Integer, AxleGroup>> getAxleGroupMapListFromList(List<Map<Integer, AxleGroup>> list) {
        List<Map<Integer, AxleGroup>> agList = new ArrayList();
        for (Map<Integer, AxleGroup> map : list) {
            Map<Integer, AxleGroup> tempMap = getMapFromMap(map);
            if (tempMap.isEmpty()) {
                logger.info("发现为空的车辆轴组集合:" + list);
            } else {
                agList.add(tempMap);
            }
        }
        return agList;
    }

    /**
     * list+list+AxleGroup嵌套值传递,对嵌套的list+list+AxleGroup进行依次遍历，确保用过此方法后目标list与源list之间不会相互影响
     *
     * @param agList 源list
     * @return 目标list
     */
    public static List<List<AxleGroup>> getAxleGroupListListFromList(List<List<AxleGroup>> agList) {
        List<List<AxleGroup>> list = new ArrayList();
        for (List<AxleGroup> l : agList) {
            List<AxleGroup> tempList = new ArrayList();
            for (AxleGroup ag : l) {
                AxleGroup ag1 = new AxleGroup(ag.getAxleType(), ag.getAxleWeight());
                tempList.add(ag1);
            }
            if (tempList.isEmpty()) {
                logger.info("发现为空的车辆轴组集合:" + list);
            } else {
                list.add(tempList);
            }
        }
        return list;
    }

    /**
     * map+AxleGroup嵌套值传递,对嵌套的map+AxleGroup进行依次遍历，确保用过此方法后目标map与源map之间不会相互影响
     *
     * @param map 源map
     * @return 目标map
     */
    public static Map<Integer, AxleGroup> getMapFromMap(Map<Integer, AxleGroup> map) {
        Map<Integer, AxleGroup> tempMap = new HashMap();
        List<Integer> list1 = new ArrayList(map.keySet());
        Collections.sort(list1);
        for (Integer i : list1) {
            AxleGroup ag = map.get(i);
            AxleGroup ag1 = new AxleGroup(ag.getAxleType(), ag.getAxleWeight());
            tempMap.put(i, ag1);
        }
        return tempMap;
    }

    /**
     * list+AxleGroup,对嵌套的list+AxleGroup，确保用过此方法后目标list与源list之间不会相互影响
     *
     * @param list 源list
     * @return 目标list
     */
    public static List<AxleGroup> getAxleGroupListFromList(List<AxleGroup> list) {
        List<AxleGroup> agList = new ArrayList();
        for (AxleGroup ag : list) {
            AxleGroup ag1 = new AxleGroup(ag.getAxleType(), ag.getAxleWeight());
            agList.add(ag1);
        }
        return agList;
    }

    /**
     * list+Axle嵌套值传递,对嵌套的list+Axle进行依次遍历，确保用过此方法后目标list与源list之间不会相互影响
     *
     * @param list 源list
     * @return 目标list
     */
    public static List<Axle> getAxleListFromList(List<Axle> list) {
        List<Axle> axList = new ArrayList();
        for (Axle a : list) {
            Axle ax = new Axle(a.getAxleType(), a.getWeight());
            axList.add(ax);
        }
        return axList;
    }

    /**
     * 根据单轴信息校验轴组的正确性，若轴组信息与单轴信息不一致，以单轴信息为准<br>
     *(该判断以车辆重量信息为判断标准，若轴组与轴重量信息不一致将会导致本方法出现不可知的问题)
     * @param axleList 单轴信息集合
     * @param axleGroupList 轴组信息集合
     * @return List
     */
    public static List<Map<Integer, AxleGroup>> checkAxleGroupByAxle(List<List<Axle>> axleList, List<Map<Integer, AxleGroup>> axleGroupList) {
        List<Map<Integer, AxleGroup>> list = new ArrayList();
        int axleLen = axleList.size();
        int axleGroupLen = axleGroupList.size();
        int len = axleLen > axleGroupLen ? axleGroupLen : axleLen;
        for (int i = 0; i < len; i++) {
            List<Axle> list1 = axleList.get(i);
            Map<Integer, AxleGroup> map1 = axleGroupList.get(i);
            Map<Integer, AxleGroup> map = checkAxleGroupByAxle(list1, map1);
            list.add(map);
        }
        return list;
    }

    /**
     * 根据车辆单轴信息校验轴组的正确性，若轴组信息与单轴信息不一致，以单轴信息为准<br>
     *(该判断以车辆重量信息为判断标准，若轴组与轴重量信息不一致将会导致本方法出现不可知的问题)
     * @param list 车辆单轴信息集合
     * @param map 车辆轴组信息集合
     * @return 车辆轴组信息集合
     */
    public static Map<Integer, AxleGroup> checkAxleGroupByAxle(List<Axle> list, Map<Integer, AxleGroup> map) {
        Map<Integer, AxleGroup> resultMap = new HashMap();
        Iterator it = map.keySet().iterator();
        int mark = 0;
        while (it.hasNext()) {
            Integer temp = (Integer) it.next();
            AxleGroup ag = map.get(temp);
            double axleGroupWeight = ag.getAxleWeight();//轴组重量
            double axleWeightSum = 0;//单轴重量和
            int cnt = 0;//轴组对应的轴的数量
            for (int i = mark; i < list.size(); i++) {
                cnt++;
                Axle ax = list.get(i);
                axleWeightSum += ax.getWeight();
                if (axleGroupWeight <= axleWeightSum) {//轴组重量小于等于单轴重量之和
                    break;
                }
            }
            List<Axle> subList = list.subList(mark, mark + cnt);
            AxleGroup resultAg = new AxleGroup();
            String type = getAxleGroupTypeByAxleList(subList);
            resultAg.setAxleType(type);
            resultAg.setAxleWeight(ag.getAxleWeight());
            resultMap.put(temp, resultAg);
            mark = mark + cnt;
            cnt = 0;
        }
        return resultMap;
    }

    /**
     * 根据单轴集合获取轴组类型
     *
     * @param list 单轴集合
     * @return 轴组类型
     */
    public static String getAxleGroupTypeByAxleList(List<Axle> list) {
        if (list.size() >= 4) {//四联轴以上轴组
            int i = list.size() + 9 - 3;
            return Integer.toHexString(i);
        }
        StringBuilder sb = new StringBuilder();
        for (Axle axle : list) {
            sb.append(axle.getAxleType());
        }
        String type = null;
        switch (sb.toString()) {
            case "01":
                type = "01";//单轴单胎
                break;
            case "02":
                type = "02";//单轴双胎
                break;
            case "0101":
                type = "03";//双联轴单胎
                break;
            case "0102":
                type = "04";//双联轴单双胎
                break;
            case "0201":
                type = "04";//双联轴单双胎
                break;
            case "0202":
                type = "05";//双联轴双胎
                break;
            case "010101":
                type = "06";//三联轴单胎
                break;
            case "010102":
                type = "07";//三轴一双两单轮
                break;
            case "020101":
                type = "07";//三轴一双两单轮
                break;
            case "010201":
                type = "07";//三轴一双两单轮
                break;
            case "010202":
                type = "08";//三轴一单两双轮
                break;
            case "020102":
                type = "08";//三轴一单两双轮
                break;
            case "020201":
                type = "08";//三轴一单两双轮
                break;
            case "020202":
                type = "09";//三轴双轮
                break;
            default:
                break;

        }
        return type;
    }
    

}
