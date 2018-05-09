package org.tizzer.smmgr.system.model.request;

public class SaveTradeRecordRequestDto {
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
        StringBuilder param = new StringBuilder("staffNo=" + staffNo +
                "&discount=" + discount +
                "&payType=" + payType +
                "&cost=" + cost +
                "&type=" + type);
        for (int i = 0; i < upc.length; i++) {
            param.append("&upc=").append(upc[i]).append("&name=").append(name[i]).append("&primeCost=").append(primeCost[i]).append("&presentCost=").append(presentCost[i]).append("&quantity=").append(quantity[i]);
        }
        if (cardNo != null) {
            param.append("&cardNo=").append(cardNo).append("&phone=").append(phone);
        }
        if (serialNo != null) {
            param.append("&serialNo=").append(serialNo);
        }
        return param.toString();
    }
}
