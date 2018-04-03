package org.tizzer.smmgr.system.model.request;

public class QueryLossSpecRequestDto {
    private Object id;

    public void setId(Object id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "id=" + id;
    }
}
