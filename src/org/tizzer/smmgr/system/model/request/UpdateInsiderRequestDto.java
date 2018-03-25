package org.tizzer.smmgr.system.model.request;

public class UpdateInsiderRequestDto {
    private String cardNo;
    private Integer id;
    private String birth;

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    @Override
    public String toString() {
        String param = "cardNo=" + cardNo + "&id=" + id;
        if (birth != null) {
            param += "&birth=" + birth;
        }
        return param;
    }
}
