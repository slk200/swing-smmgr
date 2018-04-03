package org.tizzer.smmgr.system.model.response;

public class QueryImportSpecResponseDto extends ResultResponse {
    private DataSet[] data;
    private Double cost;
    private String note;

    public String getQuantity() {
        int sum = 0;
        if (data != null) {
            for (DataSet aData : data) {
                sum += aData.getQuantity();
            }
        }
        return sum == 0 ? "" : sum + "";
    }

    public Object[][] getData() {
        if (data != null) {
            Object[][] tableBody = new Object[data.length][4];
            for (int i = 0; i < data.length; i++) {
                tableBody[i] = data[i].getData();
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

    public String getNote() {
        return note == null ? "" : note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    static class DataSet {
        private String upc;
        private String name;
        private Double primeCost;
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

        public Double getPrimeCost() {
            return primeCost;
        }

        public void setPrimeCost(Double primeCost) {
            this.primeCost = primeCost;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        Object[] getData() {
            return new Object[]{upc, name, primeCost, quantity};
        }
    }
}
