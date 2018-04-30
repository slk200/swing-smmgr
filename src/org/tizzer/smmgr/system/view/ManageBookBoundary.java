package org.tizzer.smmgr.system.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryBookRecordRequestDto;
import org.tizzer.smmgr.system.model.request.QueryBookSpecRequestDto;
import org.tizzer.smmgr.system.model.response.QueryBookRecordResponseDto;
import org.tizzer.smmgr.system.model.response.QueryBookSpecResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;
import org.tizzer.smmgr.system.view.component.JRecordView;
import org.tizzer.smmgr.system.view.dialog.AddBookDialog;
import org.tizzer.smmgr.system.view.listener.RecordListener;
import org.tizzer.smmgr.system.view.renderer.RecordRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageBookBoundary extends WebPanel implements RecordListener, ActionListener {
    private static final Object[] tableHead = {"条码", "名称", "进价", "数量"};

    private WebLabel quantityLabel;
    private WebLabel costLabel;
    private WebLabel noteLabel;
    private JRecordView recordView;
    private WebButton addBookButton;

    //当前选中单号缓存
    private Object serialNoCache;

    ManageBookBoundary() {
        quantityLabel = createInfoLabel(getBoldBlackText(""));
        costLabel = createInfoLabel(getBoldOrangeText(""));
        noteLabel = createInfoLabel("备注：");
        addBookButton = createBootstrapButton();
        recordView = createRecordView();

        this.prepareData();
        this.setOpaque(false);
        this.add(recordView, "Center");
        addBookButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean isRefresh = AddBookDialog.newInstance();
        if (isRefresh) {
            prepareData();
        }
    }

    @Override
    public void searchPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryBookRecordResponseDto queryBookRecordResponseDto = queryBookRecord(startDate, endDate, curLoadIndex, loadSize);
        if (queryBookRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.setListItem(queryBookRecordResponseDto.getData());
            recordView.setLoadPage(queryBookRecordResponseDto.getPageCount());
        } else {
            SwingUtil.showNotification("访问出错，" + queryBookRecordResponseDto.getMessage());
        }
    }

    @Override
    public void loadPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryBookRecordResponseDto queryBookRecordResponseDto = queryBookRecord(startDate, endDate, curLoadIndex, loadSize);
        if (queryBookRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.addListItem(queryBookRecordResponseDto.getData());
        } else {
            SwingUtil.showNotification("访问出错，" + queryBookRecordResponseDto.getMessage());
        }
    }

    @Override
    public void selectPerformed(int index) {
        if (index != -1) {
            Object serialNo = ((Object[]) recordView.getSelectedListSource(index))[0];
            if (!Objects.equals(serialNo, serialNoCache)) {
                serialNoCache = serialNo;
                QueryBookSpecResponseDto queryBookSpecResponseDto = queryBookSpec(serialNo);
                if (queryBookSpecResponseDto.getCode() == ResultCode.OK) {
                    recordView.setTableBody(queryBookSpecResponseDto.getData());
                    quantityLabel.setText(getBoldBlackText(queryBookSpecResponseDto.getQuantity()));
                    costLabel.setText(getBoldOrangeText(String.valueOf(queryBookSpecResponseDto.getCost())));
                    noteLabel.setText("备注：" + queryBookSpecResponseDto.getNote());
                } else {
                    SwingUtil.showNotification("访问出错，" + queryBookSpecResponseDto.getMessage());
                }
            }
        }
    }

    /**
     * 查询订单详情
     *
     * @param serialNo
     * @return
     */
    private QueryBookSpecResponseDto queryBookSpec(Object serialNo) {
        QueryBookSpecResponseDto queryBookSpecResponseDto = new QueryBookSpecResponseDto();
        try {
            QueryBookSpecRequestDto queryBookSpecRequestDto = new QueryBookSpecRequestDto();
            queryBookSpecRequestDto.setId(serialNo);
            queryBookSpecResponseDto = HttpHandler.get("/query/book/spec?" + queryBookSpecRequestDto.toString(), QueryBookSpecResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryBookSpecResponseDto;
    }

    /**
     * 查询订单记录
     *
     * @param startDate
     * @param endDate
     * @param currentLoadIndex
     * @param loadSize
     * @return
     */
    private QueryBookRecordResponseDto queryBookRecord(String startDate, String endDate, int currentLoadIndex, int loadSize) {
        QueryBookRecordResponseDto queryBookRecordResponseDto = new QueryBookRecordResponseDto();
        try {
            QueryBookRecordRequestDto queryBookRecordRequestDto = new QueryBookRecordRequestDto();
            queryBookRecordRequestDto.setStartDate(startDate);
            queryBookRecordRequestDto.setEndDate(endDate);
            queryBookRecordRequestDto.setCurrentPage(currentLoadIndex - 1);
            queryBookRecordRequestDto.setPageSize(loadSize);
            queryBookRecordResponseDto = HttpHandler.get("/query/book/record?" + queryBookRecordRequestDto.toString(), QueryBookRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryBookRecordResponseDto;
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
        QueryBookRecordResponseDto queryBookRecordResponseDto = queryBookRecord(null, null, 1, 20);
        if (queryBookRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.setListItem(queryBookRecordResponseDto.getData());
            recordView.setLoadPage(queryBookRecordResponseDto.getPageCount());
        } else {
            SwingUtil.showNotification("访问出错，" + queryBookRecordResponseDto.getMessage());
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
            this.add(addBookButton);
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
        WebButton webButton = new WebButton(" 订货 ");
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }
}
