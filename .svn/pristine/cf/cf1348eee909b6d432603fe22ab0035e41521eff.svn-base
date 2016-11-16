package com.hgits.control;

import com.hgits.task.MyTask;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.log4j.Logger;

/**
 * 语音播放控制
 *
 * @author Wang Guodong
 */
public class AudioControl {

    /**
     * 音频文件夹路径,默认为当前项目的下的audio文件夹，可通过setAudioPath方法修改
     */
    private final String audioPath = "audio";
    private boolean running = true;//运行标识
    private final ConcurrentLinkedQueue<File> audioQueue = new ConcurrentLinkedQueue<>();//需要播放的语音文件的队列
    private final ConcurrentLinkedQueue<String> fareQueue = new ConcurrentLinkedQueue<>();//需要播放的收费信息的队列

    private static final Logger logger = Logger.getLogger(AudioControl.class.getName());
    private final String audioPathHead = "audio/fare.wav";
    private final String audioPath0 = "audio/0.wav";
    private final String audioPath1 = "audio/1.WAV";
    private final String audioPath2 = "audio/2.WAV";
    private final String audioPath3 = "audio/3.WAV";
    private final String audioPath4 = "audio/4.WAV";
    private final String audioPath5 = "audio/5.WAV";
    private final String audioPath6 = "audio/6.WAV";
    private final String audioPath7 = "audio/7.WAV";
    private final String audioPath8 = "audio/8.WAV";
    private final String audioPath9 = "audio/9.WAV";
    private final String audioPath10 = "audio/10.WAV";
    private final String audioPath100 = "audio/100.WAV";
    private final String audioPath1000 = "audio/1000.WAV";
    private final String audioPathRMB = "audio/RMB.wav";
    private final String audioVehBack = "audio/VehBack.wav";
    private final String audiobyebye = "audio/byebye.wav";
    private final String audiowelcome = "audio/welcome.wav";
    private final String audioblackVeh = "audio/blackVeh.wav";
    private final String audiograyVeh = "audio/grayVeh.wav";

    private byte[] buffer;
    private byte[] buffer0;
    private byte[] buffer1;
    private byte[] buffer2;
    private byte[] buffer3;
    private byte[] buffer4;
    private byte[] buffer5;
    private byte[] buffer6;
    private byte[] buffer7;
    private byte[] buffer8;
    private byte[] buffer9;
    private byte[] buffer10;
    private byte[] buffer100;
    private byte[] buffer1000;
    private byte[] bufferRMB;
    private AudioFormat audioFormat;

    private static AudioControl audioControl;

    /**
     * 获取唯一实例
     *
     * @return 唯一实例
     */
    public static synchronized AudioControl getSingleInstance() {
        if (audioControl == null) {
            audioControl = new AudioControl();
        }
        return audioControl;
    }

    private AudioControl() {
        init();
        AudioTask audioTask = new AudioTask();
        ThreadPoolControl.getThreadPoolInstance().execute(audioTask);
        MonitorTask monitorTask = new MonitorTask(audioTask);
        ThreadPoolControl.getThreadPoolInstance().execute(monitorTask);
    }

    /**
     * 初始化,预加载用于语音保价的音频文件
     */
    private void init() {
        buffer = loadFile(audioPathHead);
        buffer0 = loadFile(audioPath0);
        buffer1 = loadFile(audioPath1);
        buffer2 = loadFile(audioPath2);
        buffer3 = loadFile(audioPath3);
        buffer4 = loadFile(audioPath4);
        buffer5 = loadFile(audioPath5);
        buffer6 = loadFile(audioPath6);
        buffer7 = loadFile(audioPath7);
        buffer8 = loadFile(audioPath8);
        buffer9 = loadFile(audioPath9);
        buffer10 = loadFile(audioPath10);
        buffer100 = loadFile(audioPath100);
        buffer1000 = loadFile(audioPath1000);
        bufferRMB = loadFile(audioPathRMB);
    }

    /**
     * 加载音频文件
     *
     * @param filePath 音频文件路径
     * @return 音频文件内容
     */
    private byte[] loadFile(String filePath) {
        AudioInputStream audioInputStream = null;
        byte[] tempBuffer1 = null;
        try {
            // 获取音频输入流
            audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
            audioFormat = audioInputStream.getFormat();
            byte[] buffer = new byte[1024];
            byte[] temp = new byte[1024 * 1024];
            int count;
            int len = 0;
            while ((count = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                System.arraycopy(buffer, 0, temp, len, count);
                len += count;
            }
            tempBuffer1 = new byte[len];
            System.arraycopy(temp, 0, tempBuffer1, 0, len);
        } catch (UnsupportedAudioFileException | IOException ex) {
            logger.error(ex, ex);
        } finally {
            if (audioInputStream != null) {
                try {
                    audioInputStream.close();
                } catch (IOException ex) {
                    logger.error(ex, ex);
                }
            }
        }
        return tempBuffer1;
    }

    /**
     * 播放指定的音频文件
     *
     * @param file 音频文件
     */
    private void playAudioFile(File file) {
        if (file == null || !file.exists() || !file.isFile() || file.isHidden()) {
            return;
        }
        AudioInputStream audioInputStream = null;
        try {
            // 获取音频输入流
            audioInputStream = AudioSystem
                    .getAudioInputStream(file);
            // 获取音频编码对象
            AudioFormat auFormat = audioInputStream.getFormat();
            // 设置数据输入
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,
                    auFormat, AudioSystem.NOT_SPECIFIED);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem
                    .getLine(dataLineInfo);
            sourceDataLine.open(auFormat);
            sourceDataLine.start();
            /*
             * 从输入流中读取数据发送到混音器
             */
            int count;
            byte tempBuffer[] = new byte[1024];
            while ((count = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                if (count > 0) {
                    sourceDataLine.write(tempBuffer, 0, count);
                }
            }   // 清空数据缓冲,并关闭输入
            sourceDataLine.drain();
            sourceDataLine.close();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            logger.error(ex, ex);
        } finally {
            try {
                if (audioInputStream != null) {
                    audioInputStream.close();
                }
            } catch (IOException ex) {
                logger.error(ex, ex);
            }
        }
    }

    /**
     * 将数字以音频方式播出 如1234，音频播出“一千二百三十四元”
     *
     * @param num 数字（0-9999）
     */
    public void playAudioFare(int num) {
        StringBuilder sb = new StringBuilder();
        int n1 = num / 1000;//千
        if (n1 > 9) {//本方法不支持万元以上数字
            return;
        }
        if (n1 > 0) {
            sb.append(n1).append("千");
        }
        int n2 = num / 100;//百
        n2 = n2 % 10;
        if (n2 > 0) {
            sb.append(n2).append("百");
        } else if (n2 == 0 && n1 != 0) {
            if (num % 100 != 0) {//十位个位数不为0时才播放0
                sb.append(0);
            }

        }
        int n3 = num / 10;//十
        n3 = n3 % 10;
        if (n3 > 0) {
            sb.append(n3).append("十");
        } else if (n3 == 0) {
            if (n1 == 0 && n2 == 0) {//千位，百位均为0

            } else if (n1 != 0 && n2 == 0) {//千位不为0，百位为0

            } else if (n1 == 0 && n2 != 0) {//千位为0，百位不为0
                if (num % 10 != 0) {//个位数不为0
                    sb.append(0);
                }
            } else if (n1 != 0 && n2 != 0) {//千位不为0，百位不为0
                if (num % 10 != 0) {//个位数不为0
                    sb.append(0);
                }
            }
        }
        int n4 = num % 10;//个位数
        if (n4 > 0) {
            sb.append(n4);
        }
        sb.append("元");
        fareQueue.add(sb.toString());

    }

    /**
     * 播放语音收费音频，如“一千二百三十四元”
     *
     * @param info 需要播放的音频，如“一千二百三十四元”
     */
    private void playAudioFare(String info) {
        try {
            // 设置数据输入
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            /*
             * 从输入流中读取数据发送到混音器
             */
            sourceDataLine.write(buffer, 0, buffer.length);
            for (int i = 0; i < info.length(); i++) {
                String str = info.substring(i, i + 1);
                switch (str) {
                    case "0":
                        sourceDataLine.write(buffer0, 0, buffer0.length);
                        break;
                    case "1":
                        sourceDataLine.write(buffer1, 0, buffer1.length);
                        break;
                    case "2":
                        sourceDataLine.write(buffer2, 0, buffer2.length);
                        break;
                    case "3":
                        sourceDataLine.write(buffer3, 0, buffer3.length);
                        break;
                    case "4":
                        sourceDataLine.write(buffer4, 0, buffer4.length);
                        break;
                    case "5":
                        sourceDataLine.write(buffer5, 0, buffer5.length);
                        break;
                    case "6":
                        sourceDataLine.write(buffer6, 0, buffer6.length);
                        break;
                    case "7":
                        sourceDataLine.write(buffer7, 0, buffer7.length);
                        break;
                    case "8":
                        sourceDataLine.write(buffer8, 0, buffer8.length);
                        break;
                    case "9":
                        sourceDataLine.write(buffer9, 0, buffer9.length);
                        break;
                    case "十":
                        sourceDataLine.write(buffer10, 0, buffer10.length);
                        break;
                    case "百":
                        sourceDataLine.write(buffer100, 0, buffer100.length);
                        break;
                    case "千":
                        sourceDataLine.write(buffer1000, 0, buffer1000.length);
                        break;
                    case "元":
                        sourceDataLine.write(bufferRMB, 0, bufferRMB.length);
                        break;
                    default:
                        break;
                }
            }
            // 清空数据缓冲,并关闭输入
            sourceDataLine.drain();
            sourceDataLine.close();
        } catch (Exception ex) {
            logger.error(ex, ex);
        } finally {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                logger.error(ex, ex);
            }
        }

    }

    /**
     * 播放语音请按键取卡
     */
    public void playAudioPressKey() {
        File file = new File(audioPath + "/pleasePressKey.wav");
        audioQueue.add(file);
    }

    /**
     * 播放语音请稍候
     */
    public void playAudioWait() {
        File file = new File(audioPath + "/pleaseWait.wav");
        audioQueue.add(file);
    }

    /**
     * 播放请取卡
     */
    public void playAudioTakeCard() {
        File file = new File(audioPath + "/pleaseTakeCard.wav");
        audioQueue.add(file);
    }

    /**
     * 播放语音谢谢，一路顺风
     */
    public void playAudioThanks() {
        File file = new File(audioPath + "/thanks.wav");
        audioQueue.add(file);
    }

    /**
     * 播放语音提示：车辆倒车，请注意
     */
    public void playAudioVehBack() {
        File file = new File(audioVehBack);
        audioQueue.add(file);
    }

    /**
     * 播放语音提示祝您一路顺风
     */
    public void playAudioByebye() {
        File file = new File(audiobyebye);
        audioQueue.add(file);
    }

    /**
     * 播放语音提示感谢您使用湖南高速公路
     */
    public void playAudioWelcome() {
        File file = new File(audiowelcome);
        audioQueue.add(file);
    }

    /**
     * 播放黑名单语音提示
     */
    public void playAudioBlackVeh() {
        File file = new File(audioblackVeh);
        audioQueue.add(file);
    }

    /**
     * 播放灰名单语音提示
     */
    public void playAudioGrayVeh() {
        File file = new File(audiograyVeh);
        audioQueue.add(file);
    }

    /**
     * 停止运行
     */
    public void stop() {
        running = false;
    }

    class AudioTask extends MyTask {

        @Override
        public void run() {
            logger.info("音频控制线程启动");
            setAlive(true);
            try {
                while (running) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                    }
                    File file = audioQueue.poll();
                    if (file != null && file.exists() && file.isFile() && !file.isHidden()) {
                        playAudioFile(file);//播放语音文件
                    }
                    String fareInfo = fareQueue.poll();
                    if (fareInfo != null) {
                        playAudioFare(fareInfo);//播放收费信息
                    }

                }
            } catch (Throwable t) {
                logger.error(t, t);
            }
            setAlive(false);
            logger.info("音频控制线程结束");
        }
    }

    class MonitorTask extends MyTask {

        private final AudioTask audioTask;

        public MonitorTask(AudioTask audioTask) {
            this.audioTask = audioTask;
        }

        @Override
        public void run() {
            logger.info("音频监控线程启动");
            while (running) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                if(audioQueue.size()>100){
                    audioQueue.clear();
                }
                if(fareQueue.size()>100){
                    fareQueue.clear();
                }
//                if (!audioTask.isAlive()) {
//                    ThreadPoolControl.getThreadPoolInstance().execute(audioTask);
//                }
            }
            logger.info("音频监控线程结束");
        }
    }

}
