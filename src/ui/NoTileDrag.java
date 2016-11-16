package ui;


import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class NoTileDrag {

    private static Point origin = new Point();
    private static boolean isDraging = false;

    public static void setCanDraged(final Component currentFrame) {
        currentFrame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isDraging = true;
                origin.x = e.getX();
                origin.y = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDraging = false;
            }
        });
        currentFrame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDraging) {
                    Point p = currentFrame.getLocation();
                    currentFrame.setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
                }
            }
        });
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(400, 300);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        JLabel label = new JLabel("test");
        label.setSize(30, 30);
        label.setBackground(Color.red);
        frame.add(label);
        NoTileDrag.setCanDraged(label);
    }
}
