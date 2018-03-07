package org.tizzer.smmgr.system.model.request;

/**
 * @author tizzer
 * @version 1.0
 */
public class QueryGoodsRequestDto extends PageableRequestDto {

    private String keyword;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "keyword=" + keyword + "&" +
                super.toString();
    }

}
