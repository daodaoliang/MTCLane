package ui.myJLabel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

/**
 * 用于创建自定义光标的标签
 *
 * @author Wang Guodong
 */
public class CursorJLabel extends JLabel {

    public CursorJLabel(int width, int height) {
        setSize(width, height);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d .setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.red);
        int x = getWidth();
        int y = getHeight();
        int x1 = x / 2;
        int y1 = 0;
        int x2 = 0;
        int y2 = y / 4;
        int x3 = 0;
        int y3 = y * 3 / 4;
        int x4 = x / 2;
        int y4 = y;
        int x5 = x;
        int y5 = y * 3 / 4;
        int x6 = x;
        int y6 = y / 4;
        int[] xBuffer = new int[6];
        int[] yBuffer = new int[6];
        xBuffer[0] = x1;
        xBuffer[1] = x2;
        xBuffer[2] = x3;
        xBuffer[3] = x4;
        xBuffer[4] = x5;
        xBuffer[5] = x6;

        yBuffer[0] = y1;
        yBuffer[1] = y2;
        yBuffer[2] = y3;
        yBuffer[3] = y4;
        yBuffer[4] = y5;
        yBuffer[5] = y6;
        g2d.fillPolygon(xBuffer, yBuffer, 6);
        g2d.dispose();
    }
}
