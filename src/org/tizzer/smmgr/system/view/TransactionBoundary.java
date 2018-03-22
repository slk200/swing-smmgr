package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.util.NPatchUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tizzer
 * @version 1.0
 */
public class TransactionBoundary extends WebPanel {

    private final static Class clazz = TransactionBoundary.class;
    private final static Object[] tableHead = {"商品条码", "商品名称", "原价", "折扣", "数量", "小计", "现价"};

    private DefaultTableModel tableModel;
    private WebTable table;
    private WebButton addButton;
    private WebButton delButton;
    private WebToggleButton noCodeButton;
    private WebTextField scanField;
    private WebButton insiderButton;
    private WebTextField insiderField;
    private WebButton checkoutButton;

    public TransactionBoundary() {
        table = createTransactionTable();
        addButton = createBootstrapButton("新增商品", IconManager._ICON_ADDGOODS, "brown.xml");
        delButton = createBootstrapButton("删除", IconManager._ICON_DELETE, "brown.xml");
        noCodeButton = createTrailingButton("无码");
        scanField = createTrailingField("商品条码/商品名称", noCodeButton);
        insiderButton = createBootstrapButton("详情", null, "default.xml");
        insiderField = createTrailingField("会员号", insiderButton);
        checkoutButton = createBootstrapButton("收款", null, "recred.xml");

        this.setOpaque(false);
        this.add(new WebScrollPane(table), BorderLayout.CENTER);
        this.add(createBottomPanel(), BorderLayout.SOUTH);
        this.initListener();
    }

    private void initListener() {
        scanField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        insiderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        delButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        noCodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        insiderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private WebTable createTransactionTable() {
        tableModel = new DefaultTableModel(null, tableHead);
        WebTable webTable = new WebTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
        tableCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        webTable.setDefaultRenderer(Object.class, tableCellRenderer);
        webTable.setShowVerticalLines(false);
        webTable.setRowHeight(30);
        webTable.getTableHeader().setReorderingAllowed(false);
        webTable.getTableHeader().setResizingAllowed(false);
        return webTable;
    }

    private WebTextField createTrailingField(String inputPrompt, JComponent trailingComponent) {
        WebTextField webTextField = new WebTextField();
        webTextField.setInputPrompt(inputPrompt);
        webTextField.setMargin(5);
        webTextField.setTrailingComponent(trailingComponent);
        return webTextField;
    }

    private WebToggleButton createTrailingButton(String text) {
        WebToggleButton webToggleButton = new WebToggleButton(text);
        webToggleButton.setForeground(Color.WHITE);
        webToggleButton.setSelectedForeground(Color.WHITE);
        webToggleButton.setCursor(Cursor.getDefaultCursor());
        webToggleButton.setPainter(NPatchUtil.getNinePatchPainter("toggle.xml"));
        return webToggleButton;
    }

    private WebButton createBootstrapButton(String text, ImageIcon icon, String colorConfig) {
        WebButton webButton = new WebButton(text, icon);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setCursor(Cursor.getDefaultCursor());
        webButton.setPainter(NPatchUtil.getNinePatchPainter(colorConfig));
        return webButton;
    }

    private WebPanel createBottomPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new VerticalFlowLayout());
        webPanel.add(createFunctionPanel());
        webPanel.add(createCheckoutPanel());
        return webPanel;
    }

    private WebPanel createFunctionPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        webPanel.add(addButton);
        webPanel.add(delButton);
        return webPanel;
    }

    private WebPanel createCheckoutPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(0, 10, 10, 10);
        webPanel.setLayout(new GridLayout(1, 3, 5, 5));
        webPanel.setBackground(ColorManager._242_242_242);
        webPanel.add(scanField, insiderField, checkoutButton);
        return webPanel;
    }
}
