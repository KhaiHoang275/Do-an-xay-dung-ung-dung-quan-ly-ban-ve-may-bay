package model;

public class MayBay {
    
    public enum TrangThai {
        HOAT_DONG("Hoạt động"),
        NGUNG_HOAT_DONG("Ngừng hoạt động");

        private final String value;
        TrangThai(String value) { this.value = value; }
        public String getValue() { return value; }

        public static TrangThai fromString(String text) {
            for (TrangThai t : TrangThai.values()) {
                if (t.value.equalsIgnoreCase(text)) {
                    return t;
                }
            }
            return HOAT_DONG;
        }
    }

    private String maMayBay;
    private String soHieu; 
    private String hangSanXuat;
    private int tongSoGhe;
    private TrangThai trangThai;

    public MayBay() {
        maMayBay = "";
        soHieu = "";
        hangSanXuat = "";
        tongSoGhe = 0;
        trangThai = TrangThai.HOAT_DONG;
    }
    
    public MayBay(String maMayBay, String soHieu, String hangSanXuat, int tongSoGhe, TrangThai trangThai) {
        this.maMayBay = maMayBay;
        this.soHieu = soHieu;
        this.hangSanXuat = hangSanXuat;
        this.tongSoGhe = tongSoGhe;
        this.trangThai = trangThai;
    }

    public MayBay(MayBay mb) {
        this.maMayBay = mb.maMayBay;
        this.soHieu = mb.soHieu;
        this.hangSanXuat = mb.hangSanXuat;
        this.tongSoGhe = mb.tongSoGhe;
        this.trangThai = mb.trangThai;
    }

    public void setMaMayBay(String maMayBay) { this.maMayBay = maMayBay; }
    public String getMaMayBay() { return maMayBay; }
    
    public void setSoHieu(String soHieu) { this.soHieu = soHieu; }
    public String getSoHieu() { return soHieu; }
    
    public void setHangSanXuat(String hangSanXuat) { this.hangSanXuat = hangSanXuat; }
    public String getHangSanXuat() { return hangSanXuat; }
    
    public void setTongSoGhe(int tongSoGhe) { this.tongSoGhe = tongSoGhe; }
    public int getTongSoGhe() { return tongSoGhe; }
    
    public void setTrangThai(TrangThai trangThai) { this.trangThai = trangThai; }
    public TrangThai getTrangThai() { return trangThai; }
}