package com.alvarenstudio.infosaham.model;

public class EmitenClosingPrice {
    String emiten;
    Long closingPrice;
    Long date;

    public EmitenClosingPrice() {
    }

    public EmitenClosingPrice(String emiten, Long closingPrice, Long date) {
        this.emiten = emiten;
        this.closingPrice = closingPrice;
        this.date = date;
    }

    public String getEmiten() {
        return emiten;
    }

    public void setEmiten(String emiten) {
        this.emiten = emiten;
    }

    public Long getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(Long closingPrice) {
        this.closingPrice = closingPrice;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
