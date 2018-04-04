package org.tizzer.smmgr.system.model.analysis;

import org.tizzer.smmgr.system.model.response.ResultResponse;

public class AnalysisResponseDto extends ResultResponse {
    private Double consumerCost;
    private Double insiderCost;

    public Double getConsumerCost() {
        return consumerCost;
    }

    public void setConsumerCost(Double consumerCost) {
        this.consumerCost = consumerCost;
    }

    public Double getInsiderCost() {
        return insiderCost;
    }

    public void setInsiderCost(Double insiderCost) {
        this.insiderCost = insiderCost;
    }
}
