/**
 * 配置文件操作类
 */
package com.hgits.util.hardware;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件操作类
 *
 * @author zengzhibo
 *
 */
public class LaneConfigUtils {

	private static Properties cachePro = new Properties();
	static{
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(new File("resource/hardware/lane.properties"));
			cachePro.load(inStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从缓存的属性文件对象中根据key获取值
	 * @param key
	 * @return String
	 */
	public static String getProperty(String key){
		return cachePro.getProperty(key);
	}
	
	/**
	 * 当isCache设置为false时，实时从属性文件获取key对应的属性值
	 * @param key
	 * @param isCache
	 * @return String 
	 */
	public static String getProperty(String key,boolean isCache){
		InputStream in = null;
		Properties pro2 = new Properties();
		if(false == isCache){
			try {
				in = new BufferedInputStream(new FileInputStream(
						Thread.currentThread().getContextClassLoader().getResource("system.properties").getFile()));
				pro2.load(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				if(in != null){
					try {
						in.close();
						in = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return pro2.getProperty(key);
		}
		return LaneConfigUtils.getProperty(key);
	}
	
	public static void main(String[] args) {
//		System.out.println("======"+LaneConfigUtils.getProperty("TFI"));
	}
}