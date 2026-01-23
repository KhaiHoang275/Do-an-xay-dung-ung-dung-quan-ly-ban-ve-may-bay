package model;

import java.time.LocalDate;

public class NguoiDung {
    private String maNguoiDung; 
    private String username; 
    private String password; 
    private String email; 
    private String sdt; 
    private LocalDate ngayTao;
    private String phan_quyen;
    private String trangThaiTK; 

    public NguoiDung() {
        maNguoiDung = ""; 
        username = ""; 
        password = ""; 
        email = ""; 
        sdt = ""; 
        ngayTao = LocalDate.of(1, 1, 1);
        phan_quyen = ""; 
        trangThaiTK = ""; 
    }
    public NguoiDung(String maNguoiDung, String username, String password, String email, String sdt, LocalDate ngayTao, String phan_quyen, String trangThaiTK) {
        this.maNguoiDung = maNguoiDung; 
        this.username = username; 
        this.password = password; 
        this.email = email; 
        this.sdt = sdt; 
        this.ngayTao = ngayTao;
        this.phan_quyen = phan_quyen; 
        this.trangThaiTK = trangThaiTK; 
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
    public void setTrangThaiTK(String trangThaiTK) {
        this.trangThaiTK = trangThaiTK; 
    }
    public String getTrangThaiTK() {
        return trangThaiTK; 
    }

    public String toString() {
        return "NguoiDung [maNguoiDung=" + maNguoiDung + ", username=" + username + ", password=" + password
                + ", email=" + email + ", sdt=" + sdt + ", ngayTao=" + ngayTao + ", phan_quyen=" + phan_quyen
                + ", trangThaiTK=" + trangThaiTK + "]";
    }
    
}