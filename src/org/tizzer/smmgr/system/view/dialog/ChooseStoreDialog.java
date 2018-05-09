package org.tizzer.smmgr.system.view.dialog;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.scroll.WebScrollPane;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryOtherStoreRequestDto;
import org.tizzer.smmgr.system.model.response.QueryOtherStoreResponseDto;
import org.tizzer.smmgr.system.utils.D9Util;
import org.tizzer.smmgr.system.utils.LafUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author tizzer
 * @version 1.0
 */
public class ChooseStoreDialog extends WebDialog {

    //选择标记
    private static int chooseId = -1;
    private DefaultListModel listModel;
    private WebList list;
    private WebButton chooseButton;
    private WebButton cancelButton;
    //商店缓存
    private Integer[] idCache;

    private ChooseStoreDialog() {
        super(RuntimeConstants.root, "选择分店", true);
        list = createStoreList();
        chooseButton = createBootstrapButton("选择");
        cancelButton = createBootstrapButton("取消");

        this.prepareData();
        this.setBackground(ColorManager._241_246_253);
        this.add(createContentPane(), "Center");
        this.initListener();
    }

    public static int newInstance() {
        ChooseStoreDialog chooseStoreDialog = new ChooseStoreDialog();
        chooseStoreDialog.setSize(200, 400);
        chooseStoreDialog.setLocationRelativeTo(RuntimeConstants.root);
        chooseStoreDialog.setVisible(true);
        return chooseId;
    }

    private void initListener() {
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = list.getSelectedIndex();
                    if (index != -1) {
                        chooseId = idCache[list.getSelectedIndex()];
                        dispose();
                    }
                }
            }
        });

        chooseButton.addActionListener(e -> {
            int index = list.getSelectedIndex();
            if (index != -1) {
                chooseId = idCache[index];
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    /**
     * 查询本店外的分店
     *
     * @return
     */
    private QueryOtherStoreResponseDto queryOtherStore() {
        QueryOtherStoreResponseDto queryOtherStoreResponseDto = new QueryOtherStoreResponseDto();
        try {
            QueryOtherStoreRequestDto queryOtherStoreRequestDto = new QueryOtherStoreRequestDto();
            queryOtherStoreRequestDto.setStoreId(RuntimeConstants.storeId);
            queryOtherStoreResponseDto = HttpHandler.get("/query/store/other?" + queryOtherStoreRequestDto.toString(), QueryOtherStoreResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), Logcat.LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryOtherStoreResponseDto;
    }

    /**
     * 准备数据
     */
    private void prepareData() {
        QueryOtherStoreResponseDto queryOtherStoreResponseDto = queryOtherStore();
        if (queryOtherStoreResponseDto.getCode() == ResultCode.OK) {
            addListItem(queryOtherStoreResponseDto.getName());
            this.idCache = queryOtherStoreResponseDto.getId();
        } else {
            LafUtil.showNotification("访问出错，" + queryOtherStoreResponseDto.getMessage());
        }
    }

    /**
     * 添加列表数据
     *
     * @param source
     */
    private void addListItem(Object[] source) {
        if (source != null) {
            for (Object element : source) {
                listModel.addElement(element);
            }
        }
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setMargin(10);
        webPanel.setBackground(ColorManager._241_246_253);
        webPanel.add(new WebLabel("选择调货分店："), "North");
        webPanel.add(new WebScrollPane(list), "Center");
        webPanel.add(createButtonPane(), "South");
        return webPanel;
    }

    private WebPanel createButtonPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        webPanel.add(chooseButton);
        webPanel.add(cancelButton);
        return webPanel;
    }

    private WebList createStoreList() {
        listModel = new DefaultListModel();
        return new WebList(listModel);
    }

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setPainter(D9Util.getNinePatchPainter("brown.xml"));
        return webButton;
    }
}
