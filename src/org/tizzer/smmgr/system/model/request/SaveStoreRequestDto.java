package org.tizzer.smmgr.system.model.request;

public class SaveStoreRequestDto {
    private String name;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "name=" + name +
                "&address=" + address;
    }
}
