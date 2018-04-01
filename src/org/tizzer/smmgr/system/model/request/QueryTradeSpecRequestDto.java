package org.tizzer.smmgr.system.model.request;

public class QueryTradeSpecRequestDto {
    private Object serialNo;

    public void setSerialNo(Object serialNo) {
        this.serialNo = serialNo;
    }

    @Override
    public String toString() {
        return "serialNo=" + serialNo;
    }
}
