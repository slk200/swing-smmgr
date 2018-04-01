package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.DeleteStoreRequestDto;
import org.tizzer.smmgr.system.model.request.QueryOneStoreRequestDto;
import org.tizzer.smmgr.system.model.request.QueryStoreRequestDto;
import org.tizzer.smmgr.system.model.response.DeleteStoreResponseDto;
import org.tizzer.smmgr.system.model.response.QueryOneStoreResponseDto;
import org.tizzer.smmgr.system.model.response.QueryStoreResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;
import org.tizzer.smmgr.system.view.component.WebPageView;
import org.tizzer.smmgr.system.view.dialog.AddStoreDialog;
import org.tizzer.smmgr.system.view.dialog.UpdateStoreDialog;
import org.tizzer.smmgr.system.view.listener.PageListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageStoreBoundary extends WebPanel implements PageListener {

    private final static Class clazz = ManageStoreBoundary.class;
    private final static Object[] tableHead = {"序号", "门店名", "地址", "录入时间"};

    private WebPageView pageView;
    private WebButton addButton;
    private WebButton editButton;
    private WebButton delButton;

    public ManageStoreBoundary() {
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
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (AddStoreDialog.newInstance()) {
                    pageView.refresh();
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QueryOneStoreResponseDto queryOneStoreResponseDto = getCurrentStore();
                if (queryOneStoreResponseDto.getCode() == ResultCode.OK) {
                    if (UpdateStoreDialog.newInstance(queryOneStoreResponseDto.getData())) {
                        pageView.refresh();
                    }
                } else {
                    SwingUtil.showTip(editButton, queryOneStoreResponseDto.getMessage());
                }
            }
        });

        delButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pageView.getSelectedRow() == -1) {
                    SwingUtil.showTip(delButton, "请至少选择一条数据");
                    return;
                }
                Vector<Integer> id = pageView.getSelectedRowsColumnIndexData(0);
                for (Integer row : id) {
                    if (row.equals(RuntimeConstants.storeId)) {
                        SwingUtil.showTip(delButton, "所在门店不可删除");
                        return;
                    }
                }
                if (deleteStore(id)) {
                    pageView.refresh();
                }
            }
        });
    }

    @Override
    public void pagePerformed(String startDate, String endDate, String keyword, int pageSize, int currentPage) {
        QueryStoreResponseDto queryStoreResponseDto = queryStore(startDate, endDate, keyword, pageSize, currentPage);
        refreshData(queryStoreResponseDto);
    }

    private boolean deleteStore(Vector<Integer> id) {
        DeleteStoreResponseDto deleteStoreResponseDto = new DeleteStoreResponseDto();
        try {
            DeleteStoreRequestDto deleteStoreRequestDto = new DeleteStoreRequestDto();
            deleteStoreRequestDto.setId(id);
            System.out.println(deleteStoreRequestDto);
            deleteStoreResponseDto = HttpHandler.post("/delete/store", deleteStoreRequestDto.toString(), DeleteStoreResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteStoreResponseDto.getCode() == ResultCode.OK;
    }

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
            Logcat.type(clazz, e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryStoreResponseDto;
    }

    private void refreshData(QueryStoreResponseDto queryStoreResponseDto) {
        pageView.setTableBody(queryStoreResponseDto.getData());
        pageView.setPageIndicator(queryStoreResponseDto.getPageCount());
    }

    private void prepareData() {
        QueryStoreResponseDto queryStoreResponseDto = queryStore(null, null, "", 30, 1);
        pageView.prepareData(tableHead, queryStoreResponseDto.getData(), queryStoreResponseDto.getPageCount());
    }

    private WebPanel createRightPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(10);
        webPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.MIDDLE));
        webPanel.add(addButton, createVSpace(), editButton, createVSpace(), delButton);
        return webPanel;
    }

    private WebPageView createPageView() {
        WebPageView webPageView = new WebPageView();
        webPageView.setInputPrompt("序号/门店名/地址");
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
