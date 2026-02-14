package model;

public class HangVe {
    private String maHangVe;
    private String tenHang;
    private float heSoHangVe;

    public HangVe() {
    }

    public HangVe(String maHangVe, String tenHang, float heSoHangVe) {
        this.maHangVe = maHangVe;
        this.tenHang = tenHang;
        this.heSoHangVe = heSoHangVe;
    }

    public HangVe(HangVe other) {
        this.maHangVe = other.maHangVe;
        this.tenHang = other.tenHang;
        this.heSoHangVe = other.heSoHangVe;
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

    @Override
    public String toString() {
        return maHangVe + " " + tenHang + " " + heSoHangVe;
    }
}
