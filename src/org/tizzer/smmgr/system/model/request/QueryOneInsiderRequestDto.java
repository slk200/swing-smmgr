package org.tizzer.smmgr.system.model.request;

public class QueryOneInsiderRequestDto {
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "keyword=" + keyword;
    }
}
