package org.tizzer.smmgr.system.model.request;

public class QueryOtherStoreRequestDto {
    private Integer storeId;

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "storeId=" + storeId;
    }
}
