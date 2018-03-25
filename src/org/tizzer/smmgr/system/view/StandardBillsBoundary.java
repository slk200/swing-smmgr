package org.tizzer.smmgr.system.view;

import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.QueryAllGoodsRequestDto;
import org.tizzer.smmgr.system.model.response.QueryAllGoodsResponseDto;

/**
 * @author tizzer
 * @version 1.0
 */
public class StandardBillsBoundary extends WebPanel {

    private static final Object[] tableHead = {"商品名", "售价", "折扣价", "数量"};

    public StandardBillsBoundary() {
        super();
    }

    /**
     * 刷新数据
     *
     * @param keyWord
     * @param currentPage
     * @param pageSize
     */
    private void refreshData(String keyWord, int currentPage, int pageSize) {
        try {
            QueryAllGoodsRequestDto queryAllGoodsRequestDto = new QueryAllGoodsRequestDto();
            queryAllGoodsRequestDto.setCurrentPage(currentPage - 1);
            queryAllGoodsRequestDto.setPageSize(pageSize);
            QueryAllGoodsResponseDto queryAllGoodsResponseDto = HttpHandler.post("/queryallgoods", queryAllGoodsRequestDto.toString(), QueryAllGoodsResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
