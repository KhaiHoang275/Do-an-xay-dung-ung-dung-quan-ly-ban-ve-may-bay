package model;

import java.time.LocalDate;

public class NguoiDung { 

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
    private String maNguoiDung; 
    private String username; 
    private String password; 
    private String email; 
    private String sdt; 
    private LocalDate ngayTao;
    private String phan_quyen; 
    private String thanhPho;
    private TrangThai trangThaiTK;

    public NguoiDung() {
        maNguoiDung = "";
        username = ""; 
        password = ""; 
        email = ""; 
        sdt = ""; 
        ngayTao = LocalDate.of(1, 1, 1);
        phan_quyen = ""; 
        trangThaiTK = TrangThai.HOAT_DONG;
        thanhPho = "";
    }
    public NguoiDung(String maNguoiDung, String username, String password, String email, String sdt, LocalDate ngayTao, String phan_quyen, TrangThai trangThaiTK, String thanhPho) {
        this.maNguoiDung = maNguoiDung; 
        this.username = username; 
        this.password = password; 
        this.email = email; 
        this.sdt = sdt; 
        this.ngayTao = ngayTao;
        this.phan_quyen = phan_quyen; 
        this.trangThaiTK = trangThaiTK;
        this.thanhPho = thanhPho;
    } 

    public NguoiDung(NguoiDung nd) {
        this.maNguoiDung = nd.maNguoiDung; 
        this.username = nd.username; 
        this.password = nd.password; 
        this.email = nd.email; 
        this.sdt = nd.sdt; 
        this.ngayTao = nd.ngayTao;
        this.phan_quyen = nd.phan_quyen; 
        this.trangThaiTK = nd.trangThaiTK;  
        this.thanhPho = nd.thanhPho;
    } 

    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung; 
    }
    
    public String getMaNguoiDung() {
        return maNguoiDung; 
    }
    public void setUsername(String username) {
        this.username = username; 
    }
    public String getUsername() {
        return username; 
    }
    public void setPassword(String password) {
        this.password = password; 
    }
    public String getPassword() {
        return password; 
    }
    public void setEmail(String email) {
        this.email = email; 
    }
    public String getEmail() {
        return email; 
    }
    public void setSoDienThoai(String sdt) {
        this.sdt = sdt; 
    }
    public String getSoDienThoai() {
        return sdt; 
    }
    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;  
    }
    public LocalDate getNgayTao() {
        return ngayTao; 
    }
    public void setPhanQuyen(String phan_quyen) {
        this.phan_quyen = phan_quyen; 
    }
    public String getPhanQuyen() {
        return phan_quyen; 
    }
    public void setTrangThaiTK(TrangThai trangThaiTK) {
        this.trangThaiTK = trangThaiTK; 
    }
    public TrangThai getTrangThaiTK() {
        return trangThaiTK; 
    }
    public void setThanhPho(String thanhPho) {
        this.thanhPho = thanhPho;
    }
    public String getThanhPho() {
        return thanhPho;
    }

    public String toString() {
        return " " + maNguoiDung + " " + username + " " + password
                + " " + email + " " + sdt + " " + ngayTao + " " + phan_quyen
                + " " + trangThaiTK + " " + thanhPho;
    }
    
}