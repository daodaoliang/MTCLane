package nc.ui.mes.video;

import com.hgits.util.IntegerUtils;
import com.hgits.util.MyPropertiesUtils;
import java.awt.Container;
import java.awt.Panel;
import java.io.File;
import java.util.List;

import com.hgits.util.file.FileUtils;
import com.hgits.vo.Constant;
import com.sun.jna.Native;
import org.apache.log4j.Logger;

public class SAA7134 {

    private Container parent;
    private List<Panel> panelList;
    private Logger logger = Logger.getLogger(SAA7134.class.getName());

    public SAA7134(List<Panel> panelList, Panel parent) {
        this.panelList = panelList;
        this.parent = parent;
        String path = FileUtils.getRootPath() + File.separator + "SAA7134.dll";
        // System.out.println("saa7134path:" + path);
        System.load(path);
        // String path = "saa7134";
        // System.loadLibrary(path);
//        init();
        initNew();
    }

//    /**
//     * 兼容两种视频采集卡之前的构造函数。
//     * @param panelList
//     * @param parent
//     */
//    private void init(List<Panel> panelList, Panel parent) {
//        this.panelList = panelList;
//        this.parent = parent;
//        String path = FileUtils.getRootPath() + File.separator + "SAA7134.dll";
//        System.load(path);
//        init();
//    }
    private void init() {
        long frameHwnd = Native.getComponentID(this.parent);
        boolean ret = initSAA7134(frameHwnd);
        int devNum = getDeviceNum();
        for (int i = 0; i < devNum; i++) {
            long hwnd = Native.getComponentID(this.panelList.get(i));
            if (!start(hwnd, i)) {
                throw new RuntimeException(" start captureDevice faild!");
            }
        }
    }

    private void initNew() {
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_FUNCTION, "videoSocketNum", "1");
        int i = IntegerUtils.parseString(str);//获取视频接口
        if (i < 1 || i > 4) {//只接受1-4接口
            i = 1;
        }
        long frameHwnd = Native.getComponentID(this.parent);
        boolean ret = initSAA7134(frameHwnd);
        if (ret) {
            logger.debug("视频母窗体初始化成功");
        } else {
            logger.debug("视频母窗体初始化失败");
        }

        int devNum = getDeviceNum();
        logger.debug("视频接口数量：" + devNum);
        logger.debug("当前使用视频接口：" + i);
        long hwnd = Native.getComponentID(this.panelList.get(0));//视频固定输出到第一个面板上
        if (!start(hwnd, i-1)) {//接口0，1，2，3对应实际视频接口1，2，3，4
            logger.debug("获取视频失败");
        }

    }

    public void saveJPG(String paramString, int paramInt) {
        grap(paramString, paramInt);
    }

//    private void setLayOut(int devNum) {
//        int size = devNum;
//        int columns;
//        int row;
//        if (size < 2) {
//            row = 1;
//            columns = 1;
//        } else {
//            row = 2;
//            columns = size / row;
//            if (size % row != 0) {
//                row++;
//            }
//        }
//        this.parent.setLayout(new GridLayout(row, columns, 5, 5));
//    }
//    public void grap() {
//        File dir = new File("temp");
//        if (!dir.exists() || !dir.isDirectory()) {
//            dir.mkdirs();
//        }
//        String filename = dir.getAbsolutePath() + File.separator + "videoTemp.jpg";
//        grap(filename, 0);
//    }
    private native int getDeviceNum();

    private native boolean start(long paramLong, int paramInt);

    private native boolean initSAA7134(long paramLong);

    public native void closeSAA7134();

    private native void grap(String paramString, int paramInt);
}
