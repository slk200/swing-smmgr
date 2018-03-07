package org.tizzer.smmgr.system.view;

import com.alee.laf.button.WebToggleButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.managers.style.skin.ninepatch.NPLabelPainter;
import org.tizzer.smmgr.system.template.Initialization;
import org.tizzer.smmgr.system.template.NavigationListener;
import org.tizzer.smmgr.system.util.GridBagUtil;
import org.tizzer.smmgr.system.util.NPatchUtil;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author tizzer
 * @version 1.0
 */
public class FunctionsBoundary extends Initialization implements NavigationListener {

    private WebLabel[] items;
    private Container parent;

    public FunctionsBoundary(Container parent) {
        super();
        this.parent = parent;
    }

    @Override
    public void initProp() {
        setLayout(new GridBagLayout());
        setMargin(50);
        setOpaque(false);
    }

    @Override
    public void initVal() {
        String[] keys = {"交接班", "新增会员", "销售单据", "退货", "调货", "报损", "商品编辑", "系统设置", "帮助", "通知"};
        items = new WebLabel[keys.length];
        for (int i = 0; i < keys.length; i++) {
            items[i] = new WebLabel(keys[i], WebLabel.CENTER);
            items[i].setPainter(new NPLabelPainter(NPatchUtil.getNinePatchIcon(i % keys.length)));
            items[i].setForeground(Color.WHITE);
            items[i].setFontSize(18);
            items[i].setBoldFont(true);
            items[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            items[i].setPreferredSize(150, 100);
        }
    }

    @Override
    public void initView() {
        for (int i = 0; i < items.length; i++) {
            GridBagUtil.setupComponent(this, items[i], i % 5, i / 5, 1, 1);
        }
    }

    @Override
    public void initAction() {
        items[0].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                WebFrame root = (WebFrame) getRootPane().getParent();
                root.setGlassPane(new TransUserBoundary());
                root.getGlassPane().setVisible(true);
            }
        });
        items[1].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.removeAll();
                parent.add(new AddVipBoundary(FunctionsBoundary.this), "North");
                parent.validate();
                parent.repaint();
            }
        });
        items[2].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.removeAll();
                parent.add(new SalesRecordBoundary());
                parent.validate();
                parent.repaint();
                FunctionsBoundary.this.performChange(2);
            }
        });
        items[3].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        items[4].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        items[5].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        items[6].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        items[7].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        items[8].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        items[9].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
    }

    @Override
    public void performChange(int index) {
        WebPanel navPanel = (WebPanel) parent.getParent().getParent().getComponent(0);
        WebToggleButton navButton = (WebToggleButton) navPanel.getComponent(index);
        navButton.setSelected(true);
    }
}
