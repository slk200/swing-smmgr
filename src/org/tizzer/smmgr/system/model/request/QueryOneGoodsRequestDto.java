package org.tizzer.smmgr.system.model.request;

public class QueryOneGoodsRequestDto {
    private Object upc;

    public void setUpc(Object upc) {
        this.upc = upc;
    }

    @Override
    public String toString() {
        return "upc=" + upc;
    }
}
