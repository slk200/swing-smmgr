package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import org.tizzer.smmgr.system.component.WebShadowBorder;
import org.tizzer.smmgr.system.manager.ColorManager;
import org.tizzer.smmgr.system.manager.IconManager;
import org.tizzer.smmgr.system.template.Initialization;

import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author tizzer
 * @version 1.0
 */
public class NotifyBoundary extends Initialization {

    private static final int STATE_OK = 0x01;
    private static final int STATE_ERR = 0x02;
    private static final int STATE_LOAD = 0x03;

    private int mState = 0;
    private WebLabel label$state;
    private WebLabel label$user;
    private WebLabel label$event$log;
    private WebLabel label$trans$user;
    private WebPopupMenu popup$user$info;

    public NotifyBoundary() {
        super();
    }

    @Override
    public void initProp() {
        setBackground(ColorManager._241_246_253);
        setBorder(new CompoundBorder(
                WebShadowBorder.newBuilder().shadowColor(Color.BLACK).shadowAlpha(0.7f).shadowSize(2).top().build(),
                null
        ));
    }

    @Override
    public void initVal() {
        label$state = new WebLabel();
        label$state.setIconTextGap(10);
        label$user = new WebLabel();
        label$user.setIcon(IconManager._ICON_USER);
        label$user.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label$event$log = new WebLabel();
        label$event$log.setIcon(IconManager._ICON_NOTICE);
        label$event$log.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label$trans$user = new WebLabel();
        label$trans$user.setIcon(IconManager._ICON_TRANSUSER);
        label$trans$user.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateState(STATE_OK);
    }

    @Override
    public void initView() {
        add(new WebPanel(new FlowLayout(FlowLayout.LEFT, 20, 10)) {{
            setOpaque(false);
            add(label$state);
        }}, "West");
        add(new WebPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10)) {{
            setOpaque(false);
            add(label$user, label$event$log, label$trans$user);
        }}, "East");
    }

    @Override
    public void initAction() {
        label$user.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (popup$user$info == null) {
                    popup$user$info = new WebPopupMenu() {{
                        add(new WebPanel() {{
                            setOpaque(false);
                            setMargin(5);
                            setLayout(new VerticalFlowLayout(0, 5));
                            add(new WebLabel("1111", IconManager._ICON_USER));
                            add(new WebLabel("1111", IconManager._ICON_USER));
                        }});
                    }};
                }
                popup$user$info.show(label$user, 0 - 18, label$user.getHeight() - 10);
            }
        });

        label$event$log.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("eventlog");
            }
        });

        label$trans$user.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                WebFrame root = (WebFrame) getRootPane().getParent();
                root.setGlassPane(new TransUserBoundary());
                root.getGlassPane().setVisible(true);
            }
        });
    }

    /**
     * 更新信号状态
     *
     * @param state
     */
    private void updateState(int state) {
        if (mState == state) {
            return;
        }
        mState = state;
        switch (mState) {
            case STATE_OK:
                label$state.setIcon(IconManager._ICON_NETON);
                label$state.setText("服务器连接正常");
                break;
            case STATE_ERR:
                label$state.setIcon(IconManager._ICON_NETOFF);
                label$state.setText("服务器连接异常");
                break;
            case STATE_LOAD:
                label$state.setIcon(IconManager._ICON_NETRELOAD);
                label$state.setText("网络恢复，正在重新连接服务器...");
        }
    }

}
