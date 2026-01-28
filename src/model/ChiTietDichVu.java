package model;
import java.math.BigDecimal;

public class ChiTietDichVu {
    private String maVe;
    private String maDichVu;
    private int soLuong;
    private BigDecimal thanhTien;
    public ChiTietDichVu() {
    }
    public ChiTietDichVu(String maVe, String maDichVu, int soLuong, BigDecimal thanhTien) {
        this.maVe = maVe;
        this.maDichVu = maDichVu;
        this.soLuong = soLuong;
        this.thanhTien = thanhTien;
    }
    public ChiTietDichVu(ChiTietDichVu ctdv){
         this.maVe = ctdv.maVe;
        this.maDichVu = ctdv.maDichVu;
        this.soLuong = ctdv.soLuong;
        this.thanhTien = ctdv.thanhTien;
    }
    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }
    @Override
    public String toString() {
        return "ChiTietDichVu{" +
                "maVe='" + maVe + '\'' +
                ", maDichVu='" + maDichVu + '\'' +
                ", soLuong=" + soLuong +
                ", thanhTien=" + thanhTien +
                '}';
    }
}