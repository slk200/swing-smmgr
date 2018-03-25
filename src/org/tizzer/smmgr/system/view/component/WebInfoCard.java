package org.tizzer.smmgr.system.view.component;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.constant.ColorManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public class WebInfoCard extends WebPanel {
    private WebLabel keyLabel;
    private WebLabel valueLabel;

    public WebInfoCard() {
        keyLabel = createKeyLabel();
        valueLabel = createValueLabel();
        this.setLayout(new VerticalFlowLayout());
        this.setOpaque(false);
        this.setBorder(new CompoundBorder(
                WebShadowBorder.newBuilder().shadowColor(Color.GRAY).shadowAlpha(0.7f).top().build(),
                null
        ));
        this.add(keyLabel);
        this.add(valueLabel);
    }

    public void setKey(String key) {
        keyLabel.setText(key);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    private WebLabel createKeyLabel() {
        WebLabel webLabel = new WebLabel();
        webLabel.setOpaque(true);
        webLabel.setMargin(5);
        webLabel.setForeground(Color.WHITE);
        webLabel.setBackground(ColorManager._187_141_89);
        webLabel.setPreferredSize(200, 30);
        return webLabel;
    }

    private WebLabel createValueLabel() {
        WebLabel webLabel = new WebLabel();
        webLabel.setMargin(5);
        webLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        webLabel.setFontSize(24);
        webLabel.setPreferredSize(200, 50);
        return webLabel;
    }
}