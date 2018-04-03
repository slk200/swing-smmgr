package org.tizzer.smmgr.system.view.listener;

/**
 * @author tizzer
 * @version 1.0
 */
public interface RecordListener {
    /**
     * monitor the params' change of the component<br/>
     * to obtain and refresh the component's list data
     *
     * @param startDate
     * @param endDate
     * @param keyword
     * @param curLoadIndex
     * @param loadSize
     */
    void searchPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize);

    /**
     * monitor the scrollbar's change<br/>
     * to obtain and refresh the component's list data
     *
     * @param startDate
     * @param endDate
     * @param keyword
     * @param curLoadIndex
     * @param loadSize
     */
    void loadPerformed(String startDate, String endDate, String keyword, int curLoadIndex, int loadSize);

    /**
     * perform of the list's selection<br/>
     *
     * @param index
     */
    void selectPerformed(int index);

}
