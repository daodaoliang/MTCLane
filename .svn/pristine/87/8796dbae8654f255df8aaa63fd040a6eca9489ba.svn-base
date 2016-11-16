/**
 * 
 */
package com.hgits.hardware.impl.video;

import java.awt.Component;
import java.awt.Container;
import java.awt.Panel;
import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.hgits.hardware.Video;
import com.hgits.tool.driver.IVideo;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;
import com.sun.jna.Native;

/**
 * GEA视频卡功能
 * @author Administrator
 *
 */
public class GeaVideo implements Video {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private static IVideo video;
	
	public GeaVideo(){
		video = IVideo.INSTANCE;
	}

	/* (non-Javadoc)
	 * @see com.hgits.hardware.Video#startCap()
	 */
	@Override
	public void startCap(Component panel) {
		int status = video.StartCap(Long.valueOf(Native.getComponentID(panel))
				.intValue(), 0, 0, 440, 315, Integer.parseInt(MyPropertiesUtils
				.getProperties(Constant.PROP_MTCLANE_TEST, "w", "320")),
				Integer.valueOf(MyPropertiesUtils.getProperties(
						Constant.PROP_MTCLANE_TEST, "h", "240")));
		log.debug("视频启动状态：" + status);

	}

	/* (non-Javadoc)
	 * @see com.hgits.hardware.Video#stopCap()
	 */
	@Override
	public void stopCap() {
		int status = video.StopCap();
		log.debug("视频关闭状态：" + status);
	}

	/* (non-Javadoc)
	 * @see com.hgits.hardware.Video#saveJPG()
	 */
	@Override
	public void saveJPG(String filePath) {
		int status = video.SaveJPG(filePath);
		log.debug("抓拍图片状态：" + status);

	}

	@Override
	public void grap() {
		File dir = new File("temp");
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		String filename = dir.getAbsolutePath() + File.separator
				+ "videoTemp.jpg";
		video.SaveJPG(filename);
	}

	@Override
	public String getRealizeName() {
		return this.getClass().getSimpleName();
	}

}
