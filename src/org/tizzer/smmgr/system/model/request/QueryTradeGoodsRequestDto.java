package org.tizzer.smmgr.system.model.request;

public class QueryTradeGoodsRequestDto {
    private String keyword;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "keyword=" + keyword;
    }
}
