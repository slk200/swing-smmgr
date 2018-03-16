package org.tizzer.smmgr.system;

import com.alee.laf.rootpane.WebFrame;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.constant.SystemConstants;
import org.tizzer.smmgr.system.manager.IconManager;
import org.tizzer.smmgr.system.util.LafUtil;
import org.tizzer.smmgr.system.view.LoginBoundary;
import org.tizzer.smmgr.system.view.TransUserBoundary;

import javax.swing.*;
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
        this.setPreferredSize(SystemConstants._DEFAULT_SIZE);
        this.setMinimumSize(SystemConstants._DEFAULT_SIZE);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (RuntimeConstants.isLogin) {
                    if (getGlassPane().isVisible()) {
                        return;
                    }
                    setGlassPane(new TransUserBoundary());
                    getGlassPane().validate();
                    getGlassPane().repaint();
                    getGlassPane().setVisible(true);
                } else {
                    System.exit(0);
                }
            }
        });
        loginBoundary.setDefaultButton();
    }

    public static void main(String[] args) {
        LafUtil.setWebLaF();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Launcher();
            }
        });
    }

}
