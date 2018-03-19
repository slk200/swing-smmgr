package org.tizzer.smmgr.system.util;

import com.alee.laf.WebLookAndFeel;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;
import org.tizzer.smmgr.system.constant.FontManager;

import javax.swing.plaf.FontUIResource;
import java.awt.*;

public class SwingUtil {
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

    /**
     * GridBagLayout布局
     *
     * @param parent
     * @param child
     * @param gridx
     * @param gridy
     * @param gridwidth
     */
    public static void setupComponent(Container parent, Container child, int gridx, int gridy, int gridwidth, int gridheight) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
        gridBagConstraints.gridwidth = gridwidth;
        gridBagConstraints.gridheight = gridheight;
        gridBagConstraints.insets = new Insets(10, 5, 10, 5);
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        parent.add(child, gridBagConstraints);
    }

    /**
     * 提示
     *
     * @param component
     * @param tooltip
     */
    public static void showTip(Component component, String tooltip) {
        TooltipManager.showOneTimeTooltip(
                component,
                null,
                "<html><font face='Microsoft YaHei'>" + tooltip + "</font></html>",
                TooltipWay.up
        );
    }
}
