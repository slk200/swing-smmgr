package org.tizzer.smmgr.system.model.request;

public class QueryLossRecordRequestDto extends PageableRequestDto {
    private String keyword;
    private String startDate;
    private String endDate;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        String param = "&keyword=" + keyword;
        if (startDate != null) {
            param += "&startDate=" + startDate.replace(" ", "%20");
        }
        if (endDate != null) {
            param += "&endDate=" + endDate.replace(" ", "%20");
        }
        return param + "&" + super.toString();
    }
}
