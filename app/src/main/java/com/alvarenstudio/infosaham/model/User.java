package com.alvarenstudio.infosaham.model;

public class User {
    private String id;
    private String name;
    private String phone;
    private String telegram;
    private String province;
    private String address;
    private String imgUrl;
    private String is_admin;

    public User() {
    }

    public User(String id, String name, String phone, String telegram, String province, String address, String imgUrl, String is_admin) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.telegram = telegram;
        this.province = province;
        this.address = address;
        this.imgUrl = imgUrl;
        this.is_admin = is_admin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }
}
