package model;
import java.math.BigDecimal;

public class CTHoaDon {
    private String maCTHD;     
    private String maHoaDon;   
    private String maVe;        
    private BigDecimal soTien;  
    private String maNguoiDung; 

    public CTHoaDon() {
    }
    public CTHoaDon(String maCTHD, String maHoaDon, String maVe, BigDecimal soTien, String maNguoiDung) {
        this.maCTHD = maCTHD;
        this.maHoaDon = maHoaDon;
        this.maVe = maVe;
        this.soTien = soTien;
        this.maNguoiDung = maNguoiDung;
    }
    public CTHoaDon(CTHoaDon cthd){
        this.maCTHD = cthd.maCTHD;
        this.maHoaDon = cthd.maHoaDon;
        this.maVe = cthd.maVe;
        this.soTien = cthd.soTien;
        this.maNguoiDung = cthd.maNguoiDung;
    }
    public String getMaCTHD() {
        return maCTHD;
    }

    public void setMaCTHD(String maCTHD) {
        this.maCTHD = maCTHD;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }
}