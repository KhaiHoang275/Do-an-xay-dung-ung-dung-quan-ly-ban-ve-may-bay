package model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class GiaoDichVe {

    private String maGD;
    private String maVeCu;             // vé gốc

    private String maChuyenBayMoi;
    private String maHangVeMoi;
    private String maGheMoi;

    private LocalDate ngayYeuCau;
    private LocalDate ngayXuLi;       // null nếu chưa xử lý
    private BigDecimal phi;           // phí đổi vé
    private BigDecimal phiChenhLech;
    private String lyDoDoi;
    private TrangThaiGiaoDich trangThai;

    public GiaoDichVe() {
        this.maGD = "";
        this.maVeCu = "";
        this.maChuyenBayMoi = "";
        this.maHangVeMoi = "";
        this.maGheMoi = "";
        this.ngayYeuCau = null;
        this.ngayXuLi = null;
        this.phi = BigDecimal.ZERO;
        this.phiChenhLech = BigDecimal.ZERO;
        this.lyDoDoi = "";
        this.trangThai = TrangThaiGiaoDich.CHO_XU_LY;
    }

    public GiaoDichVe(String maGD, String maVeCu,String maChuyenBayMoi, String maHangVeMoi, String maGheMoi,
                      LocalDate ngayYeuCau, LocalDate ngayXuLy,
                      BigDecimal phi, BigDecimal phiChenhLech, String lyDo,
                      TrangThaiGiaoDich trangThai) {
        this.maGD = maGD;
        this.maVeCu = maVeCu;
        this.maChuyenBayMoi = maChuyenBayMoi;
        this.maHangVeMoi = maHangVeMoi;
        this.maGheMoi = maGheMoi;
        this.ngayYeuCau = ngayYeuCau;
        this.ngayXuLi = ngayXuLy;
        this.phi = phi;
        this.phiChenhLech = phiChenhLech;
        this.lyDoDoi = lyDo;
        this.trangThai = trangThai;
    }

    public GiaoDichVe(GiaoDichVe gd) {
        this.maGD = gd.maGD;
        this.maVeCu = gd.maVeCu;
        this.maChuyenBayMoi = gd.maChuyenBayMoi;
        this.maHangVeMoi = gd.maHangVeMoi;
        this.maGheMoi = gd.maGheMoi;
        this.ngayYeuCau = gd.ngayYeuCau;
        this.ngayXuLi = gd.ngayXuLi;
        this.phi = gd.phi;
        this.phiChenhLech = gd.phiChenhLech;
        this.lyDoDoi = gd.lyDoDoi;
        this.trangThai = gd.trangThai;
    }

    public String getMaGD() {
        return maGD;
    }

    public void setMaGD(String maGD) {
        this.maGD = maGD;
    }

    public String getMaVeCu() {
        return maVeCu;
    }

    public void setMaVeCu(String maVeCu) {
        this.maVeCu = maVeCu;
    }

    public String getMaChuyenBayMoi() {
        return maChuyenBayMoi;
    }

    public void setMaChuyenBayMoi(String maChuyenBayMoi) {
        this.maChuyenBayMoi = maChuyenBayMoi;
    }

    public String getMaHangVeMoi() {
        return maHangVeMoi;
    }

    public void setMaHangVeMoi(String maHangVeMoi) {
        this.maHangVeMoi = maHangVeMoi;
    }

    public String getMaGheMoi() {
        return maGheMoi;
    }

    public void setMaGheMoi(String maGheMoi) {
        this.maGheMoi = maGheMoi;
    }

    public String getLyDoDoi() {
        return lyDoDoi;
    }

    public void setLyDoDoi(String lyDoDoi) {
        this.lyDoDoi = lyDoDoi;
    }

    public LocalDate getNgayYeuCau() {
        return ngayYeuCau;
    }

    public void setNgayYeuCau(LocalDate ngayYeuCau) {
        this.ngayYeuCau = ngayYeuCau;
    }

    public LocalDate getNgayXuLi() {
        return ngayXuLi;
    }

    public void setNgayXuLi(LocalDate ngayXuLi) {
        this.ngayXuLi = ngayXuLi;
    }

    public BigDecimal getPhi() {
        return phi;
    }

    public void setPhi(BigDecimal phi) {
        this.phi = phi;
    }

    public BigDecimal getPhiChenhLech(){
        return phiChenhLech;
    }

    public void setPhiChenhLech(BigDecimal phiChenhLech){
        this.phiChenhLech = phiChenhLech;
    }

    public String getLyDo() {
        return lyDoDoi;
    }

    public void setLyDo(String lyDo) {
        this.lyDoDoi = lyDo;
    }

    public TrangThaiGiaoDich getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiGiaoDich trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return maGD + " | " + maVeCu + " | " + maChuyenBayMoi + " | " + maHangVeMoi + " | " + maGheMoi + " | "
                + trangThai;
    }
}
