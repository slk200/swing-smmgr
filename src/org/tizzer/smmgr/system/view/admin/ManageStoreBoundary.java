package org.tizzer.smmgr.system.view.admin;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.component.WebPageView;
import org.tizzer.smmgr.system.component.listener.DataChangeListener;
import org.tizzer.smmgr.system.model.request.QueryAllStoreRequestDto;
import org.tizzer.smmgr.system.model.response.QueryAllStoreResponseDto;
import org.tizzer.smmgr.system.resolver.HttpResolver;
import org.tizzer.smmgr.system.util.NPatchUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageStoreBoundary extends WebPanel implements DataChangeListener {

    private final static Class clazz = ManageStoreBoundary.class;
    private final static Object[] tableHead = {"序号", "门店名", "地址"};

    private WebPageView pageView;
    private WebButton addButton;
    private WebButton editButton;
    private WebButton delButton;

    private QueryAllStoreResponseDto queryAllStoreResponseDto;

    public ManageStoreBoundary() {
        pageView = new WebPageView();
        this.prepareData(pageView.getPageSize(), pageView.getCurrentPage());
        pageView.prepareData(tableHead, queryAllStoreResponseDto.getData(), queryAllStoreResponseDto.getPageCount());
        pageView.addDateChangeListener(this);
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
        webPanel.add(addButton, editButton, createVSpace(), delButton);
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

    private Component createVSpace() {
        return Box.createVerticalStrut(20);
    }

    private void prepareData(int pageSize, int currentPage) {
        try {
            QueryAllStoreRequestDto queryAllStoreRequestDto = new QueryAllStoreRequestDto();
            queryAllStoreRequestDto.setPageSize(pageSize);
            queryAllStoreRequestDto.setCurrentPage(currentPage - 1);
            queryAllStoreResponseDto = HttpResolver.post("/queryallstore", queryAllStoreRequestDto.toString(), QueryAllStoreResponseDto.class);
        } catch (Exception e) {
            Logcat.type(clazz, e.getMessage(), LogLevel.ERROR);
        }
    }

    @Override
    public void dataChanged(Date startDate, Date endDate, String keyword, int pageSize, int currentPage) {

    }
}
