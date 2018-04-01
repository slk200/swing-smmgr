package org.tizzer.smmgr.system.model.request;

public class UpdateInsiderRequestDto {
    private String cardNo;
    private Integer id;
    private String birth;

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setId(Integer id) {
        this.id = id;
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
