package org.tizzer.smmgr.system.model.request;

public class QueryEmployeeRequestDto extends PageableRequestDto {

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
        if (startDate != null) {
            param += "&startDate=" + startDate.replace(" ", "%20");
        }
        if (endDate != null) {
            param += "&endDate=" + endDate.replace(" ", "%20");
        }
        return param + "&" + super.toString();
    }
}
