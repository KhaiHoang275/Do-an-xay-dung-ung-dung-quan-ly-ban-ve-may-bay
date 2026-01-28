package model;
import java.math.BigDecimal;

public class HanhLy {
    private String maHanhLy;
    private String maVe;
    private BigDecimal soKg;
    private String kichThuoc;
    private BigDecimal giaTien;
    private String trangThai;
    private String ghiChu;
    public HanhLy() {};
    public HanhLy(String maHanhLy, String maVe, BigDecimal soKg, String kichThuoc, BigDecimal giaTien, String trangThai, String ghiChu) {
        this.maHanhLy = maHanhLy;
        this.maVe = maVe;
        this.soKg = soKg;
        this.kichThuoc = kichThuoc;
        this.giaTien = giaTien;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }
    public HanhLy( HanhLy hl){
         this.maHanhLy = hl.maHanhLy;
        this.maVe = hl.maVe;
        this.soKg = hl.soKg;
        this.kichThuoc = hl.kichThuoc;
        this.giaTien = hl.giaTien;
        this.trangThai = hl.trangThai;
        this.ghiChu = hl.ghiChu;
    }
    public String getMaHanhLy() {
        return maHanhLy;
    }

    public void setMaHanhLy(String maHanhLy) {
        this.maHanhLy = maHanhLy;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public BigDecimal getSoKg() {
        return soKg;
    }

    public void setSoKg(BigDecimal soKg) {
        this.soKg = soKg;
    }

    public String getKichThuoc() {
        return kichThuoc;
    }

    public void setKichThuoc(String kichThuoc) {
        this.kichThuoc = kichThuoc;
    }

    public BigDecimal getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(BigDecimal giaTien) {
        this.giaTien = giaTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    @Override
    public String toString() {
        return " " + maHanhLy + " " + maVe + " " + soKg + " " + kichThuoc 
                + " " + giaTien + " " + trangThai + " " + ghiChu + " ";
    }
}