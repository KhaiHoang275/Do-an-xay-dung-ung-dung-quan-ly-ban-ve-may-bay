package model;

public class SanBay {
    
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
    
    private String maSanBay; 
    private String tenSanBay; 
    private String quocGia; 
    private String thanhPho; 
    private TrangThai trangThai;

    public SanBay() {
        maSanBay = ""; 
        tenSanBay = ""; 
        quocGia = ""; 
        thanhPho = "";   
        trangThai = TrangThai.HOAT_DONG;
    } 

    public SanBay(String maSanBay, String tenSanBay, String quocGia, String thanhPho, TrangThai trangThai) {
        this.maSanBay = maSanBay; 
        this.tenSanBay = tenSanBay; 
        this.quocGia = quocGia; 
        this.thanhPho = thanhPho; 
        this.trangThai = trangThai;
    } 

    public SanBay(SanBay sb) {
        this.maSanBay = sb.maSanBay; 
        this.tenSanBay = sb.tenSanBay; 
        this.quocGia = sb.quocGia; 
        this.thanhPho = sb.thanhPho; 
        this.trangThai = sb.trangThai;
    }

    public void setMaSanBay(String maSanBay) { this.maSanBay = maSanBay; }
    public String getMaSanBay() { return maSanBay; }
    
    public void setTenSanBay(String tenSanBay) { this.tenSanBay = tenSanBay; }
    public String getTenSanBay() { return tenSanBay; }
    
    public void setQuocGia(String quocGia) { this.quocGia = quocGia; }
    public String getQuocGia() { return quocGia; }
    
    public void setThanhPho(String thanhPho) { this.thanhPho = thanhPho; }
    public String getThanhPho() { return thanhPho; }
    
    public void setTrangThai(TrangThai trangThai) { this.trangThai = trangThai; }
    public TrangThai getTrangThai() { return trangThai; }
}