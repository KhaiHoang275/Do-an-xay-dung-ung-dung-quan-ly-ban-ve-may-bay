package entity;
import java.time.LocalDateTime;

public class ChuyenBay {
    private String maChuyenBay;
    private String maTuyenBay;
    private String maMayBay;
    private String maHeSoGia;
    private LocalDateTime ngayGioDi;
    private LocalDateTime ngayGioDen;
    private TrangThaiChuyenBay trangThai;

    public ChuyenBay() {
    }

    public ChuyenBay(String maChuyenBay, String maTuyenBay, String maMayBay, String maHeSoGia, LocalDateTime ngayGioDi, LocalDateTime ngayGioDen, TrangThaiChuyenBay trangThai) {
        this.maChuyenBay = maChuyenBay;
        this.maTuyenBay = maTuyenBay;
        this.maMayBay = maMayBay;
        this.maHeSoGia = maHeSoGia;
        this.ngayGioDi = ngayGioDi;
        this.ngayGioDen = ngayGioDen;
        this.trangThai = trangThai;
    }

    public ChuyenBay(ChuyenBay cb) {
        this.maChuyenBay = cb.maChuyenBay;
        this.maTuyenBay = cb.maTuyenBay;
        this.maMayBay = cb.maMayBay;
        this.maHeSoGia = cb.maHeSoGia;
        this.ngayGioDi = cb.ngayGioDi;
        this.ngayGioDen = cb.ngayGioDen;
        this.trangThai = cb.trangThai;
    }

    public String getMaChuyenBay() {
        return maChuyenBay;
    }

    public void setMaChuyenBay(String maChuyenBay) {
        this.maChuyenBay = maChuyenBay;
    }

    public String getMaTuyenBay() {
        return maTuyenBay;
    }

    public void setMaTuyenBay(String maTuyenBay) {
        this.maTuyenBay = maTuyenBay;
    }

    public String getMaMayBay() {
        return maMayBay;
    }

    public void setMaMayBay(String maMayBay) {
        this.maMayBay = maMayBay;
    }

    public String getMaHeSoGia() {
        return maHeSoGia;
    }

    public void setMaHeSoGia(String maHeSoGia) {
        this.maHeSoGia = maHeSoGia;
    }

    public LocalDateTime getNgayGioDi() {
        return ngayGioDi;
    }

    public void setNgayGioDi(LocalDateTime ngayGioDi) {
        this.ngayGioDi = ngayGioDi;
    }

    public LocalDateTime getNgayGioDen() {
        return ngayGioDen;
    }

    public void setNgayGioDen(LocalDateTime ngayGioDen) {
        this.ngayGioDen = ngayGioDen;
    }

    public TrangThaiChuyenBay getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiChuyenBay trangThai) {
        this.trangThai = trangThai;
    }

    public String toString() {
        return maChuyenBay + " " + maTuyenBay + " " + maMayBay + " "
                + maHeSoGia + " " + ngayGioDi + " " + ngayGioDen + " " + trangThai;
    }
}