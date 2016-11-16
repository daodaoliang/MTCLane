package com.hgits.vo;

/**
 * 公务卡信息
 *
 * @author Wang Guodong
 */
public class ServiceCard {

    private String id;//公务卡编号（6位）
    private String cardSerial;//公务卡序列号

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardSerial() {
        return cardSerial;
    }

    public void setCardSerial(String cardSerial) {
        this.cardSerial = cardSerial;
    }

    @Override
    public String toString() {
        return "ServiceCard{" + "id=" + id + ", cardSerial=" + cardSerial + '}';
    }
    
    
}
