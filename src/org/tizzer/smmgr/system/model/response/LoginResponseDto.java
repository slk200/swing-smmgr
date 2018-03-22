package org.tizzer.smmgr.system.model.response;

/**
 * @author tizzer
 * @version 1.0
 */
public class LoginResponseDto extends ResultResponse {

    private Long storeId;
    private Boolean admin;

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
