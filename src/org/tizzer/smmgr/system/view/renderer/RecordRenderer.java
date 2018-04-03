package org.tizzer.smmgr.system.view.renderer;

import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class RecordRenderer implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Object[] content = (Object[]) value;
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(10, 30, 10, 30));
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        label.setIconTextGap(5);
        label.setIcon(IconManager.BOOKTAG);
        label.setText(content[1] + "");
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
