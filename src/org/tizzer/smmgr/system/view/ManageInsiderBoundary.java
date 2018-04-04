package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryInsiderRequestDto;
import org.tizzer.smmgr.system.model.response.QueryInsiderResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;
import org.tizzer.smmgr.system.view.component.JPageView;
import org.tizzer.smmgr.system.view.dialog.UpdateInsiderTypeDialog;
import org.tizzer.smmgr.system.view.listener.PageListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageInsiderBoundary extends WebPanel implements PageListener {

    private final static Class clazz = ManageStoreBoundary.class;
    private final static Object[] tableHead = {"会员卡号", "会员姓名", "会员电话", "会员地址", "会员类型", "备注", "会员生日", "录入时间"};

    private JPageView pageView;
    private WebButton setButton;

    public ManageInsiderBoundary() {
        pageView = createPageView();
        setButton = createBootstrapButton();

        this.prepareData();
        this.setOpaque(false);
        this.setMargin(5, 10, 5, 5);
        this.add(pageView, "Center");
        this.add(createRightPanel(), "East");
        this.initListener();
    }

    private void initListener() {
        setButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (UpdateInsiderTypeDialog.newInstance()) {
                    pageView.refresh();
                }
            }
        });
    }

    @Override
    public void pagePerformed(String startDate, String endDate, String keyword, int pageSize, int currentPage) {
        QueryInsiderResponseDto queryInsiderResponseDto = queryInsider(startDate, endDate, keyword, pageSize, currentPage);
        if (queryInsiderResponseDto.getCode() == ResultCode.OK) {
            refreshData(queryInsiderResponseDto);
        } else {
            SwingUtil.showNotification("访问出错，" + queryInsiderResponseDto.getMessage());
        }
    }

    /**
     * 查询满足条件的所有会员
     *
     * @param startDate
     * @param endDate
     * @param keyword
     * @param pageSize
     * @param currentPage
     * @return
     */
    private QueryInsiderResponseDto queryInsider(String startDate, String endDate, String keyword, int pageSize, int currentPage) {
        QueryInsiderResponseDto querySomeStoreResponseDto = new QueryInsiderResponseDto();
        try {
            QueryInsiderRequestDto queryInsiderRequestDto = new QueryInsiderRequestDto();
            queryInsiderRequestDto.setStartDate(startDate);
            queryInsiderRequestDto.setEndDate(endDate);
            queryInsiderRequestDto.setKeyword(keyword);
            queryInsiderRequestDto.setPageSize(pageSize);
            queryInsiderRequestDto.setCurrentPage(currentPage - 1);
            querySomeStoreResponseDto = HttpHandler.get("/query/insider?" + queryInsiderRequestDto.toString(), QueryInsiderResponseDto.class);
        } catch (Exception e) {
            Logcat.type(clazz, e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return querySomeStoreResponseDto;
    }

    /**
     * 刷新数据
     *
     * @param querySomeStoreResponseDto
     */
    private void refreshData(QueryInsiderResponseDto querySomeStoreResponseDto) {
        pageView.setTableBody(querySomeStoreResponseDto.getData());
        pageView.setPageIndicator(querySomeStoreResponseDto.getPageCount());
    }

    /**
     * 准备数据
     */
    private void prepareData() {
        QueryInsiderResponseDto queryInsiderResponseDto = queryInsider(null, null, "", 30, 1);
        if (queryInsiderResponseDto.getCode() == ResultCode.OK) {
            pageView.prepareData(tableHead, queryInsiderResponseDto.getData(), queryInsiderResponseDto.getPageCount());
        } else {
            SwingUtil.showNotification("访问出错，" + queryInsiderResponseDto.getMessage());
        }
    }

    private WebPanel createRightPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(10);
        webPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.MIDDLE));
        webPanel.add(setButton);
        return webPanel;
    }

    private JPageView createPageView() {
        JPageView jPageView = new JPageView();
        jPageView.setInputPrompt("卡号/姓名/电话/地址");
        jPageView.addDateChangeListener(this);
        return jPageView;
    }

    private WebButton createBootstrapButton() {
        WebButton webButton = new WebButton("设置");
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }

}
