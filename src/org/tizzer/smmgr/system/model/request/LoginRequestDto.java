package org.tizzer.smmgr.system.model.request;

/**
 * @author tizzer
 * @version 1.0
 */
public class LoginRequestDto {

    private String account;
    private String password;

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "account=" + account +
                "&password=" + password;
    }

}
