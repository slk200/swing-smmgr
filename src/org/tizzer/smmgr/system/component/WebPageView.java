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
import org.tizzer.smmgr.system.util.TextUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class WebPageView extends WebPanel {

    /**
     * params supplied for searching
     */
    private static final Object[] defaultPageSizeArray = {30, 50, 100};
    private Date startDate;
    private Date endDate;
    private String keyword = "";
    private int currentPage = 1;
    private int pageSize = 30;
    private int pageCount;

    /**
     * the table's data
     */
    private Object[] tableHead;
    private Object[][] tableBody;

    /**
     * the components to make up the page view
     */
    private WebDateField startDateField;
    private WebDateField endDateField;
    private WebButton searchButton;
    private WebTextField keywordField;
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
        startDateField.setDateFormat(new SimpleDateFormat("yyyy-MM-dd 00:00:00"));
        endDateField = createDateField();
        endDateField.setDateFormat(new SimpleDateFormat("yyyy-MM-dd 23:59:59"));
        searchButton = createTrailingComponent();
        keywordField = createSearchField();
        pageSizeComboBox = new WebComboBox(defaultPageSizeArray);
        table = createPageTable();
        previousPageButton = createBootstrapButton("上一页");
        pageIndicator = new WebLabel("--/--");
        specPageField = createSpecPageField();
        gotoButton = createBootstrapButton("转到");
        nextPageButton = createBootstrapButton("下一页");

        this.setOpaque(false);
        this.add(createTopPanel(), "North");
        this.add(createCenterPanel(), "Center");
        this.add(createBottomPanel(), "South");
        this.initListener();
    }

    /**
     * initialize listener for all components
     */
    private void initListener() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                specPageField.setText(null);
                currentPage = 1;
                startDate = TextUtil.startOfDay(startDateField.getText());
                endDate = TextUtil.endOfDay(endDateField.getText());
                keyword = keywordField.getText();
                dataChangeListener.dataChanged(startDate, endDate, keyword, pageSize, currentPage);
            }
        });
        keywordField.addActionListener(searchButton.getActionListeners()[0]);
        pageSizeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    specPageField.setText(null);
                    currentPage = 1;
                    pageSize = (int) e.getItem();
                    dataChangeListener.dataChanged(startDate, endDate, keyword, pageSize, currentPage);
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

    /**
     * set input prompt<br/>
     * description:like placeholder in html
     *
     * @param inputPrompt
     */
    public void setInputPrompt(String inputPrompt) {
        keywordField.setInputPrompt(inputPrompt);
    }

    /**
     * set table's head and body<br/>
     * description:if you want to set data for the table at the class initialize
     *
     * @param tableBody
     * @param tableHead
     */
    private void setTableData(Object[][] tableBody, Object[] tableHead) {
        this.tableHead = tableHead;
        this.tableBody = tableBody;
        tableModel.setDataVector(tableBody, tableHead);
    }

    /**
     * set current page and total pages
     *
     * @param pageCount
     */
    public void setPageIndicator(int pageCount) {
        this.pageCount = pageCount;
        pageIndicator.setText(currentPage + " / " + pageCount);
    }

    /**
     * set table's head and body<br/>
     * set current page and total pages<br/>
     * description:if you want to set data for the table at class initialize
     *
     * @param tableHead
     * @param tableBody
     * @param pageCount
     */
    public void prepareData(Object[] tableHead, Object[][] tableBody, int pageCount) {
        setTableData(tableBody, tableHead);
        setPageIndicator(pageCount);
    }

    /**
     * set your custom page size array
     *
     * @param pageSize
     */
    public void setPageSizeArray(Object[] pageSize) {
        this.pageSize = (int) pageSize[0];
        pageSizeComboBox.setModel(new DefaultComboBoxModel(pageSize));
    }

    /**
     * set the table's selection mode<br/>
     * description: The selection mode used by the row and column selection models.<br/>
     * enum:
     * #SINGLE_SELECTION            <code>ListSelectionModel.SINGLE_SELECTION<code/>
     * #SINGLE_INTERVAL_SELECTION   <code>ListSelectionModel.SINGLE_INTERVAL_SELECTION<code/>
     * #MULTIPLE_INTERVAL_SELECTION <code>ListSelectionModel.MULTIPLE_INTERVAL_SELECTION<code/>
     *
     * @param selectionMode
     */
    public void setSelectionMode(int selectionMode) {
        table.setSelectionMode(selectionMode);
    }

    /**
     * set <code>columnIndex<code/> column's cell renderer
     *
     * @param columnIndex
     * @param cellRenderer
     */
    public void setColumnCellRenderer(int columnIndex, TableCellRenderer cellRenderer) {
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
    }

    /**
     * keep table's head,change its body
     *
     * @param tableBody
     */
    public void setTableBody(Object[][] tableBody) {
        this.tableBody = tableBody;
        tableModel.setDataVector(tableBody, tableHead);
    }

    /**
     * to refresh table's data<br/>
     * description:call it when updating or adding or delete
     */
    public void refresh() {
        dataChangeListener.dataChanged(startDate, endDate, keyword, pageSize, currentPage);
    }

    /**
     * get the table's head
     *
     * @return
     */
    public Object[] getTableHead() {
        return tableHead;
    }

    /**
     * get the table's body
     *
     * @return
     */
    public Object[][] getTableBody() {
        return tableBody;
    }

    /**
     * get the selected row's index<br/>
     * description:if you not choose,return <code>-1<code/>
     *
     * @return
     */
    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    /**
     * get the selected rows' index array<br/>
     * description:if you not choose,return <code>null<code/>
     *
     * @return
     */
    public int[] getSelectedRows() {
        return table.getSelectedRows();
    }

    /**
     * get the <code>columnIndex<code/> column's data array<br/>
     * range at selected rows
     *
     * @param columnIndex
     * @return
     */
    public <T> Vector<T> getSelectedRowsColumnIndexData(Class<T> clazz, int columnIndex) {
        Vector<T> vector = new Vector<>();
        int[] selectedRows = table.getSelectedRows();
        for (int i = 0; i < selectedRows.length; i++) {
            vector.add((T) tableBody[selectedRows[i]][columnIndex]);
        }
        return vector;
    }

    /**
     * get the selected row's data
     *
     * @param index
     * @return
     */
    public Object[] getSelectedRowData(int index) {
        if (index < 0 || index > tableBody.length) {
            throw new IndexOutOfBoundsException("the index not in the range of rows");
        }
        return tableBody[index];
    }

    /**
     * get current page
     *
     * @return
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * get current page size
     *
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * add listener<br/>
     * description:monitor mouse event,change table's data and params for searching
     *
     * @param dataChangeListener
     */
    public void addDateChangeListener(DataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    /**
     * response for page event
     *
     * @param pageEvent
     */
    private void pageChange(PageEvent pageEvent) {
        switch (pageEvent) {
            case PREVIOUS:
                if (currentPage > 1) {
                    specPageField.setText(null);
                    currentPage--;
                    dataChangeListener.dataChanged(startDate, endDate, keyword, pageSize, currentPage);
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
                dataChangeListener.dataChanged(startDate, endDate, keyword, pageSize, currentPage);
                break;
            case NEXT:
                if (currentPage < pageCount) {
                    specPageField.setText(null);
                    currentPage++;
                    dataChangeListener.dataChanged(startDate, endDate, keyword, pageSize, currentPage);
                }
                break;
            default:
        }
    }

    private WebDateField createDateField() {
        WebDateField webDateField = new WebDateField();
        webDateField.setColumns(10);
        webDateField.setMargin(2);
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
        webPanel.setOpaque(false);
        webPanel.add(new WebLabel("起始时间"));
        webPanel.add(startDateField);
        webPanel.add(new WebLabel("截止时间"));
        webPanel.add(endDateField);
        webPanel.add(Box.createHorizontalStrut(100));
        webPanel.add(keywordField);
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
        webPanel.setOpaque(false);
        webPanel.setLayout(new FlowLayout());
        webPanel.add(previousPageButton);
        webPanel.add(pageIndicator);
        webPanel.add(specPageField);
        webPanel.add(gotoButton);
        webPanel.add(nextPageButton);
        return webPanel;
    }

    /**
     * distinguish different page event
     */
    private enum PageEvent {
        PREVIOUS, GOTO, NEXT;
    }

}
