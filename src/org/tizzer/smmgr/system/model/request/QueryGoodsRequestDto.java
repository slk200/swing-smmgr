package org.tizzer.smmgr.system.model.request;

public class QueryGoodsRequestDto extends PageableRequestDto {
    private Integer typeId;
    private String keyword;

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "typeId=" + typeId +
                "&keyword=" + keyword +
                "&" + super.toString();
    }
}
