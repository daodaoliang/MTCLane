package com.hgits.control;

import com.hgits.util.MyPropertiesUtils;
import com.hgits.vo.Constant;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * 字体控制
 *
 * @author Wang Guodong
 */
public class FontControl {

    private static final String defaultTtfFilePath = "ttf/my.ttf";
    private static final String defaultFont = "宋体";
    private static final Logger logger = LogManager.getLogger(FontControl.class);
    private static String ttfPath;
    private static final Map<String, Font> fontMap = new HashMap();

    static {
        ttfPath = MyPropertiesUtils.getProperties(Constant.PROP_MTCLANE, "ttfPath", defaultTtfFilePath);
    }

    /**
     * 获取自带字体(若无自带字体，返回黑体)
     *
     * @param size 字体大小
     * @return 自带字体
     */
    public static Font getMyFont(int size) {
        return getMyFont(size, Font.BOLD);
    }

    /**
     * 获取自带字体(若无自带字体，返回黑体)
     *
     * @param size 字体大小
     * @param style 字体样式
     * @return 自带字体
     */
    public static Font getMyFont(int size, int style) {
        StringBuilder sb = new StringBuilder();
        sb.append(ttfPath);
        sb.append("size=").append(size).append("style=").append(style);
        if (fontMap.get(sb.toString()) != null) {
            return fontMap.get(sb.toString());
        }
        Font myFont = null;
        try {
            File file = new File(ttfPath);
            if (!file.exists() || file.isHidden() || !file.isFile()) {
                myFont = new Font(defaultFont, style, size);
            } else {
                myFont = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(style, size);
            }
        } catch (FileNotFoundException ex) {
            logger.error(ex, ex);
            myFont = new Font(defaultFont, style, size);
        } catch (FontFormatException | IOException ex) {
            logger.error(ex, ex);
            myFont = new Font(defaultFont, style, size);
        } finally {
        }
        fontMap.put(sb.toString(), myFont);
        return myFont;
    }

    /**
     * 设置字体
     *
     * @param ttfPath
     */
    public static void setTtfPath(String ttfPath) {
        FontControl.ttfPath = ttfPath;
    }

}
