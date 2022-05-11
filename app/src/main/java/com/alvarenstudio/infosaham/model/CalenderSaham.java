package com.alvarenstudio.infosaham.model;

public class CalenderSaham {
    String emiten;
    String date;
    String desc;
    String location;

    public CalenderSaham() {
    }

    public CalenderSaham(String emiten, String date, String desc, String location) {
        this.emiten = emiten;
        this.date = date;
        this.desc = desc;
        this.location = location;
    }

    public String getEmiten() {
        return emiten;
    }

    public void setEmiten(String emiten) {
        this.emiten = emiten;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
