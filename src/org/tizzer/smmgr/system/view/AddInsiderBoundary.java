package org.tizzer.smmgr.system.view;

import com.alee.extended.date.WebDateField;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.component.listener.NavigationListener;
import org.tizzer.smmgr.system.constant.SystemConstants;
import org.tizzer.smmgr.system.util.NPatchUtil;
import org.tizzer.smmgr.system.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tizzer
 * @version 1.0
 */
public class AddInsiderBoundary extends WebPanel implements ActionListener {

    private WebTextField insiderNoField;
    private WebPasswordField passwordField;
    private WebPasswordField rePasswordField;
    private WebComboBox typeComboBox;
    private WebTextField nameField;
    private WebTextField phoneField;
    private WebDateField deadLineField;
    private WebDateField birthField;
    private WebTextField addressField;
    private WebTextArea remarkArea;
    private WebButton backToSaleButton;
    private WebButton saveInsiderButton;

    private NavigationListener navigationListener;

    public AddInsiderBoundary(NavigationListener navigationListener) {
        insiderNoField = createInformationField();
        passwordField = createPasswordField();
        rePasswordField = createPasswordField();
        typeComboBox = new WebComboBox();
        nameField = createInformationField();
        phoneField = createInformationField();
        deadLineField = createDateChooser();
        birthField = createDateChooser();
        addressField = createInformationField();
        remarkArea = createRemarkArea();
        backToSaleButton = createBootstrapButton("返回收银");
        saveInsiderButton = createBootstrapButton("保存会员");

        backToSaleButton.addActionListener(this);
        saveInsiderButton.addActionListener(this);

        this.setMargin(50);
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.WHITE);
        this.createInsiderPanel();
        this.navigationListener = navigationListener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(backToSaleButton)) {
            backToSale();
            return;
        }

        String message = "<html><h3>会员保存成功！</h3><p>是否离开此界面？</p></html>";
        int option = JOptionPane.showConfirmDialog(AddInsiderBoundary.this, message, "下一步操作", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            backToSale();
        } else {
            reset();
        }
    }

    private WebTextField createInformationField() {
        return new WebTextField(15);
    }

    private WebPasswordField createPasswordField() {
        return new WebPasswordField(15);
    }

    private WebTextArea createRemarkArea() {
        return new WebTextArea(5, 15);
    }

    private WebDateField createDateChooser() {
        WebDateField webDateField = new WebDateField();
        webDateField.setEditable(false);
        webDateField.setDateFormat(SystemConstants._DEFAULT_DATE_FORM);
        return webDateField;
    }

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setBoldFont(true);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setCursor(Cursor.getDefaultCursor());
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }

    private void createInsiderPanel() {
        SwingUtil.setupComponent(this, new WebLabel("会员卡号：", SwingConstants.CENTER), 0, 0, 1, 1);
        SwingUtil.setupComponent(this, insiderNoField, 1, 0, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("会员密码：", SwingConstants.CENTER), 2, 0, 1, 1);
        SwingUtil.setupComponent(this, passwordField, 3, 0, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("确认密码：", SwingConstants.CENTER), 0, 1, 1, 1);
        SwingUtil.setupComponent(this, rePasswordField, 1, 1, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("会员分类：", SwingConstants.CENTER), 2, 1, 1, 1);
        SwingUtil.setupComponent(this, typeComboBox, 3, 1, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("会员姓名：", SwingConstants.CENTER), 0, 2, 1, 1);
        SwingUtil.setupComponent(this, nameField, 1, 2, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("会员电话：", SwingConstants.CENTER), 2, 2, 1, 1);
        SwingUtil.setupComponent(this, phoneField, 3, 2, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("会员生日：", SwingConstants.CENTER), 0, 3, 1, 1);
        SwingUtil.setupComponent(this, birthField, 1, 3, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("过期时间：", SwingConstants.CENTER), 2, 3, 1, 1);
        SwingUtil.setupComponent(this, deadLineField, 3, 3, 1, 1);
        SwingUtil.setupComponent(this, new WebLabel("联系地址：", SwingConstants.CENTER), 0, 4, 1, 1);
        SwingUtil.setupComponent(this, addressField, 1, 4, 3, 1);
        SwingUtil.setupComponent(this, new WebLabel("会员备注：", SwingConstants.CENTER), 0, 5, 1, 1);
        SwingUtil.setupComponent(this, new WebScrollPane(remarkArea), 1, 5, 3, 2);
        SwingUtil.setupComponent(this, backToSaleButton, 1, 7, 1, 1);
        SwingUtil.setupComponent(this, saveInsiderButton, 3, 7, 1, 1);
    }

    /**
     * 返回收银界面
     */
    private void backToSale() {
        Container parent = getParent();
        parent.removeAll();
        parent.add(new TransactionBoundary());
        parent.validate();
        parent.repaint();
        navigationListener.performChange(0);
    }

    /**
     * 重置界面
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
}
