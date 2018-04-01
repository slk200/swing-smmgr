package org.tizzer.smmgr.system.model.response;

public class QueryPayTypeResponseDto extends ResultResponse {
    private Object[] data;

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }
}
