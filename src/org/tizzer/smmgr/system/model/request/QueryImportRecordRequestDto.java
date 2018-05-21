package org.tizzer.smmgr.system.model.request;

public class QueryImportRecordRequestDto extends PageableRequestDto {
    private String startDate;
    private String endDate;

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        String param = "";
        if (startDate != null && !startDate.equals("")) {
            param += "startDate=" + startDate.replace(" ", "%20") + "&";
        }
        if (endDate != null && !endDate.equals("")) {
            param += "endDate=" + endDate.replace(" ", "%20") + "&";
        }
        return param + super.toString();
    }
}
