package bus;

import dal.ChiTietDichVuDAO;
import model.ChiTietDichVu;
import java.math.BigDecimal;
import java.util.List;

public class ChiTietDichVuBUS {

    private ChiTietDichVuDAO chiTietDAO;

    public ChiTietDichVuBUS() {
        chiTietDAO = new ChiTietDichVuDAO();
    }

    public List<ChiTietDichVu> docDanhSachChiTiet() {
        return chiTietDAO.selectAll();
    }

    public String themChiTiet(ChiTietDichVu ct) {
        if (ct.getMaVe() == null || ct.getMaVe().trim().isEmpty()) {
            return "Lỗi: Mã vé không được để trống!";
        }
        
        if (ct.getMaDichVu() == null || ct.getMaDichVu().trim().isEmpty()) {
            return "Lỗi: Mã dịch vụ không được để trống!";
        }

        if (ct.getSoLuong() <= 0) {
            return "Lỗi: Số lượng phải lớn hơn 0!";
        }

        if (ct.getThanhTien() == null || ct.getThanhTien().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Thành tiền không hợp lệ!";
        }

        boolean isSuccess = chiTietDAO.insert(ct);
        return isSuccess ? "Thành công" : "Lỗi: Thêm chi tiết dịch vụ thất bại!";
    }
}