package org.tizzer.smmgr.system.model.response;

public class QueryTradeGoodsResponseDto extends ResultResponse {
    private DataSet[] data;

    public Object[][] getData() {
        if (data != null) {
            Object[][] objects = new Object[data.length][3];
            for (int i = 0; i < data.length; i++) {
                objects[i] = data[i].getData();
            }
            return objects;
        }
        return null;
    }

    public void setData(DataSet[] data) {
        this.data = data;
    }

    static class DataSet {
        private String upc;
        private String name;
        private Double sPrice;

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

        public Double getsPrice() {
            return sPrice;
        }

        public void setsPrice(Double sPrice) {
            this.sPrice = sPrice;
        }

        Object[] getData() {
            return new Object[]{upc, name, sPrice};
        }
    }
}
