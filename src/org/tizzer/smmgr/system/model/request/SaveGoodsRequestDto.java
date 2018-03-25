package org.tizzer.smmgr.system.model.request;

import java.util.Date;

public class SaveGoodsRequestDto {
    private String upc;
    private String name;
    private String spell;
    private Double jPrice;
    private Double sPrice;
    private Integer invention;
    private Date scDate;
    private Integer bzDate;
    private boolean needSave;
    private String type;
    private Integer id;

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

    public Integer getInvention() {
        return invention;
    }

    public void setInvention(Integer invention) {
        this.invention = invention;
    }

    public Date getScDate() {
        return scDate;
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

    public boolean getNeedSave() {
        return needSave;
    }

    public void setNeedSave(boolean needSave) {
        this.needSave = needSave;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "upc=" + upc +
                "&name=" + name +
                "&spell=" + spell +
                "&jPrice=" + jPrice +
                "&sPrice=" + sPrice +
                "&invention=" + invention +
                "&scDate=" + scDate +
                "&bzDate=" + bzDate +
                "&needSave=" + needSave +
                "&type=" + type +
                "&id=" + id;
    }
}
