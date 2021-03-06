package ui;

import com.hgits.control.FontControl;
import com.hgits.util.UIUtils;
import java.awt.Color;
import java.awt.Font;

/**
 * 入口信息输入界面
 *
 * @author Wang Guodong
 */
public class EntMsgJFrame extends javax.swing.JFrame {

    /**
     * Creates new form EntMsgJFrame
     */
    private EntMsgJFrame() {
        UIUtils.removeMinMaxClose(this);//删除最大化，最小化，关闭按钮
        initComponents();
        init();
    }

    private static EntMsgJFrame entMsgJFrame;

    /**
     * 获取唯一实例化对象
     *
     * @return 唯一实例化对象
     */
    public static synchronized EntMsgJFrame getSingleInstance() {
        if (entMsgJFrame == null) {
            entMsgJFrame = new EntMsgJFrame();
        }
        return entMsgJFrame;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cardNum1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cardNum2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        entId = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("请输入信息");
        setAlwaysOnTop(true);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(204, 250, 189));
        jPanel1.setPreferredSize(new java.awt.Dimension(400, 300));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "入口数据", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, FontControl.getMyFont(24)));
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(380, 180));
        jPanel2.setLayout(new java.awt.GridLayout(3, 2));

        jLabel1.setFont(new java.awt.Font("微软雅黑", 1, 24)); // NOI18N
        jLabel1.setText("通行卡序列号");
        jPanel2.add(jLabel1);

        cardNum1.setBackground(new java.awt.Color(255, 255, 255));
        cardNum1.setFont(new java.awt.Font("微软雅黑", 0, 24)); // NOI18N
        cardNum1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cardNum1.setOpaque(true);
        jPanel2.add(cardNum1);

        jLabel2.setFont(new java.awt.Font("微软雅黑", 1, 24)); // NOI18N
        jLabel2.setText("重输卡序列号");
        jPanel2.add(jLabel2);

        cardNum2.setBackground(new java.awt.Color(255, 255, 255));
        cardNum2.setFont(new java.awt.Font("微软雅黑", 0, 24)); // NOI18N
        cardNum2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cardNum2.setOpaque(true);
        jPanel2.add(cardNum2);

        jLabel3.setFont(new java.awt.Font("微软雅黑", 1, 24)); // NOI18N
        jLabel3.setText("入口站号");
        jPanel2.add(jLabel3);

        entId.setBackground(new java.awt.Color(255, 255, 255));
        entId.setFont(new java.awt.Font("微软雅黑", 0, 24)); // NOI18N
        entId.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        entId.setOpaque(true);
        jPanel2.add(entId);

        jButton1.setFont(new java.awt.Font("微软雅黑", 0, 24)); // NOI18N
        jButton1.setText("确认");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(142, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(141, 141, 141))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EntMsgJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EntMsgJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EntMsgJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EntMsgJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EntMsgJFrame().setVisible(true);
            }
        });
    }
    //更新序列号

    public void updateNum1(String num) {
        cardNum1.setText(num);
    }
    //更新序列号2

    public void updateNum2(String num) {
        cardNum2.setText(num);
    }
    //更新入口站号

    public void updateEntNum(String entNum) {
        entId.setText(entNum);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cardNum1;
    private javax.swing.JLabel cardNum2;
    private javax.swing.JLabel entId;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables

    private void init() {
//        this.setBounds(0, 0, 284, 175);
        this.setLocationRelativeTo(null);
    }

    public void showFrame() {
        updateEntNum("");
        updateNum1("");
        updateNum2("");
        this.setVisible(true);
    }

    /**
     * 初始化字体
     */
    public void initFont() {
        Font font24 = FontControl.getMyFont(24);
        jLabel1.setFont(font24);
        jLabel1.setForeground(Color.black);
        jLabel2.setFont(font24);
        jLabel2.setForeground(Color.black);
        jLabel3.setFont(font24);
        jLabel3.setForeground(Color.black);

        cardNum1.setFont(font24);
        cardNum1.setForeground(Color.black);
        cardNum2.setFont(font24);
        cardNum2.setForeground(Color.black);
        entId.setFont(font24);
        entId.setForeground(Color.black);
        jButton1.setFont(font24);
        jButton1.setForeground(Color.black);
    }

}
