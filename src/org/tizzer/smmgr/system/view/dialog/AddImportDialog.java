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
import org.tizzer.smmgr.system.model.request.SaveImportRecordRequestDto;
import org.tizzer.smmgr.system.model.response.QueryImportGoodsResponseDto;
import org.tizzer.smmgr.system.model.response.SaveImportRecordResponseDto;
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
public class AddImportDialog extends WebDialog {
    private final static Object[] tableHead = {"商品条码", "商品名称", "进价", "数量", "小计"};
    //是否刷新标志
    private static boolean isRefresh;
    private DefaultTableModel tableModel;
    private WebTable importGoodsTable;
    private WebButton addGoodsButton;
    private WebButton resetImportButton;
    private WebButton deleteRowButton;
    private WebTextField searchGoodsField;
    private WebButton importButton;
    //进货商品缓存
    private Object[][] importGoodsCache;
    //记录当前进货总额
    private double currentImport = 0;

    private AddImportDialog() {
        super(RuntimeConstants.root, "进货", true);
        importGoodsTable = createTransactionTable();
        addGoodsButton = createBootstrapButton("新增商品", IconManager.ADDGOODS);
        resetImportButton = createBootstrapButton("清理台面", IconManager.RESETDESK);
        deleteRowButton = createBootstrapButton("删除记录", IconManager.DELETERECORD);
        searchGoodsField = createTrailingField();
        importButton = createRefundButton();
        this.setImportButton("0.0");

        this.setBackground(ColorManager._241_246_253);
        this.add(new WebScrollPane(importGoodsTable), "Center");
        this.add(createRefundPane(), "South");
        this.initListener();
    }

    public static boolean newInstance() {
        AddImportDialog addImportDialog = new AddImportDialog();
        addImportDialog.setSize(800, 600);
        addImportDialog.setLocationRelativeTo(RuntimeConstants.root);
        addImportDialog.setVisible(true);
        return isRefresh;
    }

    private void initListener() {
        new TableCellListener(importGoodsTable, new AbstractAction() {
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
                        double presentCost = (double) importGoodsTable.getValueAt(tcl.getRow(), 2);
                        //更新小计
                        importGoodsTable.setValueAt((double) Math.round(presentCost * newValue * 100) / 100, tcl.getRow(), 4);
                        //更新当前进货总额
                        currentImport += (newValue - oldValue) * presentCost;
                        currentImport = (double) Math.round(currentImport * 100) / 100;
                        setImportButton(String.valueOf(currentImport));
                        return;
                    }
                }
                importGoodsTable.setValueAt(tcl.getOldValue(), tcl.getRow(), tcl.getColumn());
            }
        });

        addGoodsButton.addActionListener(e -> AddGoodsDialog.newInstance());

        resetImportButton.addActionListener(e -> {
            int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>确定要清理台面吗？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
            if (operation == JOptionPane.YES_OPTION) {
                reset();
            }
        });

        deleteRowButton.addActionListener(e -> {
            if (importGoodsTable.getSelectedRow() == -1) {
                LafUtil.showTip(deleteRowButton, "请至少选中表格中的一个商品");
                return;
            }
            int[] rows = importGoodsTable.getSelectedRows();
            for (int i = rows.length; i > 0; i--) {
                //小计
                double cost = (double) importGoodsTable.getValueAt(rows[i - 1], 4);
                //更新当前进货总额
                currentImport -= cost;
                tableModel.removeRow(rows[i - 1]);
            }
            currentImport = (double) Math.round(currentImport * 100) / 100;
            setImportButton(String.valueOf(currentImport));
            //后续台面校验
            if (importGoodsTable.getRowCount() == 0) {
                int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>您已清空进货区，是否要清理台面？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
                if (operation == JOptionPane.YES_OPTION) {
                    reset();
                }
            }
        });

        searchGoodsField.addActionListener(e -> {
            String keyword = searchGoodsField.getText().trim();
            if (!keyword.equals("")) {
                QueryImportGoodsResponseDto queryImportGoodsResponseDto = queryImportGoods(keyword);
                if (queryImportGoodsResponseDto.getCode() == ResultCode.OK) {
                    importGoodsCache = queryImportGoodsResponseDto.getData();
                    if (importGoodsCache.length > 1) {
                        importGoodsCache = ChooseGoodsDialog.newInstance(importGoodsCache);
                    }
                    refreshTable();
                }
            }
        });

        importButton.addActionListener(e -> {
            if (currentImport == 0) {
                return;
            }
            String note = JOptionPane.showInputDialog(RuntimeConstants.root, "请输入订购备注", "填充备注", JOptionPane.QUESTION_MESSAGE);
            SaveImportRecordResponseDto saveBookRecordResponseDto = saveImportRecord(note);
            if (saveBookRecordResponseDto.getCode() != ResultCode.OK) {
                LafUtil.showTip(importButton, saveBookRecordResponseDto.getMessage());
            } else {
                isRefresh = true;
                dispose();
            }
        });
    }

    /**
     * 保存进货记录
     *
     * @param note
     * @return
     */
    private SaveImportRecordResponseDto saveImportRecord(String note) {
        SaveImportRecordResponseDto saveImportRecordResponseDto = new SaveImportRecordResponseDto();
        try {
            //从表格获取商品参数
            int rowCount = importGoodsTable.getRowCount();
            Object[] upc = new Object[rowCount];
            Object[] name = new Object[rowCount];
            Object[] primeCost = new Object[rowCount];
            Object[] quantity = new Object[rowCount];
            for (int i = 0; i < rowCount; i++) {
                upc[i] = importGoodsTable.getValueAt(i, 0);
                name[i] = importGoodsTable.getValueAt(i, 1);
                primeCost[i] = importGoodsTable.getValueAt(i, 2);
                quantity[i] = importGoodsTable.getValueAt(i, 3);
            }
            //参数设置
            SaveImportRecordRequestDto saveImportRecordRequestDto = new SaveImportRecordRequestDto();
            saveImportRecordRequestDto.setCost(currentImport);
            saveImportRecordRequestDto.setNote(note);
            saveImportRecordRequestDto.setUpc(upc);
            saveImportRecordRequestDto.setName(name);
            saveImportRecordRequestDto.setPrimeCost(primeCost);
            saveImportRecordRequestDto.setQuantity(quantity);
            saveImportRecordResponseDto = HttpHandler.post("/save/import/record", saveImportRecordRequestDto.toString(), SaveImportRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return saveImportRecordResponseDto;
    }

    /**
     * 查询进货商品
     *
     * @param keyword
     * @return
     */
    private QueryImportGoodsResponseDto queryImportGoods(String keyword) {
        QueryImportGoodsResponseDto queryImportGoodsResponseDto = new QueryImportGoodsResponseDto();
        try {
            QueryTradeGoodsRequestDto queryTradeGoodsRequestDto = new QueryTradeGoodsRequestDto();
            queryTradeGoodsRequestDto.setKeyword(keyword);
            queryImportGoodsResponseDto = HttpHandler.post("/query/trade/goods", queryTradeGoodsRequestDto.toString(), QueryImportGoodsResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryImportGoodsResponseDto;
    }

    /**
     * 刷新台面
     */
    private void refreshTable() {
        if (importGoodsCache != null) {
            String upc = String.valueOf(importGoodsCache[0][0]);
            for (int i = 0; i < importGoodsTable.getRowCount(); i++) {
                if (upc.equals(importGoodsTable.getValueAt(i, 0))) {
                    //进价
                    double presentCost = (double) importGoodsTable.getValueAt(i, 2);
                    //当前数量
                    int num = Integer.parseInt(String.valueOf(importGoodsTable.getValueAt(i, 3)));
                    //当前小计
                    double cost = (double) importGoodsTable.getValueAt(i, 4);
                    //现在小计
                    cost = (double) Math.round((presentCost + cost) * 100) / 100;
                    //更新数量
                    importGoodsTable.setValueAt(num + 1, i, 3);
                    //更新小计
                    importGoodsTable.setValueAt(cost, i, 4);
                    //更新当前进货总额
                    currentImport = (double) Math.round((currentImport + presentCost) * 100) / 100;
                    setImportButton(String.valueOf(currentImport));
                    searchGoodsField.setText(null);
                    return;
                }
            }
            double importPrice = (double) importGoodsCache[0][2];
            //增加行数据
            Object[] row = {upc, importGoodsCache[0][1], importPrice, 1, importPrice};
            tableModel.addRow(row);
            //更新当前进货总额
            currentImport = (double) Math.round((currentImport + importPrice) * 100) / 100;
            setImportButton(String.valueOf(currentImport));
        }
        searchGoodsField.setText(null);
    }

    /**
     * 清理台面数据
     */
    private void reset() {
        importGoodsCache = null;
        currentImport = 0;
        setImportButton("0.0");
        tableModel.setDataVector(null, tableHead);
    }

    /**
     * 更新亏损按钮的text
     */
    private void setImportButton(String text) {
        importButton.setText("<html><font face='Microsoft YaHei' color=white size=6><b>进货</b>&nbsp;&nbsp;&nbsp;￥" + text + "</font></html>");
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
        LafUtil.setupComponent(webPanel, addGoodsButton, 0, 0, 1, 1);
        LafUtil.setupComponent(webPanel, resetImportButton, 1, 0, 1, 1);
        LafUtil.setupComponent(webPanel, deleteRowButton, 2, 0, 1, 1);
        LafUtil.setupComponent(webPanel, importButton, 0, 1, 3, 1);
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
