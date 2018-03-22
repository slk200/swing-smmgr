package org.tizzer.smmgr.system.model.response;

import com.alee.utils.TimeUtils;

import java.util.Date;

public class QuerySomeInsiderResponseDto {
    private DataSet[] data;
    private Integer pageCount;
    private Integer currentPage;

    public Object[][] getData() {
        if (data != null) {
            Object[][] tBody = new Object[data.length][8];
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

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }


    static class DataSet {
        private String cardNo;
        private String name;
        private String phone;
        private String address;
        private String type;
        private String note;
        private Date birth;
        private Date createAt;

        public String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardNo) {
            this.cardNo = cardNo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public Date getBirth() {
            return birth;
        }

        public void setBirth(Date birth) {
            this.birth = birth;
        }

        public Date getCreateAt() {
            return createAt;
        }

        public void setCreateAt(Date createAt) {
            this.createAt = createAt;
        }

        Object[] getData() {
            return new Object[]{cardNo, name, phone, address, type, note, birth == null ? "" : TimeUtils.formatDate("yyyy-MM-dd", birth), TimeUtils.formatDate("yyyy-MM-dd HH:mm:ss", createAt)};
        }
    }
}
