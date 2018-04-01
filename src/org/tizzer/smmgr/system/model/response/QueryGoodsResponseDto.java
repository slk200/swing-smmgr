package org.tizzer.smmgr.system.model.response;

public class QueryGoodsResponseDto extends ResultResponse {

    private DataSet[] data;
    private Integer pageCount;

    public Object[][] getData() {
        if (data != null) {
            Object[][] tableBody = new Object[data.length][5];
            for (int i = 0; i < data.length; i++) {
                tableBody[i] = data[i].getData();
            }
            return tableBody;
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

    static class DataSet {
        private String upc;
        private String name;
        private Double jPrice;
        private Double sPrice;
        private Integer inventory;

        public String getUpc() {
            return upc;
        }

        public void setUpc(String upc) {
            this.upc = upc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getjPrice() {
            return jPrice;
        }

        public void setjPrice(Double jPrice) {
            this.jPrice = jPrice;
        }

        public Double getsPrice() {
            return sPrice;
        }

        public void setsPrice(Double sPrice) {
            this.sPrice = sPrice;
        }

        public Integer getInventory() {
            return inventory;
        }

        public void setInventory(Integer inventory) {
            this.inventory = inventory;
        }

        Object[] getData() {
            return new Object[]{upc, name, jPrice, sPrice, inventory};
        }
    }
}
