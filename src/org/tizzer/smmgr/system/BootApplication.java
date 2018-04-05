package org.tizzer.smmgr.system;

import org.tizzer.smmgr.system.handler.XMLHandler;
import org.tizzer.smmgr.system.utils.SwingUtil;

import javax.swing.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class BootApplication {

    public static void main(String[] args) {
        BootApplication bootApplication = new BootApplication();
        bootApplication.entry();
    }

    public void entry() {
        SwingUtil.setWebLaF();
        XMLHandler xmlHandler = new XMLHandler("data-init.xml");
        if (xmlHandler.getBoolean("guide")) {
            SwingUtilities.invokeLater(SmmgrApplication::new);
        } else {
            SwingUtilities.invokeLater(GuideApplication::new);
        }
    }
}
