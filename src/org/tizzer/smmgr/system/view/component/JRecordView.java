package org.tizzer.smmgr.system.view.component;

import com.alee.extended.date.WebDateField;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollBar;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.constant.IconManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;

/**
 * @author tizzer
 * @version 1.0
 */
public class JRecordView extends WebPanel {
    /**
     * params for load new data
     */
    private final static int loadSize = 20;
    private int curLoadIndex = 1;
    private String startDate;
    private String endDate;
    private String keyword = "";
    private int loadPage = 1;
    /**
     * the components to make up the record view
     */
    private WebDateField startDateField;
    private WebDateField endDateField;
    private WebTextField searchField;
    private WebButton searchButton;
    private WebTable table;
    private DefaultListModel listModel;
    private WebList list;
    private DefaultTableModel tableModel;
    private WebScrollPane scrollPane;
    private WebScrollBar scrollBar;
    /**
     * cache table's head
     */
    private Object[] tableHead;
    /**
     * for set external component at the right panel's south direction
     */
    private WebPanel customPane;
    /**
     * to provide the interface for outer class
     */
    private RecordListener recordListener;

    public JRecordView() {
        startDateField = createDateField("yyyy-MM-dd 00:00:00");
        endDateField = createDateField("yyyy-MM-dd 23:59:59");
        searchButton = createTrailingComponent();
        searchField = createSearchField();
        table = createRecordTable();
        list = createRecordList();
        customPane = createRightPane();
        scrollPane = createScrollPane();
        scrollBar = scrollPane.getWebVerticalScrollBar();

        this.setOpaque(false);
        this.add(createTopPane(), "North");
        this.add(createBottomPane(), "Center");
        this.initListener();
    }

    private void initListener() {
        searchButton.addActionListener(e -> {
            startDate = startDateField.getText();
            endDate = endDateField.getText();
            keyword = searchField.getText().trim();
            curLoadIndex = 1;
            recordListener.searchPerformed(startDate, endDate, keyword, curLoadIndex, loadSize);
        });
        searchField.addActionListener(searchButton.getActionListeners()[0]);

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                recordListener.selectPerformed(list.getSelectedIndex());
            }
        });

        scrollBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                loadNew();
            }
        });

        scrollPane.addMouseWheelListener(e -> loadNew());
    }

    private void loadNew() {
        if (curLoadIndex < loadPage) {
            if (scrollBar.getValue() + scrollBar.getModel().getExtent() == scrollBar.getMaximum()) {
                curLoadIndex++;
                recordListener.loadPerformed(startDate, endDate, keyword, curLoadIndex, loadSize);
            }
        }
    }

    /**
     * set external container at right panel's south direction
     *
     * @param container
     */
    public void setExternalComponent(Container container) {
        customPane.add(container, BorderLayout.SOUTH);
        customPane.validate();
        customPane.repaint();
    }

    /**
     * set the placeholder like html at tag'<input/>'<br/>
     * for search field
     *
     * @param inputPrompt
     */
    public void setInputPrompt(String inputPrompt) {
        searchField.setInputPrompt(inputPrompt);
    }

    /**
     * set table's head<br/>
     * call it will cause the table's data to null<br/>
     * call it when the first time you initialize table's head
     *
     * @param tableHead
     */
    public void setTableHead(Object[] tableHead) {
        this.tableHead = tableHead;
        tableModel.setDataVector(null, tableHead);
    }

    /**
     * set renderer for list's each item
     *
     * @param cellRenderer
     */
    public void setListCellRenderer(ListCellRenderer cellRenderer) {
        list.setCellRenderer(cellRenderer);
    }

    /**
     * add record listener
     *
     * @param recordListener
     */
    public void addRecordListener(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    /**
     * it will clear list's model firstly<br/>
     * than put new element into list's model
     *
     * @param data
     */
    public void setListItem(Object[] data) {
        listModel.removeAllElements();
        if (data != null) {
            for (Object element : data) {
                listModel.addElement(element);
            }
        }
    }

    /**
     * put new element into list's model
     *
     * @param data
     */
    public void addListItem(Object[] data) {
        for (Object element : data) {
            listModel.addElement(element);
        }
    }

    /**
     * get selected list index
     *
     * @param index
     * @return
     */
    public Object getSelectedListSource(int index) {
        return listModel.get(index);
    }

    /**
     * set table's data
     *
     * @param tableBody
     */
    public void setTableBody(Object[][] tableBody) {
        tableModel.setDataVector(tableBody, tableHead);
    }

    /**
     * cache current load times
     *
     * @param loadPage
     */
    public void setLoadPage(int loadPage) {
        this.loadPage = loadPage;
    }

    private WebPanel createTopPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new FlowLayout());
        webPanel.add(new WebLabel("起止时间："));
        webPanel.add(startDateField);
        webPanel.add(new WebLabel(" — "));
        webPanel.add(endDateField);
        webPanel.add(createHSpace(100));
        webPanel.add(searchField);
        return webPanel;
    }

    private WebPanel createRightPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.add(new WebScrollPane(table), BorderLayout.CENTER);
        return webPanel;
    }

    private WebSplitPane createBottomPane() {
        WebSplitPane webPanel = new WebSplitPane(WebSplitPane.HORIZONTAL_SPLIT);
        webPanel.setOpaque(false);
        webPanel.setLeftComponent(scrollPane);
        webPanel.setRightComponent(customPane);
        return webPanel;
    }

    private WebDateField createDateField(String pattern) {
        WebDateField webDateField = new WebDateField();
        webDateField.setDateFormat(new SimpleDateFormat(pattern));
        webDateField.setColumns(15);
        webDateField.setMargin(2);
        webDateField.setEditable(false);
        return webDateField;
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
        WebTextField webTextField = new WebTextField(20);
        webTextField.setTrailingComponent(searchButton);
        webTextField.setMargin(-1, 2, -1, -1);
        return webTextField;
    }

    private WebList createRecordList() {
        listModel = new DefaultListModel();
        WebList webList = new WebList(listModel);
        webList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return webList;
    }

    private WebScrollPane createScrollPane() {
        return new WebScrollPane(list);
    }

    private WebTable createRecordTable() {
        tableModel = new DefaultTableModel();
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

    private Component createHSpace(int width) {
        return Box.createHorizontalStrut(width);
    }

    /**
     * 回调
     */
    public interface RecordListener {
        /**
         * monitor the params' change of the component<br/>
         * to obtain and refresh the component's list data
         *
         * @param startDate
         * @param endDate
         * @param keyword
         * @param curLoadIndex
         * @param loadSize
         */
        void searchPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize);

        /**
         * monitor the scrollbar's change<br/>
         * to obtain and refresh the component's list data
         *
         * @param startDate
         * @param endDate
         * @param keyword
         * @param curLoadIndex
         * @param loadSize
         */
        void loadPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize);

        /**
         * perform of the list's selection<br/>
         *
         * @param index
         */
        void selectPerformed(int index);

    }
}
