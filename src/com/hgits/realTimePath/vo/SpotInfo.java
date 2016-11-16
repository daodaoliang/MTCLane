package com.hgits.realTimePath.vo;

/**
 * 识别点信息
 *
 * @author Wang Guodong
 */
public class SpotInfo {

    private String SpotId;//识别点序列
    private String SpotTime;//车牌识别时间（格式为:YYYYMMDD24HHMISS例：20140506215804）
    private String Distance;//车辆从入口站或上一个识别点到当前识别点的行驶里程数，数值精确到小数点后四位   单位：公里（单位不包含在Value中）
    private String Fee;	//车辆从入口站或上一个识别点到当前识别点的通行费，精确到小数点后两位   
    private String ImagePath;//识别图片（大图）URL，如：http://192.168.1.1/pic/stest.jpg
    private String Path;//车辆从入口站或上一个识别点到当前识别点的行驶路径信息

    public String getSpotId() {
        return SpotId;
    }

    public void setSpotId(String SpotId) {
        this.SpotId = SpotId;
    }

    public String getSpotTime() {
        return SpotTime;
    }

    public void setSpotTime(String SpotTime) {
        this.SpotTime = SpotTime;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String Distance) {
        this.Distance = Distance;
    }

    public String getFee() {
        return Fee;
    }

    public void setFee(String Fee) {
        this.Fee = Fee;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String ImagePath) {
        this.ImagePath = ImagePath;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String Path) {
        this.Path = Path;
    }

    @Override
    public String toString() {
        return "SpotInfo{" + "SpotId=" + SpotId + ", SpotTime=" + SpotTime + ", Distance=" + Distance + ", Fee=" + Fee + ", ImagePath=" + ImagePath + ", Path=" + Path + '}';
    }

}
