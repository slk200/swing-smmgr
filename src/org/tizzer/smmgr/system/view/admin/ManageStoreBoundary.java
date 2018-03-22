package org.tizzer.smmgr.system.view.admin;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.component.WebPageView;
import org.tizzer.smmgr.system.component.listener.DataChangeListener;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.model.request.DeleteStoreRequestDto;
import org.tizzer.smmgr.system.model.request.QueryAllStoreRequestDto;
import org.tizzer.smmgr.system.model.request.QueryOneStoreRequestDto;
import org.tizzer.smmgr.system.model.request.QuerySomeStoreRequestDto;
import org.tizzer.smmgr.system.model.response.DeleteStoreResponseDto;
import org.tizzer.smmgr.system.model.response.QueryAllStoreResponseDto;
import org.tizzer.smmgr.system.model.response.QueryOneStoreResponseDto;
import org.tizzer.smmgr.system.model.response.QuerySomeStoreResponseDto;
import org.tizzer.smmgr.system.resolver.HttpResolver;
import org.tizzer.smmgr.system.util.NPatchUtil;
import org.tizzer.smmgr.system.util.SwingUtil;
import org.tizzer.smmgr.system.view.admin.dialog.AddStoreDialog;
import org.tizzer.smmgr.system.view.admin.dialog.UpdateStoreDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Vector;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageStoreBoundary extends WebPanel implements DataChangeListener {

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
                Vector<Long> id = pageView.getSelectedRowsColumnIndexData(Long.class, 0);
                for (Long row : id) {
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

    private boolean deleteStore(Vector<Long> id) {
        DeleteStoreResponseDto deleteStoreResponseDto = new DeleteStoreResponseDto();
        try {
            DeleteStoreRequestDto deleteStoreRequestDto = new DeleteStoreRequestDto();
            deleteStoreRequestDto.setId(id);
            System.out.println(deleteStoreRequestDto);
            deleteStoreResponseDto = HttpResolver.post("/delete/store", deleteStoreRequestDto.toString(), DeleteStoreResponseDto.class);
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
            queryOneStoreResponseDto = HttpResolver.post("/query/store/one", queryOneStoreRequestDto.toString(), QueryOneStoreResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryOneStoreResponseDto;
    }

    private void initData() {
        QueryAllStoreResponseDto queryAllStoreResponseDto = prepareData();
        pageView.prepareData(tableHead, queryAllStoreResponseDto.getData(), queryAllStoreResponseDto.getPageCount());
    }

    private QueryAllStoreResponseDto prepareData() {
        QueryAllStoreResponseDto queryAllStoreResponseDto = new QueryAllStoreResponseDto();
        try {
            QueryAllStoreRequestDto queryAllStoreRequestDto = new QueryAllStoreRequestDto();
            queryAllStoreRequestDto.setPageSize(pageView.getPageSize());
            queryAllStoreRequestDto.setCurrentPage(pageView.getCurrentPage() - 1);
            queryAllStoreResponseDto = HttpResolver.post("/query/store/all", queryAllStoreRequestDto.toString(), QueryAllStoreResponseDto.class);
        } catch (Exception e) {
            Logcat.type(clazz, e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryAllStoreResponseDto;
    }

    @Override
    public void dataChanged(Date startDate, Date endDate, String keyword, int pageSize, int currentPage) {
        try {
            QuerySomeStoreRequestDto querySomeStoreRequestDto = new QuerySomeStoreRequestDto();
            querySomeStoreRequestDto.setStartDate(startDate);
            querySomeStoreRequestDto.setEndDate(endDate);
            querySomeStoreRequestDto.setKeyWord(keyword);
            querySomeStoreRequestDto.setPageSize(pageSize);
            querySomeStoreRequestDto.setCurrentPage(currentPage - 1);
            QuerySomeStoreResponseDto querySomeStoreResponseDto = HttpResolver.post("/query/store/some", querySomeStoreRequestDto.toString(), QuerySomeStoreResponseDto.class);
            refreshData(querySomeStoreResponseDto);
        } catch (Exception e) {
            Logcat.type(clazz, e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
    }

    private void refreshData(QuerySomeStoreResponseDto querySomeStoreResponseDto) {
        pageView.setTableBody(querySomeStoreResponseDto.getData());
        pageView.setPageIndicator(querySomeStoreResponseDto.getPageCount());
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
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }

    private Component createVSpace() {
        return Box.createVerticalStrut(10);
    }
}
