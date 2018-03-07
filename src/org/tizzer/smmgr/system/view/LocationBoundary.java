package org.tizzer.smmgr.system.view;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import com.alee.laf.rootpane.WebDialog;

/**
 * @author tizzer
 * @version 1.0
 */
public class LocationBoundary extends WebDialog {

    private static final String url = "http://localhost:8080/smmgr/location";

    public LocationBoundary() {
        JWebBrowser webBrowser = new JWebBrowser();
        webBrowser.navigate(url);
        webBrowser.setButtonBarVisible(false);
        webBrowser.setMenuBarVisible(false);
        webBrowser.setBarsVisible(false);
        webBrowser.setStatusBarVisible(false);
        add(webBrowser, "Center");

        setTitle("定位");
        setSize(800, 600);
        setLocationRelativeTo(getRootPane().getParent());
        setVisible(true);
    }

    public static void main(String[] args) {

//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//                frame.getContentPane().add(new LocationBoundary(), BorderLayout.CENTER);
//                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//                frame.setLocationByPlatform(true);
//                frame.setVisible(true);
//            }
//        });
    }

}  