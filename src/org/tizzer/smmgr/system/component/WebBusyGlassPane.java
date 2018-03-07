package org.tizzer.smmgr.system.component;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.manager.IconManager;

import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class WebBusyGlassPane extends WebPanel {

    public WebBusyGlassPane(String text) {
        this.add(new WebLabel() {{
            setMargin(15);
            setIcon(IconManager._ICON_BUSY);
            setText(text);
            setIconTextGap(10);
            setHorizontalAlignment(CENTER);
        }});
        this.setOpaque(false);
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }

}
