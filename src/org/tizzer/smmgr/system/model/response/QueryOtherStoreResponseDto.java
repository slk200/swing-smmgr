package org.tizzer.smmgr.system.model.response;

public class QueryOtherStoreResponseDto extends ResultResponse {
    private DataSet[] data;

    public Object[] getName() {
        if (data != null) {
            Object[] names = new Object[data.length];
            for (int i = 0; i < data.length; i++) {
                names[i] = data[i].getName();
            }
            return names;
        }
        return null;
    }

    public Integer[] getId() {
        if (data != null) {
            Integer[] ids = new Integer[data.length];
            for (int i = 0; i < data.length; i++) {
                ids[i] = data[i].getId();
            }
            return ids;
        }
        return null;
    }

    public void setData(DataSet[] data) {
        this.data = data;
    }

    static class DataSet {
        private Integer id;
        private String name;

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
