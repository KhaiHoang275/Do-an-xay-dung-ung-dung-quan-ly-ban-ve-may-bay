package model;

import java.time.LocalDate;

public class NhanVien {
    
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

    private String maNV; 
    private String maNguoiDung; 
    private String hoten; 
    private String chucVu; 
    private LocalDate ngayVaoLam;
    private TrangThai trangThaiLamViec; 

    public NhanVien() {
        maNV = ""; 
        maNguoiDung = ""; 
        hoten = ""; 
        chucVu = "";
        ngayVaoLam = LocalDate.of(1, 1, 1); 
        trangThaiLamViec = TrangThai.HOAT_DONG;
    } 

    public NhanVien(String maNV, String maNguoiDung, String hoten, String chucVu, LocalDate ngayVaoLam, TrangThai trangThaiLamViec) {
        this.maNV = maNV; 
        this.maNguoiDung = maNguoiDung; 
        this.hoten = hoten; 
        this.chucVu = chucVu; 
        this.ngayVaoLam = ngayVaoLam;
        this.trangThaiLamViec = trangThaiLamViec;
    } 

    public NhanVien(NhanVien nv) {
        this.maNV = nv.maNV; 
        this.maNguoiDung = nv.maNguoiDung; 
        this.hoten = nv.hoten; 
        this.chucVu = nv.chucVu; 
        this.ngayVaoLam = nv.ngayVaoLam;
        this.trangThaiLamViec = nv.trangThaiLamViec;
    } 

    public void setMaNV(String maNV) { this.maNV = maNV; } 
    public String getMaNV() { return maNV; } 
    
    public void setMaNguoiDung(String maNguoiDung) { this.maNguoiDung = maNguoiDung; } 
    public String getMaNguoiDung() { return maNguoiDung; } 
    
    public void setHoTen(String hoten) { this.hoten = hoten; } 
    public String getHoTen() { return hoten; } 
    
    public void setChucVu(String chucVu) { this.chucVu = chucVu; } 
    public String getChucVu() { return chucVu; } 
    
    public void setNgayVaoLam(LocalDate ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; } 
    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    
    public void setTrangThaiLamViec(TrangThai trangThaiLamViec) { this.trangThaiLamViec = trangThaiLamViec; }
    public TrangThai getTrangThaiLamViec() { return trangThaiLamViec; }
}