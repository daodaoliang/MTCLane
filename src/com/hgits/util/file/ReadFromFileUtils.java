package com.hgits.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hgits.exception.MTCException;

public class ReadFromFileUtils {

	private static Logger logger = Logger.getLogger(ReadFromFileUtils.class);

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 *
	 * @param fileName
	 *            文件名
	 * @throws MTCException
	 */
	public static List<String> readFileByLinesToListSkipTitleList(File file,String encode)
			throws MTCException {
		List<String> tempList = readFileByLinesToList(file, true, -1,encode);
		return tempList;
	}

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 *
	 * @param fileName
	 *            文件名
	 * @param skipTitle
	 *            是否跳过表头
	 * @param row
	 *            读取多少行
	 *            @param encode 编码格式
	 * @throws MTCException
	 */
	public static List<String> readFileByLinesToList(File file,
			boolean skipTitle, int row,String encode) throws MTCException {
		List<String> tempList = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			// logger.info("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), encode));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				if (row == -1) {
					tempList.add(tempString);
					
				} else {
					row--;
					tempList.add(tempString);
					if (row == 0) {
						break;
					}
				}
			}
			if (skipTitle) {
				if(!tempList.isEmpty()){
					tempList.remove(0); // 移出首行
				}
			}
			reader.close();
		} catch (IOException e) {
			throw new MTCException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
//			System.gc();
		}
		return tempList;
	}
	

	/**
	 * 随机读取文件内容
	 *
	 * @param fileName
	 *            文件名
	 */
	public static void readFileByRandomAccess(String fileName) {
		RandomAccessFile randomFile = null;
		try {
			// logger.info("随机读取一段文件内容：");
			// 打开一个随机访问文件流，按只读方式
			randomFile = new RandomAccessFile(fileName, "r");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 读文件的起始位置
			int beginIndex = (fileLength > 4) ? 4 : 0;
			// 将读文件的开始位置移到beginIndex位置。
			randomFile.seek(beginIndex);
			byte[] bytes = new byte[10];
			int byteread = 0;
			// 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
			// 将一次读取的字节数赋给byteread
			while ((byteread = randomFile.read(bytes)) != -1) {
				System.out.write(bytes, 0, byteread);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * 显示输入流中还剩的字节数
	 *
	 * @param in
	 */
	private static void showAvailableBytes(InputStream in) {
		try {
			logger.info("当前字节输入流中的字节数为:" + in.available());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			String fileName = "c:\\tb_VehRoute1.txt";
			// ReadFromFile.readFileByBytes(fileName);
			// ReadFromFile.readFileByChars(fileName);
			// ReadFromFile.readFileByLines(fileName);
			// ReadFromFile.readFileByRandomAccess(fileName);
			List<String> list = ReadFromFileUtils
					.readFileByLinesToList(new File(fileName),true,2,FileUtils.encode());
			for (String string : list) {
//				System.out.println(string);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}