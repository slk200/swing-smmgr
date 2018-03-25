package org.tizzer.smmgr.system.view.listener;

import java.util.Date;

public interface DataChangeListener {

    /**
     * monitor the param's change of the Component
     *
     * @param startDate
     * @param endDate
     * @param keyword
     * @param pageSize
     * @param currentPage
     */
    void dataChanged(Date startDate, Date endDate, String keyword, int pageSize, int currentPage);
}
