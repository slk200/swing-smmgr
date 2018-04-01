package org.tizzer.smmgr.system.model.request;

public class SaveTradeGoodsRequestDto {
    private Object staffNo;
    private Object discount;
    private Object payType;
    private Object cardNo;
    private Object phone;
    private Object cost;
    private Object type;
    private Object serialNo;
    private Object[] upc;
    private Object[] name;
    private Object[] primeCost;
    private Object[] presentCost;
    private Object[] quantity;

    public void setStaffNo(Object staffNo) {
        this.staffNo = staffNo;
    }

    public void setDiscount(Object discount) {
        this.discount = discount;
    }

    public void setCardNo(Object cardNo) {
        this.cardNo = cardNo;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public void setPayType(Object payType) {
        this.payType = payType;
    }

    public void setCost(Object cost) {
        this.cost = cost;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public void setSerialNo(Object serialNo) {
        this.serialNo = serialNo;
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

    public void setPresentCost(Object[] presentCost) {
        this.presentCost = presentCost;
    }

    public void setQuantity(Object[] quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        String param = "staffNo=" + staffNo +
                "&discount=" + discount +
                "&payType=" + payType +
                "&cost=" + cost +
                "&type=" + type;
        for (int i = 0; i < upc.length; i++) {
            param += "&upc=" + upc[i] +
                    "&name=" + name[i] +
                    "&primeCost=" + primeCost[i] +
                    "&presentCost=" + presentCost[i] +
                    "&quantity=" + quantity[i];
        }
        if (cardNo != null) {
            param += "&cardNo=" + cardNo +
                    "&phone=" + phone;
        }
        if (serialNo != null) {
            param += "&serialNo=" + serialNo;
        }
        return param;
    }
}
