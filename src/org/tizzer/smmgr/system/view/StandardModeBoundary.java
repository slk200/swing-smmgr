package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.managers.popup.PopupStyle;
import com.alee.managers.popup.WebPopup;
import com.alee.utils.SwingUtils;
import org.tizzer.smmgr.system.component.WebShadowBorder;
import org.tizzer.smmgr.system.component.listener.NavigationListener;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.util.NPatchUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class StandardModeBoundary extends WebPanel implements NavigationListener {

    private WebToggleButton homeButton;
    private WebToggleButton insiderButton;
    private WebToggleButton recordButton;
    private WebToggleButton helpButton;
    private WebToggleButton settingButton;
    private WebToggleButton menuButton;
    private WebButton userButton;
    private WebButton logoutButton;
    private WebPopup userInfoPopup;
    private WebPanel handlePanel;

    public StandardModeBoundary() {
        homeButton = createNavigationButton("首页", IconManager._ICON_HOME);
        homeButton.setSelected(true);
        insiderButton = createNavigationButton("新增会员", IconManager._ICON_ADDINSIDIER);
        recordButton = createNavigationButton("销售单据", IconManager._ICON_SALERECORD);
        helpButton = createNavigationButton("帮助", IconManager._ICON_HELP);
        settingButton = createNavigationButton("设置", IconManager._ICON_SETTING);
        menuButton = createNavigationButton("所有功能", IconManager._ICON_FUNCTION);
        userButton = createRolloverDecoratedButton(IconManager._ICON_USER);
        logoutButton = createRolloverDecoratedButton(IconManager._ICON_TRANSUSER);
        handlePanel = new WebPanel();
        handlePanel.setBackground(Color.WHITE);
        handlePanel.add(new TransactionBoundary());

        this.add(createNavigationPanel(), BorderLayout.WEST);
        this.add(createHandlePanel(), BorderLayout.CENTER);
        this.initListener();
        this.setClosingOperation();
    }

    private void initListener() {
        userButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (userInfoPopup == null) {
                    userInfoPopup = createUserInfoPopup();
                }
                if (!userInfoPopup.isShowing()) {
                    userInfoPopup.showAsPopupMenu(userButton);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (userInfoPopup != null && userInfoPopup.isShowing()) {
                    userInfoPopup.hidePopup();
                }
            }
        });

        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                WebFrame root = RuntimeConstants.root;
                root.setGlassPane(new TransUserBoundary());
                root.getGlassPane().validate();
                root.getGlassPane().repaint();
                root.getGlassPane().setVisible(true);
            }
        });
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new TransactionBoundary(), "Center");
            }
        });

        insiderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new AddInsiderBoundary(StandardModeBoundary.this), "North");
            }
        });

        recordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new SalesRecordBoundary(), "Center");
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        settingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeView(new FunctionsBoundary(handlePanel), "North");
            }
        });
    }

    private void changeView(Component component, String constrains) {
        handlePanel.removeAll();
        handlePanel.add(component, constrains);
        handlePanel.validate();
        handlePanel.repaint();
    }

    /**
     * set closing operation for the context
     */
    private void setClosingOperation() {
        WebFrame root = RuntimeConstants.root;
        root.removeWindowListener(root.getWindowListeners()[0]);
        root.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (root.getGlassPane().isVisible()) {
                    return;
                }
                root.setGlassPane(new TransUserBoundary());
                root.getGlassPane().validate();
                root.getGlassPane().repaint();
                root.getGlassPane().setVisible(true);
            }
        });
    }

    private void setDefaultClickOperation() {
        WebFrame root = RuntimeConstants.root;
        root.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point mouse = e.getPoint();
                System.out.println(mouse.x + " " + mouse.y);
                Point popup = userInfoPopup.getLocation();
                System.out.println(popup.x + " " + popup.y);
                Dimension popupSize = userInfoPopup.getSize();
                if (mouse.x < popup.x || mouse.x > (popup.x + popupSize.width) || mouse.y < popup.y || mouse.y > (popup.y + popupSize.height)) {
                    userInfoPopup.hidePopup();
                    root.removeMouseListener(root.getMouseListeners()[0]);
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

    private WebPanel createNavigationPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setBackground(ColorManager._31_31_31);
        webPanel.setLayout(new VerticalFlowLayout());
        webPanel.add(homeButton, insiderButton, recordButton, helpButton, settingButton, menuButton);
        SwingUtils.groupButtons(webPanel);
        return webPanel;
    }

    private WebPanel createNotificationPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setBackground(ColorManager._241_246_253);
        webPanel.setMargin(0, 0, 0, 10);
        webPanel.setBorder(new CompoundBorder(
                WebShadowBorder.newBuilder().shadowColor(Color.BLACK).shadowAlpha(0.7f).shadowSize(2).top().build(),
                null
        ));
        webPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        webPanel.add(userButton);
        webPanel.add(logoutButton);
        return webPanel;
    }

    private WebPanel createHandlePanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.add(createNotificationPanel(), BorderLayout.NORTH);
        webPanel.add(handlePanel, BorderLayout.CENTER);
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

    private WebButton createRolloverDecoratedButton(ImageIcon icon) {
        return WebButton.createIconWebButton(icon, true);
    }

    private WebPopup createUserInfoPopup() {
        WebPopup webPopup = new WebPopup();
        webPopup.setPopupStyle(PopupStyle.lightSmall);
        webPopup.setAnimated(true);
        webPopup.add(createUserInfoPanel());
        return webPopup;
    }

    private WebPanel createUserInfoPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(5);
        webPanel.setLayout(new VerticalFlowLayout(0, 5));
        webPanel.add(new WebLabel("1111", IconManager._ICON_USER));
        webPanel.add(new WebLabel("1111", IconManager._ICON_USER));
        return webPanel;
    }
}
