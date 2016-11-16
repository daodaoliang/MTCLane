package com.hgits.util.file;

import java.io.File;
import java.io.FileFilter;

/**
 * 文件过滤器
 * @author wh
 *
 */
public class StartsWithFileFilter implements FileFilter {
	private String startsWith;
	private boolean inclDirs = false;

	/**
	     * 
	     */
	public StartsWithFileFilter(String startsWith, boolean includeDirectories) {
		super();
		this.startsWith = startsWith.toUpperCase();
		inclDirs = includeDirectories;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		if (!inclDirs && pathname.isDirectory()) {
			return false;
		} else
			return pathname.getName().toUpperCase().startsWith(startsWith);
	}
}