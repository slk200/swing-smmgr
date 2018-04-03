package org.tizzer.smmgr.system.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryGoodsRequestDto;
import org.tizzer.smmgr.system.model.response.QueryAllGoodsTypeResponseDto;
import org.tizzer.smmgr.system.model.response.QueryGoodsResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;
import org.tizzer.smmgr.system.view.dialog.UpdateGoodsDialog;
import org.tizzer.smmgr.system.view.renderer.GoodsTypeRenderer;

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
public class ManageGoodsBoundary extends WebPanel {
    private final static Object[] tableHead = {"条码", "名称", "进价", "售价", "库存"};

    /**
     * 查询参数缓存
     */
    private int typeId = 0;
    private int currentPage = 1;
    private String keyword = "";
    private int pageCount;

    private DefaultListModel listModel;
    private WebList list;
    private DefaultTableModel tableModel;
    private WebTable table;
    private WebTextField searchField;
    private WebButton previousButton;
    private WebLabel pageIndicator;
    private WebButton nextButton;

    //商品类型缓存
    private Integer[] typeIdCache;

    public ManageGoodsBoundary() {
        searchField = createSearchField();
        list = createTypeList();
        table = createGoodsTable();
        previousButton = createBootstrapButton("上一页");
        pageIndicator = createPageIndicator();
        nextButton = createBootstrapButton("下一页");

        this.prepareData();
        this.setOpaque(false);
        this.add(new WebScrollPane(list), "West");
        this.add(createRightPane(), "Center");
        this.initListener();
    }

    private void initListener() {
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = list.getSelectedIndex();
                    if (index != -1) {
                        typeId = typeIdCache[index];
                        keyword = "";
                        currentPage = 1;
                        QueryGoodsResponseDto queryGoodsResponseDto = queryGoods(typeId, keyword, currentPage);
                        if (queryGoodsResponseDto.getCode() == ResultCode.OK) {
                            refreshTableData(queryGoodsResponseDto.getData());
                            setPageIndicator(queryGoodsResponseDto.getPageCount());
                        } else {
                            SwingUtil.showNotification("访问出错，" + queryGoodsResponseDto.getMessage());
                        }
                    }
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int flag = UpdateGoodsDialog.newInstance(table.getValueAt(table.getSelectedRow(), 0));
                if (flag != 0) {
                    if (flag == 2) {
                        QueryAllGoodsTypeResponseDto queryAllGoodsTypeResponseDto = queryAllGoodsType();
                        typeIdCache = queryAllGoodsTypeResponseDto.getId();
                        setListItem(queryAllGoodsTypeResponseDto.getName());
                    }
                    QueryGoodsResponseDto queryGoodsResponseDto = queryGoods(typeId, keyword, currentPage);
                    if (queryGoodsResponseDto.getCode() == ResultCode.OK) {
                        refreshTableData(queryGoodsResponseDto.getData());
                        setPageIndicator(queryGoodsResponseDto.getPageCount());
                    } else {
                        SwingUtil.showNotification("访问出错，" + queryGoodsResponseDto.getMessage());
                    }
                }
            }
        });

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyword = searchField.getText().trim();
                currentPage = 1;
                QueryGoodsResponseDto queryGoodsResponseDto = queryGoods(typeId, keyword, currentPage);
                if (queryGoodsResponseDto.getCode() == ResultCode.OK) {
                    refreshTableData(queryGoodsResponseDto.getData());
                    setPageIndicator(queryGoodsResponseDto.getPageCount());
                } else {
                    SwingUtil.showNotification("访问出错，" + queryGoodsResponseDto.getMessage());
                }
            }
        });

        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPage > 1) {
                    currentPage--;
                    QueryGoodsResponseDto queryGoodsResponseDto = queryGoods(typeId, keyword, currentPage);
                    if (queryGoodsResponseDto.getCode() == ResultCode.OK) {
                        refreshTableData(queryGoodsResponseDto.getData());
                        setPageIndicator(queryGoodsResponseDto.getPageCount());
                    } else {
                        SwingUtil.showNotification("访问出错，" + queryGoodsResponseDto.getMessage());
                    }
                }
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPage < pageCount) {
                    currentPage++;
                    QueryGoodsResponseDto queryGoodsResponseDto = queryGoods(typeId, keyword, currentPage);
                    if (queryGoodsResponseDto.getCode() == ResultCode.OK) {
                        refreshTableData(queryGoodsResponseDto.getData());
                        setPageIndicator(queryGoodsResponseDto.getPageCount());
                    } else {
                        SwingUtil.showNotification("访问出错，" + queryGoodsResponseDto.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 查询满足条件的所有商品
     *
     * @param typeId
     * @param keyword
     * @param currentPage
     * @return
     */
    private QueryGoodsResponseDto queryGoods(int typeId, String keyword, int currentPage) {
        QueryGoodsResponseDto queryGoodsResponseDto = new QueryGoodsResponseDto();
        try {
            QueryGoodsRequestDto queryGoodsRequestDto = new QueryGoodsRequestDto();
            queryGoodsRequestDto.setTypeId(typeId);
            queryGoodsRequestDto.setKeyword(keyword);
            queryGoodsRequestDto.setCurrentPage(currentPage - 1);
            queryGoodsRequestDto.setPageSize(30);
            queryGoodsResponseDto = HttpHandler.get("/query/goods?" + queryGoodsRequestDto.toString(), QueryGoodsResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryGoodsResponseDto;
    }

    /**
     * 查询所有商品类型
     *
     * @return
     */
    private QueryAllGoodsTypeResponseDto queryAllGoodsType() {
        QueryAllGoodsTypeResponseDto queryAllGoodsTypeResponseDto = new QueryAllGoodsTypeResponseDto();
        try {
            queryAllGoodsTypeResponseDto = HttpHandler.get("/query/goods/type", QueryAllGoodsTypeResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryAllGoodsTypeResponseDto;
    }

    /**
     * 准备数据
     */
    private void prepareData() {
        QueryAllGoodsTypeResponseDto queryAllGoodsTypeResponseDto = queryAllGoodsType();
        if (queryAllGoodsTypeResponseDto.getCode() == ResultCode.OK) {
            this.typeIdCache = queryAllGoodsTypeResponseDto.getId();
            this.setListItem(queryAllGoodsTypeResponseDto.getName());
        } else {
            SwingUtil.showNotification("访问出错，" + queryAllGoodsTypeResponseDto.getMessage());
        }
        if (!listModel.isEmpty()) {
            typeId = typeIdCache[0];
            QueryGoodsResponseDto queryGoodsResponseDto = queryGoods(typeId, keyword, currentPage);
            if (queryGoodsResponseDto.getCode() == ResultCode.OK) {
                refreshTableData(queryGoodsResponseDto.getData());
                setPageIndicator(queryGoodsResponseDto.getPageCount());
            } else {
                SwingUtil.showNotification("访问出错，" + queryGoodsResponseDto.getMessage());
            }
        }
    }

    /**
     * 刷新列表数据
     *
     * @param types
     */
    private void setListItem(String[] types) {
        if (types != null && types.length > 0) {
            listModel.removeAllElements();
            for (String element : types) {
                listModel.addElement(element);
            }
            list.setSelectedIndex(0);
        }
    }

    /**
     * 刷新表格数据
     *
     * @param tableBody
     */
    private void refreshTableData(Object[][] tableBody) {
        tableModel.setDataVector(tableBody, tableHead);
    }

    /**
     * 设置页面指示器
     *
     * @param pageCount
     */
    private void setPageIndicator(int pageCount) {
        this.pageCount = pageCount;
        pageIndicator.setText(currentPage + " / " + pageCount);
    }

    private WebPanel createRightPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.add(new WebScrollPane(table), "Center");
        webPanel.add(createFunctionPane(), "South");
        return webPanel;
    }

    private WebPanel createFunctionPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(10);
        webPanel.add(searchField, "West");
        webPanel.add(createButtonPane(), "East");
        return webPanel;
    }

    private WebPanel createButtonPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new FlowLayout());
        webPanel.add(previousButton, pageIndicator, nextButton);
        return webPanel;
    }

    private WebList createTypeList() {
        listModel = new DefaultListModel();
        WebList webList = new WebList(listModel);
        webList.setCellRenderer(new GoodsTypeRenderer());
        webList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return webList;
    }

    private WebTable createGoodsTable() {
        tableModel = new DefaultTableModel(null, tableHead);
        WebTable webTable = new WebTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        webTable.getTableHeader().setReorderingAllowed(false);
        webTable.getTableHeader().setResizingAllowed(false);
        DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
        tableCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        webTable.setDefaultRenderer(Object.class, tableCellRenderer);
        webTable.setShowVerticalLines(false);
        webTable.setRowHeight(30);
        return webTable;
    }

    private WebTextField createSearchField() {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setInputPrompt("条码/名称/拼音码");
        return webTextField;
    }

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }

    private WebLabel createPageIndicator() {
        return new WebLabel("-- / --");
    }

}
