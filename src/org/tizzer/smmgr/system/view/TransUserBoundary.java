package org.tizzer.smmgr.system.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.utils.TimeUtils;
import org.tizzer.smmgr.system.component.WebInfoCard;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.util.NPatchUtil;
import org.tizzer.smmgr.system.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author tizzer
 * @version 1.0
 */
public class TransUserBoundary extends WebPanel {

    private WebButton backButton;
    private WebPanel employeeCard;
    private WebPanel cashCard;
    private WebPanel salesCard;
    private WebPanel insiderCard;
    private WebPanel billCard;
    private WebButton logoutButton;

    public TransUserBoundary() {
        backButton = createBootstrapButton(null, IconManager._ICON_BACK, "transparent.xml");
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        employeeCard = createInfoCard("收银员：", RuntimeConstants.loginAccount + "（工号：" + RuntimeConstants.loginAccount + "）");
        cashCard = createInfoCard("应有现金：", "30");
        salesCard = createInfoCard("总销售额：", "30");
        insiderCard = createInfoCard("会员充值：", "30");
        billCard = createInfoCard("总单据数：", "30");
        logoutButton = createBootstrapButton("交接班并登出", null, "recred.xml");
        logoutButton.setFontSize(20);

        this.add(createTitlePanel(), BorderLayout.NORTH);
        this.add(createInfoPanel(), BorderLayout.CENTER);
        this.initListener();
    }

    private void initListener() {
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WebFrame root = RuntimeConstants.root;
                root.getGlassPane().setVisible(false);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WebFrame root = RuntimeConstants.root;
                root.getContentPane().removeAll();
                root.getContentPane().add(new LoginBoundary());
                root.getContentPane().validate();
                root.getContentPane().repaint();
                root.getGlassPane().setVisible(false);
                setClosingOperation();
            }
        });
    }

    private void setClosingOperation() {
        WebFrame root = RuntimeConstants.root;
        root.removeWindowListener(root.getWindowListeners()[0]);
        root.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradientPaint = new GradientPaint(getWidth() / 2, 0, Color.WHITE, getWidth() / 2, getHeight(), ColorManager._28_102_220, false);
        g2d.setPaint(gradientPaint);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private WebButton createBootstrapButton(String text, ImageIcon icon, String colorConfig) {
        WebButton webButton = new WebButton(text, icon);
        webButton.setBoldFont(true);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter(colorConfig));
        return webButton;
    }

    private WebInfoCard createInfoCard(String key, String value) {
        WebInfoCard webInfoCard = new WebInfoCard();
        webInfoCard.setKey(key);
        webInfoCard.setValue(value);
        return webInfoCard;
    }

    private WebLabel createTitleLabel() {
        WebLabel webLabel = new WebLabel();
        webLabel.setText("交接班");
        webLabel.setForeground(Color.WHITE);
        webLabel.setFontSize(30);
        webLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return webLabel;
    }

    private WebLabel createTimeLabel() {
        WebLabel webLabel = new WebLabel();
        webLabel.setMargin(5);
        webLabel.setForeground(ColorManager._187_141_89);
        webLabel.setBoldFont(true);
        webLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        webLabel.setText(RuntimeConstants.loginAt + " / " + TimeUtils.formatCurrentDate("yyyy-MM-dd HH:mm:ss"));
        return webLabel;
    }

    private WebPanel createTitlePanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setBackground(ColorManager._28_102_220);
        webPanel.add(backButton, BorderLayout.WEST);
        webPanel.add(createTitleLabel(), BorderLayout.CENTER);
        return webPanel;
    }

    private WebPanel createInfoPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(50, 0, 0, 0);
        webPanel.setLayout(new FlowLayout());
        webPanel.add(createRoundPanel());
        return webPanel;
    }

    private WebPanel createRoundPanel() {
        WebPanel webPanel = new WebPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        webPanel.setMargin(20);
        webPanel.setLayout(new GridBagLayout());
        SwingUtil.setupComponent(webPanel, employeeCard, 0, 0, 2, 1);
        SwingUtil.setupComponent(webPanel, cashCard, 0, 1, 1, 1);
        SwingUtil.setupComponent(webPanel, salesCard, 1, 1, 1, 1);
        SwingUtil.setupComponent(webPanel, insiderCard, 0, 2, 1, 1);
        SwingUtil.setupComponent(webPanel, billCard, 1, 2, 1, 1);
        SwingUtil.setupComponent(webPanel, createTimeLabel(), 0, 3, 2, 1);
        SwingUtil.setupComponent(webPanel, logoutButton, 0, 4, 2, 1);
        return webPanel;
    }
}
