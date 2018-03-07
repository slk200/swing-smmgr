package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.component.WebBSButton;
import org.tizzer.smmgr.system.manager.ColorManager;
import org.tizzer.smmgr.system.manager.IconManager;
import org.tizzer.smmgr.system.template.Initialization;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tizzer
 * @version 1.0
 */
public class TransactionBoundary extends Initialization {

    private static final Object[] TABLE_HEAD = {"商品条码", "商品名称", "原价", "折扣", "数量", "小计", "现价"};

    private DefaultTableModel table$model;
    private WebTable table$data;
    private WebButton button$add$goods;
    private WebButton button$delete$item;
    private WebToggleButton button$none$bar;
    private WebTextField field$goods;
    private WebButton button$specific;
    private WebTextField field$vip;
    private WebButton button$checkout;

    public TransactionBoundary() {
        super();
    }

    @Override
    public void initProp() {
        setOpaque(false);
    }

    @Override
    public void initVal() {
        table$model = new DefaultTableModel(null, TABLE_HEAD);
        table$data = new WebTable(table$model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        button$add$goods = new WebBSButton("新增商品", IconManager._ICON_ADDGOODS, WebBSButton.BROWN);
        button$delete$item = new WebBSButton("删除", IconManager._ICON_DELETE, WebBSButton.BROWN);
        button$none$bar = new WebToggleButton("无码");
        button$none$bar.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        field$goods = new WebTextField();
        field$goods.setPreferredSize(300, 50);
        field$goods.setInputPrompt("商品条码/商品名称");
        field$goods.setMargin(5);
        field$goods.setTrailingComponent(button$none$bar);
        button$specific = new WebBSButton("详情", WebBSButton.BLUE);
        button$specific.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        field$vip = new WebTextField();
        field$vip.setPreferredSize(300, 50);
        field$vip.setInputPrompt("会员号");
        field$vip.setMargin(5);
        field$vip.setTrailingComponent(button$specific);
        button$checkout = new WebBSButton("收款", WebBSButton.RED);
        button$checkout.setPreferredSize(300, 50);
    }

    @Override
    public void initView() {
        add(new WebScrollPane(table$data), "Center");
        add(new WebPanel() {{
            setOpaque(false);
            setLayout(new VerticalFlowLayout());
            add(new WebPanel() {{
                setOpaque(false);
                setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
                add(button$add$goods);
                add(button$delete$item);
            }});
            add(new WebPanel() {{
                setMargin(10);
                setBackground(ColorManager._242_242_242);
                add(new WebPanel() {{
                    setOpaque(false);
                    setLayout(new FlowLayout());
                    add(field$goods);
                    add(field$vip);
                }}, "West");
                add(new WebPanel() {{
                    setOpaque(false);
                    setLayout(new FlowLayout(FlowLayout.RIGHT));
                    add(button$checkout);
                }}, "East");
            }});
        }}, "South");
    }

    @Override
    public void initAction() {
        button$add$goods.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        button$delete$item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        button$none$bar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        button$specific.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        button$checkout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

}
