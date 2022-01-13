package com.alvarenstudio.pasardanawatcher.model;

public class LVDividend {
    private String desc;
    private String dividend;
    private String year;
    private String recorddate;
    private String distributedate;

    public LVDividend() {
    }

    public LVDividend(String desc, String dividend, String year, String recorddate, String distributedate) {
        this.desc = desc;
        this.dividend = dividend;
        this.year = year;
        this.recorddate = recorddate;
        this.distributedate = distributedate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDividend() {
        return dividend;
    }

    public void setDividend(String dividend) {
        this.dividend = dividend;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRecorddate() {
        return recorddate;
    }

    public void setRecorddate(String recorddate) {
        this.recorddate = recorddate;
    }

    public String getDistributedate() {
        return distributedate;
    }

    public void setDistributedate(String distributedate) {
        this.distributedate = distributedate;
    }
}
