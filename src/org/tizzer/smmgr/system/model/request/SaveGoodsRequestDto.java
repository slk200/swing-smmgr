package org.tizzer.smmgr.system.model.request;

public class SaveGoodsRequestDto {
    private String upc;
    private String name;
    private String spell;
    private Double jPrice;
    private Double sPrice;
    private Integer invention;
    private String scDate;
    private Integer bzDate;
    private String type;
    private Integer id;

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public void setjPrice(Double jPrice) {
        this.jPrice = jPrice;
    }

    public void setsPrice(Double sPrice) {
        this.sPrice = sPrice;
    }

    public void setInvention(Integer invention) {
        this.invention = invention;
    }

    public void setScDate(String scDate) {
        this.scDate = scDate + "%2000:00:00";
    }

    public void setBzDate(Integer bzDate) {
        this.bzDate = bzDate;
    }

    public void setType(String type) {
        this.type = type;
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
                "&type=" + type +
                "&id=" + id;
    }
}
