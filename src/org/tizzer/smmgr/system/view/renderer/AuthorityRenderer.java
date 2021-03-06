package org.tizzer.smmgr.system.view.renderer;

import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class AuthorityRenderer implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setIcon((boolean) value ? IconManager.ADMINTAG : IconManager.STANDARDTAG);
        if (isSelected) {
            label.setBackground(ColorManager._59_115_175);
        }
        return label;
    }
}
