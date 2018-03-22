package org.tizzer.smmgr.system.model.request;

import java.util.Date;

public class SaveInsiderRequestDto {
    private String cardNo;
    private String password;
    private String name;
    private String phone;
    private String address;
    private String note;
    private Integer type;
    private Date birth;

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    @Override
    public String toString() {
        String param = "cardNo=" + cardNo +
                "&password=" + password +
                "&name=" + name +
                "&phone=" + phone +
                "&address=" + address +
                "&note=" + note +
                "&type=" + type;
        if (birth != null) {
            param += "&birth=" + birth;
        }
        return param;
    }
}
