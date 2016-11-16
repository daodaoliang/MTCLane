package com.hgits.tool.driver;

import com.hgits.util.file.FileUtils;
import com.sun.jna.Library;
import com.sun.jna.Native;

import java.io.File;

/**
 *  调用车牌识别器的动态链接库的接口类，包含识别器：通讯端口初始化、关闭当前打开的通讯端口、发送一个触发命令以抓拍一张图像并对其进行处理、
 * 检查LPR是否处于准备工作状态、 获得最后一次图像处理的时刻、获取可用的车牌号码、获取应处理的车牌照号及捕捉到的图像、获取车牌颜色、
 * 获得最新的一张图象以进行处理。
 * 
 * @author lq
 */
public interface HvInterface extends Library{
    
    String dllName = HvLPRNew.lprDLL;
    HvInterface INSTANCE = (HvInterface) Native.loadLibrary(FileUtils.getRootPath()+File.separator+dllName, HvInterface.class);
   
     /**
     * 开始对通讯端口初始化
     *
     * @param nPort 通讯端口 如果通讯连接方式为RS232(串口)，通讯端口可设置为1到18
     * 如果通讯连接方式为以太网，则通讯段口号为配置文件中ip地址对应的摄像机编号
     * @param plConfigFileName 配置文件的路径,若不需要则为null
     * @return 返回值大于0 通讯端口打开； 返回值小于等于0 通讯端口不能打开；
     */
    public int LPR_InitPortComm(int nPort, String plConfigFileName);
    
   
    /**
     * 发送一个触发命令以抓拍一张图像并对其进行处理
     *
     * @param nPort 通讯端口
     * @return 返回值大于0 捕捉到图像； 返回值=0 系统忙碌； 返回值〈0 其他错误；
     */
    public int LPR_CaptureImage(int nPort);

    /**
     * 关闭当前打开的通讯端口
     *
     * @param nPort 通讯端口
     * @return 返回值大于0 通讯端口关闭； 返回值小于等于0 任何其他错误；
     */
    public int LPR_ClosePortComm(int nPort);

    /**
     * 在系统初始化后或在任意时刻检查LPR是否处于准备工作状态，连接是否正确
     *
     * @param nPort 通讯端口
     * @return 返回值大于0 车牌识别设备处于准备工作状态且连接正确； 返回值=0 车牌识别设备仍处于初始化状态（功能不可用）； 返回值〈0 车牌识别设备不能运行；
     */
    public int LPR_IsReady(int nPort);

    /**
     * 获得最后一次图像处理的时刻
     *
     * @param nPort 通讯端口
     * @return 返回值大于0 获得最后一次图像处理结束的时刻（为自1970/1/1本地时间累计秒数）； 返回值=0 没有执行图像处理，无捕捉图像； 返回值〈0 其他错误；
     */
    public long LPR_LastTimePlate(int nPort);

    /**
     * 获取可用的车牌号码
     *
     * @param nPort 通讯端口
     * @param lpPlateNumber 车牌号码
     * @param PlateBufferLength 车牌号码对应的长度
     * @return 返回值大于0 被识别的字符数； 返回值=0 没用进行处理或没有捕捉到图像或车牌照长度为0； 返回值〈0 其他错误；
     */
    public int LPR_GetPlate(int nPort, byte[] lpPlateNumber, int PlateBufferLength);

    /**
     * 获得最新的一张图象以进行处理（尽管处理可能失败）
     *
     * @param nPort 通讯端口
     * @param lpJpegPicture 获得图像
     * @param PictureBufferLength 图像的缓冲
     * @param lpPictureLength 实际获得图像的长度
     * @return 返回值大于0 图像尺寸； 返回值=0 无图像可用； 返回值〈0 其他错误；
     */
    public int LPR_GetPicture(int nPort, byte[] lpJpegPicture, int PictureBufferLength, int[] lpPictureLength);

    /**
     * 获取应处理的车牌照号及捕捉到的图像（尽管处理有可能失败）
     *
     * @param nPort 通讯端口
     * @param lpPlateNumber 车牌号码
     * @param PlateBufferLength 车牌号码缓冲区大小
     * @param lpPlateLegnth 车牌长度
     * @param lpJpegPicture 捕捉到的图像
     * @param PictureBufferLength 捕捉到图像的缓冲区大小
     * @param lpPictureLength 捕捉到图像的长度
     * @return 返回值大于0 处理成功 ；（=1 只有车牌照号可用， =2 只有图像可用， =3 车牌照号和图像均可用）； 返回值=0 获取失败（车牌照号和图像均不可用）；
     * 返回值〈0 其他错误；
     */
    public int LPR_GetPlatePicture(int nPort, byte[] lpPlateNumber, int PlateBufferLength,
            int[] lpPlateLegnth, byte[] lpJpegPicture, int PictureBufferLength, int[] lpPictureLength);

    /**
     * 获取车牌颜色
     *
     * @param nPort 通讯端口
     * @param lpPlateColor 车牌颜色（代码长度为1字节，可以是以下数值） _:无颜色 W:白 Y:黄 U:蓝 B:黑 R:红 O:橙； G:绿
     * @return 返回值大于0 识别出车牌颜色； 返回值=0 没有进行识别/没有抓拍图片/没有识别出车牌颜色； 返回值〈0 其他错误；
     */
    public int LPR_GetPlateColor(int nPort, byte[] lpPlateColor);
}
