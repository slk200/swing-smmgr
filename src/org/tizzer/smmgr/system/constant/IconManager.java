package org.tizzer.smmgr.system.constant;

import javax.swing.*;
import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public interface IconManager {
    //taskbar
    Image TASKBAR = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/taskbar.png")).getImage();

    //ad.
    ImageIcon POSTER1 = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/poster1.png"));
    ImageIcon POSTER2 = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/poster2.png"));
    ImageIcon POSTER3 = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/poster3.png"));

    //leading
    ImageIcon ACCOUNT = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/account.png"));
    ImageIcon PASSWORD = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/password.png"));
    ImageIcon SECURITY = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/security.png"));

    //button
    ImageIcon SEARCH = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/search.png"));
    ImageIcon RESETDESK = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/resetdesk.png"));
    ImageIcon ADDGOODS = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/addgoods.png"));
    ImageIcon DELETERECORD = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/deleterecord.png"));
    ImageIcon PREVIOUS = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/previous.png"));
    ImageIcon NEXT = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/next.png"));

    //navigation
    ImageIcon HOME = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/home.png"));
    ImageIcon TRADERECORD = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/traderecord.png"));
    ImageIcon FACT = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/fact.png"));
    ImageIcon STORE = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/store.png"));
    ImageIcon EMPLOYEE = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/employee.png"));
    ImageIcon INSIDER = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/insider.png"));
    ImageIcon GOODSEDIT = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/goodsedit.png"));
    ImageIcon BOOKGOODS = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/bookgoods.png"));
    ImageIcon GOODSLOSS = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/goodsloss.png"));
    ImageIcon PURCHASEGOODS = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/purchasegoods.png"));
    ImageIcon REFUNDGOODS = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/refundgoods.png"));
    ImageIcon TRANSGOODS = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/transgoods.png"));
    ImageIcon SETUP = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/setup.png"));
    ImageIcon HELP = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/help.png"));

    //tag
    ImageIcon ADMINTAG = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/admintag.png"));
    ImageIcon STANDARDTAG = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/standardtag.png"));
    ImageIcon USINGTAG = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/usingtag.png"));
    ImageIcon STOPTAG = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/stoptag.png"));
    ImageIcon INCOMETAG = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/incometag.png"));
    ImageIcon REFUNDTAG = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/refundtag.png"));
    ImageIcon BOOKTAG = new ImageIcon(IconManager.class.getResource("/org/tizzer/smmgr/image/other/booktag.png"));
}