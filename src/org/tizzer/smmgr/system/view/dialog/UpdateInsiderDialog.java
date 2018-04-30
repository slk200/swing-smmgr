package org.tizzer.smmgr.system.view.dialog;

import com.alee.extended.date.WebDateField;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.UpdateInsiderRequestDto;
import org.tizzer.smmgr.system.model.response.QueryAllInsiderTypeResponseDto;
import org.tizzer.smmgr.system.model.response.UpdateInsiderResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author tizzer
 * @version 1.0
 */
public class UpdateInsiderDialog extends WebDialog {

    //记录新折扣
    private static int newDiscount;
    private WebTextField cardNoField;
    private WebTextField nameField;
    private WebTextField phoneField;
    private WebTextField addressField;
    private WebTextField noteField;
    private WebComboBox typeComboBox;
    private WebDateField birthField;
    private WebButton updateButton;
    private WebButton cancelButton;
    //传入参数缓存
    private Object[] dataCache;
    //会员类型缓存
    private Integer[] idCache;
    private Integer[] discountCache;

    private UpdateInsiderDialog() {
        super(RuntimeConstants.root, "编辑会员资料", true);
        cardNoField = createDisableField();
        nameField = createDisableField();
        phoneField = createDisableField();
        addressField = createDisableField();
        noteField = createDisableField();
        typeComboBox = new WebComboBox();
        birthField = createBirthField();
        updateButton = createBootstrapButton("修改");
        cancelButton = createBootstrapButton("取消");

        this.prepareData();
        this.add(createContentPane());
        this.pack();
        this.setLocationRelativeTo(RuntimeConstants.root);
        this.initListener();
    }

    public static int newInstance(Object[] dataCache) {
        UpdateInsiderDialog updateInsiderDialog = new UpdateInsiderDialog();
        updateInsiderDialog.setDataCache(dataCache);
        updateInsiderDialog.setVisible(true);
        return newDiscount;
    }

    private void initListener() {
        updateButton.addActionListener(e -> {
            if (birthField.isEnabled()) {
                if (birthField.getDate() == null) {
                    if (Objects.equals(typeComboBox.getSelectedItem(), dataCache[4])) {
                        SwingUtil.showTip(updateButton, "并没有修改信息");
                        return;
                    }
                }
                updateInsider();
            } else {
                if (Objects.equals(typeComboBox.getSelectedItem(), dataCache[4])) {
                    SwingUtil.showTip(updateButton, "并没有修改信息");
                    return;
                }
                UpdateInsiderResponseDto updateInsiderResponseDto = updateInsider();
                if (updateInsiderResponseDto.getCode() != ResultCode.OK) {
                    SwingUtil.showTip(updateButton, updateInsiderResponseDto.getMessage());
                } else {
                    newDiscount = discountCache[typeComboBox.getSelectedIndex()];
                    dispose();
                }
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    /**
     * 修改会员信息
     *
     * @return
     */
    private UpdateInsiderResponseDto updateInsider() {
        UpdateInsiderResponseDto updateInsiderResponseDto = new UpdateInsiderResponseDto();
        try {
            UpdateInsiderRequestDto updateInsiderRequestDto = new UpdateInsiderRequestDto();
            updateInsiderRequestDto.setCardNo(dataCache[0].toString());
            updateInsiderRequestDto.setId(idCache[typeComboBox.getSelectedIndex()]);
            updateInsiderRequestDto.setBirth(birthField.getText());
            updateInsiderResponseDto = HttpHandler.post("/update/insider", updateInsiderRequestDto.toString(), UpdateInsiderResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return updateInsiderResponseDto;
    }

    /**
     * 初始化数据
     *
     * @param dataCache
     */
    private void setDataCache(Object[] dataCache) {
        this.dataCache = dataCache;
        newDiscount = (int) dataCache[6];
        cardNoField.setText(dataCache[0].toString());
        nameField.setText(dataCache[1].toString());
        phoneField.setText(dataCache[2].toString());
        addressField.setText(dataCache[3].toString());
        typeComboBox.setSelectedItem(dataCache[4]);
        noteField.setText(dataCache[5].toString());
        birthField.setText(dataCache[7].toString());
        if (!birthField.getText().equals("")) {
            birthField.setEnabled(false);
        }
    }

    /**
     * 准备数据
     */
    private void prepareData() {
        try {
            QueryAllInsiderTypeResponseDto queryAllInsiderTypeResponseDto = HttpHandler.get("/query/insider/type", QueryAllInsiderTypeResponseDto.class);
            this.idCache = queryAllInsiderTypeResponseDto.getId();
            this.discountCache = queryAllInsiderTypeResponseDto.getDiscount();
            typeComboBox.setModel(new DefaultComboBoxModel(queryAllInsiderTypeResponseDto.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        List<String> stringList = Arrays.asList("会员卡号：", "会员姓名：", "会员电话：", "会员地址：", "备注：", "会员类型：", "会员生日：");
        List<Container> containerList = Arrays.asList(cardNoField, nameField, phoneField, addressField, noteField, typeComboBox, birthField);
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

    private WebTextField createDisableField() {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setEnabled(false);
        return webTextField;
    }

    private WebDateField createBirthField() {
        WebDateField webDateField = new WebDateField();
        webDateField.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        webDateField.setEditable(false);
        return webDateField;
    }

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }
}
