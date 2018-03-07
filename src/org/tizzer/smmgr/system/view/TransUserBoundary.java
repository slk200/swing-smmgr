package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import org.tizzer.smmgr.system.component.WebBSButton;
import org.tizzer.smmgr.system.component.WebShadowBorder;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.manager.ColorManager;
import org.tizzer.smmgr.system.manager.IconManager;
import org.tizzer.smmgr.system.template.Initialization;
import org.tizzer.smmgr.system.util.GridBagUtil;
import org.tizzer.smmgr.system.util.TimeUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author tizzer
 * @version 1.0
 */
public class TransUserBoundary extends Initialization {

    private WebLabel label$back;
    private Item[] items;
    private WebBSButton button$trans$user;

    public TransUserBoundary() {
        super();
    }

    @Override
    public void initProp() {

    }

    @Override
    public void initVal() {
        label$back = new WebLabel();
        label$back.setIcon(IconManager._ICON_BACK);
        label$back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        String[] keys = {"应有现金：", "总销售额：", "会员充值：", "总单据数："};
        items = new Item[keys.length];
        for (int i = 0; i < keys.length; i++) {
            items[i] = new Item();
            items[i].setKey(keys[i]);
            items[i].setValue(String.valueOf(30));
        }
        button$trans$user = new WebBSButton("交接班并登出", WebBSButton.RED);
        button$trans$user.setFontSize(20);
    }

    @Override
    public void initView() {
        add(new WebPanel() {{
            setBackground(ColorManager._28_102_220);
            add(label$back, "West");
            add(new WebLabel() {{
                setText("交接班");
                setForeground(Color.WHITE);
                setFontSize(30);
                setHorizontalAlignment(SwingConstants.CENTER);
            }}, "Center");
        }}, "North");
        add(new WebPanel() {{
            setMargin(50);
            setOpaque(false);
            setLayout(new FlowLayout());
            add(new WebPanel() {{
                setMargin(20);
                setBackground(Color.WHITE);
                setLayout(new GridBagLayout());
                Item item = new Item();
                item.setKey("收银员：");
                item.setValue(RuntimeConstants.login_account + "（工号：" + RuntimeConstants.login_account + "）");
                GridBagUtil.setupComponent(this, item, 0, 0, 2, 1);
                for (int i = 0; i < items.length; i++) {
                    GridBagUtil.setupComponent(this, items[i], i % 2, i / 2 + 1, 1, 1);
                }
                GridBagUtil.setupComponent(this, new WebLabel() {{
                    setMargin(5);
                    setForeground(ColorManager._187_141_89);
                    setBoldFont(true);
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setText(RuntimeConstants.login_at + " / " + TimeUtil.getCurrentTime());
                }}, 0, 3, 2, 1);
                GridBagUtil.setupComponent(this, button$trans$user, 0, 4, 2, 1);
            }});
        }}, "Center");
    }

    @Override
    public void initAction() {
        label$back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                WebFrame root = (WebFrame) getRootPane().getParent();
                root.getGlassPane().setVisible(false);
            }
        });
        button$trans$user.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRuntimeParam();
                WebFrame root = (WebFrame) getRootPane().getParent();
                root.getContentPane().removeAll();
                root.getContentPane().add(new LoginBoundary());
                root.getContentPane().validate();
                root.getContentPane().repaint();
                root.getGlassPane().setVisible(false);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradientPaint = new GradientPaint(0, 0, Color.ORANGE, getWidth() * 3 / 4, getHeight() * 3 / 4, Color.GREEN, false);
        g2d.setPaint(gradientPaint);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private void updateRuntimeParam() {
        RuntimeConstants.isLogin = false;
        RuntimeConstants.login_at = null;
    }

    class Item extends WebPanel {

        WebLabel item$key;
        WebLabel item$value;

        Item() {
            initVal();
            setLayout(new VerticalFlowLayout());
            setOpaque(false);
            setBorder(new CompoundBorder(
                    WebShadowBorder.newBuilder().shadowColor(Color.GRAY).shadowAlpha(0.7f).top().build(),
                    null
            ));
            add(item$key);
            add(item$value);
        }

        void initVal() {
            item$key = new WebLabel();
            item$key.setOpaque(true);
            item$key.setMargin(5);
            item$key.setForeground(Color.WHITE);
            item$key.setBackground(ColorManager._187_141_89);
            item$key.setPreferredSize(200, 30);
            item$value = new WebLabel();
            item$value.setMargin(5);
            item$value.setHorizontalAlignment(SwingConstants.RIGHT);
            item$value.setFontSize(24);
            item$value.setPreferredSize(200, 50);
        }

        void setKey(String key) {
            item$key.setText(key);
        }

        void setValue(String value) {
            item$value.setText(value);
        }

    }

}
