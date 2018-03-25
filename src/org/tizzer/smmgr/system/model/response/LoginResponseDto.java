package org.tizzer.smmgr.system.model.response;

/**
 * @author tizzer
 * @version 1.0
 */
public class LoginResponseDto extends ResultResponse {

    private Integer storeId;
    private Boolean admin;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
