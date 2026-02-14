package model;

import java.time.LocalDate;

public class ThongTinHanhKhach {
    private String maHK;
    private String maNguoiDung;
    private String maThuHang;
    private String hoTen;
    private String cccd;
    private String hoChieu;
    private String gioiTinh;
    private LocalDate ngaySinh;
    private int diemTichLuy;

    public ThongTinHanhKhach(){}

    public ThongTinHanhKhach(ThongTinHanhKhach a){
        this.maHK = a.maHK;
        this.maNguoiDung = a.maNguoiDung;
        this.maThuHang = a.maThuHang;
        this.hoTen = a.hoTen;
        this.cccd = a.cccd;
        this.hoChieu = a.hoChieu;
        this.ngaySinh = a.ngaySinh;
        this.gioiTinh = a.gioiTinh;
        this.diemTichLuy = a.diemTichLuy;
    }

    public ThongTinHanhKhach(String maHK, String maNguoiDung, String maThuHang, String hoTen, String cccd,
            String hoChieu, String gioiTinh, LocalDate ngaySinh, int diemTichLuy) {
        this.maHK = maHK;
        this.maNguoiDung = maNguoiDung;
        this.maThuHang = maThuHang;
        this.hoTen = hoTen;
        this.cccd = cccd;
        this.hoChieu = hoChieu;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.diemTichLuy = diemTichLuy;
    }

    public String getMaHK() {
        return maHK;
    }

    public void setMaHK(String maHK) {
        this.maHK = maHK;
    }

    public String getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getMaThuHang() {
        return maThuHang;
    }

    public void setMaThuHang(String maThuHang) {
        this.maThuHang = maThuHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getHoChieu() {
        return hoChieu;
    }

    public void setHoChieu(String hoChieu) {
        this.hoChieu = hoChieu;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }
}
