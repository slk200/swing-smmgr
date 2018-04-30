package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.TableLayout;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.Direction;
import com.alee.extended.transition.effects.slide.SlideTransitionEffect;
import com.alee.extended.transition.effects.slide.SlideType;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.analysis.IdentityCostResponseDto;
import org.tizzer.smmgr.system.model.analysis.PayTypeCostResponseDto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageStateBoundary extends WebPanel implements ActionListener {

    private Font defaultFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
    private Font titleFont = new Font("Microsoft YaHei", Font.PLAIN, 18);

    private JFreeChart costChart;
    private ComponentTransition transition;
    private WebButton previousButton;
    private WebButton nextButton;
    private int currentIndex = 0;

    ManageStateBoundary() {
        previousButton = createSwitchButton(IconManager.PREVIOUS, IconManager.PREVIOUSOVER, IconManager.PREVIOUSPRESS);
        previousButton.setVisible(false);
        nextButton = createSwitchButton(IconManager.NEXT, IconManager.NEXTOVER, IconManager.NEXTPRESS);
        previousButton.addActionListener(this);
        nextButton.addActionListener(this);
        this.createIdentityCostChart();
        transition = createAnimationComponent();

        this.createContentPane(previousButton, transition, nextButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(previousButton)) {
            ((SlideTransitionEffect) transition.getTransitionEffect()).setDirection(Direction.right);
            switchView(--currentIndex);
        } else {
            ((SlideTransitionEffect) transition.getTransitionEffect()).setDirection(Direction.left);
            switchView(++currentIndex);
        }
    }

    private void switchView(int index) {
        if (index == 0) {
            previousButton.setVisible(false);
            nextButton.setVisible(true);
            this.createIdentityCostChart();
            transition.performTransition(createChartPane(costChart));
        } else {
            previousButton.setVisible(true);
            nextButton.setVisible(false);
            this.createPayTypeCostChart();
            transition.performTransition(createChartPane(costChart));
        }
    }

    private IdentityCostResponseDto getIdentityCost() {
        IdentityCostResponseDto identityCostResponseDto = new IdentityCostResponseDto();
        try {
            identityCostResponseDto = HttpHandler.get("/analysis/identity", IdentityCostResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return identityCostResponseDto;
    }

    private PayTypeCostResponseDto getPayTypeCost() {
        PayTypeCostResponseDto payTypeCostResponseDto = new PayTypeCostResponseDto();
        try {
            payTypeCostResponseDto = HttpHandler.get("/analysis/pay", PayTypeCostResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return payTypeCostResponseDto;
    }

    private void createIdentityCostChart() {
        IdentityCostResponseDto identityCostResponseDto = getIdentityCost();
        DefaultPieDataset dataset = createDataset(identityCostResponseDto.getData());
        this.costChart = createChart("近30天的消费明细(根据顾客权益)", dataset);
        this.setChartPlot(costChart);
        this.setExternalChartPlot(costChart);
    }

    private void createPayTypeCostChart() {
        PayTypeCostResponseDto payTypeCostResponseDto = getPayTypeCost();
        DefaultPieDataset dataset = createDataset(payTypeCostResponseDto.getData());
        this.costChart = createChart("近30天的消费明细(根据支付方式)", dataset);
        this.setChartPlot(costChart);
        this.setExternalChartPlot(costChart, dataset);
    }

    private void createContentPane(JComponent... jComponents) {
        double border = 20;
        double space = 0.20;
        this.setLayout(new TableLayout(new double[][]{
                {border, space, border, TableLayout.FILL, border, space, border},
                {border, space, border, TableLayout.FILL, border, space, border}
        }));
        this.add(jComponents[0], "1, 3, c, c");
        this.add(jComponents[1], "3, 3      ");
        this.add(jComponents[2], "5, 3, c, c");
        this.setOpaque(false);
    }

    private ChartPanel createChartPane(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        return chartPanel;
    }

    private DefaultPieDataset createDataset(Map<String, Double> map) {
        if (map == null) {
            return null;
        }
        DefaultPieDataset dataset = new DefaultPieDataset();
        Set<Map.Entry<String, Double>> set = map.entrySet();
        for (Map.Entry<String, Double> element : set) {
            dataset.setValue(element.getKey(), element.getValue());
        }
        return dataset;
    }

    private JFreeChart createChart(String title, DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, false, false);
        chart.getLegend().setItemFont(defaultFont);
        chart.getTitle().setFont(titleFont);
        chart.setBackgroundPaint(ColorManager._241_246_253);
        return chart;
    }

    private void setChartPlot(JFreeChart chart) {
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setNoDataMessage("暂时没有数据");
        plot.setNoDataMessageFont(defaultFont);
        plot.setNoDataMessagePaint(Color.RED);
        plot.setOutlineVisible(false);
        plot.setSectionOutlinesVisible(false);
        plot.setLabelBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(ColorManager._212_234_255);
        plot.setLabelFont(defaultFont);
        plot.setLabelBackgroundPaint(new Color(254, 234, 12));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}：￥: {1}    {2}"));
    }

    private void setExternalChartPlot(JFreeChart chart) {
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setExplodePercent("会员消费", 0.2);
        plot.setSectionPaint("会员消费", new Color(167, 118, 72));
        plot.setSectionPaint("普通消费", new Color(255, 97, 1));
    }

    private void setExternalChartPlot(JFreeChart chart, DefaultPieDataset dataset) {
        PiePlot plot = (PiePlot) chart.getPlot();
        List keys = dataset.getKeys();
        for (int i = 1; i < keys.size(); i++) {
            plot.setExplodePercent((Comparable) keys.get(i), 0.2);
        }
    }

    private WebButton createSwitchButton(ImageIcon icon, ImageIcon overIcon, ImageIcon pressIcon) {
        WebButton webButton = new WebButton();
        webButton.setIcon(icon);
        webButton.setRolloverIcon(overIcon);
        webButton.setPressedIcon(pressIcon);
        webButton.setMoveIconOnPress(false);
        webButton.setUndecorated(true);
        return webButton;
    }

    private ComponentTransition createAnimationComponent() {
        ComponentTransition componentTransition = new ComponentTransition();
        componentTransition.setOpaque(false);
        componentTransition.setContent(createChartPane(costChart));
        componentTransition.addTransitionEffect(createSlideTransitionEffect());
        return componentTransition;
    }

    private SlideTransitionEffect createSlideTransitionEffect() {
        SlideTransitionEffect effect = new SlideTransitionEffect();
        effect.setFade(false);
        effect.setSpeed(50);
        effect.setType(SlideType.moveBoth);
        effect.setDirection(Direction.left);
        return effect;
    }
}
