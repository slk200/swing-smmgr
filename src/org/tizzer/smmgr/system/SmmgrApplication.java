package org.tizzer.smmgr.system;

import com.alee.laf.rootpane.WebFrame;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.utils.SwingUtil;
import org.tizzer.smmgr.system.view.LoginBoundary;

import javax.swing.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class SmmgrApplication extends WebFrame {

    private SmmgrApplication() {
        LoginBoundary loginBoundary = new LoginBoundary();
        this.getContentPane().add(loginBoundary);
        this.setIconImage(IconManager.TASKBAR);
        this.setTitle("超市管家");
        this.setSize(1280, 800);
        this.setMinimumSize(this.getSize());
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
        loginBoundary.setClosingOperation(this);
        loginBoundary.setDefaultButton();
        RuntimeConstants.root = this;
    }

    public static void main(String[] args) {
        SwingUtil.setWebLaF();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SmmgrApplication();
            }
        });
    }

}
