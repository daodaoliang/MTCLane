package com.hgits.control;

import com.hgits.util.file.UnicodeReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * 建立resourceTemp文件夹， 软件启动时检测该文件夹， 若检测到该文件夹下有properties文件，
 * 则将该properties文件与resource文件夹下相应的properties文件做比较，
 * resourceTemp下有而resource没有，在resource追加
 * resourceTemp下有resource有但不同，根据resourceTemp更新resource
 * resourceTemp下有resource有并且resource下的属性被注释，根据resourceTemp更新resource
 *
 * @author WangGuodong
 */
public class RsControl {

    private final String dir1 = "resourceTemp";
    private final String dir2 = "resource";
    private final String equalMark = "=";
    private final Logger logger = Logger.getLogger(RsControl.class.getName());
    private final String defaultCharset = "UTF-8";
    private static RsControl instance;

    /**
     * 获取唯一实例化对象
     *
     * @return 唯一实例化对象
     */
    public synchronized static RsControl getSingleInstance() {
        if (instance == null) {
            instance = new RsControl();
        }
        return instance;
    }

    private RsControl() {

    }

    public void run() {
        log("开始检测" + dir1 + "文件夹");
        File file = new File(dir1);
        if (!file.exists() || !file.isDirectory()) {
            log("创建resourceTemp文件夹");
            boolean flag = file.mkdirs();
            if (flag) {
                log("创建resourceTemp文件夹成功");
            } else {
                log("创建resourceTemp文件夹失败");
            }
            return;
        }
        checkResourceTemp(file);

    }

    /**
     * 检测临时文件夹
     *
     * @param file
     */
    private void checkResourceTemp(File file) {
        File[] list = file.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                checkResourceTemp(f);
                continue;
            }
            String path = f.getAbsolutePath();
            String[] buffer = path.split(dir1);
            String name = buffer[buffer.length - 1];
            if (name.endsWith(".properties")) {
                String resourceFilePath = dir1 + name;
                String targetFilePath = dir2 + name;
                log("发现" + resourceFilePath + "文件,开始检测该文件");
                List<String> resourceList = getListFromFile(resourceFilePath);//获取源文件内容
                if (resourceList == null || resourceList.isEmpty()) {//源文件无内容
                    continue;
                }
                log(resourceFilePath + "文件内容为：" + resourceList);
                List<String> targetList = getListFromFile(targetFilePath);//获取目标文件内容
                compareLists(targetFilePath, resourceList, targetList);
            }
        }
    }

    /**
     * 获取文件内容
     *
     * @param resourceFilePath 文件路径
     * @return 文件集合
     */
    private List<String> getListFromFile(String resourceFilePath) {
        List<String> list = new ArrayList();
        FileInputStream fis = null;
        UnicodeReader ur = null;
        BufferedReader br = null;
        try {
            File resourceFile = new File(resourceFilePath);
            if (!resourceFile.exists() || !resourceFile.isFile() || resourceFile.isHidden()) {
                log("文件" + resourceFilePath + "不存在");
                return list;
            }
            fis = new FileInputStream(resourceFile);
            ur = new UnicodeReader(fis, defaultCharset);
            br = new BufferedReader(ur);
            String str;
            while ((str = br.readLine()) != null) {
                str = str.replaceAll(" ", "");
                if (str.trim().isEmpty()) {
                    continue;
                }
                list.add(str);
            }
        } catch (FileNotFoundException ex) {
            log(ex, ex);
        } catch (IOException ex) {
            log(ex, ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                log(ex,ex);
            }
            try {
                if (ur != null) {
                    ur.close();
                }
            } catch (IOException ex) {
                log(ex,ex);
            }
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                log(ex,ex);
            }
        }
        return list;
    }

    /**
     * 比较集合内容并根据比较结果修改目标文件
     *
     * @param targetFilePath
     * @param resourceList
     * @param targetList
     */
    private void compareLists(String targetFilePath, List<String> resourceList, List<String> targetList) {
        boolean flag = false;
        OUTER:
        for (String str : resourceList) {
            if (str == null || str.trim().isEmpty()) {//空不处理
                continue;
            }
            str = str.replaceAll(" ", "");
            if (str.startsWith("#")) {//注释不处理
                continue;
            }
            if (!str.contains(equalMark)) {//无等号不处理
                continue;
            }
            String tempStr = str.substring(0, str.indexOf("=") + 1);
            int len = targetList.size();
            for (int i = 0; i < len; i++) {
                String targetInfo = targetList.get(i);
                String temp = targetInfo.replaceAll(" ", "");
                if (temp.equals(str)) {//已包含更改内容,不需要对targetList做修改
                    continue OUTER;
                }
                temp = temp.replaceAll("#", "");
                if (temp.startsWith(tempStr)) {//包含更改内容的属性，需要对targetList做修改
                    log(targetFilePath + "文件发现需要将" + targetInfo + "修改为" + str);
                    flag = true;
                    targetList.set(i, str);
                    continue OUTER;
                }
            }
            log(targetFilePath + "文件发现需要追加内容：" + str);
            flag = true;//targetList全部处理完成后没有发现需要更改内容，表示需要追加改内容
            targetList.add(str);
        }
        if (flag) {
            log("修改" + targetFilePath + "文件");
            writeFile(targetFilePath, targetList);
        } else {
            log(targetFilePath + "文件检测完毕，没有需要修改或追加内容");
        }
    }

    /**
     * 更新文件
     *
     * @param filePath 文件路径
     * @param list 文件内容
     */
    private void writeFile(String filePath, List<String> list) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (!parent.exists() || !parent.isDirectory()) {
            boolean flag = parent.mkdirs();
            String path = parent.getAbsolutePath();
            if (flag) {
                log("创建" + path + "文件夹成功");
            } else {
                log("创建" + path + "文件夹失败");
                return;
            }
        }
        log("开始重写" + filePath);
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        PrintWriter pw = null;
        try {
            fos = new FileOutputStream(filePath);
            osw = new OutputStreamWriter(fos, defaultCharset);
            pw = new PrintWriter(osw);
            for (String str : list) {
                if (str != null) {
                    pw.println(str);
                    pw.flush();
                }
            }
        } catch (FileNotFoundException ex) {
            log(ex, ex);
        } catch (UnsupportedEncodingException ex) {
            log(ex, ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    log(ex, ex);
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException ex) {
                    log(ex, ex);
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        log(filePath + "文件修改成功");
    }

    private void log(String info) {
        String name = RsControl.class.getSimpleName();
        logger.debug(name + ": " + info);
    }

    private void log(Object info, Throwable t) {
        String name = RsControl.class.getSimpleName();
        logger.error(name + ": " + info, t);
    }

    public static void main(String[] args) {
        RsControl.getSingleInstance().run();
    }
}
