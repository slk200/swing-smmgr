package org.tizzer.smmgr.system.view.dialog;

import com.alee.extended.date.WebDateField;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.spinner.WebSpinner;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.common.SpellWorker;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryOneGoodsRequestDto;
import org.tizzer.smmgr.system.model.request.SaveGoodsRequestDto;
import org.tizzer.smmgr.system.model.response.QueryAllGoodsTypeResponseDto;
import org.tizzer.smmgr.system.model.response.QueryOneGoodsResponseDto;
import org.tizzer.smmgr.system.model.response.SaveGoodsResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class UpdateGoodsDialog extends WebDialog {

    private static int flag = 0;
    private WebTextField upcField;
    private WebTextField nameField;
    private WebComboBox typeComboBox;
    private WebSpinner jPriceSpinner;
    private WebSpinner sPriceSpinner;
    private WebSpinner inventorySpinner;
    private WebDateField scDateField;
    private WebSpinner bzDateSpinner;
    private WebButton updateButton;
    private WebButton cancelButton;

    private Integer[] idCache;
    private String[] nameCache;
    private Integer chooseId = -1;

    public UpdateGoodsDialog(Object upc) {
        super(RuntimeConstants.root, "编辑商品", true);
        upcField = createDisabledField();
        nameField = createEditableField();
        typeComboBox = createGoodsTypeComboBox();
        jPriceSpinner = createPriceSpinner();
        sPriceSpinner = createPriceSpinner();
        inventorySpinner = createNumberSpinner();
        scDateField = createSCDateField();
        bzDateSpinner = createNumberSpinner();
        updateButton = createBootstrapButton("修改");
        cancelButton = createBootstrapButton("取消");

        this.prepareData(upc);
        this.add(createContentPane());
        this.pack();
        this.setLocationRelativeTo(RuntimeConstants.root);
        this.initListener();
    }

    public static int newInstance(Object upc) {
        UpdateGoodsDialog updateGoodsDialog = new UpdateGoodsDialog(upc);
        updateGoodsDialog.setVisible(true);
        return flag;
    }

    private void initListener() {
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                if (name.equals("")) {
                    SwingUtil.showTip(nameField, "名称不能为空");
                    return;
                }
                String type = ((String) typeComboBox.getEditor().getItem()).trim();
                for (int i = 0; i < nameCache.length; i++) {
                    if (type.equals(nameCache[i])) {
                        chooseId = idCache[i];
                        break;
                    }
                }
                SaveGoodsResponseDto saveGoodsResponseDto = saveGoods(name, type);
                if (saveGoodsResponseDto.getCode() != ResultCode.OK) {
                    SwingUtil.showTip(updateButton, saveGoodsResponseDto.getMessage());
                } else {
                    if (chooseId == -1) {
                        flag = 2;
                    } else {
                        flag = 1;
                    }
                    dispose();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private SaveGoodsResponseDto saveGoods(String name, String type) {
        SaveGoodsResponseDto saveGoodsResponseDto = new SaveGoodsResponseDto();
        try {
            SaveGoodsRequestDto saveGoodsRequestDto = new SaveGoodsRequestDto();
            saveGoodsRequestDto.setUpc(upcField.getText());
            saveGoodsRequestDto.setName(name);
            saveGoodsRequestDto.setSpell(SpellWorker.getAllFirstLetter(name));
            saveGoodsRequestDto.setjPrice((Double) jPriceSpinner.getValue());
            saveGoodsRequestDto.setsPrice((Double) sPriceSpinner.getValue());
            saveGoodsRequestDto.setInvention((Integer) inventorySpinner.getValue());
            saveGoodsRequestDto.setScDate(scDateField.getText());
            saveGoodsRequestDto.setBzDate((Integer) bzDateSpinner.getValue());
            saveGoodsRequestDto.setType(type);
            saveGoodsRequestDto.setId(chooseId);
            System.out.println(saveGoodsRequestDto);
            saveGoodsResponseDto = HttpHandler.post("/save/goods", saveGoodsRequestDto.toString(), SaveGoodsResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return saveGoodsResponseDto;
    }

    private QueryOneGoodsResponseDto queryOneGoods(Object upc) {
        QueryOneGoodsResponseDto queryOneGoodsResponseDto = new QueryOneGoodsResponseDto();
        try {
            QueryOneGoodsRequestDto queryOneGoodsRequestDto = new QueryOneGoodsRequestDto();
            queryOneGoodsRequestDto.setUpc(upc);
            queryOneGoodsResponseDto = HttpHandler.get("/query/goods/one?" + queryOneGoodsRequestDto.toString(), QueryOneGoodsResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryOneGoodsResponseDto;
    }

    private QueryAllGoodsTypeResponseDto queryAllGoodsType() {
        QueryAllGoodsTypeResponseDto queryAllGoodsTypeResponseDto = new QueryAllGoodsTypeResponseDto();
        try {
            queryAllGoodsTypeResponseDto = HttpHandler.get("/query/goods/type", QueryAllGoodsTypeResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return queryAllGoodsTypeResponseDto;
    }

    private void prepareData(Object upc) {
        QueryAllGoodsTypeResponseDto queryAllGoodsTypeResponseDto = queryAllGoodsType();
        this.idCache = queryAllGoodsTypeResponseDto.getId();
        this.nameCache = queryAllGoodsTypeResponseDto.getName();
        typeComboBox.setModel(new DefaultComboBoxModel(nameCache));

        QueryOneGoodsResponseDto queryOneGoodsResponseDto = queryOneGoods(upc);
        upcField.setText(queryOneGoodsResponseDto.getUpc());
        nameField.setText(queryOneGoodsResponseDto.getName());
        jPriceSpinner.setValue(queryOneGoodsResponseDto.getjPrice());
        sPriceSpinner.setValue(queryOneGoodsResponseDto.getsPrice());
        inventorySpinner.setValue(queryOneGoodsResponseDto.getInventory());
        scDateField.setText(queryOneGoodsResponseDto.getScDate());
        bzDateSpinner.setValue(queryOneGoodsResponseDto.getBzDate());
        typeComboBox.setSelectedItem(queryOneGoodsResponseDto.getType());
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        List<String> stringList = Arrays.asList("条码：", "名称：", "类型：", "进价：", "售价：", "库存：", "生产期：", "保质期(天)：");
        List<Container> containerList = Arrays.asList(upcField, nameField, typeComboBox, jPriceSpinner, sPriceSpinner, inventorySpinner, scDateField, bzDateSpinner);
        for (int i = 0; i < stringList.size(); i++) {
            SwingUtil.setupComponent(webPanel, new WebLabel(stringList.get(i)), 0, i, 1, 1);
            SwingUtil.setupComponent(webPanel, containerList.get(i), 1, i, 1, 1);
        }
        SwingUtil.setupComponent(webPanel, createButtonPane(), 0, stringList.size(), 2, 1);
        return webPanel;
    }

    private WebPanel createButtonPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        webPanel.add(updateButton);
        webPanel.add(cancelButton);
        return webPanel;
    }

    private WebTextField createDisabledField() {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setRound(4);
        webTextField.setEnabled(false);
        return webTextField;
    }

    private WebTextField createEditableField() {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setRound(4);
        return webTextField;
    }

    private WebComboBox createGoodsTypeComboBox() {
        WebComboBox webComboBox = new WebComboBox();
        webComboBox.setRound(4);
        webComboBox.setEditable(true);
        return webComboBox;
    }

    private WebSpinner createPriceSpinner() {
        WebSpinner webSpinner = new WebSpinner();
        webSpinner.setModel(new SpinnerNumberModel(0.01, 0.01, Integer.MAX_VALUE, 0.01));
        return webSpinner;
    }

    private WebSpinner createNumberSpinner() {
        WebSpinner webSpinner = new WebSpinner();
        webSpinner.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        return webSpinner;
    }

    private WebDateField createSCDateField() {
        WebDateField webDateField = new WebDateField();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        webDateField.setDateFormat(simpleDateFormat);
        webDateField.setText(simpleDateFormat.format(new Date()));
        webDateField.setRound(4);
        webDateField.setEditable(false);
        return webDateField;
    }

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setCursor(Cursor.getDefaultCursor());
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }
}
