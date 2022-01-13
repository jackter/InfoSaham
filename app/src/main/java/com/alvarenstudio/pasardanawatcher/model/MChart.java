package com.alvarenstudio.pasardanawatcher.model;

public class MChart {
    private String date;
    private Long price;

    public MChart(Long price, String date) {
        this.price = price;
        this.date = date;
    }

    public MChart() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
