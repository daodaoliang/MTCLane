#pragma once


#ifdef LPRAPI

#else
	#define LPRAPI extern "C" __declspec(dllimport)
#endif



#define LPR_RESULT_OK 1//>0			function successful 功能成功
#define LPR_ONLY_IMAGE 2
#define LPR_IMAGE_AND_PLATE 3
#define LPR_NO_FUNCTION 0//=0			function failedunavailable 功能不可用
//<0			Non functional failure(error codes) 功能失败(错误代码)error：
#define LPR_NO_INI -1//		Not initialised没有初始化
#define LPR_NET_ERROR -2//		Communication error (cable disconnected) 通讯错误(缆线断开)
#define LPR_PARAMS_ERROR -3 //		Bad parameters参数错误
#define LPR_DEVICE_ERROR -4
#define LPR_SOCKET_NULL -5
#define LPR_DATA_THREAD_FAIL -6
#define LPR_CAPTURE_OUT -7
#define LPR_CAPTURE_FAIL -8
#define LPR_UDPTHREAD_FAIL -9
#define LPR_UDPSCAN_FAIL -10
#define LPR_CONNET_TIMEOUT 5
#define LPR_GETDATA_TIMEOUT 1500


#define HWY_VI  1//标清模块化
#define HW_IHD200  2//IHD设备
#define HWY_VI_P  3//智通慧眼
#define TC_SERIAL  4//TC智能相机



//接口函数声明
/*******************************************************************
-	Function name函数名称: LPR_InitPortComm

-	Description描述：Start the initialisation of the port communication
开始对通讯端口初始化

-	Parameters参数：
?	UINT nPort;
?	char *plConfigFileName

-	Return value返回值：
?	>0 Port opened通讯端口打开
?	<=0 Port number cannot be opened通讯端口不能打开

********************************************************************/

LPRAPI int WINAPI LPR_InitPortComm(UINT nPort,char *plConfigFileName);

/*******************************************************************
-	Function name 函数名称: LPR_ClosePortComm

-	Description描述: Close the port communication previously opened
关闭以前打开的通讯端口

-	Parameters 参数:
	UINT nPort

-	Return value返回值:
	>0 Port closed通讯端口关闭
	<=0 Any errors 任何其它错误


********************************************************************/

LPRAPI int WINAPI LPR_ClosePortComm(UINT nPort);

/*******************************************************************
-	Function name函数名称：LPR_IsReady

-	Description描述：Check after the initialisation or at any time, if the LPR system is ready and correctly connected
在系统完成初始化后或在任一时刻检查LPR是否处于准备工作状态，连接是否正确。

-	Parameters参数：
	UINT nPort

-	Return value返回值：
	>0 LPR system is ready and connected
车牌识别设备处于准备工作状态且连接正确
	=0 LPR system still in initialisation status (unavailable) 
车牌识别设备仍处于初始化状态(功能不可用)
	<0 LPR system cannot operate
车牌识别设备不能运行


********************************************************************/

LPRAPI  int WINAPI LPR_IsReady(UINT nPort) ;



/*********************************************************************
-	Function name函数名称：LPR_CaptureImage

-	Description描述：Send a trigger command to take a picture and analyse process it
发送一个触发命令以抓拍一张图像并对其进行处理

-	Parameters参数：
	UINT nPort

-	Return value返回值：
	>0 Picture captured捕捉到图像
	=0 LPR system busy系统忙碌
	<0 other errors其它错误

*********************************************************************/

LPRAPI int WINAPI LPR_CaptureImage (UINT nPort);

/*********************************************************************
-	Function name函数名称： LPR_LastTimePlate

-	Description 描述： Get the stamp of the latest analysepicture processing
获取最后一次图像处理结束的时刻

-	Parameters参数：
	UINT nPort

-	Return value返回值：
	>0 Time of latest analyse processing (“time_t”: number of second from 1/1/1970 local time)
获取最后一次图像处理结束的时刻(“time_t”为自1/1/1970本地时间累计秒数)
	=0 No processing performed, no picture are is taken
没有执行图像处理, 无捕捉图像
	<0 other errors其它错误

Remarks注意:

During a new analyseprocessing, the function must return the time of the previous analyseprocessing.
当一个新的处理过程开始时， 本函数必须返回前一次处理的结束时间。

*********************************************************************/

LPRAPI int WINAPI LPR_LastTimePlate (UINT nPort);

/*********************************************************************
-	Function name函数名称：LPR_GetPlate

-	Description描述：Get the available plate number获取可用的车牌照号

-	Parameters 参数：
	UINT nPort;
	BYTE *lpPlateNumber;
	UINT PlateBufferLength

-	Return value返回值：
	>0 Number of characters recognized被识别的字符数
	=0 No analyse processing performed or no picture are taken or plate number length is 0
没有进行处理或没有捕捉到图像或车牌照长度为0
	<0 other errors 其它错误

Remarks注意:

If the plate number length is  highergreater than the buffer length, the returned value is the real one, and the buffer is filled with the first characters of plate number. (Plate “L9P0R4” BufferLength=3 --> returned value is 6, Buffer=”L9P”)
如果车牌号的长度大于缓冲区的长度, 则返回值为真实值, 缓冲区由车牌号的前几位字符充满。 (例如车牌号为: L9P0R4, 缓冲区长度BufferLength为3, 返回值为6, 缓冲区为”L9P”)

During a new analyseprocessing, the function must return the time of the previous analyseprocessing.
当一个新的处理过程开始时，本函数必须返回前一次处理结束的时间。

*********************************************************************/

LPRAPI int WINAPI LPR_GetPlate(UINT nPort,BYTE *lpPlateNumber,UINT PlateBufferLength);

/*********************************************************************
-	Function name函数名称: LPR_GetPicture

-	Description描述: Get the latest picture taken for analyse processing (even if the analyse processing failedfails) 获得最新的一张图像以进行处理(尽管处理可能失败)

-	Parameters参数:
	UINT nPort;
	BYTE *lpJpegPicture;
	UINT PictureBufferLength;
	UINT *lpPictureLength;

-	Return value返回值:
	>0 Size of the picture图像尺寸
	=0 No picture are available无图像可采用
	<0 other errors其它错误

Remarks注意:

If the DLL.dll programme cannot transfer the picture file, the function must return the code 0. 
如果.dll程序不能传输图像文件，函数应返回0代码。

During a new processinganalyse or during a new file transfer, the function must return the code 0. 
当一个新的处理过程开始或当一个新的文件开始传输时, 函数的返回值为0代码。

If the picture size is greater than the buffer size, the value *lpPictureLength is equal to 0, the buffer is not filled but the returned value is still the picture size. In other successful cases *lpPictureLength is equal to the returned value.

如果图像的大小超过缓冲区的尺寸,  函数*lpPictureLength等于0, 缓冲区未被填充, 不过返回值仍为图像的大小。对其它成功的处理来说, *lpPictureLength与返回值相等。

*********************************************************************/

LPRAPI int WINAPI LPR_GetPicture (UINT nPort,BYTE *lpJpegPicture,UINT PictureBufferLength,UINT *lpPictureLength);

/*********************************************************************
-	Function name函数名称: LPR_GetPlatePicture

-	Description描述: Get the Plate number and picture taken for processinganalyse (even if the processinganalyse failsed) 
获取应处理的车牌照号及捕捉到的图像 (尽管处理有可能失败)

-	Parameters参数:
	UINT nPort;
	BYTE *lpPlateNumber;
	UINT PlateBufferLength;
	UINT *lpPlateLength;
	BYTE *lpJpegPicture;
	UINT PictureBufferLength;
	UINT *lpPictureLength

-	Return value返回值:
	>0 Success处理成功: 
=1 only plate available
只有车牌照号可用
=2 only picture available
只有图像可用
=3 both are available
车牌照号和图像均可用
	=0 Nothing are is available
获取失败（车牌照号和图像均不可用）
	<0 other errors其它错误

*********************************************************************/

LPRAPI int WINAPI LPR_GetPlatePicture (UINT nPort,BYTE *lpPlateNumber,UINT PlateBufferLength,UINT *lpPlateLength,
									   BYTE *lpJpegPicture,	UINT PictureBufferLength,UINT *lpPictureLength);

/****************************************************************
Function name函数名称: LPR_GetPlateColor

-	Description描述: 获取车牌颜色

-	Parameters参数:
	UINT nPort;
	BYTE *lpPlateColor;

-	Return value返回值:
	>0 Success处理成功: 
****************************************************************/
LPRAPI int WINAPI LPR_GetPlateColor(UINT nPort,BYTE *lpPlateColor);