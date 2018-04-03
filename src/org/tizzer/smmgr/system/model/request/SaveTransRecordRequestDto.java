package org.tizzer.smmgr.system.model.request;

public class SaveTransRecordRequestDto {
    private Object cost;
    private Object storeId;
    private Object[] upc;
    private Object[] name;
    private Object[] primeCost;
    private Object[] quantity;

    public void setCost(Object cost) {
        this.cost = cost;
    }

    public void setStoreId(Object storeId) {
        this.storeId = storeId;
    }

    public void setUpc(Object[] upc) {
        this.upc = upc;
    }

    public void setName(Object[] name) {
        this.name = name;
    }

    public void setPrimeCost(Object[] primeCost) {
        this.primeCost = primeCost;
    }

    public void setQuantity(Object[] quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        String param = "cost=" + cost +
                "&storeId=" + storeId;
        for (int i = 0; i < upc.length; i++) {
            param += "&upc=" + upc[i] +
                    "&name=" + name[i] +
                    "&primeCost=" + primeCost[i] +
                    "&quantity=" + quantity[i];
        }
        return param;
    }

}
