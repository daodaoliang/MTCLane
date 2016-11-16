package unique;

import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javax.swing.JOptionPane;

/**
 * 唯一性校验（软件只能启动一次）
 *
 * @author Wang Guodong
 */
public class UniqueCheck {

    /**
     * 软件唯一性校验，采用FileChannel文件锁以及socket通信双重确认
     *
     * @param software 软件名称
     * @return true 正常 false已启动
     */
    public static boolean check(String software) {
        boolean flag = false;
        try {
            String socketId = MyPropertiesUtils.getProperties(Constant.PROP_SOCKET, software + "UniqueSocket", "55555");
            JUnique.acquireLock(socketId);
            flag = true;
        } catch (AlreadyLockedException ex) {
            JOptionPane.showMessageDialog(null, software + "软件已启动，请勿重新启动");
        }
        return flag;
    }
}
