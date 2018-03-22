package org.tizzer.smmgr.system.model.request;

public class UpdateEmployeeRequestDto {
    private String staffNo;
    private String phone;
    private String address;
    private Boolean admin;
    private Boolean enable;

    public String getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
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

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "staffNo=" + staffNo +
                "&phone=" + phone +
                "&address=" + address +
                "&admin=" + admin +
                "&enable=" + enable;
    }
}
