package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryOneInsiderRequestDto;
import org.tizzer.smmgr.system.model.request.QueryTradeGoodsRequestDto;
import org.tizzer.smmgr.system.model.response.QueryOneInsiderResponseDto;
import org.tizzer.smmgr.system.model.response.QueryTradeGoodsResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;
import org.tizzer.smmgr.system.view.dialog.AddGoodsDialog;
import org.tizzer.smmgr.system.view.dialog.ChooseGoodsDialog;
import org.tizzer.smmgr.system.view.dialog.UpdateInsiderDialog;
import org.tizzer.smmgr.system.view.listener.TableCellListener;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class StandardTradeBoundary extends WebPanel {

    private final static Object[] tableHead = {"商品条码", "商品名称", "原价", "折扣(%)", "现价", "数量", "小计"};

    private DefaultTableModel tableModel;
    private WebTable table;
    private WebButton addButton;
    private WebButton resetButton;
    private WebButton delButton;
    private WebTextField scanField;
    private WebButton insiderButton;
    private WebTextField insiderField;
    private WebButton checkoutButton;

    private Object[][] tradeGoodsCache;
    private Object[] insiderCache;
    private int discount = 100;
    private double cCost = 0;

    public StandardTradeBoundary() {
        table = createTransactionTable();
        addButton = createBootstrapButton("新增商品", IconManager._ICON_ADDGOODS, "brown.xml");
        resetButton = createBootstrapButton("重置收银", IconManager._ICON_RESET, "brown.xml");
        delButton = createBootstrapButton("删除记录", IconManager._ICON_DELETE, "brown.xml");
        scanField = createTrailingField("条码/名称/拼音码", null);
        insiderButton = createBootstrapButton("详情", null, "default.xml");
        insiderField = createTrailingField("会员号/手机号", insiderButton);
        checkoutButton = createCheckoutButton();
        this.setCheckoutButton("0.0");

        this.setOpaque(false);
        this.add(new WebScrollPane(table), BorderLayout.CENTER);
        this.add(createBottomPanel(), BorderLayout.SOUTH);
        this.initListener();
    }

    private void initListener() {
        new TableCellListener(table, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener) e.getSource();
                String value = tcl.getNewValue() + "";
                if (value.matches("[1-9]([0-9]?)*")) {
                    //新数量
                    int newValue = Integer.parseInt(table.getValueAt(tcl.getRow(), 5) + "");
                    //现价
                    double pCost = (double) table.getValueAt(tcl.getRow(), 4);
                    //更新小计
                    table.setValueAt((double) Math.round(pCost * newValue * 100) / 100, tcl.getRow(), 6);
                    //旧数量
                    int oldValue = Integer.parseInt(tcl.getOldValue() + "");
                    //更新当前消费总额
                    cCost += (newValue - oldValue) * pCost;
                    cCost = (double) Math.round(cCost * 100) / 100;
                    setCheckoutButton(cCost + "");
                } else {
                    table.setValueAt(tcl.getOldValue(), tcl.getRow(), tcl.getColumn());
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddGoodsDialog.newInstance();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>确定要重置收银吗？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
                if (operation == JOptionPane.YES_OPTION) {
                    reset();
                }
            }
        });

        delButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectedRow() == -1) {
                    SwingUtil.showTip(delButton, "请至少选中表格中的一个商品");
                    return;
                }
                int[] rows = table.getSelectedRows();
                for (int i = rows.length; i > 0; i--) {
                    //小计
                    double cost = (double) table.getValueAt(rows[i - 1], 6);
                    //更新当前消费总额
                    cCost -= cost;
                    tableModel.removeRow(rows[i - 1]);
                }
                cCost = (double) Math.round(cCost * 100) / 100;
                setCheckoutButton(cCost + "");
            }
        });

        scanField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = scanField.getText().trim();
                if (!keyword.equals("")) {
                    QueryTradeGoodsResponseDto queryTradeGoodsResponseDto = queryTradeGoods(keyword);
                    if (queryTradeGoodsResponseDto.getData() != null) {
                        tradeGoodsCache = queryTradeGoodsResponseDto.getData();
                        if (tradeGoodsCache.length != 1) {
                            tradeGoodsCache = ChooseGoodsDialog.newInstance(tradeGoodsCache);
                        }
                        refreshTable();
                    }
                }
            }
        });

        insiderField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = insiderField.getText().trim();
                if (!keyword.equals("")) {
                    QueryOneInsiderResponseDto queryOneInsiderResponseDto = queryOneInsider(keyword);
                    if (queryOneInsiderResponseDto.getCode() != ResultCode.OK) {
                        SwingUtil.showTip(insiderField, queryOneInsiderResponseDto.getMessage());
                    } else {
                        insiderCache = queryOneInsiderResponseDto.getData();
                        discount = queryOneInsiderResponseDto.getDiscount();
                        refreshTable(discount);
                    }
                }
            }
        });
        insiderField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (discount != 100) {
                    insiderField.setSelectionStart(0);
                    insiderField.setSelectionEnd(insiderField.getText().length());
                }
            }
        });
        insiderField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (discount != 100) {
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        discount = 100;
                        refreshTable(discount);
                    }
                }
            }
        });

        insiderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (insiderCache == null) {
                    return;
                }
                int newDiscount = UpdateInsiderDialog.newInstance(insiderCache);
                if (discount != newDiscount) {
                    QueryOneInsiderResponseDto queryOneInsiderResponseDto = queryOneInsider(insiderCache[0].toString());
                    insiderCache = queryOneInsiderResponseDto.getData();
                    refreshTable(discount);
                }
            }
        });

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String staffNo = RuntimeConstants.staffNo;

            }
        });
    }

    /**
     * 查询商品
     *
     * @param keyword
     * @return
     */
    private QueryTradeGoodsResponseDto queryTradeGoods(String keyword) {
        QueryTradeGoodsResponseDto queryTradeGoodsResponseDto = new QueryTradeGoodsResponseDto();
        try {
            QueryTradeGoodsRequestDto queryTradeGoodsRequestDto = new QueryTradeGoodsRequestDto();
            queryTradeGoodsRequestDto.setKeyword(keyword);
            queryTradeGoodsResponseDto = HttpHandler.post("/query/goods/trade", queryTradeGoodsRequestDto.toString(), QueryTradeGoodsResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryTradeGoodsResponseDto;
    }

    /**
     * 查询会员
     *
     * @param keyword
     * @return
     */
    private QueryOneInsiderResponseDto queryOneInsider(String keyword) {
        QueryOneInsiderResponseDto queryOneInsiderResponseDto = new QueryOneInsiderResponseDto();
        try {
            QueryOneInsiderRequestDto queryOneInsiderRequestDto = new QueryOneInsiderRequestDto();
            queryOneInsiderRequestDto.setKeyword(keyword);
            queryOneInsiderResponseDto = HttpHandler.post("/query/insider/one", queryOneInsiderRequestDto.toString(), QueryOneInsiderResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryOneInsiderResponseDto;
    }

    /**
     * 增加商品更新数据
     */
    private void refreshTable() {
        if (tradeGoodsCache != null) {
            String upc = (String) tradeGoodsCache[0][0];
            for (int i = 0; i < table.getRowCount(); i++) {
                if (upc.equals(table.getValueAt(i, 0))) {
                    //现价
                    double pCost = (double) table.getValueAt(i, 4);
                    //当前数量
                    int num = Integer.parseInt(table.getValueAt(i, 5) + "");
                    //当前小计
                    double cost = (double) table.getValueAt(i, 6);
                    //现在小计
                    cost = (double) Math.round((pCost + cost) * 100) / 100;
                    //更新数量
                    table.setValueAt(num + 1, i, 5);
                    //更新小计
                    table.setValueAt(cost, i, 6);
                    //更新当前花费总额
                    cCost = (double) Math.round((cCost + pCost) * 100) / 100;
                    setCheckoutButton(cCost + "");
                    scanField.setText(null);
                    return;
                }
            }
            //售价
            double oCost = (double) tradeGoodsCache[0][2];
            //现价
            double pCost = (double) Math.round(oCost * discount) / 100;
            //增加行数据
            Object[] row = {upc, tradeGoodsCache[0][1], oCost, discount, pCost, 1, pCost};
            tableModel.addRow(row);
            //更新当前消费总额
            cCost = (double) Math.round((cCost + pCost) * 100) / 100;
            setCheckoutButton(cCost + "");
        }
        scanField.setText(null);
    }

    /**
     * 更新折扣更新数据
     *
     * @param discount
     */
    private void refreshTable(int discount) {
        cCost = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            //现价
            double pCost = (double) Math.round((double) table.getValueAt(i, 2) * discount) / 100;
            //小计
            double total = (double) Math.round(pCost * Integer.parseInt(table.getValueAt(i, 5) + "") * 100) / 100;
            //更新折扣
            table.setValueAt(discount, i, 3);
            //更新现价
            table.setValueAt(pCost, i, 4);
            //更新小计
            table.setValueAt(total, i, 6);
            //更新当前消费总额
            cCost += total;
        }
        cCost = (double) Math.round(cCost * 100) / 100;
        setCheckoutButton(cCost + "");
    }

    /**
     * 重置收银数据<br/>
     * 初始化为登录状态
     */
    private void reset() {
        insiderCache = null;
        tradeGoodsCache = null;
        discount = 100;
        cCost = 0;
        setCheckoutButton("0.0");
        tableModel.setDataVector(null, tableHead);
        scanField.setText(null);
        insiderField.setText(null);
        insiderField.setEditable(true);
    }

    //更新结算按钮的text
    private void setCheckoutButton(String text) {
        checkoutButton.setText("<html><font face='Microsoft YaHei' color=white size=5>收款&nbsp;&nbsp;&nbsp;￥" + text + "</font></html>");
    }

    private WebTable createTransactionTable() {
        tableModel = new DefaultTableModel(null, tableHead);
        WebTable webTable = new WebTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
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

    private WebToggleButton createTrailingButton() {
        WebToggleButton webToggleButton = new WebToggleButton("无码");
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

    private WebButton createCheckoutButton() {
        WebButton webButton = new WebButton();
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("red.xml"));
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
        webPanel.add(addButton, resetButton, delButton);
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
