package org.tizzer.smmgr.system.util;

import java.awt.*;

/**
 * @author tizzer
 * @version 1.0
 */
public class GridBagUtil {
    /**
     * GridBagLayout布局
     *
     * @param parent
     * @param child
     * @param gridx
     * @param gridy
     * @param gridwidth
     */
    public static void setupComponent(Container parent, Container child, int gridx, int gridy, int gridwidth, int gridheight) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
        gridBagConstraints.gridwidth = gridwidth;
        gridBagConstraints.gridheight = gridheight;
        gridBagConstraints.insets = new Insets(10, 5, 10, 5);
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        parent.add(child, gridBagConstraints);
    }
}
