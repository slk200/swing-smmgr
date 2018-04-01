package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryEmployeeRequestDto;
import org.tizzer.smmgr.system.model.response.QueryEmployeeResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;
import org.tizzer.smmgr.system.view.component.WebPageView;
import org.tizzer.smmgr.system.view.dialog.AddEmployeeDialog;
import org.tizzer.smmgr.system.view.dialog.UpdateEmployeeDialog;
import org.tizzer.smmgr.system.view.listener.PageListener;
import org.tizzer.smmgr.system.view.renderer.AuthorityRenderer;
import org.tizzer.smmgr.system.view.renderer.StateRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageEmployeeBoundary extends WebPanel implements PageListener {

    private final static Class clazz = ManageEmployeeBoundary.class;
    private final static Object[] tableHead = {"员工号", "姓名", "电话", "地址", "所属门店", "注册时间", "权限", "状态"};

    private WebPageView pageView;
    private WebButton addButton;
    private WebButton editButton;

    public ManageEmployeeBoundary() {
        pageView = createPageView();
        addButton = createBootstrapButton("新增");
        editButton = createBootstrapButton("编辑");

        this.prepareData();
        this.setOpaque(false);
        this.setMargin(5, 10, 5, 5);
        this.add(pageView, "Center");
        this.add(createRightPanel(), "East");
        this.initListener();
    }

    private void initListener() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (AddEmployeeDialog.newInstance()) {
                    pageView.refresh();
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pageView.getSelectedRow() == -1) {
                    SwingUtil.showTip(editButton, "请先选择一条数据");
                    return;
                }
                if (UpdateEmployeeDialog.newInstance(pageView.getSelectedRowData(pageView.getSelectedRow()))) {
                    pageView.refresh();
                }
            }
        });
    }

    @Override
    public void pagePerformed(String startDate, String endDate, String keyword, int pageSize, int currentPage) {
        QueryEmployeeResponseDto queryEmployeeResponseDto = queryEmployee(startDate, endDate, keyword, pageSize, currentPage);
        refreshData(queryEmployeeResponseDto);
    }

    private QueryEmployeeResponseDto queryEmployee(String startDate, String endDate, String keyword, int pageSize, int currentPage) {
        QueryEmployeeResponseDto queryEmployeeResponseDto = new QueryEmployeeResponseDto();
        try {
            QueryEmployeeRequestDto queryEmployeeRequestDto = new QueryEmployeeRequestDto();
            queryEmployeeRequestDto.setStartDate(startDate);
            queryEmployeeRequestDto.setEndDate(endDate);
            queryEmployeeRequestDto.setKeyword(keyword);
            queryEmployeeRequestDto.setPageSize(pageSize);
            queryEmployeeRequestDto.setCurrentPage(currentPage - 1);
            queryEmployeeResponseDto = HttpHandler.get("/query/employee?" + queryEmployeeRequestDto.toString(), QueryEmployeeResponseDto.class);
        } catch (Exception e) {
            Logcat.type(clazz, e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryEmployeeResponseDto;
    }

    private void refreshData(QueryEmployeeResponseDto queryEmployeeResponseDto) {
        pageView.setTableBody(queryEmployeeResponseDto.getData());
        pageView.setPageIndicator(queryEmployeeResponseDto.getPageCount());
        this.setRenderer();
    }

    private void prepareData() {
        QueryEmployeeResponseDto queryEmployeeResponseDto = queryEmployee(null, null, "", 30, 1);
        pageView.prepareData(tableHead, queryEmployeeResponseDto.getData(), queryEmployeeResponseDto.getPageCount());
        this.setRenderer();
    }

    private void setRenderer() {
        pageView.setColumnCellRenderer(6, new AuthorityRenderer());
        pageView.setColumnCellRenderer(7, new StateRenderer());
    }

    private WebPanel createRightPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setMargin(10);
        webPanel.setOpaque(false);
        webPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.MIDDLE));
        webPanel.add(addButton, createVSpace(), editButton);
        return webPanel;
    }

    private WebPageView createPageView() {
        WebPageView webPageView = new WebPageView();
        webPageView.setInputPrompt("员工号/姓名/地址/电话");
        webPageView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        webPageView.addDateChangeListener(this);
        return webPageView;
    }

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }

    private Component createVSpace() {
        return Box.createVerticalStrut(10);
    }
}
