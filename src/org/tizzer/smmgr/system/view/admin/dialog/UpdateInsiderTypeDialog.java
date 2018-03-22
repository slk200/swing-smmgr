package org.tizzer.smmgr.system.view.admin.dialog;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.spinner.WebSpinner;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.model.request.SaveInsiderTypeRequestDto;
import org.tizzer.smmgr.system.model.request.UpdateInsiderTypeRequestDto;
import org.tizzer.smmgr.system.model.response.QueryAllInsiderTypeResponseDto;
import org.tizzer.smmgr.system.model.response.SaveInsiderTypeResponseDto;
import org.tizzer.smmgr.system.model.response.UpdateInsiderTypeResponseDto;
import org.tizzer.smmgr.system.resolver.HttpResolver;
import org.tizzer.smmgr.system.util.NPatchUtil;
import org.tizzer.smmgr.system.util.SwingUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UpdateInsiderTypeDialog extends WebDialog {

    private final static Object[] tableHead = {"序号", "会员等级", "会员折扣"};

    private DefaultTableModel tableModel;
    private WebTable table;
    private WebTextField nameField;
    private WebSpinner discountField;
    private WebButton addButton;
    private WebButton updateButton;

    private Object[][] dataCache;
    private static boolean isRefresh;

    public static boolean newInstance() {
        UpdateInsiderTypeDialog updateInsiderTypeDialog = new UpdateInsiderTypeDialog();
        updateInsiderTypeDialog.setVisible(true);
        return isRefresh;
    }

    public UpdateInsiderTypeDialog() {
        super(RuntimeConstants.root, "设置会员等级", true);
        table = createInsiderTypeTable();
        nameField = createInsiderTypeNameField();
        discountField = createInsiderDiscountSpinner();
        addButton = createBootstrapButton("新增");
        updateButton = createBootstrapButton("修改");

        this.add(createContentPane());
        this.refreshData();
        this.pack();
        this.setLocationRelativeTo(RuntimeConstants.root);
        this.initListener();
    }

    private void initListener() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    nameField.setText((String) table.getValueAt(row, 1));
                    discountField.setValue(table.getValueAt(row, 2));
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                if (name.equals("")) {
                    SwingUtil.showTip(nameField, "会员等级不能为空");
                    return;
                }
                for (int i = 0; i < dataCache.length; i++) {
                    if (name.equals(dataCache[i][1])) {
                        SwingUtil.showTip(addButton, "会员等级与第" + (i + 1) + "行重复");
                        return;
                    }
                    if (discountField.getValue().equals(dataCache[i][2])) {
                        SwingUtil.showTip(addButton, "会员折扣与第" + (i + 1) + "行重复");
                        return;
                    }
                }
                SaveInsiderTypeResponseDto saveInsiderTypeResponseDto = saveInsiderType(name);
                if (saveInsiderTypeResponseDto.getCode() != ResultCode.OK) {
                    SwingUtil.showTip(addButton, saveInsiderTypeResponseDto.getMessage());
                } else {
                    refreshData();
                    isRefresh = true;
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                if (name.equals("")) {
                    SwingUtil.showTip(nameField, "会员等级不能为空");
                    return;
                }
                int row = table.getSelectedRow();
                if (name.equals(dataCache[row][1]) && discountField.getValue().equals(dataCache[row][2])) {
                    SwingUtil.showTip(updateButton, "并没有修改信息");
                    return;
                }
                for (int i = 0; i < dataCache.length; i++) {
                    if (i == row) {
                        continue;
                    }
                    if (name.equals(dataCache[i][1])) {
                        SwingUtil.showTip(updateButton, "会员等级与第" + (i + 1) + "行重复");
                        return;
                    }
                    if (discountField.getValue().equals(dataCache[i][2])) {
                        SwingUtil.showTip(updateButton, "会员折扣与第" + (i + 1) + "行重复");
                        return;
                    }
                }
                UpdateInsiderTypeResponseDto updateInsiderTypeResponseDto = updateInsiderType(name);
                if (updateInsiderTypeResponseDto.getCode() != ResultCode.OK) {
                    SwingUtil.showTip(updateButton, updateInsiderTypeResponseDto.getMessage());
                } else {
                    refreshData();
                }
            }
        });
    }

    private SaveInsiderTypeResponseDto saveInsiderType(String name) {
        SaveInsiderTypeResponseDto saveInsiderTypeResponseDto = new SaveInsiderTypeResponseDto();
        try {
            SaveInsiderTypeRequestDto saveInsiderTypeRequestDto = new SaveInsiderTypeRequestDto();
            saveInsiderTypeRequestDto.setName(name);
            saveInsiderTypeRequestDto.setDiscount((Integer) discountField.getValue());
            saveInsiderTypeResponseDto = HttpResolver.post("/save/insider/type", saveInsiderTypeRequestDto.toString(), SaveInsiderTypeResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return saveInsiderTypeResponseDto;
    }

    private UpdateInsiderTypeResponseDto updateInsiderType(String name) {
        UpdateInsiderTypeResponseDto updateInsiderTypeResponseDto = new UpdateInsiderTypeResponseDto();
        try {
            UpdateInsiderTypeRequestDto updateInsiderTypeRequestDto = new UpdateInsiderTypeRequestDto();
            updateInsiderTypeRequestDto.setId((Integer) dataCache[table.getSelectedRow()][0]);
            updateInsiderTypeRequestDto.setName(name);
            updateInsiderTypeRequestDto.setDiscount((Integer) discountField.getValue());
            updateInsiderTypeResponseDto = HttpResolver.post("/update/insider/type", updateInsiderTypeRequestDto.toString(), UpdateInsiderTypeResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return updateInsiderTypeResponseDto;
    }

    private void refreshData() {
        try {
            QueryAllInsiderTypeResponseDto queryAllInsiderTypeResponseDto = HttpResolver.get("/query/insider/type", QueryAllInsiderTypeResponseDto.class);
            this.dataCache = queryAllInsiderTypeResponseDto.getData();
            tableModel.setDataVector(dataCache, tableHead);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WebTable createInsiderTypeTable() {
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
        webTable.getTableHeader().setReorderingAllowed(false);
        webTable.getTableHeader().setResizingAllowed(false);
        webTable.setRowHeight(30);
        webTable.setShowVerticalLines(false);
        webTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return webTable;
    }

    private WebTextField createInsiderTypeNameField() {
        WebTextField webTextField = new WebTextField(8);
        webTextField.setInputPrompt("会员等级");
        webTextField.setRound(5);
        return webTextField;
    }

    private WebSpinner createInsiderDiscountSpinner() {
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(99, 50, 99, 1);
        return new WebSpinner(spinnerNumberModel);
    }

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }

    private WebPanel createInsiderTypeInfOperationPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setMargin(10, 0, 0, 0);
        webPanel.setOpaque(false);
        webPanel.setLayout(new GridLayout(2, 2, 5, 5));
        webPanel.add(nameField, discountField, addButton, updateButton);
        return webPanel;
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        webPanel.add(new WebScrollPane(table), BorderLayout.CENTER);
        webPanel.add(createInsiderTypeInfOperationPanel(), BorderLayout.SOUTH);
        return webPanel;
    }

}
