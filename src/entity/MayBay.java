package entity;

public class MayBay {
    private String maMayBay;
    private String soHieu; 
    private String hangSanXuat;
    private int tongSoGhe;

    public MayBay() {
        maMayBay = "";
        soHieu = "";
        hangSanXuat = "";
        tongSoGhe = 0;
    }
    public MayBay(String maMayBay, String soHieu, String hangSanXuat, int tongSoGhe) {
        this.maMayBay = maMayBay;
        this.soHieu = soHieu;
        this.hangSanXuat = hangSanXuat;
        this.tongSoGhe = tongSoGhe;
    }

    public MayBay(MayBay mb) {
        this.maMayBay = mb.maMayBay;
        this.soHieu = mb.soHieu;
        this.hangSanXuat = mb.hangSanXuat;
        this.tongSoGhe = mb.tongSoGhe;
    }

    public void setMaMayBay(String maMayBay) {
        this.maMayBay = maMayBay;
    }
    public String getMaMayBay() {
        return maMayBay;
    }
    public void setSoHieu(String soHieu) {
        this.soHieu = soHieu;
    }
    public String getSoHieu() {
        return soHieu;
    }
    public void setHangSanXuat(String hangSanXuat) {
        this.hangSanXuat = hangSanXuat;
    }
    public String getHangSanXuat() {
        return hangSanXuat;
    }
    public void setTongSoGhe(int tongSoGhe) {
        this.tongSoGhe = tongSoGhe;
    }
    public int getTongSoGhe() {
        return tongSoGhe;
    } 

    public String toString() {
        return maMayBay + " " + soHieu + " " + hangSanXuat
                + " " + tongSoGhe;
    }
    
}
