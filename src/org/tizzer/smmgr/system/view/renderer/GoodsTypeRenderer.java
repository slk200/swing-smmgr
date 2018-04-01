package org.tizzer.smmgr.system.view.renderer;

import org.tizzer.smmgr.system.constant.ColorManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GoodsTypeRenderer implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(20, 90, 20, 90));
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        label.setText(value + "");
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
