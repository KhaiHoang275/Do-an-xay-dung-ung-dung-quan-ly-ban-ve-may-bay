package model;

public class HeSoGia {
    private String ma_He_So_Gia;
    private float he_So;
    private float so_Gio_Dat_Truoc;

    public HeSoGia(){}

    public HeSoGia(String ma_He_So_Gia, float he_So, float so_Gio_Dat_Truoc) {
        this.ma_He_So_Gia = ma_He_So_Gia;
        this.he_So = he_So;
        this.so_Gio_Dat_Truoc = so_Gio_Dat_Truoc;
    }

    public HeSoGia(HeSoGia a) {
        this.ma_He_So_Gia = a.ma_He_So_Gia;
        this.he_So = a.he_So;
        this.so_Gio_Dat_Truoc = a.so_Gio_Dat_Truoc;
    }

    public String getMa_He_So_Gia() {
        return ma_He_So_Gia;
    }

    public void setMa_He_So_Gia(String ma_He_So_Gia) {
        this.ma_He_So_Gia = ma_He_So_Gia;
    }

    public float getHe_So() {
        return he_So;
    }

    public void setHe_So(float he_So) {
        this.he_So = he_So;
    }

    public float getSo_Gio_Dat_Truoc() {
        return so_Gio_Dat_Truoc;
    }

    public void setSo_Gio_Dat_Truoc(float so_Gio_Dat_Truoc) {
        this.so_Gio_Dat_Truoc = so_Gio_Dat_Truoc;
    }
}