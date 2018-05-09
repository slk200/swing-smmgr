package org.tizzer.smmgr.system.model.request;

import java.util.Vector;

public class DeleteStoreRequestDto {
    private Vector<Integer> id;

    public void setId(Vector<Integer> id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder param = new StringBuilder();
        for (int i = 0; i < id.size() - 1; i++) {
            param.append("id=").append(id.get(i)).append("&");
        }
        param.append("id=").append(id.get(id.size() - 1));
        return param.toString();
    }
}
