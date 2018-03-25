package org.tizzer.smmgr.system.model.request;

import java.util.Vector;

public class DeleteInsiderTypeRequestDto {
    private Vector<Integer> id;

    public Vector<Integer> getId() {
        return id;
    }

    public void setId(Vector<Integer> id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String param = "";
        for (int i = 0; i < id.size() - 1; i++) {
            param += "id=" + id.get(i) + "&";
        }
        param += "id=" + id.get(id.size() - 1);
        return param;
    }
}
