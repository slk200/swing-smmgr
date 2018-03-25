package org.tizzer.smmgr.system.model.response;

import com.alee.utils.TimeUtils;

import java.util.Date;

public class QueryOneInsiderResponseDto extends ResultResponse {
    private String cardNo;
    private String name;
    private String phone;
    private String address;
    private String type;
    private String note;
    private Integer discount;
    private Date birth;
    private Date createAt;

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Object[] getData() {
        return new Object[]{cardNo, name, phone, address, type, note, discount, birth == null ? "" : TimeUtils.formatDate("yyyy-MM-dd", birth), TimeUtils.formatDate("yyyy-MM-dd HH:mm:ss", createAt)};
    }
}
