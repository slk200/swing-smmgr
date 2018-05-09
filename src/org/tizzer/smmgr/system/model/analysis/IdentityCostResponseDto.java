package org.tizzer.smmgr.system.model.analysis;

import org.tizzer.smmgr.system.model.response.ResultResponse;

import java.util.HashMap;
import java.util.Map;

public class IdentityCostResponseDto extends ResultResponse {
    private Double consumerCost;
    private Double insiderCost;

    public void setConsumerCost(Double consumerCost) {
        this.consumerCost = consumerCost;
    }

    public void setInsiderCost(Double insiderCost) {
        this.insiderCost = insiderCost;
    }

    public Map<String, Double> getData() {
        Map<String, Double> map = new HashMap<>();
        map.put("标准消费", consumerCost == null ? 0 : consumerCost);
        map.put("会员消费", insiderCost == null ? 0 : insiderCost);
        return map;
    }
}
