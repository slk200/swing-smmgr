package org.tizzer.smmgr.system.view;

import com.alee.extended.date.WebDateField;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.SaveInsiderRequestDto;
import org.tizzer.smmgr.system.model.response.QueryAllInsiderTypeResponseDto;
import org.tizzer.smmgr.system.model.response.SaveInsiderResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;
import org.tizzer.smmgr.system.view.listener.NavigationListener;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * @author tizzer
 * @version 1.0
 */
class StandardInsiderBoundary extends WebPanel {

    private WebTextField cardNoField;
    private WebComboBox typeComboBox;
    private WebTextField nameField;
    private WebTextField phoneField;
    private WebDateField birthField;
    private WebTextField addressField;
    private WebTextArea noteArea;
    private WebButton backToSaleButton;
    private WebButton saveInsiderButton;

    //回掉函数
    private NavigationListener navigationListener;
    //会员类型缓存
    private Integer[] idCache;

    StandardInsiderBoundary(NavigationListener navigationListener) {
        cardNoField = createInformationField("必填");
        typeComboBox = new WebComboBox();
        nameField = createInformationField("必填");
        phoneField = createInformationField("必填");
        birthField = createDateChooser();
        addressField = createInformationField(null);
        noteArea = createRemarkArea();
        backToSaleButton = createBootstrapButton("返回收银");
        saveInsiderButton = createBootstrapButton("保存会员");

        this.prepareData();
        this.setOpaque(false);
        this.setMargin(50, 0, 0, 0);
        this.setLayout(new GridBagLayout());
        this.createInsiderPanel();
        this.initListener();
        this.navigationListener = navigationListener;
    }

    private void initListener() {
        backToSaleButton.addActionListener(e -> backToSale());

        saveInsiderButton.addActionListener(e -> {
            String cardNo = cardNoField.getText().trim();
            if (cardNo.equals("")) {
                SwingUtil.showTip(cardNoField, "会员卡号不能为空");
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
            SaveInsiderResponseDto saveInsiderResponseDto = saveInsider(cardNo, name, phone);
            if (saveInsiderResponseDto.getCode() == ResultCode.OK) {
                int option = JOptionPane.showConfirmDialog(RuntimeConstants.root, "<html><h3>会员保存成功！</h3><p>是否离开此界面？</p></html>", "询问", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    backToSale();
                } else {
                    reset();
                }
            } else {
                SwingUtil.showTip(saveInsiderButton, saveInsiderResponseDto.getMessage());
            }
        });
    }

    /**
     * 准备数据
     */
    private void prepareData() {
        try {
            QueryAllInsiderTypeResponseDto queryAllInsiderTypeResponseDto = HttpHandler.get("/query/insider/type", QueryAllInsiderTypeResponseDto.class);
            if (queryAllInsiderTypeResponseDto.getCode() == ResultCode.OK) {
                this.idCache = queryAllInsiderTypeResponseDto.getId();
                typeComboBox.setModel(new DefaultComboBoxModel(queryAllInsiderTypeResponseDto.getName()));
            } else {
                SwingUtil.showNotification("访问出错，" + queryAllInsiderTypeResponseDto.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存会员
     *
     * @param cardNo
     * @param name
     * @param phone
     */
    private SaveInsiderResponseDto saveInsider(String cardNo, String name, String phone) {
        SaveInsiderResponseDto saveInsiderResponseDto = new SaveInsiderResponseDto();
        try {
            SaveInsiderRequestDto saveInsiderRequestDto = new SaveInsiderRequestDto();
            saveInsiderRequestDto.setCardNo(cardNo);
            saveInsiderRequestDto.setName(name);
            saveInsiderRequestDto.setPhone(phone);
            saveInsiderRequestDto.setAddress(addressField.getText().trim());
            saveInsiderRequestDto.setNote(noteArea.getText().trim());
            saveInsiderRequestDto.setType(idCache[typeComboBox.getSelectedIndex()]);
            saveInsiderRequestDto.setBirth(birthField.getText());
            saveInsiderResponseDto = HttpHandler.post("/save/insider", saveInsiderRequestDto.toString(), SaveInsiderResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveInsiderResponseDto;
    }

    /**
     * 返回收银界面
     */
    private void backToSale() {
        Container parent = getParent();
        parent.removeAll();
        parent.add(new StandardCollectionBoundary());
        parent.validate();
        parent.repaint();
        navigationListener.performChange(0);
    }

    /**
     * 重置台面
     */
    private void reset() {
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JTextField) {
                ((JTextField) component).setText(null);
                continue;
            }
            if (component instanceof WebScrollPane) {
                JViewport child = (JViewport) ((WebScrollPane) component).getComponent(0);
                ((WebTextArea) child.getComponent(0)).setText(null);
            }
        }
    }

    private WebTextField createInformationField(String inputPrompt) {
        WebTextField webTextField = new WebTextField(15);
        webTextField.setInputPrompt(inputPrompt);
        return webTextField;
    }

    private WebTextArea createRemarkArea() {
        return new WebTextArea(5, 15);
    }

    private WebDateField createDateChooser() {
        WebDateField webDateField = new WebDateField();
        webDateField.setEditable(false);
        webDateField.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        return webDateField;
    }

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setCursor(Cursor.getDefaultCursor());
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }

    private void createInsiderPanel() {
        SwingUtil.setupComponent(this, new WebLabel("会员卡号：", SwingConstants.CENTER), 0, 0, 1, 1);
        SwingUtil.setupComponent(this, cardNoField, 1, 0, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("会员分类：", SwingConstants.CENTER), 2, 0, 1, 1);
        SwingUtil.setupComponent(this, typeComboBox, 3, 0, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("会员姓名：", SwingConstants.CENTER), 0, 1, 1, 1);
        SwingUtil.setupComponent(this, nameField, 1, 1, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("会员电话：", SwingConstants.CENTER), 2, 1, 1, 1);
        SwingUtil.setupComponent(this, phoneField, 3, 1, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("联系地址：", SwingConstants.CENTER), 0, 2, 1, 1);
        SwingUtil.setupComponent(this, addressField, 1, 2, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("会员生日：", SwingConstants.CENTER), 2, 2, 1, 1);
        SwingUtil.setupComponent(this, birthField, 3, 2, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("会员备注：", SwingConstants.CENTER), 0, 3, 1, 1);
        SwingUtil.setupComponent(this, new WebScrollPane(noteArea), 1, 3, 3, 2);
        SwingUtil.setupComponent(this, backToSaleButton, 1, 5, 1, 1);
        SwingUtil.setupComponent(this, saveInsiderButton, 3, 5, 1, 1);
    }
}
