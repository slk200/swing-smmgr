package org.tizzer.smmgr.system.view;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryLossRecordRequestDto;
import org.tizzer.smmgr.system.model.request.QueryLossSpecRequestDto;
import org.tizzer.smmgr.system.model.response.QueryLossRecordResponseDto;
import org.tizzer.smmgr.system.model.response.QueryLossSpecResponseDto;
import org.tizzer.smmgr.system.utils.LafUtil;
import org.tizzer.smmgr.system.view.component.JRecordView;
import org.tizzer.smmgr.system.view.renderer.LossRecordRenderer;

import java.awt.*;
import java.util.Objects;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageLossBoundary extends WebPanel implements JRecordView.RecordListener {

    private static final Object[] tableHead = {"条码", "名称", "进价", "数量"};

    private WebLabel quantityLabel;
    private WebLabel costLabel;
    private WebLabel noteLabel;
    private JRecordView recordView;

    //当前选中单号缓存
    private Object serialNoCache;

    ManageLossBoundary() {
        quantityLabel = createInfoLabel(getBoldBlackText(""));
        costLabel = createInfoLabel(getBoldOrangeText(""));
        noteLabel = createInfoLabel("备注：");
        recordView = createRecordView();

        this.prepareData();
        this.setOpaque(false);
        this.add(recordView, "Center");
    }

    @Override
    public void searchPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryLossRecordResponseDto queryLossRecordResponseDto = queryLossRecord(startDate, endDate, keyword, curLoadIndex, loadSize);
        if (queryLossRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.setListItem(queryLossRecordResponseDto.getData());
            recordView.setLoadPage(queryLossRecordResponseDto.getPageCount());
        } else {
            LafUtil.showNotification("访问出错，" + queryLossRecordResponseDto.getMessage());
        }
    }

    @Override
    public void loadPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryLossRecordResponseDto queryLossRecordResponseDto = queryLossRecord(startDate, endDate, keyword, curLoadIndex, loadSize);
        if (queryLossRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.addListItem(queryLossRecordResponseDto.getData());
        } else {
            LafUtil.showNotification("访问出错，" + queryLossRecordResponseDto.getMessage());
        }
    }

    @Override
    public void selectPerformed(int index) {
        if (index != -1) {
            Object serialNo = ((Object[]) recordView.getSelectedListSource(index))[0];
            if (!Objects.equals(serialNo, serialNoCache)) {
                serialNoCache = serialNo;
                QueryLossSpecResponseDto queryLossSpecResponseDto = queryLossSpec(serialNo);
                if (queryLossSpecResponseDto.getCode() == ResultCode.OK) {
                    recordView.setTableBody(queryLossSpecResponseDto.getData());
                    quantityLabel.setText(getBoldBlackText(queryLossSpecResponseDto.getQuantity()));
                    costLabel.setText(getBoldOrangeText(String.valueOf(queryLossSpecResponseDto.getCost())));
                    noteLabel.setText("备注：" + queryLossSpecResponseDto.getNote());
                } else {
                    LafUtil.showNotification("访问出错，" + queryLossSpecResponseDto.getMessage());
                }
            }
        }
    }

    /**
     * 查询报损详情
     *
     * @param id
     * @return
     */
    private QueryLossSpecResponseDto queryLossSpec(Object id) {
        QueryLossSpecResponseDto queryLossSpecResponseDto = new QueryLossSpecResponseDto();
        try {
            QueryLossSpecRequestDto queryLossSpecRequestDto = new QueryLossSpecRequestDto();
            queryLossSpecRequestDto.setId(id);
            queryLossSpecResponseDto = HttpHandler.get("/query/loss/spec?" + queryLossSpecRequestDto.toString(), QueryLossSpecResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryLossSpecResponseDto;
    }

    /**
     * 查询报损记录
     *
     * @param startDate
     * @param endDate
     * @param keyword
     * @param curLoadIndex
     * @param loadSize
     * @return
     */
    private QueryLossRecordResponseDto queryLossRecord(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryLossRecordResponseDto queryTradeRecordResponseDto = new QueryLossRecordResponseDto();
        try {
            QueryLossRecordRequestDto queryTradeGoodsRequestDto = new QueryLossRecordRequestDto();
            queryTradeGoodsRequestDto.setStartDate(startDate);
            queryTradeGoodsRequestDto.setEndDate(endDate);
            queryTradeGoodsRequestDto.setKeyword(keyword);
            queryTradeGoodsRequestDto.setCurrentPage(curLoadIndex - 1);
            queryTradeGoodsRequestDto.setPageSize(loadSize);
            queryTradeRecordResponseDto = HttpHandler.get("/query/loss/record?" + queryTradeGoodsRequestDto.toString(), QueryLossRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryTradeRecordResponseDto;
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
        QueryLossRecordResponseDto queryLossRecordResponseDto = queryLossRecord(null, null, "", 1, 20);
        if (queryLossRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.addListItem(queryLossRecordResponseDto.getData());
            recordView.setLoadPage(queryLossRecordResponseDto.getPageCount());
        } else {
            LafUtil.showNotification("访问出错，" + queryLossRecordResponseDto.getMessage());
        }
    }

    private WebPanel createExternalPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new GridLayout(2, 2, 0, 10));
        webPanel.setMargin(20, 30, 20, 30);
        webPanel.add(quantityLabel, costLabel, noteLabel);
        return webPanel;
    }

    private JRecordView createRecordView() {
        JRecordView jRecordView = new JRecordView();
        jRecordView.setInputPrompt("单号");
        jRecordView.setTableHead(tableHead);
        jRecordView.setExternalComponent(createExternalPane());
        jRecordView.setListCellRenderer(new LossRecordRenderer());
        jRecordView.addRecordListener(this);
        return jRecordView;
    }

    private WebLabel createInfoLabel(String text) {
        return new WebLabel(text);
    }
}
