package org.tizzer.smmgr.system.view;

import com.alee.laf.panel.WebPanel;
import org.tizzer.smmgr.system.model.request.QueryAllGoodsRequestDto;
import org.tizzer.smmgr.system.model.response.QueryAllGoodsResponseDto;
import org.tizzer.smmgr.system.resolver.HttpResolver;

/**
 * @author tizzer
 * @version 1.0
 */
public class SalesRecordBoundary extends WebPanel {

    private static final Object[] tableHead = {"条形码", "名称", "种类", "采购价", "零售价", "库存"};

    public SalesRecordBoundary() {
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
            QueryAllGoodsResponseDto queryAllGoodsResponseDto = HttpResolver.post("/queryallgoods", queryAllGoodsRequestDto.toString(), QueryAllGoodsResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
