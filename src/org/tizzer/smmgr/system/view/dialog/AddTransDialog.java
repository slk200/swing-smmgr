package org.tizzer.smmgr.system.view.dialog;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryTradeGoodsRequestDto;
import org.tizzer.smmgr.system.model.request.SaveTransRecordRequestDto;
import org.tizzer.smmgr.system.model.response.QueryTransGoodsResponseDto;
import org.tizzer.smmgr.system.model.response.SaveTransRecordResponseDto;
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
public class AddTransDialog extends WebDialog {
    private final static Object[] tableHead = {"商品条码", "商品名称", "进价", "数量", "小计"};
    //是否刷新标志
    private static boolean isRefresh;
    private DefaultTableModel tableModel;
    private WebTable transGoodsTable;
    private WebButton resetTransButton;
    private WebButton deleteRowButton;
    private WebTextField searchGoodsField;
    private WebButton transButton;
    //调货商品缓存
    private Object[][] transGoodsCache;
    //记录当前调货总额
    private double currentTrans = 0;

    private AddTransDialog() {
        super(RuntimeConstants.root, "调货", true);
        transGoodsTable = createTransactionTable();
        resetTransButton = createBootstrapButton("清理台面", IconManager.RESETDESK);
        deleteRowButton = createBootstrapButton("删除记录", IconManager.DELETERECORD);
        searchGoodsField = createTrailingField();
        transButton = createRefundButton();
        this.setTransButton("0.0");

        this.setBackground(ColorManager._241_246_253);
        this.add(new WebScrollPane(transGoodsTable), "Center");
        this.add(createRefundPane(), "South");
        this.initListener();
    }

    public static boolean newInstance() {
        AddTransDialog addTransDialog = new AddTransDialog();
        addTransDialog.setSize(800, 600);
        addTransDialog.setLocationRelativeTo(RuntimeConstants.root);
        addTransDialog.setVisible(true);
        return isRefresh;
    }

    private void initListener() {
        new TableCellListener(transGoodsTable, new AbstractAction() {
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
                        double presentCost = (double) transGoodsTable.getValueAt(tcl.getRow(), 2);
                        //更新小计
                        transGoodsTable.setValueAt((double) Math.round(presentCost * newValue * 100) / 100, tcl.getRow(), 4);
                        //更新当前调货总额
                        currentTrans += (newValue - oldValue) * presentCost;
                        currentTrans = (double) Math.round(currentTrans * 100) / 100;
                        setTransButton(String.valueOf(currentTrans));
                        return;
                    }
                }
                transGoodsTable.setValueAt(tcl.getOldValue(), tcl.getRow(), tcl.getColumn());
            }
        });

        resetTransButton.addActionListener(e -> {
            int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>确定要清理台面吗？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
            if (operation == JOptionPane.YES_OPTION) {
                reset();
            }
        });

        deleteRowButton.addActionListener(e -> {
            if (transGoodsTable.getSelectedRow() == -1) {
                LafUtil.showTip(deleteRowButton, "请至少选中表格中的一个商品");
                return;
            }
            int[] rows = transGoodsTable.getSelectedRows();
            for (int i = rows.length; i > 0; i--) {
                //小计
                double cost = (double) transGoodsTable.getValueAt(rows[i - 1], 4);
                //更新当前调货总额
                currentTrans -= cost;
                tableModel.removeRow(rows[i - 1]);
            }
            currentTrans = (double) Math.round(currentTrans * 100) / 100;
            setTransButton(String.valueOf(currentTrans));
            //后续台面校验
            if (transGoodsTable.getRowCount() == 0) {
                int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>您已清空调货区，是否要清理台面？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
                if (operation == JOptionPane.YES_OPTION) {
                    reset();
                }
            }
        });

        searchGoodsField.addActionListener(e -> {
            String keyword = searchGoodsField.getText().trim();
            if (!keyword.equals("")) {
                QueryTransGoodsResponseDto queryTransGoodsResponseDto = queryTransGoods(keyword);
                if (queryTransGoodsResponseDto.getCode() == ResultCode.OK) {
                    transGoodsCache = queryTransGoodsResponseDto.getData();
                    if (transGoodsCache.length > 1) {
                        transGoodsCache = ChooseGoodsDialog.newInstance(transGoodsCache);
                    }
                    refreshTable();
                }
            }
        });

        transButton.addActionListener(e -> {
            if (currentTrans == 0) {
                return;
            }
            int storeId = ChooseStoreDialog.newInstance();
            if (storeId == -1) {
                return;
            }
            SaveTransRecordResponseDto saveTransRecordResponseDto = saveTransRecord(storeId);
            if (saveTransRecordResponseDto.getCode() != ResultCode.OK) {
                LafUtil.showTip(transButton, saveTransRecordResponseDto.getMessage());
            } else {
                isRefresh = true;
                dispose();
            }
        });
    }

    /**
     * 保存调货记录
     *
     * @param storeId
     * @return
     */
    private SaveTransRecordResponseDto saveTransRecord(Object storeId) {
        SaveTransRecordResponseDto saveTransRecordResponseDto = new SaveTransRecordResponseDto();
        try {
            //从表格获取商品参数
            int rowCount = transGoodsTable.getRowCount();
            Object[] upc = new Object[rowCount];
            Object[] name = new Object[rowCount];
            Object[] primeCost = new Object[rowCount];
            Object[] quantity = new Object[rowCount];
            for (int i = 0; i < rowCount; i++) {
                upc[i] = transGoodsTable.getValueAt(i, 0);
                name[i] = transGoodsTable.getValueAt(i, 1);
                primeCost[i] = transGoodsTable.getValueAt(i, 2);
                quantity[i] = transGoodsTable.getValueAt(i, 3);
            }
            //参数设置
            SaveTransRecordRequestDto saveTransRecordRequestDto = new SaveTransRecordRequestDto();
            saveTransRecordRequestDto.setCost(currentTrans);
            saveTransRecordRequestDto.setStoreId(storeId);
            saveTransRecordRequestDto.setUpc(upc);
            saveTransRecordRequestDto.setName(name);
            saveTransRecordRequestDto.setPrimeCost(primeCost);
            saveTransRecordRequestDto.setQuantity(quantity);
            saveTransRecordResponseDto = HttpHandler.post("/save/trans/record", saveTransRecordRequestDto.toString(), SaveTransRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return saveTransRecordResponseDto;
    }

    /**
     * 查询调货商品
     *
     * @param keyword
     * @return
     */
    private QueryTransGoodsResponseDto queryTransGoods(String keyword) {
        QueryTransGoodsResponseDto queryTransGoodsResponseDto = new QueryTransGoodsResponseDto();
        try {
            QueryTradeGoodsRequestDto queryTradeGoodsRequestDto = new QueryTradeGoodsRequestDto();
            queryTradeGoodsRequestDto.setKeyword(keyword);
            queryTransGoodsResponseDto = HttpHandler.post("/query/trade/goods", queryTradeGoodsRequestDto.toString(), QueryTransGoodsResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryTransGoodsResponseDto;
    }

    /**
     * 刷新台面
     */
    private void refreshTable() {
        if (transGoodsCache != null) {
            String upc = String.valueOf(transGoodsCache[0][0]);
            for (int i = 0; i < transGoodsTable.getRowCount(); i++) {
                if (upc.equals(transGoodsTable.getValueAt(i, 0))) {
                    //进价
                    double presentCost = (double) transGoodsTable.getValueAt(i, 2);
                    //当前数量
                    int num = Integer.parseInt(String.valueOf(transGoodsTable.getValueAt(i, 3)));
                    //当前小计
                    double cost = (double) transGoodsTable.getValueAt(i, 4);
                    //现在小计
                    cost = (double) Math.round((presentCost + cost) * 100) / 100;
                    //更新数量
                    transGoodsTable.setValueAt(num + 1, i, 3);
                    //更新小计
                    transGoodsTable.setValueAt(cost, i, 4);
                    //更新当前调货总额
                    currentTrans = (double) Math.round((currentTrans + presentCost) * 100) / 100;
                    setTransButton(String.valueOf(currentTrans));
                    searchGoodsField.setText(null);
                    return;
                }
            }
            double importPrice = (double) transGoodsCache[0][2];
            //增加行数据
            Object[] row = {upc, transGoodsCache[0][1], importPrice, 1, importPrice};
            tableModel.addRow(row);
            //更新当前调货总额
            currentTrans = (double) Math.round((currentTrans + importPrice) * 100) / 100;
            setTransButton(String.valueOf(currentTrans));
        }
        searchGoodsField.setText(null);
    }

    /**
     * 清理台面数据
     */
    private void reset() {
        transGoodsCache = null;
        currentTrans = 0;
        setTransButton("0.0");
        tableModel.setDataVector(null, tableHead);
    }

    /**
     * 更新亏损按钮的text
     */
    private void setTransButton(String text) {
        transButton.setText("<html><font face='Microsoft YaHei' color=white size=6><b>调货</b>&nbsp;&nbsp;&nbsp;￥" + text + "</font></html>");
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
        LafUtil.setupComponent(webPanel, resetTransButton, 0, 0, 1, 1);
        LafUtil.setupComponent(webPanel, deleteRowButton, 1, 0, 1, 1);
        LafUtil.setupComponent(webPanel, transButton, 0, 1, 2, 1);
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
