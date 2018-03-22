package org.tizzer.smmgr.system.view.admin;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.component.WebPageView;
import org.tizzer.smmgr.system.component.listener.DataChangeListener;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.model.request.QueryAllInsiderRequestDto;
import org.tizzer.smmgr.system.model.request.QuerySomeInsiderRequestDto;
import org.tizzer.smmgr.system.model.response.QueryAllInsiderResponseDto;
import org.tizzer.smmgr.system.model.response.QuerySomeInsiderResponseDto;
import org.tizzer.smmgr.system.resolver.HttpResolver;
import org.tizzer.smmgr.system.util.NPatchUtil;
import org.tizzer.smmgr.system.view.admin.dialog.UpdateInsiderTypeDialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageInsiderBoundary extends WebPanel implements DataChangeListener {

    private final static Class clazz = ManageStoreBoundary.class;
    private final static Object[] tableHead = {"会员卡号", "会员姓名", "会员电话", "会员地址", "会员类型", "备注", "会员生日", "录入时间"};

    private WebPageView pageView;
    private WebButton setButton;

    public ManageInsiderBoundary() {
        pageView = createPageView();
        setButton = createBootstrapButton();

        this.setMargin(5, 10, 5, 5);
        this.setBackground(ColorManager._241_246_253);
        this.add(pageView, BorderLayout.CENTER);
        this.add(createRightPanel(), BorderLayout.EAST);
        this.initListener();
        this.initData();
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

    private void initData() {
        QueryAllInsiderResponseDto queryAllStoreResponseDto = prepareData();
        pageView.prepareData(tableHead, queryAllStoreResponseDto.getData(), queryAllStoreResponseDto.getPageCount());
    }

    private QueryAllInsiderResponseDto prepareData() {
        QueryAllInsiderResponseDto queryAllInsiderResponseDto = new QueryAllInsiderResponseDto();
        try {
            QueryAllInsiderRequestDto queryAllStoreRequestDto = new QueryAllInsiderRequestDto();
            queryAllStoreRequestDto.setPageSize(pageView.getPageSize());
            queryAllStoreRequestDto.setCurrentPage(pageView.getCurrentPage() - 1);
            queryAllInsiderResponseDto = HttpResolver.post("/query/insider/all", queryAllStoreRequestDto.toString(), QueryAllInsiderResponseDto.class);
        } catch (Exception e) {
            Logcat.type(clazz, e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryAllInsiderResponseDto;
    }

    @Override
    public void dataChanged(Date startDate, Date endDate, String keyword, int pageSize, int currentPage) {
        try {
            QuerySomeInsiderRequestDto querySomeInsiderRequestDto = new QuerySomeInsiderRequestDto();
            querySomeInsiderRequestDto.setStartDate(startDate);
            querySomeInsiderRequestDto.setEndDate(endDate);
            querySomeInsiderRequestDto.setKeyWord(keyword);
            querySomeInsiderRequestDto.setPageSize(pageSize);
            querySomeInsiderRequestDto.setCurrentPage(currentPage - 1);
            QuerySomeInsiderResponseDto querySomeStoreResponseDto = HttpResolver.post("/query/insider/some", querySomeInsiderRequestDto.toString(), QuerySomeInsiderResponseDto.class);
            refreshData(querySomeStoreResponseDto);
        } catch (Exception e) {
            Logcat.type(clazz, e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
    }

    private void refreshData(QuerySomeInsiderResponseDto querySomeStoreResponseDto) {
        pageView.setTableBody(querySomeStoreResponseDto.getData());
        pageView.setPageIndicator(querySomeStoreResponseDto.getPageCount());
    }

    private WebPanel createRightPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(10);
        webPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.MIDDLE));
        webPanel.add(setButton);
        return webPanel;
    }

    private WebPageView createPageView() {
        WebPageView webPageView = new WebPageView();
        webPageView.setInputPrompt("卡号/姓名/电话/地址");
        webPageView.addDateChangeListener(this);
        return webPageView;
    }

    private WebButton createBootstrapButton() {
        WebButton webButton = new WebButton("设置");
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }

}
