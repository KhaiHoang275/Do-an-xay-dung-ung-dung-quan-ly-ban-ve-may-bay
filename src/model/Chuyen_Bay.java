package model;
public class Chuyen_Bay {
    String ma_chuyen_bay;
    String ma_tuyen_bay;
    String ma_may_bay;
    String ma_he_so_gia;
    String ngay_gio_di;
    String ngay_gio_den;
    boolean trang_thai;

    public Chuyen_Bay(){

    }

    public Chuyen_Bay(String ma_chuyen_bay, String ma_tuyen_bay, String ma_may_bay, String ma_he_so_gia, String ngay_gio_di, String ngay_gio_den, boolean trang_thai) {
        this.ma_chuyen_bay = ma_chuyen_bay;
        this.ma_tuyen_bay = ma_tuyen_bay;
        this.ma_may_bay = ma_may_bay;
        this.ma_he_so_gia = ma_he_so_gia;
        this.ngay_gio_di = ngay_gio_di;
        this.ngay_gio_den = ngay_gio_den;
        this.trang_thai = trang_thai;
    }

    public Chuyen_Bay(Chuyen_Bay cb){
        this.ma_chuyen_bay = cb.ma_chuyen_bay;
        this.ma_tuyen_bay = cb.ma_tuyen_bay;
        this.ma_may_bay = cb.ma_may_bay;
        this.ma_he_so_gia = cb.ma_he_so_gia;
        this.ngay_gio_di = cb.ngay_gio_di;
        this.ngay_gio_den = cb.ngay_gio_den;
        this.trang_thai = cb.trang_thai;
    }

    public String getMa_chuyen_bay() {
        return ma_chuyen_bay;
    }

    public String getMa_tuyen_bay() {
        return ma_tuyen_bay;
    }

    public String getMa_may_bay() {
        return ma_may_bay;
    }

    public String getMa_he_so_gia() {
        return ma_he_so_gia;
    }

    public Sgtring getNgay_gio_di() {
        return ngay_gio_di;
    }

    public String getNgay_gio_den() {
        return ngay_gio_den;
    }

    public boolean isTrang_thai() {
        return trang_thai;
    }

    public void setMa_chuyen_bay(String ma_chuyen_bay) {
        this.ma_chuyen_bay = ma_chuyen_bay;
    }

    public void setMa_tuyen_bay(String ma_tuyen_bay) {
        this.ma_tuyen_bay = ma_tuyen_bay;
    }

    public void setMa_may_bay(String ma_may_bay) {
        this.ma_may_bay = ma_may_bay;
    }

    public void setMa_he_so_gia(String ma_he_so_gia) {
        this.ma_he_so_gia = ma_he_so_gia;
    }

    public void setNgay_gio_di(String ngay_gio_di) {
        this.ngay_gio_di = ngay_gio_di;
    }

    public void setNgay_gio_den(String ngay_gio_den) {
        this.ngay_gio_den = ngay_gio_den;
    }

    public void setTrang_thai(boolean trang_thai) {
        this.trang_thai = trang_thai;
    }


}
