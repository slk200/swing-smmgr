package org.tizzer.smmgr.system.model.response;

public class QueryAllStoreResponseDto extends ResultResponse {
    private DataSet[] data;
    private Integer pageCount;
    private Integer currentPage;
    private Long total;

    public Object[][] getData() {
        if (data != null) {
            Object[][] tBody = new Object[data.length][3];
            for (int i = 0; i < data.length; i++) {
                DataSet dataSet = data[i];
                tBody[i] = dataSet.getData();
            }
            return tBody;
        }
        return null;
    }

    public void setData(DataSet[] data) {
        this.data = data;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    static class DataSet {

        private Long id;
        private String name;
        private String address;

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        Object[] getData() {
            return new Object[]{id, name, address};
        }

    }
}
