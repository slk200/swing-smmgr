package org.tizzer.smmgr.system.component;

import com.alee.laf.text.WebPasswordField;
import org.tizzer.smmgr.system.util.NPatchUtil;

/**
 * @author tizzer
 * @version 1.0
 */
public class WebBSPasswordField extends WebPasswordField {

    public WebBSPasswordField(int columns) {
        this(columns, LEFT);
        this.setPainter(NPatchUtil.getNinePatchPainter("field.xml"));
    }

    public WebBSPasswordField(int columns, int align) {
        super(columns);
        this.setHorizontalAlignment(align);
    }

}
