package org.tizzer.smmgr.system.component;

import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.util.NPatchUtil;

/**
 * @author tizzer
 * @version 1.0
 */
public class WebBSTextField extends WebTextField {

    public WebBSTextField(int columns) {
        this(columns, LEFT);
        this.setPainter(NPatchUtil.getNinePatchPainter("field.xml"));
    }

    public WebBSTextField(int columns, int align) {
        super(columns);
        this.setHorizontalAlignment(align);
    }

}
