package org.tizzer.smmgr.system.model.response;

import com.alee.utils.TimeUtils;

import java.util.Date;

public class QueryTradeRecordResponseDto extends ResultResponse {
    private DataSet[] data;
    private Integer pageCount;

    public Object[][] getData() {
        if (data != null) {
            Object[][] recordList = new Object[data.length][4];
            for (int i = 0; i < data.length; i++) {
                recordList[i] = data[i].getData();
            }
            return recordList;
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
        private String serialNo;
        private String markNo;
        private boolean type;
        private Date soldTime;

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public String getMarkNo() {
            return markNo;
        }

        public void setMarkNo(String markNo) {
            this.markNo = markNo;
        }

        public boolean isType() {
            return type;
        }

        public void setType(boolean type) {
            this.type = type;
        }

        public Date getSoldTime() {
            return soldTime;
        }

        public void setSoldTime(Date soldTime) {
            this.soldTime = soldTime;
        }

        Object[] getData() {
            return new Object[]{serialNo, markNo, type, TimeUtils.formatDate("MM-dd HH:mm", soldTime)};
        }
    }
}
