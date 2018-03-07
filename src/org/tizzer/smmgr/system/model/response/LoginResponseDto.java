package org.tizzer.smmgr.system.model.response;

/**
 * @author tizzer
 * @version 1.0
 */
public class LoginResponseDto {

    private int code;
    private String message;
    private boolean admin;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}
