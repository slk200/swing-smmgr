package org.tizzer.smmgr.system.template;

import com.alee.laf.panel.WebPanel;

/**
 * @author tizzer
 * @version 1.0
 */
public abstract class Initialization extends WebPanel {

    public Initialization() {
        initProp();
        initVal();
        initView();
        initAction();
    }

    public abstract void initProp();

    public abstract void initVal();

    public abstract void initView();

    public abstract void initAction();

}
