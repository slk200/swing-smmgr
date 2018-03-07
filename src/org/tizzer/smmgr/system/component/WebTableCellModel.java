package org.tizzer.smmgr.system.component;

import com.alee.laf.button.WebButton;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.template.Initialization;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tizzer
 * @version 1.0
 */
public class WebTableCellModel extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

    private CellEditor cellEditor = new CellEditor();

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        cellEditor.numField.setText(value == null ? "1" : value.toString());
        return cellEditor;
    }

    @Override
    public Object getCellEditorValue() {
        return cellEditor.getValue();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        cellEditor.numField.setText(value == null ? "1" : value.toString());
        return cellEditor;
    }

    class CellEditor extends Initialization {
        private WebButton add;
        private WebTextField numField;
        private WebButton dec;

        CellEditor() {
            super();
        }

        @Override
        public void initProp() {

        }

        public void initVal() {
            dec = new WebButton("–");
            dec.setRolloverDecoratedOnly(true);
            numField = new WebTextField();
            numField.setHorizontalAlignment(SwingConstants.CENTER);
            add = new WebButton("+");
            add.setRolloverDecoratedOnly(true);
        }

        public void initView() {
            this.add(dec, "West");
            this.add(numField, "Center");
            this.add(add, "East");
        }

        public void initAction() {
            dec.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String content = numField.getText();
                    if (content.matches("[1-9][0-9]*")) {
                        int num = Integer.parseInt(content);
                        if (num > 1) {
                            num--;
                            numField.setText(String.valueOf(num));
                        }
                    }
                }
            });
            add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String content = numField.getText();
                    if (content.matches("[1-9][0-9]*")) {
                        int num = Integer.parseInt(numField.getText());
                        num++;
                        numField.setText(String.valueOf(num));
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        Object getValue() {
            String content = numField.getText();
            if (content.matches("[1-9][0-9]*")) {
                return content;
            }
            return 1;
        }
    }

}
