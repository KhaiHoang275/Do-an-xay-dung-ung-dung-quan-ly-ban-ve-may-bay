package bus;

import dal.CTHoaDonDAO;
import model.CTHoaDon;
import java.math.BigDecimal;
import java.util.List;

public class CTHoaDonBUS {

    private CTHoaDonDAO ctHoaDonDAO;

    public CTHoaDonBUS() {
        ctHoaDonDAO = new CTHoaDonDAO();
    }

    public List<CTHoaDon> docDanhSachCTHoaDon() {
        return ctHoaDonDAO.selectAll();
    }

    public String themCTHoaDon(CTHoaDon ct) {
        // Đã bổ sung kiểm tra khóa chính maCTHD theo model mới
        if (ct.getMaCTHD() == null || ct.getMaCTHD().trim().isEmpty()) {
            return "Lỗi: Mã chi tiết hóa đơn không được để trống!";
        }

        if (ct.getMaHoaDon() == null || ct.getMaHoaDon().trim().isEmpty()) {
            return "Lỗi: Mã hóa đơn không được để trống!";
        }

        if (ct.getMaVe() == null || ct.getMaVe().trim().isEmpty()) {
            return "Lỗi: Mã vé không được để trống!";
        }

        // Đã sửa thành getSoTien()
        if (ct.getSoTien() == null || ct.getSoTien().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Số tiền không hợp lệ!";
        }

        boolean isSuccess = ctHoaDonDAO.insert(ct);
        return isSuccess ? "Thành công" : "Lỗi: Thêm chi tiết hóa đơn thất bại!";
    }

    // Đã sửa hàm xóa truyền duy nhất 1 biến là maCTHD (do model của bạn có khóa chính này)
    public String xoaCTHoaDon(String maCTHD) {
        if (maCTHD == null || maCTHD.trim().isEmpty()) {
            return "Lỗi: Mã chi tiết hóa đơn không hợp lệ!";
        }
        
        boolean isSuccess = ctHoaDonDAO.delete(maCTHD);
        return isSuccess ? "Thành công" : "Lỗi: Xóa chi tiết hóa đơn thất bại!";
    }
}