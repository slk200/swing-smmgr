package org.tizzer.smmgr.system.model.response;

import com.alee.utils.TimeUtils;

import java.util.Date;

public class QueryLossRecordResponseDto extends ResultResponse {

    private DataSet[] data;
    private Integer pageCount;

    public Object[][] getData() {
        if (data != null) {
            Object[][] recordList = new Object[data.length][3];
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
        private String id;
        private String markNo;
        private Date createAt;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMarkNo() {
            return markNo;
        }

        public void setMarkNo(String markNo) {
            this.markNo = markNo;
        }

        public Date getCreateAt() {
            return createAt;
        }

        public void setCreateAt(Date createAt) {
            this.createAt = createAt;
        }

        Object[] getData() {
            return new Object[]{id, markNo, TimeUtils.formatDate("MM-dd HH:mm", createAt)};
        }
    }
}
