package org.tizzer.smmgr.system.util;

import com.alee.laf.WebLookAndFeel;
import org.tizzer.smmgr.system.manager.FontManager;

import javax.swing.plaf.FontUIResource;

/**
 * @author tizzer
 * @version 1.0
 */
public class LafUtil {
    /**
     * 设置weblaf外观
     */
    public static void setWebLaF() {
        //关闭weblaf文字抗锯齿
        //StyleConstants.defaultTextRenderingHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        //StyleConstants.textRenderingHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        WebLookAndFeel.globalControlFont = new FontUIResource(FontManager._FONT_DEFAULT);
        WebLookAndFeel.globalTooltipFont = new FontUIResource(FontManager._FONT_DEFAULT);
        WebLookAndFeel.globalAlertFont = new FontUIResource(FontManager._FONT_DEFAULT);
        WebLookAndFeel.globalMenuFont = new FontUIResource(FontManager._FONT_DEFAULT);
        WebLookAndFeel.globalAcceleratorFont = new FontUIResource(FontManager._FONT_DEFAULT);
        WebLookAndFeel.globalTitleFont = new FontUIResource(FontManager._FONT_DEFAULT);
        WebLookAndFeel.globalTextFont = new FontUIResource(FontManager._FONT_DEFAULT);
        WebLookAndFeel.install();
    }
}
