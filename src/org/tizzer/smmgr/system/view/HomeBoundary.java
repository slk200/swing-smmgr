package org.tizzer.smmgr.system.view;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.panel.WebPanel;
import com.alee.utils.SwingUtils;
import org.tizzer.smmgr.system.component.WebBSNavButton;
import org.tizzer.smmgr.system.manager.ColorManager;
import org.tizzer.smmgr.system.manager.IconManager;
import org.tizzer.smmgr.system.template.Initialization;
import org.tizzer.smmgr.system.template.NavigationListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tizzer
 * @version 1.0
 */
public class HomeBoundary extends Initialization implements NavigationListener {

    private WebToggleButton toggle$home;
    private WebToggleButton toggle$add$vip;
    private WebToggleButton toggle$sale$record;
    private WebToggleButton toggle$help;
    private WebToggleButton toggle$setting;
    private WebToggleButton toggle$functions;
    private WebPanel panel$notify;
    private WebPanel panel$handle;

    public HomeBoundary() {
        super();
    }

    @Override
    public void initProp() {

    }

    @Override
    public void initVal() {
        toggle$home = new WebBSNavButton("首页", IconManager._ICON_HOME);
        toggle$home.setSelected(true);
        toggle$add$vip = new WebBSNavButton("新增会员", IconManager._ICON_ADDVIP);
        toggle$sale$record = new WebBSNavButton("销售单据", IconManager._ICON_SALERECORD);
        toggle$help = new WebBSNavButton("帮助", IconManager._ICON_HELP);
        toggle$setting = new WebBSNavButton("设置", IconManager._ICON_SETTING);
        toggle$functions = new WebBSNavButton("所有功能", IconManager._ICON_FUNCTION);
        panel$notify = new NotifyBoundary();
        panel$handle = new WebPanel();
        panel$handle.setBackground(Color.WHITE);
    }

    @Override
    public void initView() {
        add(new WebPanel() {{
            setBackground(ColorManager._31_31_31);
            setLayout(new VerticalFlowLayout());
            add(toggle$home, toggle$add$vip, toggle$sale$record, toggle$help, toggle$setting, toggle$functions);
            SwingUtils.groupButtons(this);
        }}, "West");
        add(new WebPanel() {{
            add(panel$notify, "North");
            add(panel$handle, "Center");
        }}, "Center");
        panel$handle.add(new TransactionBoundary());
    }

    @Override
    public void initAction() {
        toggle$home.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel$handle.removeAll();
                panel$handle.add(new TransactionBoundary(), "Center");
                panel$handle.validate();
                panel$handle.repaint();
            }
        });
        toggle$add$vip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel$handle.removeAll();
                panel$handle.add(new AddVipBoundary(HomeBoundary.this), "North");
                panel$handle.validate();
                panel$handle.repaint();
            }
        });
        toggle$sale$record.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel$handle.removeAll();
                panel$handle.add(new SalesRecordBoundary(), "Center");
                panel$handle.validate();
                panel$handle.repaint();
            }
        });
        toggle$help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NativeInterface.open();
                SwingUtils.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new LocationBoundary();
                    }
                });
                NativeInterface.runEventPump();
            }
        });
        toggle$setting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getActionCommand());
            }
        });
        toggle$functions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel$handle.removeAll();
                panel$handle.add(new FunctionsBoundary(panel$handle), "North");
                panel$handle.validate();
                panel$handle.repaint();
            }
        });
    }

    @Override
    public void performChange(int index) {
        WebPanel navPanel = (WebPanel) getComponent(0);
        WebToggleButton navButton = (WebToggleButton) navPanel.getComponent(index);
        navButton.setSelected(true);
    }

}
