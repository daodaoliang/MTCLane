/**
 * 
 */
package com.hgits.hardware.impl.video;

import java.awt.Component;
import java.awt.Container;
import java.awt.Panel;
import java.io.File;
import java.util.List;

import nc.ui.mes.video.SAA7134;

import org.apache.log4j.Logger;

import com.hgits.hardware.Video;
import com.sun.jna.Native;

/**
 * VC4000天敏视频采集卡
 * @author Administrator
 *
 */
public class Vc400Video implements Video {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private SAA7134 saa7134;
	
	public Vc400Video(List<Panel> panelList, Panel parentPanel){
		saa7134 = new SAA7134(panelList,parentPanel);
	}

	/* (non-Javadoc)
	 * @see com.hgits.hardware.Video#startCap(java.awt.Component)
	 */
	@Override
	public void startCap(Component panel) {
//		saa7134.init(panelList,parentPanel);
//		long frameHwnd = Native.getComponentID(parentPanel);
//		boolean ret = saa7134.initSAA7134(frameHwnd);
//		log.debug("初始化视频：" + ret);
//		 int devNum = saa7134.getDeviceNum();
//		 for (int i = 0; i < devNum; i++) {
//			 long hwnd = Native.getComponentID(panelList.get(i));
//				boolean status = saa7134.start(hwnd, 0);
//				if (!status) {
//					throw new RuntimeException(" start captureDevice faild!");
//				}
//				log.debug("启动视频状态：" + status);
//		 }
	}

	/* (non-Javadoc)
	 * @see com.hgits.hardware.Video#stopCap()
	 */
	@Override
	public void stopCap() {
		saa7134.closeSAA7134();

	}

	/* (non-Javadoc)
	 * @see com.hgits.hardware.Video#saveJPG(java.lang.String)
	 */
	@Override
	public void saveJPG(String filePath) {
		saa7134.saveJPG(filePath, 0);
	}

	@Override
	public void grap() {
		File dir = new File("temp");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        String filename = dir.getAbsolutePath() + File.separator + "videoTemp.jpg";
        saa7134.saveJPG(filename, 0);
	}

	@Override
	public String getRealizeName() {
		return this.getClass().getSimpleName();
	}

}
