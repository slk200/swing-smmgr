package org.tizzer.smmgr.system.utils;

import com.alee.laf.WebLookAndFeel;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotification;
import com.alee.managers.tooltip.TooltipManager;

import javax.swing.plaf.FontUIResource;
import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class SwingUtil {
    /**
     * 设置weblaf外观
     */
    public static void setWebLaF() {
        //关闭weblaf文字抗锯齿
        //StyleConstants.defaultTextRenderingHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        //StyleConstants.textRenderingHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        Font defaultFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
        WebLookAndFeel.globalControlFont = new FontUIResource(defaultFont);
        WebLookAndFeel.globalTooltipFont = new FontUIResource(defaultFont);
        WebLookAndFeel.globalAlertFont = new FontUIResource(defaultFont);
        WebLookAndFeel.globalMenuFont = new FontUIResource(defaultFont);
        WebLookAndFeel.globalAcceleratorFont = new FontUIResource(defaultFont);
        WebLookAndFeel.globalTitleFont = new FontUIResource(defaultFont);
        WebLookAndFeel.globalTextFont = new FontUIResource(defaultFont);
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
    public static void setupComponent(Container parent, Component child, int gridx, int gridy, int gridwidth, int gridheight) {
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
     * 通知
     *
     * @param content
     */
    public static void showNotification(String content) {
        WebNotification notificationPopup = new WebNotification();
        notificationPopup.setIcon(NotificationIcon.error);
        notificationPopup.setContent(content);
        notificationPopup.setDisplayTime(5000);
        NotificationManager.showNotification(notificationPopup);
    }

    /**
     * 提示
     *
     * @param component
     * @param tooltip
     */
    public static void showTip(Component component, String tooltip) {
        TooltipManager.showOneTimeTooltip(component, null, "<html><font face='Microsoft YaHei'>" + tooltip + "</font></html>", TooltipWay.up);
    }
}
