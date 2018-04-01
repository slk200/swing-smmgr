package org.tizzer.smmgr.system.model.response;

public class QueryRefundRecordResponseDto extends ResultResponse {
    private DataSet[] data;
    private Double cost;
    private String cardNo;
    private String phone;
    private String payType;
    private Integer discount;

    public Object[] getCache() {
        return new Object[]{cardNo, phone, payType, discount};
    }

    public Object[][] getData() {
        if (data != null) {
            Object[][] tableBody = new Object[data.length][5];
            for (int i = 0; i < data.length; i++) {
                DataSet dataSet = data[i];
                tableBody[i] = new Object[]{dataSet.upc, dataSet.name, dataSet.primeCost, discount, dataSet.presentCost, dataSet.quantity, (double) Math.round(dataSet.presentCost * dataSet.quantity * 100) / 100};
            }
            return tableBody;
        }
        return null;
    }

    public void setData(DataSet[] data) {
        this.data = data;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getCardNo() {
        return cardNo == null ? "" : cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getPhone() {
        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    static class DataSet {
        private String upc;
        private String name;
        private double primeCost;
        private double presentCost;
        private Integer quantity;

        public String getUpc() {
            return upc;
        }

        public void setUpc(String upc) {
            this.upc = upc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrimeCost() {
            return primeCost;
        }

        public void setPrimeCost(double primeCost) {
            this.primeCost = primeCost;
        }

        public double getPresentCost() {
            return presentCost;
        }

        public void setPresentCost(double presentCost) {
            this.presentCost = presentCost;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

    }
}
