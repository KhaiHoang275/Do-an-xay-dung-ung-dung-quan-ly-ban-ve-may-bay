package bll;

import dal.CTHoaDonDAO;
import dal.HoaDonDAO;
import model.HoaDon;
import model.ThanhToanDTO;

import java.math.BigDecimal;
import java.util.List;

public class HoaDonBUS {

    private HoaDonDAO hoaDonDAO;
    private CTHoaDonDAO ctHoaDonDAO; // Thêm DAO của chi tiết để check ràng buộc khóa ngoại

    public HoaDonBUS() {
        hoaDonDAO = new HoaDonDAO();
        ctHoaDonDAO = new CTHoaDonDAO();
    }

    // Lấy danh sách hóa đơn
    public List<HoaDon> docDanhSachHoaDon() {
        return hoaDonDAO.selectAll();
    }

    // Thêm hóa đơn
    public String themHoaDon(HoaDon hd) {
        if (hd.getMaHoaDon() == null || hd.getMaHoaDon().trim().isEmpty()) {
            return "Lỗi: Mã hóa đơn không được để trống!";
        }

        // Kiểm tra tổng tiền
        if (hd.getTongTien() == null || hd.getTongTien().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Tổng tiền hóa đơn không hợp lệ!";
        }

        // Kiểm tra tiền thuế (Mới thêm)
        if (hd.getThue() == null || hd.getThue().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Tiền thuế không hợp lệ!";
        }

        // Kiểm tra trạng thái (Mới thêm)
        if (hd.getTrangThai() == null || hd.getTrangThai().trim().isEmpty()) {
            return "Lỗi: Trạng thái hóa đơn không được để trống!";
        }

        if (hoaDonDAO.selectById(hd.getMaHoaDon()) != null) {
            return "Lỗi: Mã hóa đơn này đã tồn tại!";
        }

        boolean isSuccess = hoaDonDAO.insert(hd);
        return isSuccess ? "Thành công" : "Lỗi: Lập hóa đơn thất bại!";
    }

    // Cập nhật hóa đơn
    public String capNhatHoaDon(HoaDon hd) {
        if (hd.getMaHoaDon() == null || hd.getMaHoaDon().trim().isEmpty()) {
            return "Lỗi: Mã hóa đơn không hợp lệ!";
        }

        if (hd.getTongTien() == null || hd.getTongTien().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Tổng tiền cập nhật không hợp lệ!";
        }

        if (hd.getThue() == null || hd.getThue().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Tiền thuế cập nhật không hợp lệ!";
        }
        
        if (hd.getTrangThai() == null || hd.getTrangThai().trim().isEmpty()) {
            return "Lỗi: Trạng thái cập nhật không được để trống!";
        }

        boolean isSuccess = hoaDonDAO.update(hd);
        return isSuccess ? "Thành công" : "Lỗi: Cập nhật hóa đơn thất bại!";
    }

    // Xóa hóa đơn
    public String xoaHoaDon(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return "Lỗi: Mã hóa đơn không hợp lệ!";
        }

        // Logic nghiệp vụ: Kiểm tra xem hóa đơn này có chứa chi tiết hóa đơn (vé) nào không
        // Nếu có thì báo lỗi bắt người dùng xóa chi tiết trước để tránh vi phạm khóa ngoại (Foreign Key constraint)
        if (!ctHoaDonDAO.selectByMaHoaDon(maHoaDon).isEmpty()) {
            return "Lỗi: Không thể xóa! Hóa đơn này đang chứa chi tiết (vé). Vui lòng xóa các chi tiết thuộc hóa đơn này trước.";
        }

        boolean isSuccess = hoaDonDAO.delete(maHoaDon);
        return isSuccess ? "Thành công" : "Lỗi: Xóa hóa đơn thất bại!";
    }

    // Lấy chi tiết thanh toán theo mã phiếu đặt vé
    public ThanhToanDTO layChiTietThanhToan(String maPhieu) {
        if (maPhieu == null || maPhieu.trim().isEmpty()) {
            return null;
        }
        return hoaDonDAO.getChiTietThanhToan(maPhieu);
    }
  /**
     * TÁC DỤNG: Tự động quét tất cả vé trong hóa đơn để tính lại Tổng tiền và Thuế.
     * Logic: Tổng = (Tổng các Chi tiết vé) - (Tiền khuyến mãi thực tế).
     * Đảm bảo: Không bị âm tiền và luôn đồng bộ với Database.
     */
     public void capNhatTongTienVaThueTuDong(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) return;

        // 1. Lấy tổng thành tiền từ các vé (đã tính ở Bước 2)
        List<model.CTHoaDon> danhSachChiTiet = ctHoaDonDAO.selectByMaHoaDon(maHoaDon);
        BigDecimal tongThanhTienCacVe = BigDecimal.ZERO;
        BigDecimal tongThue = BigDecimal.ZERO;

        for (model.CTHoaDon ct : danhSachChiTiet) {
            tongThanhTienCacVe = tongThanhTienCacVe.add(ct.getThanhTien());
            tongThue = tongThue.add(ct.getThueVAT());
        }

        HoaDon hd = hoaDonDAO.selectById(maHoaDon);
        if (hd != null) {
            // 2. Lấy tiền giảm giá
            BigDecimal tienGiam = hoaDonDAO.layTienGiamGiaTuPhieuDat(hd.getMaPhieuDatVe());
            
            // 3. Lấy tổng tiền ghế của các vé trong hóa đơn
            BigDecimal tongTienGhe = hoaDonDAO.layTongTienGheCuaHoaDon(maHoaDon);

            // 4. CÔNG THỨC CHỐT: Tổng = Tổng thành tiền - Tiền giảm + Tổng tiền ghế
            BigDecimal tongCuoiCung = tongThanhTienCacVe
                                        .subtract(tienGiam)
                                        .add(tongTienGhe);

            // Chặn lỗi tiền âm
            if (tongCuoiCung.compareTo(BigDecimal.ZERO) < 0) tongCuoiCung = BigDecimal.ZERO;

            hd.setTongTien(tongCuoiCung);
            hd.setThue(tongThue);
            hoaDonDAO.update(hd);
        }
    }
}