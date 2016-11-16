package com.hgits.service;

import com.hgits.util.DoubleUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * 用于获取电脑信息的服务类
 *
 * @author WangGuodong
 */
public class PcService {

    /**
     * 获取CPU型号
     *
     * @return CPU型号
     */
    public String getCpuType() {
        Set<String> set = new HashSet();
        try {
            Sigar sigar = new Sigar();
            CpuInfo infos[] = sigar.getCpuInfoList();
            CpuPerc cpuList[] = null;
            cpuList = sigar.getCpuPercList();
            for (int i = 0; i < infos.length; i++) {// 不管是单块CPU还是多CPU都适用
                CpuInfo info = infos[i];
                String cpuModel = info.getModel();//获取CPU型号
                set.add(cpuModel);
            }
        } catch (Exception ex) {
            Logger.getLogger(PcService.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringBuilder sb = new StringBuilder();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String model = (String) it.next();
            sb.append(model).append("/");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    /**
     * 获取CPU使用率，不带小数点，如50%
     *
     * @return CPU使用率
     */
    public String getCpuUse() {
        StringBuilder sb = new StringBuilder();
        Sigar sigar = new Sigar();
        try {
            CpuPerc cp = sigar.getCpuPerc();
            double d = cp.getCombined();//获取CPU总的使用率
            sb.append(CpuPerc.format(d));
        } catch (SigarException ex) {
            Logger.getLogger(PcService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }

    /**
     * 获取剩余内存/总内存(单位：G)，如3.5/8
     *
     * @return 剩余内存/总内存(单位：G)
     */
    public String getMemoryUse() {
        StringBuilder sb = new StringBuilder();
        try {
            Sigar sigar = new Sigar();
            Mem mem = sigar.getMem();
            // 内存总量，单位byte
            long total = mem.getTotal();
            //内存剩余量，单位byte
            long free = mem.getFree();
            //获取内存总量，单位GB
            double totalG = DoubleUtils.div(total, 1024 * 1024 * 1024, 1);
            //获取内存剩余量，单位GB
            double freeG = DoubleUtils.div(free, 1024 * 1024 * 1024, 1);
            sb.append(freeG).append("/").append(totalG);
        } catch (SigarException ex) {
            Logger.getLogger(PcService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }

    /**
     * 获取操作系统
     *
     * @return 操作系统
     */
    public String getOperatingSystem() {
        StringBuilder sb = new StringBuilder();
        OperatingSystem OS = OperatingSystem.getInstance();
        // 系统描述
//        String des = OS.getDescription();
        String des = OS.getVendorName();
        if (des != null) {
            sb.append(des);
        }
        // 操作系统内核类型如： 386、486、586等x86
//        String arch = OS.getArch();
//        if (arch != null) {
//            sb.append(arch);
//        }
        return sb.toString();
    }
    public static void main(String[] args) throws InterruptedException {
        PcService svc = new PcService();
        while(true){
            svc.getCpuUse();
            Thread.sleep(10);
        }
        
    }
    
    
}
