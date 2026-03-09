package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ThanhToanDTO {
    public String tenKH;
    public String sdt;
    public String email;
    public String tuyenBay;
    public String veGhe;
    public String gioDi;
    public String gioDen;
    public BigDecimal giaVeGoc;      
    public BigDecimal tongTienDichVu; 
    public List<String> danhSachDichVu; 
    public List<MucHoaDon> danhSachChiTiet = new ArrayList<>();

    public static class MucHoaDon {
        public String tenMuc;
        public int soLuong;
        public BigDecimal donGia;
        public BigDecimal thanhTien;

        public MucHoaDon(String tenMuc, int soLuong, BigDecimal donGia, BigDecimal thanhTien) {
            this.tenMuc = tenMuc;
            this.soLuong = soLuong;
            this.donGia = donGia;
            this.thanhTien = thanhTien;
        }
    }

    public ThanhToanDTO() {
    }
}