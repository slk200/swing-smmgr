package org.tizzer.smmgr.system.component;

import com.alee.laf.button.WebButton;
import org.tizzer.smmgr.system.util.NPatchUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class WebBSButton extends WebButton {

    public static final int BLUE = 0;
    public static final int RED = 1;
    public static final int BROWN = 2;

    public WebBSButton(String text, int type) {
        this(text, null, type);
    }

    public WebBSButton(String text, ImageIcon icon, int type) {
        super(text, icon);
        this.setBoldFont(true);
        this.setForeground(Color.WHITE);
        this.setSelectedForeground(Color.WHITE);
        String configName = null;
        if (type == BLUE) {
            configName = "bsbutton.xml";
        } else if (type == RED) {
            configName = "bsbutton1.xml";
        } else if (type == BROWN) {
            configName = "bsbutton2.xml";
        }
        this.setPainter(NPatchUtil.getNinePatchPainter(configName));
    }


}
