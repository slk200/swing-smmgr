package org.tizzer.smmgr.system.view.dialog;

import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.spinner.WebSpinner;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.response.QueryPayTypeResponseDto;
import org.tizzer.smmgr.system.utils.D9Util;
import org.tizzer.smmgr.system.utils.LafUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * @author tizzer
 * @version 1.0
 */
public class CheckoutDialog extends WebDialog {

    //付款类型
    private static Object payType;
    private WebLabel costLabel;
    private WebComboBox typeComboBox;
    private WebSpinner paySpinner;
    private WebTextField changeField;
    private WebButton confirmButton;
    private WebButton cancelButton;
    //消费总额
    private double cost;

    private CheckoutDialog() {
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
        checkoutDialog.costLabel.setText(String.valueOf(cost));
        checkoutDialog.paySpinner.setValue(cost);
        checkoutDialog.setVisible(true);
        return payType;
    }

    private void initListener() {
        confirmButton.addActionListener(e -> {
            double payment = (double) paySpinner.getValue();
            if (payment < cost) {
                LafUtil.showTip(confirmButton, "请核对收款");
                return;
            }
            payType = typeComboBox.getSelectedItem();
            dispose();
        });

        paySpinner.addChangeListener(e -> {
            double pay = (double) paySpinner.getValue();
            double gap = (double) Math.round((pay - cost) * 100) / 100;
            changeField.setText(String.valueOf(gap));
        });

        typeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (!e.getItem().equals("现金")) {
                    paySpinner.setEnabled(false);
                    paySpinner.setValue(cost);
                } else {
                    paySpinner.setEnabled(true);
                }
            }
        });

        cancelButton.addActionListener(e -> {
            payType = null;
            dispose();
        });
    }

    /**
     * 查询付款方式
     *
     * @return
     */
    private QueryPayTypeResponseDto queryPayType() {
        QueryPayTypeResponseDto queryPayTypeResponseDto = new QueryPayTypeResponseDto();
        try {
            queryPayTypeResponseDto = HttpHandler.get("/query/pay/type", QueryPayTypeResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryPayTypeResponseDto;
    }

    /**
     * 准备数据
     */
    private void prepareData() {
        QueryPayTypeResponseDto queryPayTypeResponseDto = queryPayType();
        typeComboBox.setModel(new DefaultComboBoxModel(queryPayTypeResponseDto.getData()));
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        LafUtil.setupComponent(webPanel, createTitleLabel("总额："), 0, 0, 1, 1);
        LafUtil.setupComponent(webPanel, costLabel, 1, 0, 1, 1);
        LafUtil.setupComponent(webPanel, typeComboBox, 0, 1, 1, 1);
        LafUtil.setupComponent(webPanel, paySpinner, 1, 1, 1, 1);
        LafUtil.setupComponent(webPanel, createTitleLabel("找零："), 0, 2, 1, 1);
        LafUtil.setupComponent(webPanel, changeField, 1, 2, 1, 1);
        LafUtil.setupComponent(webPanel, createButtonPane(), 0, 3, 2, 1);
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
        webButton.setPainter(D9Util.getNinePatchPainter("default.xml"));
        return webButton;
    }

}
