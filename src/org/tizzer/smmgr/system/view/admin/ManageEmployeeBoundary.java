package org.tizzer.smmgr.system.view.admin;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.component.WebPageView;
import org.tizzer.smmgr.system.component.listener.DataChangeListener;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.model.request.QueryAllEmployeeRequestDto;
import org.tizzer.smmgr.system.model.request.QuerySomeEmployeeRequestDto;
import org.tizzer.smmgr.system.model.response.QueryAllEmployeeResponseDto;
import org.tizzer.smmgr.system.model.response.QuerySomeEmployeeResponseDto;
import org.tizzer.smmgr.system.resolver.HttpResolver;
import org.tizzer.smmgr.system.util.NPatchUtil;
import org.tizzer.smmgr.system.util.SwingUtil;
import org.tizzer.smmgr.system.view.admin.dialog.AddEmployeeDialog;
import org.tizzer.smmgr.system.view.admin.dialog.UpdateEmployeeDialog;
import org.tizzer.smmgr.system.view.admin.renderer.AuthorityRenderer;
import org.tizzer.smmgr.system.view.admin.renderer.StateRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageEmployeeBoundary extends WebPanel implements DataChangeListener {

    private final static Class clazz = ManageEmployeeBoundary.class;
    private final static Object[] tableHead = {"员工号", "姓名", "电话", "地址", "所属门店", "注册时间", "权限", "状态"};

    private WebPageView pageView;
    private WebButton addButton;
    private WebButton editButton;

    public ManageEmployeeBoundary() {
        pageView = createPageView();
        addButton = createBootstrapButton("新增");
        editButton = createBootstrapButton("编辑");

        this.setMargin(5, 10, 5, 5);
        this.setBackground(ColorManager._241_246_253);
        this.add(pageView, BorderLayout.CENTER);
        this.add(createRightPanel(), BorderLayout.EAST);
        this.initListener();
        this.initData();
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

    private void initData() {
        QueryAllEmployeeResponseDto queryAllEmployeeResponseDto = prepareData();
        pageView.prepareData(tableHead, queryAllEmployeeResponseDto.getData(), queryAllEmployeeResponseDto.getPageCount());
        this.setRenderer();
    }

    private QueryAllEmployeeResponseDto prepareData() {
        QueryAllEmployeeResponseDto queryAllEmployeeResponseDto = new QueryAllEmployeeResponseDto();
        try {
            QueryAllEmployeeRequestDto queryAllEmployeeRequestDto = new QueryAllEmployeeRequestDto();
            queryAllEmployeeRequestDto.setPageSize(pageView.getPageSize());
            queryAllEmployeeRequestDto.setCurrentPage(pageView.getCurrentPage() - 1);
            queryAllEmployeeResponseDto = HttpResolver.post("/query/employee/all", queryAllEmployeeRequestDto.toString(), QueryAllEmployeeResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryAllEmployeeResponseDto;
    }

    @Override
    public void dataChanged(Date startDate, Date endDate, String keyword, int pageSize, int currentPage) {
        try {
            QuerySomeEmployeeRequestDto querySomeEmployeeRequestDto = new QuerySomeEmployeeRequestDto();
            querySomeEmployeeRequestDto.setStartDate(startDate);
            querySomeEmployeeRequestDto.setEndDate(endDate);
            querySomeEmployeeRequestDto.setKeyWord(keyword);
            querySomeEmployeeRequestDto.setPageSize(pageSize);
            querySomeEmployeeRequestDto.setCurrentPage(currentPage - 1);
            QuerySomeEmployeeResponseDto querySomeEmployeeResponseDto = HttpResolver.post("/query/employee/some", querySomeEmployeeRequestDto.toString(), QuerySomeEmployeeResponseDto.class);
            refreshData(querySomeEmployeeResponseDto);
        } catch (Exception e) {
            Logcat.type(clazz, e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
    }

    private void refreshData(QuerySomeEmployeeResponseDto querySomeEmployeeResponseDto) {
        pageView.setTableBody(querySomeEmployeeResponseDto.getData());
        pageView.setPageIndicator(querySomeEmployeeResponseDto.getPageCount());
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
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }

    private Component createVSpace() {
        return Box.createVerticalStrut(10);
    }
}
