package com.hgits.tool.driver;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.hgits.common.log.MTCLog;
import com.hgits.control.FunctionControl;
import com.hgits.hardware.CXP;
import com.hgits.service.KeyBoardService;
import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;
import javax.swing.JFrame;

/**
 * 电脑键盘监听类
 *
 * @author jelly
 */
public class MyKeyAdapter extends KeyAdapter {

    private CXP cxp;
    //电脑键盘启用标识 0标识不启用电脑键盘，1表示启用电脑键盘
    private int pcKeyboardCode = 0;

    private KeyBoardService keyBoardService;

    public void setKeyBoardService(KeyBoardService keyBoardService) {
        this.keyBoardService = keyBoardService;
    }

    public void setCxp(CXP cxp) {
        this.cxp = cxp;
    }

    public MyKeyAdapter() {
        try {
            if (FunctionControl.isPcKeyboardActive()) {
                pcKeyboardCode = 1;
                MTCLog.log("启用电脑键盘");
            } else {
                MTCLog.log("不启用电脑键盘");
            }
        } catch (Exception ex) {
            MTCLog.log("电脑键盘初始化异常", ex);
        }
    }
    private int key = -1;

    @Override
    public void keyPressed(KeyEvent e) {
        if (pcKeyboardCode == 1) {
        	 int tempKey = e.getKeyCode();
     		switch (tempKey) {
     			case KeyEvent.VK_NUMPAD0 :
     				tempKey = KeyEvent.VK_0;
     				break;
     			case KeyEvent.VK_NUMPAD1 :
     				tempKey = KeyEvent.VK_1;
     				break;
     			case KeyEvent.VK_NUMPAD2 :
     				tempKey = KeyEvent.VK_2;
     				break;
     			case KeyEvent.VK_NUMPAD3 :
     				tempKey = KeyEvent.VK_3;
     				break;
     			case KeyEvent.VK_NUMPAD4 :
     				tempKey = KeyEvent.VK_4;
     				break;
     			case KeyEvent.VK_NUMPAD5 :
     				tempKey = KeyEvent.VK_5;
     				break;
     			case KeyEvent.VK_NUMPAD6 :
     				tempKey = KeyEvent.VK_6;
     				break;
     			case KeyEvent.VK_NUMPAD7 :
     				tempKey = KeyEvent.VK_7;
     				break;
     			case KeyEvent.VK_NUMPAD8 :
     				tempKey = KeyEvent.VK_8;
     				break;
     			case KeyEvent.VK_NUMPAD9 :
     				tempKey = KeyEvent.VK_9;
     				break;
     			default :
     				break;
     		}
     		MTCLog.log("电脑键盘：" + tempKey+","+KeyEvent.getKeyText(tempKey));
            checkCXP(tempKey);
            //只有启用的电脑键盘，才截取操作
            String str = String.valueOf(tempKey);
            if (keyBoardService.specialHand(str)) {
                key = -1;
            } else {
                key = tempKey;
            }
        }
    }

    public String getKey() {
        if (pcKeyboardCode != 1) {
            return null;
        }
        if (key == -1) {
            return null;
        } else {
            String str = String.valueOf(key);
            key = -1;
            return str;
        }
    }

    private void checkCXP(int key) {
        if (cxp == null) {
            return;
        }
        String str = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE_TEST, "coilTestCode", "1");
		if ("0".equals(str)) {
			switch (key) {
				case KeyEvent.VK_MINUS :
					cxp.setArrriveCoil(true);
					break;
				case KeyEvent.VK_EQUALS :
					cxp.setArrriveCoil(false);
					break;
				case KeyEvent.VK_OPEN_BRACKET :
					cxp.setPassageCoil(true);
					break;
				case KeyEvent.VK_CLOSE_BRACKET :
					cxp.setPassageCoil(false);
					break;
				default :
					break;
			}
			// if (key == KeyEvent.VK_MINUS) {
			// cxp.setArrriveCoil(true);
			// }else if(key==KeyEvent.VK_EQUALS){
			// cxp.setArrriveCoil(false);
			// }else if(key==KeyEvent.VK_OPEN_BRACKET){
			// cxp.setPassageCoil(true);
			// }else if(key==KeyEvent.VK_CLOSE_BRACKET){
			// cxp.setPassageCoil(false);
			// }

		}
    }
    public static void main(String[] args) {
    }
}
