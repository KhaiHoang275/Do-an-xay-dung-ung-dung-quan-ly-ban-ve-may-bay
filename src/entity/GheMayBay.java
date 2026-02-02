package entity;

public class GheMayBay {
    private String ma_Ghe;
    private String ma_May_Bay;
    private String so_Ghe;
    private double gia_Ghe;

    public GheMayBay(){}

    public GheMayBay(String ma_Ghe, String ma_May_Bay,String so_Ghe, double gia_Ghe){
        this.ma_Ghe = ma_Ghe;
        this.ma_May_Bay = ma_May_Bay;
        this.so_Ghe = so_Ghe;
        this.gia_Ghe = gia_Ghe;
    }

    public GheMayBay(GheMayBay a){
        this.ma_Ghe = a.ma_Ghe;
        this.ma_May_Bay = a.ma_May_Bay;
        this.so_Ghe = a.so_Ghe;
        this.gia_Ghe = a.gia_Ghe;
    }

    public String getMa_Ghe() {
        return ma_Ghe;
    }

    public void setMa_Ghe(String ma_Ghe) {
        this.ma_Ghe = ma_Ghe;
    }

    public String getSo_Ghe() {
        return so_Ghe;
    }

    public String getMa_May_Bay() {
        return ma_May_Bay;
    }

    public void setMa_May_Bay(String ma_May_Bay) {
        this.ma_May_Bay = ma_May_Bay;
    }

    public void setSo_Ghe(String so_Ghe) {
        this.so_Ghe = so_Ghe;
    }

    public double getGia_Ghe() {
        return gia_Ghe;
    }

    public void setGia_Ghe(double gia_Ghe) {
        this.gia_Ghe = gia_Ghe;
    }
}