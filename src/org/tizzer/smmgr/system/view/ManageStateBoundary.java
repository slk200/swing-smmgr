package org.tizzer.smmgr.system.view;

import com.alee.extended.layout.VerticalFlowLayout;
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
import org.tizzer.smmgr.system.model.analysis.AnalysisResponseDto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class ManageStateBoundary extends WebPanel {

    private Font defaultFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
    private Font titleFont = new Font("Microsoft YaHei", Font.PLAIN, 18);

    private JFreeChart costProportionChart;
    private WebButton previousButton;
    private WebButton nextButton;

    public ManageStateBoundary() {
        previousButton = createSwitchButton(IconManager.PREVIOUS, IconManager.PREVIOUSOVER, IconManager.PREVIOUSPRESS);
        nextButton = createSwitchButton(IconManager.NEXT, IconManager.NEXTOVER, IconManager.NEXTPRESS);

        this.prepareData();
        this.setOpaque(false);
        this.add(createSwitchPane(previousButton), "West");
        this.add(createSaleChartPane(costProportionChart), "Center");
        this.add(createSwitchPane(nextButton), "East");
    }

    private AnalysisResponseDto analysis() {
        AnalysisResponseDto analysisResponseDto = new AnalysisResponseDto();
        try {
            analysisResponseDto = HttpHandler.get("/analysis", AnalysisResponseDto.class);
        } catch (Exception e) {
            Logcat.type(getClass(), e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return analysisResponseDto;
    }

    private void prepareData() {
        AnalysisResponseDto analysisResponseDto = analysis();
        DefaultPieDataset dataset = createDataset(analysisResponseDto.getConsumerCost(), analysisResponseDto.getInsiderCost());
        this.costProportionChart = createChart(dataset);
        this.createChartPlot(costProportionChart);
    }

    private WebPanel createSwitchPane(JComponent component) {
        WebPanel webPanel = new WebPanel();
        webPanel.setOpaque(false);
        webPanel.setMargin(0, 50, 0, 50);
        webPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.MIDDLE));
        webPanel.add(component);
        return webPanel;
    }

    private ChartPanel createSaleChartPane(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        chartPanel.setBorder(new EmptyBorder(70, 60, 70, 60));
        return chartPanel;
    }

    private JFreeChart createChart(DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart("消费明细(近30天)", dataset, true, false, false);
        chart.getLegend().setItemFont(defaultFont);
        chart.getTitle().setFont(titleFont);
        chart.setBackgroundPaint(ColorManager._241_246_253);
        return chart;
    }

    private void createChartPlot(JFreeChart chart) {
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setNoDataMessage("暂时没有数据");
        plot.setNoDataMessageFont(defaultFont);
        plot.setNoDataMessagePaint(Color.RED);
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(ColorManager._212_234_255);
        plot.setLabelFont(defaultFont);
        plot.setLabelBackgroundPaint(new Color(254, 234, 12));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}：￥: {1}    {2}"));
        plot.setExplodePercent("会员消费", 0.3);
        plot.setSectionPaint("会员消费", new Color(167, 118, 72));
        plot.setSectionPaint("普通消费", new Color(255, 97, 1));
    }

    private DefaultPieDataset createDataset(Double consumerCost, Double insiderCost) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (consumerCost == 0 && insiderCost == 0) {
            return null;
        }
        dataset.setValue("会员消费", insiderCost);
        dataset.setValue("普通消费", consumerCost);
        return dataset;
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

}
