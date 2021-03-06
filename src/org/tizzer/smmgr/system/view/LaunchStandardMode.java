package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.utils.SwingUtils;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.utils.D9Util;
import org.tizzer.smmgr.system.view.listener.NavigationListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author tizzer
 * @version 1.0
 */
public class LaunchStandardMode extends WebPanel implements NavigationListener {

    private WebToggleButton homeButton;
    private WebToggleButton insiderButton;
    private WebToggleButton recordButton;
    private WebToggleButton refundButton;
    private WebToggleButton lossButton;
    private WebToggleButton settingButton;
    private WebToggleButton helpButton;
    private WebPanel handlePanel;

    LaunchStandardMode() {
        homeButton = createNavigationButton("收银台面", IconManager.HOME);
        homeButton.setSelected(true);
        insiderButton = createNavigationButton("新增会员", IconManager.INSIDER);
        refundButton = createNavigationButton("商品退货", IconManager.REFUNDGOODS);
        lossButton = createNavigationButton("商品报损", IconManager.GOODSLOSS);
        recordButton = createNavigationButton("销售单据", IconManager.TRADERECORD);
        settingButton = createNavigationButton("设置", IconManager.SETUP);
        helpButton = createNavigationButton("帮助", IconManager.HELP);
        handlePanel = createHandlePanel();

        this.add(createNavigationPane(), "West");
        this.add(handlePanel, "Center");
        this.initListener();
        this.setClosingOperation();
    }

    private void initListener() {
        homeButton.addActionListener(e -> changeView(new StandardCollectionBoundary(), "Center"));
        insiderButton.addActionListener(e -> changeView(new StandardInsiderBoundary(LaunchStandardMode.this), "North"));
        refundButton.addActionListener(e -> changeView(new StandardRefundBoundary(), "Center"));
        lossButton.addActionListener(e -> changeView(new StandardLossBoundary(), "Center"));
        recordButton.addActionListener(e -> changeView(new StandardTradeBoundary(), "Center"));
        settingButton.addActionListener(e -> {
            //TODO
        });

        helpButton.addActionListener(e -> {
            //TODO
        });
    }

    /**
     * 切换画面
     *
     * @param webPanel
     * @param constrains
     */
    private void changeView(WebPanel webPanel, String constrains) {
        handlePanel.removeAll();
        handlePanel.add(webPanel, constrains);
        handlePanel.validate();
        handlePanel.repaint();
    }

    /**
     * 设置关闭前事件
     */
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

    @Override
    public void performChange(int index) {
        WebPanel navPanel = (WebPanel) getComponent(0);
        WebToggleButton navButton = (WebToggleButton) navPanel.getComponent(index);
        navButton.setSelected(true);
    }

    private WebPanel createNavigationPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setBackground(ColorManager._31_31_31);
        webPanel.setLayout(new VerticalFlowLayout());
        webPanel.add(homeButton, insiderButton, refundButton, lossButton, recordButton, settingButton, helpButton);
        SwingUtils.groupButtons(webPanel);
        return webPanel;
    }

    private WebToggleButton createNavigationButton(String text, ImageIcon icon) {
        WebToggleButton webToggleButton = new WebToggleButton(text, icon);
        webToggleButton.setIconTextGap(10);
        webToggleButton.setMoveIconOnPress(false);
        webToggleButton.setForeground(Color.WHITE);
        webToggleButton.setSelectedForeground(Color.WHITE);
        webToggleButton.setHorizontalAlignment(SwingConstants.LEFT);
        webToggleButton.setMargin(0, 10, 0, 20);
        webToggleButton.setPainter(D9Util.getNinePatchPainter("navigation.xml"));
        return webToggleButton;
    }

    private WebPanel createHandlePanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setBackground(ColorManager._241_246_253);
        webPanel.add(new StandardCollectionBoundary());
        return webPanel;
    }

}
