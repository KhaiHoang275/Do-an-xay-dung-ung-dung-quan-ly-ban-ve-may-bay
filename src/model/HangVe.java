package model;

public class HangVe {
    private String maHangVe;
    private String tenHang;
    private float heSoHangVe;
    private TrangThaiHangVe trangThai;

    public HangVe() {
    }

    public HangVe(String maHangVe, String tenHang, float heSoHangVe, TrangThaiHangVe trangThai) {
        this.maHangVe = maHangVe;
        this.tenHang = tenHang;
        this.heSoHangVe = heSoHangVe;
        this.trangThai = trangThai;
    }

    public HangVe(HangVe other) {
        this.maHangVe = other.maHangVe;
        this.tenHang = other.tenHang;
        this.heSoHangVe = other.heSoHangVe;
        this.trangThai = other.trangThai;
    }

    public String getMaHangVe() {
        return maHangVe;
    }

    public void setMaHangVe(String maHangVe) {
        this.maHangVe = maHangVe;
    }

    public String getTenHang() {
        return tenHang;
    }

    public void setTenHang(String tenHang) {
        this.tenHang = tenHang;
    }

    public float getHeSoHangVe() {
        return heSoHangVe;
    }

    public void setHeSoHangVe(float heSoHangVe) {
        this.heSoHangVe = heSoHangVe;
    }

    public TrangThaiHangVe getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiHangVe trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return tenHang; 
    }
}