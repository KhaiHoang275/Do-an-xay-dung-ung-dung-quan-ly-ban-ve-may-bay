package model;

public class HeSoGia {
    private String maHeSoGia;
    private float heSo;
    private float soGioDatTruoc;
    private TrangThaiHeSoGia trangThai;

    public HeSoGia() {
    }

    public HeSoGia(String maHeSoGia, float heSo, float soGioDatTruoc, TrangThaiHeSoGia trangThai) {
        this.maHeSoGia = maHeSoGia;
        this.heSo = heSo;
        this.soGioDatTruoc = soGioDatTruoc;
        this.trangThai = trangThai;
    }

    public HeSoGia(HeSoGia other) {
        this.maHeSoGia = other.maHeSoGia;
        this.heSo = other.heSo;
        this.soGioDatTruoc = other.soGioDatTruoc;
        this.trangThai = other.trangThai;
    }

    public String getMaHeSoGia() {
        return maHeSoGia;
    }

    public void setMaHeSoGia(String maHeSoGia) {
        this.maHeSoGia = maHeSoGia;
    }

    public float getHeSo() {
        return heSo;
    }

    public void setHeSo(float heSo) {
        this.heSo = heSo;
    }

    public float getSoGioDatTruoc() {
        return soGioDatTruoc;
    }

    public void setSoGioDatTruoc(float soGioDatTruoc) {
        this.soGioDatTruoc = soGioDatTruoc;
    }

    public TrangThaiHeSoGia getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiHeSoGia trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return maHeSoGia + " " + heSo + " " + soGioDatTruoc + " " + trangThai;
    }
}