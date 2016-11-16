package com.hgits.tool.socket.entity;

/**
 * 封装需要监控室确认的信息
 *
 * @author huangjz
 */
public class Confirm extends BaseConfirm {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2957409304846374026L;
	/**
     * 入口站
     */
    private String confirmEnStation = "";
    /**
     * 通行卡
     */
    private String card = "";
    /**
     * 车牌号
     */
    private String plate = "";
    /**
     * 报警信息
     */
    private String alamInfo = "";
    /**
     * 请求时间
     */
    private String time = "";
    /**
     * 请求车道
     */
    private String lane = "";
    /**
     * 报警原因
     */
    private String whyAlam = "";//1 通行卡状态异常 2 超时 3 车型差异 4 车牌差异  5 车型车牌不符 6 U型车两小时之内 7 U型车两小时之外 8 无卡/残卡 9 不可读卡
    /**
     * 入口时间
     */
    private String enTime = "";
    /**
     * 入口车道
     */
    private String enLane = "";
    private String enType = "";//入口车型
    private String inputType = "";//收费员输入的车型
    public String getEnTime() {
        return enTime;
    }

    public void setEnTime(String enTime) {
        this.enTime = enTime;
    }

    public String getEnLane() {
        return enLane;
    }

    public void setEnLane(String enLane) {
        this.enLane = enLane;
    }

    public String getWhyAlam() {
        return whyAlam;
    }

    public void setWhyAlam(String whyAlam) {
        this.whyAlam = whyAlam;
    }

    public String getConfirmEnStation() {
        return confirmEnStation;
    }

    public void setConfirmEnStation(String confirmEnStation) {
        this.confirmEnStation = confirmEnStation;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getAlamInfo() {
        return alamInfo;
    }

    public void setAlamInfo(String alamInfo) {
        this.alamInfo = alamInfo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLane() {
        return lane;
    }

    public void setLane(String lane) {
        this.lane = lane;
    }

    public String getEnType() {
        return enType;
    }

    public void setEnType(String enType) {
        this.enType = enType;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }
    
    @Override
	public String toString() {
		return "{\"confirmEnStation\":\"" + confirmEnStation + "\","
				+ "\"card\":\"" + card + "\",\"plate\":\"" + plate + "\","
				+ "\"alamInfo\":\"" + alamInfo + "\",\"time\":\"" + time
				+ "\"," + "\"lane\":\"" + lane + "\",\"whyAlam\":\"" + whyAlam
				+ "\"," + "\"enTime\":\"" + enTime + "\",\"enLane\":\""
				+ enLane + "\"," + "\"enType\":\"" + enType
				+ "\",\"inputType\":\"" + inputType + "\",\"timeout\":\""
				+ super.getTimeout() + "\"}";// ,\"exVehType\":\""+exVehType+"\",\"exVehClass\":\""+exVehClass+"\",\"enStation\":\""+enStation+"\",\"enVehType\":\""+enVehType+"\",\"enVehClass\":\""+enVehClass+"\",\"fee\":\""+fee+"\",\"paymentMethod\":\""+paymentMethod+"\",\"status\":\""+status+"\",\"alarmInformation\":\""+alarmInformation+"\"}";
	}
}
