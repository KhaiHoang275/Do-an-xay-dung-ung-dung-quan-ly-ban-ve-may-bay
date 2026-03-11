package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DatVeSession {
    // 1. Thông tin cơ bản
    public String maNguoiDung; // Mã tài khoản đang đăng nhập
    public String maChuyenBay;
    public String loaiVe;      // "Một chiều" hoặc "Khứ hồi"
    public String maHangVe;    // ECO, BUS...

    public String tenSanBayDi;
    public String tenSanBayDen;
    public String thoiGianDi;
    
    // 2. Số lượng khách
    public int soNguoiLon = 0;
    public int soTreEm = 0;
    public int soEmBe = 0;
    
    // 3. Dữ liệu mảng (Được đắp thêm vào qua từng màn hình)
    public List<GheMayBay> danhSachGhe = new ArrayList<>();
    public List<ThongTinHanhKhach> danhSachHanhKhach = new ArrayList<>();
    public List<HanhLy> danhSachHanhLy = new ArrayList<>(); // Tạm dùng class HanhLy
    
    // 4. Tiền nong
    public BigDecimal tongTienVe = BigDecimal.ZERO;
    public BigDecimal tongTienDichVu = BigDecimal.ZERO;
    public KhuyenMai khuyenMaiApDung = null;
    
    public int getTongSoHanhKhach() {
        return soNguoiLon + soTreEm + soEmBe;
    }
}