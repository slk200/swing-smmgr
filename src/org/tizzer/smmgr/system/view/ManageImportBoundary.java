package org.tizzer.smmgr.system.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryBookSpecRequestDto;
import org.tizzer.smmgr.system.model.request.QueryImportRecordRequestDto;
import org.tizzer.smmgr.system.model.response.QueryImportRecordResponseDto;
import org.tizzer.smmgr.system.model.response.QueryImportSpecResponseDto;
import org.tizzer.smmgr.system.utils.D9Util;
import org.tizzer.smmgr.system.utils.LafUtil;
import org.tizzer.smmgr.system.view.component.JRecordView;
import org.tizzer.smmgr.system.view.dialog.AddImportDialog;
import org.tizzer.smmgr.system.view.renderer.RecordRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageImportBoundary extends WebPanel implements JRecordView.RecordListener, ActionListener {
    private static final Object[] tableHead = {"条码", "名称", "进价", "数量"};

    private WebLabel quantityLabel;
    private WebLabel costLabel;
    private WebLabel noteLabel;
    private JRecordView recordView;
    private WebButton addImportButton;

    //当前选中单号缓存
    private Object serialNoCache;

    ManageImportBoundary() {
        quantityLabel = createInfoLabel(getBoldBlackText(""));
        costLabel = createInfoLabel(getBoldOrangeText(""));
        noteLabel = createInfoLabel("备注：");
        addImportButton = createBootstrapButton();
        recordView = createRecordView();

        this.prepareData();
        this.setOpaque(false);
        this.add(recordView, "Center");
        addImportButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean isRefresh = AddImportDialog.newInstance();
        if (isRefresh) {
            prepareData();
        }
    }

    @Override
    public void searchPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryImportRecordResponseDto queryImportRecordResponseDto = queryImportRecord(startDate, endDate, curLoadIndex, loadSize);
        if (queryImportRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.setListItem(queryImportRecordResponseDto.getData());
            recordView.setLoadPage(queryImportRecordResponseDto.getPageCount());
        } else {
            LafUtil.showNotification("访问出错，" + queryImportRecordResponseDto.getMessage());
        }
    }

    @Override
    public void loadPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryImportRecordResponseDto queryImportRecordResponseDto = queryImportRecord(startDate, endDate, curLoadIndex, loadSize);
        if (queryImportRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.addListItem(queryImportRecordResponseDto.getData());
        } else {
            LafUtil.showNotification("访问出错，" + queryImportRecordResponseDto.getMessage());
        }
    }

    @Override
    public void selectPerformed(int index) {
        if (index != -1) {
            Object serialNo = ((Object[]) recordView.getSelectedListSource(index))[0];
            if (!Objects.equals(serialNo, serialNoCache)) {
                serialNoCache = serialNo;
                QueryImportSpecResponseDto queryImportSpecResponseDto = queryImportSpec(serialNo);
                if (queryImportSpecResponseDto.getCode() == ResultCode.OK) {
                    recordView.setTableBody(queryImportSpecResponseDto.getData());
                    quantityLabel.setText(getBoldBlackText(queryImportSpecResponseDto.getQuantity()));
                    costLabel.setText(getBoldOrangeText(String.valueOf(queryImportSpecResponseDto.getCost())));
                    noteLabel.setText("备注：" + queryImportSpecResponseDto.getNote());
                } else {
                    LafUtil.showNotification("访问出错，" + queryImportSpecResponseDto.getMessage());
                }
            }
        }
    }

    /**
     * 查询进货详情
     *
     * @param serialNo
     * @return
     */
    private QueryImportSpecResponseDto queryImportSpec(Object serialNo) {
        QueryImportSpecResponseDto queryImportSpecResponseDto = new QueryImportSpecResponseDto();
        try {
            QueryBookSpecRequestDto queryBookSpecRequestDto = new QueryBookSpecRequestDto();
            queryBookSpecRequestDto.setId(serialNo);
            queryImportSpecResponseDto = HttpHandler.get("/query/import/spec?" + queryBookSpecRequestDto.toString(), QueryImportSpecResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryImportSpecResponseDto;
    }

    /**
     * 查询进货记录
     *
     * @param startDate
     * @param endDate
     * @param currentLoadIndex
     * @param loadSize
     * @return
     */
    private QueryImportRecordResponseDto queryImportRecord(String startDate, String endDate, int currentLoadIndex, int loadSize) {
        QueryImportRecordResponseDto queryImportRecordResponseDto = new QueryImportRecordResponseDto();
        try {
            QueryImportRecordRequestDto queryImportRecordRequestDto = new QueryImportRecordRequestDto();
            queryImportRecordRequestDto.setStartDate(startDate);
            queryImportRecordRequestDto.setEndDate(endDate);
            queryImportRecordRequestDto.setCurrentPage(currentLoadIndex - 1);
            queryImportRecordRequestDto.setPageSize(loadSize);
            queryImportRecordResponseDto = HttpHandler.get("/query/import/record?" + queryImportRecordRequestDto.toString(), QueryImportRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryImportRecordResponseDto;
    }

    /**
     * 设置黑色粗体字
     *
     * @param quantity
     * @return
     */
    private String getBoldBlackText(String quantity) {
        return "<html><font face='Microsoft YaHei' color=black><b>件数：" + quantity + "</b></html>";
    }

    /**
     * 设置橙色粗体字
     *
     * @param cost
     * @return
     */
    private String getBoldOrangeText(String cost) {
        return "<html><font face='Microsoft YaHei' color=orange><b>总额：" + cost + "</b></html>";
    }

    /**
     * 准备数据
     */
    private void prepareData() {
        QueryImportRecordResponseDto queryImportRecordResponseDto = queryImportRecord(null, null, 1, 20);
        if (queryImportRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.setListItem(queryImportRecordResponseDto.getData());
            recordView.setLoadPage(queryImportRecordResponseDto.getPageCount());
        } else {
            LafUtil.showNotification("访问出错，" + queryImportRecordResponseDto.getMessage());
        }
    }

    private WebPanel createExternalPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new GridLayout(2, 2, 0, 10));
        webPanel.setMargin(20, 30, 20, 30);
        webPanel.add(quantityLabel, costLabel, noteLabel, new WebPanel() {{
            this.setOpaque(false);
            this.setLayout(new FlowLayout(FlowLayout.RIGHT));
            this.add(addImportButton);
        }});
        return webPanel;
    }

    private JRecordView createRecordView() {
        JRecordView jRecordView = new JRecordView();
        jRecordView.setTableHead(tableHead);
        jRecordView.setExternalComponent(createExternalPane());
        jRecordView.setListCellRenderer(new RecordRenderer());
        jRecordView.addRecordListener(this);
        return jRecordView;
    }

    private WebLabel createInfoLabel(String text) {
        return new WebLabel(text);
    }

    private WebButton createBootstrapButton() {
        WebButton webButton = new WebButton(" 进货 ");
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(D9Util.getNinePatchPainter("default.xml"));
        return webButton;
    }
}
