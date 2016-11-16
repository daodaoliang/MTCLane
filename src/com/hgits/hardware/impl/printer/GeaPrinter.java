package com.hgits.hardware.impl.printer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TooManyListenersException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

import com.hgits.common.log.MTCLog;
import com.hgits.hardware.Printer;
import com.hgits.util.HexUtils;
import com.hgits.util.PrintUitls;
import com.hgits.util.SetCOMs;
import com.hgits.util.StringUtils;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

public class GeaPrinter implements SerialPortEventListener, Printer {

	public static SerialPort port;
	// 端口输入流
	private InputStream in;
	// 端口输出流
	private OutputStream out;
	/**
	 * 打印机状态
	 */
	private String printStatus = "";
	/**
	 * 打印机错误信息
	 */
	public String printError = "";
	/**
	 * 线程池
	 */
	private ExecutorService javaThreadPool = Executors.newFixedThreadPool(2);
	private int times;
	private String writerLog = "0";
	private int printTimes = 0;

	public OutputStream getOut() {
		return out;
	}

	public GeaPrinter() {
		
		try {
			// 加载串口信息
			String info = SetCOMs.getPrint();
			String[] infos = info.split(",");
			String com = infos[0];

			int baudRate = Integer.parseInt(infos[1]);// 波特率
			int dataBits = Integer.parseInt(infos[2]);// 数据位
			int stopBits = Integer.parseInt(infos[3]);// 停止位
			int parity = Integer.parseInt(infos[4]);// 校验位
			writerLog = infos[6];
			// 配置串口
			CommPortIdentifier portName = CommPortIdentifier
					.getPortIdentifier(com);
			port = (SerialPort) portName.open("打印机", 2000);

			port.setSerialPortParams(baudRate, dataBits, stopBits, parity);
			port.setDTR(true);
			port.setRTS(true);
			port.addEventListener(this);
			port.notifyOnDataAvailable(true);

			in = port.getInputStream();
			out = port.getOutputStream();

			checkMyslfe();

		} catch (NullPointerException e) {
			MTCLog.log("构造器：NullPointerException", e);
		} catch (PortInUseException e) {
			MTCLog.log("构造器：PortInUseException", e);
			printError = "打印机：串口被占用";
		} catch (NoSuchPortException e) {
			MTCLog.log("构造器：NoSuchPortException", e);
			printError = "打印机：没有这个串口";
		} catch (IOException e) {
			MTCLog.log("构造器：IOException", e);
			printError = "打印机：IO异常";
		} catch (UnsupportedCommOperationException e) {
			MTCLog.log("构造器：UnsupportedCommOperationException", e);
			printError = "打印机：UnsupportedCommOperationException";
		} catch (TooManyListenersException e) {
			MTCLog.log("构造器：TooManyListenersException", e);
			printError = "打印机：TooManyListenersException";
		}
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		times = 0;
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			byte[] b = null;
			try {
				while (in.available() > 0) {
					byte[] buffer = new byte[in.available()];
					in.read(buffer);
					for (int a = 0; a < buffer.length; a++) {
						printStatus = StringUtils.changeIntToHex(buffer[a], 2);
					}
				}
			} catch (IOException ex) {
				MTCLog.log("监听器：IOException", ex);
			}
			break;
		}
	}
    
	/**
	 * 基于打印模版方式进行打印
	 * @author zengzb
	 * @since 2016-08-29
	 */
	public void print(String limit_weight, String weight, String laneId,
            String staffId, String type, String cash,
            String entrance, String exit, String city, String cityFee, String plate) throws UnsupportedEncodingException {		
        if (out == null) {
            MTCLog.log("打印机输出流为null");
            return;
        }
        try {
            writeLog("打印机正在打印...", "");
            if(limit_weight == null || "".equals(limit_weight)){
            	limit_weight = "0";
            }
            if(weight == null || "".equals(weight)){
            	weight = "";
            }
            double dWeight = Double.parseDouble(limit_weight);
            if(dWeight > 0L){
	            //总限重
	            limit_weight = HexBin.encode(limit_weight.getBytes());
	            //总轴重
	            weight = HexBin.encode(weight.getBytes());
            }else{
	            //总限重(如果总限重小于或等于0时,采用空格占位)
	            limit_weight = HexBin.encode("".getBytes());
	            //总轴重(如果总轴重小于或等于0时,采用空格占位)
	            weight = HexBin.encode("".getBytes());
            }

            //车道ID
            laneId = HexBin.encode(laneId.getBytes());
            //员工编号
            staffId = HexBin.encode(staffId.getBytes());
            //车型
            if(type.length() == 1){
            	type = "0".concat(type);//意丰要求打印车型前加0
            }
            type = HexBin.encode(type.getBytes());
            //收费金额
            cash = HexBin.encode(cash.getBytes());
            //车辆所经入口
            entrance = HexBin.encode(entrance.getBytes("gbk"));
            //车辆所经出口
            exit = HexBin.encode(exit.getBytes("gbk"));
            //所在城市
            city = HexBin.encode(city.trim().getBytes("gbk"));
            String cityName = city;//代收费项目名称
            //城市通行费
            cityFee = HexBin.encode(cityFee.trim().getBytes());
            String cityNameFee = cityFee;//代收费数额
            if ("".equals(city)) {
                cityNameFee = "";
            }
            //车牌号码
            plate = HexBin.encode(plate.getBytes("gbk"));
            SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            String printDate = HexBin.encode(sf.format(new Date()).getBytes());
            
        	String[] params = new String[]{
        			limit_weight,//0:总限重
        			weight,//1：总轴重
        			laneId,//2：车道号
        			staffId,//3：收费员
        			type,//4：车型
        			cash,//5：金额
        			entrance,//6：入口
        			exit,//7：出口
        			printDate,//8：打印日期
        			plate,//9：车牌号码
        			cityName,//10：代收费项目名称
        			cityNameFee//11：代收费数额
        	};
        	
        	//根据打印模版获取最终的打印指令
        	String printTemplate = PrintUitls.getInstance().getPrintTemplate(params).replaceAll(" ", "");
        	
            write("发送打印指令", printTemplate);
        } catch (Exception ex) {
            MTCLog.log("打印机异常：", ex);
        }		
		
	}

	/**
	 * 出纸
	 */
	private void feed() {
		write("每打印6次出纸", "1B4A05");
	}

	/**
	 * 打印机测试页面
	 *
	 * @throws Exception
	 */
	public static void printTestPage() throws Exception {
		/**
		 * 打印机测试指令 20：空格  30：数字0
		 */
		String printTest = "1B 41 01 1B32 1B 4A 0A 1B 4A 0E"
				+ "14 1B 4A 1B "
				+ "20202020202020202020202020203020202020202020202020202020202020203020202020"
				+ "14 1B 4A 1B "
				+ "20 20 20 20 2020202020202020 583034 20 20 20 20 20 "// 车道 X04
				+ "20202020202020202020 30 30 30 30 31 38 20 20 20 20 "// 收费员
																		// 000018
				+ "14 1B 4A 1B"
				+ "20 20 20 2020 20 20 20202020202030 31"
				+ "202020202020202020 20 20 20 20 20203230303030"
				+ "14 1B 4A 1B "// 回车换行
				+ "20202020202020 202020 "
				+ "20202020D4C2D0CEC9BD 20 20 20 20 20 2020 20202020 b1 b1 be a9 b3 c7 "// 月形山
																						// //雨花
				+ "14 1B 4A 1B "
				+ "202020202020202030 30"// 某某城市
				+ "1B 4A 0E"//设置行距
				+ "202020202020202020202020202020202020202020202020202020203220202020"// 2日有效
				+ "14 1B 4A 1B"
				+ "2020202020202020202020202020202020 32 30 31 32 2F 31 31 2F 32 30 20 31 30 3A 30 32 1B 4A 0E"// 票据打印日期
				+ "14 1B 4A 1B " + "1B 4A 0E" + "1B 4A 0E" + "14 1B 4A 1B ";
		byte[] b = HexUtils.decode(printTest.replaceAll(" ", ""));
		port.getOutputStream().write(b);
	}

	/**
	 * 打印机自检
	 */
	private void checkMyslfe() {
		javaThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
						String order = changOrder(printStatus);
						String s = "";
						String s1 = "";
						try {
							s = order.charAt(4) + "";

							s1 = order.charAt(2) + "";
						} catch (Exception e) {
							times++;
							if (times > 2) {
								// 打印机通讯异常
								printError = "17";// "打印机通讯异常";
								continue;
							}

						}

						if (s.equals("1")) {
							// 打印机缺纸
							printStatus = "";
							printError = "18"; // "打印机缺纸";

						} else {
							printError = "";
						}
						if (s1.equals("1")) {
							// 打印机正常
							printStatus = "";
						}
					} catch (InterruptedException ex) {
						MTCLog.log("InterruptedException", ex);
					}
				}
			}
		});
		// 每1秒发送5，获取打印机的当前状态码
		javaThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
						MTCLog.log("打印机获取状态码异常线程", ex);
					}
					if (out != null) {
						write("获取打印机的当前状态码", 5);
					}

				}
			}
		});
	}

	/**
	 * 转换指令 将二进制第二位变成0
	 *
	 * @param hexString
	 * @return
	 */
	private String changOrder(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return "";
		}
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000"
					+ Integer.toBinaryString(Integer.parseInt(
							hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}

	/**
	 * 变回指令
	 *
	 * @param bString
	 * @return
	 */
	// private String backOrder(String bString) {
	// if (bString == null || bString.equals("") || bString.length() % 8 != 0) {
	// return null;
	// }
	// StringBuffer tmp = new StringBuffer();
	// int iTmp = 0;
	// for (int i = 0; i < bString.length(); i += 4) {
	// iTmp = 0;
	// for (int j = 0; j < 4; j++) {
	// iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j -
	// 1);
	// }
	// tmp.append(Integer.toHexString(iTmp));
	// }
	// return tmp.toString();
	// }
	public static void main(String[] args) {
		// Printer p = new Printer();

	}

	/**
	 * 获取打印机错误信息
	 *
	 * @return 打印机错误信息
	 */
	public String getPrintError() {
		String error = "";
		if (printError.equals("")) {
			error = null;
		} else {
			error = printError;
		}
		return error;
	}

	/**
	 * 发送串口命令记录日志
	 *
	 * @param orderName
	 * @param order
	 */
	private void writeLog(String orderName, String order) {
		if ("1".equals(writerLog)) {
			MTCLog.log("Printer " + orderName + ":" + order);
		}
	}

	/**
	 * 发送指令
	 *
	 * @param orderName
	 *            指令描述
	 * @param order
	 *            指令
	 */
	private synchronized void write(String orderName, Object order) {
		if (out == null) {
			MTCLog.log("打印机输出流为null");
			return;
		}
		try {
			if (order instanceof String) {
				out.write(HexUtils.decode((String) order));
				out.flush();

				writeLog(orderName, (String) order);
			} else {
				out.write((Integer) order);
				out.flush();
			}

		} catch (IOException ex) {
			MTCLog.log("打印机 :" + orderName, ex);
		}
	}
}
