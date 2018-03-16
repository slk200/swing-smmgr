package org.tizzer.smmgr.system.model.request;

/**
 * @author tizzer
 * @version 1.0
 */
public class LoginRequestDto {

    private String staffNo;
    private String password;

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "staffNo=" + staffNo +
                "&password=" + password;
    }

}
