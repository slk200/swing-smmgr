package org.tizzer.smmgr.system.view.dialog;

import com.alee.laf.button.WebButton;
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
import org.tizzer.smmgr.system.utils.D9Util;
import org.tizzer.smmgr.system.utils.LafUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author tizzer
 * @version 1.0
 */
public class AddEmployeeDialog extends WebDialog {

    //是否刷新标志
    private static boolean isRefresh;
    private WebTextField staffNoField;
    private WebTextField passwordField;
    private WebTextField nameField;
    private WebTextField phoneField;
    private WebTextField addressField;
    private WebButton addButton;
    private WebButton cancelButton;

    private AddEmployeeDialog() {
        super(RuntimeConstants.root, "新增员工", true);
        staffNoField = createInfoTextField();
        passwordField = createInfoTextField();
        nameField = createInfoTextField();
        phoneField = createInfoTextField();
        addressField = createInfoTextField();
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
        addButton.addActionListener(e -> {
            String staffNo = staffNoField.getText().trim();
            if (staffNo.equals("")) {
                LafUtil.showTip(staffNoField, "员工号不能为空");
                return;
            }
            String password = passwordField.getText().trim();
            if (password.equals("")) {
                LafUtil.showTip(passwordField, "密码不能为空");
                return;
            }
            String name = nameField.getText().trim();
            if (name.equals("")) {
                LafUtil.showTip(nameField, "姓名不能为空");
                return;
            }
            String phone = phoneField.getText().trim();
            if (phone.equals("")) {
                LafUtil.showTip(phoneField, "电话不能为空");
                return;
            }
            String address = addressField.getText().trim();
            if (address.equals("")) {
                LafUtil.showTip(addressField, "地址不能为空");
                return;
            }
            SaveEmployeeResponseDto saveEmployeeResponseDto = saveEmployee(staffNo, password, name, phone, address);
            if (saveEmployeeResponseDto.getCode() != ResultCode.OK) {
                LafUtil.showTip(addButton, saveEmployeeResponseDto.getMessage());
            } else {
                isRefresh = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    /**
     * 保存员工
     *
     * @param value
     * @return
     */
    private SaveEmployeeResponseDto saveEmployee(String... value) {
        SaveEmployeeResponseDto saveEmployeeResponseDto = new SaveEmployeeResponseDto();
        try {
            SaveEmployeeRequestDto saveEmployeeRequestDto = new SaveEmployeeRequestDto();
            saveEmployeeRequestDto.setStaffNo(value[0]);
            saveEmployeeRequestDto.setPassword(value[1]);
            saveEmployeeRequestDto.setName(value[2]);
            saveEmployeeRequestDto.setPhone(value[3]);
            saveEmployeeRequestDto.setAddress(value[4]);
            saveEmployeeRequestDto.setAdmin(false);
            saveEmployeeRequestDto.setStoreId(RuntimeConstants.storeId);
            saveEmployeeResponseDto = HttpHandler.post("/save/employee", saveEmployeeRequestDto.toString(), SaveEmployeeResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveEmployeeResponseDto;
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        List<String> stringList = Arrays.asList("员工号：", "密码：", "姓名：", "电话：", "地址：");
        List<Container> containerList = Arrays.asList(staffNoField, passwordField, nameField, phoneField, addressField);
        for (int i = 0; i < stringList.size(); i++) {
            LafUtil.setupComponent(webPanel, new WebLabel(stringList.get(i)), 0, i, 1, 1);
            LafUtil.setupComponent(webPanel, containerList.get(i), 1, i, 1, 1);
        }
        LafUtil.setupComponent(webPanel, createButtonPane(), 0, stringList.size(), 2, 1);
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

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(D9Util.getNinePatchPainter("default.xml"));
        return webButton;
    }
}
