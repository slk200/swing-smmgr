package org.tizzer.smmgr.system.view;

import com.alee.extended.date.WebDateField;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.component.WebBSButton;
import org.tizzer.smmgr.system.constant.SystemConstants;
import org.tizzer.smmgr.system.manager.IconManager;
import org.tizzer.smmgr.system.model.request.QueryGoodsRequestDto;
import org.tizzer.smmgr.system.model.response.QueryGoodsResponseDto;
import org.tizzer.smmgr.system.resolver.HttpResolver;
import org.tizzer.smmgr.system.template.Initialization;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

/**
 * @author tizzer
 * @version 1.0
 */
public class SalesRecordBoundary extends Initialization {

    private static final Object[] PAGE_SIZE = {30, 50, 100};
    private static final Object[] TABLE_HEAD = {"条形码", "名称", "种类", "采购价", "零售价", "库存"};

    private Object[][] tableBody = new Object[30][TABLE_HEAD.length];
    private String mKeyWord = "";
    private int mCurrentPage = 1;
    private int mPageSize = 30;
    private int mPageCount;
    private WebDateField field$start$date;
    private WebDateField field$end$date;
    private WebTextField field$search;
    private WebButton button$search;
    private WebComboBox box$page$size;
    private DefaultTableModel table$model;
    private WebTable table$data;
    private WebButton button$prev$page;
    private WebLabel label$page$indicator;
    private WebTextField field$spec$page;
    private WebButton button$go$to;
    private WebButton button$next$page;

    public SalesRecordBoundary() {
        super();
        refreshData(mKeyWord, mCurrentPage, mPageSize);
    }

    @Override
    public void initProp() {
        setOpaque(false);
    }

    @Override
    public void initVal() {
        field$start$date = new WebDateField();
        field$start$date.setEditable(false);
        field$start$date.setDateFormat(SystemConstants._START_TIME_FORM);
        field$start$date.setText(SystemConstants._DATE_OF_MANUFACTURE + " 00:00:00");
        field$end$date = new WebDateField();
        field$end$date.setEditable(false);
        field$end$date.setDateFormat(SystemConstants._DEFAULT_TIME_FORM);
        field$end$date.setText(SystemConstants._DEFAULT_TIME_FORM.format(new Date()));
        button$search = new WebButton();
        button$search.setIcon(IconManager._ICON_SEARCH);
        button$search.setFocusable(false);
        button$search.setShadeWidth(0);
        button$search.setMoveIconOnPress(false);
        button$search.setRolloverDecoratedOnly(true);
        button$search.setCursor(Cursor.getDefaultCursor());
        field$search = new WebTextField(20);
        field$search.setMargin(-1, 2, -1, -1);
        field$search.setTrailingComponent(button$search);
        box$page$size = new WebComboBox(PAGE_SIZE);
        table$model = new DefaultTableModel(tableBody, TABLE_HEAD);
        table$data = new WebTable(table$model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
        tableCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table$data.setDefaultRenderer(Object.class, tableCellRenderer);
        table$data.setShowVerticalLines(false);
        table$data.getTableHeader().setReorderingAllowed(false);
        table$data.getTableHeader().setResizingAllowed(false);
        button$prev$page = new WebBSButton("<上一页", WebBSButton.BLUE);
        label$page$indicator = new WebLabel("--/--");
        field$spec$page = new WebTextField(4);
        field$spec$page.setHorizontalAlignment(SwingConstants.CENTER);
        button$go$to = new WebBSButton("转到>", WebBSButton.BLUE);
        button$next$page = new WebBSButton("下一页>", WebBSButton.BLUE);
    }

    @Override
    public void initView() {
        add(new WebPanel() {{
            setOpaque(false);
            add(new WebPanel() {{
                setOpaque(false);
                add(new WebPanel() {{
                    setOpaque(false);
                    setLayout(new FlowLayout());
                    add(new WebLabel("起始时间："));
                    add(field$start$date);
                    add(new WebLabel("结束时间："));
                    add(field$end$date);
                }}, "West");
                add(new WebPanel() {{
                    setOpaque(false);
                    setLayout(new FlowLayout());
                    add(field$search);
                }}, "Center");
                add(new WebLabel() {{
                    setOpaque(false);
                    setLayout(new FlowLayout());
                    add(new WebLabel("每页显示："));
                    add(box$page$size);
                }}, "East");
            }}, "North");
            add(new WebScrollPane(table$data));
            add(new WebPanel() {{
                setOpaque(false);
                setLayout(new FlowLayout());
                add(button$prev$page, label$page$indicator, field$spec$page, button$go$to, button$next$page);
            }}, "South");
        }});
    }

    @Override
    public void initAction() {
        button$search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyWord = field$search.getText();
                if (!keyWord.equals(mKeyWord)) {
                    mKeyWord = keyWord;
                    mCurrentPage = 1;
                    refreshData(mKeyWord, mCurrentPage, mPageSize);
                }
            }
        });
        field$search.addActionListener(button$search.getActionListeners()[0]);

        box$page$size.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    mPageSize = (int) e.getItem();
                    mCurrentPage = 1;
                    refreshData(mKeyWord, mCurrentPage, mPageSize);
                }
            }
        });

        button$prev$page.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mCurrentPage > 1) {
                    mCurrentPage--;
                    refreshData(mKeyWord, mCurrentPage, mPageSize);
                }
            }
        });

        button$go$to.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String page = field$spec$page.getText();
                if (!page.matches("[0-9]+")) {
                    return;
                }
                if (Integer.parseInt(page) == mCurrentPage) {
                    return;
                }
                if (Integer.parseInt(page) < 1) {
                    return;
                }
                if (Integer.parseInt(page) > mPageCount) {
                    return;
                }
                mCurrentPage = Integer.parseInt(page);
                refreshData(mKeyWord, mCurrentPage, mPageSize);
            }
        });

        button$next$page.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mCurrentPage < mPageCount) {
                    mCurrentPage++;
                    refreshData(mKeyWord, mCurrentPage, mPageSize);
                }
            }
        });
    }

    /**
     * 刷新数据
     *
     * @param keyWord
     * @param currentPage
     * @param pageSize
     */
    private void refreshData(String keyWord, int currentPage, int pageSize) {
        try {
            QueryGoodsRequestDto queryGoodsRequestDto = new QueryGoodsRequestDto();
            queryGoodsRequestDto.setCurrentPage(currentPage - 1);
            queryGoodsRequestDto.setPageSize(pageSize);
            queryGoodsRequestDto.setKeyword(keyWord);
            QueryGoodsResponseDto queryGoodsResponseDto = HttpResolver.post("/queryGoods", queryGoodsRequestDto.toString(), QueryGoodsResponseDto.class);
            tableBody = queryGoodsResponseDto.getData();
            mPageCount = Math.toIntExact(queryGoodsResponseDto.getPageCount());
            table$model.setDataVector(tableBody, TABLE_HEAD);
            label$page$indicator.setText(mCurrentPage + "/" + mPageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
