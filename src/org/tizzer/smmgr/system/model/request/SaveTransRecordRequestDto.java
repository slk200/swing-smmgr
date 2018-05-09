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
        StringBuilder param = new StringBuilder("cost=" + cost +
                "&storeId=" + storeId);
        for (int i = 0; i < upc.length; i++) {
            param.append("&upc=").append(upc[i]).append("&name=").append(name[i]).append("&primeCost=").append(primeCost[i]).append("&quantity=").append(quantity[i]);
        }
        return param.toString();
    }

}
