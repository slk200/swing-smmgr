package org.tizzer.smmgr.system.view.admin;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.utils.SwingUtils;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.util.NPatchUtil;
import org.tizzer.smmgr.system.view.LoginBoundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ManageModeBoundary extends WebPanel {

    private WebToggleButton factButton;
    private WebToggleButton storeButton;
    private WebToggleButton employeeButton;
    private WebToggleButton insiderButton;
    private WebToggleButton settingButton;
    private WebPanel handlePanel;

    public ManageModeBoundary() {
        factButton = createNavigationButton("概况", IconManager._ICON_FACT);
        factButton.setSelected(true);
        storeButton = createNavigationButton("门店资料", IconManager._ICON_STORE);
        employeeButton = createNavigationButton("员工资料", IconManager._ICON_EMPLOYEE);
        insiderButton = createNavigationButton("会员资料", IconManager._ICON_INSIDER);
        settingButton = createNavigationButton("系统设置", IconManager._ICON_SETTING);
        handlePanel = new WebPanel();

        this.add(createNavigationPanel(), BorderLayout.WEST);
        this.add(handlePanel, BorderLayout.CENTER);
        this.initListener();
        this.setClosingOperation();
    }

    private void initListener() {
        factButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        storeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePanel.removeAll();
                handlePanel.add(new ManageStoreBoundary());
                handlePanel.validate();
                handlePanel.repaint();
            }
        });
        employeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePanel.removeAll();
                handlePanel.add(new ManageEmployeeBoundary());
                handlePanel.validate();
                handlePanel.repaint();
            }
        });
        insiderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePanel.removeAll();
                handlePanel.add(new ManageInsiderBoundary());
                handlePanel.validate();
                handlePanel.repaint();
            }
        });
        settingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
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
                    root.getContentPane().add(new LoginBoundary());
                    root.getContentPane().validate();
                    root.getContentPane().repaint();
                    changeClosingOperation();
                }
            }
        });
    }

    private void changeClosingOperation() {
        WebFrame root = RuntimeConstants.root;
        root.removeWindowListener(root.getWindowListeners()[0]);
        root.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private WebPanel createNavigationPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setBackground(ColorManager._31_31_31);
        webPanel.setLayout(new VerticalFlowLayout());
        webPanel.add(factButton, storeButton, employeeButton, insiderButton, settingButton);
        SwingUtils.groupButtons(webPanel);
        return webPanel;
    }

    private WebToggleButton createNavigationButton(String text, ImageIcon icon) {
        WebToggleButton webToggleButton = new WebToggleButton(text, icon);
        webToggleButton.setIconTextGap(10);
        webToggleButton.setForeground(Color.WHITE);
        webToggleButton.setSelectedForeground(Color.WHITE);
        webToggleButton.setHorizontalAlignment(SwingConstants.LEFT);
        webToggleButton.setMargin(0, 10, 0, 30);
        webToggleButton.setPainter(NPatchUtil.getNinePatchPainter("navigation.xml"));
        return webToggleButton;
    }
}