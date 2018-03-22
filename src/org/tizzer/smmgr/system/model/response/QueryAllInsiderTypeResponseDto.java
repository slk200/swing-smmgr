package org.tizzer.smmgr.system.model.response;

public class QueryAllInsiderTypeResponseDto extends ResultResponse {
    private DataSet[] data;

    public Object[][] getData() {
        if (data != null) {
            Object[][] objects = new Object[data.length][4];
            for (int i = 0; i < data.length; i++) {
                DataSet dataSet = data[i];
                objects[i] = dataSet.getData();
            }
            return objects;
        }
        return null;
    }

    public void setData(DataSet[] data) {
        this.data = data;
    }

    static class DataSet {
        private Integer id;
        private String name;
        private Integer discount;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getDiscount() {
            return discount;
        }

        public void setDiscount(Integer discount) {
            this.discount = discount;
        }

        Object[] getData() {
            return new Object[]{id, name, discount};
        }
    }
}
