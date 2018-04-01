package org.tizzer.smmgr.system.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryRefundRecordRequestDto;
import org.tizzer.smmgr.system.model.request.SaveTradeGoodsRequestDto;
import org.tizzer.smmgr.system.model.response.QueryRefundRecordResponseDto;
import org.tizzer.smmgr.system.model.response.SaveTradeGoodsResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;
import org.tizzer.smmgr.system.view.listener.TableCellListener;

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
public class StandardRefundBoundary extends WebPanel {

    private final static Object[] tableHead = {"商品条码", "商品名称", "原价", "折扣(%)", "现价", "数量", "小计"};

    private DefaultTableModel tableModel;
    private WebTable tradeGoodsTable;
    private WebButton resetTradeButton;
    private WebButton deleteRowButton;
    private WebLabel currentRefundLabel;
    private WebTextField searchRecordField;
    private WebButton refundButton;

    private Object[] recordCache;
    private Object currentSerialNo;
    private double currentCost = 0;

    public StandardRefundBoundary() {
        tradeGoodsTable = createTransactionTable();
        resetTradeButton = createBootstrapButton("清理台面", IconManager.RESETDESK, "brown.xml");
        deleteRowButton = createBootstrapButton("删除记录", IconManager.DELETERECORD, "brown.xml");
        currentRefundLabel = createRefundLabel();
        searchRecordField = createTrailingField("详细单号");
        refundButton = createRefundButton();
        this.setLabelText("");
        this.setRefundButton("0.0");

        this.setOpaque(false);
        this.add(new WebScrollPane(tradeGoodsTable), "Center");
        this.add(createRefundPane(), "South");
        this.initListener();
    }

    private void initListener() {
        new TableCellListener(tradeGoodsTable, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener) e.getSource();
                String value = tcl.getNewValue() + "";
                if (value.matches("[1-9]([0-9]?)*")) {
                    //新数量
                    int newValue = Integer.parseInt(value);
                    //旧数量
                    int oldValue = Integer.parseInt(tcl.getOldValue() + "");
                    if (newValue < oldValue) {
                        //现价
                        double presentCost = (double) tradeGoodsTable.getValueAt(tcl.getRow(), 4);
                        //更新小计
                        tradeGoodsTable.setValueAt((double) Math.round(presentCost * newValue * 100) / 100, tcl.getRow(), 6);
                        //更新当前退款总额
                        currentCost += (newValue - oldValue) * presentCost;
                        currentCost = (double) Math.round(currentCost * 100) / 100;
                        setRefundButton(currentCost + "");
                        return;
                    }
                }
                tradeGoodsTable.setValueAt(tcl.getOldValue(), tcl.getRow(), tcl.getColumn());
            }
        });

        resetTradeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>确定要清理台面吗？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
                if (operation == JOptionPane.YES_OPTION) {
                    reset();
                }
            }
        });

        deleteRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tradeGoodsTable.getSelectedRow() == -1) {
                    SwingUtil.showTip(deleteRowButton, "请至少选中表格中的一个商品");
                    return;
                }
                int[] rows = tradeGoodsTable.getSelectedRows();
                for (int i = rows.length; i > 0; i--) {
                    //小计
                    double cost = (double) tradeGoodsTable.getValueAt(rows[i - 1], 6);
                    //更新当前消费总额
                    currentCost -= cost;
                    tableModel.removeRow(rows[i - 1]);
                }
                currentCost = (double) Math.round(currentCost * 100) / 100;
                setRefundButton(currentCost + "");
                //后续台面校验
                if (tradeGoodsTable.getRowCount() == 0) {
                    int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>您已清空退货区，是否要清理台面？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
                    if (operation == JOptionPane.YES_OPTION) {
                        reset();
                    }
                }
            }
        });

        searchRecordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serialNo = searchRecordField.getText().trim();
                if (!serialNo.equals("")) {
                    QueryRefundRecordResponseDto queryRefundRecordResponseDto = queryRefundRecord(serialNo);
                    if (queryRefundRecordResponseDto.getCode() != ResultCode.OK) {
                        SwingUtil.showTip(searchRecordField, queryRefundRecordResponseDto.getMessage());
                    } else {
                        currentSerialNo = serialNo;
                        recordCache = queryRefundRecordResponseDto.getCache();
                        currentCost = queryRefundRecordResponseDto.getCost();
                        tableModel.setDataVector(queryRefundRecordResponseDto.getData(), tableHead);
                        setRefundButton(queryRefundRecordResponseDto.getCost() + "");
                        setLabelText(serialNo);
                    }
                }
            }
        });

        refundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentCost == 0) {
                    return;
                }
                SaveTradeGoodsResponseDto saveTradeGoodsResponseDto = tradeGoods(currentSerialNo);
                if (saveTradeGoodsResponseDto.getCode() != ResultCode.OK) {
                    SwingUtil.showTip(refundButton, saveTradeGoodsResponseDto.getMessage());
                } else {
                    reset();
                }
            }

        });
    }

    private QueryRefundRecordResponseDto queryRefundRecord(String serialNo) {
        QueryRefundRecordResponseDto queryRefundRecordResponseDto = new QueryRefundRecordResponseDto();
        try {
            QueryRefundRecordRequestDto queryRefundRecordRequestDto = new QueryRefundRecordRequestDto();
            queryRefundRecordRequestDto.setSerialNo(serialNo);
            queryRefundRecordResponseDto = HttpHandler.get("/query/refund/record?" + queryRefundRecordRequestDto.toString(), QueryRefundRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryRefundRecordResponseDto;
    }

    private SaveTradeGoodsResponseDto tradeGoods(Object originalSerialNo) {
        SaveTradeGoodsResponseDto saveTradeGoodsResponseDto = new SaveTradeGoodsResponseDto();
        try {
            //从表格获取商品参数
            int rowCount = tradeGoodsTable.getRowCount();
            Object[] upc = new Object[rowCount];
            Object[] name = new Object[rowCount];
            Object[] primeCost = new Object[rowCount];
            Object[] presentCost = new Object[rowCount];
            Object[] quantity = new Object[rowCount];
            for (int i = 0; i < rowCount; i++) {
                upc[i] = tradeGoodsTable.getValueAt(i, 0);
                name[i] = tradeGoodsTable.getValueAt(i, 1);
                primeCost[i] = tradeGoodsTable.getValueAt(i, 2);
                presentCost[i] = tradeGoodsTable.getValueAt(i, 4);
                quantity[i] = "-" + tradeGoodsTable.getValueAt(i, 5);
            }
            //参数设置
            SaveTradeGoodsRequestDto saveTradeGoodsRequestDto = new SaveTradeGoodsRequestDto();
            saveTradeGoodsRequestDto.setStaffNo(RuntimeConstants.staffNo);
            saveTradeGoodsRequestDto.setCardNo(recordCache[0]);
            saveTradeGoodsRequestDto.setPhone(recordCache[1]);
            saveTradeGoodsRequestDto.setPayType(recordCache[2]);
            saveTradeGoodsRequestDto.setDiscount(recordCache[3]);
            saveTradeGoodsRequestDto.setUpc(upc);
            saveTradeGoodsRequestDto.setName(name);
            saveTradeGoodsRequestDto.setPrimeCost(primeCost);
            saveTradeGoodsRequestDto.setPresentCost(presentCost);
            saveTradeGoodsRequestDto.setQuantity(quantity);
            saveTradeGoodsRequestDto.setCost(-currentCost);
            saveTradeGoodsRequestDto.setType(false);
            saveTradeGoodsRequestDto.setSerialNo(originalSerialNo);
            saveTradeGoodsResponseDto = HttpHandler.post("/save/trade/record", saveTradeGoodsRequestDto.toString(), SaveTradeGoodsResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return saveTradeGoodsResponseDto;
    }

    /**
     * 清理台面数据
     */
    private void reset() {
        recordCache = null;
        currentCost = 0;
        setLabelText("");
        setRefundButton("0.0");
        tableModel.setDataVector(null, tableHead);
    }

    private void setLabelText(String text) {
        currentRefundLabel.setText("当前退单：" + text);
    }

    /**
     * 更新退款按钮的text
     */
    private void setRefundButton(String text) {
        refundButton.setText("<html><font face='Microsoft YaHei' color=white size=6><b>退款</b>&nbsp;&nbsp;&nbsp;￥" + text + "</font></html>");
    }

    private WebPanel createRefundPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(0, 10, 10, 10);
        webPanel.add(createFieldPane(), "West");
        webPanel.add(createFunctionPane(), "East");
        return webPanel;
    }

    private WebPanel createFieldPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(15, 10, 10, 0);
        webPanel.setLayout(new GridLayout(2, 1, 0, 15));
        webPanel.add(currentRefundLabel);
        webPanel.add(searchRecordField);
        return webPanel;
    }

    private WebPanel createFunctionPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new GridBagLayout());
        SwingUtil.setupComponent(webPanel, resetTradeButton, 0, 0, 1, 1);
        SwingUtil.setupComponent(webPanel, deleteRowButton, 1, 0, 1, 1);
        SwingUtil.setupComponent(webPanel, refundButton, 0, 1, 2, 1);
        return webPanel;
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
        webTable.setVisibleRowCount(4);
        webTable.getTableHeader().setReorderingAllowed(false);
        webTable.getTableHeader().setResizingAllowed(false);
        return webTable;
    }

    private WebLabel createRefundLabel() {
        return new WebLabel();
    }

    private WebTextField createTrailingField(String inputPrompt) {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setInputPrompt(inputPrompt);
        webTextField.setMargin(5);
        return webTextField;
    }

    private WebButton createBootstrapButton(String text, ImageIcon icon, String colorConfig) {
        WebButton webButton = new WebButton(text, icon);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setCursor(Cursor.getDefaultCursor());
        webButton.setPainter(NPatchUtil.getNinePatchPainter(colorConfig));
        return webButton;
    }

    private WebButton createRefundButton() {
        WebButton webButton = new WebButton();
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("green.xml"));
        return webButton;
    }

}
