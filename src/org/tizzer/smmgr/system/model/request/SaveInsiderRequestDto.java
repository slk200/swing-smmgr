package org.tizzer.smmgr.system.model.request;

public class SaveInsiderRequestDto {
    private String cardNo;
    private String name;
    private String phone;
    private String address;
    private String note;
    private Integer type;
    private String birth;

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    @Override
    public String toString() {
        String param = "cardNo=" + cardNo +
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
