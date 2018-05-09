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
import org.tizzer.smmgr.system.model.request.SaveBookRecordRequestDto;
import org.tizzer.smmgr.system.model.response.QueryBookGoodsResponseDto;
import org.tizzer.smmgr.system.model.response.SaveBookRecordResponseDto;
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
public class AddBookDialog extends WebDialog {
    private final static Object[] tableHead = {"商品条码", "商品名称", "进价", "数量", "小计"};
    //是否刷新标志
    private static boolean isRefresh;
    private DefaultTableModel tableModel;
    private WebTable bookGoodsTable;
    private WebButton resetBookButton;
    private WebButton deleteRowButton;
    private WebTextField searchGoodsField;
    private WebButton bookButton;

    //订购商品缓存
    private Object[][] bookGoodsCache;
    //记录当前订购总额
    private double currentBook = 0;

    private AddBookDialog() {
        super(RuntimeConstants.root, "新建订单", true);
        bookGoodsTable = createTransactionTable();
        resetBookButton = createBootstrapButton("清理台面", IconManager.RESETDESK);
        deleteRowButton = createBootstrapButton("删除记录", IconManager.DELETERECORD);
        searchGoodsField = createTrailingField();
        bookButton = createRefundButton();
        this.setBookButton("0.0");

        this.setBackground(ColorManager._241_246_253);
        this.setLayout(new BorderLayout());
        this.add(new WebScrollPane(bookGoodsTable), "Center");
        this.add(createRefundPane(), "South");
        this.initListener();
    }

    public static boolean newInstance() {
        AddBookDialog addBookDialog = new AddBookDialog();
        addBookDialog.setSize(800, 600);
        addBookDialog.setLocationRelativeTo(RuntimeConstants.root);
        addBookDialog.setVisible(true);
        return isRefresh;
    }

    private void initListener() {
        new TableCellListener(bookGoodsTable, new AbstractAction() {
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
                        double presentCost = (double) bookGoodsTable.getValueAt(tcl.getRow(), 2);
                        //更新小计
                        bookGoodsTable.setValueAt((double) Math.round(presentCost * newValue * 100) / 100, tcl.getRow(), 4);
                        //更新当前订购总额
                        currentBook += (newValue - oldValue) * presentCost;
                        currentBook = (double) Math.round(currentBook * 100) / 100;
                        setBookButton(String.valueOf(currentBook));
                        return;
                    }
                }
                bookGoodsTable.setValueAt(tcl.getOldValue(), tcl.getRow(), tcl.getColumn());
            }
        });

        resetBookButton.addActionListener(e -> {
            int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>确定要清理台面吗？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
            if (operation == JOptionPane.YES_OPTION) {
                reset();
            }
        });

        deleteRowButton.addActionListener(e -> {
            if (bookGoodsTable.getSelectedRow() == -1) {
                LafUtil.showTip(deleteRowButton, "请至少选中表格中的一个商品");
                return;
            }
            int[] rows = bookGoodsTable.getSelectedRows();
            for (int i = rows.length; i > 0; i--) {
                //小计
                double cost = (double) bookGoodsTable.getValueAt(rows[i - 1], 4);
                //更新当前订购总额
                currentBook -= cost;
                tableModel.removeRow(rows[i - 1]);
            }
            currentBook = (double) Math.round(currentBook * 100) / 100;
            setBookButton(String.valueOf(currentBook));
            //后续台面校验
            if (bookGoodsTable.getRowCount() == 0) {
                int operation = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>您已清空订货区，是否要清理台面？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
                if (operation == JOptionPane.YES_OPTION) {
                    reset();
                }
            }
        });

        searchGoodsField.addActionListener(e -> {
            String keyword = searchGoodsField.getText().trim();
            if (!keyword.equals("")) {
                QueryBookGoodsResponseDto queryBookGoodsResponseDto = queryBookGoods(keyword);
                if (queryBookGoodsResponseDto.getCode() == ResultCode.OK) {
                    bookGoodsCache = queryBookGoodsResponseDto.getData();
                    if (bookGoodsCache.length > 1) {
                        bookGoodsCache = ChooseGoodsDialog.newInstance(bookGoodsCache);
                    }
                    refreshTable();
                }
            }
        });

        bookButton.addActionListener(e -> {
            if (currentBook == 0) {
                return;
            }
            String note = JOptionPane.showInputDialog(RuntimeConstants.root, "请输入订购备注", "填充备注", JOptionPane.QUESTION_MESSAGE);
            SaveBookRecordResponseDto saveBookRecordResponseDto = saveBookRecord(note);
            if (saveBookRecordResponseDto.getCode() != ResultCode.OK) {
                LafUtil.showTip(bookButton, saveBookRecordResponseDto.getMessage());
            } else {
                isRefresh = true;
                dispose();
            }
        });
    }

    /**
     * 保存订购记录
     *
     * @param note
     * @return
     */
    private SaveBookRecordResponseDto saveBookRecord(String note) {
        SaveBookRecordResponseDto saveBookRecordResponseDto = new SaveBookRecordResponseDto();
        try {
            //从表格获取商品参数
            int rowCount = bookGoodsTable.getRowCount();
            Object[] upc = new Object[rowCount];
            Object[] name = new Object[rowCount];
            Object[] primeCost = new Object[rowCount];
            Object[] quantity = new Object[rowCount];
            for (int i = 0; i < rowCount; i++) {
                upc[i] = bookGoodsTable.getValueAt(i, 0);
                name[i] = bookGoodsTable.getValueAt(i, 1);
                primeCost[i] = bookGoodsTable.getValueAt(i, 2);
                quantity[i] = bookGoodsTable.getValueAt(i, 3);
            }
            //参数设置
            SaveBookRecordRequestDto saveBookRecordRequestDto = new SaveBookRecordRequestDto();
            saveBookRecordRequestDto.setCost(currentBook);
            saveBookRecordRequestDto.setNote(note);
            saveBookRecordRequestDto.setUpc(upc);
            saveBookRecordRequestDto.setName(name);
            saveBookRecordRequestDto.setPrimeCost(primeCost);
            saveBookRecordRequestDto.setQuantity(quantity);
            saveBookRecordResponseDto = HttpHandler.post("/save/book/record", saveBookRecordRequestDto.toString(), SaveBookRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return saveBookRecordResponseDto;
    }

    /**
     * 查询订购商品
     *
     * @param keyword
     * @return
     */
    private QueryBookGoodsResponseDto queryBookGoods(String keyword) {
        QueryBookGoodsResponseDto queryBookGoodsResponseDto = new QueryBookGoodsResponseDto();
        try {
            QueryTradeGoodsRequestDto queryTradeGoodsRequestDto = new QueryTradeGoodsRequestDto();
            queryTradeGoodsRequestDto.setKeyword(keyword);
            queryBookGoodsResponseDto = HttpHandler.get("/query/trade/goods?" + queryTradeGoodsRequestDto.toString(), QueryBookGoodsResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryBookGoodsResponseDto;
    }

    /**
     * 刷新台面
     */
    private void refreshTable() {
        if (bookGoodsCache != null) {
            String upc = String.valueOf(bookGoodsCache[0][0]);
            for (int i = 0; i < bookGoodsTable.getRowCount(); i++) {
                if (upc.equals(bookGoodsTable.getValueAt(i, 0))) {
                    //进价
                    double presentCost = (double) bookGoodsTable.getValueAt(i, 2);
                    //当前数量
                    int num = Integer.parseInt(String.valueOf(bookGoodsTable.getValueAt(i, 3)));
                    //当前小计
                    double cost = (double) bookGoodsTable.getValueAt(i, 4);
                    //现在小计
                    cost = (double) Math.round((presentCost + cost) * 100) / 100;
                    //更新数量
                    bookGoodsTable.setValueAt(num + 1, i, 3);
                    //更新小计
                    bookGoodsTable.setValueAt(cost, i, 4);
                    //更新当前订购总额
                    currentBook = (double) Math.round((currentBook + presentCost) * 100) / 100;
                    setBookButton(String.valueOf(currentBook));
                    searchGoodsField.setText(null);
                    return;
                }
            }
            double importPrice = (double) bookGoodsCache[0][2];
            //增加行数据
            Object[] row = {upc, bookGoodsCache[0][1], importPrice, 1, importPrice};
            tableModel.addRow(row);
            //更新当前订购总额
            currentBook = (double) Math.round((currentBook + importPrice) * 100) / 100;
            setBookButton(String.valueOf(currentBook));
        }
        searchGoodsField.setText(null);
    }

    /**
     * 清理台面数据
     */
    private void reset() {
        bookGoodsCache = null;
        currentBook = 0;
        setBookButton("0.0");
        tableModel.setDataVector(null, tableHead);
    }

    /**
     * 更新亏损按钮的text
     */
    private void setBookButton(String text) {
        bookButton.setText("<html><font face='Microsoft YaHei' color=white size=6><b>订购</b>&nbsp;&nbsp;&nbsp;￥" + text + "</font></html>");
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
        LafUtil.setupComponent(webPanel, resetBookButton, 0, 0, 1, 1);
        LafUtil.setupComponent(webPanel, deleteRowButton, 1, 0, 1, 1);
        LafUtil.setupComponent(webPanel, bookButton, 0, 1, 2, 1);
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
