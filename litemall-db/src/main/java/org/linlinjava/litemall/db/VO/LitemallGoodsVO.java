package org.linlinjava.litemall.db.VO;

import org.linlinjava.litemall.db.domain.LitemallGoods;

public class LitemallGoodsVO extends LitemallGoods {
    private short number;

    private String specifications;

    public short getNumber() {
        return number;
    }

    public void setNumber(short number) {
        this.number = number;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }
}
