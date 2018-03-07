package org.tizzer.smmgr.system.component;

import com.alee.laf.button.WebToggleButton;
import org.tizzer.smmgr.system.util.NPatchUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class WebBSNavButton extends WebToggleButton {

    public WebBSNavButton(String text, ImageIcon imageIcon) {
        super(text, imageIcon);
        setIconTextGap(10);
        setForeground(Color.WHITE);
        setSelectedForeground(Color.WHITE);
        setHorizontalAlignment(SwingConstants.LEFT);
        setMargin(0, 10, 0, 30);
        setPainter(NPatchUtil.getNinePatchPainter("navbutton.xml"));
    }

}
