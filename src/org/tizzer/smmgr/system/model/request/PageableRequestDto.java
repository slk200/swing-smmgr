package org.tizzer.smmgr.system.model.request;

/**
 * @author tizzer
 * @version 1.0
 */
public class PageableRequestDto {

    private Integer pageSize;
    private Integer currentPage;

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public String toString() {
        return "pageSize=" + pageSize +
                "&currentPage=" + currentPage;
    }

}
