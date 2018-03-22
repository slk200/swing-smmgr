package org.tizzer.smmgr.system.view.admin.dialog;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.text.WebTextField;
import com.alee.utils.TimeUtils;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.model.request.SaveStoreRequestDto;
import org.tizzer.smmgr.system.model.response.SaveStoreResponseDto;
import org.tizzer.smmgr.system.resolver.HttpResolver;
import org.tizzer.smmgr.system.util.NPatchUtil;
import org.tizzer.smmgr.system.util.SwingUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * @author tizzer
 * @version 1.0
 */
public class AddStoreDialog extends WebDialog {

    private WebTextField nameField;
    private WebTextField addressField;
    private WebButton addButton;
    private WebButton cancelButton;
    private static boolean isRefresh;

    public static boolean newInstance(){
        AddStoreDialog addStoreDialog = new AddStoreDialog();
        addStoreDialog.setVisible(true);
        return isRefresh;
    }

    public AddStoreDialog() {
        super(RuntimeConstants.root, "新增门店", true);
        addButton = createBootstrapButton("新增");
        cancelButton = createBootstrapButton("取消");
        nameField = createInfoTextField();
        addressField = createInfoTextField();

        this.add(createContentPane());
        this.pack();
        this.setLocationRelativeTo(RuntimeConstants.root);
        this.initListener();
    }

    private void initListener() {
        addButton.addActionListener(new ActionListener() {
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
                boolean result = saveStore(name, address);
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

    private boolean saveStore(String name, String address) {
        SaveStoreResponseDto saveStoreResponseDto = new SaveStoreResponseDto();
        try{
            SaveStoreRequestDto saveStoreRequestDto = new SaveStoreRequestDto();
            saveStoreRequestDto.setName(name);
            saveStoreRequestDto.setAddress(address);
            saveStoreResponseDto = HttpResolver.post("/save/store", saveStoreRequestDto.toString(), SaveStoreResponseDto.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return saveStoreResponseDto.getCode() == ResultCode.OK;
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        SwingUtil.setupComponent(webPanel, new WebLabel("门店名："), 0, 0, 1, 1);
        SwingUtil.setupComponent(webPanel, nameField, 1, 0, 1, 1);
        SwingUtil.setupComponent(webPanel, new WebLabel("地址："), 0, 1, 1, 1);
        SwingUtil.setupComponent(webPanel, addressField, 1, 1, 1, 1);
        SwingUtil.setupComponent(webPanel, createButtonPane(), 0, 2, 2, 1);
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
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }

}
