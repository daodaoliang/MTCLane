package com.hgits.util;

import com.hgits.vo.Constant;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class SetCOMs {
 
    private static String CXP;//CXP
    private static String TFI;//费显
    private static String Keyboard;//键盘
    private static String MIF;
    private static String PAT;//计重
    private static String CICM;//卡机
    private static String print;//打印机
    private static String etc; //ETC卡读写器

    public static String getPrint() {
        return print;
    }

    public static void setPrint(String print) {
        SetCOMs.print = print;
    }
    
    public static String getCXP() {
        return CXP;
    }

    public static void setCXP(String CXP) {
        SetCOMs.CXP = CXP;
    }

    public static String getPAT() {
        return PAT;
    }

    public static void setPAT(String PAT) {
        SetCOMs.PAT = PAT;
    }

    public static String getCICM() {
        return CICM;
    }

    public static void setCICM(String CICM) {
        SetCOMs.CICM = CICM;
    }



    public static String getTFI() {
        return TFI;
    }

    public static String getKeyboard() {
        return Keyboard;
    }

    public static String getMIF() {
        return MIF;
    }

    public static String getEtc() {
        return etc;
    }

    static {
        Properties prop = new Properties();
        FileInputStream fis = null;
        try {
           fis = new FileInputStream(Constant.PROP_MTCLANE_COMM);
           prop.load(fis);
           PAT = prop.getProperty("PAT");   
           CXP = prop.getProperty("CXP");
           TFI = prop.getProperty("TFI");
           Keyboard = prop.getProperty("Keyboard");
           CICM = prop.getProperty("CICM");
           MIF = prop.getProperty("MIF");
            etc = prop.getProperty("ETC");
           print = prop.getProperty("Print");
        } catch (Exception e) {
           Logger.getLogger(SetCOMs.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(SetCOMs.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
}