package org.tizzer.smmgr.system.model.request;

public class SaveBookRecordRequestDto {
    private Object cost;
    private Object note;
    private Object[] upc;
    private Object[] name;
    private Object[] primeCost;
    private Object[] quantity;

    public void setCost(Object cost) {
        this.cost = cost;
    }

    public void setNote(Object note) {
        this.note = note;
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
        StringBuilder param = new StringBuilder("cost=" + cost);
        if (note != null) {
            param.append("&note=").append(note);
        }
        for (int i = 0; i < upc.length; i++) {
            param.append("&upc=").append(upc[i]).append("&name=").append(name[i]).append("&primeCost=").append(primeCost[i]).append("&quantity=").append(quantity[i]);
        }
        return param.toString();
    }

}
