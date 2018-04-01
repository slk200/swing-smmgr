package org.tizzer.smmgr.system.model.request;

public class UpdateInsiderTypeRequestDto {
    private Integer id;
    private String name;
    private Integer discount;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "id=" + id +
                "&name=" + name +
                "&discount=" + discount;
    }
}
