package ui;

import java.awt.im.InputMethodRequests;
import javax.swing.JTextField;

/**
 *用来代替JTextField，解决JTextField与微软ABC输入法不兼容而导致虚拟机崩溃的问题
 * @author Wang Guodong
 */
public class MyTextField extends JTextField{
    @Override
        public InputMethodRequests getInputMethodRequests() {
            return null;
        }
}
