/**
 * 
 */
package com.hgits.service.constant;

import java.io.File;

/**
 * 基本信息的常量
 * @author wh
 * @date 2014-08-14
 *
 */
public interface BaseConstant {

	/**
	 * 文件间隔 File.separator
	 */
	public final static String FILE_SEPARAOTR = File.separator;
	
	/**
	 * String Buffer 默认初始化大小
	 */
	public final static int STRING_BUFFER_SIZE = 1024;
	
	/**
	 * 默认空字符串 
	 */
	public final static String EMPTY_STRING = "";
	
	/**
	 * 。的字符表示
	 */
	public final static char POINT = '.';
	
	/**
	 * 一个空格
	 */
	public final static String SPACE_STRING = " ";
	
	/**
	 * 前中括号
	 */
	public final static String OBVERSE_BRACKET = "\\[";
	
	/**
	 * 后中括号
	 */
	public final static String BACK_BRACKET = "\\]";
	
	/**
	 * 分割符 * 
	 */
	public final static char SPLIT_STAR = '*';
	
	/**
	 * 。的字符串表示
	 */
	public final static String POINT_STRING = ".";
	
	/**
	 * ' 号的字符串表示
	 */
	public final static String QUO_STRING = "'";
	
	/**
	 * ，的字符串表示
	 */
	public final static String COMMA_STRING = ",";
	
	/**
	 * Tab缩进的字符串表示
	 */
	public final static String TAB_STRING = "\t";
	
	/**
	 * 换行的字符表示
	 */
	public final static String WRAP_STRING = "\r\n";
	
	/**
	 * txt的文件后缀
	 */
	public final static String FILE_POSTFIX_TXT = "txt";
}
