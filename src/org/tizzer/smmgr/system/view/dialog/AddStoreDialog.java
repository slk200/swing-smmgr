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
import org.tizzer.smmgr.system.model.request.SaveStoreRequestDto;
import org.tizzer.smmgr.system.model.response.SaveStoreResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author tizzer
 * @version 1.0
 */
public class AddStoreDialog extends WebDialog {

    //是否刷新标志
    private static boolean isRefresh;
    private WebTextField nameField;
    private WebTextField addressField;
    private WebButton addButton;
    private WebButton cancelButton;

    private AddStoreDialog() {
        super(RuntimeConstants.root, "新增门店", true);
        addButton = createBootstrapButton("添加");
        cancelButton = createBootstrapButton("取消");
        nameField = createInfoTextField();
        addressField = createInfoTextField();

        this.add(createContentPane());
        this.pack();
        this.setLocationRelativeTo(RuntimeConstants.root);
        this.initListener();
    }

    public static boolean newInstance() {
        AddStoreDialog addStoreDialog = new AddStoreDialog();
        addStoreDialog.setVisible(true);
        return isRefresh;
    }

    private void initListener() {
        addButton.addActionListener(e -> {
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
            SaveStoreResponseDto saveStoreResponseDto = saveStore(name, address);
            if (saveStoreResponseDto.getCode() != ResultCode.OK) {
                SwingUtil.showTip(addButton, saveStoreResponseDto.getMessage());
            } else {
                isRefresh = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    /**
     * 保存分店
     *
     * @param name
     * @param address
     * @return
     */
    private SaveStoreResponseDto saveStore(String name, String address) {
        SaveStoreResponseDto saveStoreResponseDto = new SaveStoreResponseDto();
        try {
            SaveStoreRequestDto saveStoreRequestDto = new SaveStoreRequestDto();
            saveStoreRequestDto.setName(name);
            saveStoreRequestDto.setAddress(address);
            saveStoreResponseDto = HttpHandler.post("/save/store", saveStoreRequestDto.toString(), SaveStoreResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveStoreResponseDto;
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        List<String> stringList = Arrays.asList("门店名：", "地址：");
        List<Container> containerList = Arrays.asList(nameField, addressField);
        for (int i = 0; i < stringList.size(); i++) {
            SwingUtil.setupComponent(webPanel, new WebLabel(stringList.get(i)), 0, i, 1, 1);
            SwingUtil.setupComponent(webPanel, containerList.get(i), 1, i, 1, 1);
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

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }

}
