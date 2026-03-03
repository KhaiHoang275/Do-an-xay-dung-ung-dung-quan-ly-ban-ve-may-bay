package model;

import java.math.BigDecimal;

public class TuyenBay {
    private String maTuyenBay;
    private String sanBayDi;
    private String sanBayDen;
    private float khoangCachKM;
    private BigDecimal giaGoc;
    private TrangThaiTuyenBay trangThai;

    public TuyenBay() {
    }

    public TuyenBay(String maTuyenBay, String sanBayDi, String sanBayDen, float khoangCachKM, BigDecimal giaGoc, TrangThaiTuyenBay trangThai) {
        this.maTuyenBay = maTuyenBay;
        this.sanBayDi = sanBayDi;
        this.sanBayDen = sanBayDen;
        this.khoangCachKM = khoangCachKM;
        this.giaGoc = giaGoc;
        this.trangThai = trangThai; 
    }

    public TuyenBay(TuyenBay tb) {
        this.maTuyenBay = tb.maTuyenBay;
        this.sanBayDi = tb.sanBayDi;
        this.sanBayDen = tb.sanBayDen;
        this.khoangCachKM = tb.khoangCachKM;
        this.giaGoc = tb.giaGoc;
        this.trangThai = tb.trangThai;
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

    public float getKhoangCachKM() {
        return khoangCachKM;
    }

    public void setKhoangCachKM(float khoangCachKM) {
        this.khoangCachKM = khoangCachKM;
    }

    public BigDecimal getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(BigDecimal giaGoc) {
        this.giaGoc = giaGoc;
    } 

    public TrangThaiTuyenBay getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiTuyenBay trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return maTuyenBay + " "+ sanBayDi + " " + sanBayDen
                + " " + khoangCachKM + " " + giaGoc + " " + trangThai;
    }
}