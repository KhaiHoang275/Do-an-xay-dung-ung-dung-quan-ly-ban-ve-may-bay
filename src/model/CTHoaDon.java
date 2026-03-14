package model;

import java.math.BigDecimal;

public class CTHoaDon {
    private String maHoaDon;
    private String maVe;
    private BigDecimal donGiaVe;
    private BigDecimal tienDichVu;
    private BigDecimal thueVAT;
    private BigDecimal thanhTien;

    // Default constructor
    public CTHoaDon() {
    }

    // Parameterized constructor
    public CTHoaDon(String maHoaDon, String maVe, BigDecimal donGiaVe, BigDecimal tienDichVu, BigDecimal thueVAT, BigDecimal thanhTien) {
        this.maHoaDon = maHoaDon;
        this.maVe = maVe;
        this.donGiaVe = donGiaVe;
        this.tienDichVu = tienDichVu;
        this.thueVAT = thueVAT;
        this.thanhTien = thanhTien;
    }

    // Copy constructor
    public CTHoaDon(CTHoaDon cthd) {
        this.maHoaDon = cthd.maHoaDon;
        this.maVe = cthd.maVe;
        this.donGiaVe = cthd.donGiaVe;
        this.tienDichVu = cthd.tienDichVu;
        this.thueVAT = cthd.thueVAT;
        this.thanhTien = cthd.thanhTien;
    }

    // Getters and Setters
    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public BigDecimal getDonGiaVe() {
        return donGiaVe;
    }

    public void setDonGiaVe(BigDecimal donGiaVe) {
        this.donGiaVe = donGiaVe;
    }

    public BigDecimal getTienDichVu() {
        return tienDichVu;
    }

    public void setTienDichVu(BigDecimal tienDichVu) {
        this.tienDichVu = tienDichVu;
    }

    public BigDecimal getThueVAT() {
        return thueVAT;
    }

    public void setThueVAT(BigDecimal thueVAT) {
        this.thueVAT = thueVAT;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

   @Override
    public String toString() {
        return " " + maHoaDon + " " + maVe + " " + donGiaVe 
                + " " + tienDichVu + " " + thueVAT + " " + thanhTien + " ";
    }
}