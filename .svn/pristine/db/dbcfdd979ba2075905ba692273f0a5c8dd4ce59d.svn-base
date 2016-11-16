package com.hgits.control;

import am.Avc;

import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;

public class AvcControl {
	private static AvcControl avcControl = null;
	private Avc avc = null;

	public static AvcControl getAvcControl(){
		if(avcControl == null){
			avcControl = new AvcControl();
		}
		return avcControl;
	}
	
	/**
	 * 构造函数
	 */
	private AvcControl(){
		String autoFunction = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "manualAutoFunction", null);
		String autoLevel = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "manualAutoLevel", null);
		
		//只有在使用自助发卡机模拟人工卡机模式且为西埃斯的卡机时进行初始化
		if("0".equals(autoFunction) && "2".equals(autoLevel)){
			initDevice();
		}		
	}
	
	/**
	 * 设备初始化
	 */
	private void initDevice(){
		ThreadPoolControl.getThreadPoolInstance().execute(new Runnable() {
			@Override
			public void run() {
			    String avcInfo = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_COMM, "AVC", null);//获取车型分类器的串口信息
			    if (avcInfo == null) {
			        LogControl.logInfo("启用自助发卡，但车型分类器配置文件异常");
			            throw new RuntimeException("车型分类器未配置!");
			    } else {
			        String[] buffer = avcInfo.split(",");
			        String portName = buffer[0].trim();//串口号
			        int baudRate = IntegerUtils.parseString(buffer[1].trim());//波特率
			        int dataBits = IntegerUtils.parseString(buffer[2].trim());//数据位
			        int stopBits = IntegerUtils.parseString(buffer[3].trim());//停止位
			        int parity = IntegerUtils.parseString(buffer[4].trim());//校验位
			        int logLevel = IntegerUtils.parseString(buffer[6].trim());//日志级别
			        avc = new Avc();
			        avc.init(portName, baudRate, dataBits, stopBits, parity, logLevel);
			        setAvc(avc);//设置对象
			    }
			}
		});

	}
	
	public void setAvc(Avc avc){
		this.avc = avc;
	}
	
	public Avc getAvc() {
		return avc;
	}
}
