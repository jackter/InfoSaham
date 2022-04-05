package com.alvarenstudio.infosaham.model;

public class Chat {
    private String userid;
    private String sender;
    private int is_admin;
    private String msg;
    private String time;
    private Long timestamp;
    private String img_url;

    public Chat() {
    }

    public Chat(String userid, String sender, int is_admin, String msg, String time, Long timestamp, String img_url) {
        this.userid = userid;
        this.sender = sender;
        this.is_admin = is_admin;
        this.msg = msg;
        this.time = time;
        this.timestamp = timestamp;
        this.img_url = img_url;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(int is_admin) {
        this.is_admin = is_admin;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
