package com.hgits.vo;

import com.hgits.util.IntegerUtils;

/**
 *
 * @author jelly
 */
public class AxleGroup {

    //轴组类型 01 单轴单轮 02 单轴双轮 03 双轴单轮 04 双轴单双轮 05 双轴双轮 06 三轴单轮 07 三轴一双两单轮  08 三轴一单两双轮 09 三轴双轮 A四联轴 B五联轴 C六联轴 D 七联轴（依次类推）
    private String axleType;
    //轴重 重量单位为10千克
    private double axleWeight;

    public AxleGroup() {
    }

    public AxleGroup(String axleType, double axleWeight) {
        this.axleType = axleType;
        this.axleWeight = axleWeight;
    }

    public String getAxleType() {
        return axleType;
    }

    public void setAxleType(String axleType) {
        this.axleType = axleType;
    }

    public double getAxleWeight() {
        return axleWeight;
    }

    public void setAxleWeight(double axleWeight) {
        this.axleWeight = axleWeight;
    }

    @Override
    public String toString() {
        return "AxleGroup{" + "axleType=" + axleType + ", axleWeight=" + axleWeight + '}';
    }

    /**
     * 判断当前轴组是否超载
     *
     * @return true超载 false未超载
     */
    public boolean isOverWeight() {
        double limitWeight = 0;//限重 单位10千克
        if (axleType == null) {
            return false;
        }
        switch (axleType) {
            case "01":
                limitWeight = 700;
                break;
            case "02":
                limitWeight = 1000;
                break;
            case "03":
                limitWeight = 1000;
                break;
            case "04":
                limitWeight = 1400;
                break;
            case "05":
                limitWeight = 1800;
                break;
            case "06":
                limitWeight = 1200;
                break;
            case "07":
                limitWeight = 1600;
                break;
            case "08":
                limitWeight = 2000;
                break;
            case "09":
                limitWeight = 2400;
                break;
            default:
                if (axleType.matches("[a-fA-F0-9]+")) {//四联轴以上的车轴
                    int i = Integer.parseInt(axleType, 16);
                    i = i - 9;
                    limitWeight = 2400 + i * 400;
                }
                break;
        }
        return axleWeight > limitWeight;
    }

    /**
     * 获取限重，单位10千克
     *
     * @return 限重
     */
    public double getLimitWeight() {
        double limitWeight = 0;//限重 单位10千克
        if (axleType == null) {
            return 0;
        }
        switch (axleType) {
            case "01":
                limitWeight = 700;
                break;
            case "02":
                limitWeight = 1000;
                break;
            case "03":
                limitWeight = 1000;
                break;
            case "04":
                limitWeight = 1400;
                break;
            case "05":
                limitWeight = 1800;
                break;
            case "06":
                limitWeight = 1200;
                break;
            case "07":
                limitWeight = 1600;
                break;
            case "08":
                limitWeight = 2000;
                break;
            case "09":
                limitWeight = 2400;
                break;
            default:
                if (axleType.matches("[a-fA-F0-9]+")) {//四联轴以上的车轴
                    int i = Integer.parseInt(axleType, 16);
                    i = i - 9;
                    limitWeight = 2400 + i * 400;
                }
                break;
        }
        return limitWeight;
    }

    /**
     * 获取轴组对应的轴数
     *
     * @return 轴数
     */
    public int getAxleCount() {
        int a = 0;
        String axleGroupType = this.getAxleType();
        int temp = 0;
        if (axleGroupType.matches("[a-fA-F0-9]+")) {
            temp = IntegerUtils.parseHexString(axleGroupType);
        } else {
            return 0;
        }
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
}
