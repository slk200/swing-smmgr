package org.tizzer.smmgr.system.view.dialog;

import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.UpdateEmployeeRequestDto;
import org.tizzer.smmgr.system.model.response.UpdateEmployeeResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateEmployeeDialog extends WebDialog {

    private static boolean isRefresh;
    private WebTextField staffNoField;
    private WebTextField nameField;
    private WebTextField phoneField;
    private WebTextField addressField;
    private WebTextField createAtField;
    private WebTextField storeField;
    private WebCheckBox adminBox;
    private WebCheckBox enableBox;
    private WebButton updateButton;
    private WebButton cancelButton;

    private Object[] dataCache;

    public UpdateEmployeeDialog() {
        super(RuntimeConstants.root, "编辑员工信息", true);
        staffNoField = createDisabledTextField();
        nameField = createDisabledTextField();
        phoneField = createInfoTextField();
        addressField = createInfoTextField();
        createAtField = createDisabledTextField();
        storeField = createDisabledTextField();
        adminBox = createInfoBox("管理员");
        enableBox = createInfoBox("启用");
        updateButton = createBootstrapButton("修改");
        cancelButton = createBootstrapButton("取消");

        this.add(createContentPane());
        this.pack();
        this.setLocationRelativeTo(RuntimeConstants.root);
        this.initListener();
    }

    public static boolean newInstance(Object[] editData) {
        UpdateEmployeeDialog updateEmployeeDialog = new UpdateEmployeeDialog();
        updateEmployeeDialog.setDataCache(editData);
        updateEmployeeDialog.setVisible(true);
        return isRefresh;
    }

    private void setDataCache(Object[] editData) {
        this.dataCache = editData;
        staffNoField.setText(editData[0].toString());
        nameField.setText(editData[1].toString());
        phoneField.setText(editData[2].toString());
        addressField.setText(editData[3].toString());
        createAtField.setText(editData[5].toString());
        storeField.setText(editData[4].toString());
        adminBox.setSelected((Boolean) editData[6]);
        enableBox.setSelected((Boolean) editData[7]);
    }

    private void initListener() {
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String phone = phoneField.getText().trim();
                if (phone.equals("")) {
                    SwingUtil.showTip(phoneField, "电话不能为空");
                    return;
                }
                String address = addressField.getText().trim();
                if (address.equals("")) {
                    SwingUtil.showTip(addressField, "地址不能为空");
                    return;
                }
                if (phone.equals(dataCache[2]) && address.equals(dataCache[3]) && dataCache[6].equals(adminBox.isSelected()) && dataCache[7].equals(enableBox.isSelected())) {
                    dispose();
                    return;
                }
                UpdateEmployeeResponseDto updateEmployeeResponseDto = updateEmployee(phone, address);
                if (updateEmployeeResponseDto.getCode() == ResultCode.OK) {
                    isRefresh = true;
                    dispose();
                } else {
                    SwingUtil.showTip(updateButton, "修改失败");
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

    /**
     * 更新员工资料
     *
     * @param phone
     * @param address
     * @return
     */
    private UpdateEmployeeResponseDto updateEmployee(String phone, String address) {
        UpdateEmployeeResponseDto updateEmployeeResponseDto = new UpdateEmployeeResponseDto();
        try {
            UpdateEmployeeRequestDto updateEmployeeRequestDto = new UpdateEmployeeRequestDto();
            updateEmployeeRequestDto.setStaffNo(dataCache[0].toString());
            updateEmployeeRequestDto.setPhone(phone);
            updateEmployeeRequestDto.setAddress(address);
            updateEmployeeRequestDto.setAdmin(adminBox.isSelected());
            updateEmployeeRequestDto.setEnable(enableBox.isSelected());
            updateEmployeeResponseDto = HttpHandler.post("/update/employee", updateEmployeeRequestDto.toString(), UpdateEmployeeResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateEmployeeResponseDto;
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        SwingUtil.setupComponent(webPanel, new WebLabel("员工号："), 0, 0, 1, 1);
        SwingUtil.setupComponent(webPanel, staffNoField, 1, 0, 1, 1);
        SwingUtil.setupComponent(webPanel, new WebLabel("姓名："), 0, 1, 1, 1);
        SwingUtil.setupComponent(webPanel, nameField, 1, 1, 1, 1);
        SwingUtil.setupComponent(webPanel, new WebLabel("电话："), 0, 2, 1, 1);
        SwingUtil.setupComponent(webPanel, phoneField, 1, 2, 1, 1);
        SwingUtil.setupComponent(webPanel, new WebLabel("地址："), 0, 3, 1, 1);
        SwingUtil.setupComponent(webPanel, addressField, 1, 3, 1, 1);
        SwingUtil.setupComponent(webPanel, new WebLabel("注册时间："), 0, 4, 1, 1);
        SwingUtil.setupComponent(webPanel, createAtField, 1, 4, 1, 1);
        SwingUtil.setupComponent(webPanel, new WebLabel("所属门店："), 0, 5, 1, 1);
        SwingUtil.setupComponent(webPanel, storeField, 1, 5, 1, 1);
        SwingUtil.setupComponent(webPanel, new WebLabel("权限："), 0, 6, 1, 1);
        SwingUtil.setupComponent(webPanel, adminBox, 1, 6, 1, 1);
        SwingUtil.setupComponent(webPanel, new WebLabel("状态："), 0, 7, 1, 1);
        SwingUtil.setupComponent(webPanel, enableBox, 1, 7, 1, 1);
        SwingUtil.setupComponent(webPanel, createButtonPane(), 0, 8, 2, 1);
        return webPanel;
    }

    private WebPanel createButtonPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        webPanel.add(updateButton);
        webPanel.add(cancelButton);
        return webPanel;
    }

    private WebTextField createInfoTextField() {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setInputPrompt("必填");
        return webTextField;
    }

    private WebTextField createDisabledTextField() {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setEnabled(false);
        return webTextField;
    }

    private WebCheckBox createInfoBox(String text) {
        return new WebCheckBox(text);
    }

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }
}
