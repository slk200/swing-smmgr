package org.tizzer.smmgr.system.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryOneInsiderRequestDto;
import org.tizzer.smmgr.system.model.request.QueryTradeGoodsRequestDto;
import org.tizzer.smmgr.system.model.request.SaveTradeRecordRequestDto;
import org.tizzer.smmgr.system.model.response.QueryOneInsiderResponseDto;
import org.tizzer.smmgr.system.model.response.QueryTradeGoodsResponseDto;
import org.tizzer.smmgr.system.model.response.SaveTradeRecordResponseDto;
import org.tizzer.smmgr.system.utils.D9Util;
import org.tizzer.smmgr.system.utils.LafUtil;
import org.tizzer.smmgr.system.view.dialog.CheckoutDialog;
import org.tizzer.smmgr.system.view.dialog.TradeGoodsDialog;
import org.tizzer.smmgr.system.view.dialog.UpdateInsiderDialog;
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
class StandardCollectionBoundary extends WebPanel {

    private final static Object[] tableHead = {"商品条码", "商品名称", "原价", "折扣(%)", "现价", "数量", "小计"};

    private DefaultTableModel tableModel;
    private WebTable tradeGoodsTable;
    private WebButton resetTradeButton;
    private WebButton deleteRowButton;
    private WebButton seeInsiderButton;
    private WebButton cancelInsiderButton;
    private WebTextField searchGoodsField;
    private WebTextField searchInsiderField;
    private WebButton checkoutButton;

    //查询的交易商品缓存
    private Object[][] tradeGoodsCache;
    //会员信息缓存
    private Object[] insiderCache;
    //折扣
    private int discount = 100;
    //当前消费总额
    private double currentCost = 0;

    StandardCollectionBoundary() {
        tradeGoodsTable = createTransactionTable();
        resetTradeButton = createBootstrapButton("清理台面", IconManager.RESETDESK, "brown.xml");
        deleteRowButton = createBootstrapButton("删除记录", IconManager.DELETERECORD, "brown.xml");
        searchGoodsField = createTrailingField("条码/名称/拼音码", null, null);
        seeInsiderButton = createBootstrapButton("查看", null, "default.xml");
        cancelInsiderButton = createBootstrapButton("取消", null, "default.xml");
        searchInsiderField = createTrailingField("会员号/手机号", seeInsiderButton, cancelInsiderButton);
        checkoutButton = createCheckoutButton();
        this.setCheckoutButton("0.0");

        this.setOpaque(false);
        this.add(new WebScrollPane(tradeGoodsTable), "Center");
        this.add(createCheckoutPane(), "South");
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
                    int newValue = Integer.parseInt(String.valueOf(tradeGoodsTable.getValueAt(tcl.getRow(), 5)));
                    //旧数量
                    int oldValue = Integer.parseInt(String.valueOf(tcl.getOldValue()));
                    if (newValue != oldValue) {
                        //现价
                        double presentCost = (double) tradeGoodsTable.getValueAt(tcl.getRow(), 4);
                        //更新小计
                        tradeGoodsTable.setValueAt((double) Math.round(presentCost * newValue * 100) / 100, tcl.getRow(), 6);
                        //更新当前消费总额
                        currentCost += (newValue - oldValue) * presentCost;
                        currentCost = (double) Math.round(currentCost * 100) / 100;
                        setCheckoutButton(String.valueOf(currentCost));
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
                currentCost -= cost;
                tableModel.removeRow(rows[i - 1]);
            }
            currentCost = (double) Math.round(currentCost * 100) / 100;
            setCheckoutButton(String.valueOf(currentCost));
            //后续台面校验
            if (tradeGoodsTable.getRowCount() == 0 && discount != 100) {
                int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>您已清空交易区，是否要退出会员？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
                if (operation == JOptionPane.YES_OPTION) {
                    discount = 100;
                    insiderCache = null;
                    searchInsiderField.setText(null);
                    searchInsiderField.setEditable(true);
                }
            }
        });

        searchGoodsField.addActionListener(e -> {
            String keyword = searchGoodsField.getText().trim();
            if (!keyword.equals("")) {
                QueryTradeGoodsResponseDto queryTradeGoodsResponseDto = queryTradeGoods(keyword);
                if (queryTradeGoodsResponseDto.getCode() == ResultCode.OK) {
                    tradeGoodsCache = queryTradeGoodsResponseDto.getData();
                    if (tradeGoodsCache.length > 1) {
                        tradeGoodsCache = TradeGoodsDialog.newInstance(tradeGoodsCache);
                    }
                    refreshTable();
                }
            }
        });

        searchInsiderField.addActionListener(e -> {
            String keyword = searchInsiderField.getText().trim();
            if (!keyword.equals("")) {
                QueryOneInsiderResponseDto queryOneInsiderResponseDto = queryOneInsider(keyword);
                if (queryOneInsiderResponseDto.getCode() != ResultCode.OK) {
                    LafUtil.showTip(searchInsiderField, queryOneInsiderResponseDto.getMessage());
                } else {
                    //缓存会员信息
                    insiderCache = queryOneInsiderResponseDto.getData();
                    //记录会员折扣
                    discount = queryOneInsiderResponseDto.getDiscount();
                    //刷新表格数据和当前总额
                    refreshTable(discount);
                    //锁定会员的组件
                    searchInsiderField.setEditable(false);
                }
            }
        });

        seeInsiderButton.addActionListener(e -> {
            if (insiderCache == null) {
                return;
            }
            //打开会员详情对话框
            int newDiscount = UpdateInsiderDialog.newInstance(insiderCache);
            //对比会员折扣是否修改了
            if (discount != newDiscount) {
                QueryOneInsiderResponseDto queryOneInsiderResponseDto = queryOneInsider(insiderCache[0].toString());
                if (queryOneInsiderResponseDto.getCode() == ResultCode.OK) {
                    insiderCache = queryOneInsiderResponseDto.getData();
                    refreshTable(discount);
                } else {
                    LafUtil.showNotification("访问出错，" + queryOneInsiderResponseDto.getMessage());
                }
            }
        });

        cancelInsiderButton.addActionListener(e -> {
            //修改折扣
            discount = 100;
            //清理会员数据缓存
            insiderCache = null;
            //刷新表格数据和当前总额
            refreshTable(discount);
            //恢复会员的组件的初始态
            searchInsiderField.setText(null);
            searchInsiderField.setEditable(true);
        });

        checkoutButton.addActionListener(e -> {
            if (currentCost == 0) {
                return;
            }
            //付款方式
            Object payType = CheckoutDialog.newInstance(currentCost);
            if (payType != null) {
                Object cardNo = null;
                Object phone = null;
                if (discount != 100) {
                    //会员参数
                    cardNo = insiderCache[0];
                    phone = insiderCache[2];
                }
                SaveTradeRecordResponseDto saveTradeRecordResponseDto = saveTradeRecord(payType, cardNo, phone);
                if (saveTradeRecordResponseDto.getCode() != ResultCode.OK) {
                    LafUtil.showTip(checkoutButton, saveTradeRecordResponseDto.getMessage());
                } else {
                    reset();
                }
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
            queryTradeGoodsResponseDto = HttpHandler.get("/query/trade/goods?" + queryTradeGoodsRequestDto.toString(), QueryTradeGoodsResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
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
            queryOneInsiderResponseDto = HttpHandler.get("/query/insider/one?" + queryOneInsiderRequestDto.toString(), QueryOneInsiderResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryOneInsiderResponseDto;
    }

    /**
     * 保存交易记录
     *
     * @param payType
     * @param cardNo
     * @param phone
     * @return
     */
    private SaveTradeRecordResponseDto saveTradeRecord(Object payType, Object cardNo, Object phone) {
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
                quantity[i] = tradeGoodsTable.getValueAt(i, 5);
            }
            //参数设置
            SaveTradeRecordRequestDto saveTradeRecordRequestDto = new SaveTradeRecordRequestDto();
            saveTradeRecordRequestDto.setStaffNo(RuntimeConstants.staffNo);
            saveTradeRecordRequestDto.setDiscount(discount);
            saveTradeRecordRequestDto.setPayType(payType);
            saveTradeRecordRequestDto.setCardNo(cardNo);
            saveTradeRecordRequestDto.setPhone(phone);
            saveTradeRecordRequestDto.setUpc(upc);
            saveTradeRecordRequestDto.setName(name);
            saveTradeRecordRequestDto.setPrimeCost(primeCost);
            saveTradeRecordRequestDto.setPresentCost(presentCost);
            saveTradeRecordRequestDto.setQuantity(quantity);
            saveTradeRecordRequestDto.setCost(currentCost);
            saveTradeRecordRequestDto.setType(true);
            saveTradeRecordRequestDto.setSerialNo(null);
            saveTradeRecordResponseDto = HttpHandler.post("/save/trade/record", saveTradeRecordRequestDto.toString(), SaveTradeRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return saveTradeRecordResponseDto;
    }

    /**
     * 增加商品更新数据
     */
    private void refreshTable() {
        if (tradeGoodsCache != null) {
            String upc = String.valueOf(tradeGoodsCache[0][0]);
            for (int i = 0; i < tradeGoodsTable.getRowCount(); i++) {
                if (upc.equals(tradeGoodsTable.getValueAt(i, 0))) {
                    //现价
                    double presentCost = (double) tradeGoodsTable.getValueAt(i, 4);
                    //当前数量
                    int num = Integer.parseInt(String.valueOf(tradeGoodsTable.getValueAt(i, 5)));
                    //当前小计
                    double cost = (double) tradeGoodsTable.getValueAt(i, 6);
                    //现在小计
                    cost = (double) Math.round((presentCost + cost) * 100) / 100;
                    //更新数量
                    tradeGoodsTable.setValueAt(num + 1, i, 5);
                    //更新小计
                    tradeGoodsTable.setValueAt(cost, i, 6);
                    //更新当前花费总额
                    currentCost = (double) Math.round((currentCost + presentCost) * 100) / 100;
                    setCheckoutButton(String.valueOf(currentCost));
                    searchGoodsField.setText(null);
                    return;
                }
            }
            //售价
            double primeCost = (double) tradeGoodsCache[0][2];
            //现价
            double presentCost = (double) Math.round(primeCost * discount) / 100;
            //增加行数据
            Object[] row = {upc, tradeGoodsCache[0][1], primeCost, discount, presentCost, 1, presentCost};
            tableModel.addRow(row);
            //更新当前消费总额
            currentCost = (double) Math.round((currentCost + presentCost) * 100) / 100;
            setCheckoutButton(String.valueOf(currentCost));
        }
        searchGoodsField.setText(null);
    }

    /**
     * 更新折扣更新数据
     *
     * @param discount
     */
    private void refreshTable(int discount) {
        currentCost = 0;
        for (int i = 0; i < tradeGoodsTable.getRowCount(); i++) {
            //现价
            double presentCost = (double) Math.round((double) tradeGoodsTable.getValueAt(i, 2) * discount) / 100;
            //小计
            double total = (double) Math.round(presentCost * Integer.parseInt(String.valueOf(tradeGoodsTable.getValueAt(i, 5))) * 100) / 100;
            //更新折扣
            tradeGoodsTable.setValueAt(discount, i, 3);
            //更新现价
            tradeGoodsTable.setValueAt(presentCost, i, 4);
            //更新小计
            tradeGoodsTable.setValueAt(total, i, 6);
            //更新当前消费总额
            currentCost += total;
        }
        currentCost = (double) Math.round(currentCost * 100) / 100;
        setCheckoutButton(String.valueOf(currentCost));
    }

    /**
     * 清理台面数据
     */
    private void reset() {
        insiderCache = null;
        tradeGoodsCache = null;
        discount = 100;
        currentCost = 0;
        setCheckoutButton("0.0");
        tableModel.setDataVector(null, tableHead);
        searchGoodsField.setText(null);
        searchInsiderField.setText(null);
        searchInsiderField.setEditable(true);
    }

    /**
     * 更新结算按钮的text
     */
    private void setCheckoutButton(String text) {
        checkoutButton.setText("<html><font face='Microsoft YaHei' color=white size=6><b>收款</b>&nbsp;&nbsp;&nbsp;￥" + text + "</font></html>");
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

    private WebTextField createTrailingField(String inputPrompt, JComponent leadingComponent, JComponent trailingComponent) {
        WebTextField webTextField = new WebTextField(15);
        webTextField.setInputPrompt(inputPrompt);
        webTextField.setMargin(5);
        webTextField.setLeadingComponent(leadingComponent);
        webTextField.setTrailingComponent(trailingComponent);
        return webTextField;
    }

    private WebButton createBootstrapButton(String text, ImageIcon icon, String colorConfig) {
        WebButton webButton = new WebButton(text, icon);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setCursor(Cursor.getDefaultCursor());
        webButton.setPainter(D9Util.getNinePatchPainter(colorConfig));
        return webButton;
    }

    private WebButton createCheckoutButton() {
        WebButton webButton = new WebButton();
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(D9Util.getNinePatchPainter("red.xml"));
        return webButton;
    }

    private WebPanel createFunctionPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new GridBagLayout());
        LafUtil.setupComponent(webPanel, resetTradeButton, 0, 0, 1, 1);
        LafUtil.setupComponent(webPanel, deleteRowButton, 1, 0, 1, 1);
        LafUtil.setupComponent(webPanel, checkoutButton, 0, 1, 2, 1);
        return webPanel;
    }

    private WebPanel createFieldPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(15, 10, 10, 0);
        webPanel.setLayout(new GridLayout(2, 1, 0, 15));
        webPanel.add(searchGoodsField);
        webPanel.add(searchInsiderField);
        return webPanel;
    }

    private WebPanel createCheckoutPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(0, 10, 10, 10);
        webPanel.add(createFieldPane(), "West");
        webPanel.add(createFunctionPane(), "East");
        return webPanel;
    }
}
