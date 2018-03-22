package org.tizzer.smmgr.system.view.admin.renderer;

import com.alee.laf.label.WebLabel;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class StateRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        WebLabel webLabel = new WebLabel();
        webLabel.setOpaque(true);
        webLabel.setBackground(Color.WHITE);
        webLabel.setHorizontalAlignment(SwingConstants.CENTER);
        webLabel.setIcon((boolean)value? IconManager._ICON_USINGTAG:IconManager._ICON_STOP);
        if (isSelected) {
            webLabel.setBackground(ColorManager._59_115_175);
        }
        return webLabel;
    }
}
