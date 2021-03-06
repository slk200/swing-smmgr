package org.tizzer.smmgr.system.model.response;

import com.alee.utils.TimeUtils;

import java.util.Date;

public class QueryStoreResponseDto extends ResultResponse {
    private DataSet[] data;
    private Integer pageCount;

    public Object[][] getData() {
        if (data != null) {
            Object[][] tBody = new Object[data.length][4];
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

    static class DataSet {

        private Integer id;
        private String name;
        private String address;
        private Date foundDate;

        public void setId(Integer id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setFoundDate(Date foundDate) {
            this.foundDate = foundDate;
        }

        Object[] getData() {
            return new Object[]{id, name, address, TimeUtils.formatDate("yyyy-MM-dd HH:mm:ss", foundDate)};
        }

    }
}
