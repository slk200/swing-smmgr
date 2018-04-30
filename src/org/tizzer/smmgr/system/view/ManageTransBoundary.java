package org.tizzer.smmgr.system.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryTransRecordRequestDto;
import org.tizzer.smmgr.system.model.request.QueryTransSpecRequestDto;
import org.tizzer.smmgr.system.model.response.QueryTransRecordResponseDto;
import org.tizzer.smmgr.system.model.response.QueryTransSpecResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;
import org.tizzer.smmgr.system.view.component.JRecordView;
import org.tizzer.smmgr.system.view.dialog.AddTransDialog;
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
public class ManageTransBoundary extends WebPanel implements RecordListener, ActionListener {

    private static final Object[] tableHead = {"条码", "名称", "进价", "数量"};

    private WebLabel quantityLabel;
    private WebLabel costLabel;
    private WebLabel storeLabel;
    private JRecordView recordView;
    private WebButton addTransButton;

    //当前选中单号缓存
    private Object serialNoCache;

    ManageTransBoundary() {
        quantityLabel = createInfoLabel(getBoldBlackText(""));
        costLabel = createInfoLabel(getBoldOrangeText(""));
        storeLabel = createInfoLabel("分店：");
        addTransButton = createBootstrapButton();
        recordView = createRecordView();

        this.prepareData();
        this.setOpaque(false);
        this.add(recordView, "Center");
        addTransButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean isRefresh = AddTransDialog.newInstance();
        if (isRefresh) {
            prepareData();
        }
    }

    @Override
    public void searchPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryTransRecordResponseDto queryTransRecordResponseDto = queryTransRecord(startDate, endDate, curLoadIndex, loadSize);
        if (queryTransRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.setListItem(queryTransRecordResponseDto.getData());
            recordView.setLoadPage(queryTransRecordResponseDto.getPageCount());
        } else {
            SwingUtil.showNotification("访问出错，" + queryTransRecordResponseDto.getMessage());
        }
    }

    @Override
    public void loadPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize) {
        QueryTransRecordResponseDto queryTransRecordResponseDto = queryTransRecord(startDate, endDate, curLoadIndex, loadSize);
        if (queryTransRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.addListItem(queryTransRecordResponseDto.getData());
        } else {
            SwingUtil.showNotification("访问出错，" + queryTransRecordResponseDto.getMessage());
        }
    }

    @Override
    public void selectPerformed(int index) {
        if (index != -1) {
            Object serialNo = ((Object[]) recordView.getSelectedListSource(index))[0];
            if (!Objects.equals(serialNo, serialNoCache)) {
                serialNoCache = serialNo;
                QueryTransSpecResponseDto queryTransSpecResponseDto = queryTransSpec(serialNo);
                if (queryTransSpecResponseDto.getCode() == ResultCode.OK) {
                    recordView.setTableBody(queryTransSpecResponseDto.getData());
                    quantityLabel.setText(getBoldBlackText(queryTransSpecResponseDto.getQuantity()));
                    costLabel.setText(getBoldOrangeText(String.valueOf(queryTransSpecResponseDto.getCost())));
                    storeLabel.setText("分店：" + queryTransSpecResponseDto.getName());
                } else {
                    SwingUtil.showNotification("访问出错，" + queryTransSpecResponseDto.getMessage());
                }
            }
        }
    }

    /**
     * 查询调货详情
     *
     * @param serialNo
     * @return
     */
    private QueryTransSpecResponseDto queryTransSpec(Object serialNo) {
        QueryTransSpecResponseDto queryTransSpecResponseDto = new QueryTransSpecResponseDto();
        try {
            QueryTransSpecRequestDto queryTransSpecRequestDto = new QueryTransSpecRequestDto();
            queryTransSpecRequestDto.setId(serialNo);
            queryTransSpecResponseDto = HttpHandler.get("/query/trans/spec?" + queryTransSpecRequestDto.toString(), QueryTransSpecResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryTransSpecResponseDto;
    }

    /**
     * 查询调货记录
     *
     * @param startDate
     * @param endDate
     * @param currentLoadIndex
     * @param loadSize
     * @return
     */
    private QueryTransRecordResponseDto queryTransRecord(String startDate, String endDate, int currentLoadIndex, int loadSize) {
        QueryTransRecordResponseDto queryTransRecordResponseDto = new QueryTransRecordResponseDto();
        try {
            QueryTransRecordRequestDto queryTransRecordRequestDto = new QueryTransRecordRequestDto();
            queryTransRecordRequestDto.setStartDate(startDate);
            queryTransRecordRequestDto.setEndDate(endDate);
            queryTransRecordRequestDto.setCurrentPage(currentLoadIndex - 1);
            queryTransRecordRequestDto.setPageSize(loadSize);
            queryTransRecordResponseDto = HttpHandler.get("/query/trans/record?" + queryTransRecordRequestDto.toString(), QueryTransRecordResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryTransRecordResponseDto;
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
        QueryTransRecordResponseDto queryTransRecordResponseDto = queryTransRecord(null, null, 1, 20);
        if (queryTransRecordResponseDto.getCode() == ResultCode.OK) {
            recordView.setListItem(queryTransRecordResponseDto.getData());
            recordView.setLoadPage(queryTransRecordResponseDto.getPageCount());
        } else {
            SwingUtil.showNotification("访问出错，" + queryTransRecordResponseDto.getMessage());
        }
    }

    private WebPanel createExternalPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new GridLayout(2, 2, 0, 10));
        webPanel.setMargin(20, 30, 20, 30);
        webPanel.add(quantityLabel, costLabel, storeLabel, new WebPanel() {{
            this.setOpaque(false);
            this.setLayout(new FlowLayout(FlowLayout.RIGHT));
            this.add(addTransButton);
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
        WebButton webButton = new WebButton(" 调货 ");
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }
}
