package model;

public class HeSoGia {
    private String maHeSoGia;
    private float heSo;
    private float soGioDatTruoc;

    public HeSoGia() {
    }

    public HeSoGia(String maHeSoGia, float heSo, float soGioDatTruoc) {
        this.maHeSoGia = maHeSoGia;
        this.heSo = heSo;
        this.soGioDatTruoc = soGioDatTruoc;
    }

    public HeSoGia(HeSoGia other) {
        this.maHeSoGia = other.maHeSoGia;
        this.heSo = other.heSo;
        this.soGioDatTruoc = other.soGioDatTruoc;
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

    @Override
    public String toString() {
        return maHeSoGia + " " + heSo + " " + soGioDatTruoc;
    }
}
