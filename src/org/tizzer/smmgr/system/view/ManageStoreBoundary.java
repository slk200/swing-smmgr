package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.DeleteStoreRequestDto;
import org.tizzer.smmgr.system.model.request.QueryOneStoreRequestDto;
import org.tizzer.smmgr.system.model.request.QueryStoreRequestDto;
import org.tizzer.smmgr.system.model.response.DeleteStoreResponseDto;
import org.tizzer.smmgr.system.model.response.QueryOneStoreResponseDto;
import org.tizzer.smmgr.system.model.response.QueryStoreResponseDto;
import org.tizzer.smmgr.system.utils.D9Util;
import org.tizzer.smmgr.system.utils.LafUtil;
import org.tizzer.smmgr.system.view.component.JPageView;
import org.tizzer.smmgr.system.view.dialog.AddStoreDialog;
import org.tizzer.smmgr.system.view.dialog.UpdateStoreDialog;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageStoreBoundary extends WebPanel implements JPageView.PageListener {

    private final static Object[] tableHead = {"序号", "门店名", "地址", "录入时间"};

    private JPageView pageView;
    private WebButton addButton;
    private WebButton editButton;
    private WebButton delButton;

    ManageStoreBoundary() {
        pageView = createPageView();
        addButton = createBootstrapButton("新增");
        editButton = createBootstrapButton("编辑");
        delButton = createBootstrapButton("删除");

        this.prepareData();
        this.setOpaque(false);
        this.setMargin(5, 10, 5, 5);
        this.add(pageView, "Center");
        this.add(createRightPanel(), "East");
        this.initListener();
    }

    private void initListener() {
        addButton.addActionListener(e -> {
            if (AddStoreDialog.newInstance()) {
                pageView.refresh();
            }
        });

        editButton.addActionListener(e -> {
            QueryOneStoreResponseDto queryOneStoreResponseDto = getCurrentStore();
            if (queryOneStoreResponseDto.getCode() == ResultCode.OK) {
                if (UpdateStoreDialog.newInstance(queryOneStoreResponseDto.getData())) {
                    pageView.refresh();
                }
            } else {
                LafUtil.showTip(editButton, queryOneStoreResponseDto.getMessage());
            }
        });

        delButton.addActionListener(e -> {
            if (pageView.getSelectedRow() == -1) {
                LafUtil.showTip(delButton, "请至少选择一条数据");
                return;
            }
            Vector<Integer> id = pageView.getSelectedRowsColumnIndexData(0);
            for (Integer row : id) {
                if (row.equals(RuntimeConstants.storeId)) {
                    LafUtil.showTip(delButton, "所在门店不可删除");
                    return;
                }
            }
            DeleteStoreResponseDto deleteStoreResponseDto = deleteStore(id);
            if (deleteStoreResponseDto.getCode() != ResultCode.OK) {
                LafUtil.showTip(delButton, deleteStoreResponseDto.getMessage());
            } else {
                pageView.refresh();
            }
        });
    }

    @Override
    public void pagePerformed(String startDate, String endDate, String keyword, int pageSize, int currentPage) {
        QueryStoreResponseDto queryStoreResponseDto = queryStore(startDate, endDate, keyword, pageSize, currentPage);
        if (queryStoreResponseDto.getCode() == ResultCode.OK) {
            refreshData(queryStoreResponseDto);
        } else {
            LafUtil.showNotification("访问出错，" + queryStoreResponseDto.getMessage());
        }
    }

    /**
     * 删除分店信息
     *
     * @param id
     * @return
     */
    private DeleteStoreResponseDto deleteStore(Vector<Integer> id) {
        DeleteStoreResponseDto deleteStoreResponseDto = new DeleteStoreResponseDto();
        try {
            DeleteStoreRequestDto deleteStoreRequestDto = new DeleteStoreRequestDto();
            deleteStoreRequestDto.setId(id);
            deleteStoreResponseDto = HttpHandler.post("/delete/store", deleteStoreRequestDto.toString(), DeleteStoreResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteStoreResponseDto;
    }

    /**
     * 查询具体分店
     *
     * @return
     */
    private QueryOneStoreResponseDto getCurrentStore() {
        QueryOneStoreResponseDto queryOneStoreResponseDto = new QueryOneStoreResponseDto();
        try {
            QueryOneStoreRequestDto queryOneStoreRequestDto = new QueryOneStoreRequestDto();
            queryOneStoreRequestDto.setId(RuntimeConstants.storeId);
            queryOneStoreResponseDto = HttpHandler.get("/query/store/one?" + queryOneStoreRequestDto.toString(), QueryOneStoreResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryOneStoreResponseDto;
    }

    /**
     * 查询满足条件的所有分店
     *
     * @param startDate
     * @param endDate
     * @param keyword
     * @param pageSize
     * @param currentPage
     * @return
     */
    private QueryStoreResponseDto queryStore(String startDate, String endDate, String keyword, int pageSize, int currentPage) {
        QueryStoreResponseDto queryStoreResponseDto = new QueryStoreResponseDto();
        try {
            QueryStoreRequestDto queryStoreRequestDto = new QueryStoreRequestDto();
            queryStoreRequestDto.setStartDate(startDate);
            queryStoreRequestDto.setEndDate(endDate);
            queryStoreRequestDto.setKeyword(keyword);
            queryStoreRequestDto.setPageSize(pageSize);
            queryStoreRequestDto.setCurrentPage(currentPage - 1);
            queryStoreResponseDto = HttpHandler.get("/query/store?" + queryStoreRequestDto.toString(), QueryStoreResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryStoreResponseDto;
    }

    /**
     * 刷新数据
     *
     * @param queryStoreResponseDto
     */
    private void refreshData(QueryStoreResponseDto queryStoreResponseDto) {
        pageView.setTableBody(queryStoreResponseDto.getData());
        pageView.setPageIndicator(queryStoreResponseDto.getPageCount());
    }

    /**
     * 准备数据
     */
    private void prepareData() {
        QueryStoreResponseDto queryStoreResponseDto = queryStore(null, null, "", 30, 1);
        if (queryStoreResponseDto.getCode() == ResultCode.OK) {
            pageView.prepareData(tableHead, queryStoreResponseDto.getData(), queryStoreResponseDto.getPageCount());
        } else {
            LafUtil.showNotification("访问出错，" + queryStoreResponseDto.getMessage());
        }
    }

    private WebPanel createRightPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(10);
        webPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.MIDDLE));
        webPanel.add(addButton, createVSpace(), editButton, createVSpace(), delButton);
        return webPanel;
    }

    private JPageView createPageView() {
        JPageView jPageView = new JPageView();
        jPageView.setInputPrompt("序号/门店名/地址");
        jPageView.addDateChangeListener(this);
        return jPageView;
    }

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(D9Util.getNinePatchPainter("default.xml"));
        return webButton;
    }

    private Component createVSpace() {
        return Box.createVerticalStrut(10);
    }
}
