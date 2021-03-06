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
import org.tizzer.smmgr.system.model.request.QueryTradeGoodsRequestDto;
import org.tizzer.smmgr.system.model.request.SaveLossRecordRequestDto;
import org.tizzer.smmgr.system.model.response.QueryLossGoodsResponseDto;
import org.tizzer.smmgr.system.model.response.SaveLossRecordResponseDto;
import org.tizzer.smmgr.system.utils.D9Util;
import org.tizzer.smmgr.system.utils.LafUtil;
import org.tizzer.smmgr.system.view.dialog.ChooseGoodsDialog;
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
class StandardLossBoundary extends WebPanel {
    private final static Object[] tableHead = {"商品条码", "商品名称", "进价", "数量", "小计"};

    private DefaultTableModel tableModel;
    private WebTable lossGoodsTable;
    private WebButton resetLossButton;
    private WebButton deleteRowButton;
    private WebTextField searchGoodsField;
    private WebButton lossButton;

    //报损商品缓存
    private Object[][] lossGoodsCache;
    //记录当前亏损总额
    private double currentLoss = 0;

    StandardLossBoundary() {
        lossGoodsTable = createTransactionTable();
        resetLossButton = createBootstrapButton("清理台面", IconManager.RESETDESK);
        deleteRowButton = createBootstrapButton("删除记录", IconManager.DELETERECORD);
        searchGoodsField = createTrailingField();
        lossButton = createRefundButton();
        this.setLossButton("0.0");

        this.setOpaque(false);
        this.add(new WebScrollPane(lossGoodsTable), "Center");
        this.add(createRefundPane(), "South");
        this.initListener();
    }

    private void initListener() {
        new TableCellListener(lossGoodsTable, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener) e.getSource();
                String value = String.valueOf(tcl.getNewValue());
                if (value.matches("[1-9]([0-9]?)*")) {
                    //新数量
                    int newValue = Integer.parseInt(value);
                    //旧数量
                    int oldValue = Integer.parseInt(String.valueOf(tcl.getOldValue()));
                    if (newValue != oldValue) {
                        //进价
                        double presentCost = (double) lossGoodsTable.getValueAt(tcl.getRow(), 2);
                        //更新小计
                        lossGoodsTable.setValueAt((double) Math.round(presentCost * newValue * 100) / 100, tcl.getRow(), 4);
                        //更新当前亏损总额
                        currentLoss += (newValue - oldValue) * presentCost;
                        currentLoss = (double) Math.round(currentLoss * 100) / 100;
                        setLossButton(String.valueOf(currentLoss));
                        return;
                    }
                }
                lossGoodsTable.setValueAt(tcl.getOldValue(), tcl.getRow(), tcl.getColumn());
            }
        });

        resetLossButton.addActionListener(e -> {
            int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>确定要清理台面吗？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
            if (operation == JOptionPane.YES_OPTION) {
                reset();
            }
        });

        deleteRowButton.addActionListener(e -> {
            if (lossGoodsTable.getSelectedRow() == -1) {
                LafUtil.showTip(deleteRowButton, "请至少选中表格中的一个商品");
                return;
            }
            int[] rows = lossGoodsTable.getSelectedRows();
            for (int i = rows.length; i > 0; i--) {
                //小计
                double cost = (double) lossGoodsTable.getValueAt(rows[i - 1], 4);
                //更新当前亏损总额
                currentLoss -= cost;
                tableModel.removeRow(rows[i - 1]);
            }
            currentLoss = (double) Math.round(currentLoss * 100) / 100;
            setLossButton(String.valueOf(currentLoss));
            //后续台面校验
            if (lossGoodsTable.getRowCount() == 0) {
                int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>您已清空退货区，是否要清理台面？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
                if (operation == JOptionPane.YES_OPTION) {
                    reset();
                }
            }
        });

        searchGoodsField.addActionListener(e -> {
            String keyword = searchGoodsField.getText().trim();
            if (!keyword.equals("")) {
                QueryLossGoodsResponseDto queryLossGoodsResponseDto = queryLossGoods(keyword);
                if (queryLossGoodsResponseDto.getCode() == ResultCode.OK) {
                    lossGoodsCache = queryLossGoodsResponseDto.getData();
                    if (lossGoodsCache.length > 1) {
                        lossGoodsCache = ChooseGoodsDialog.newInstance(lossGoodsCache);
                    }
                    refreshTable();
                }
            }
        });

        lossButton.addActionListener(e -> {
            if (currentLoss == 0) {
                return;
            }
            String note = JOptionPane.showInputDialog(RuntimeConstants.root, "请输入报损备注", "填充备注", JOptionPane.QUESTION_MESSAGE);
            SaveLossRecordResponseDto saveLossRecordResponseDto = saveLossRecord(note);
            if (saveLossRecordResponseDto.getCode() != ResultCode.OK) {
                LafUtil.showTip(lossButton, saveLossRecordResponseDto.getMessage());
            } else {
                reset();
            }
        });
    }

    /**
     * 保存报损记录
     *
     * @param note
     * @return
     */
    private SaveLossRecordResponseDto saveLossRecord(String note) {
        SaveLossRecordResponseDto saveLossRecordResponseDto = new SaveLossRecordResponseDto();
        try {
            //从表格获取商品参数
            int rowCount = lossGoodsTable.getRowCount();
            Object[] upc = new Object[rowCount];
            Object[] name = new Object[rowCount];
            Object[] primeCost = new Object[rowCount];
            Object[] quantity = new Object[rowCount];
            for (int i = 0; i < rowCount; i++) {
                upc[i] = lossGoodsTable.getValueAt(i, 0);
                name[i] = lossGoodsTable.getValueAt(i, 1);
                primeCost[i] = lossGoodsTable.getValueAt(i, 2);
                quantity[i] = lossGoodsTable.getValueAt(i, 3);
            }
            //参数设置
            SaveLossRecordRequestDto saveLossRecordRequestDto = new SaveLossRecordRequestDto();
            saveLossRecordRequestDto.setStaffNo(RuntimeConstants.staffNo);
            saveLossRecordRequestDto.setCost(currentLoss);
            saveLossRecordRequestDto.setNote(note);
            saveLossRecordRequestDto.setUpc(upc);
            saveLossRecordRequestDto.setName(name);
            saveLossRecordRequestDto.setPrimeCost(primeCost);
            saveLossRecordRequestDto.setQuantity(quantity);
            saveLossRecordResponseDto = HttpHandler.post("/save/loss/record", saveLossRecordRequestDto.toString(), SaveLossRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return saveLossRecordResponseDto;
    }

    /**
     * 查询亏损商品
     *
     * @param keyword
     * @return
     */
    private QueryLossGoodsResponseDto queryLossGoods(String keyword) {
        QueryLossGoodsResponseDto queryLossGoodsResponseDto = new QueryLossGoodsResponseDto();
        try {
            QueryTradeGoodsRequestDto queryTradeGoodsRequestDto = new QueryTradeGoodsRequestDto();
            queryTradeGoodsRequestDto.setKeyword(keyword);
            queryLossGoodsResponseDto = HttpHandler.post("/query/trade/goods", queryTradeGoodsRequestDto.toString(), QueryLossGoodsResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryLossGoodsResponseDto;
    }

    /**
     * 刷新台面
     */
    private void refreshTable() {
        if (lossGoodsCache != null) {
            String upc = String.valueOf(lossGoodsCache[0][0]);
            for (int i = 0; i < lossGoodsTable.getRowCount(); i++) {
                if (upc.equals(lossGoodsTable.getValueAt(i, 0))) {
                    //进价
                    double presentCost = (double) lossGoodsTable.getValueAt(i, 2);
                    //当前数量
                    int num = Integer.parseInt(String.valueOf(lossGoodsTable.getValueAt(i, 3)));
                    //当前小计
                    double cost = (double) lossGoodsTable.getValueAt(i, 4);
                    //现在小计
                    cost = (double) Math.round((presentCost + cost) * 100) / 100;
                    //更新数量
                    lossGoodsTable.setValueAt(num + 1, i, 3);
                    //更新小计
                    lossGoodsTable.setValueAt(cost, i, 4);
                    //更新当前亏损总额
                    currentLoss = (double) Math.round((currentLoss + presentCost) * 100) / 100;
                    setLossButton(String.valueOf(currentLoss));
                    searchGoodsField.setText(null);
                    return;
                }
            }
            double importPrice = (double) lossGoodsCache[0][2];
            //增加行数据
            Object[] row = {upc, lossGoodsCache[0][1], importPrice, 1, importPrice};
            tableModel.addRow(row);
            //更新当前亏损总额
            currentLoss = (double) Math.round((currentLoss + importPrice) * 100) / 100;
            setLossButton(String.valueOf(currentLoss));
        }
        searchGoodsField.setText(null);
    }

    /**
     * 清理台面数据
     */
    private void reset() {
        lossGoodsCache = null;
        currentLoss = 0;
        setLossButton("0.0");
        tableModel.setDataVector(null, tableHead);
    }

    /**
     * 更新亏损按钮的text
     */
    private void setLossButton(String text) {
        lossButton.setText("<html><font face='Microsoft YaHei' color=white size=6><b>亏损</b>&nbsp;&nbsp;&nbsp;￥" + text + "</font></html>");
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
        webPanel.add(createVSpace());
        webPanel.add(searchGoodsField);
        return webPanel;
    }

    private WebPanel createFunctionPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new GridBagLayout());
        LafUtil.setupComponent(webPanel, resetLossButton, 0, 0, 1, 1);
        LafUtil.setupComponent(webPanel, deleteRowButton, 1, 0, 1, 1);
        LafUtil.setupComponent(webPanel, lossButton, 0, 1, 2, 1);
        return webPanel;
    }

    private WebTable createTransactionTable() {
        tableModel = new DefaultTableModel(null, tableHead);
        WebTable webTable = new WebTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
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

    private WebTextField createTrailingField() {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setInputPrompt("条码/名称/拼音码");
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

    private Component createVSpace() {
        return Box.createVerticalStrut(20);
    }
}
