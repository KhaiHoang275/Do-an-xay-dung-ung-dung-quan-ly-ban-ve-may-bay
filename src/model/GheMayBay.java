package model;

import java.math.BigDecimal;

public class GheMayBay {
    private String maGhe;
    private String maMayBay;
    private String soGhe;
    private BigDecimal giaGhe;
    private TrangThaiGhe trangThai;

    public GheMayBay() {
    }

    public GheMayBay(String maGhe, String maMayBay, String soGhe, BigDecimal giaGhe, TrangThaiGhe trangThai) {
        this.maGhe = maGhe;
        this.maMayBay = maMayBay;
        this.soGhe = soGhe;
        this.giaGhe = giaGhe;
        this.trangThai = trangThai;
    }

    public GheMayBay(GheMayBay other) {
        this.maGhe = other.maGhe;
        this.maMayBay = other.maMayBay;
        this.soGhe = other.soGhe;
        this.giaGhe = other.giaGhe;
        this.trangThai = other.trangThai;
    }

    public String getMaGhe() {
        return maGhe;
    }

    public void setMaGhe(String maGhe) {
        this.maGhe = maGhe;
    }

    public String getMaMayBay() {
        return maMayBay;
    }

    public void setMaMayBay(String maMayBay) {
        this.maMayBay = maMayBay;
    }

    public String getSoGhe() {
        return soGhe;
    }

    public void setSoGhe(String soGhe) {
        this.soGhe = soGhe;
    }

    public BigDecimal getGiaGhe() {
        return giaGhe;
    }

    public void setGiaGhe(BigDecimal giaGhe) {
        this.giaGhe = giaGhe;
    }

    public TrangThaiGhe getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiGhe trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return maGhe + " " + maMayBay + " " + soGhe + " " + giaGhe + " " + trangThai;
    }
}
