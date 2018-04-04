package org.tizzer.smmgr.system.view.renderer;

import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class TradeRecordRenderer implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Object[] content = (Object[]) value;
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setPreferredSize(new Dimension(240, 60));
        label.setIcon(content[2].equals(true) ? IconManager.INCOMETAG : IconManager.REFUNDTAG);
        label.setText("<html><font face='Microsoft YaHei' color=black>" +
                "<b>" + content[0] + "</b><br/>" +
                "牌号：" + content[1] +
                "&nbsp;&nbsp;&nbsp;&nbsp;" +
                content[3] +
                "</font><html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        if (index % 2 == 1) {
            label.setBackground(ColorManager._241_246_253);
        } else {
            label.setBackground(Color.WHITE);
        }
        if (isSelected) {
            label.setBackground(ColorManager._212_234_255);
        }
        return label;
    }
}
