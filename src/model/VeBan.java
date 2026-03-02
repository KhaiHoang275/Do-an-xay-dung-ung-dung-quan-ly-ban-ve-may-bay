package model;

import java.math.BigDecimal;

public class VeBan {
    private String maVe;
    private String maPhieuDatVe; 
    private String maChuyenBay;
    private String maHK;
    private String maHangVe;
    private String maGhe;
    private String loaiVe;
    private String loaiHK;
    private BigDecimal giaVe;
    private String trangThaiVe;

    public VeBan(){}

    public VeBan(VeBan a){
        this.maVe = a.maVe;
        this.maPhieuDatVe = a.maPhieuDatVe;
        this.maChuyenBay = a.maChuyenBay;
        this.maHK = a.maHK;
        this.maHangVe = a.maHangVe;
        this.loaiVe = a.loaiVe;
        this.loaiHK = a.loaiHK;
        this.giaVe = a.giaVe;
        this.trangThaiVe = a.trangThaiVe;
    }

    public VeBan(String maVe, String maPhieuDatVe, String maChuyenBay, String maHK, String maHangVe, String maGhe,
            String loaiVe, String loaiHK, BigDecimal giaVe, String trangThaiVe) {
        this.maVe = maVe;
        this.maPhieuDatVe = maPhieuDatVe;
        this.maChuyenBay = maChuyenBay;
        this.maHK = maHK;
        this.maHangVe = maHangVe;
        this.maGhe = maGhe;
        this.loaiVe = loaiVe;
        this.loaiHK = loaiHK;
        this.giaVe = giaVe;
        this.trangThaiVe = trangThaiVe;
    }
    

    @Override
    public String toString() {
        return maVe + " " + " " + maChuyenBay + " "
                + maHK + " " + maHangVe + " " + maGhe + " " + loaiVe + " " + loaiHK
                + " " + giaVe + " " + trangThaiVe;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public String getMaPhieuDatVe() {
        return maPhieuDatVe;
    }

    public void setMaPhieuDatVe(String maPhieuDatVe) {
        this.maPhieuDatVe = maPhieuDatVe;
    }

    public String getMaChuyenBay() {
        return maChuyenBay;
    }

    public void setMaChuyenBay(String maChuyenBay) {
        this.maChuyenBay = maChuyenBay;
    }

    public String getMaHK() {
        return maHK;
    }

    public void setMaHK(String maHK) {
        this.maHK = maHK;
    }

    public String getMaHangVe() {
        return maHangVe;
    }

    public void setMaHangVe(String maHangVe) {
        this.maHangVe = maHangVe;
    }

    public String getMaGhe() {
        return maGhe;
    }

    public void setMaGhe(String maGhe) {
        this.maGhe = maGhe;
    }

    public String getLoaiVe() {
        return loaiVe;
    }

    public void setLoaiVe(String loaiVe) {
        this.loaiVe = loaiVe;
    }

    public String getLoaiHK() {
        return loaiHK;
    }

    public void setLoaiHK(String loaiHK) {
        this.loaiHK = loaiHK;
    }

    public BigDecimal getGiaVe() {
        return giaVe;
    }

    public void setGiaVe(BigDecimal giaVe) {
        this.giaVe = giaVe;
    }

    public String getTrangThaiVe() {
        return trangThaiVe;
    }

    public void setTrangThaiVe(String trangThaiVe) {
        this.trangThaiVe = trangThaiVe;
    }
}