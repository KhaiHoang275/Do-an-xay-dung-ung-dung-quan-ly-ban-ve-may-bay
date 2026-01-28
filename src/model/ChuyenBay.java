package model;

public class ChuyenBay {
    private String maChuyenBay;
    private String maTuyenBay;
    private String maMayBay;
    private String maHeSoGia;
    private String ngayGioDi;
    private String ngayGioDen;
    private boolean trangThai;

    public ChuyenBay() {}

    public ChuyenBay(String maChuyenBay, String maTuyenBay, String maMayBay, String maHeSoGia, String ngayGioDi, String ngayGioDen, boolean trangThai) {
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

    public String getNgayGioDi() {
        return ngayGioDi;
    }

    public void setNgayGioDi(String ngayGioDi) {
        this.ngayGioDi = ngayGioDi;
    }

    public String getNgayGioDen() {
        return ngayGioDen;
    }

    public void setNgayGioDen(String ngayGioDen) {
        this.ngayGioDen = ngayGioDen;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }
}