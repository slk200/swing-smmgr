package org.tizzer.smmgr.system.view.dialog;

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
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.DeleteInsiderTypeRequestDto;
import org.tizzer.smmgr.system.model.request.SaveInsiderTypeRequestDto;
import org.tizzer.smmgr.system.model.request.UpdateInsiderTypeRequestDto;
import org.tizzer.smmgr.system.model.response.DeleteInsiderTypeResponseDto;
import org.tizzer.smmgr.system.model.response.QueryAllInsiderTypeResponseDto;
import org.tizzer.smmgr.system.model.response.SaveInsiderTypeResponseDto;
import org.tizzer.smmgr.system.model.response.UpdateInsiderTypeResponseDto;
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
import java.util.Vector;

public class UpdateInsiderTypeDialog extends WebDialog {

    private final static Object[] tableHead = {"序号", "会员等级", "会员折扣"};
    private static boolean isRefresh;

    private DefaultTableModel tableModel;
    private WebTable table;
    private WebTextField nameField;
    private WebSpinner discountField;
    private WebButton addButton;
    private WebButton updateButton;
    private WebButton delButton;
    private WebButton cancelButton;

    public UpdateInsiderTypeDialog() {
        super(RuntimeConstants.root, "编辑会员等级", true);
        table = createInsiderTypeTable();
        nameField = createInsiderTypeNameField();
        discountField = createInsiderDiscountSpinner();
        addButton = createBootstrapButton("新增");
        updateButton = createBootstrapButton("修改");
        delButton = createBootstrapButton("删除");
        cancelButton = createBootstrapButton("取消");

        this.add(createContentPane());
        this.refreshData();
        this.pack();
        this.setLocationRelativeTo(RuntimeConstants.root);
        this.initListener();
    }

    public static boolean newInstance() {
        UpdateInsiderTypeDialog updateInsiderTypeDialog = new UpdateInsiderTypeDialog();
        updateInsiderTypeDialog.setVisible(true);
        return isRefresh;
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

        delButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectedRow() == -1) {
                    SwingUtil.showTip(delButton, "请至少选择一条数据");
                    return;
                }
                Vector<Integer> id = new Vector<>();
                for (int row : table.getSelectedRows()) {
                    id.add((Integer) table.getValueAt(row, 0));
                }
                DeleteInsiderTypeResponseDto deleteInsiderTypeResponseDto = deleteInsiderType(id);
                if (deleteInsiderTypeResponseDto.getCode() != ResultCode.OK) {
                    SwingUtil.showTip(delButton, deleteInsiderTypeResponseDto.getMessage());
                } else {
                    refreshData();
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
                for (int i = 0; i < table.getRowCount(); i++) {
                    if (name.equals(table.getValueAt(i, 1))) {
                        SwingUtil.showTip(addButton, "会员等级与第" + (i + 1) + "行重复");
                        return;
                    }
                    if (discountField.getValue().equals(table.getValueAt(i, 2))) {
                        SwingUtil.showTip(addButton, "会员折扣与第" + (i + 1) + "行重复");
                        return;
                    }
                }
                SaveInsiderTypeResponseDto saveInsiderTypeResponseDto = saveInsiderType(name);
                if (saveInsiderTypeResponseDto.getCode() != ResultCode.OK) {
                    SwingUtil.showTip(addButton, saveInsiderTypeResponseDto.getMessage());
                } else {
                    refreshData();
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
                if (name.equals(table.getValueAt(row, 1)) && discountField.getValue().equals(table.getValueAt(row, 2))) {
                    SwingUtil.showTip(updateButton, "并没有修改信息");
                    return;
                }
                for (int i = 0; i < table.getRowCount(); i++) {
                    if (i == row) {
                        continue;
                    }
                    if (name.equals(table.getValueAt(i, 1))) {
                        SwingUtil.showTip(updateButton, "会员等级与第" + (i + 1) + "行重复");
                        return;
                    }
                    if (discountField.getValue().equals(table.getValueAt(i, 2))) {
                        SwingUtil.showTip(updateButton, "会员折扣与第" + (i + 1) + "行重复");
                        return;
                    }
                }
                UpdateInsiderTypeResponseDto updateInsiderTypeResponseDto = updateInsiderType(name);
                if (updateInsiderTypeResponseDto.getCode() != ResultCode.OK) {
                    SwingUtil.showTip(updateButton, updateInsiderTypeResponseDto.getMessage());
                } else {
                    refreshData();
                    isRefresh = true;
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private DeleteInsiderTypeResponseDto deleteInsiderType(Vector<Integer> id) {
        DeleteInsiderTypeResponseDto deleteInsiderTypeResponseDto = new DeleteInsiderTypeResponseDto();
        try {
            DeleteInsiderTypeRequestDto deleteInsiderTypeRequestDto = new DeleteInsiderTypeRequestDto();
            deleteInsiderTypeRequestDto.setId(id);
            deleteInsiderTypeResponseDto = HttpHandler.post("/delete/insider/type", deleteInsiderTypeRequestDto.toString(), DeleteInsiderTypeResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return deleteInsiderTypeResponseDto;
    }

    private SaveInsiderTypeResponseDto saveInsiderType(String name) {
        SaveInsiderTypeResponseDto saveInsiderTypeResponseDto = new SaveInsiderTypeResponseDto();
        try {
            SaveInsiderTypeRequestDto saveInsiderTypeRequestDto = new SaveInsiderTypeRequestDto();
            saveInsiderTypeRequestDto.setName(name);
            saveInsiderTypeRequestDto.setDiscount((Integer) discountField.getValue());
            saveInsiderTypeResponseDto = HttpHandler.post("/save/insider/type", saveInsiderTypeRequestDto.toString(), SaveInsiderTypeResponseDto.class);
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
            updateInsiderTypeRequestDto.setId((Integer) table.getValueAt(table.getSelectedRow(), 0));
            updateInsiderTypeRequestDto.setName(name);
            updateInsiderTypeRequestDto.setDiscount((Integer) discountField.getValue());
            updateInsiderTypeResponseDto = HttpHandler.post("/update/insider/type", updateInsiderTypeRequestDto.toString(), UpdateInsiderTypeResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return updateInsiderTypeResponseDto;
    }

    private void refreshData() {
        try {
            QueryAllInsiderTypeResponseDto queryAllInsiderTypeResponseDto = HttpHandler.get("/query/insider/type", QueryAllInsiderTypeResponseDto.class);
            tableModel.setDataVector(queryAllInsiderTypeResponseDto.getData(), tableHead);
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
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(99, 10, 99, 1);
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
        webPanel.setLayout(new GridLayout(2, 3, 5, 5));
        webPanel.add(nameField, discountField, delButton, addButton, updateButton, cancelButton);
        return webPanel;
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        webPanel.add(new WebScrollPane(table), "Center");
        webPanel.add(createInsiderTypeInfOperationPanel(), "South");
        return webPanel;
    }

}
