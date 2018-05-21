package org.tizzer.smmgr.system.model.request;

public class QueryInsiderRequestDto extends PageableRequestDto {

    private String startDate;
    private String endDate;
    private String keyword;

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        String param = "keyword=" + keyword;
        if (startDate != null && !startDate.equals("")) {
            param += "&startDate=" + startDate.replace(" ", "%20");
        }
        if (endDate != null && !endDate.equals("")) {
            param += "&endDate=" + endDate.replace(" ", "%20");
        }
        return param + "&" + super.toString();
    }
}
