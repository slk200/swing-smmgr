package org.tizzer.smmgr.system.model.request;

public class QueryOneStoreRequestDto {
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "id=" + id;
    }
}
