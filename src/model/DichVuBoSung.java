package model;

import java.math.BigDecimal;

public class DichVuBoSung {
    private String maDichVu;
    private String tenDichVu;
    private BigDecimal donGia;
    private String trangThai;

    // Default constructor
    public DichVuBoSung() {
    }

    // Parameterized constructor
    public DichVuBoSung(String maDichVu, String tenDichVu, BigDecimal donGia, String trangThai) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.donGia = donGia;
        this.trangThai = trangThai;
    }

    // Copy constructor
    public DichVuBoSung(DichVuBoSung dvbs) {
        this.maDichVu = dvbs.maDichVu;
        this.tenDichVu = dvbs.tenDichVu;
        this.donGia = dvbs.donGia;
        this.trangThai = dvbs.trangThai;
    }

    // Getters and Setters
    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return " " + maDichVu + " " + tenDichVu + " " + donGia + " " + trangThai + " ";
    }
}