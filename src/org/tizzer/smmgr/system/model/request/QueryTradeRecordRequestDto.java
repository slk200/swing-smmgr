package org.tizzer.smmgr.system.model.request;

public class QueryTradeRecordRequestDto extends PageableRequestDto {
    private String staffNo;
    private String keyword;
    private String startDate;
    private String endDate;

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        String param = "staffNo=" + staffNo +
                "&keyword=" + keyword;
        if (startDate != null) {
            param += "&startDate=" + startDate.replace(" ", "%20");
        }
        if (endDate != null) {
            param += "&endDate=" + endDate.replace(" ", "%20");
        }
        return param + "&" + super.toString();
    }
}
