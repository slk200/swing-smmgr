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
import org.tizzer.smmgr.system.common.UPCWorker;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.SaveGoodsRequestDto;
import org.tizzer.smmgr.system.model.response.QueryAllGoodsTypeResponseDto;
import org.tizzer.smmgr.system.model.response.SaveGoodsResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author tizzer
 * @version 1.0
 */
public class AddGoodsDialog extends WebDialog {

    private WebTextField upcField;
    private WebTextField nameField;
    private WebComboBox typeComboBox;
    private WebSpinner jPriceSpinner;
    private WebSpinner sPriceSpinner;
    private WebDateField scDateField;
    private WebSpinner bzDateSpinner;
    private WebButton upcCreateButton;
    private WebButton addButton;
    private WebButton cancelButton;
    //商品类型缓存
    private Integer[] idCache;
    private String[] nameCache;
    //选择的索引
    private Integer chooseId = -1;

    public AddGoodsDialog() {
        super(RuntimeConstants.root, "新增商品", true);
        upcCreateButton = createBootstrapButton("生成");
        upcField = createInfoField(upcCreateButton);
        upcField.setMargin(-4, 2, -4, -6);
        upcField.setEditable(false);
        nameField = createInfoField(null);
        typeComboBox = createGoodsTypeComboBox();
        jPriceSpinner = createPriceSpinner();
        sPriceSpinner = createPriceSpinner();
        scDateField = createSCDateField();
        bzDateSpinner = createNumberSpinner();
        addButton = createBootstrapButton("添加");
        cancelButton = createBootstrapButton("取消");

        this.prepareData();
        this.add(createContentPane());
        this.pack();
        this.setLocationRelativeTo(RuntimeConstants.root);
        this.initListener();
    }

    public static void newInstance() {
        AddGoodsDialog addGoodsDialog = new AddGoodsDialog();
        addGoodsDialog.setVisible(true);
    }

    private void initListener() {
        upcCreateButton.addActionListener(e -> {
            if (upcField.getText().equals("")) {
                Long upc = UPCWorker.createId();
                upcField.setText(String.valueOf(upc));
            }
        });

        addButton.addActionListener(e -> {
            String upc = upcField.getText();
            if (upc.equals("")) {
                SwingUtil.showTip(upcField, "条码不能为空");
                return;
            }
            String name = nameField.getText().trim();
            if (name.equals("")) {
                SwingUtil.showTip(nameField, "名称不能为空");
                return;
            }
            String type = String.valueOf(typeComboBox.getEditor().getItem()).trim();
            for (int i = 0; i < nameCache.length; i++) {
                if (type.equals(nameCache[i])) {
                    chooseId = idCache[i];
                    break;
                }
            }
            SaveGoodsResponseDto saveGoodsResponseDto = saveGoods(upc, name, type);
            if (saveGoodsResponseDto.getCode() != ResultCode.OK) {
                SwingUtil.showTip(addButton, saveGoodsResponseDto.getMessage());
            } else {
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    /**
     * 保存商品
     *
     * @param upc
     * @param name
     * @param type
     * @return
     */
    private SaveGoodsResponseDto saveGoods(String upc, String name, String type) {
        SaveGoodsResponseDto saveGoodsResponseDto = new SaveGoodsResponseDto();
        try {
            SaveGoodsRequestDto saveGoodsRequestDto = new SaveGoodsRequestDto();
            saveGoodsRequestDto.setUpc(upc);
            saveGoodsRequestDto.setName(name);
            saveGoodsRequestDto.setSpell(SpellWorker.getAllFirstLetter(name));
            saveGoodsRequestDto.setjPrice((Double) jPriceSpinner.getValue());
            saveGoodsRequestDto.setsPrice((Double) sPriceSpinner.getValue());
            saveGoodsRequestDto.setInvention(0);
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

    /**
     * 准备数据
     */
    private void prepareData() {
        try {
            QueryAllGoodsTypeResponseDto queryAllGoodsTypeResponseDto = new QueryAllGoodsTypeResponseDto();
            queryAllGoodsTypeResponseDto = HttpHandler.get("/query/goods/type", QueryAllGoodsTypeResponseDto.class);
            this.idCache = queryAllGoodsTypeResponseDto.getId();
            this.nameCache = queryAllGoodsTypeResponseDto.getName();
            typeComboBox.setModel(new DefaultComboBoxModel(nameCache));
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
    }

    private WebPanel createContentPane() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        webPanel.setBackground(ColorManager._241_246_253);
        List<String> stringList = Arrays.asList("条码：", "名称：", "类型：", "进价：", "售价：", "生产期：", "保质期(天)：");
        List<Container> containerList = Arrays.asList(upcField, nameField, typeComboBox, jPriceSpinner, sPriceSpinner, scDateField, bzDateSpinner);
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
        webPanel.add(addButton);
        webPanel.add(cancelButton);
        return webPanel;
    }

    private WebTextField createInfoField(JComponent component) {
        WebTextField webTextField = new WebTextField(20);
        webTextField.setTrailingComponent(component);
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
