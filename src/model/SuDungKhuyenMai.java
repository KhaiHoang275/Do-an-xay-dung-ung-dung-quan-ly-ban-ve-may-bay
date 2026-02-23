package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SuDungKhuyenMai {

    private long id;
    private String maKhuyenMai;
    private String maHanhKhach;
    private String maDatVe;
    private LocalDateTime ngaySuDung;
    private BigDecimal giaTriGiamThucTe;

    public SuDungKhuyenMai() {}

    public SuDungKhuyenMai(long id, String maKhuyenMai,
                           String maHanhKhach, String maDatVe,
                           LocalDateTime ngaySuDung,
                           BigDecimal giaTriGiamThucTe) {
        this.id = id;
        this.maKhuyenMai = maKhuyenMai;
        this.maHanhKhach = maHanhKhach;
        this.maDatVe = maDatVe;
        this.ngaySuDung = ngaySuDung;
        this.giaTriGiamThucTe = giaTriGiamThucTe;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public String getMaHanhKhach() {
        return maHanhKhach;
    }

    public void setMaHanhKhach(String maHanhKhach) {
        this.maHanhKhach = maHanhKhach;
    }

    public String getMaDatVe() {
        return maDatVe;
    }

    public void setMaDatVe(String maDatVe) {
        this.maDatVe = maDatVe;
    }

    public LocalDateTime getNgaySuDung() {
        return ngaySuDung;
    }

    public void setNgaySuDung(LocalDateTime ngaySuDung) {
        this.ngaySuDung = ngaySuDung;
    }

    public BigDecimal getGiaTriGiamThucTe() {
        return giaTriGiamThucTe;
    }

    public void setGiaTriGiamThucTe(BigDecimal giaTriGiamThucTe) {
        this.giaTriGiamThucTe = giaTriGiamThucTe;
    }

    // getters & setters
}