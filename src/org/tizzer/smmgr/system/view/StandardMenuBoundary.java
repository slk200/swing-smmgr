package org.tizzer.smmgr.system.view;

import com.alee.laf.button.WebToggleButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.managers.style.skin.ninepatch.NPLabelPainter;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;
import org.tizzer.smmgr.system.view.listener.NavigationListener;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author tizzer
 * @version 1.0
 */
public class StandardMenuBoundary extends WebPanel implements NavigationListener {

    private WebLabel transUserLabel;
    private WebLabel addInsiderLabel;
    private WebLabel billsLabel;
    private WebLabel refundLabel;
    private WebLabel transLabel;
    private WebLabel purchaseLabel;
    private WebLabel orderLabel;
    private WebLabel lossLabel;
    private WebLabel editLabel;
    private WebLabel helpLabel;
    private Container parent;

    public StandardMenuBoundary(Container parent) {
        transUserLabel = createCardLabel("交接班", "label0");
        addInsiderLabel = createCardLabel("新增会员", "label1");
        editLabel = createCardLabel("商品编辑", "label2");
        billsLabel = createCardLabel("销售单据", "label3");
        refundLabel = createCardLabel("退货", "label4");
        transLabel = createCardLabel("调货", "label5");
        purchaseLabel = createCardLabel("进货", "label6");
        orderLabel = createCardLabel("订货", "label7");
        lossLabel = createCardLabel("报损", "label8");
        helpLabel = createCardLabel("帮助", "label9");
        this.parent = parent;

        this.setMargin(50, 0, 0, 0);
        this.setLayout(new GridBagLayout());
        this.setOpaque(false);
        this.createMenuPanel();
        this.initListener();
    }

    private WebLabel createCardLabel(String text, String paintConfig) {
        WebLabel webLabel = new WebLabel(text, WebLabel.CENTER);
        webLabel.setPainter(new NPLabelPainter(NPatchUtil.getNinePatchIcon(paintConfig)));
        webLabel.setForeground(Color.WHITE);
        webLabel.setFontSize(18);
        webLabel.setBoldFont(true);
        webLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        webLabel.setPreferredSize(150, 100);
        return webLabel;
    }

    private void createMenuPanel() {
        SwingUtil.setupComponent(this, transUserLabel, 0, 0, 1, 1);
        SwingUtil.setupComponent(this, addInsiderLabel, 1, 0, 1, 1);
        SwingUtil.setupComponent(this, billsLabel, 2, 0, 1, 1);
        SwingUtil.setupComponent(this, refundLabel, 3, 0, 1, 1);
        SwingUtil.setupComponent(this, transLabel, 4, 0, 1, 1);
        SwingUtil.setupComponent(this, purchaseLabel, 0, 1, 1, 1);
        SwingUtil.setupComponent(this, orderLabel, 1, 1, 1, 1);
        SwingUtil.setupComponent(this, lossLabel, 2, 1, 1, 1);
        SwingUtil.setupComponent(this, editLabel, 3, 1, 1, 1);
        SwingUtil.setupComponent(this, helpLabel, 4, 1, 1, 1);
    }

    public void initListener() {
        transUserLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                WebFrame root = (WebFrame) getRootPane().getParent();
                root.setGlassPane(new StandardLogoutBoundary());
                root.getGlassPane().validate();
                root.getGlassPane().repaint();
                root.getGlassPane().setVisible(true);
            }
        });
        addInsiderLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.removeAll();
                parent.add(new StandardInsiderBoundary(StandardMenuBoundary.this), "North");
                parent.validate();
                parent.repaint();
                StandardMenuBoundary.this.performChange(1);
            }
        });
        editLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        billsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.removeAll();
                parent.add(new StandardBillsBoundary());
                parent.validate();
                parent.repaint();
                StandardMenuBoundary.this.performChange(2);
            }
        });
        refundLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        transLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        purchaseLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        orderLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        lossLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        helpLabel.addMouseListener(new MouseAdapter() {
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
