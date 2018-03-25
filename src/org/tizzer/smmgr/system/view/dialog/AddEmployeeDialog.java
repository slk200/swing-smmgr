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
import org.tizzer.smmgr.system.model.request.SaveEmployeeRequestDto;
import org.tizzer.smmgr.system.model.response.SaveEmployeeResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

public class AddEmployeeDialog extends WebDialog {

    private static boolean isRefresh;
    private WebTextField staffNoField;
    private WebTextField passwordField;
    private WebTextField nameField;
    private WebTextField phoneField;
    private WebTextField addressField;
    private WebCheckBox adminBox;
    private WebButton addButton;
    private WebButton cancelButton;

    public AddEmployeeDialog() {
        super(RuntimeConstants.root, "新增员工", true);
        staffNoField = createInfoTextField();
        passwordField = createInfoTextField();
        nameField = createInfoTextField();
        phoneField = createInfoTextField();
        addressField = createInfoTextField();
        adminBox = createInfoBox();
        addButton = createBootstrapButton("添加");
        cancelButton = createBootstrapButton("取消");

        this.add(createContentPane());
        this.pack();
        this.setLocationRelativeTo(RuntimeConstants.root);
        this.initListener();
    }

    public static boolean newInstance() {
        AddEmployeeDialog addEmployeeDialog = new AddEmployeeDialog();
        addEmployeeDialog.setVisible(true);
        return isRefresh;
    }

    private void initListener() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String staffNo = staffNoField.getText().trim();
                if (staffNo.equals("")) {
                    SwingUtil.showTip(staffNoField, "员工号不能为空");
                    return;
                }
                String password = passwordField.getText().trim();
                if (password.equals("")) {
                    SwingUtil.showTip(passwordField, "密码不能为空");
                    return;
                }
                String name = nameField.getText().trim();
                if (name.equals("")) {
                    SwingUtil.showTip(nameField, "姓名不能为空");
                    return;
                }
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
                boolean result = saveEmployee(staffNo, password, name, phone, address);
                if (result) {
                    isRefresh = true;
                    dispose();
                } else {
                    SwingUtil.showTip(addButton, "保存失败");
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

    private boolean saveEmployee(String... value) {
        SaveEmployeeResponseDto saveEmployeeResponseDto = new SaveEmployeeResponseDto();
        try {
            SaveEmployeeRequestDto saveEmployeeRequestDto = new SaveEmployeeRequestDto();
            saveEmployeeRequestDto.setStaffNo(value[0]);
            saveEmployeeRequestDto.setPassword(value[1]);
            saveEmployeeRequestDto.setName(value[2]);
            saveEmployeeRequestDto.setPhone(value[3]);
            saveEmployeeRequestDto.setAddress(value[4]);
            saveEmployeeRequestDto.setAdmin(adminBox.isSelected());
            saveEmployeeRequestDto.setStoreId(RuntimeConstants.storeId);
            System.out.println(saveEmployeeRequestDto);
            saveEmployeeResponseDto = HttpHandler.post("/save/employee", saveEmployeeRequestDto.toString(), SaveEmployeeResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveEmployeeResponseDto.getCode() == ResultCode.OK;
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        List<String> stringList = Arrays.asList("员工号：", "密码：", "姓名：", "电话：", "地址：", "权限：");
        List<Container> containerList = Arrays.asList(staffNoField, passwordField, nameField, phoneField, addressField, adminBox);
        for (int i = 0; i < stringList.size(); i++) {
            SwingUtil.setupComponent(webPanel, new WebLabel(stringList.get(i)), 0, i, 1, 1);
            SwingUtil.setupComponent(webPanel, containerList.get(i), 0, i, 1, 1);
        }
        SwingUtil.setupComponent(webPanel, createButtonPane(), 0, stringList.size(), 2, 1);
        return webPanel;
    }

    private WebPanel createButtonPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        webPanel.add(addButton);
        webPanel.add(cancelButton);
        return webPanel;
    }

    private WebTextField createInfoTextField() {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setInputPrompt("必填");
        return webTextField;
    }

    private WebCheckBox createInfoBox() {
        return new WebCheckBox("管理员");
    }

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }
}
