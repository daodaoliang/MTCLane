package nc.ui.mes.video;

import com.hgits.util.file.FileUtils;
import com.sun.jna.Native;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Panel;
import java.io.File;
import java.util.List;

public class SAA7134Back {

    private Container parent;
    private List<Panel> panelList;

    public SAA7134Back(List<Panel> panelList, Panel parent) {
        this.panelList = panelList;
        this.parent = parent;
        String path = FileUtils.getRootPath() + File.separator + "SAA7134.dll";
//        System.out.println("saa7134path:" + path);
        System.load(path);
//        String path = "saa7134";
//        System.loadLibrary(path);
        init();
    }

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

    private void setLayOut(int devNum) {
        int size = devNum;
        int columns;
        int row;
        if (size < 2) {
            row = 1;
            columns = 1;
        } else {
            row = 2;
            columns = size / row;
            if (size % row != 0) {
                row++;
            }
        }
        this.parent.setLayout(new GridLayout(row, columns, 5, 5));
    }

    public void grap() {
        File dir = new File("temp");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        String filename = dir.getAbsolutePath() + File.separator + "videoTemp.jpg";
        grap(filename, 0);
    }

    private native int getDeviceNum();

    private native boolean start(long paramLong, int paramInt);

    private native boolean initSAA7134(long paramLong);

    public native void closeSAA7134();

    private native void grap(String paramString, int paramInt);
}
