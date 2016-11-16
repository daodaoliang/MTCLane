package ui.myJLabel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

/**
 * 到达线圈的标签
 *
 * @author Wang Guodong
 */
public class ArriveCoilJLabel extends JLabel {

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d .setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.black);
        int[] xbuffer = new int[4];
        int[] ybuffer = new int[4];
        xbuffer[0] = 58;
        xbuffer[1] = getWidth() - 17;
        xbuffer[2] = getWidth() - 58;
        xbuffer[3] = 17;
        ybuffer[0] = 2;
        ybuffer[1] = 2;
        ybuffer[2] = getHeight() - 2;
        ybuffer[3] = getHeight() - 2;
        g2d.setStroke(new BasicStroke(3));//粗细
        if (active) {
            g2d.fillPolygon(xbuffer, ybuffer, 4);
        } else {
            g2d.drawPolygon(xbuffer, ybuffer, 4);
        }
        g2d.dispose();
    }
    /**
     * 线圈激活标识
     */
    private boolean active;

    /**
     * 激活线圈
     */
    public void activateCoil() {
        active = true;
        repaint();
    }

    /**
     * 不激活线圈
     */
    public void deactivateCoil() {
        active = false;
        repaint();
    }

}
