/**
 * 
 */
package com.hgits.util.rate;

import java.io.File;
import java.io.FileFilter;

import org.apache.log4j.Logger;

import com.hgits.util.file.FileUtils;

/**
 * 多版本费率文件过滤器
 * @author Administrator
 *
 */
public class MultiRateFileFilter implements FileFilter {
	
	private static Logger logger = Logger.getLogger(MultiRateFileFilter.class);
	
	
	String fileHead = "";
	public MultiRateFileFilter(String fileHead) {
		this.fileHead = fileHead;
	}

	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File pathname) {
		if(pathname.getName().startsWith(fileHead)){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		File directory = new File(FileUtils.getRootPath()+File.separator+"param");
		// 列出所有文件
		File[] files = directory.listFiles();
		logger.debug("\n目录" + directory.getName() + "下的所有文件");
		for (File file : files) {
			System.out.print("  " + file.getName());
		}
		// 列出所有20161001文件
		String fileHead = "20161001";
		File[] txtFiles = directory.listFiles(new MultiRateFileFilter(fileHead));
		logger.debug("\n目录" + directory.getName() + "下的"+fileHead+"文件");
		for (File file : txtFiles) {
			System.out.print("  " + file.getName());
		}
	}

}
