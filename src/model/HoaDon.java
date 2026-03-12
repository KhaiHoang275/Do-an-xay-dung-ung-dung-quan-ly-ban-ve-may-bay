package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HoaDon {
    private String maHoaDon;
    private String maPhieuDatVe;
    private String maNV;
    private LocalDateTime ngayLap;
    private BigDecimal tongTien;
    private String phuongThuc;
    private String donViTienTe;
    private BigDecimal thue;
    private String trangThai;

    // Default constructor
    public HoaDon() {
    }

    // Parameterized constructor
    public HoaDon(String maHoaDon, String maPhieuDatVe, String maNV, LocalDateTime ngayLap, 
                  BigDecimal tongTien, String phuongThuc, String donViTienTe, BigDecimal thue, String trangThai) {
        this.maHoaDon = maHoaDon;
        this.maPhieuDatVe = maPhieuDatVe;
        this.maNV = maNV;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
        this.phuongThuc = phuongThuc;
        this.donViTienTe = donViTienTe;
        this.thue = thue;
        this.trangThai = trangThai;
    }

    // Copy constructor
    public HoaDon(HoaDon hd) {
        this.maHoaDon = hd.maHoaDon;
        this.maPhieuDatVe = hd.maPhieuDatVe;
        this.maNV = hd.maNV;
        this.ngayLap = hd.ngayLap;
        this.tongTien = hd.tongTien;
        this.phuongThuc = hd.phuongThuc;
        this.donViTienTe = hd.donViTienTe;
        this.thue = hd.thue;
        this.trangThai = hd.trangThai;
    }

    // Getters and Setters
    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaPhieuDatVe() {
        return maPhieuDatVe;
    }

    public void setMaPhieuDatVe(String maPhieuDatVe) {
        this.maPhieuDatVe = maPhieuDatVe;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public String getDonViTienTe() {
        return donViTienTe;
    }

    public void setDonViTienTe(String donViTienTe) {
        this.donViTienTe = donViTienTe;
    }

    public BigDecimal getThue() {
        return thue;
    }

    public void setThue(BigDecimal thue) {
        this.thue = thue;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return " " + maHoaDon + " " + maPhieuDatVe + " " + maNV + " " + ngayLap 
                + " " + tongTien + " " + phuongThuc + " " + donViTienTe + " " + thue + " " + trangThai + " ";
    }
}