package org.tizzer.smmgr.system.view.admin.dialog;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.model.request.UpdateStoreRequestDto;
import org.tizzer.smmgr.system.model.response.UpdateStoreResponseDto;
import org.tizzer.smmgr.system.resolver.HttpResolver;
import org.tizzer.smmgr.system.util.NPatchUtil;
import org.tizzer.smmgr.system.util.SwingUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tizzer
 * @version 1.0
 */
public class UpdateStoreDialog extends WebDialog {

    private WebTextField idField;
    private WebTextField nameField;
    private WebTextField addressField;
    private WebTextField dateField;
    private WebButton updateButton;
    private WebButton cancelButton;

    private Object[] dataCache;
    private static boolean isRefresh;

    public static boolean newInstance(Object[] editData) {
        UpdateStoreDialog updateStoreDialog = new UpdateStoreDialog();
        updateStoreDialog.setDataCache(editData);
        updateStoreDialog.setVisible(true);
        return isRefresh;
    }

    public UpdateStoreDialog() {
        super(RuntimeConstants.root, "修改门店信息", true);
        idField = createDisabledTextField();
        nameField = createInfoTextField();
        addressField = createInfoTextField();
        dateField = createDisabledTextField();
        updateButton = createBootstrapButton("修改");
        cancelButton = createBootstrapButton("取消");

        this.add(createContentPane());
        this.pack();
        this.setLocationRelativeTo(RuntimeConstants.root);
        this.initListener();
    }

    private void initListener() {
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                if (name.equals("")) {
                    SwingUtil.showTip(nameField, "门店名不能为空");
                    return;
                }
                String address = addressField.getText().trim();
                if (address.equals("")) {
                    SwingUtil.showTip(addressField, "地址不能为空");
                    return;
                }
                if (name.equals(dataCache[1]) && address.equals(dataCache[2])) {
                    dispose();
                    return;
                }
                boolean result = updateStore(name, address);
                if (result) {
                    isRefresh = true;
                    dispose();
                } else {
                    SwingUtil.showTip(updateButton, "修改失败！");
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

    public void setDataCache(Object[] dataCache) {
        this.dataCache = dataCache;
        idField.setText(dataCache[0].toString());
        nameField.setText(dataCache[1].toString());
        addressField.setText(dataCache[2].toString());
        dateField.setText(dataCache[3].toString());
    }

    private boolean updateStore(String name, String address) {
        UpdateStoreResponseDto updateStoreResponseDto = new UpdateStoreResponseDto();
        try {
            UpdateStoreRequestDto updateStoreRequestDto = new UpdateStoreRequestDto();
            updateStoreRequestDto.setId((Long) dataCache[0]);
            updateStoreRequestDto.setName(name);
            updateStoreRequestDto.setAddress(address);
            updateStoreResponseDto = HttpResolver.post("/update/store", updateStoreRequestDto.toString(), UpdateStoreResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateStoreResponseDto.getCode() == ResultCode.OK;
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        SwingUtil.setupComponent(webPanel, new WebLabel("序号："), 0, 0, 1, 1);
        SwingUtil.setupComponent(webPanel, idField, 1, 0, 1, 1);
        SwingUtil.setupComponent(webPanel, new WebLabel("门店名："), 0, 1, 1, 1);
        SwingUtil.setupComponent(webPanel, nameField, 1, 1, 1, 1);
        SwingUtil.setupComponent(webPanel, new WebLabel("地址："), 0, 2, 1, 1);
        SwingUtil.setupComponent(webPanel, addressField, 1, 2, 1, 1);
        SwingUtil.setupComponent(webPanel, new WebLabel("时间："), 0, 3, 1, 1);
        SwingUtil.setupComponent(webPanel, dateField, 1, 3, 1, 1);
        SwingUtil.setupComponent(webPanel, createButtonPane(), 0, 4, 2, 1);
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

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }
}
