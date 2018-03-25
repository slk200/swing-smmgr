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

    public Integer[] getId() {
        if (data != null) {
            Integer[] id = new Integer[data.length];
            for (int i = 0; i < data.length; i++) {
                id[i] = data[i].getId();
            }
            return id;
        }
        return null;
    }

    public String[] getName() {
        if (data != null) {
            String[] name = new String[data.length];
            for (int i = 0; i < data.length; i++) {
                name[i] = data[i].getName();
            }
            return name;
        }
        return null;
    }

    public Integer[] getDiscount() {
        if (data != null) {
            Integer[] discount = new Integer[data.length];
            for (int i = 0; i < data.length; i++) {
                discount[i] = data[i].getDiscount();
            }
            return discount;
        }
        return null;
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
