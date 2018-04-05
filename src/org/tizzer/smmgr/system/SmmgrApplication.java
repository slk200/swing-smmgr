package org.tizzer.smmgr.system;

import com.alee.laf.rootpane.WebFrame;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.view.LoginBoundary;

import javax.swing.*;
import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class SmmgrApplication extends WebFrame {

    public SmmgrApplication() {
        LoginBoundary loginBoundary = new LoginBoundary();
        this.initProperties(loginBoundary);
        this.updateRuntimeParams(this);
        loginBoundary.setClosingOperation(this);
        loginBoundary.setDefaultButton();
    }

    private void initProperties(JComponent component) {
        this.getContentPane().add(component);
        this.setIconImage(IconManager.TASKBAR);
        this.setTitle("超市管家");
        this.setMinimumSize(new Dimension(1280, 720));
        this.setLocationRelativeTo(this);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
    }

    private void updateRuntimeParams(WebFrame frame) {
        RuntimeConstants.root = frame;
    }

}
