package com.hgits.util.keyborad;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hgits.util.file.FileUtils;
import com.hgits.util.file.UnicodeReader;

/**
 * 键盘映射配置工具类
 * 
 * @author Administrator
 * @since 2016-09-22
 */
public class KeyBoardUitls {

	private static Logger logger = Logger.getLogger(KeyBoardUitls.class);

	private final static KeyBoardUitls util = null;
	private Map<String, String> keyMap = new HashMap<>();

	// 禁用外部构造对象
	private KeyBoardUitls() {
		// 根据配置文件加载键盘映射配置
		loadKeybordMapping();
	}

	public static KeyBoardUitls getInstance() {
		if (util == null) {
			return new KeyBoardUitls();
		}
		return util;
	}

	/**
	 * 加载键盘映射
	 */
	private void loadKeybordMapping() {
		InputStream ips = null;
		try {
			// 开启电脑键盘
			String filePath = new StringBuilder()
					.append(FileUtils.getRootPath())
					.append("/resource/template/keyboard.properties")
					.toString();
			// 构造配置文件对象
			Properties properties = new Properties();
			ips = new BufferedInputStream(new FileInputStream(filePath));
			Reader reader = new UnicodeReader(ips, "UTF-8");
			properties.load(reader);
			if (!properties.isEmpty()) {
				logger.debug("键盘映射关系表" + properties);
				for (Entry<Object, Object> entry : properties.entrySet()) {
					keyMap.put(String.valueOf(entry.getValue()),
							String.valueOf(entry.getKey()));
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("加载配置文件失败!", e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("加载键值映射配置文件失败!", e);
			e.printStackTrace();
		} finally {
			if (ips != null) {
				try {
					ips.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 获取键值映射关系
	 * 
	 * @return
	 */
	public Map<String, String> getKeyMap() {
		return keyMap;
	}

	public static void main(String[] args) {
		// Map<String, String> map = KeyBoardUitls.getInstance().getKeyMap();
		// for (Entry<String, String> entry : map.entrySet()) {
		// System.out.println(entry.getKey() + "\t" + entry.getValue());
		// }
	}
}
