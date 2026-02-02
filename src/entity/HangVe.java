package entity;

public class HangVe {
    private String ma_Hang_Ve;
    private String ten_Hang;
    private float he_So_Hang_Ve;

    public HangVe(){}

    public HangVe(String ma_Hang_Ve, String ten_Hang, float he_So_Hang_Ve) {
        this.ma_Hang_Ve = ma_Hang_Ve;
        this.ten_Hang = ten_Hang;
        this.he_So_Hang_Ve = he_So_Hang_Ve;
    }

    public HangVe(HangVe a) {
        this.ma_Hang_Ve = a.ma_Hang_Ve;
        this.ten_Hang = a.ten_Hang;
        this.he_So_Hang_Ve = a.he_So_Hang_Ve;
    }

    public String getMa_Hang_Ve() {
        return ma_Hang_Ve;
    }

    public void setMa_Hang_Ve(String ma_Hang_Ve) {
        this.ma_Hang_Ve = ma_Hang_Ve;
    }

    public String getTen_Hang() {
        return ten_Hang;
    }

    public void setTen_Hang(String ten_Hang) {
        this.ten_Hang = ten_Hang;
    }

    public float getHe_So_Hang_Ve() {
        return he_So_Hang_Ve;
    }

    public void setHe_So_Hang_Ve(float he_So_Hang_Ve) {
        this.he_So_Hang_Ve = he_So_Hang_Ve;
    }
}