package org.tizzer.smmgr.system.view.admin;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.panel.WebPanel;
import com.alee.utils.SwingUtils;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.util.NPatchUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        storeButton = createNavigationButton("门店管理", IconManager._ICON_STORE);
        employeeButton = createNavigationButton("员工管理", IconManager._ICON_EMPLOYEE);
        insiderButton = createNavigationButton("会员管理", IconManager._ICON_INSIDER);
        settingButton = createNavigationButton("系统设置", IconManager._ICON_SETTING);
        handlePanel = new WebPanel();
        handlePanel.setBackground(Color.ORANGE);

        this.add(createNavigationPanel(), BorderLayout.WEST);
        this.add(handlePanel, BorderLayout.CENTER);
        this.initListener();
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

            }
        });
        settingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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