package org.tizzer.smmgr.system.view;

import com.alee.extended.date.WebDateField;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.component.WebBSButton;
import org.tizzer.smmgr.system.constant.SystemConstants;
import org.tizzer.smmgr.system.template.Initialization;
import org.tizzer.smmgr.system.template.NavigationListener;
import org.tizzer.smmgr.system.util.GridBagUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tizzer
 * @version 1.0
 */
public class AddInsiderBoundary extends Initialization {

    private WebTextField field$vip$number;
    private WebPasswordField field$vip$password;
    private WebPasswordField field$confirm$password;
    private WebComboBox box$vip$category;
    private WebTextField field$vip$name;
    private WebTextField field$vip$phone;
    private WebDateField field$deadline;
    private WebDateField field$vip$birth;
    private WebTextField field$contact$address;
    private WebTextArea area$remark;
    private WebBSButton button$back$sale;
    private WebBSButton button$save$vip;
    private NavigationListener navigationListener;

    public AddInsiderBoundary(NavigationListener navigationListener) {
        super();
        this.navigationListener = navigationListener;
    }

    @Override
    public void initProp() {
        setMargin(50);
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
    }

    @Override
    public void initVal() {
        field$vip$number = new WebTextField(15);
        field$vip$number.setInputPrompt("必填");
        field$vip$password = new WebPasswordField(15);
        field$confirm$password = new WebPasswordField(15);
        box$vip$category = new WebComboBox();
        field$vip$name = new WebTextField(15);
        field$vip$name.setInputPrompt("必填");
        field$vip$phone = new WebTextField(15);
        field$vip$phone.setInputPrompt("必填");
        field$deadline = new WebDateField();
        field$deadline.setEditable(false);
        field$deadline.setDateFormat(SystemConstants._DEFAULT_DATE_FORM);
        field$vip$birth = new WebDateField();
        field$vip$birth.setEditable(false);
        field$vip$birth.setDateFormat(SystemConstants._DEFAULT_DATE_FORM);
        field$contact$address = new WebTextField(15);
        area$remark = new WebTextArea(5, 15);
        button$back$sale = new WebBSButton("返回收银", WebBSButton.BLUE);
        button$save$vip = new WebBSButton("保存会员", WebBSButton.BLUE);
    }

    @Override
    public void initView() {
        GridBagUtil.setupComponent(this, new WebLabel("会员卡号：", SwingConstants.CENTER), 0, 0, 1, 1);
        GridBagUtil.setupComponent(this, field$vip$number, 1, 0, 1, 1);
        GridBagUtil.setupComponent(this, new WebLabel("会员密码：", SwingConstants.CENTER), 2, 0, 1, 1);
        GridBagUtil.setupComponent(this, field$vip$password, 3, 0, 1, 1);
        GridBagUtil.setupComponent(this, new WebLabel("确认密码：", SwingConstants.CENTER), 0, 1, 1, 1);
        GridBagUtil.setupComponent(this, field$confirm$password, 1, 1, 1, 1);
        GridBagUtil.setupComponent(this, new WebLabel("会员分类：", SwingConstants.CENTER), 2, 1, 1, 1);
        GridBagUtil.setupComponent(this, box$vip$category, 3, 1, 1, 1);
        GridBagUtil.setupComponent(this, new WebLabel("会员姓名：", SwingConstants.CENTER), 0, 2, 1, 1);
        GridBagUtil.setupComponent(this, field$vip$name, 1, 2, 1, 1);
        GridBagUtil.setupComponent(this, new WebLabel("会员电话：", SwingConstants.CENTER), 2, 2, 1, 1);
        GridBagUtil.setupComponent(this, field$vip$phone, 3, 2, 1, 1);
        GridBagUtil.setupComponent(this, new WebLabel("会员生日：", SwingConstants.CENTER), 0, 3, 1, 1);
        GridBagUtil.setupComponent(this, field$vip$birth, 1, 3, 1, 1);
        GridBagUtil.setupComponent(this, new WebLabel("过期时间：", SwingConstants.CENTER), 2, 3, 1, 1);
        GridBagUtil.setupComponent(this, field$deadline, 3, 3, 1, 1);
        GridBagUtil.setupComponent(this, new WebLabel("联系地址：", SwingConstants.CENTER), 0, 4, 1, 1);
        GridBagUtil.setupComponent(this, field$contact$address, 1, 4, 3, 1);
        GridBagUtil.setupComponent(this, new WebLabel("会员备注：", SwingConstants.CENTER), 0, 5, 1, 1);
        GridBagUtil.setupComponent(this, new WebScrollPane(area$remark), 1, 5, 3, 2);
        GridBagUtil.setupComponent(this, button$back$sale, 1, 7, 1, 1);
        GridBagUtil.setupComponent(this, button$save$vip, 3, 7, 1, 1);
    }

    @Override
    public void initAction() {
        button$back$sale.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToSale();
            }
        });

        button$save$vip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(AddInsiderBoundary.this, "<html><p>会员保存成功！</p><p>是否离开此界面？</p></html>", "下一步操作", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    backToSale();
                } else {
                    reset();
                }
            }
        });
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
