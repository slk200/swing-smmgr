package org.tizzer.smmgr.system.view.renderer;

import org.tizzer.smmgr.system.constant.ColorManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LossRecordRenderer implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Object[] content = (Object[]) value;
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        label.setText("<html><font face='Microsoft YaHei' color=black>" +
                "<b>" + content[0] + "</b><br/>" +
                "牌号：" + content[1] +
                "&nbsp;&nbsp;&nbsp;&nbsp;" +
                content[2] +
                "</font><html>");
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
