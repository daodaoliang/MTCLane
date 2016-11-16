/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hgits.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.apache.log4j.Logger;

/**
 * 界面的工具类
 *
 * @author Administrator
 */
public class UIUtils {
	
	private static Logger logger = Logger.getLogger(UIUtils.class);

    /**
     * 移除最大最小和关闭按钮的方法
     *
     * @param frame
     */
    public static void removeMinMaxClose(JFrame frame) {
        frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG); // 设置为简单对话框风格
        Container p = (Container) frame.getRootPane().getComponent(1);
        Container p1 = (Container) p.getComponent(1);
        Component[] comps = p1.getComponents();
        for (int i = 0; i < comps.length; i++) {
            p1.remove(i);
        }
    }
    
	/**
	 * 统一设置字体，父界面设置之后，所有由父界面进入的子界面都不需要再次设置字体
	 * @param fontResource 界面字体
	 * @param dataFontResource 数据字体
	 */
    public static void InitGlobalFont(Font fontResource, Font dataFontResource) {
    	//设置界面字体
		FontUIResource fontRes = new FontUIResource(fontResource);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys
				.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
		
		FontUIResource dataFontRes = new FontUIResource(dataFontResource);
		//元素字体
		UIManager.put("Button.font", fontRes);
        UIManager.put("ToggleButton.font", fontRes);
        UIManager.put("RadioButton.font", fontRes);
        UIManager.put("CheckBox.font", fontRes);
        UIManager.put("ColorChooser.font", fontRes);
        UIManager.put("ToggleButton.font", fontRes);
        UIManager.put("ComboBox.font", fontRes);
        UIManager.put("ComboBoxItem.font", fontRes);
        UIManager.put("InternalFrame.titleFont", fontRes);
        UIManager.put("Label.font", fontRes);
        UIManager.put("List.font", dataFontRes);
        UIManager.put("MenuBar.font", fontRes);
        UIManager.put("Menu.font", fontRes);
        UIManager.put("MenuItem.font", fontRes);
        UIManager.put("RadioButtonMenuItem.font", fontRes);
        UIManager.put("CheckBoxMenuItem.font", fontRes);
        UIManager.put("PopupMenu.font", fontRes);
        UIManager.put("OptionPane.font", fontRes);
        UIManager.put("Panel.font", fontRes);
        UIManager.put("ProgressBar.font", fontRes);
        UIManager.put("ScrollPane.font", fontRes);
        UIManager.put("Viewport", fontRes);
        UIManager.put("TabbedPane.font", fontRes);
        UIManager.put("TableHeader.font", fontRes);
        UIManager.put("TextField.font", dataFontRes);
        UIManager.put("PasswordFiled.font", dataFontRes);
        UIManager.put("TextArea.font", dataFontRes);
        UIManager.put("TextPane.font", dataFontRes);
        UIManager.put("EditorPane.font", fontRes);
        UIManager.put("TitledBorder.font", fontRes);
        UIManager.put("ToolBar.font", fontRes);
        UIManager.put("ToolTip.font", fontRes);
        UIManager.put("Tree.font", dataFontRes);
        UIManager.put("TabbedPane.font", dataFontRes);
        UIManager.put("ComboBox.font", dataFontRes);
        UIManager.put("ProgressBar.repaintInterval", new Integer(150));
        UIManager.put("ProgressBar.cycleTime", new Integer(1050));
	}
    
}
