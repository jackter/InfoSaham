package com.alvarenstudio.pasardanawatcher.model;

public class MainCardReksadana {
    private String name;
    private String category;
    private String type;
    private int cur;//0 : IDR, 1 : USD
    private double nav;
    private double aum;
    private double oneday;
    private double mtd;
    private double onemonth;
    private double ytd;
    private double oneyear;

    public MainCardReksadana() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCur() {
        return cur;
    }

    public void setCur(int cur) {
        this.cur = cur;
    }

    public double getNav() {
        return nav;
    }

    public void setNav(double nav) {
        this.nav = nav;
    }

    public double getAum() {
        return aum;
    }

    public void setAum(double aum) {
        this.aum = aum;
    }

    public double getOneday() {
        return oneday;
    }

    public void setOneday(double oneday) {
        this.oneday = oneday;
    }

    public double getMtd() {
        return mtd;
    }

    public void setMtd(double mtd) {
        this.mtd = mtd;
    }

    public double getOnemonth() {
        return onemonth;
    }

    public void setOnemonth(double onemonth) {
        this.onemonth = onemonth;
    }

    public double getYtd() {
        return ytd;
    }

    public void setYtd(double ytd) {
        this.ytd = ytd;
    }

    public double getOneyear() {
        return oneyear;
    }

    public void setOneyear(double oneyear) {
        this.oneyear = oneyear;
    }
}
