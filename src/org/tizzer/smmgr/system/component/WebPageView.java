package org.tizzer.smmgr.system.component;

import com.alee.extended.date.WebDateField;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.component.listener.DataChangeListener;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.util.NPatchUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebPageView extends WebPanel {
    private static final Object[] defaultPageSizeArray = {30, 50, 100};
    private Object[] tableHead;
    private int currentPage = 1;
    private int pageSize = 30;
    private int pageCount;
    private WebDateField startDateField;
    private WebDateField endDateField;
    private WebButton searchButton;
    private WebTextField keyWordField;
    private WebComboBox pageSizeComboBox;
    private DefaultTableModel tableModel;
    private WebTable table;
    private WebButton previousPageButton;
    private WebLabel pageIndicator;
    private WebTextField specPageField;
    private WebButton gotoButton;
    private WebButton nextPageButton;
    /**
     * monitor the param's change of the Component<br/>
     * to override its inner methods in outer class
     */
    private DataChangeListener dataChangeListener;

    public WebPageView() {
        startDateField = createDateField();
        endDateField = createDateField();
        searchButton = createTrailingComponent();
        keyWordField = createSearchField();
        pageSizeComboBox = new WebComboBox(defaultPageSizeArray);
        table = createPageTable();
        previousPageButton = createBootstrapButton("上一页");
        pageIndicator = new WebLabel("--/--");
        specPageField = createSpecPageField();
        gotoButton = createBootstrapButton("转到");
        nextPageButton = createBootstrapButton("下一页");
        this.add(createTopPanel(), "North");
        this.add(createCenterPanel(), "Center");
        this.add(createBottomPanel(), "South");
        this.initListener();
    }

    private void initListener() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataChange();
            }
        });
        keyWordField.addActionListener(searchButton.getActionListeners()[0]);
        pageSizeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    pageSize = (int) e.getItem();
                    dataChange();
                }
            }
        });
        previousPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pageChange(PageEvent.PREVIOUS);
            }
        });
        gotoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pageChange(PageEvent.GOTO);
            }
        });
        specPageField.addActionListener(gotoButton.getActionListeners()[0]);
        nextPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pageChange(PageEvent.NEXT);
            }
        });
    }

    public void setTableData(Object[][] tableBody, Object[] tableHead) {
        this.tableHead = tableHead;
        tableModel.setDataVector(tableBody, tableHead);
    }

    public void setTableBody(Object[][] tableBody) {
        tableModel.setDataVector(tableBody, tableHead);
    }

    public void setPageSizeArray(Object[] pageSize) {
        this.pageSize = (int) pageSize[0];
        pageSizeComboBox.setModel(new DefaultComboBoxModel<>(pageSize));
    }

    public void setPageIndicator(int pageCount) {
        this.pageCount = pageCount;
        pageIndicator.setText(currentPage + " / " + pageCount);
    }

    public void prepareData(Object[] tableHead, Object[][] tableBody, int pageCount) {
        setTableData(tableBody, tableHead);
        setPageIndicator(pageCount);
    }

    public Object[] getTableHead() {
        return tableHead;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void addDateChangeListener(DataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    private void pageChange(PageEvent pageEvent) {
        switch (pageEvent) {
            case PREVIOUS:
                if (currentPage > 1) {
                    currentPage--;
                    dataChange();
                }
                break;
            case GOTO:
                String text = specPageField.getText();
                if (!text.matches("[1-9]([0-9]*)?")) {
                    specPageField.setText(null);
                    return;
                }
                int specPage = Integer.parseInt(text);
                if (specPage == currentPage || specPage > pageCount) {
                    specPageField.setText(null);
                    return;
                }
                currentPage = specPage;
                dataChange();
                break;
            case NEXT:
                if (currentPage < pageCount) {
                    currentPage++;
                    dataChange();
                }
                break;
            default:
        }
    }

    private void dataChange() {
        Date startDate = startDateField.getDate();
        Date endDate = endDateField.getDate();
        String keyword = keyWordField.getText();
        dataChangeListener.dataChanged(startDate, endDate, keyword, pageSize, currentPage);
    }

    private WebDateField createDateField() {
        WebDateField webDateField = new WebDateField();
        webDateField.setColumns(10);
        webDateField.setMargin(2);
        webDateField.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        webDateField.setEditable(false);
        return webDateField;
    }

    private WebButton createTrailingComponent() {
        WebButton webButton = WebButton.createIconWebButton(IconManager._ICON_SEARCH, true);
        webButton.setFocusable(false);
        webButton.setShadeWidth(0);
        webButton.setMoveIconOnPress(false);
        webButton.setCursor(Cursor.getDefaultCursor());
        return webButton;
    }

    private WebTextField createSearchField() {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setTrailingComponent(searchButton);
        webTextField.setMargin(-1, 2, -1, -1);
        return webTextField;
    }

    private WebTable createPageTable() {
        tableModel = new DefaultTableModel();
        WebTable webTable = new WebTable(tableModel);
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
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        webButton.setForeground(Color.WHITE);
        return webButton;
    }

    private WebTextField createSpecPageField() {
        WebTextField webTextField = new WebTextField(4);
        webTextField.setHorizontalAlignment(SwingConstants.CENTER);
        return webTextField;
    }

    private WebPanel createTopPanel() {
        WebPanel webPanel = new WebPanel(new FlowLayout());
        webPanel.add(new WebLabel("起始时间"));
        webPanel.add(startDateField);
        webPanel.add(new WebLabel("截止时间"));
        webPanel.add(endDateField);
        webPanel.add(Box.createHorizontalStrut(100));
        webPanel.add(keyWordField);
        webPanel.add(pageSizeComboBox);
        return webPanel;
    }

    private WebPanel createCenterPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.add(new WebScrollPane(table));
        return webPanel;
    }

    private WebPanel createBottomPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new FlowLayout());
        webPanel.add(previousPageButton);
        webPanel.add(pageIndicator);
        webPanel.add(specPageField);
        webPanel.add(gotoButton);
        webPanel.add(nextPageButton);
        return webPanel;
    }

    private enum PageEvent {
        PREVIOUS, GOTO, NEXT;
    }

}
