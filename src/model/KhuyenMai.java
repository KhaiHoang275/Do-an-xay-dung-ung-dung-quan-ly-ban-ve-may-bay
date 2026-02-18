package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class KhuyenMai {

    private String maKhuyenMai;
    private String tenKM;
    private String moTa;

    private String loaiKM;        //  → 'PHAN_TRAM', 'TIEN_CO_DINH'
    private BigDecimal giaTri;
    private BigDecimal donHangToiThieu;

    private int soLuongTong;
    private int soLuongDaDung;

    private LocalDate ngayBD;      // cũ là ngayBD
    private LocalDate ngayKT;     // cũ là ngayKT

    private boolean apDungChoTatCa;
    private String loaiKhachApDung;    // chuỗi phân cách bằng dấu phẩy

    private int gioiHanMoiKhach;

    private boolean trangThai;         // cũ có sẵn
    private String nguoiTao;
    private LocalDateTime ngayTao;

    // Constructor mặc định
    public KhuyenMai() {}

    // Constructor đầy đủ (bạn tự thêm nếu cần)

    // Getters & Setters (copy-paste từ đây)
    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }

    public String getTenKM() { return tenKM; }
    public void setTenKM(String tenKhuyenMai) { this.tenKM = tenKhuyenMai; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getLoaiKM() { return loaiKM; }
    public void setLoaiKM(String loaiKM) { this.loaiKM = loaiKM; }

    public BigDecimal getGiaTri() { return this.giaTri; }
    public void setGiaTri(BigDecimal giaTri) { this.giaTri = giaTri; }

    public BigDecimal getDonHangToiThieu() { return donHangToiThieu; }
    public void setDonHangToiThieu(BigDecimal donHangToiThieu) { this.donHangToiThieu = donHangToiThieu; }

    public int getSoLuongTong() { return soLuongTong; }
    public void setSoLuongTong(int soLuongTong) { this.soLuongTong = soLuongTong; }

    public int getSoLuongDaDung() { return soLuongDaDung; }
    public void setSoLuongDaDung(int soLuongDaDung) { this.soLuongDaDung = soLuongDaDung; }

    public LocalDate getNgayBD() { return ngayBD; }
    public void setNgayBD(LocalDate ngayBatDau) { this.ngayBD = ngayBatDau; }

    public LocalDate getNgayKT() { return ngayKT; }
    public void setNgayKT(LocalDate ngayKetThuc) { this.ngayKT = ngayKetThuc; }

    public boolean isApDungChoTatCa() { return apDungChoTatCa; }
    public void setApDungChoTatCa(boolean apDungChoTatCa) { this.apDungChoTatCa = apDungChoTatCa; }

    public String getLoaiKhachApDung() { return loaiKhachApDung; }
    public void setLoaiKhachApDung(String loaiKhachApDung) { this.loaiKhachApDung = loaiKhachApDung; }

    public int getGioiHanMoiKhach() { return gioiHanMoiKhach; }
    public void setGioiHanMoiKhach(int gioiHanMoiKhach) { this.gioiHanMoiKhach = gioiHanMoiKhach; }

    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    public String getNguoiTao() { return nguoiTao; }
    public void setNguoiTao(String nguoiTao) { this.nguoiTao = nguoiTao; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    // ────────────────────────────────────────────────
    // Các method hỗ trợ logic
    // ────────────────────────────────────────────────

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return trangThai &&
                now.isAfter(ngayBD.minusDays(1)) &&
                now.isBefore(ngayKT.plusDays(1)) &&
                (soLuongTong - soLuongDaDung > 0);
    }

    public boolean isEligibleForCustomer(String loaiKhachHang) {
        if (apDungChoTatCa) return true;
        if (loaiKhachApDung == null || loaiKhachApDung.trim().isEmpty()) return false;

        String[] allowedTypes = loaiKhachApDung.split(",");
        for (String type : allowedTypes) {
            if (type.trim().equalsIgnoreCase(loaiKhachHang)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "KhuyenMai{" +
                "maKhuyenMai='" + maKhuyenMai + '\'' +
                ", tenKhuyenMai='" + tenKM + '\'' +
                ", loaiKM='" + loaiKM + '\'' +
                ", giaTri=" + giaTri +
                '}';
    }
}