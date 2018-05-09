package org.tizzer.smmgr.system.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryRefundRecordRequestDto;
import org.tizzer.smmgr.system.model.request.SaveTradeRecordRequestDto;
import org.tizzer.smmgr.system.model.response.QueryRefundRecordResponseDto;
import org.tizzer.smmgr.system.model.response.SaveTradeRecordResponseDto;
import org.tizzer.smmgr.system.utils.D9Util;
import org.tizzer.smmgr.system.utils.LafUtil;
import org.tizzer.smmgr.system.view.listener.TableCellListener;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author tizzer
 * @version 1.0
 */
class StandardRefundBoundary extends WebPanel {

    private final static Object[] tableHead = {"商品条码", "商品名称", "原价", "折扣(%)", "现价", "数量", "小计"};

    private DefaultTableModel tableModel;
    private WebTable tradeGoodsTable;
    private WebButton resetTradeButton;
    private WebButton deleteRowButton;
    private WebLabel currentRefundLabel;
    private WebTextField searchRecordField;
    private WebButton refundButton;

    //被退单的缓存
    private Object[] recordCache;
    private Integer[] quantityCache;
    //记录当前被退单号
    private Object currentSerialNo;
    //记录当前退款总额
    private double currentRefund = 0;

    StandardRefundBoundary() {
        tradeGoodsTable = createTransactionTable();
        resetTradeButton = createBootstrapButton("清理台面", IconManager.RESETDESK);
        deleteRowButton = createBootstrapButton("删除记录", IconManager.DELETERECORD);
        currentRefundLabel = createRefundLabel();
        searchRecordField = createTrailingField();
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
                String value = String.valueOf(tcl.getNewValue());
                if (value.matches("[1-9]([0-9]?)*")) {
                    //新数量
                    int newValue = Integer.parseInt(value);
                    if (newValue < quantityCache[tcl.getRow()]) {
                        //现价
                        double presentCost = (double) tradeGoodsTable.getValueAt(tcl.getRow(), 4);
                        //更新小计
                        tradeGoodsTable.setValueAt((double) Math.round(presentCost * newValue * 100) / 100, tcl.getRow(), 6);
                        //旧数量
                        int oldValue = Integer.parseInt(String.valueOf(tcl.getOldValue()));
                        //更新当前退款总额
                        currentRefund += (newValue - oldValue) * presentCost;
                        currentRefund = (double) Math.round(currentRefund * 100) / 100;
                        setRefundButton(String.valueOf(currentRefund));
                        return;
                    }
                }
                tradeGoodsTable.setValueAt(tcl.getOldValue(), tcl.getRow(), tcl.getColumn());
            }
        });

        resetTradeButton.addActionListener(e -> {
            int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>确定要清理台面吗？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
            if (operation == JOptionPane.YES_OPTION) {
                reset();
            }
        });

        deleteRowButton.addActionListener(e -> {
            if (tradeGoodsTable.getSelectedRow() == -1) {
                LafUtil.showTip(deleteRowButton, "请至少选中表格中的一个商品");
                return;
            }
            int[] rows = tradeGoodsTable.getSelectedRows();
            for (int i = rows.length; i > 0; i--) {
                //小计
                double cost = (double) tradeGoodsTable.getValueAt(rows[i - 1], 6);
                //更新当前消费总额
                currentRefund -= cost;
                tableModel.removeRow(rows[i - 1]);
            }
            currentRefund = (double) Math.round(currentRefund * 100) / 100;
            setRefundButton(String.valueOf(currentRefund));
            //后续台面校验
            if (tradeGoodsTable.getRowCount() == 0) {
                int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>您已清空退货区，是否要清理台面？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
                if (operation == JOptionPane.YES_OPTION) {
                    reset();
                }
            }
        });

        searchRecordField.addActionListener(e -> {
            String serialNo = searchRecordField.getText().trim();
            if (!serialNo.equals("")) {
                QueryRefundRecordResponseDto queryRefundRecordResponseDto = queryRefundRecord(serialNo);
                if (queryRefundRecordResponseDto.getCode() != ResultCode.OK) {
                    LafUtil.showTip(searchRecordField, queryRefundRecordResponseDto.getMessage());
                } else {
                    currentSerialNo = serialNo;
                    recordCache = queryRefundRecordResponseDto.getCache();
                    quantityCache = queryRefundRecordResponseDto.getQuantity();
                    currentRefund = queryRefundRecordResponseDto.getCost();
                    tableModel.setDataVector(queryRefundRecordResponseDto.getData(), tableHead);
                    setRefundButton(String.valueOf(queryRefundRecordResponseDto.getCost()));
                    setLabelText(serialNo);
                }
            }
        });

        refundButton.addActionListener(e -> {
            if (currentRefund == 0) {
                return;
            }
            SaveTradeRecordResponseDto saveTradeRecordResponseDto = tradeGoods(currentSerialNo);
            if (saveTradeRecordResponseDto.getCode() != ResultCode.OK) {
                LafUtil.showTip(refundButton, saveTradeRecordResponseDto.getMessage());
            } else {
                reset();
            }
        });
    }

    /**
     * 查询被退单的记录
     *
     * @param serialNo
     * @return
     */
    private QueryRefundRecordResponseDto queryRefundRecord(String serialNo) {
        QueryRefundRecordResponseDto queryRefundRecordResponseDto = new QueryRefundRecordResponseDto();
        try {
            QueryRefundRecordRequestDto queryRefundRecordRequestDto = new QueryRefundRecordRequestDto();
            queryRefundRecordRequestDto.setSerialNo(serialNo);
            queryRefundRecordResponseDto = HttpHandler.get("/query/refund/record?" + queryRefundRecordRequestDto.toString(), QueryRefundRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryRefundRecordResponseDto;
    }

    /**
     * 保存退单记录
     *
     * @param originalSerialNo
     * @return
     */
    private SaveTradeRecordResponseDto tradeGoods(Object originalSerialNo) {
        SaveTradeRecordResponseDto saveTradeRecordResponseDto = new SaveTradeRecordResponseDto();
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
            SaveTradeRecordRequestDto saveTradeRecordRequestDto = new SaveTradeRecordRequestDto();
            saveTradeRecordRequestDto.setStaffNo(RuntimeConstants.staffNo);
            saveTradeRecordRequestDto.setCardNo(recordCache[0]);
            saveTradeRecordRequestDto.setPhone(recordCache[1]);
            saveTradeRecordRequestDto.setPayType(recordCache[2]);
            saveTradeRecordRequestDto.setDiscount(recordCache[3]);
            saveTradeRecordRequestDto.setUpc(upc);
            saveTradeRecordRequestDto.setName(name);
            saveTradeRecordRequestDto.setPrimeCost(primeCost);
            saveTradeRecordRequestDto.setPresentCost(presentCost);
            saveTradeRecordRequestDto.setQuantity(quantity);
            saveTradeRecordRequestDto.setCost(-currentRefund);
            saveTradeRecordRequestDto.setType(false);
            saveTradeRecordRequestDto.setSerialNo(originalSerialNo);
            saveTradeRecordResponseDto = HttpHandler.post("/save/trade/record", saveTradeRecordRequestDto.toString(), SaveTradeRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return saveTradeRecordResponseDto;
    }

    /**
     * 清理台面数据
     */
    private void reset() {
        recordCache = null;
        currentRefund = 0;
        setLabelText("");
        setRefundButton("0.0");
        tableModel.setDataVector(null, tableHead);
    }

    /**
     * 更新当前退单标签的text
     *
     * @param text
     */
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
        LafUtil.setupComponent(webPanel, resetTradeButton, 0, 0, 1, 1);
        LafUtil.setupComponent(webPanel, deleteRowButton, 1, 0, 1, 1);
        LafUtil.setupComponent(webPanel, refundButton, 0, 1, 2, 1);
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
        webTable.getTableHeader().setReorderingAllowed(false);
        webTable.getTableHeader().setResizingAllowed(false);
        return webTable;
    }

    private WebLabel createRefundLabel() {
        return new WebLabel();
    }

    private WebTextField createTrailingField() {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setInputPrompt("详细单号");
        webTextField.setMargin(5);
        return webTextField;
    }

    private WebButton createBootstrapButton(String text, ImageIcon icon) {
        WebButton webButton = new WebButton(text, icon);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setCursor(Cursor.getDefaultCursor());
        webButton.setPainter(D9Util.getNinePatchPainter("brown.xml"));
        return webButton;
    }

    private WebButton createRefundButton() {
        WebButton webButton = new WebButton();
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(D9Util.getNinePatchPainter("green.xml"));
        return webButton;
    }

}
