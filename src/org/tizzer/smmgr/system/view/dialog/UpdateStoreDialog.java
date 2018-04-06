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
import org.tizzer.smmgr.system.model.request.UpdateStoreRequestDto;
import org.tizzer.smmgr.system.model.response.UpdateStoreResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author tizzer
 * @version 1.0
 */
public class UpdateStoreDialog extends WebDialog {

    //是否刷新标志
    private static boolean isRefresh;
    private WebTextField idField;
    private WebTextField nameField;
    private WebTextField addressField;
    private WebTextField dateField;
    private WebButton updateButton;
    private WebButton cancelButton;
    //传入参数缓存
    private Object[] dataCache;

    public UpdateStoreDialog() {
        super(RuntimeConstants.root, "编辑门店信息", true);
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

    public static boolean newInstance(Object[] editData) {
        UpdateStoreDialog updateStoreDialog = new UpdateStoreDialog();
        updateStoreDialog.setDataCache(editData);
        updateStoreDialog.setVisible(true);
        return isRefresh;
    }

    private void initListener() {
        updateButton.addActionListener(e -> {
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
            UpdateStoreResponseDto updateStoreResponseDto = updateStore(name, address);
            if (updateStoreResponseDto.getCode() != ResultCode.OK) {
                SwingUtil.showTip(updateButton, updateStoreResponseDto.getMessage());
            } else {
                isRefresh = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    /**
     * 初始化数据
     *
     * @param dataCache
     */
    private void setDataCache(Object[] dataCache) {
        this.dataCache = dataCache;
        idField.setText(dataCache[0].toString());
        nameField.setText(dataCache[1].toString());
        addressField.setText(dataCache[2].toString());
        dateField.setText(dataCache[3].toString());
    }

    /**
     * 修改分店资料
     *
     * @param name
     * @param address
     * @return
     */
    private UpdateStoreResponseDto updateStore(String name, String address) {
        UpdateStoreResponseDto updateStoreResponseDto = new UpdateStoreResponseDto();
        try {
            UpdateStoreRequestDto updateStoreRequestDto = new UpdateStoreRequestDto();
            updateStoreRequestDto.setId(Long.valueOf(String.valueOf(dataCache[0])));
            updateStoreRequestDto.setName(name);
            updateStoreRequestDto.setAddress(address);
            updateStoreResponseDto = HttpHandler.post("/update/store", updateStoreRequestDto.toString(), UpdateStoreResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateStoreResponseDto;
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        List<String> stringList = Arrays.asList("序号：", "门店名：", "地址：", "时间：");
        List<Container> containerList = Arrays.asList(idField, nameField, addressField, dateField);
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
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }
}
