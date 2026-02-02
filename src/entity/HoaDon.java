package entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HoaDon {
    private String maHoaDon;
    private String maPhieuDatVe;
    private String maNV;
    private LocalDateTime ngayLap;
    private BigDecimal tongTien;
    private String phuongThuc;
    private String donViTienTe;
    private BigDecimal thue;
    public HoaDon() {
    }
    public HoaDon(String maHoaDon, String maPhieuDatVe, String maNV, LocalDateTime ngayLap, 
                  BigDecimal tongTien, String phuongThuc, String donViTienTe, BigDecimal thue) {
        this.maHoaDon = maHoaDon;
        this.maPhieuDatVe = maPhieuDatVe;
        this.maNV = maNV;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
        this.phuongThuc = phuongThuc;
        this.donViTienTe = donViTienTe;
        this.thue = thue;
    }
    public HoaDon(HoaDon hd){
         this.maHoaDon = hd.maHoaDon;
        this.maPhieuDatVe = hd.maPhieuDatVe;
        this.maNV = hd.maNV;
        this.ngayLap = hd.ngayLap;
        this.tongTien = hd.tongTien;
        this.phuongThuc = hd.phuongThuc;
        this.donViTienTe = hd.donViTienTe;
        this.thue = hd.thue;
    }
    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaPhieuDatVe() {
        return maPhieuDatVe;
    }

    public void setMaPhieuDatVe(String maPhieuDatVe) {
        this.maPhieuDatVe = maPhieuDatVe;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public String getDonViTienTe() {
        return donViTienTe;
    }

    public void setDonViTienTe(String donViTienTe) {
        this.donViTienTe = donViTienTe;
    }

    public BigDecimal getThue() {
        return thue;
    }

    public void setThue(BigDecimal thue) {
        this.thue = thue;
    }
   @Override
    public String toString() {
        return " " + maHoaDon + " " + maPhieuDatVe + " " + maNV + " " + ngayLap 
                + " " + tongTien + " " + phuongThuc + " " + donViTienTe + " " + thue + " ";
    }
}