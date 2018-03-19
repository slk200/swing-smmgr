package org.tizzer.smmgr.system.model.response;

/**
 * @author tizzer
 * @version 1.0
 */
public class QueryAllGoodsResponseDto extends ResultResponse {

    private DataSet[] data;
    private Integer pageCount;
    private Integer currentPage;
    private Long total;

    public Object[][] getData() {
        if (data != null) {
            Object[][] tBody = new Object[data.length][6];
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
        private String upc;
        private String name;
        private String type;
        private Double oPrice;
        private Double sPrice;
        private Long inventory;

        public void setUpc(String upc) {
            this.upc = upc;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setoPrice(Double oPrice) {
            this.oPrice = oPrice;
        }

        public void setsPrice(Double sPrice) {
            this.sPrice = sPrice;
        }

        public void setInventory(Long inventory) {
            this.inventory = inventory;
        }

        Object[] getData() {
            return new Object[]{upc, name, type, oPrice, sPrice, inventory};
        }
    }

}
