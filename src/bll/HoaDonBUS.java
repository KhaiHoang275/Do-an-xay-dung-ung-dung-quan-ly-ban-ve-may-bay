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
    // =========================================================================
    // HÀM TỰ ĐỘNG CỘNG DỒN TỔNG TIỀN VÀ THUẾ TỪ CHI TIẾT LÊN HÓA ĐƠN
    // =========================================================================
    public void capNhatTongTienVaThueTuDong(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) return;

        // 1. Lấy tất cả chi tiết vé thuộc hóa đơn này
        List<model.CTHoaDon> danhSachChiTiet = ctHoaDonDAO.selectByMaHoaDon(maHoaDon);
        
        BigDecimal tongTienMoi = BigDecimal.ZERO;
        BigDecimal tongThueMoi = BigDecimal.ZERO;

        // 2. Chạy vòng lặp cộng dồn tất cả lại
        for (model.CTHoaDon ct : danhSachChiTiet) {
            if (ct.getThanhTien() != null) {
                tongTienMoi = tongTienMoi.add(ct.getThanhTien());
            }
            if (ct.getThueVAT() != null) {
                tongThueMoi = tongThueMoi.add(ct.getThueVAT());
            }
        }

        // 3. Lấy hóa đơn gốc ra, set con số mới vào và lưu xuống Database
        HoaDon hd = hoaDonDAO.selectById(maHoaDon);
        if (hd != null) {
            hd.setTongTien(tongTienMoi);
            hd.setThue(tongThueMoi);
            hoaDonDAO.update(hd); // Lưu thay đổi
        }
    }
}