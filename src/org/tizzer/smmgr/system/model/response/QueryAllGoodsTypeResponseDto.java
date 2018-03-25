package org.tizzer.smmgr.system.model.response;

public class QueryAllGoodsTypeResponseDto extends ResultResponse {
    private DataSet[] data;

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

    static class DataSet {
        Integer id;
        String name;

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
    }
}
