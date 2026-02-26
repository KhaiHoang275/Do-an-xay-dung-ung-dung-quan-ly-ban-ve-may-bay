package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class    PhieuDatVe {
    private String maPhieuDatVe;
    private String maNguoiDung;
    private String maNV;
    private int thoiLuong;
    private LocalDate ngayDat;
    private int soLuongVe;
    private BigDecimal tongTien;
    private String trangThaiThanhToan;
    private String maKhuyenMai;

    public PhieuDatVe(){}

    public PhieuDatVe(PhieuDatVe a){
        this.maPhieuDatVe = a.maPhieuDatVe;
        this.maNguoiDung = a.maNguoiDung;
        this.maNV = a.maNV;
        this.thoiLuong = a.thoiLuong;
        this.ngayDat = a.ngayDat;
        this.soLuongVe = a.soLuongVe;
        this.tongTien = a.tongTien;
        this.trangThaiThanhToan = a.trangThaiThanhToan;
        this.maKhuyenMai = a.maKhuyenMai;
    }

    public PhieuDatVe(String maPhieuDatVe, String maNguoiDung, String maNV, int thoiLuong, LocalDate ngayDat,
            int soLuongVe, BigDecimal tongTien, String trangThaiThanhToan, String maKhuyenMai) {
        this.maPhieuDatVe = maPhieuDatVe;
        this.maNguoiDung = maNguoiDung;
        this.maNV = maNV;
        this.thoiLuong = thoiLuong;
        this.ngayDat = ngayDat;
        this.soLuongVe = soLuongVe;
        this.tongTien = tongTien;
        this.trangThaiThanhToan = trangThaiThanhToan;
        this.maKhuyenMai = maKhuyenMai;
    }

    public String getMaPhieuDatVe() {
        return maPhieuDatVe;
    }

    public void setMaPhieuDatVe(String maPhieuDatVe) {
        this.maPhieuDatVe = maPhieuDatVe;
    }

    public String getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public int getThoiLuong() {
        return thoiLuong;
    }

    public void setThoiLuong(int thoiLuong) {
        this.thoiLuong = thoiLuong;
    }

    public LocalDate getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDate ngayDat) {
        this.ngayDat = ngayDat;
    }

    public int getSoLuongVe() {
        return soLuongVe;
    }

    public void setSoLuongVe(int soLuongVe) {
        this.soLuongVe = soLuongVe;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(String trangThaiThanhToan) {
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    @Override
    public String toString() {
        return maPhieuDatVe + " " + maNguoiDung + " " + maNV
                + " " + thoiLuong + " " + ngayDat + " " + soLuongVe + " "
                + tongTien + " " + trangThaiThanhToan + " " + maKhuyenMai + " ";
    }
}