/**
 *
 */
package com.hgits.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import com.hgits.util.file.FileUtils;
import com.hgits.util.file.StartsWithFileFilter;

/**
 * 压缩工具类
 *
 * @author wh
 *
 */
public class ZipUtils {

    /**
     * Compresses the passed file to a .zip file, stores the .zip in the same
     * directory as the passed file, and then deletes the original, leaving only
     * the .zipped archive.
     *
     * @param file
     */
    public static void zipAndDelete(File file) throws IOException {
        if (!file.getName().endsWith(".zip")) {
            File zipFile = new File(file.getParent(), file.getName() + ".zip");
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);

//            byte[] buffer = new byte[4096];
//            while (true) {
//                int bytesRead = fis.read(buffer);
//                if (bytesRead == -1) {
//                    break;
//                } else {
//                	
//                    zos.write(buffer, 0, bytesRead);
//                }
//            }
            IOUtils.copy(fis, zos);
            zos.closeEntry();
            fis.close();
            zos.close();
            file.delete();
        }
    }

    /**
     * 功能:压缩多个文件成一个zip文件 <p>
     *
     * @param srcfile ：源文件列表
     * @param zipfile ：压缩后的文件
     * @throws IOException
     */
    public static void zipFilesAndDelete(List<File> srcfile, File zipfile)
            throws IOException {
//        byte[] buf = new byte[1024];
        
        final String tempZipName = zipfile+DateUtils.formatDateToString(DateUtils.getCurrentDate(),"yyyyMMddHHmmss")+"_temp";
		if(zipfile.exists()){//尝试将原备份文件转成临时文件，如果存在重名文件的话
			FileUtils.rename(zipfile.getPath(), tempZipName);
		}

        // ZipOutputStream类：完成文件或文件夹的压缩
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
        for (File file : srcfile) {
            FileInputStream in = new FileInputStream(file);
            out.putNextEntry(new ZipEntry(file.getName()));
//            int len;
//            while ((len = in.read(buf)) > 0) {
//                out.write(buf, 0, len);
//            }
            IOUtils.copy(in, out);
            out.closeEntry();
            in.close();
            file.delete();
        }
        out.close();
    }

    /**
     * 功能:解压缩 <p>
     *
     * @param zipfile ：需要解压缩的文件
     * @param descDir ：解压后的目标目录
     * @throws IOException
     */
    public static void unZipFilesAndDelete(File zipfile, String descDir)
            throws IOException {
        ZipFile zf = new ZipFile(zipfile);
        for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zf.getInputStream(entry);
            OutputStream out = new FileOutputStream(descDir + zipEntryName);
//            byte[] buf1 = new byte[1024];
//            int len;
//            while ((len = in.read(buf1)) > 0) {
//                out.write(buf1, 0, len);
//            }
            IOUtils.copy(in, out);
            in.close();
            out.close();
        }
    }

    /**
     * 将指定文件集合压缩到目标文件
     *
     * @param srcFileList 需要压缩的文件集合
     * @param destFile 目标文件
     * @throws IOException
     */
    public static void zipFile(List<String> srcFileList, String destFile) throws IOException {
        File zipFile = new File(destFile + ".zip");
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);
        for (String str : srcFileList) {
            FileInputStream fis = new FileInputStream(str);
            String temp = str.substring(str.lastIndexOf(File.separator)+1);
            ZipEntry zipEntry = new ZipEntry(temp);
            zos.putNextEntry(zipEntry);
//            byte[] buffer = new byte[4096];
//            while (true) {
//                int bytesRead = fis.read(buffer);
//                if (bytesRead == -1) {
//                    break;
//                } else {
//                    zos.write(buffer, 0, bytesRead);
//                }
//            }
            IOUtils.copy(fis, zos);
            zos.closeEntry();
            fis.close();
        }
        zos.close();
    }

    public static void main(String[] args) {
        File file = new File("logs/info.log");
        File[] files = file.getParentFile().listFiles(
                new StartsWithFileFilter(file.getName(), false));
        try {
            Vector<File> list = new Vector(Arrays.asList(files));
            FileUtils.remove(list, new Vector<File>());
            File zipFile = new File(file.getParent(), "backup3.zip");
            zipFilesAndDelete(list, zipFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
