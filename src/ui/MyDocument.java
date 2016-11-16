package ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author WangGuodong
 */
public class MyDocument extends PlainDocument {

    int maxLength = 32;
    String regex;

    public MyDocument(int newMaxLength, String regex) {
        super();
        this.maxLength = newMaxLength;
        this.regex = regex;
    }

    public MyDocument() {
        this(10, null);
    }

    //重载父类的insertString函数 
    @Override
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        if (regex != null) {
            if (!str.matches(regex)) {//正则表达式规则
                return;
            }
        }
        if (getLength() + str.length() > maxLength) {//这里假定你的限制长度为10 
            return;
        } else {
            super.insertString(offset, str, a);

        }
    }
}
