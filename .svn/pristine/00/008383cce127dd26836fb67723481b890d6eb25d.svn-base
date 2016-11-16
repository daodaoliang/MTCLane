/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import com.hgits.control.FontControl;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;

/**
 *
 * @author Wangguodong
 */
public class RunningModeJFrame extends JFrame {

    private RunningModeJFrame() {
        init();
    }

    private static RunningModeJFrame runningModeJFrame;

    /**
     * 获取唯一实例化对象
     *
     * @return 唯一实例化对象
     */
    public static synchronized RunningModeJFrame getSingleInstance() {
        if (runningModeJFrame == null) {
            runningModeJFrame = new RunningModeJFrame();
        }
        return runningModeJFrame;
    }
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;

    private void init() {
        this.setUndecorated(true);
        this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        Container p = (Container) this.getRootPane().getComponent(1);
        Container p1 = (Container) p.getComponent(1);
        Component[] comps = p1.getComponents();
        for (int i = 0; i < comps.length; i++) {
            p1.remove(i);
        }
        this.setTitle("选择发卡模式");
        this.setSize(600, 500);
        this.setLocationRelativeTo(null);
        Font font = FontControl.getMyFont(24);
        label1 = new JLabel("请按数字键选择相应发卡模式");
        label1.setSize(200, 500);
        label1.setFont(font);
        label1.setForeground(Color.black);

        label2 = new JLabel("1 - 自助发卡");
        label2.setSize(200, 500);
        label2.setFont(font);
        label2.setForeground(Color.black);

        label3 = new JLabel("(自助发卡需要将天线切换至自助卡机内天线)");
        label3.setSize(200, 500);
        label3.setFont(font);
        label3.setForeground(Color.black);
        label4 = new JLabel("2 - 人工发卡");
        label4.setSize(200, 500);
        label4.setFont(font);
        label4.setForeground(Color.black);
        label5 = new JLabel("(人工发卡需要将天线切换至收费亭内天线)");
        label5.setSize(200, 500);
        label5.setFont(font);
        label5.setForeground(Color.black);

        this.setLayout(new GridLayout(5, 1));
        this.getContentPane().setBackground(Color.white);
        this.getContentPane().add(label1);
        this.getContentPane().add(label2);
        this.getContentPane().add(label3);
        this.getContentPane().add(label4);
        this.getContentPane().add(label5);
        this.setAlwaysOnTop(true);
    }

    /**
     * 初始化字体
     */
    public void initFont() {
        Font font24 = FontControl.getMyFont(24);
        label1.setFont(font24);
        label2.setFont(font24);
        label3.setFont(font24);
        label4.setFont(font24);
        label5.setFont(font24);

    }
}
