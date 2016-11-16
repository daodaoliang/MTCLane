/**
 * 
 */
package com.hgits.tool.driver;

import java.io.File;

import com.hgits.util.file.FileUtils;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

/**
 * 视频动态库调用类
 * 
 * @author Administrator
 *
 */
public interface IVideo extends StdCallLibrary {

	/**
	 * 实例
	 */
	public static final IVideo INSTANCE = (IVideo) Native.loadLibrary(
			FileUtils.getRootPath() + File.separator + "resource"
					+ File.separator + "dll" + File.separator + "video"
					+ File.separator + "VideoDLL", IVideo.class);

	/**
	 * 启动视频
	 * 
	 * @param hWnd
	 *            窗口句柄
	 * @param rect_left
	 *            区域左上x轴坐标
	 * @param rect_top
	 *            区域左上y轴坐标
	 * @param rect_right
	 *            区域右下x轴坐标
	 * @param rect_bottom
	 *            区域右下y轴坐标
	 * @param w
	 *            图片宽度
	 * @param h
	 *            图片高度
	 */
	public int StartCap(int hWnd, int rect_left, int rect_top, int rect_right,
			int rect_bottom, int w, int h);

	/**
	 * 关闭视频的方法
	 */
	public int StopCap();

	/**
	 * 抓拍图片的方法
	 * 
	 * @return
	 */
	public int SaveJPG(String filename);

}
