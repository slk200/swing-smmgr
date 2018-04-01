package org.tizzer.smmgr.system.model.request;

public class SaveEmployeeRequestDto {
    private String staffNo;
    private String password;
    private String name;
    private String phone;
    private String address;
    private Boolean admin;
    private Integer storeId;

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "staffNo=" + staffNo +
                "&password=" + password +
                "&name=" + name +
                "&phone=" + phone +
                "&address=" + address +
                "&admin=" + admin +
                "&storeId=" + storeId;
    }
}
