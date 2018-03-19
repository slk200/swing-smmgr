package org.tizzer.smmgr.system.view.admin;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.component.WebPageView;
import org.tizzer.smmgr.system.util.NPatchUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageEmployeeBoundary extends WebPanel {

    private final static Object[] tableHead = {"序号", "门店名", "地址"};

    private WebPageView pageView;
    private WebButton addButton;
    private WebButton editButton;
    private WebButton delButton;

    public ManageEmployeeBoundary() {
        pageView = new WebPageView();
        addButton = createBootstrapButton("新增", "default.xml");
        editButton = createBootstrapButton("编辑", "default.xml");
        delButton = createBootstrapButton("删除", "red.xml");

        this.setMargin(10);
        this.add(pageView, BorderLayout.CENTER);
        this.add(createRightPanel(), BorderLayout.EAST);
    }

    private WebPanel createRightPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setMargin(10);
        webPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.MIDDLE));
        webPanel.add(addButton, editButton, createVSpace(20), delButton);
        return webPanel;
    }

    private WebButton createBootstrapButton(String text, String colorConfig) {
        WebButton webButton = new WebButton(text);
        webButton.setBoldFont(true);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter(colorConfig));
        return webButton;
    }

    private Component createVSpace(int height) {
        return Box.createVerticalStrut(height);
    }

    private void getData() {

    }

}
