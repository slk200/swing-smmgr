package org.tizzer.smmgr.system;

import com.alee.laf.rootpane.WebFrame;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.util.SwingUtil;
import org.tizzer.smmgr.system.view.LoginBoundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author tizzer
 * @version 1.0
 */
public class Launcher extends WebFrame {

    private Launcher() {
        LoginBoundary loginBoundary = new LoginBoundary();
        this.getContentPane().add(loginBoundary);
        this.setTitle("超市管家");
        this.setIconImage(IconManager._ICON_TASKBAR);
        this.setPreferredSize(new Dimension(1280, 800));
        this.setMinimumSize(this.getPreferredSize());
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WebFrame.DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        loginBoundary.setDefaultButton();
        RuntimeConstants.root = this;
    }

    public static void main(String[] args) {
        SwingUtil.setWebLaF();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Launcher();
            }
        });
    }

}
