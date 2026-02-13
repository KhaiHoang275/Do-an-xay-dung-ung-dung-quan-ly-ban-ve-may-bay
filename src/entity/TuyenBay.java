<<<<<<< HEAD:src/entity/TuyenBay.java
package entity;

public class TuyenBay {
    private String maTuyenBay;
    private String sanBayDi;
    private String sanBayDen;
    private float khoangCach;
    private double giaGoc;

    public TuyenBay() {
    }

    public TuyenBay(String maTuyenBay, String sanBayDi, String sanBayDen, float khoangCach, double giaGoc) {
        this.maTuyenBay = maTuyenBay;
        this.sanBayDi = sanBayDi;
        this.sanBayDen = sanBayDen;
        this.khoangCach = khoangCach;
        this.giaGoc = giaGoc;
    }

    public TuyenBay(TuyenBay tb) {
        this.maTuyenBay = tb.maTuyenBay;
        this.sanBayDi = tb.sanBayDi;
        this.sanBayDen = tb.sanBayDen;
        this.khoangCach = tb.khoangCach;
        this.giaGoc = tb.giaGoc;
    }

    public String getMaTuyenBay() {
        return maTuyenBay;
    }

    public void setMaTuyenBay(String maTuyenBay) {
        this.maTuyenBay = maTuyenBay;
    }

    public String getSanBayDi() {
        return sanBayDi;
    }

    public void setSanBayDi(String sanBayDi) {
        this.sanBayDi = sanBayDi;
    }

    public String getSanBayDen() {
        return sanBayDen;
    }

    public void setSanBayDen(String sanBayDen) {
        this.sanBayDen = sanBayDen;
    }

    public float getKhoangCach() {
        return khoangCach;
    }

    public void setKhoangCach(float khoangCach) {
        this.khoangCach = khoangCach;
    }

    public double getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(double giaGoc) {
        this.giaGoc = giaGoc;
    } 

    public String toString() {
        return maTuyenBay + " "+ sanBayDi + " " + sanBayDen
                + " " + khoangCach + " " + giaGoc;
    }
=======
package model;

import java.math.BigDecimal;

public class TuyenBay {
    private String maTuyenBay;
    private String sanBayDi;
    private String sanBayDen;
    private float khoangCach;
    private BigDecimal giaGoc;

    public TuyenBay() {
    }

    public TuyenBay(String maTuyenBay, String sanBayDi, String sanBayDen, float khoangCach, BigDecimal giaGoc) {
        this.maTuyenBay = maTuyenBay;
        this.sanBayDi = sanBayDi;
        this.sanBayDen = sanBayDen;
        this.khoangCach = khoangCach;
        this.giaGoc = giaGoc;
    }

    public TuyenBay(TuyenBay tb) {
        this.maTuyenBay = tb.maTuyenBay;
        this.sanBayDi = tb.sanBayDi;
        this.sanBayDen = tb.sanBayDen;
        this.khoangCach = tb.khoangCach;
        this.giaGoc = tb.giaGoc;
    }

    public String getMaTuyenBay() {
        return maTuyenBay;
    }

    public void setMaTuyenBay(String maTuyenBay) {
        this.maTuyenBay = maTuyenBay;
    }

    public String getSanBayDi() {
        return sanBayDi;
    }

    public void setSanBayDi(String sanBayDi) {
        this.sanBayDi = sanBayDi;
    }

    public String getSanBayDen() {
        return sanBayDen;
    }

    public void setSanBayDen(String sanBayDen) {
        this.sanBayDen = sanBayDen;
    }

    public float getKhoangCach() {
        return khoangCach;
    }

    public void setKhoangCach(float khoangCach) {
        this.khoangCach = khoangCach;
    }

    public BigDecimal getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(BigDecimal giaGoc) {
        this.giaGoc = giaGoc;
    } 

    public String toString() {
        return maTuyenBay + " "+ sanBayDi + " " + sanBayDen
                + " " + khoangCach + " " + giaGoc;
    }
>>>>>>> 8a48929e091de8d7322bafb1e06d69aee6fe6ba8:src/model/TuyenBay.java
}