package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.utils.SwingUtils;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.utils.NPatchUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LaunchManageMode extends WebPanel {

    private WebToggleButton factButton;
    private WebToggleButton storeButton;
    private WebToggleButton employeeButton;
    private WebToggleButton insiderButton;
    private WebToggleButton goodsButton;
    private WebToggleButton recordButton;
    private WebToggleButton purchaseButton;
    private WebToggleButton bookButton;
    private WebToggleButton lossButton;
    private WebToggleButton settingButton;
    private WebToggleButton helpButton;
    private WebPanel handlePanel;

    public LaunchManageMode() {
        factButton = createNavigationButton("营业概况", IconManager.FACT);
        factButton.setSelected(true);
        storeButton = createNavigationButton("门店管理", IconManager.STORE);
        employeeButton = createNavigationButton("员工管理", IconManager.EMPLOYEE);
        insiderButton = createNavigationButton("会员管理", IconManager.INSIDER);
        goodsButton = createNavigationButton("商品管理", IconManager.GOODSEDIT);
        purchaseButton = createNavigationButton("进货管理", IconManager.PURCHASEGOODS);
        bookButton = createNavigationButton("订货管理", IconManager.BOOKGOODS);
        recordButton = createNavigationButton("销售记录", IconManager.TRADERECORD);
        lossButton = createNavigationButton("报损记录", IconManager.GOODSLOSS);
        settingButton = createNavigationButton("系统设置", IconManager.SETUP);
        helpButton = createNavigationButton("帮助", IconManager.HELP);
        handlePanel = createHandlePanel();

        this.add(createNavigationPane(), "West");
        this.add(handlePanel, "Center");
        this.initListener();
        this.setClosingOperation();
    }

    private void initListener() {
        factButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new ManageFactBoundary());
            }
        });

        storeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new ManageStoreBoundary());
            }
        });

        employeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new ManageEmployeeBoundary());
            }
        });

        insiderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new ManageInsiderBoundary());
            }
        });

        goodsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new ManageGoodsBoundary());
            }
        });

        purchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new ManagePurchaseBoundary());
            }
        });

        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new ManageBookBoundary());
            }
        });

        recordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new ManageRecordBoundary());
            }
        });

        lossButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new ManageLossBoundary());
            }
        });

        settingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void changeView(WebPanel webPanel) {
        handlePanel.removeAll();
        handlePanel.add(webPanel);
        handlePanel.validate();
        handlePanel.repaint();
    }

    private void setClosingOperation() {
        WebFrame root = RuntimeConstants.root;
        root.removeWindowListener(root.getWindowListeners()[0]);
        root.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(root, "<html><h3>是否要退出？</h3></html>", "询问", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    root.getContentPane().removeAll();
                    LoginBoundary loginBoundary = new LoginBoundary();
                    root.getContentPane().add(loginBoundary);
                    root.getContentPane().validate();
                    root.getContentPane().repaint();
                    root.removeWindowListener(this);
                    loginBoundary.setClosingOperation(root);
                    loginBoundary.setDefaultButton();
                }
            }
        });
    }

    private WebPanel createNavigationPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setBackground(ColorManager._31_31_31);
        webPanel.setLayout(new VerticalFlowLayout());
        webPanel.add(factButton, storeButton, employeeButton, insiderButton, goodsButton, purchaseButton, bookButton, recordButton, lossButton, settingButton, helpButton);
        SwingUtils.groupButtons(webPanel);
        return webPanel;
    }

    private WebToggleButton createNavigationButton(String text, ImageIcon icon) {
        WebToggleButton webToggleButton = new WebToggleButton(text, icon);
        webToggleButton.setIconTextGap(10);
        webToggleButton.setForeground(Color.WHITE);
        webToggleButton.setSelectedForeground(Color.WHITE);
        webToggleButton.setHorizontalAlignment(SwingConstants.LEFT);
        webToggleButton.setMargin(0, 10, 0, 20);
        webToggleButton.setPainter(NPatchUtil.getNinePatchPainter("navigation.xml"));
        return webToggleButton;
    }

    private WebPanel createHandlePanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setBackground(ColorManager._241_246_253);
        webPanel.add(new ManageFactBoundary());
        return webPanel;
    }
}