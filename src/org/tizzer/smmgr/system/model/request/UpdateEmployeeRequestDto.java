package org.tizzer.smmgr.system.model.request;

public class UpdateEmployeeRequestDto {
    private String staffNo;
    private String phone;
    private String address;
    private Boolean admin;
    private Boolean enable;

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
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
