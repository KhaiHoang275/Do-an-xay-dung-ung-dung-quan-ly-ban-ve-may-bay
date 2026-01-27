package model;
import java.time.LocalDate;
public class NhanVien {
    private String maNV; 
    private String maNguoiDung; 
    private String hoten; 
    private String chucVu; 
    private LocalDate ngayVaoLam;

    public NhanVien() {
        maNV = ""; 
        maNguoiDung = ""; 
        hoten = ""; 
        chucVu = "";
        ngayVaoLam = LocalDate.of(1, 1, 1); 
    } 

    public NhanVien(String maNV, String maNguoiDung, String hoten, String chucVu, LocalDate ngayVaoLam) {
        this.maNV = maNV; 
        this.maNguoiDung = maNguoiDung; 
        this.hoten = hoten; 
        this.chucVu = chucVu; 
        this.ngayVaoLam = ngayVaoLam;
    } 

    public NhanVien(NhanVien nv) {
        this.maNV = nv.maNV; 
        this.maNguoiDung = nv.maNguoiDung; 
        this.hoten = nv.hoten; 
        this.chucVu = nv.chucVu; 
        this.ngayVaoLam = nv.ngayVaoLam;
    } 

    public void setMaNV(String maNV) {
        this.maNV = maNV; 
    } 
    public String getMaNV() {
        return maNV; 
    } 
    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung; 
    } 
    public String getMaNguoiDung() {
        return maNguoiDung; 
    } 
    public void setHoTen(String hoten) {
        this.hoten = hoten; 
    } 
    public String getHoTen() {
        return hoten; 
    } 
    public void setChucVu(String chucVu) {
        this.chucVu = chucVu; 
    }
    public String getChucVu() {
        return chucVu; 
    } 
    
    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam; 
    }
    public LocalDate getNgayVaoLam() {
        return ngayVaoLam; 
    } 

    public String toString() {
        return maNV + " " + maNguoiDung + " " + hoten + " " + chucVu
                + " " + ngayVaoLam; 
    }

}
