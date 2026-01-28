package model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class GiaoDichVe {

    private String maGD;
    private String maVe;             // vé gốc
    private String maVeMoi;          // vé sau khi đổi
    private LocalDate ngayYeuCau;
    private LocalDate ngayXuLy;       // null nếu chưa xử lý
    private BigDecimal phi;           // phí đổi vé
    private String lyDo;
    private TrangThaiGiaoDich trangThai;

    public GiaoDichVe() {
        this.maGD = "";
        this.maVe = "";
        this.maVeMoi = "";
        this.ngayYeuCau = null;
        this.ngayXuLy = null;
        this.phi = BigDecimal.ZERO;
        this.lyDo = "";
        this.trangThai = TrangThaiGiaoDich.CHO_XU_LY;
    }

    public GiaoDichVe(String maGD, String maVe, String maVeMoi,
                      LocalDate ngayYeuCau, LocalDate ngayXuLy,
                      BigDecimal phi, String lyDo,
                      TrangThaiGiaoDich trangThai) {
        this.maGD = maGD;
        this.maVe = maVe;
        this.maVeMoi = maVeMoi;
        this.ngayYeuCau = ngayYeuCau;
        this.ngayXuLy = ngayXuLy;
        this.phi = phi;
        this.lyDo = lyDo;
        this.trangThai = trangThai;
    }

    public GiaoDichVe(GiaoDichVe gd) {
        this.maGD = gd.maGD;
        this.maVe = gd.maVe;
        this.maVeMoi = gd.maVeMoi;
        this.ngayYeuCau = gd.ngayYeuCau;
        this.ngayXuLy = gd.ngayXuLy;
        this.phi = gd.phi;
        this.lyDo = gd.lyDo;
        this.trangThai = gd.trangThai;
    }

    public String getMaGD() {
        return maGD;
    }

    public void setMaGD(String maGD) {
        this.maGD = maGD;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public String getMaVeMoi() {
        return maVeMoi;
    }

    public void setMaVeMoi(String maVeMoi) {
        this.maVeMoi = maVeMoi;
    }

    public LocalDate getNgayYeuCau() {
        return ngayYeuCau;
    }

    public void setNgayYeuCau(LocalDate ngayYeuCau) {
        this.ngayYeuCau = ngayYeuCau;
    }

    public LocalDate getNgayXuLy() {
        return ngayXuLy;
    }

    public void setNgayXuLy(LocalDate ngayXuLy) {
        this.ngayXuLy = ngayXuLy;
    }

    public BigDecimal getPhi() {
        return phi;
    }

    public void setPhi(BigDecimal phi) {
        this.phi = phi;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public TrangThaiGiaoDich getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiGiaoDich trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return maGD + " | " + maVe + " -> " + maVeMoi + " | "
                + trangThai;
    }
}
