package org.tizzer.smmgr.system.model.response;

/**
 * @author tizzer
 * @version 1.0
 */
public class LoginResponseDto extends ResultResponse {

    private Boolean admin;

    public Boolean isAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
