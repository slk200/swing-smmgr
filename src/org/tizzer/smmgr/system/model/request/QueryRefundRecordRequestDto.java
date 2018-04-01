package org.tizzer.smmgr.system.model.request;

public class QueryRefundRecordRequestDto {
    private String serialNo;

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    @Override
    public String toString() {
        return "serialNo=" + serialNo;
    }
}
