package model;
public class Tuyen_Bay {
    String ma_tuyen_bay;
    String san_bay_di;
    String san_bay_den;
    float khoang_cach;
    double gia_goc;

    public Tuyen_Bay(){

    }

    public Tuyen_Bay(String ma_tuyen_bay, String san_bay_di, String san_bay_den, float khoang_cach, double gia_goc) {
        this.ma_tuyen_bay = ma_tuyen_bay;
        this.san_bay_di = san_bay_di;
        this.san_bay_den = san_bay_den;
        this.khoang_cach = khoang_cach;
        this.gia_goc = gia_goc;
    }

    public Tuyen_Bay(Tuyen_Bay tb){
        this.ma_tuyen_bay = tb.ma_tuyen_bay;
        this.san_bay_di = tb.san_bay_di;
        this.san_bay_den = tb.san_bay_den;
        this.khoang_cach = tb.khoang_cach;
        this.gia_goc = tb.gia_goc;
    }

    public String getMa_tuyen_bay() {
        return ma_tuyen_bay;
    }

    public String getSan_bay_di() {
        return san_bay_di;
    }

    public String getSan_bay_den() {
        return san_bay_den;
    }

    public float getKhoang_cach() {
        return khoang_cach;
    }

    public double getGia_goc() {
        return gia_goc;
    }

    public void setMa_tuyen_bay(String ma_tuyen_bay) {
        this.ma_tuyen_bay = ma_tuyen_bay;
    }

    public void setSan_bay_di(String san_bay_di) {
        this.san_bay_di = san_bay_di;
    }

    public void setSan_bay_den(String san_bay_den) {
        this.san_bay_den = san_bay_den;
    }

    public void setKhoang_cach(float khoang_cach) {
        this.khoang_cach = khoang_cach;
    }

    public void setGia_goc(double gia_goc) {
        this.gia_goc = gia_goc;
    }

    

}