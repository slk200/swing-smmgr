package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.panel.WebPanel;
import com.alee.utils.SwingUtils;
import org.tizzer.smmgr.system.component.listener.NavigationListener;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.util.NPatchUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tizzer
 * @version 1.0
 */
public class StandardModeBoundary extends WebPanel implements ActionListener, NavigationListener {

    private WebToggleButton homeButton;
    private WebToggleButton insiderButton;
    private WebToggleButton recordButton;
    private WebToggleButton helpButton;
    private WebToggleButton settingButton;
    private WebToggleButton menuButton;
    private WebPanel notificationPanel;
    private WebPanel handlePanel;

    public StandardModeBoundary() {
        homeButton = createNavigationButton("首页", IconManager._ICON_HOME);
        homeButton.setSelected(true);
        insiderButton = createNavigationButton("新增会员", IconManager._ICON_ADDINSIDIER);
        recordButton = createNavigationButton("销售单据", IconManager._ICON_SALERECORD);
        helpButton = createNavigationButton("帮助", IconManager._ICON_HELP);
        settingButton = createNavigationButton("设置", IconManager._ICON_SETTING);
        menuButton = createNavigationButton("所有功能", IconManager._ICON_FUNCTION);
        notificationPanel = new NotificationBoundary();
        handlePanel = new WebPanel();
        handlePanel.setBackground(Color.WHITE);
        handlePanel.add(new TransactionBoundary());

        homeButton.addActionListener(this);
        insiderButton.addActionListener(this);
        recordButton.addActionListener(this);
        helpButton.addActionListener(this);
        settingButton.addActionListener(this);
        menuButton.addActionListener(this);

        this.add(createNavigationPanel(), BorderLayout.WEST);
        this.add(createHandlePanel(), BorderLayout.CENTER);
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

    private WebPanel createNavigationPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setBackground(ColorManager._31_31_31);
        webPanel.setLayout(new VerticalFlowLayout());
        webPanel.add(homeButton, insiderButton, recordButton, helpButton, settingButton, menuButton);
        SwingUtils.groupButtons(webPanel);
        return webPanel;
    }

    private WebPanel createHandlePanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.add(notificationPanel, BorderLayout.NORTH);
        webPanel.add(handlePanel, BorderLayout.CENTER);
        return webPanel;
    }

    private void changeView(Component component, String constrains) {
        handlePanel.removeAll();
        handlePanel.add(component, constrains);
        handlePanel.validate();
        handlePanel.repaint();
    }

    @Override
    public void performChange(int index) {
        WebPanel navPanel = (WebPanel) getComponent(0);
        WebToggleButton navButton = (WebToggleButton) navPanel.getComponent(index);
        navButton.setSelected(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(homeButton)) {
            changeView(new TransactionBoundary(), "Center");
            return;
        }

        if (e.getSource().equals(insiderButton)) {
            changeView(new AddInsiderBoundary(StandardModeBoundary.this), "North");
            return;
        }

        if (e.getSource().equals(recordButton)) {
            changeView(new SalesRecordBoundary(), "Center");
            return;
        }

        if (e.getSource().equals(helpButton)) {
            //TODO
            return;
        }

        if (e.getSource().equals(settingButton)) {
            //TODO
            return;
        }

        changeView(new FunctionsBoundary(handlePanel), "North");
    }
}
