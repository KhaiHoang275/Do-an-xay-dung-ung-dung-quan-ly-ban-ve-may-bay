package entity;
import java.math.BigDecimal;

public class DichVuBoSung {
    private String maDichVu;
    private String tenDichVu;
    private BigDecimal donGia;
    public DichVuBoSung() {
    }
    public DichVuBoSung(String maDichVu, String tenDichVu, BigDecimal donGia) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.donGia = donGia;
    }
    public DichVuBoSung(DichVuBoSung dvbs){
         this.maDichVu = dvbs.maDichVu;
        this.tenDichVu = dvbs.tenDichVu;
        this.donGia = dvbs.donGia;
    }
    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }
    @Override
    public String toString() {
        return " " + maDichVu + " " + tenDichVu + " " + donGia + " ";
    }
}