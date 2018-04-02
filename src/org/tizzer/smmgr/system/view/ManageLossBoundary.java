package org.tizzer.smmgr.system.view;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryLossRecordRequestDto;
import org.tizzer.smmgr.system.model.response.QueryLossRecordResponseDto;
import org.tizzer.smmgr.system.view.component.WebRecordView;
import org.tizzer.smmgr.system.view.listener.RecordListener;
import org.tizzer.smmgr.system.view.renderer.LossRecordRenderer;

import java.awt.*;

public class ManageLossBoundary extends WebPanel implements RecordListener {

    private static final Object[] tableHead = {"条码", "名称", "进价", "数量"};

    private WebLabel quantityLabel;
    private WebLabel costLabel;
    private WebLabel noteLabel;
    private WebRecordView recordView;

    private Object serialNoCache;

    public ManageLossBoundary() {
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
        recordView.setListItem(queryLossRecordResponseDto.getData());
        recordView.setLoadPage(queryLossRecordResponseDto.getPageCount());
    }

    @Override
    public void loadPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryLossRecordResponseDto queryLossRecordResponseDto = queryLossRecord(startDate, endDate, keyword, curLoadIndex, loadSize);
        recordView.addListItem(queryLossRecordResponseDto.getData());
    }

    @Override
    public void selectPerformed(int index) {

    }

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
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryTradeRecordResponseDto;
    }

    private String getBoldBlackText(String quantity) {
        return "<html><font face='Microsoft YaHei' color=black><b>件数：" + quantity + "</b></html>";
    }

    private String getBoldOrangeText(String cost) {
        return "<html><font face='Microsoft YaHei' color=orange><b>总额：" + cost + "</b></html>";
    }

    private void prepareData() {
        QueryLossRecordResponseDto queryLossRecordResponseDto = queryLossRecord(null, null, "", 1, 20);
        recordView.addListItem(queryLossRecordResponseDto.getData());
        recordView.setLoadPage(queryLossRecordResponseDto.getPageCount());
    }

    private WebPanel createExternalPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new GridLayout(2, 2, 0, 10));
        webPanel.setMargin(20, 30, 20, 30);
        webPanel.add(quantityLabel, costLabel, noteLabel);
        return webPanel;
    }

    private WebRecordView createRecordView() {
        WebRecordView webRecordView = new WebRecordView();
        webRecordView.setInputPrompt("单号");
        webRecordView.setTableHead(tableHead);
        webRecordView.setExternalComponent(createExternalPane());
        webRecordView.setListCellRenderer(new LossRecordRenderer());
        webRecordView.addRecordListener(this);
        return webRecordView;
    }

    private WebLabel createInfoLabel(String text) {
        return new WebLabel(text);
    }
}
