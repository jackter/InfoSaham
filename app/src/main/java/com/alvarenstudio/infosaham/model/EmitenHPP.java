package com.alvarenstudio.infosaham.model;

public class EmitenHPP {
    String emiten;
    Long openingStock;
    Long closingStock;
    Long openingPrice;
    Long closingPrice;

    public EmitenHPP() {
    }

    public EmitenHPP(String emiten, Long openingStock, Long closingStock, Long openingPrice, Long closingPrice) {
        this.emiten = emiten;
        this.openingStock = openingStock;
        this.closingStock = closingStock;
        this.openingPrice = openingPrice;
        this.closingPrice = closingPrice;
    }

    public String getEmiten() {
        return emiten;
    }

    public void setEmiten(String emiten) {
        this.emiten = emiten;
    }

    public Long getOpeningStock() {
        return openingStock;
    }

    public void setOpeningStock(Long openingStock) {
        this.openingStock = openingStock;
    }

    public Long getClosingStock() {
        return closingStock;
    }

    public void setClosingStock(Long closingStock) {
        this.closingStock = closingStock;
    }

    public Long getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(Long openingPrice) {
        this.openingPrice = openingPrice;
    }

    public Long getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(Long closingPrice) {
        this.closingPrice = closingPrice;
    }
}
