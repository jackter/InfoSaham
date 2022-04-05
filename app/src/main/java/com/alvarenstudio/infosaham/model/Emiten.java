package com.alvarenstudio.infosaham.model;

public class Emiten {
    String emiten;
    Long qty;
    Long price;
    Long lastPrice;

    public Emiten() {
    }

    public Emiten(String emiten, Long qty, Long price, Long lastPrice) {
        this.emiten = emiten;
        this.qty = qty;
        this.price = price;
        this.lastPrice = lastPrice;
    }

    public String getEmiten() {
        return emiten;
    }

    public void setEmiten(String emiten) {
        this.emiten = emiten;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Long lastPprice) {
        this.lastPrice = lastPprice;
    }
}
