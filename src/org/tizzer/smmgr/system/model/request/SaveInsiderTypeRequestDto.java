package org.tizzer.smmgr.system.model.request;

public class SaveInsiderTypeRequestDto {
    private String name;
    private Integer discount;

    public void setName(String name) {
        this.name = name;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "name=" + name +
                "&discount=" + discount;
    }
}
