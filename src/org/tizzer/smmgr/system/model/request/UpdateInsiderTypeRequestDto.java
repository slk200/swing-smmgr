package org.tizzer.smmgr.system.model.request;

public class UpdateInsiderTypeRequestDto {
    private Integer id;
    private String name;
    private Integer discount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDiscount() {
        return discount;
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
