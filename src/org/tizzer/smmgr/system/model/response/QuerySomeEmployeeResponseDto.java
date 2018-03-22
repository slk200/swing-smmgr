package org.tizzer.smmgr.system.model.response;

import com.alee.utils.TimeUtils;

import java.util.Date;

public class QuerySomeEmployeeResponseDto extends ResultResponse {
    private QueryAllEmployeeResponseDto.DataSet[] data;
    private Integer pageCount;
    private Integer currentPage;

    public Object[][] getData() {
        if (data != null) {
            Object[][] tBody = new Object[data.length][8];
            for (int i = 0; i < data.length; i++) {
                QueryAllEmployeeResponseDto.DataSet dataSet = data[i];
                tBody[i] = dataSet.getData();
            }
            return tBody;
        }
        return null;
    }

    public void setData(QueryAllEmployeeResponseDto.DataSet[] data) {
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
        private String staffNo;
        private String name;
        private String phone;
        private String address;
        private Date createAt;
        private String store;
        private Boolean isAdmin;
        private Boolean state;

        public String getStaffNo() {
            return staffNo;
        }

        public void setStaffNo(String staffNo) {
            this.staffNo = staffNo;
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

        public Date getCreateAt() {
            return createAt;
        }

        public void setCreateAt(Date createAt) {
            this.createAt = createAt;
        }

        public String getStore() {
            return store;
        }

        public void setStore(String store) {
            this.store = store;
        }

        public Boolean getAdmin() {
            return isAdmin;
        }

        public void setAdmin(Boolean admin) {
            isAdmin = admin;
        }

        public Boolean getState() {
            return state;
        }

        public void setState(Boolean state) {
            this.state = state;
        }

        Object[] getData() {
            return new Object[]{staffNo, name, phone, address, store, TimeUtils.formatDate("yyyy-MM-dd HH:mm:ss", createAt), isAdmin, state};
        }
    }
}
