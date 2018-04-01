package org.tizzer.smmgr.system.view.listener;

public interface PageListener {

    /**
     * monitor the params' change of the Component<br/>
     * change the component's page data
     *
     * @param startDate
     * @param endDate
     * @param keyword
     * @param pageSize
     * @param currentPage
     */
    void pagePerformed(String startDate, String endDate, String keyword, int pageSize, int currentPage);
}
