package org.tizzer.smmgr.system.model.response;

import com.alee.utils.TimeUtils;

import java.util.Date;

public class QueryImportRecordResponseDto extends ResultResponse {
    private DataSet[] data;
    private Integer pageCount;

    public Object[][] getData() {
        if (data != null) {
            Object[][] recordList = new Object[data.length][2];
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
        private Long id;
        private Date createAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Date getCreateAt() {
            return createAt;
        }

        public void setCreateAt(Date createAt) {
            this.createAt = createAt;
        }

        Object[] getData() {
            return new Object[]{id, TimeUtils.formatDate("yyyy-MM-dd HH:mm:ss", createAt)};
        }
    }
}
