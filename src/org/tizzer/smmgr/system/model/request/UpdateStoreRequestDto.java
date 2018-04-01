package org.tizzer.smmgr.system.model.request;

public class UpdateStoreRequestDto {
    private Long id;
    private String name;
    private String address;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "id=" + id +
                "&name=" + name +
                "&address=" + address;
    }
}
