package com.alvarenstudio.infosaham.model;

public class CatatanSaham {
    String id;
    String userid;
    String type;
    String emiten;
    Long jmlSaham;
    Long hargaSaham;
    Long nilaiSaham;
    Long feeTrx;
    Long tglTrx;

    public CatatanSaham() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmiten() {
        return emiten;
    }

    public void setEmiten(String emiten) {
        this.emiten = emiten;
    }

    public Long getJmlSaham() {
        return jmlSaham;
    }

    public void setJmlSaham(Long jmlSaham) {
        this.jmlSaham = jmlSaham;
    }

    public Long getHargaSaham() {
        return hargaSaham;
    }

    public void setHargaSaham(Long hargaSaham) {
        this.hargaSaham = hargaSaham;
    }

    public Long getNilaiSaham() {
        return nilaiSaham;
    }

    public void setNilaiSaham(Long nilaiSaham) {
        this.nilaiSaham = nilaiSaham;
    }

    public Long getFeeTrx() {
        return feeTrx;
    }

    public void setFeeTrx(Long feeTrx) {
        this.feeTrx = feeTrx;
    }

    public Long getTglTrx() {
        return tglTrx;
    }

    public void setTglTrx(Long tglTrx) {
        this.tglTrx = tglTrx;
    }
}
