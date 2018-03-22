package org.tizzer.smmgr.system.model.request;

import java.util.Date;

public class QuerySomeInsiderRequestDto extends PageableRequestDto {

    private Date startDate;
    private Date endDate;
    private String keyWord;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public String toString() {
        String param = "";
        if (startDate != null) {
            param += "startDate=" + startDate + "&";
        }
        if (endDate != null) {
            param += "endDate=" + endDate + "&";
        }
        return param + "keyWord=" + keyWord + "&" + super.toString();
    }
}
