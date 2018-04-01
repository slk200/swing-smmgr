package org.tizzer.smmgr.system.view.dialog;

import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.spinner.WebSpinner;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.response.QueryPayTypeResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckoutDialog extends WebDialog {

    private static Object payType;
    private WebLabel costLabel;
    private WebComboBox typeComboBox;
    private WebSpinner paySpinner;
    private WebTextField changeField;
    private WebButton confirmButton;
    private WebButton cancelButton;
    private double cost;

    public CheckoutDialog() {
        super(RuntimeConstants.root, "付款", true);
        costLabel = createCostLabel();
        typeComboBox = createPayTypeComboBox();
        paySpinner = createPaySpinner();
        changeField = createUneditableField();
        confirmButton = createBootstrapButton("确定");
        cancelButton = createBootstrapButton("取消");

        this.prepareData();
        this.setContentPane(createContentPane());
        this.pack();
        this.setLocationRelativeTo(RuntimeConstants.root);
        this.initListener();
    }

    public static Object newInstance(double cost) {
        CheckoutDialog checkoutDialog = new CheckoutDialog();
        checkoutDialog.cost = cost;
        checkoutDialog.costLabel.setText(cost + "");
        checkoutDialog.paySpinner.setValue(cost);
        checkoutDialog.setVisible(true);
        return payType;
    }

    private void initListener() {
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double payment = (double) paySpinner.getValue();
                if (payment < cost) {
                    SwingUtil.showTip(confirmButton, "请核对收款");
                    return;
                }
                payType = typeComboBox.getSelectedItem();
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                payType = null;
                dispose();
            }
        });
    }

    private void prepareData() {
        try {
            QueryPayTypeResponseDto queryPayTypeResponseDto = HttpHandler.get("/query/pay/type", QueryPayTypeResponseDto.class);
            Object[] payTypes = queryPayTypeResponseDto.getData();
            typeComboBox.setModel(new DefaultComboBoxModel(payTypes));
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        SwingUtil.setupComponent(webPanel, createTitleLabel("总额："), 0, 0, 1, 1);
        SwingUtil.setupComponent(webPanel, costLabel, 1, 0, 1, 1);
        SwingUtil.setupComponent(webPanel, typeComboBox, 0, 1, 1, 1);
        SwingUtil.setupComponent(webPanel, paySpinner, 1, 1, 1, 1);
        SwingUtil.setupComponent(webPanel, createTitleLabel("找零："), 0, 2, 1, 1);
        SwingUtil.setupComponent(webPanel, changeField, 1, 2, 1, 1);
        SwingUtil.setupComponent(webPanel, createButtonPane(), 0, 3, 2, 1);
        return webPanel;
    }

    private WebPanel createButtonPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        webPanel.add(confirmButton);
        webPanel.add(cancelButton);
        return webPanel;
    }

    private WebLabel createTitleLabel(String text) {
        return new WebLabel(text, SwingConstants.RIGHT);
    }

    private WebLabel createCostLabel() {
        WebLabel webLabel = new WebLabel();
        webLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        return webLabel;
    }

    private WebComboBox createPayTypeComboBox() {
        return new WebComboBox();
    }

    private WebSpinner createPaySpinner() {
        return new WebSpinner(new SpinnerNumberModel(0.0, 0.0, Integer.MAX_VALUE, 0.01));
    }

    private WebTextField createUneditableField() {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        webTextField.setEditable(false);
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
