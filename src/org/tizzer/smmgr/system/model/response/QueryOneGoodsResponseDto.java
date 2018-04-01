package org.tizzer.smmgr.system.model.response;

import com.alee.utils.TimeUtils;

import java.util.Date;

public class QueryOneGoodsResponseDto extends ResultResponse {
    private String upc;
    private String name;
    private String spell;
    private Double jPrice;
    private Double sPrice;
    private Integer inventory;
    private Date scDate;
    private Integer bzDate;
    private String Type;

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public Double getjPrice() {
        return jPrice;
    }

    public void setjPrice(Double jPrice) {
        this.jPrice = jPrice;
    }

    public Double getsPrice() {
        return sPrice;
    }

    public void setsPrice(Double sPrice) {
        this.sPrice = sPrice;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public String getScDate() {
        return TimeUtils.formatDate("yyyy-MM-dd", scDate);
    }

    public void setScDate(Date scDate) {
        this.scDate = scDate;
    }

    public Integer getBzDate() {
        return bzDate;
    }

    public void setBzDate(Integer bzDate) {
        this.bzDate = bzDate;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
