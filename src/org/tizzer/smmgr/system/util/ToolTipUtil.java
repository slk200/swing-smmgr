package org.tizzer.smmgr.system.util;

import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;

import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class ToolTipUtil {
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
