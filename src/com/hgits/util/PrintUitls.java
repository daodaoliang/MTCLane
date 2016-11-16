package com.hgits.util;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import org.apache.log4j.Logger;

import com.hgits.exception.MTCException;
import com.hgits.util.file.FileUtils;
import com.hgits.util.file.ReadFromFileUtils;
import com.hgits.vo.Constant;

/**
 * 打印模版定制工具类
 * @author zengzb
 * @since 2016-08-30
 */
public class PrintUitls {
	private Logger log = Logger.getLogger(this.getClass().getName());
	private final static PrintUitls util = null;
	private static String printPattern = null;
	
	//禁用外部构造对象
	private PrintUitls(){
		//根据配置文件加载打印模版
		loadPrintPattern();
	}
	
	public static PrintUitls getInstance(){
		if(util == null){
			return new PrintUitls();
		}
		return util;
	}
	
	/**
	 * 加载打印配置模版
	 * @author zengzb
	 */
	private void loadPrintPattern(){
		String filePath = null;
		try {
			String collectFlag = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "collectFlag", "0");
			if("1".equals(collectFlag)){//开启代收功能
				filePath = new StringBuilder()
				.append(FileUtils.getRootPath())
				.append("/resource/template/printCollect.txt").toString();
			}else{
				filePath = new StringBuilder()
					.append(FileUtils.getRootPath())
					.append("/resource/template/print.txt").toString();
			}
			
			//构造模版文件对象
			File file = new File(filePath);
			
			//跳过文件第一行内容并读取之后的文本内容，第一行用于进行参数的注释			
			List<String> contentList = ReadFromFileUtils.readFileByLinesToListSkipTitleList(file, "gbk");
			if(null != contentList){
				StringBuffer sBuffer = new StringBuffer();//临时保存读取的字符串
				for(String tmpStr : contentList){
					sBuffer.append(tmpStr).append(" ");//拼接读取的每一行字符串
				}
				printPattern = sBuffer.toString();
			}
		} catch (MTCException e) {
			log.error("加载打印模版配置文件失败!", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取打印的原始模版
	 * @return 配置模版
	 */
	public String getPrintPattern(){
		return printPattern;
	}
	
	/**
	 * 根据配置模版解析出打印模版
	 * @param printPattern
	 * @return
	 */
	private String transPrintPattern(String printPattern){
		StringBuffer order = new StringBuffer();
		order.append("1B 40 18 1B 41 01 1B 32");
		String[] strs = printPattern.split("\\:");
		for(int i = 0; i < strs.length; i++){
			String type = strs[i];
			if(type.contains("Enter")){
				int num = Integer.parseInt(type.substring(type.indexOf("[")+1, type.indexOf("]")));
				if(num > 0){
					String hexNum = Integer.toHexString(num);
					if(hexNum.length() == 1){
						hexNum = "0".concat(hexNum);
					}
					order.append(" 1B 4A ").append(hexNum.toUpperCase());
				}				
			}else if(type.contains("Space")){
				int num = Integer.parseInt(type.substring(type.indexOf("[")+1, type.indexOf("]")));
				if(num > 0){
					for(int t = 0; t < num; t++){
						order.append(" 20");
					}	
				}	
			}else if(type.contains("Param")){
				int num = Integer.parseInt(type.substring(type.indexOf("(")+1, type.indexOf(")")));
				if(num >= 0){
					order.append(" {"+num+"}");
				}
			}			
		}
		order.append(" 1B 4A 6C");
		return order.toString();
	}
	
	/**
	 * 根据打印模版及参数转换出相应的打印指令
	 * @param params
	 * @return String 打印指令模版
	 */
	public String getPrintTemplate(String... params){
		String template = "";
		if(null == params || params.length < 1){
			return null;
		}
		
		if(null != printPattern){
			String printOrder = transPrintPattern(printPattern);//格式化打印模版
			
			//根据参数及打印模版转换出最终打印指令
			log.info("==============转换打印格式   输出打印指令==============");
			template = MessageFormat.format(printOrder, params);			
			log.info(template);
		}
		return template;
	}
	
	
	
	public static void main(String[] args) {
/*		String str = PrintUitls.getInstance().getPrintPattern();
		System.out.println(str);
		
		System.out.println("======================================");
		String[] params = new String[]{"14.00","5.00","X01","000018","01","12","宁乡","长沙西","i","湘A12567","k","m"};
		String str2 = PrintUitls.getInstance().getPrintTemplate(params);*/
		
		//System.out.println(str2);
		
		System.out.println(Integer.parseInt("12"));
		System.out.println("12=="+Integer.toHexString(Integer.parseInt("12")));
		
/*		String parttern = "Enter[60]:Space[10]:Param(0):Space[5]:Param(1):Space[10]";
		PrintUitls u = new PrintUitls();
		String str = u.transPrintPattern(parttern);
		System.out.println("str==="+str);*/
	}
}
