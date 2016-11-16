package com.hgits.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import jcifs.smb.SmbFile;

import org.apache.commons.io.IOUtils;

/**
 * 处理远程共享文件的工具类
 *
 * @author 王国栋
 */
public class SMBUtil {

    /**
     * 将远程共享文件smbFile复制到本地
     *
     * @param smbFile 远程共享文件
     * @param localPath 本地文件夹地址
     */
    public static void copySMBtoLocal(SmbFile smbFile, String localPath) {
        InputStream is = null;
        OutputStream os = null;
        try {
            if (!localPath.endsWith("/")) {
                localPath = localPath + "/";
            }
            File destDir = new File(localPath);
            if (!destDir.exists() || !destDir.isDirectory()) {
                destDir.mkdir();
            }
            String fileName = smbFile.getName();
            File destFile = new File(localPath + fileName);
            if (!destFile.exists() || !destFile.isFile()) {
                destFile.createNewFile();
            }
            is = smbFile.getInputStream();
            os = new FileOutputStream(localPath + fileName);
//            byte[] buffer = new byte[1024];
//            int len;
            IOUtils.copy(is, os);
//            while ((len=is.read(buffer)) != -1) {
//                os.write(buffer,0,len);
//            }
            os.flush();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SMBUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SMBUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(SMBUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
