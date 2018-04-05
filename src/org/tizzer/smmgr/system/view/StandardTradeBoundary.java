package org.tizzer.smmgr.system.view;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryTradeRecordRequestDto;
import org.tizzer.smmgr.system.model.request.QueryTradeSpecRequestDto;
import org.tizzer.smmgr.system.model.response.QueryTradeRecordResponseDto;
import org.tizzer.smmgr.system.model.response.QueryTradeSpecResponseDto;
import org.tizzer.smmgr.system.utils.SwingUtil;
import org.tizzer.smmgr.system.view.component.JRecordView;
import org.tizzer.smmgr.system.view.listener.RecordListener;
import org.tizzer.smmgr.system.view.renderer.TradeRecordRenderer;

import java.awt.*;
import java.util.Objects;

/**
 * @author tizzer
 * @version 1.0
 */
public class StandardTradeBoundary extends WebPanel implements RecordListener {

    private static final Object[] tableHead = {"条码", "名称", "售价", "折扣价", "数量"};

    private WebLabel quantityLabel;
    private WebLabel costLabel;
    private WebLabel cardNoLabel;
    private WebLabel phoneLabel;
    private JRecordView recordView;

    //当前选中单号缓存
    private Object serialNoCache;

    public StandardTradeBoundary() {
        quantityLabel = createInfoLabel(getBoldBlackText(""));
        costLabel = createInfoLabel(getBoldOrangeText("", ""));
        cardNoLabel = createInfoLabel("会员：");
        phoneLabel = createInfoLabel("电话：");
        recordView = createRecordView();

        this.prepareData();
        this.setOpaque(false);
        this.add(recordView, "Center");
    }

    @Override
    public void searchPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryTradeRecordResponseDto queryTradeRecordResponseDto = queryTradeRecord(startDate, endDate, keyword, curLoadIndex, loadSize);
        if (queryTradeRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.setListItem(queryTradeRecordResponseDto.getData());
            recordView.setLoadPage(queryTradeRecordResponseDto.getPageCount());
        } else {
            SwingUtil.showNotification("访问出错，" + queryTradeRecordResponseDto.getMessage());
        }
    }

    @Override
    public void loadPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryTradeRecordResponseDto queryTradeRecordResponseDto = queryTradeRecord(startDate, endDate, keyword, curLoadIndex, loadSize);
        if (queryTradeRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.addListItem(queryTradeRecordResponseDto.getData());
        } else {
            SwingUtil.showNotification("访问出错，" + queryTradeRecordResponseDto.getMessage());
        }
    }

    @Override
    public void selectPerformed(int index) {
        if (index != -1) {
            Object serialNo = ((Object[]) recordView.getSelectedListSource(index))[0];
            if (!Objects.equals(serialNo, serialNoCache)) {
                serialNoCache = serialNo;
                QueryTradeSpecResponseDto queryTradeSpecResponseDto = queryTradeSpec(serialNo);
                if (queryTradeSpecResponseDto.getCode() == ResultCode.OK) {
                    recordView.setTableBody(queryTradeSpecResponseDto.getData());
                    quantityLabel.setText(getBoldBlackText(queryTradeSpecResponseDto.getQuantity()));
                    costLabel.setText(getBoldOrangeText(String.valueOf(queryTradeSpecResponseDto.getCost()), queryTradeSpecResponseDto.getPayType()));
                    cardNoLabel.setText("会员：" + queryTradeSpecResponseDto.getCardNo());
                    phoneLabel.setText("电话：" + queryTradeSpecResponseDto.getPhone());
                } else {
                    SwingUtil.showNotification("访问出错，" + queryTradeSpecResponseDto.getMessage());
                }
            }
        }
    }

    /**
     * 查询员工相关交易记录
     *
     * @param startDate
     * @param endDate
     * @param keyword
     * @param curLoadIndex
     * @param loadSize
     * @return
     */
    private QueryTradeRecordResponseDto queryTradeRecord(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryTradeRecordResponseDto queryTradeRecordResponseDto = new QueryTradeRecordResponseDto();
        try {
            QueryTradeRecordRequestDto queryTradeGoodsRequestDto = new QueryTradeRecordRequestDto();
            queryTradeGoodsRequestDto.setStartDate(startDate);
            queryTradeGoodsRequestDto.setEndDate(endDate);
            queryTradeGoodsRequestDto.setKeyword(keyword);
            queryTradeGoodsRequestDto.setCurrentPage(curLoadIndex - 1);
            queryTradeGoodsRequestDto.setPageSize(loadSize);
            queryTradeGoodsRequestDto.setStaffNo(RuntimeConstants.staffNo);
            queryTradeRecordResponseDto = HttpHandler.get("/query/trade/record?" + queryTradeGoodsRequestDto.toString(), QueryTradeRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryTradeRecordResponseDto;
    }

    /**
     * 查询交易详情
     *
     * @param serialNo
     * @return
     */
    private QueryTradeSpecResponseDto queryTradeSpec(Object serialNo) {
        QueryTradeSpecResponseDto queryTradeSpecResponseDto = new QueryTradeSpecResponseDto();
        try {
            QueryTradeSpecRequestDto queryTradeSpecRequestDto = new QueryTradeSpecRequestDto();
            queryTradeSpecRequestDto.setSerialNo(serialNo);
            queryTradeSpecResponseDto = HttpHandler.get("/query/trade/spec?" + queryTradeSpecRequestDto.toString(), QueryTradeSpecResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryTradeSpecResponseDto;
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
     * @param payType
     * @return
     */
    private String getBoldOrangeText(String cost, String payType) {
        return "<html><font face='Microsoft YaHei' color=orange><b>总额：" + cost + "&nbsp;(付款方式：" + payType + ")</b></html>";
    }

    /**
     * 准备数据
     */
    private void prepareData() {
        QueryTradeRecordResponseDto queryTradeRecordResponseDto = queryTradeRecord(null, null, "", 1, 20);
        recordView.addListItem(queryTradeRecordResponseDto.getData());
        recordView.setLoadPage(queryTradeRecordResponseDto.getPageCount());
    }

    private WebPanel createExternalPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new GridLayout(2, 2, 0, 10));
        webPanel.setMargin(20, 30, 20, 30);
        webPanel.add(quantityLabel, costLabel, cardNoLabel, phoneLabel);
        return webPanel;
    }

    private JRecordView createRecordView() {
        JRecordView jRecordView = new JRecordView();
        jRecordView.setInputPrompt("流水号");
        jRecordView.setTableHead(tableHead);
        jRecordView.setExternalComponent(createExternalPane());
        jRecordView.setListCellRenderer(new TradeRecordRenderer());
        jRecordView.addRecordListener(this);
        return jRecordView;
    }

    private WebLabel createInfoLabel(String text) {
        return new WebLabel(text);
    }
}
