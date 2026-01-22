public class May_Bay {
    private String ma_may_bay;
    private String so_hieu; 
    private String hang_san_xuat;
    private int tong_so_ghe;

    public May_Bay() {
        ma_may_bay = "";
        so_hieu = "";
        hang_san_xuat = "";
        tong_so_ghe = 0;
    }
    public May_Bay(String ma_may_bay, String so_hieu, String hang_san_xuat, int tong_so_ghe) {
        this.ma_may_bay = ma_may_bay;
        this.so_hieu = so_hieu;
        this.hang_san_xuat = hang_san_xuat;
        this.tong_so_ghe = tong_so_ghe;
    }

    public May_Bay(May_Bay mb) {
        this.ma_may_bay = mb.ma_may_bay;
        this.so_hieu = mb.so_hieu;
        this.hang_san_xuat = mb.hang_san_xuat;
        this.tong_so_ghe = mb.tong_so_ghe;
    }

    public void setMaMayBay(String ma_may_bay) {
        this.ma_may_bay = ma_may_bay;
    }
    public String getMaMayBay() {
        return ma_may_bay;
    }
    public void setSoHieu(String so_hieu) {
        this.so_hieu = so_hieu;
    }
    public String getSoHieu() {
        return so_hieu;
    }
    public void setHangSanXuat(String hang_san_xuat) {
        this.hang_san_xuat = hang_san_xuat;
    }
    public String getHangSanXuat() {
        return hang_san_xuat;
    }
    public void setTongSoGhe(int tong_so_ghe) {
        this.tong_so_ghe = tong_so_ghe;
    }
    public int getTongSoGhe() {
        return tong_so_ghe;
    }
    
}
