package org.tizzer.smmgr.system.view.dialog;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryTradeGoodsRequestDto;
import org.tizzer.smmgr.system.model.response.QueryTradeGoodsResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author tizzer
 * @version 1.0
 */
public class TradeGoodsDialog extends WebDialog {

    private final static Object[] tableHead = {"条码", "名称", "售价"};
    //传入参数缓存
    private static Object[][] dataCache;
    //是否处理标志
    private static boolean isHandle;
    private WebButton searchButton;
    private WebTextField searchField;
    private DefaultTableModel tableModel;
    private WebTable table;
    private WebButton chooseButton;
    private WebButton cancelButton;

    public TradeGoodsDialog(Object[][] dataCache) {
        super(RuntimeConstants.root, "选择交易商品", true);
        searchButton = createTrailingComponent();
        searchField = createSearchField();
        table = createChooseTable();
        this.setTableData(dataCache);
        chooseButton = createBootstrapButton("选择");
        cancelButton = createBootstrapButton("取消");

        this.add(createContentPane());
        this.pack();
        this.setLocationRelativeTo(RuntimeConstants.root);
        this.initListener();
    }

    public static Object[][] newInstance(Object[][] dataCache) {
        TradeGoodsDialog tradeGoodsDialog = new TradeGoodsDialog(dataCache);
        tradeGoodsDialog.setVisible(true);
        if (!isHandle) {
            dataCache = null;
        }
        return dataCache;
    }

    private void initListener() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText().trim();
                if (!keyword.equals("")) {
                    QueryTradeGoodsResponseDto queryTradeGoodsResponseDto = queryTradeGoods(keyword);
                    if (queryTradeGoodsResponseDto.getData() != null) {
                        dataCache = queryTradeGoodsResponseDto.getData();
                        setTableData(dataCache);
                    }
                }
            }
        });
        searchField.addActionListener(searchButton.getActionListeners()[0]);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    dataCache = new Object[1][tableHead.length];
                    for (int i = 0; i < tableHead.length; i++) {
                        dataCache[0][i] = table.getValueAt(row, i);
                    }
                    isHandle = true;
                    dispose();
                }
            }
        });

        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    SwingUtil.showTip(chooseButton, "请先选择一个商品");
                    return;
                }
                dataCache = new Object[1][tableHead.length];
                for (int i = 0; i < tableHead.length; i++) {
                    dataCache[0][i] = table.getValueAt(row, i);
                }
                isHandle = true;
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /**
     * 查询交易商品
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
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryTradeGoodsResponseDto;
    }

    /**
     * 刷新表格数据
     *
     * @param dataCache
     */
    private void setTableData(Object[][] dataCache) {
        tableModel.setDataVector(dataCache, tableHead);
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setMargin(10);
        webPanel.setBackground(ColorManager._241_246_253);
        webPanel.add(searchField, "North");
        webPanel.add(new WebScrollPane(table), "Center");
        webPanel.add(createButtonPane(), "South");
        return webPanel;
    }

    private WebPanel createButtonPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        webPanel.add(chooseButton);
        webPanel.add(cancelButton);
        return webPanel;
    }

    private WebButton createTrailingComponent() {
        WebButton webButton = WebButton.createIconWebButton(IconManager.SEARCH, true);
        webButton.setFocusable(false);
        webButton.setShadeWidth(0);
        webButton.setMoveIconOnPress(false);
        webButton.setCursor(Cursor.getDefaultCursor());
        return webButton;
    }

    private WebTextField createSearchField() {
        WebTextField webTextField = new WebTextField(30);
        webTextField.setTrailingComponent(searchButton);
        webTextField.setMargin(-1, 2, -1, -1);
        return webTextField;
    }

    private WebTable createChooseTable() {
        tableModel = new DefaultTableModel(null, tableHead);
        WebTable webTable = new WebTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }
}
