package bus;

import dal.HoaDonDAO;
import model.HoaDon;
import java.math.BigDecimal;
import java.util.List;

public class HoaDonBUS {

    private HoaDonDAO hoaDonDAO;

    public HoaDonBUS() {
        hoaDonDAO = new HoaDonDAO();
    }

    public List<HoaDon> docDanhSachHoaDon() {
        return hoaDonDAO.selectAll();
    }

    public String themHoaDon(HoaDon hd) {
        if (hd.getMaHoaDon() == null || hd.getMaHoaDon().trim().isEmpty()) {
            return "Lỗi: Mã hóa đơn không được để trống!";
        }

        // Kiểm tra tổng tiền
        if (hd.getTongTien() == null || hd.getTongTien().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Tổng tiền hóa đơn không hợp lệ!";
        }

        if (hoaDonDAO.selectById(hd.getMaHoaDon()) != null) {
            return "Lỗi: Mã hóa đơn này đã tồn tại!";
        }

        boolean isSuccess = hoaDonDAO.insert(hd);
        return isSuccess ? "Thành công" : "Lỗi: Lập hóa đơn thất bại!";
    }

    public String capNhatHoaDon(HoaDon hd) {
        // Hóa đơn thường ít khi cho cập nhật tùy tiện, nhưng nếu cần:
        if (hd.getTongTien() == null || hd.getTongTien().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Tổng tiền cập nhật không hợp lệ!";
        }

        boolean isSuccess = hoaDonDAO.update(hd);
        return isSuccess ? "Thành công" : "Lỗi: Cập nhật hóa đơn thất bại!";
    }

    public String xoaHoaDon(String maHoaDon) {
        // Logic nghiệp vụ: Cần check xem hóa đơn này đã có trong CT_Hoa_Don chưa, nếu có thì phải xóa chi tiết trước.
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return "Lỗi: Mã hóa đơn không hợp lệ!";
        }

        boolean isSuccess = hoaDonDAO.delete(maHoaDon);
        return isSuccess ? "Thành công" : "Lỗi: Xóa hóa đơn thất bại!";
    }
}